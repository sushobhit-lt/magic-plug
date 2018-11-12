package com.lambdatest.api;

import java.util.HashMap;
import java.util.logging.Logger;

import com.lambdatest.plugin.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class Account
  extends ApiFactory
{
  public boolean connectionSuccessful = true;//false;
  private static final Logger log = Logger.getLogger(Account.class.getName());
  
  public Account(String username, String apikey)
  {
    super(Constants.authUrl, username, apikey);
  }
  
  public void init()
  {
    testConnection();
  }
  
  public boolean testConnection()
  {
    int userId = 0;
    String email = "";
    String json = "";

    json = req.get(Constants.authUrl);

    System.out.print(json);
    try
    {
      JSONObject results = new JSONObject(json);
      userId = results.getInt("id");
      email = results.getString("email");
      if ((userId > 0) && (email != null) && (!email.equals("")))
      {
        log.info("successful connection");
        connectionSuccessful = true;
        return true;
      }
      log.info("unsuccessful connection");
      connectionSuccessful = false;
      return false;
    }
    catch (JSONException je)
    {
      log.warning("caught exeception parsing JSON");
      connectionSuccessful = false;
    }
    return false;
  }
  
  public boolean sendMixpanelEvent(String eventName)
  {
    String json = "";
    HashMap<String, String> params = new HashMap();
    params.put("event_name", eventName);
    System.out.println("event_name: "+eventName);
    json = req.post("/sendMixpanelEvent", params);
    if (json.isEmpty()) {
      return false;
    }
    System.out.println("track install: "+json);
    return true;
  }
}

/* Location:
 * Qualified Name:     com.lambdatest.api.Account
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */