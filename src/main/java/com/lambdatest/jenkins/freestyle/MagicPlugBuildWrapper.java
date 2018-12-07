package com.lambdatest.jenkins.freestyle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdatest.jenkins.credential.MagicPlugCredentials;
import com.lambdatest.jenkins.credential.MagicPlugCredentialsImpl;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.osystem.Version;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.ItemGroup;
import hudson.tasks.BuildWrapper;
import net.sf.json.JSONObject;

@SuppressWarnings("serial")
public class MagicPlugBuildWrapper extends BuildWrapper implements Serializable {

	private List<JSONObject> seleniumCapabilityRequests = null;
	private static String username;
	private String accessToken;
	private String gridURL;

	@DataBoundConstructor
	public MagicPlugBuildWrapper(StaplerRequest req, @CheckForNull List<JSONObject> seleniumCapabilityRequest,
			@CheckForNull String credentialsId, ItemGroup context) throws Exception {
		try {
			System.out.println(credentialsId);
			if (seleniumCapabilityRequest == null) {
				// prevent null pointer
				this.seleniumCapabilityRequests = new ArrayList<JSONObject>();
			} else {
				System.out.println(seleniumCapabilityRequest);
				validateTestInput(seleniumCapabilityRequest);
				this.seleniumCapabilityRequests = seleniumCapabilityRequest;
				setCredentials(credentialsId, context);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void validateTestInput(List<JSONObject> seleniumCapabilityRequest) {
		// TODO : Validation For Input Data
	}

	private void setCredentials(String credentialsId, ItemGroup context) throws Exception {
		final MagicPlugCredentials magicPlugCredential = MagicPlugCredentialsImpl.getCredentials(credentialsId,
				context);
		if (magicPlugCredential != null) {
			this.username = magicPlugCredential.getUserName();
			this.accessToken = magicPlugCredential.getAccessToken();
			System.out.println(username + ":" + accessToken);
			// Verify Auth Token Once again
			if (CapabilityService.isValidUser(this.username, this.accessToken)) {
				System.out.println("Valid User");
			} else {
				throw new Exception("Invalid Credentials ...");
			}
		} else {
			System.out.println("");
			throw new Exception("Credentials not found");
		}
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws IOException, InterruptedException {
		System.out.println("Environment setUp() -start");
		String buildname = build.getFullDisplayName().substring(0,
				build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
		String buildnumber = String.valueOf(build.getNumber());
		System.out.println("buildname :" + buildname);
		System.out.println("buildnumber :" + buildnumber);
		for (JSONObject seleniumCapabilityRequest : seleniumCapabilityRequests) {
			makeFreeStyleBuildActions(build, buildname, buildnumber, seleniumCapabilityRequest, this.username,
					this.accessToken);
		}
		System.out.println("Adding LT actions done");

		// Create Grid URL
		this.gridURL = createGridURL(this.username, this.accessToken);
		System.out.println(this.gridURL);
		System.out.println("Environment setUp() -end");
		return new MagicPlugEnvironment(build);
	}

	private String createGridURL(String username, String authKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://").append(username).append(":").append(authKey).append("@dev-ml.lambdatest.com/wd/hub");
		return sb.toString();
	}

	public static void makeFreeStyleBuildActions(AbstractBuild build, String buildname, String buildnumber,
			JSONObject seleniumCapabilityRequest, String username, String accessToken) {
		String operatingSystem = seleniumCapabilityRequest.getString("operatingSystem");
		String browser = seleniumCapabilityRequest.getString("browser");
		String resolution = seleniumCapabilityRequest.getString("resolution");

		LambdaFreeStyleBuildAction lfsBuildAction = new LambdaFreeStyleBuildAction("SeleniumTest", operatingSystem,
				browser, resolution);
		lfsBuildAction.setBuild(build);
		lfsBuildAction.setBuildName(buildname);
		lfsBuildAction.setBuildNumber(buildnumber);
		lfsBuildAction.setIframeLink(buildnumber, username, accessToken);
		build.addAction(lfsBuildAction);
	}

	@Override
	public MagicPlugDescriptor getDescriptor() {
		return (MagicPlugDescriptor) super.getDescriptor();
	}

	@SuppressWarnings("rawtypes")
	public class MagicPlugEnvironment extends BuildWrapper.Environment {
		private AbstractBuild build;

		private MagicPlugEnvironment(final AbstractBuild build) {
			this.build = build;
		}

		@Override
		public void buildEnvVars(Map<String, String> env) {
			String buildname = build.getFullDisplayName().substring(0,
					build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
			String buildnumber = String.valueOf(build.getNumber());
			Map<String, Version> allBrowsersData = CapabilityService.allBrowserData;
			if (!CollectionUtils.isEmpty(seleniumCapabilityRequests) && seleniumCapabilityRequests.size() == 1) {
				JSONObject seleniumCapability = seleniumCapabilityRequests.get(0);
				env.put(Constant.LT_OPERATING_SYSTEM, seleniumCapability.getString(Constant.OPERATING_SYSTEM));
				String selectedBrowser = seleniumCapability.getString(Constant.BROWSER);
				if (allBrowsersData.containsKey(selectedBrowser)) {
					Version brVersion = allBrowsersData.get(selectedBrowser);
					env.put(Constant.LT_BROWSER_NAME, StringUtils.substringBefore(selectedBrowser, "-"));
					env.put(Constant.LT_BROWSER_VERSION, brVersion.getVersion());
				}
				env.put(Constant.LT_RESOLUTION, seleniumCapability.getString(Constant.RESOLUTION));
			}

			env.put(Constant.LT_BROWSERS, createLTBrowsers(seleniumCapabilityRequests));
			env.put(Constant.LT_GRID_URL, gridURL);
			env.put(Constant.LT_BUILD_NAME, buildname);
			env.put(Constant.LT_BUILD_NUMBER, buildnumber);
			env.put(Constant.USERNAME, username);
			env.put(Constant.ACCESS_TOKEN, accessToken);

			System.out.println(env);
			super.buildEnvVars(env);
		}

		private String createLTBrowsers(List<JSONObject> seleniumCapabilityRequests) {
			String config = Constant.NOT_AVAILABLE;
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				config = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(seleniumCapabilityRequests);

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return config;
		}

		@Override
		public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
			/*
			 * Runs after the build
			 */
			System.out.println("tearDown");
			return super.tearDown(build, listener);
		}

	}

}
