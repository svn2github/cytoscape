package org.biyoenformatik.cytoscape_merge.util;

public class MatchScore implements Comparable {
    public Double score = .0;
    public ComponentType type;
    public String id1, id2;

    public MatchScore(ComponentType type, double score, String id1, String id2) {
        this.type = type;
        this.score = score;
        this.id1 = id1;
        this.id2 = id2;
    }

    public int compareTo(Object o) {
        MatchScore that = (MatchScore) o;

        return this.score.compareTo( that.score );
    }
}
