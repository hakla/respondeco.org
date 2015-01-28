package org.respondeco.respondeco.matching;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SolutionMultipleTest {


    Solution solution;

    private final int N_E = 2;
    private final int N_T = 5;
    private MatchingTag matchingTag;
    private Set<MatchingTag> V;

    private CountFunction countFunction = Mockito.mock(CountFunction.class);

    @Before
    public void setUp() throws Exception {
        solution = new SolutionMultiple();

        // simulate two entities that have given the one tag
        when(countFunction.apply(any(MatchingTag.class))).thenReturn(2l);

        // simulate one entity that has given both tags
        when(countFunction.apply(any(MatchingTag.class), any(MatchingTag.class))).thenReturn(1l);

        matchingTag = Mockito.mock(MatchingTag.class);

        MatchingTag v = Mockito.mock(MatchingTag.class);
        V = new TreeSet<>();
        V.add(v);
    }

    @Test
    public void testEvaluate() throws Exception {
        ProbabilityTag evaluate = solution.evaluate(matchingTag, V, N_E, N_T, 1d, countFunction);

        assertEquals(matchingTag, evaluate.getMatchingTag());
        assertEquals(0.833333, evaluate.getProbability(), 0.00001);
    }

}
