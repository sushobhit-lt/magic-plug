package com.lambdatest.jenkins.freestyle;

import hudson.model.AbstractBuild;
import hudson.model.Action;

public abstract class AbstractFreeStyleBuildAction implements Action {
	private AbstractBuild<?, ?> build;

	protected String displayName;
	protected String iconFileName;
	protected String testUrl;

	@Override
	public String getIconFileName() {
		return iconFileName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getUrlName() {
		return testUrl;
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public void setBuild(AbstractBuild<?, ?> build) {
		this.build = build;
	}
	
	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}

	public void setDisplayName(String dn) {
		// the displayname does not wrap after 46 characters
		// so it will bleed into the view
		int maxCharactersViewable = 46;
		if (dn.length() > maxCharactersViewable - 3) {
			// going to cut the string down and add "..."
			dn = dn.substring(0, maxCharactersViewable - 3);
			dn += "...";
		}
		displayName = dn;
	}
	
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl.replaceAll("[:.()|/ ]", "").toLowerCase();
	}

}
