package lt;

import hudson.Extension;

@Extension
public class SeleniumBuildAction extends LTBuildAction {
    /*
     * Holds info about the Selenium Test
     */
    private String operatingSystemApiName,
        browserApiName,
        resolution,
        buildName,
        buildNumber;

    public SeleniumBuildAction() {
        super("");
    }

    public SeleniumBuildAction(final String os, final String browser, final String resolution) {
        super("");
        this.operatingSystemApiName = os;
        this.browserApiName = browser;
        this.resolution = resolution;
        setDisplayName("Hi Lambdatest");
        setTestUrl("Hi LT");

    }

    public String getOperatingSystem() {
        return operatingSystemApiName;
    }
    public String getBuildName() {
        return buildName;
    }
    public String getBuildNumber() {
        return buildNumber;
    }

    public void setOperatingSystem(String operatingSystemApiName) {
        this.operatingSystemApiName = operatingSystemApiName;
    }

    public String getBrowser() {
        return browserApiName;
    }

    public void setBrowser(String browserApiName) {
        this.browserApiName = browserApiName;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public void setBuildName(String buildname) {
        this.buildName = buildname;
    }
    public void setBuildNumber(String buildnumber) {
        this.buildNumber = buildnumber;
    }
}
