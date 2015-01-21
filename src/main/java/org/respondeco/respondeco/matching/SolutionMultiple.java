package org.respondeco.respondeco.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Solution for the formula with multiple given tags
 */
public class SolutionMultiple extends Solution {

    private final double A = 0.5;
    private final double B = 0.5;

    private SolutionSingle solutionSingle = new SolutionSingle();

    private FormulaParameter a = new FormulaParameter() {
        @Override
        public double evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count) {
            double result;
            Set<MatchingTag> inclusive = new TreeSet<>();
            inclusive.add(t);
            inclusive.addAll(V);

            double p1 = count.apply(inclusive);

            // p1 is the denominator in the equation, if it is 0 then this part would evaluate to NaN
            if (p1 == 0) {
                result = 0;
            } else {
                double p2 = count.apply(V);

                result = A * ((p1 + aPriori) / (p2 + aPriori));
            }

            return result;
        }
    };

    private FormulaParameter b = new FormulaParameter() {
        @Override
        public double evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count) {
            // b/n + SUM( P( T=t | V=v_i ) )

            List<ProbabilityTag> probabilities = new ArrayList<>();
            double probability = B / V.size();

            V.forEach(v -> {
                Set<MatchingTag> tag = new TreeSet<MatchingTag>();
                tag.add(v);

                probabilities.add(solutionSingle.evaluate(t, tag, N_E, N_T, aPriori, count));
            });

            probability *= probabilities.stream().mapToDouble(p -> p.getProbability()).sum();

            return probability;
        }
    };

    public SolutionMultiple() {
        add(a).add(b);
    }

}
