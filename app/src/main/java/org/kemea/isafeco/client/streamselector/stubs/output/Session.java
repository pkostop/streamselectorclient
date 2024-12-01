package org.kemea.isafeco.client.streamselector.stubs.output;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Session implements Serializable {
    @SerializedName("session_info")
    SessionInfo sessionInfo;
    @SerializedName("cluster_info")
    ClusterInfo clusterInfo;

    public Session(SessionInfo sessionInfo, ClusterInfo clusterInfo) {
        this.sessionInfo = sessionInfo;
        this.clusterInfo = clusterInfo;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public ClusterInfo getClusterInfo() {
        return clusterInfo;
    }

    public void setClusterInfo(ClusterInfo clusterInfo) {
        this.clusterInfo = clusterInfo;
    }
}
