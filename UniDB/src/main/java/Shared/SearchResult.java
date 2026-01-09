package Shared;


public class SearchResult {
    private int count;
    private double time ;
    private int scaned;

    public SearchResult(int count, double time, int scaned) {
        this.count = count;
        this.time = time;
        this.scaned = scaned;
    }

    public int getCount() {
        return count;
    }

    public double getTime() {
        return time;
    }

    public int getScaned() {
        return scaned;
    }
}
