package org.jenkinsci.plugins.lt_jenkins.pipeline;

import com.lambdatest.api.LocalTunnel;
import com.lambdatest.plugin.Constants;
import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.model.*;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class LTStep extends AbstractLTStep {
    private transient final static Logger log = Logger.getLogger(LTStep.class.getName());

    public boolean useLocalTunnel,
            useTestResults= false;
    public String localTunnelPath,
            tunnelName = "";

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new LTStepExecution(this, context);
    }

    @DataBoundConstructor
    public LTStep(boolean useLocalTunnel, boolean useTestResults, String credentialsId, String tunnelName, String localTunnelPath) {
        this.credentialsId = credentialsId;
        setLocalTunnelPath(localTunnelPath);
        setTunnelName(tunnelName);
        setUseLocalTunnel(useLocalTunnel);
        setUseTestResults(useTestResults);
    }
    private void setLocalTunnelPath(String localTunnelPath) {
        if (localTunnelPath == null) {
            this.localTunnelPath = "";
        } else {
            this.localTunnelPath = localTunnelPath;
        }
    }
    private void setTunnelName(String tunnelName) {
        if (tunnelName == null) {
            this.tunnelName = "";
        } else {
            this.tunnelName = tunnelName;
        }
    }
    private void setUseLocalTunnel(boolean useLocalTunnel) {
        try { // prevent null pointer
            if (useLocalTunnel != true) {
                this.useLocalTunnel = false;
            } else {
                this.useLocalTunnel = useLocalTunnel;
            }
        } catch (NullPointerException npe) {
            this.useLocalTunnel = false;
        }
    }
    private void setUseTestResults(boolean useTestResults) {
        try { // prevent null pointer
            if (useTestResults != true) {
                this.useTestResults = false;
            } else {
                this.useTestResults = useTestResults;
            }
        } catch (NullPointerException npe) {
            this.useTestResults = false;
        }
    }

    public static class LTStepExecution extends AbstractLTStepExecution {
        private transient final static Logger log = Logger.getLogger(LTStepExecution.class.getName());
        private transient LTStep LTStep;
        private transient LocalTunnel tunnel = null;

        public LTStepExecution(LTStep step, StepContext context) throws Exception {
            super(step, context);
            LTStep = step;
        }

        @Override
        public boolean start() throws Exception {
            if (!(job instanceof TopLevelItem)) {
                throw new Exception(job + " must be a top-level job");
            }
            if (LTStep.getCredentials() == null) {
                throw new Exception("no credentials provided");
            }else {
                username = LTStep.getCredentials().getUsername();
                log.finest("username = "+username);
                authkey = LTStep.getCredentials().getAuthkey();
                log.finest("authkey = "+authkey);
                if (LTStep.useLocalTunnel) {
                    if (!LTStep.tunnelName.isEmpty()) {
                        listener.getLogger().println(Constants.TUNNEL_USING_TUNNELNAME(LTStep.tunnelName));
                        tunnel = new LocalTunnel(username, authkey, LTStep.tunnelName);
                    }else if(LTStep.tunnelName.isEmpty()){
                        listener.getLogger().println(Constants.TUNNEL_USING_DEFAULT);
                        tunnel = new LocalTunnel(username, authkey);
                    }
                    LTStepDescriptor.checkProxySettingsAndReloadRequest(tunnel);
                    tunnel.queryTunnel();
                    if (!tunnel.isTunnelRunning) {
                        listener.getLogger().println(Constants.TUNNEL_NEED_TO_START);
                        try {
                            if (LTStep.localTunnelPath != null && LTStep.localTunnelPath.equals("")) {
                                log.fine("using embedded local tunnel");
                                tunnel.start(true);
                            } else {
                                log.fine("using specified local tunnel");
                                tunnel.start(LTStep.localTunnelPath);
                            }
                            listener.getLogger().println(Constants.TUNNEL_WAITING);
                            for (int i=1 ; i<15 && !tunnel.isTunnelRunning ; i++) {
                                //will check every 2 seconds for upto 30 to see if the tunnel connected
                                TimeUnit.SECONDS.sleep(4);
                                tunnel.queryTunnel();
                            }
                            if (tunnel.isTunnelRunning) {
                                listener.getLogger().println(Constants.TUNNEL_CONNECTED);
                            }else {
                                throw new Error(Constants.TUNNEL_START_FAIL);
                            }
                        }catch (URISyntaxException | IOException e) {
                            log.finer("err: "+e);
                            throw new Error(Constants.TUNNEL_START_FAIL);
                        }
                    }else {
                        listener.getLogger().println(Constants.TUNNEL_NO_NEED_TO_START);
                    }
                }
            }
            body = getContext().newBodyInvoker()
                    .withContexts(LTStep.getCredentials(), LTStep.useTestResults)
                    .withCallback(new LTStepTailCall())
                    .start();
            return false;
        }
        public class LTStepTailCall extends BodyExecutionCallback.TailCall {
            private transient final Logger log = Logger.getLogger(LTStepTailCall.class.getName());


            @Override
            protected void finished(StepContext context) throws Exception {
                if (tunnel != null && tunnel.pluginStartedTheTunnel) {
                    for (int i=1 ; i<20 && tunnel.isTunnelRunning; i++) {
                        try {
                            log.info("about to kill the tunnel using the api");
                            tunnel.stop();
                            log.info("done killing the tunnel using the api");
                        }catch(IOException ioe) {
                            // most likely got a bad gateway but we're going to delete the cbt_tunnel binary on exit anyway so the tunnel will still be killed
                            log.warning("got IOException while killing the tunnel");
                        }
                        //will check every 15 seconds for up to 5 minutes to see if the tunnel disconnected
                        log.fine("waiting for the tunnel to die");
                        Thread.sleep(15000);
                        boolean tunnelIsRunning = tunnel.queryTunnel();
                        log.fine("tunnelIsRunning: "+tunnelIsRunning);
                        log.fine("tunnel.isTunnelRunning: "+tunnel.isTunnelRunning);
                    }
                    if (!tunnel.isTunnelRunning) {
                        listener.getLogger().println(Constants.TUNNEL_STOP);
                    } else {
                        listener.getLogger().println(Constants.TUNNEL_STOP_FAIL);
                    }
                }
            }
        }
    }
    @Extension
    public static final class LTStepDescriptor extends AbstractLTStepDescriptor {
        private transient final static Logger log = Logger.getLogger(LTStepDescriptor.class.getName());

        //@Override
        public String getFunctionName() {
            return functionNamePrefix();
        }
        @Override
        public String getDisplayName() {
            return Constants.DISPLAYNAME;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }
    }
}
