package com.github.chengzhx76.buy;

import com.github.chengzhx76.buy.utils.CollectionUtils;
import com.github.chengzhx76.buy.utils.ConfigUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @desc:
 * @author: hp
 * @date: 2018/1/10
 */
public class Buyer {
    /** 乘车时间 **/
    private String stationDate;
    /** 车次 **/
    private List<String> stationTrains;
    /** 乘车地点 **/
    private String fromStation;
    /** 到达地点 **/
    private String toStation;
    /** 席别 **/
    private List<String> setType;
    /** 12306帐号 **/
    private String username;
    /** 12306密码 **/
    private String password;

    private Site site;

    public Buyer() {
    }

    public Buyer(Site site) {
        this.site = site;
    }

    public String getStationDate() {
        return stationDate;
    }

    public Buyer setStationDate(String stationDate) {
        this.stationDate = stationDate;
        return this;
    }

    public List<String> getStationTrains() {
        return stationTrains;
    }

    public Buyer setStationTrains(List<String> stationTrains) {
        this.stationTrains = stationTrains;
        return this;
    }

    public String getFromStation() {
        return fromStation;
    }

    public Buyer setFromStation(String fromStation) {
        this.fromStation = fromStation;
        return this;
    }

    public String getToStation() {
        return toStation;
    }

    public Buyer setToStation(String toStation) {
        this.toStation = toStation;
        return this;
    }

    public List<String> getSetType() {
        return setType;
    }

    public Buyer setSetType(List<String> setType) {
        this.setType = setType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Buyer setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Buyer setPassword(String password) {
        this.password = password;
        return this;
    }

    public Site getSite() {
        return site;
    }

    public Buyer setSite(Site site) {
        this.site = site;
        return this;
    }

    public static Buyer create(Site site) {
        return new Buyer(site);
    }

    public void go() {

        System.out.println("-----go--->");
    }

    private void loadConfig() {
        Properties properties = ConfigUtils.loadProperties("buy.properties");
        if (StringUtils.isBlank(getStationDate())) {
            stationDate = properties.getProperty("stationDate");
            if (StringUtils.isBlank(stationDate)) {
                throw new RuntimeException("出发日期不能为空");
            }
        }
        if (CollectionUtils.isEmpty(getStationTrains())) {
            String stationTrain = properties.getProperty("stationTrains");
            if (StringUtils.isBlank(stationTrain)) {
                throw new RuntimeException("车次不能为空");
            }
            stationTrains = Arrays.asList(StringUtils.split(stationTrain, ","));
        }
        if (StringUtils.isBlank(getFromStation())) {
            fromStation = properties.getProperty("fromStation");
            if (StringUtils.isBlank(fromStation)) {
                throw new RuntimeException("到达站不能为空");
            }
        }
        if (StringUtils.isBlank(getStationDate())) {
            stationDate = properties.getProperty("stationDate");
        }
    }

}
