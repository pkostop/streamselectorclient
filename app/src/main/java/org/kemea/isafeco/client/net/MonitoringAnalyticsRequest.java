package org.kemea.isafeco.client.net;

import java.util.List;

public class MonitoringAnalyticsRequest {

    private String source_type;
    private Test test;
    private Data data;

    private Netapp netapp;

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Netapp getNetapp() {
        return netapp;
    }

    public void setNetapp(Netapp netapp) {
        this.netapp = netapp;
    }
}

class Test {
    private String use_case;
    private String test_case;
    private String test_case_id;

    public String getUse_case() {
        return use_case;
    }

    public void setUse_case(String use_case) {
        this.use_case = use_case;
    }

    public String getTest_case() {
        return test_case;
    }

    public void setTest_case(String test_case) {
        this.test_case = test_case;
    }

    public String getTest_case_id() {
        return test_case_id;
    }

    public void setTest_case_id(String test_case_id) {
        this.test_case_id = test_case_id;
    }
}

class Data {
    private String timestamp;
    private List<Moid> moids;
    private List<Kpi> kpis;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Moid> getMoids() {
        return moids;
    }

    public void setMoids(List<Moid> moids) {
        this.moids = moids;
    }

    public List<Kpi> getKpis() {
        return kpis;
    }

    public void setKpis(List<Kpi> kpis) {
        this.kpis = kpis;
    }
}

class Moid {
    private String name;
    private String value;

    public Moid(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class Kpi {
    private String name;
    private String value;
    private String unit;

    public Kpi(String name, String value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public Kpi() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

class Netapp {
    String net_app_id;

    public String getNet_app_id() {
        return net_app_id;
    }

    public void setNet_app_id(String net_app_id) {
        this.net_app_id = net_app_id;
    }
}