package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.utils.Util;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MonitoringAnalyticsClient {
    public static final String NETWORK_APPLICATION = "Network_Application";
    public static final String USE_CASE_OC_9_1 = "OC09_1";
    public static final String TEST_CASE_OPEN_CALL_TC = "Open Call TC";
    public static final String TEST_CASE_ID = "dummy";
    public static final String MOBILE_DEVICE_IP = "ip";
    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String STREAM_SELECTOR = "StreamSelector";
    String url;

    public MonitoringAnalyticsClient(String url) {
        this.url = url;
    }

    public void sendMonitoringAnalyticsRequest(long txMBytes, long heapMemory, Double bitRate, float videoFps) throws Exception {
        MonitoringAnalyticsRequest monitoringAnalyticsRequest = new MonitoringAnalyticsRequest();
        Data data = new Data();
        monitoringAnalyticsRequest.setData(data);
        data.setKpis(Arrays.asList(new Kpi("videosize", String.valueOf(txMBytes), "MB"),
                new Kpi("appheap", String.valueOf(heapMemory), "MB"), new Kpi("bitrate", String.valueOf(bitRate), "mbps"), new Kpi("videofps", String.valueOf(videoFps), "no")
        ));
        data.setTimestamp(new SimpleDateFormat(ISO8601_FORMAT).format(new Date()));
        data.setMoids(Arrays.asList(new Moid(MOBILE_DEVICE_IP, ApplicationMonitoringUtil.getDeviceIPAddress())));
        Test test = new Test();
        monitoringAnalyticsRequest.setTest(test);
        test.setTest_case(TEST_CASE_OPEN_CALL_TC);
        test.setUse_case(USE_CASE_OC_9_1);
        test.setTest_case_id(TEST_CASE_ID);
        monitoringAnalyticsRequest.setSource_type(NETWORK_APPLICATION);
        Netapp netapp = new Netapp();
        netapp.setNet_app_id(STREAM_SELECTOR);
        monitoringAnalyticsRequest.setNetapp(netapp);
        Map<String, String> headers = new HashMap<String, String>();
        String json = Util.toJson(monitoringAnalyticsRequest);
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(json.length()));
        NetUtil.post(url, json, headers, StandardCharsets.UTF_8.name(), 10000, 10000);
    }
}

