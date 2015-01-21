package org.respondeco.respondeco.matching;

import lombok.*;

/**
 * Created by Klaus on 15.01.2015.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ProbabilityTag {

    @NonNull
    private MatchingTag matchingTag;
    private Double probability;

}
