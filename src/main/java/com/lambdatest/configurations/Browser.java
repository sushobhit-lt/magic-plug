package com.lambdatest.configurations;

public class Browser
  extends InfoPrototype
{
  private String icon_class;
  private String browserName;
  private String version;
  
  public Browser(String name)
  {
    super(name);
    //this.icon_class = icon_class;
  }
  
//  public Browser(String name, String icon_class, String browserName)
//  {
//    super(name);
//    this.icon_class = icon_class;
//    this.browserName = browserName;
//  }
//
//  public Browser(String api_name, String name, String icon_class, String device, String browserName, String version)
//  {
//    super(name);
//    this.icon_class = icon_class;
//    this.browserName = browserName;
//    this.version = version;
//  }
  
  public String getIconClass()
  {
    return icon_class;
  }
  
  public String getBrowserName()
  {
    return browserName;
  }
  
  public String getVersion()
  {
    if (!isMobile()) {
      return version;
    }
    return "";
  }
  
  public void setVersion(String version)
  {
    if (!isMobile()) {
      this.version = version;
    }
  }
}

/* Location:
 * Qualified Name:     com.lambdatest.configurations.Browser
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */