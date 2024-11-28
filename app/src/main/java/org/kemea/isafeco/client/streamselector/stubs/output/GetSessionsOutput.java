package org.kemea.isafeco.client.streamselector.stubs.output;

public class GetSessionsOutput {
    Session[] sessions;
    int total_sessions;

    public int getTotal_sessions() {
        return total_sessions;
    }

    public void setTotal_sessions(int total_sessions) {
        this.total_sessions = total_sessions;
    }

    public Session[] getSessions() {
        return sessions;
    }

    public void setSessions(Session[] sessions) {
        this.sessions = sessions;
    }
    
}
