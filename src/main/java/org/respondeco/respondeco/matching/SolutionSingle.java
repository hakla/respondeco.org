package org.respondeco.respondeco.matching;

import java.util.Set;

/**
 * Solution for the formula with one given tag
 */
public class SolutionSingle extends Solution {

    private FormulaParameter PV = new FormulaParameter() {

        @Override
        public double evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count) {
            if (V.size() != 1) {
                throw new IllegalArgumentException("This formula can only be used with one tag!");
            }

            // get the actual tag
            MatchingTag v = V.iterator().next();

            // count the occurrences of entities that
            double cV = count.apply(v);

            return (cV + aPriori) / (N_E + aPriori);
        }
    };

    private FormulaParameter PTV = new FormulaParameter() {

        @Override
        public double evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count) {
            if (V.size() != 1) {
                throw new IllegalArgumentException("This formula can only be used with one tag!");
            }

            // get the actual tag
            MatchingTag v = V.iterator().next();

            // count the occurrences of entities that have t and v as tag
            double cTV = count.apply(t, v);

            return (cTV + aPriori) / (N_E + aPriori);
        }
    };

    public SolutionSingle() {
        add(PTV).add(PV);
    }
}
