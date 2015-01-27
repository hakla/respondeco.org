package org.respondeco.respondeco.matching;

import javafx.collections.transformation.SortedList;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

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

    private static long counter = 0;

    private CountFunction count = new CountFunction() {
        @Override
        public Long apply(MatchingTag... t) {
            List<MatchingTag> tags = Arrays.asList(t);
            counter++;

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

    /**
     * The formula this algorithm uses for the calculation based on multiple tags
     */
    private Solution solutionMultiple = new SolutionMultiple();

    @Override
    public List<ProbabilityEntity> evaluate(Set<MatchingEntity> entities) {
        List<ProbabilityEntity> p_Entities = new ArrayList<>();

        // iterate over all entities
        entities.stream().forEach(entity -> {
            // store the probabilities for all the tags a project is tagged with
            Set<ProbabilityTag> probabilities = new HashSet<>();

            long N_V = tags.size();
            long N_E = entities.size();
            Set<MatchingTag> V = entity.getTags();

            Solution solution;

            // we use a different formula if a entity is only tagged with one tag
            if (entity.getTags().size() == 1) {
                solution = solutionSingle;
            } else {
                solution = solutionMultiple;
            }

            // calculate for each tag a probability
            entity.getTags().stream().forEach(tag -> {
                // and add it to the list of probabilities
                probabilities.add(solution.evaluate(tag, V, N_E, N_V, aPriori, count));
            });

            // aggregate the probabilities
            double probability = aggregateProbability(probabilities);

            p_Entities.add(new ProbabilityEntity(entity, probability));
        });

        p_Entities.sort(new Comparator<ProbabilityEntity>() {
            @Override
            public int compare(ProbabilityEntity o1, ProbabilityEntity o2) {
                return o2.getProbability().compareTo(o1.getProbability());
            }
        });

        System.out.println(counter);

        return p_Entities;
    }

    /**
     * For a set of probabilities this function aggregates one probability
     *
     * @param probabilities
     * @return
     */
    private double aggregateProbability(Set<ProbabilityTag> probabilities) {
        double product = probabilities
            .stream()
            .mapToDouble(p -> p.getProbability())
            .reduce(1, (a, b) -> a * b);

        return Math.pow(product, 1 / (double) probabilities.size());
    }

}
