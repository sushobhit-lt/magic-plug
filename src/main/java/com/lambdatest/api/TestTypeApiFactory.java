package com.lambdatest.api;

import com.lambdatest.configurations.Browser;
import com.lambdatest.configurations.OperatingSystem;
import com.lambdatest.configurations.Resolution;

import java.util.*;

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class TestTypeApiFactory
        extends ApiFactory {
    @Deprecated
    public List<OperatingSystem> operatingSystems;
    public Map<String, OperatingSystem> operatingSystems2;
    public List<Browser> browsers;
    public Map<String, Browser> browsers2;
    public List<Resolution> resolutions;
    public Map<String, Resolution> resolutions2;
    public String configurationsAsJson = "";

    public TestTypeApiFactory(String url) {
        super(url);
    }

    public TestTypeApiFactory(String url, String username, String password) {
        super(url, username, password);
    }

    public void init() {
        operatingSystems = new LinkedList();
        operatingSystems2 = new HashMap();
        browsers = new LinkedList();
        browsers2 = new HashMap();
        resolutions = new LinkedList();
        configurationsAsJson = "";
        populateBrowsers();
    }

    void populateBrowsers() {

        //json = req.get("/browsers");

        try {
            operatingSystems = new LinkedList();
            operatingSystems2 = new HashMap();
            browsers = new LinkedList();
            browsers2 = new HashMap();
            resolutions = new LinkedList();
            resolutions2 = new HashMap();
        } catch (JSONException localJSONException) {
        }
        Map <String,String> osJson = new HashedMap();
        osJson = GetOSResult();
        //out.println("here is the json data: "+configurationsAsJson.toString());
        if ((osJson != null) && (!osJson.isEmpty())) { //&& (String.valueOf(json.charAt(0)).equals("["))) {
            for (Map.Entry<String, String> entry : osJson.entrySet())
            {
                //make request for browser configure
                Map<String, String> params = new HashMap();
                String browserJson = req.get("", params);
                String osName = entry.getKey();
                OperatingSystem operatingSystem = new OperatingSystem(entry.getKey());
                JSONObject obj = new JSONObject(osJson);
                obj = (JSONObject) obj.get("chrome");
                for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    Browser browser = new Browser(key);
                    browsers.add(browser);
                    //browsers2.put(osName,browser);
                    operatingSystem.setBrowser(browser);

                }

                this.operatingSystems.add(operatingSystem);
            }

        }
    }
    public Map<String,String> GetOSResult() {
        String json = "";
        //json = req.get("/browsers");
        json = req.getBuild("");
        Map<String,String> params = new HashMap<>();
        JSONObject obj = new JSONObject(json);
        obj = obj.getJSONObject("os");
        params.put("elcapitan",obj.getString("elcapitan"));
        params.put("highsierra",obj.getString("highsierra"));
        params.put("linux",obj.getString("linux"));
        params.put("mavericks",obj.getString("mavericks"));
        params.put("mojave",obj.getString("mojave"));
        params.put("sierra",obj.getString("sierra"));//make request for browsers,obj.getString("sierra"));
        params.put("win10",obj.getString("win10"));
        params.put("win7",obj.getString("win7"));
        params.put("win8",obj.getString("win8"));
        params.put("win8.1",obj.getString("win8.1"));
        params.put("yosemite",obj.getString("yosemite"));
        return params;


    }


}

/* Location:
 * Qualified Name:     com.lambdatest.api.TestTypeApiFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */