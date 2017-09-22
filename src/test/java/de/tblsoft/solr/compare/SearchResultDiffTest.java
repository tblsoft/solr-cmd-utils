package de.tblsoft.solr.compare;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tblsoft on 21.07.17.
 */
public class SearchResultDiffTest {


    @Test
    public void testEqualResults() {
        SearchResult reference = createSearchResult();
        SearchResult test = createSearchResult();;
        SearchResultDiff searchResultDiff = new SearchResultDiff(reference, test);
        searchResultDiff.compute();

        Assert.assertEquals(0.0, searchResultDiff.getNumFoundRelative(), 0.0);
        Assert.assertEquals(0, searchResultDiff.getNumFoundTotal());
        Assert.assertEquals(0, searchResultDiff.getPositions());
        Assert.assertEquals(0, searchResultDiff.getPositionChangedCount());
        Assert.assertEquals(0.0, searchResultDiff.getResponseTimeRelative(), 0.0);
        Assert.assertEquals(0.0, searchResultDiff.getResponseTimeTotal(), 0.0);
    }

    @Test
    public void testDifflResults() {
        SearchResult reference = createSearchResult();
        reference.getResultList().get(0).setId("0816");
        reference.getResultList().get(5).setId("0815");
        SearchResult test = createSearchResult();

        test.getResultList().get(0).setId("0815");
        test.getResultList().get(5).setId("0816");
        test.setResponseTime(1100);
        SearchResultDiff searchResultDiff = new SearchResultDiff(reference, test);
        searchResultDiff.compute();

        Assert.assertEquals(0.0, searchResultDiff.getNumFoundRelative(), 0.0);
        Assert.assertEquals(0, searchResultDiff.getNumFoundTotal());
        Assert.assertEquals(10, searchResultDiff.getPositions());
        Assert.assertEquals(2, searchResultDiff.getPositionChangedCount());
        Assert.assertEquals(10.0, searchResultDiff.getResponseTimeRelative(), 0.0);
        Assert.assertEquals(100, searchResultDiff.getResponseTimeTotal());

    }

    SearchResult createSearchResult() {
        SearchResult searchResult = new SearchResult();
        searchResult.setResponseTime(1000);
        searchResult.setNumFound(100);
        for (int i = 0; i < 72; i++) {
            searchResult.getResultList().add(new Result(String.valueOf(i), i));
        }
        return searchResult;
    }
}
