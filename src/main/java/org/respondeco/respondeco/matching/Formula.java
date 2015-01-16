package org.respondeco.respondeco.matching;

import lombok.*;

import java.util.Set;

/**
 * Created by Klaus on 16.01.2015.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Formula implements FormulaParameter {

    /**
     * The tag for which the formula will evaluate the probability
     */
    private MatchingTag t;

    /**
     * The set of tags that will be used by the formula
     */
    private Set<MatchingTag> V;

    /**
     * Amount of all entities
     */
    private long N_E;

    /**
     * Amount of all tags
     */
    private long N_T;

    /**
     * Function that returns the amount of entities that contain a given set of tags
     */
    CountFunction count;

    @NonNull
    public FormulaParameter p1;

    public FormulaParameter p2;
    public FormulaConnector connector;

    @Override
    public double evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count) {
        double result = 0;
        double _1 = p1.evaluate(t, V, N_E, N_T, aPriori, count);

        if (p2 == null) {
            result = _1;
        } else {
            double _2 = p2.evaluate(t, V, N_E, N_T, aPriori, count);

            switch (connector) {
                case ADD:
                    result += _2;
                    break;
                case SUBTRACT:
                    result -= _2;
                    break;
                case MULTIPLY:
                    result *= _2;
                    break;
                case DIVIDE:
                    result /= _2;
                    break;
            }
        }

        return result;
    }
}
