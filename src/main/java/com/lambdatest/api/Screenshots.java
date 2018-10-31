package com.lambdatest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Screenshots
  extends TestTypeApiFactory
{
  public List<String> browserLists;
  public List<String> loginProfiles;
  private static final Logger log = Logger.getLogger(Screenshots.class.getName());
  
  public Screenshots(String username, String apikey)
  {
    super("screenshots", username, apikey);
    init();
  }
  
  public void init()
  {
    super.init();
    browserLists = new LinkedList();
    loginProfiles = new LinkedList();
    populateBrowserLists();
    populateSavedLoginProfiles();
    populateSavedSeleniumScripts();
  }
  
  private void populateBrowserLists()
  {
    String json = "";
    json = req.get("/browserlists");
    log.finest("json = " + json);
    try
    {
      JSONTokener jt = new JSONTokener(json);
      JSONArray j_browserLists = new JSONArray(jt);
      for (int i = 0; i < j_browserLists.length(); i++)
      {
        JSONObject j_browserList = j_browserLists.getJSONObject(i);
        String browser_list_name = j_browserList.getString("browser_list_name");
        browserLists.add(browser_list_name);
      }
    }
    catch (JSONException jsone)
    {
      log.warning("got error from JSON serialization");
    }
  }
  
  private void populateSavedLoginProfiles()
  {
    String json = "";
    json = req.get("/loginprofiles/");
    try
    {
      JSONTokener jt = new JSONTokener(json);
      JSONArray j_loginProfiles = new JSONArray(jt);
      for (int i = 0; i < j_loginProfiles.length(); i++)
      {
        JSONObject j_loginProfile = j_loginProfiles.getJSONObject(i);
        String loginProfileName = j_loginProfile.getString("profile_name");
        loginProfiles.add(loginProfileName);
      }
    }
    catch (JSONException jsone)
    {
      log.warning("got error from JSON serialization");
    }
  }
  
  private void populateSavedSeleniumScripts()
  {
    String json = "";
    json = req.get("/seleniumscripts");
    log.finest("json = " + json);
    try
    {
      JSONTokener jt = new JSONTokener(json);
      JSONArray j_seleniumScripts = new JSONArray(jt);
      for (int i = 0; i < j_seleniumScripts.length(); i++)
      {
        JSONObject j_seleniumScript = j_seleniumScripts.getJSONObject(i);
        String seleniumScriptName = j_seleniumScript.getString("script_name");
        loginProfiles.add(seleniumScriptName);
      }
    }
    catch (JSONException jsone)
    {
      log.warning("got error from JSON serialization");
    }
  }
  
  public HashMap<String, String> runScreenshotTest(String selectedBrowserList, String url)
  {
    HashMap<String, String> params = new HashMap();
    params.put("url", url);
    params.put("browser_list_name", selectedBrowserList);
    return runScreenshotTest(params);
  }
  
  public HashMap<String, String> runScreenshotTest(String browserList, String url, String loginProfile)
  {
    HashMap<String, String> params = new HashMap();
    params.put("url", url);
    params.put("browser_list_name", browserList);
    params.put("login", loginProfile);
    return runScreenshotTest(params);
  }
  
  private HashMap<String, Object> addMultipleBrowsers(List<Map<String, String>> browsers, HashMap<String, Object> params)
  {
    List<String> browsersParam = new LinkedList();
    ListIterator<Map<String, String>> browserIterator = browsers.listIterator();
    while (browserIterator.hasNext())
    {
      Map<String, String> browser = (Map)browserIterator.next();
      String browserString = (String)browser.get("os_api_name") + "|" + (String)browser.get("browser_api_name") + "|" + (String)browser.get("resolution");
      browsersParam.add(browserString);
    }
    params.put("browsers", browsersParam);
    return params;
  }
  
  public HashMap<String, String> runScreenshot(List<Map<String, String>> browsers, String url)
  {
    HashMap<String, Object> params = new HashMap();
    params.put("url", url);
    params = addMultipleBrowsers(browsers, params);
    return runScreenshotTest(params, true);
  }
  
  public HashMap<String, String> runScreenshot(List<Map<String, String>> browsers, String url, String loginProfile)
  {
    HashMap<String, Object> params = new HashMap();
    params.put("url", url);
    params.put("login", loginProfile);
    params = addMultipleBrowsers(browsers, params);
    return runScreenshotTest(params, true);
  }
  
  @Deprecated
  private HashMap<String, String> runScreenshotTest(HashMap<String, String> params)
  {
    String json = "";
    json = req.post("/", params);
    return parseResults(json);
  }
  
  private HashMap<String, String> runScreenshotTest(HashMap<String, Object> params, boolean paramsContainsMultipleBrowsers)
  {
    String json = "";
    json = req.post("/", params, paramsContainsMultipleBrowsers);
    return parseResults(json);
  }
  
  private HashMap<String, String> parseResults(String json)
  {
    HashMap<String, String> results = new HashMap();
    try
    {
      JSONObject screenshotResults = new JSONObject(json);
      JSONArray screenshotVersions = screenshotResults.getJSONArray("versions");
      JSONObject latestScreenshotVersion = screenshotVersions.getJSONObject(screenshotVersions.length() - 1);
      
      results.put("screenshot_test_id", Integer.toString(screenshotResults.getInt("screenshot_test_id")));
      results.put("url", screenshotResults.getString("url"));
      results.put("version_id", Integer.toString(latestScreenshotVersion.getInt("version_id")));
      results.put("download_results_zip_public_url", latestScreenshotVersion.getString("download_results_zip_public_url"));
      results.put("show_results_public_url", latestScreenshotVersion.getString("show_results_public_url"));
      results.put("active", Boolean.toString(latestScreenshotVersion.getBoolean("active")));
    }
    catch (Exception e)
    {
      results.put("error", e.toString());
      results.put("json", json);
    }
    return results;
  }
  
  public boolean testIsRunning(String screenshotsTestId)
    throws IOException
  {
    String json = req.get("/" + screenshotsTestId);
    HashMap<String, String> results = parseResults(json);
    return Boolean.parseBoolean((String)results.get("active"));
  }
}

/* Location:
 * Qualified Name:     com.crossbrowsertesting.api.Screenshots
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */