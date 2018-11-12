package com.lambdatest.api;

import com.lambdatest.configurations.Browser;
import com.lambdatest.configurations.OperatingSystem;
import com.lambdatest.configurations.Resolution;

import static java.lang.System.out;

import java.io.IOException;
import java.util.*;

import groovy.json.JsonException;
import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lambdatest.plugin.Constants;
public class Selenium
        extends TestTypeApiFactory {
    public Selenium() {
        super("");
        init();
    }

    public Selenium(String username, String apikey) {
        super("", username, apikey);
        init();
    }

    public void init() {
        operatingSystems = new LinkedList();
        operatingSystems2 = new HashMap();
        browsers = new LinkedList();
        browsers2 = new HashMap();
        resolutions = new LinkedList();
        resolutions2 = new HashMap();
        configurationsAsJson = "";
        populateBrowsers();
    }

    void populateBrowsers() {

        try {
            operatingSystems = new LinkedList();
            operatingSystems2 = new HashMap();
            browsers = new LinkedList();
            browsers2 = new HashMap();
            resolutions = new LinkedList();
            resolutions2 = new HashMap();
        } catch (JSONException localJSONException) {
        }
        Map<String, String> osJson = new HashedMap();
        osJson = GetOSResult();
        //String[] browserArray = new String[]{"chrome", "edge", "firefox", "opera", "safari"};
        out.println("here is the json data: "+osJson);
        if ((osJson != null) && (!osJson.isEmpty())) {
            for (Map.Entry<String, String> entry1 : osJson.entrySet()) {
                //make request for browser configure
                Map<String, String> paramOs = new HashMap();
                String osName = entry1.getKey();
                paramOs.put("os", osName);//entry1.getKey());
//                if (osName=="linux"){
//                    continue;
//                }
                String browserJson = req.get(Constants.configUrl, paramOs);
                OperatingSystem operatingSystem = new OperatingSystem(osName);
                //JSONObject obj = new JSONObject(browserJson);
                //out.println("here is the browser json data for os "+osName+" : "+browserJson);
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
                        // out.println(browserName+":"+osName);
                        Browser browser = new Browser(browserName);
                        browsers.add(browser);
                        browsers2.put(osName, browser);
                        operatingSystem.setBrowser(browser);
                        operatingSystem.browsers2.put(osName, browser);

                    }


                }
                this.operatingSystems.add(operatingSystem);
                this.operatingSystems2.put(osName, operatingSystem);
            }

        }
    }

    public Map<String, String> GetOSResult() {
        String json = "";
        json = req.getBuild(Constants.configUrl);
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

    public Map<String, String> GetBrowserResult() {
        String json = "";
        //json = req.get("/browsers");
        json = req.getBuild(Constants.configUrl);
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


    public String getSeleniumTestId(String name, String build, String browserApiName, String osApiName, String resolution)
            throws IOException {
        String seleniumTestId = "";
        Map<String, String> params = new HashMap();
        boolean done = false;
        for (int tryCount = 1; (tryCount <= 3) && (!done); tryCount++) {
            seleniumTestId = "";
            params.put("name", name);
            params.put("build", build);
            OperatingSystem os = (OperatingSystem) operatingSystems2.get(osApiName);
            try {
                params.put("os", os.getName());
                params.put("browser", ((Browser) browsers2.get(browserApiName)).getName());
                params.put("resolution", resolution);
            } catch (NullPointerException npe) {
                params.put("os", "");
                params.put("resolution", "");
                params.put("browser", "");
            }
            switch (tryCount) {
                case 1:
                    break;
                case 2:
                    params.remove("name");
                    params.remove("build");
                    break;
                case 3:
                    params.clear();
            }
            String json = req.get("", params);

            JSONObject j = new JSONObject(json);
            JSONArray seleniumTests = j.getJSONArray("selenium");
            for (int i = 0; (i < seleniumTests.length()) && (!done); i++) {
                JSONObject seleniumTest = seleniumTests.getJSONObject(i);
                System.out.print(seleniumTest);
                String clientPlatform = seleniumTest.getString("client_platform");
                String tmp_seleniumTestId = Integer.toString(seleniumTest.getInt("selenium_test_id"));
                if ((tryCount == 1) && (!tmp_seleniumTestId.isEmpty())) {
                    done = true;
                    seleniumTestId = tmp_seleniumTestId;
                } else if ((tryCount > 1) && (!tmp_seleniumTestId.isEmpty()) && (!clientPlatform.contains("jenkins"))) {
                    done = true;
                    seleniumTestId = tmp_seleniumTestId;
                }
            }
        }
        return seleniumTestId;
    }

    public Queue<Map<String, String>> getSeleniumTestInfo2(Map<String, String> params)
            throws IOException {
        //String json = req.get("", params);
        //System.out.println("selenium data "+json);
        return parseIdAndPublicUrl();
    }

    public Queue<Map<String, String>> getSeleniumTestInfo2(String name, String build, String browserApiName, String osApiName, String resolution)
            throws IOException {
        Map<String, String> params = new HashMap();
//        params.put("name", name);
//        params.put("build", build);
//        OperatingSystem os = (OperatingSystem) operatingSystems2.get(osApiName);
//        if (os != null) {
//            params.put("os", os.getName());
//            params.put("browser", ((Browser) browsers2.get(browserApiName)).getName());
//        }
//        params.put("resolution", r
// esolution);
        return getSeleniumTestInfo2(params);
    }

    private Queue<Map<String, String>> parseIdAndPublicUrl() {
//        JSONObject j = new JSONObject(json);
        //
        // JSONArray seleniumTests = j.getJSONArray("selenium");
        Queue<Map<String, String>> tests = new LinkedList();
//        for (int i = 0; i < seleniumTests.length(); i++) {
//            JSONObject seleniumTest = seleniumTests.getJSONObject(i);
//            //int seleniumTestId = seleniumTest.getInt("selenium_test_id");
//            //String publicUrl = seleniumTest.getString("show_result_public_url");
//
//            //testInfo.put("selenium_test_id", Integer.toString(seleniumTestId));
//
//        }
        Map<String, String> testInfo = new HashMap();
        testInfo.put("show_result_public_url", "https://dev.magicleap.lambdatest.io/automation"); //publicUrl);
        tests.add(testInfo);
        return tests;
    }

    private String apiSetAction(String seleniumTestId, String action, String param, String value)
            throws IOException {
        HashMap<String, String> params = new HashMap();
        params.put("action", action);
        params.put(param, value);
        return req.put("/" + seleniumTestId, params);
    }

    public void markPassOrFail(String seleniumTestId, boolean pass)
            throws IOException {
        if (pass) {
            apiSetAction(seleniumTestId, "set_score", "score", "pass");
        } else {
            apiSetAction(seleniumTestId, "set_score", "score", "fail");
        }
    }

    public void updateContributer(String seleniumTestId, String contributer, String contributerVersion, String pluginVersion)
            throws IOException {
        String fullContributer = contributer + contributerVersion + "|v" + pluginVersion;
        //apiSetAction(seleniumTestId, "set_contributer", "contributer", fullContributer);
    }
}

/* Location:
 * Qualified Name:     com.lambdatest.api.Selenium
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */