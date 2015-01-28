package org.respondeco.respondeco.matching;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MatchingImplTest {

    Set<MatchingEntity> myEntities;

    MatchingImpl matching;

    MatchingTag t1;
    MatchingTag t2;
    MatchingTag t3;
    MatchingTag t4;

    MatchingEntity e1;
    MatchingEntity e2;
    MatchingEntity e3;
    MatchingEntity e4;

    @Before
    public void setup() {
        myEntities = new HashSet<>();

        e1 = Mockito.mock(MatchingEntity.class);
        e2 = Mockito.mock(MatchingEntity.class);
        e3 = Mockito.mock(MatchingEntity.class);
        e4 = Mockito.mock(MatchingEntity.class);

        t1 = Mockito.mock(MatchingTag.class);
        t2 = Mockito.mock(MatchingTag.class);
        t3 = Mockito.mock(MatchingTag.class);
        t4 = Mockito.mock(MatchingTag.class);

        Set<MatchingTag> V1 = new HashSet<>();
        Set<MatchingTag> V2 = new HashSet<>();
        Set<MatchingTag> V3 = new HashSet<>();
        Set<MatchingTag> V4 = new HashSet<>();
        Set<MatchingTag> V = new HashSet<>();

        V1.add(t1);
        V1.add(t2);

        V2.add(t1);
        V2.add(t3);

        V3.add(t4);

        V4.add(t1);
        V4.add(t2);
        V4.add(t3);

        V.add(t1);
        V.add(t2);
        V.add(t3);
        V.add(t4);

        when(e1.getTags()).thenReturn(V1);
        when(e2.getTags()).thenReturn(V2);
        when(e3.getTags()).thenReturn(V3);
        when(e4.getTags()).thenReturn(V4);

        myEntities.add(e1);
        myEntities.add(e2);
        myEntities.add(e3);
        myEntities.add(e4);

        matching = new MatchingImpl();
        matching.setAPriori(1d);
        matching.setEntities(myEntities);
        matching.setTags(V);
    }

    @Test
    public void testEvaluate_ShouldUseSingleAndReturnExpectedValue() throws Exception {
        Set<MatchingEntity> V = new HashSet<>();

        Set<MatchingTag> tags = new HashSet<>();
        tags.add(t2);
        tags.add(t4);

        MatchingEntity mock = Mockito.mock(MatchingEntity.class);
        when(mock.getTags()).thenReturn(tags);

        V.add(mock);

        assertEquals(0.875, matching.evaluate(V).get(0).getProbability(), 0.000001);
    }

    @Test
    public void testEvaluate_ShouldSortEntitiesCorrectly() throws Exception {
        Set<MatchingEntity> V = new HashSet<>();

        Set<MatchingTag> tags = new HashSet<>();
        tags.add(t2);
        tags.add(t4);

        MatchingEntity e1 = Mockito.mock(MatchingEntity.class);
        when(e1.getTags()).thenReturn(createTagSet(t2, t4));

        MatchingEntity e2 = Mockito.mock(MatchingEntity.class);
        when(e2.getTags()).thenReturn(createTagSet(t1, t3));

        MatchingEntity e3 = Mockito.mock(MatchingEntity.class);
        when(e3.getTags()).thenReturn(createTagSet(t1));

        MatchingEntity e4 = Mockito.mock(MatchingEntity.class);
        when(e4.getTags()).thenReturn(createTagSet(t1, t2, t4));

        V.add(e1);
        V.add(e2);
        V.add(e3);
        V.add(e4);

        List<ProbabilityEntity> evaluate = matching.evaluate(V);

        assertEquals(e3, evaluate.get(0).getMatchingEntity());
        assertEquals(e2, evaluate.get(1).getMatchingEntity());
        assertEquals(e4, evaluate.get(2).getMatchingEntity());
        assertEquals(e1, evaluate.get(3).getMatchingEntity());
    }

    private Set<MatchingTag> createTagSet(MatchingTag... tags) {
        return new HashSet<>(Arrays.asList(tags));
    }
}
