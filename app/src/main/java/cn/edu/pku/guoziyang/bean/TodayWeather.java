package cn.edu.pku.guoziyang.bean;

/**
 * 用于存放从网络获取的天气信息
 */
public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    //今日明日后日最高最低温度
    private String high;
    private String high2;
    private String high3;
    private String low;
    private String low2;
    private String low3;

    private String type;



    //get方法
    public String getCity() {
        return city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public String getQuality() {
        return quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public String getHigh2() {
        return high2;
    }

    public void setHigh2(String high2) {
        this.high2 = high2;
    }

    public String getHigh3() {
        return high3;
    }

    public void setHigh3(String high3) {
        this.high3 = high3;
    }

    public String getLow2() {
        return low2;
    }

    public void setLow2(String low2) {
        this.low2 = low2;
    }

    public String getLow3() {
        return low3;
    }

    public void setLow3(String low3) {
        this.low3 = low3;
    }

    //set方法
    public void setCity(String city) {
        this.city = city;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ",updatetime='" + updatetime+ '\'' +
                ",wendu='" + wendu + '\'' +
                ",shidu='" + shidu + '\'' +
                ",pm25='" + pm25 + '\'' +
                ",quality='" + quality + '\'' +
                ",fengxiang='" + fengxiang + '\'' +
                ",fengli='" + fengli + '\'' +
                ",date='" + date + '\'' +
                ",high='" + high + '\'' +
                ",low='" + low + '\'' +
                ",type='" + type + '\'' +
                '}';
    }
}
