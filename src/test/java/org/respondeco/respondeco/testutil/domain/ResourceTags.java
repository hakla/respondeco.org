package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.testutil.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.respondeco.respondeco.testutil.TestUtil.blank;

/**
 * Created by clemens on 19/08/15.
 */
public class ResourceTags {

    public static final ResourceTag BLANK;
    public static final ArrayList<ResourceTag> NEW_LIST = new ArrayList<>();
    public static final ArrayList<ResourceTag> SAVED_LIST = new ArrayList<>();
    public static final ArrayList<ResourceTag> MIXED_LIST = new ArrayList<>();

    static {
        BLANK = blank(ResourceTag.class);
        BLANK.setName("");

        ResourceTag newTag1 = blank(ResourceTag.class);
        newTag1.setName("newTag1");
        ResourceTag newTag2 = blank(ResourceTag.class);
        newTag2.setName("newTag2");
        ResourceTag newTag3 = blank(ResourceTag.class);
        newTag3.setName("newTag3");
        NEW_LIST.add(newTag1);
        NEW_LIST.add(newTag2);
        NEW_LIST.add(newTag3);

        ResourceTag savedTag1 = new ResourceTag();
        savedTag1.setId(100L);
        savedTag1.setName("savedTag1");
        ResourceTag savedTag2 = new ResourceTag();
        savedTag2.setId(101L);
        savedTag2.setName("savedTag2");
        ResourceTag savedTag3 = new ResourceTag();
        savedTag3.setName("savedTag3");
        SAVED_LIST.add(savedTag1);
        SAVED_LIST.add(savedTag2);
        SAVED_LIST.add(savedTag3);

        MIXED_LIST.addAll(TestUtil.clone(NEW_LIST));
        MIXED_LIST.addAll(TestUtil.clone(SAVED_LIST));
    }

}
