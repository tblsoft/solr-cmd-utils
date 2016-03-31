package de.tblsoft.solr.log.parser;

import java.util.*;

public class SolrLogRow {

    private String raw;

    private String coreName;

    private String logFile;

    private String webapp;

    private String handler;

    private Date timestamp;

    private int hits;
    private int status;

    private int qTime;

    private String url;

    private Map<String, List<String>> urlParams = new HashMap<String, List<String>>();

    public void addParam(String name, String value) {
        List<String> params = getUrlParams().get(name);
        if (params == null) {
            params = new ArrayList<String>();
        }
        params.add(value);
        getUrlParams().put(name, params);
    }

    public String getFirstUrlParam(String param) {
        List<String> params = getUrlParams().get(param);
        if (params == null || params.size() == 0) {
            return "";
        }
        String firstParam = params.get(0);
        if (firstParam == null) {
            return "";
        }
        return firstParam;
    }

    public boolean urlParamEquals(String param, String equals) {
        List<String> params = getUrlParams().get(param);
        if (params == null || params.size() == 0) {
            return false;
        }

        for (String p : params) {
            if (equals.equals(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean urlParamStartsWith(String param, String startsWith) {
        List<String> params = getUrlParams().get(param);
        if (params == null || params.size() == 0) {
            return false;
        }

        for (String p : params) {
            if (p.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }

    public boolean urlParamContains(String param, String contains) {
        List<String> params = getUrlParams().get(param);
        if (params == null || params.size() == 0) {
            return false;
        }

        for (String p : params) {
            if (p.contains(contains)) {
                return true;
            }
        }
        return false;
    }


    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getCoreName() {
        return coreName;
    }

    public void setCoreName(String coreName) {
        this.coreName = coreName;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getqTime() {
        return qTime;
    }

    public void setqTime(int qTime) {
        this.qTime = qTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, List<String>> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(Map<String, List<String>> urlParams) {
        this.urlParams = urlParams;
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
