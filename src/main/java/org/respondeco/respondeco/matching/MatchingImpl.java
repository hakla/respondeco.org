package org.respondeco.respondeco.matching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Matching algorithm
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchingImpl implements Matching {

    private Set<MatchingEntity> entities;
    private Set<MatchingTag> tags;
    private double aPriori;

    private CountFunction count = new CountFunction() {
        @Override
        public Long apply(MatchingTag... t) {
            List<MatchingTag> tags = Arrays.asList(t);

            return entities.stream().filter(entity ->
                    entity.getTags().stream().filter(
                        tag -> tags.contains(tag)
                    ).count() == tags.size()
            ).count();
        }
    };

    /**
     * The formula this algorithm uses for the calculation based on one tag
     */
    private Solution solutionSingle = new SolutionSingle();

    @Override
    public Set<Probability> evaluate(MatchingTag t) {
        Set<Probability> probabilities = new HashSet<>();

        entities.stream().forEach(p -> {
            if (p.getTags().size() == 1) {
                probabilities.add(solutionSingle.evaluate(t, tags, entities.size(), tags.size(), aPriori, count));
            }
        });

        return probabilities;
    }

}
