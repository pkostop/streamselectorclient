package org.kemea.isafeco.client.streamselector.stubs.output;

import com.google.gson.annotations.SerializedName;

public class ClusterInfo {
    @SerializedName("cluster_id")
    private double clusterId;

    @SerializedName("contract_id")
    private long contractId;

    public ClusterInfo(double clusterId, long contractId) {
        this.clusterId = clusterId;
        this.contractId = contractId;
    }

    public ClusterInfo() {
    }

    // Getters and Setters
    public double getClusterId() {
        return clusterId;
    }

    public void setClusterId(double clusterId) {
        this.clusterId = clusterId;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }
}
