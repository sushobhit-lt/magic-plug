package lt;

public class LTBuildResults {
    private final String configinfo;
    private final String status;
    LTBuildResults(String osName,String browserName,boolean status){
        this.configinfo = osName+" "+browserName;
        if (status){
            this.status = "Pass";
        }else{
            this.status = "Fail";
        }

    }
    public String getConfigInfo() {
        return configinfo;
    }

    public String getStatus() {
        return status;
    }
}
