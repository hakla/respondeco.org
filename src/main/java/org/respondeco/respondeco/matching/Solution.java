package org.respondeco.respondeco.matching;

import java.util.Set;

/**
 * Created by Klaus on 15.01.2015.
 */
public abstract class Solution {

    double value = 0;
    Formula formula;

    public final ProbabilityTag evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count) {
        return new ProbabilityTag(t, formula.evaluate(t, V, N_E, N_T, aPriori, count));
    }

    public Solution add(FormulaParameter f) {
        return push(f, FormulaConnector.ADD);
    }

    public Solution subtract(FormulaParameter f) {
        return push(f, FormulaConnector.SUBTRACT);
    }

    public Solution multiply(FormulaParameter f) {
        return push(f, FormulaConnector.MULTIPLY);
    }

    public Solution divide(FormulaParameter f) {
        return push(f, FormulaConnector.DIVIDE);
    }

    private Solution push(FormulaParameter f, FormulaConnector connector) {
        if (formula == null) {
            formula = new Formula(f);
        } else {
            if (formula.getP1() == null) {
                // if p1 isn't set then the formula is empty

                formula.setP1(f);
            } else if (formula.getP2() == null) {
                // if p2 isn't set then we complete the part by setting p2 and the connector
                formula.setConnector(connector);
                formula.setP2(f);

                // and set the function part as p1 of a new formula part
                formula = new Formula(formula);
            }
        }

        return this;
    }

}
