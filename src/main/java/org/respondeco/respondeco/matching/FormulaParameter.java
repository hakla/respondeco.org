package org.respondeco.respondeco.matching;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Klaus on 15.01.2015.
 */
public interface FormulaParameter {

    public double evaluate(MatchingTag t, Set<MatchingTag> V, long N_E, long N_T, double aPriori, CountFunction count);

}
