package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.testutil.TestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 19/08/15.
 */
public class PropertyTags {

    public static final PropertyTag BLANK = new PropertyTag();
    public static final List<PropertyTag> NEW_LIST = new ArrayList<>();
    public static final List<PropertyTag> SAVED_LIST = new ArrayList<>();
    public static final List<PropertyTag> MIXED_LIST = new ArrayList<>();

    static {
        BLANK.setName("");

        PropertyTag newTag1 = new PropertyTag();
        newTag1.setName("newTag1");
        PropertyTag newTag2 = new PropertyTag();
        newTag2.setName("newTag2");
        PropertyTag newTag3 = new PropertyTag();
        newTag3.setName("newTag3");
        NEW_LIST.add(newTag1);
        NEW_LIST.add(newTag2);
        NEW_LIST.add(newTag3);

        PropertyTag savedTag1 = new PropertyTag();
        savedTag1.setId(100L);
        savedTag1.setName("savedTag1");
        PropertyTag savedTag2 = new PropertyTag();
        savedTag2.setId(101L);
        savedTag2.setName("savedTag2");
        PropertyTag savedTag3 = new PropertyTag();
        savedTag3.setId(102L);
        savedTag3.setName("savedTag3");
        SAVED_LIST.add(savedTag1);
        SAVED_LIST.add(savedTag2);
        SAVED_LIST.add(savedTag3);

        MIXED_LIST.addAll(TestUtil.clone(NEW_LIST));
        MIXED_LIST.addAll(TestUtil.clone(SAVED_LIST));
    }

}
