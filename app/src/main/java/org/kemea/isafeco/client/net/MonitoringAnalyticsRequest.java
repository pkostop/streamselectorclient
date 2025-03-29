package org.kemea.isafeco.client.net;

import java.util.List;

public class MonitoringAnalyticsRequest {

    private String sourceType;
    private Test test;
    private Data data;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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


}

class Test {
    private String useCase;
    private String testCase;
    private String testCaseId;

    public String getUseCase() {
        return useCase;
    }

    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
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