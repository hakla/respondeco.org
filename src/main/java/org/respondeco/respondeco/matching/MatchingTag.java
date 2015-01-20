package org.respondeco.respondeco.matching;

/**
 * Created by Klaus on 15.01.2015.
 */
public interface MatchingTag extends Comparable<MatchingTag> {

    public String getName();

    @Override
    public default int compareTo(MatchingTag o) {
        return o.getName().toLowerCase().compareTo(getName().toLowerCase());
    }

}
