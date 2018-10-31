package org.jenkinsci.plugins.lt_jenkins.pipeline;

import com.lambdatest.api.Screenshots;
import hudson.Extension;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.lt_jenkins.LTCredentials;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.logging.Logger;

public class LTScreenshotsStep extends AbstractLTStep {
    private transient final static Logger log = Logger.getLogger(LTScreenshotsStep.class.getName());
    public String browserList, loginProfile,  url = "";

    @DataBoundConstructor
    public LTScreenshotsStep(String browserList, String loginProfile, String url) {
        this.browserList = browserList;
        this.loginProfile = loginProfile;
        this.url = url;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new LTScreenshotsStepExecution(this, context);
    }

    public static class LTScreenshotsStepExecution extends AbstractLTStepExecution {
        private transient final static Logger log = Logger.getLogger(LTScreenshotsStepExecution.class.getName());
        private transient LTScreenshotsStep screenshotsStep;

        public LTScreenshotsStepExecution(LTScreenshotsStep step, StepContext context) throws Exception {
            super(step, context);
            screenshotsStep = step;
            if (screenshotsStep.getDescriptor().screenshotApi == null) {
                screenshotsStep.getDescriptor().screenshotApi = new Screenshots(getUsername(), getAuthkey());
            }
        }

        @Override
        public boolean start() throws Exception {
            if (screenshotsStep.getDescriptor().screenshotApi == null) {
                screenshotsStep.getDescriptor().screenshotApi = new Screenshots(getUsername(), getAuthkey());
            }
            log.finest("username="+username);
            log.finest("authkey="+authkey);
            log.finest("browserList="+screenshotsStep.browserList);
            log.finest("loginProfile"+screenshotsStep.loginProfile);
            log.finest("url="+screenshotsStep.url);

            boolean useTestResult = getContext().get(Boolean.class);
            LTScreenshotsStepExecutionThread screenshotTest = new LTScreenshotsStepExecutionThread(useTestResult, screenshotsStep.getDescriptor().screenshotApi, screenshotsStep, this);
            screenshotTest.start();
            return false;
        }
    }
    @Override
    public LTScreenshotsStepDescriptor getDescriptor() {
        return (LTScreenshotsStepDescriptor) super.getDescriptor();
    }
    @Extension
    public static final class LTScreenshotsStepDescriptor extends AbstractLTStepDescriptor {
        private transient final static Logger log = Logger.getLogger(LTScreenshotsStepDescriptor.class.getName());
        Screenshots screenshotApi = null;
        private static String username, authkey = "";
        LTCredentials credentials;

        @Override
        public String getFunctionName() {
            return functionNamePrefix()+"ScreenshotsTest";
        }
        @Override
        public String getDisplayName() {
            return "Run a lambdatest.com Screenshots Test";
        }

        private void checkCredentials(String credentialsId) {
            LTCredentials local_credentials = LTCredentials.getCredentialsById(null, credentialsId);
            if (local_credentials != null) {
                credentials = local_credentials;
                username = local_credentials.getUsername();
                authkey = local_credentials.getAuthkey();
            }
            if (screenshotApi == null) {
                screenshotApi = new Screenshots(username, authkey);
                checkProxySettingsAndReloadRequest(screenshotApi);
            }
        }
        public ListBoxModel doFillBrowserListItems(@QueryParameter("credentialsId") final String credentialsId) {
            checkCredentials(credentialsId);
            ListBoxModel items = new ListBoxModel();
            try {
                if (screenshotApi == null) {
                    screenshotApi = new Screenshots(username, authkey);
                    checkProxySettingsAndReloadRequest(screenshotApi);
                }
                items.add("**SELECT A BROWSERLIST**", "");

                for (int i=0 ; i<screenshotApi.browserLists.size() ; i++) {
                    String browserList = screenshotApi.browserLists.get(i);
                    items.add(browserList);
                }
            } catch(NullPointerException npe) {}
            return items;
        }
        public ListBoxModel doFillLoginProfileItems(@QueryParameter("credentialsId") final String credentialsId) {
            checkCredentials(credentialsId);
            LTCredentials local_credentials = LTCredentials.getCredentialsById(null, credentialsId);
            if (local_credentials != null) {
                credentials = local_credentials;
                username = local_credentials.getUsername();
                authkey = local_credentials.getAuthkey();
            }
            ListBoxModel items = new ListBoxModel();
            try {
                if (screenshotApi == null) {
                    screenshotApi = new Screenshots(username, authkey);
                    checkProxySettingsAndReloadRequest(screenshotApi);
                }

                items.add("**SELECT A LOGIN PROFILE / SELENIUM SCRIPT**", "");

                for (int i=0 ; i<screenshotApi.loginProfiles.size() ; i++) {
                    String loginProfile = screenshotApi.loginProfiles.get(i);
                    items.add(loginProfile);
                }
            } catch(NullPointerException npe) {}
            return items;
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return false;
        }
    }
}
