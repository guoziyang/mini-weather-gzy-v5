package cn.edu.pku.guoziyang.bean;

/**
 * 用于保存从数据库中取出的城市列表的某条记录
 */

public class City {
    private String province;
    private String city;
    private String number;
    private String firstPV;
    private String allPY;
    private String allFirstPY;

    public City(String province, String city, String number, String firstPV, String allPY, String allFirstPY) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPV = firstPV;
        this.allPY = allPY;
        this.allFirstPY = allFirstPY;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstPV() {
        return firstPV;
    }

    public void setFirstPV(String firstPV) {
        this.firstPV = firstPV;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }
}
