package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.testutil.TestUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 19/08/15.
 */
public class ResourceRequirements {

    public static final List<ResourceRequirement> NEW_MINIMAL_LIST = new ArrayList<>();
    public static final List<ResourceRequirement> SAVED_MINIMAL_LIST = new ArrayList<>();
    public static final List<ResourceRequirement> MIXED_MINIMAL_LIST = new ArrayList<>();

    static {
        ResourceRequirement newRequirement1 = new ResourceRequirement();
        newRequirement1.setName("newRequirement1");
        newRequirement1.setDescription("newRequirement1Description");
        newRequirement1.setAmount(new BigDecimal(10));
        ResourceRequirement newRequirement2 = new ResourceRequirement();
        newRequirement2.setName("newRequirement2");
        newRequirement2.setDescription("newRequirement2Description");
        newRequirement2.setAmount(new BigDecimal(20));
        ResourceRequirement newRequirement3 = new ResourceRequirement();
        newRequirement3.setName("newRequirement3");
        newRequirement3.setDescription("newRequirement3Description");
        newRequirement3.setAmount(new BigDecimal(30));
        NEW_MINIMAL_LIST.add(newRequirement1);
        NEW_MINIMAL_LIST.add(newRequirement2);
        NEW_MINIMAL_LIST.add(newRequirement3);

        ResourceRequirement savedRequirement1 = new ResourceRequirement();
        savedRequirement1.setName("savedRequirement1");
        savedRequirement1.setDescription("savedRequirement1Description");
        savedRequirement1.setAmount(new BigDecimal(40));
        savedRequirement1.setOriginalAmount(new BigDecimal(40));
        ResourceRequirement savedRequirement2 = new ResourceRequirement();
        savedRequirement2.setName("savedRequirement2");
        savedRequirement2.setDescription("savedRequirement2Description");
        savedRequirement2.setAmount(new BigDecimal(50));
        savedRequirement2.setOriginalAmount(new BigDecimal(50));
        ResourceRequirement savedRequirement3 = new ResourceRequirement();
        savedRequirement3.setName("savedRequirement3");
        savedRequirement3.setDescription("savedRequirement3Description");
        savedRequirement3.setAmount(new BigDecimal(60));
        savedRequirement3.setOriginalAmount(new BigDecimal(60));
        SAVED_MINIMAL_LIST.add(savedRequirement1);
        SAVED_MINIMAL_LIST.add(savedRequirement2);
        SAVED_MINIMAL_LIST.add(savedRequirement3);

        MIXED_MINIMAL_LIST.addAll(TestUtil.clone(NEW_MINIMAL_LIST));
        MIXED_MINIMAL_LIST.addAll(TestUtil.clone(SAVED_MINIMAL_LIST));
    }

}
