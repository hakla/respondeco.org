package org.respondeco.respondeco.matching;

import lombok.*;

/**
 * Created by Klaus on 15.01.2015.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ProbabilityEntity {

    @NonNull
    private MatchingEntity matchingEntity;
    private Double probability;

}
