package de.tblsoft.solr.compare;

/**
 * Created by tbl on 21.07.17.
 */
public class SearchResultDiff {

    public SearchResultDiff(SearchResult reference, SearchResult test) {
        this.reference = reference;
        this.test = test;
    }

    private SearchResult reference;
    private SearchResult test;


    private int resultsPerPage = 72;

    private int positionChangedCount = 0;


    private double numFoundRelative = 0.0;
    private long numFoundTotal = 0;
    private int positions;
    private int responseTimeTotal = 0;
    private double responseTimeRelative = 0.0;

    public void compute() {
        computeNumFoundRelative();
        computeNumFoundTotal();
        computeResponseTimeRelative();
        computeResponseTimeTotal();
        computePositions();
    }

    private void computeResponseTimeRelative() {
        if(reference.getResponseTime() == 0.0 && test.getResponseTime() == 0.0) {
            this.responseTimeRelative = 0.0;
            return;
        }
        this.responseTimeRelative=Math.abs((100*test.getResponseTime()/reference.getResponseTime())-100);
    }

    private void computeResponseTimeTotal() {
        this.responseTimeTotal = Math.abs(reference.getResponseTime() - test.getResponseTime());
    }

    private void computeNumFoundRelative() {
        if(reference.getNumFound() == 0.0 && test.getNumFound() == 0.0) {
            this.numFoundRelative = 0.0;
            return;
        }
        if(reference.getNumFound() == 0.0) {
            this.numFoundRelative = 100.0;
            return;
        }

        this.numFoundRelative=Math.abs((100*test.getNumFound()/reference.getNumFound())-100);
    }

    private void computeNumFoundTotal() {
        this.numFoundTotal = Math.abs(reference.getNumFound() - test.getNumFound());
    }

    private void computePositions() {
        int positionDiff = 0;
        for(Result result : reference.getResultList()) {
            Result testResult = getTestResultById(result.getId());
            if(testResult == null) {
                positionDiff = positionDiff + (resultsPerPage - result.getPosition());
            } else {
                int diff = Math.abs(result.getPosition() - testResult.getPosition());
                positionDiff = positionDiff + diff;
                if(diff != 0) {
                    positionChangedCount++;
                }


            }
        }
        this.positions = positionDiff;
    }

    Result getTestResultById(String id) {
        for(Result result : test.getResultList()) {
            if(result.getId().equals(id)) {
                return result;
            }
        }
        return null;
    }

    public double getNumFoundRelative() {
        return numFoundRelative;
    }

    public long getNumFoundTotal() {
        return numFoundTotal;
    }

    public int getPositions() {
        return positions;
    }

    public int getPositionChangedCount() {
        return positionChangedCount;
    }

    public int getResponseTimeTotal() {
        return responseTimeTotal;
    }

    public double getResponseTimeRelative() {
        return responseTimeRelative;
    }

    public double getTotalDiff() {
        // TODO implement a smart metric
        return 0.0;
    }
}
