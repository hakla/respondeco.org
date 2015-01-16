package org.respondeco.respondeco.matching;

import java.util.Set;
import java.util.function.Function;

/**
 * Created by Klaus on 15.01.2015.
 */
public interface CountFunction   {
    Long apply(MatchingTag...t);
    default Long apply(Set<MatchingTag> tags) {
        return apply(tags.toArray(new MatchingTag[] {}));
    }
}
