package org.kemea.isafeco.client.streamselector.stubs.output;

public class GetSessionsOutput {
	SessionInfo sessionInfo;
	ClusterInfo clusterInfo;
	int total_sessions;

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

	public int getTotal_sessions() {
		return total_sessions;
	}

	public void setTotal_sessions(int total_sessions) {
		this.total_sessions = total_sessions;
	}

}
