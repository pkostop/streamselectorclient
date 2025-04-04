package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.utils.Util;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MonitoringAnalyticsClient {
    public static final String NETWORK_APPLICATION = "Network_Application";
    public static final String USE_CASE_OC_9_1 = "OC9_1";
    public static final String TEST_CASE_OPEN_CALL_TC = "Open Call TC";
    public static final String TEST_CASE_ID = "dummy";
    public static final String MOBILE_DEVICE_IP = "IP";
    String url;

    public MonitoringAnalyticsClient(String url) {
        this.url = url;
    }

    public void sendMonitoringAnalyticsRequest(long txMBytes, long heapMemory) throws Exception {
        MonitoringAnalyticsRequest monitoringAnalyticsRequest = new MonitoringAnalyticsRequest();
        Data data = new Data();
        monitoringAnalyticsRequest.setData(data);
        data.setKpis(Arrays.asList(new Kpi("Data Transmitted in bytes", String.valueOf(txMBytes), "MB"),
                new Kpi("Application Heap Memory Consumption", String.valueOf(heapMemory), "MB")
        ));
        data.setTimestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        data.setMoids(Arrays.asList(new Moid(MOBILE_DEVICE_IP, InetAddress.getLocalHost().getHostAddress())));
        Test test = new Test();
        monitoringAnalyticsRequest.setTest(test);
        test.setTestCase(TEST_CASE_OPEN_CALL_TC);
        test.setUseCase(USE_CASE_OC_9_1);
        test.setTestCaseId(TEST_CASE_ID);
        monitoringAnalyticsRequest.setSourceType(NETWORK_APPLICATION);
        Map<String, String> headers = new HashMap<String, String>();
        String json = Util.toJson(monitoringAnalyticsRequest);
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(json.length()));
        NetUtil.post(url, json, headers, StandardCharsets.UTF_8.name(), 10000, 10000);
    }
}

