package lt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OperatingSystem {
    private String osName;
    @Deprecated
    public List<Resolution> resolutions = new ArrayList();
    @Deprecated
    public Map<String, Resolution> resolutions2 = new HashMap();
    public List<Browser> browsers = new ArrayList();
    public Map<String, Browser> browsers2 = new HashMap();

    public OperatingSystem(String name) {
        this.osName = name;
    }

    public void setBrowser(Browser browser) {
        this.browsers.add(browser);
    }

    public void setBrowser2(String browser, Browser boj) {
        this.browsers2.put(browser, boj);
    }

    public void setResolution(Resolution resolution) {
        this.resolutions.add(resolution);
    }

    public void setResolution2(String resName, Resolution resolution) {
        this.resolutions2.put(resName, resolution);
    }

    public String getOsName() {
        return osName;
    }

}
