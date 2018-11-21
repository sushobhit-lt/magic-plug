package lt;

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONObject;

import java.util.*;

import static java.lang.System.out;

public class Selenium {

    public Selenium() {
        init();
    }

    public List<OperatingSystem> operatingSystems;
    public Map<String, OperatingSystem> operatingSystems2;
    public List<Browser> browsers;
    public Map<String, Browser> browsers2;
    public List<Resolution> resolutions;
    public Map<String, Resolution> resolutions2;
    public String configurationsAsJson = "";

    public void init() {

        operatingSystems = new LinkedList();
        operatingSystems2 = new HashMap();
        browsers = new LinkedList();
        browsers2 = new HashMap();
        resolutions = new LinkedList();
        resolutions2 = new HashMap();

        PopulateBrowsers();
    }

    void PopulateBrowsers() {

        operatingSystems = new LinkedList();
        operatingSystems2 = new HashMap();
        browsers = new LinkedList();
        browsers2 = new HashMap();
        resolutions = new LinkedList();
        resolutions2 = new HashMap();
        Utils utils = new Utils();
        Map<String, String> osJson = new HashedMap();
        osJson = GetOSResult();

        if ((osJson != null) && (!osJson.isEmpty())) {
            for (Map.Entry<String, String> entry1 : osJson.entrySet()) {
                Map<String, String> paramOs = new HashMap();
                String osName = entry1.getKey();
                paramOs.put("os", osName);//entry1.getKey());
                String browserJson = utils.get(Constants.configUrl, paramOs);
                String osValue = entry1.getValue();
                OperatingSystem operatingSystem = new OperatingSystem(osValue);
                Map<String, String> brJson = new HashMap();
                brJson = GetBrowserResult();
                for (Map.Entry<String, String> entry2 : brJson.entrySet()) {
                    JSONObject obj = new JSONObject(browserJson);
                    String bName = entry2.getKey();
                    //entry2.getKey();
                    try {
                        obj = (JSONObject) obj.get(bName);
                    } catch (org.json.JSONException exception) {
                        continue;
                    }
                    for (Iterator iterator2 = obj.keySet().iterator(); iterator2.hasNext(); ) {
                        String browserName = (String) iterator2.next();
                        Browser browser = new Browser(browserName);
                        browsers.add(browser);
                        browsers2.put(osValue, browser);
                        operatingSystem.setBrowser(browser);
                        operatingSystem.browsers2.put(osValue, browser);
                    }
                }
                List<String> resArray = new ArrayList<>();
                resArray = GetResolution();
                for (String res : resArray) {
                    Resolution resolution = new Resolution(res);
                    operatingSystem.resolutions.add(resolution);
                    operatingSystem.resolutions2.put(res, resolution);
                }

                this.operatingSystems.add(operatingSystem);
                this.operatingSystems2.put(osValue, operatingSystem);
            }

        }

    }

    public Map<String, String> GetOSResult() {
        String json = "";
        Utils utils = new Utils();
        json = utils.getBuild(Constants.configUrl);
        Map<String, String> params = new HashMap<>();
        JSONObject obj = new JSONObject(json);
        obj = obj.getJSONObject("os");
        params.put("elcapitan", obj.getString("elcapitan"));
        params.put("highsierra", obj.getString("highsierra"));
        params.put("linux", obj.getString("linux"));
        params.put("mavericks", obj.getString("mavericks"));
        params.put("mojave", obj.getString("mojave"));
        params.put("sierra", obj.getString("sierra"));//make request for browsers,obj.getString("sierra"));
        params.put("win10", obj.getString("win10"));
        params.put("win7", obj.getString("win7"));
        params.put("win8", obj.getString("win8"));
        params.put("win8.1", obj.getString("win8.1"));
        params.put("yosemite", obj.getString("yosemite"));

        return params;


    }

    public Map<String, String> StoreOSMapping() {
        Map<String, String> params = new HashMap<>();

        params.put("OS X El Capitan", "elcapitan");
        params.put("macOS High Sierra", "highsierra");
        params.put("Linux", "linux");
        params.put("OS X Mavericks", "mavericks");
        params.put("macOS Mojave", "mojave");
        params.put("macOS Sierra", "sierra");//make request for browsers,obj.getString("sierra"));
        params.put("Windows 10", "win10");
        params.put("Windows 7", "win7");
        params.put("Windows 8", "win8");
        params.put("Windows 8.1", "win8.1");
        params.put("OS X Yosemite", "yosemite");

        return params;
    }

    public Map<String, String> GetBrowserResult() {
        String json = "";
        Utils utils = new Utils();
        json = utils.getBuild(Constants.configUrl);
        Map<String, String> params = new HashMap<>();
        JSONObject obj = new JSONObject(json);
        obj = obj.getJSONObject("browsers");
        params.put("chrome", obj.getString("chrome"));
        params.put("edge", obj.getString("edge"));
        params.put("firefox", obj.getString("firefox"));
        params.put("ie", obj.getString("ie"));
        params.put("opera", obj.getString("opera"));
        params.put("safari", obj.getString("safari"));

        return params;
    }

    public List<String> GetResolution() {
        List<String> resArray = new ArrayList<>();
        resArray.add("1024x768");
        resArray.add("1280x800");
        resArray.add("1280x1240");
        resArray.add("1366x768");
        resArray.add("1440x900");
        resArray.add("1600x1200");
        resArray.add("1680x1050");
        resArray.add("1920x1080");
        resArray.add("1920x1200");
        return resArray;
    }

}
