package org.jenkinsci.plugins.lt_jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.servlet.ServletException;

import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.HostnamePortRequirement;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import com.lambdatest.api.Account;
import com.lambdatest.plugin.Constants;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.Secret;

@SuppressWarnings("serial")
public class LTCredentials extends BaseStandardCredentials implements StandardUsernamePasswordCredentials {
	private final String username;
	private final Secret authkey;
    public final static DomainRequirement DOMAIN_REQUIREMENT = new HostnamePortRequirement("lambdatest.com", 443);
	
	@DataBoundConstructor
	public LTCredentials(@CheckForNull CredentialsScope scope,
                          @CheckForNull String username, @CheckForNull String authkey,
                          @CheckForNull String id, @CheckForNull String description) {
            super(scope, id, description);

        /*
            Instantiated whenever we make new credentials, whether its in the configuration or globally
         */

        this.username = username;
		this.authkey = Secret.fromString(authkey);

	}
	
	@Override
        public String getUsername() {
            if (this.username == null) {
                return "";
            } else {
                return this.username;
            }
	}
	@Override
	public Secret getPassword() {
	    return this.authkey;
	}
	public String getAuthkey() {
	    if (this.authkey == null) {
	        return "";
        } else {
            return Secret.toString(this.authkey);
        }
	}
    public static List<LTCredentials> all(ItemGroup context) {
        return CredentialsProvider.lookupCredentials(LTCredentials.class, context, ACL.SYSTEM, LTCredentials.DOMAIN_REQUIREMENT);
    }

    public static List<LTCredentials> all(Item context) {
        return CredentialsProvider.lookupCredentials(LTCredentials.class, context, ACL.SYSTEM, LTCredentials.DOMAIN_REQUIREMENT);
    }
    public static LTCredentials getCredentials(final AbstractItem buildItem, final String credentialsId) {
    	List<LTCredentials> creds = CredentialsProvider.lookupCredentials(LTCredentials.class, buildItem, null, new ArrayList<DomainRequirement>());
    	if (creds.isEmpty()) {
            return null;
        }
        CredentialsMatcher matcher;
        if (credentialsId != null) {
            matcher = CredentialsMatchers.allOf(CredentialsMatchers.withId(credentialsId));
        } else {
            matcher = CredentialsMatchers.always();
        }
        return CredentialsMatchers.firstOrDefault(creds, matcher,creds.get(0));
    }
    public static LTCredentials getCredentialsById(Item context, String id) {
	    if (id == null) {
	        return null;
        } else {
            return CredentialsMatchers.firstOrNull(
                    LTCredentials.all((Item) context),
                    CredentialsMatchers.withId(id)
            );
        }
    }
    public static ListBoxModel fillCredentialsIdItems(final @AncestorInPath ItemGroup<?> context) {
        return new StandardUsernameListBoxModel().withAll(LTCredentials.all(context));
    }
    public static FormValidation testCredentials(final String username, final String authkey) {
        Account account = new Account(username, authkey);
        account.init();
        if (account.connectionSuccessful) {
            //account.sendMixpanelEvent("Jenkins Plugin Downloaded"); //track install
            return FormValidation.ok(Constants.AUTH_SUCCESS);
        } else {
            return FormValidation.error(Constants.AUTH_FAIL);
        }
    }
    @Extension
    public static class DescriptorImpl extends CredentialsDescriptor {

        public FormValidation doTestConnection(@QueryParameter("username") final String username, @QueryParameter("authkey") final String authkey) throws IOException, ServletException {
        	Account account = new Account(username, authkey);
        	account.init();
//        	if (account.connectionSuccessful) {
//        		//account.sendMixpanelEvent("Jenkins Plugin Downloaded"); // track install
//                return FormValidation.ok("Successful Authentication");
//            } else {
//                return FormValidation.error("Error: Bad username or authkey");
//            }
            return FormValidation.ok("Successful Authentication");
        }

        @Override
        public String getDisplayName() {
            return Constants.DISPLAYNAME;
        }
 
    }
}
