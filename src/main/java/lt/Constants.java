package lt;
import java.util.HashMap;
public final class Constants
{
    public static final String USERNAME = "LT_USERNAME";
    @Deprecated
    public static final String APIKEY = "LT_APIKEY";
    public static final String AUTHKEY = "LT_AUTHKEY";
    public static final String BUILDNAME =  "LT_BUILD_NAME";
    public static final String BUILDNUMBER = "LT_BUILD_NUMBER";
    public static final String OPERATINGSYSTEM = "LT_OPERATING_SYSTEM";
    public static final String BROWSER = "LT_BROWSER";
    public static final String BROWSERS = "LT_BROWSERS";
    public static final String RESOLUTION = "LT_RESOLUTION";
    public static final String BROWSERNAME = "LT_BROWSERNAME";
    public static final String DISPLAYNAME = "LambdaTest";
    public static final String TEAMCITY_CONTRIBUTER = "teamcity";
    public static final String JENKINS_CONTRIBUTER = "jenkins";
    public static final String TUNNEL_START_FAIL = "Failed to start Local Tunnel";
    public static final String TUNNEL_STOP_FAIL = "Failed to stop Local Tunnel";
    public static final String TUNNEL_START = "Started Local Tunnel";
    public static final String TUNNEL_STOP = "Stopped Local Tunnel";
    public static final String TUNNEL_NEED_TO_START = "Tunnel is currently not running. Need to start one.";
    public static final String TUNNEL_NO_NEED_TO_START = "Local Tunnel is already running. No need to start a new one.";
    public static final String TUNNEL_CONNECTED = "Tunnel is now connected.";
    public static final String TUNNEL_WAITING = "Waiting for the tunnel to establish a connection.";
    public static final String GRID_URL = "LT_GRID_URL";
    public static final String BROWSER_VERSION = "LT_BROWSER_VERSION";
    public static final String pluginUrl = "LT_PLUGIN";
    public static String browserVersion;
    @Deprecated
    public static final String TUNNEL_USING = "Going to use tunnel";
    public static final String TUNNEL_USING_DEFAULT = "Going to use default tunnel";
    public static final String AUTH_SUCCESS = "Successful Authentication";
    public static final String AUTH_FAIL = "Error: Bad username or authkey";
    @Deprecated
    public static final String TUNNEL_NO_NEED_TO_START_MSG = "Local Tunnel is already running. No need to start a new one.";
    @Deprecated
    public static final String TUNNEL_STOP_FAIL_MSG = "Failed to stop Local Tunnel";
    @Deprecated
    public static final String TUNNEL_START_MSG = "Started Local Tunnel";
    @Deprecated
    public static final String TUNNEL_START_FAIL_MSG = "Failed to start Local Tunnel";
    @Deprecated
    public static final String TUNNEL_STOP_MSG = "Stopped Local Tunnel";
    public static final String SELENIUM_START_MSG = "\n---------------------\nSELENIUM TEST RESULTS\n---------------------";
    public static final String CREDENTIALS_INVALID_MSG = "Invalid username or apikey";
    public static final String AUTH_TOKEN = "";
    public static final String configUrl = "https://dev-ml.lambdatest.com/api/v1/capability";
    public static final String authUrl = "https://dev-accounts.lambdatest.com/api/user/token/auth";
    public static final String videoUrl = "VIDEOURL";
    public static final String logUrl = "LOGURL";
    public static final String TUNNEL_USING_TUNNELNAME(String tunnelName)
    {
        return "Going to use tunnel with tunnelname \"" + tunnelName + "\"";
    }
    public static final HashMap<String,String> output=new HashMap<>();
    public static final String REPORT_TEMPLATE_PATH = "/stats.html";
    public static final String CONFIG_INFO = "$CONFIG_INFO$";
    public static final String STATUS = "$STATUS$";
    public static final String VIDEO = "$VIDEO$";
    public static final String LOGS = "$LOGS$";
    private Constants()
    {
        throw new AssertionError();
    }
}
