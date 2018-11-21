package lt;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import net.sf.json.JSONObject;
import org.json.JSONArray;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.FilePath;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class LTBuildWrapper extends BuildWrapper implements Serializable {
    private final static Logger log = Logger.getLogger(LTBuildWrapper.class.getName());
    private String authkey,username,credentialsId;
    @DataBoundConstructor
    public LTBuildWrapper(List<JSONObject> seleniumTests,String credentialsId) {
        /*
         * Instantiated when the configuration is saved
         * Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
         */
        log.entering(this.getClass().getName(), "constructor");
        setSeleniumTests(seleniumTests);
        System.out.print("*****credentials are***"+credentialsId);
        setCredentials(credentialsId);
        log.exiting(this.getClass().getName(), "constructor");
    }


    private List<JSONObject> seleniumTests;


    private void setSeleniumTests(List<JSONObject> seleniumTests) {
        if (seleniumTests == null) { // prevent null pointer
            log.finest("seleniumTests is null");
            this.seleniumTests = new LinkedList<JSONObject>();
        } else {
            log.finest("seleniumTests: " + seleniumTests.toString());
            this.seleniumTests = seleniumTests;
        }
    }

    private void setCredentials(String credentialsId) {
        this.credentialsId = credentialsId;
        final LTCredentials credentials = LTCredentials.getCredentials(null, credentialsId);
        log.fine("got credentials");
        if (credentials != null) {
            this.username = credentials.getUsername();
            this.authkey = credentials.getAuthkey();
        } else {
            log.fine("got null pointer from username or authkey. going to set them both to empty");
            this.username = this.authkey = "";
        }
        log.fine("setting credentials");
        getDescriptor().setBuildCredentials(username,authkey);
    }

    @Override
    public Environment setUp(final AbstractBuild build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        /*
         * called when you the build runs
         */
        log.entering(this.getClass().getName(), "setup");
        return new LTEnvironment(build);

    }

    @Override
    public LTDescriptor getDescriptor() {
        return (LTDescriptor) super.getDescriptor();
    }

    private class LTEnvironment extends BuildWrapper.Environment {
        private AbstractBuild build;

        private LTEnvironment(final AbstractBuild build) {
            this.build = build;
        }

        private JSONObject addBrowserNameToJSONObject(JSONObject config) {
            String osName = config.getString("operating_system");
            String browserName = config.getString("browser");
            config.put("browserName", browserName);
            return config;
        }


        @Override
        public void buildEnvVars(Map<String, String> env) {
            log.entering(this.getClass().getName(), "buildEnvVars");
            String buildname = build.getFullDisplayName().substring(0, build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
            String buildnumber = String.valueOf(build.getNumber());
            JSONArray browsers = new JSONArray();
            log.finest("seleniumTests.size(): " + seleniumTests.size());
            for (JSONObject config : seleniumTests) {
                config = addBrowserNameToJSONObject(config);
                browsers.put(config);
                Map<String, String> params = new HashMap<>();
                Selenium selenium = new Selenium();
                params = selenium.StoreOSMapping();
                if (seleniumTests.size() == 1) {
                    JSONObject seTest = seleniumTests.get(0);
                    String operatingSystemApiName = params.get(seTest.getString("operating_system"));
                    String browserApiName = seTest.getString("browser");
                    String resolution = seTest.getString("resolution");
                    log.info("Going to use old selenium capabilites");
                    String browserName = seTest.getString("browser");
                    String[] arrOfBr = browserName.split("-", 2);
                    browserApiName = arrOfBr[0];
                    System.out.print(browserApiName);
                    Constants.browserVersion = arrOfBr[1] + ".0";
                    env.put(Constants.OPERATINGSYSTEM, operatingSystemApiName);
                    Constants.output.put(Constants.OPERATINGSYSTEM,operatingSystemApiName);
                    Constants.output.put(Constants.BROWSER,browserApiName);
                    Constants.output.put(Constants.RESOLUTION,resolution);
                    env.put(Constants.BROWSER, browserApiName);
                    env.put(Constants.RESOLUTION, resolution);
                    env.put(Constants.BROWSERNAME, browserName);
                    env.put(Constants.BROWSER_VERSION, Constants.browserVersion);

                }
                env.put(Constants.BROWSERS,browsers.toString());
                Constants.output.put(Constants.BROWSERS,browsers.toString());
                env.put(Constants.USERNAME, username);
                env.put(Constants.APIKEY, authkey);
                env.put(Constants.BUILDNAME, buildname);
                env.put(Constants.BUILDNUMBER, buildnumber);
                env.put(Constants.pluginUrl, "jenkins");
                Constants.output.put(Constants.videoUrl,"https://www.lambdatest.com");
                Constants.output.put(   Constants.logUrl,"https://www.360logica.com");
                //define grid url here
                String gridurl= String.format("https://%s:%s@dev-ml.lambdatest.com/wd/hub",username,authkey );
                env.put(Constants.GRID_URL,gridurl);
                Constants.output.put(Constants.GRID_URL,gridurl);
                super.buildEnvVars(env);
                System.out.println(Constants.output);
                log.exiting(this.getClass().getName(), "buildEnvVars");
            }
        }
        private String generateReport(LTBuildResults stats) throws IOException {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            try {
                InputStream in = LTBuildResults.class.getResourceAsStream(Constants.REPORT_TEMPLATE_PATH);
                if(in==null){
                    System.out.println("file is null");
                }
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) >= 0) {
                    bOut.write(buffer, 0, read);
                }
            }catch (Exception c){

            }
            String content = new String(bOut.toByteArray(), StandardCharsets.UTF_8);
            content = content.replace(Constants.STATUS, String.valueOf(stats.getStatus()));
            content = content.replace(Constants.CONFIG_INFO, String.valueOf(stats.getConfigInfo()));
            content = content.replace(Constants.VIDEO,Constants.output.get(Constants.videoUrl));
            content = content.replace(Constants.LOGS,Constants.output.get(Constants.logUrl));
            return content;
        }

        @Override
        public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
            log.entering(this.getClass().getName(), "teardown");
            listener.getLogger().println("-------Here are your environment variables-------------------");
            listener.getLogger().println("output of env" + Constants.output);
            boolean status = true;
            LTBuildResults result = new LTBuildResults(Constants.output.get(Constants.OPERATINGSYSTEM),Constants.output.get(Constants.BROWSER),status);
            String report = generateReport(result);
            File artifactsDir = build.getArtifactsDir();
            if (!artifactsDir.isDirectory()) {
                boolean success = artifactsDir.mkdirs();
                if (!success) {
                    listener.getLogger().println("Can't create artifacts directory at "
                        + artifactsDir.getAbsolutePath());
                }
            }
            String path = artifactsDir.getCanonicalPath() + Constants.REPORT_TEMPLATE_PATH;
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),
                StandardCharsets.UTF_8))) {
                writer.write(report);
            }
            return true;
        }


    }
}