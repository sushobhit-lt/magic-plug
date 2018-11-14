package com.lambdatest.configurations;

public class Resolution
  extends InfoPrototype
{
  private String screenResolution;
  private String deviceOrientation = "";
  public Resolution(String name){
    super(name);
    screenResolution = name;
  }
//  {
//    super(name, name, device);
//    screenResolution = name;
//  }
//  @Deprecated
//  public Resolution(String name)
//  {
//    super(name, name);
//    screenResolution = name;
//  }
//
//  public Resolution(String name, String device)
//  {
//    super(name, name, device);
//    screenResolution = name;
//  }
//
//  public Resolution(String name, String device, String deviceOrientation)
//  {
//    super(name, name, device);
//    this.deviceOrientation = deviceOrientation;
//    screenResolution = name;
//  }
//
//  public String getDeviceOrientation()
//  {
//    if (isMobile()) {
//      return deviceOrientation;
//    }
//    return "";
//  }
//
  public String getScreenResolution()
  {
    return screenResolution;
  }
//
//  public void setDeviceOrientation(String deviceOrientation)
//  {
//    this.deviceOrientation = deviceOrientation;
//  }
}

/* Location:
 * Qualified Name:     com.lambdatest.configurations.Resolution
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */