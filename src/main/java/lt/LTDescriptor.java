package lt;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.ItemGroup;
import hudson.security.ACL;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.System.out;

@Extension
public final class LTDescriptor extends BuildWrapperDescriptor {
    private String 	globalUsername, globalAuthkey, buildUsername, buildAuthkey = "";
    private final static Logger log = Logger.getLogger(LTDescriptor.class.getName());
    Selenium seleniumApi = new Selenium();

    public LTDescriptor() throws IOException {
        super(LTBuildWrapper.class);
        try {
            load();
        } catch (Exception ex) {
            log.finest("Unable to load XML file");
        }
    }

    public String getUsername() {
        if (buildUsername != null && buildAuthkey != null && !buildUsername.isEmpty() && !buildAuthkey.isEmpty()) {
            log.fine("using build username = "+buildUsername);
            return buildUsername;
        } else {
            log.fine("using global username");
            return globalUsername;
        }
    }
    public String getAuthkey() {
        if (buildUsername != null && buildAuthkey != null && !buildUsername.isEmpty() && !buildAuthkey.isEmpty()) {
            return buildAuthkey;
        } else {
            return globalAuthkey;
        }
    }
    public void setBuildCredentials(String username, String authkey) {
        buildUsername = username;
        buildAuthkey = authkey;
    }

    public String getDisplayName() {
        return Constants.DISPLAYNAME;
    }

    public ListBoxModel doFillOperating_systemItems() {
        ListBoxModel items = new ListBoxModel();
        items.add("CHOOSE A OPERATING SYSTEM", "");
        try {
            for (int i = 0; i < seleniumApi.operatingSystems.size(); i++) {
                OperatingSystem config = seleniumApi.operatingSystems.get(i);
                System.out.print("os data is here" + config);
                items.add(config.getOsName());
            }
        } catch (NullPointerException npe) {
        }
        return items;
    }


    public ListBoxModel doFillBrowserItems(@QueryParameter String operating_system) {
        ListBoxModel items = new ListBoxModel();
        if (operating_system.isEmpty()) {
            items.add("CHOOSE A BROWSER", "");
        }
        out.println("os is" + operating_system);
        try {
            OperatingSystem config = seleniumApi.operatingSystems2.get(operating_system);
            //Browser browserConfig = seleniumApi.br
            out.println("config is " + config.toString());
            for (int i = 0; i < config.browsers.size(); i++) {
                Browser configBrowser = config.browsers.get(i);
                //out.println("data is here "+config.browsers.toString());
                items.add(configBrowser.getBrowserName());
            }
        } catch (NullPointerException npe) {
        }

        return items;
    }

    public ListBoxModel doFillResolutionItems(@QueryParameter String operating_system) {
        ListBoxModel items = new ListBoxModel();
        try {
            if (operating_system.isEmpty()) {
                items.add("CHOOSE A RESOLUTION", "");
            }

            OperatingSystem config = seleniumApi.operatingSystems2.get(operating_system);

            for (int i = 0; i < config.resolutions.size(); i++) {
                Resolution configResolution = config.resolutions.get(i);
                items.add(configResolution.getScreenResolution());
            }
        } catch (NullPointerException npe) {
        }
        return items;
    }
    public ListBoxModel doFillCredentialsIdItems(final @AncestorInPath ItemGroup<?> context) {
        return new StandardUsernameListBoxModel().withAll(LTCredentials.all(context));
    }


    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
        return true;
    }

}
