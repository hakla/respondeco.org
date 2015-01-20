package org.respondeco.respondeco.matching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * Matching algorithm
 */
public class MatchingImpl implements Matching {

    @Getter
    @Setter
    private Set<MatchingEntity> entities;

    @Getter
    @Setter
    private Set<MatchingTag> tags;

    @Getter
    @Setter
    private double aPriori;

    private CountFunction count = new CountFunction() {
        @Override
        public Long apply(MatchingTag... t) {
            List<MatchingTag> tags = Arrays.asList(t);

            return entities.stream().filter(entity ->
                    entity.getTags().stream().filter(
                        tags::contains
                    ).count() == tags.size()
            ).count();
        }
    };

    /**
     * The formula this algorithm uses for the calculation based on one tag
     */
    private Solution solutionSingle = new SolutionSingle();
    private Solution solutionMultiple = new SolutionMultiple();

    @Override
    public Set<Probability> evaluate(MatchingTag t) {
        Set<Probability> probabilities = new HashSet<>();

        entities.stream().forEach(p -> {
            if (p.getTags().size() == 1) {
                probabilities.add(solutionSingle.evaluate(t, p.getTags(), entities.size(), tags.size(), aPriori, count));
            } else {
                probabilities.add(solutionMultiple.evaluate(t, p.getTags(), entities.size(), tags.size(), aPriori, count));
            }
        });

        return probabilities;
    }

}
