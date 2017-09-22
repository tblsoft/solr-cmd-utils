package de.tblsoft.solr.compare;

/**
 * Created by tblsoft on 21.07.17.
 */
public class Result {

    public Result(String id, int position) {
        this.id = id;
        this.position = position;
    }

    private String id;

    private int position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
