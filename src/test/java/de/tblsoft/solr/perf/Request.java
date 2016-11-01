package de.tblsoft.solr.perf;

/**
 * Created by oelbaer on 18.06.16.
 */
public class Request {

    private long executionTime;

    private String httpMethod;

    private String payload;

    private String out;

    public static Request create() {
        return new Request();
    }

    public Request executionTime(long executionTime) {
        this.executionTime = executionTime;
        return this;
    }

    public Request out(String out) {
        this.out = out;
        return this;
    }


    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    @Override
    public String toString() {
        return "Request{" +
                "executionTime=" + executionTime +
                ", out='" + out + '\'' +
                '}';
    }
}
