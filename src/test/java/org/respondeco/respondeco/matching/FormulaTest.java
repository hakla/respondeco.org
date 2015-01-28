package org.respondeco.respondeco.matching;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FormulaTest {

    private Formula formula;

    private final int N_E = 0;
    private final int N_T = 5;
    private MatchingTag matchingTag;
    private Set<MatchingTag> V;

    private FormulaParameter p1;
    private FormulaParameter p2;

    @Before
    public void setUp() throws Exception {
        matchingTag = Mockito.mock(MatchingTag.class);

        p1 = Mockito.mock(FormulaParameter.class);
        p2 = Mockito.mock(FormulaParameter.class);

        MatchingTag t1 = Mockito.mock(MatchingTag.class);
        MatchingTag t2 = Mockito.mock(MatchingTag.class);
        MatchingTag t3 = Mockito.mock(MatchingTag.class);
        MatchingTag t4 = Mockito.mock(MatchingTag.class);

        when(t1.getName()).thenReturn("t1");
        when(t2.getName()).thenReturn("t2");
        when(t3.getName()).thenReturn("t3");
        when(t4.getName()).thenReturn("t4");

        V = new TreeSet<>();
        V.add(t1);
        V.add(t2);
        V.add(t3);
        V.add(t4);

        when(p1.evaluate(any(MatchingTag.class), anySet(), anyLong(), anyLong(), anyDouble(), any(CountFunction.class))).thenReturn(4d);
        when(p2.evaluate(any(MatchingTag.class), anySet(), anyLong(), anyLong(), anyDouble(), any(CountFunction.class))).thenReturn(12d);

        formula = new Formula(p1);

        formula.setT(matchingTag);
        formula.setN_E(N_E);
        formula.setN_T(N_T);
        formula.setV(V);
    }

    @Test
    public void testEvaluate_shouldAdd() throws Exception {
        formula.setP1(p1);
        formula.setP2(p2);
        formula.setConnector(FormulaConnector.ADD);
        double evaluate = formula.evaluate(matchingTag, V, N_E, N_T, 1, null);

        assertEquals(16, evaluate, 0.00001);
    }

    @Test
    public void testEvaluate_shouldSubtract() throws Exception {
        formula.setP1(p2);
        formula.setP2(p1);
        formula.setConnector(FormulaConnector.SUBTRACT);
        double evaluate = formula.evaluate(matchingTag, V, N_E, N_T, 1, null);

        assertEquals(8, evaluate, 0.00001);
    }

    @Test
    public void testEvaluate_shouldMultiply() throws Exception {
        formula.setP1(p1);
        formula.setP2(p2);
        formula.setConnector(FormulaConnector.MULTIPLY);
        double evaluate = formula.evaluate(matchingTag, V, N_E, N_T, 1, null);

        assertEquals(48, evaluate, 0.00001);
    }

    @Test
    public void testEvaluate_shouldDivide() throws Exception {
        formula.setP1(p2);
        formula.setP2(p1);
        formula.setConnector(FormulaConnector.DIVIDE);
        double evaluate = formula.evaluate(matchingTag, V, N_E, N_T, 1, null);

        assertEquals(3, evaluate, 0.00001);
    }

}
