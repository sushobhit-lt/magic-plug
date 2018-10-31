package org.jenkinsci.plugins.lt_jenkins.pipeline;

import org.jenkinsci.plugins.lt_jenkins.LTCredentials;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import java.util.logging.Logger;

public abstract class AbstractLTStep extends Step {
    private transient final static Logger log = Logger.getLogger(AbstractLTStep.class.getName());
    public static String credentialsId = "";
    public static LTCredentials credentials;
    public static String username, authkey = "";

    public AbstractLTStep(){}
    public AbstractLTStep(String credentialsId) {
        this.credentialsId = credentialsId;
        credentials = LTCredentials.getCredentialsById(null, credentialsId);
    }
    public String getCredentialsId() {
        if (credentialsId == null || credentialsId.isEmpty()) {
            return credentials.getId();
        } else {
            return this.credentialsId;
        }
    }
    public static LTCredentials getCredentials() {
        if (credentials == null) {
            return LTCredentials.getCredentialsById(null, credentialsId);
        }else {
            return credentials;
        }
    }
    public static String getUsername() {
        if (username == null || username.isEmpty()) {
            return getCredentials().getUsername();
        }else {
            return username;
        }
    }
    public static String getAuthkey() {
        if (authkey == null || authkey.isEmpty()) {
            return getCredentials().getAuthkey();
        } else {
            return authkey;
        }
    }
    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }
    @Override public AbstractLTStepDescriptor getDescriptor() {
        return (AbstractLTStepDescriptor) super.getDescriptor();
    }
}
