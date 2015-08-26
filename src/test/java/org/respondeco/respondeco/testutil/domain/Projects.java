package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.testutil.TestUtil;

import static org.respondeco.respondeco.testutil.TestUtil.blank;

/**
 * Created by Clemens Puehringer on 19/08/15.
 */
public class Projects {

    public static final Project NEW_MINIMAL;
    public static final Project NEW_TAGS;
    public static final Project NEW_REQUIREMENTS;
    public static final Project NEW_COMPLETE;
    public static final Project SAVED_MINIMAL;
    public static final Project SAVED_TAGS;
    public static final Project SAVED_REQUIREMENTS;
    public static final Project SAVED_COMPLETE;


    static {
        NEW_MINIMAL = blank(Project.class);
        NEW_MINIMAL.setName("new project");
        NEW_MINIMAL.setPurpose("some fancy purpose");
        NEW_MINIMAL.setConcrete(false);

        NEW_TAGS = TestUtil.clone(NEW_MINIMAL);
        NEW_TAGS.setPropertyTags(TestUtil.clone(PropertyTags.NEW_LIST));

        NEW_REQUIREMENTS = TestUtil.clone(NEW_MINIMAL);
        NEW_REQUIREMENTS.setResourceRequirements(TestUtil.clone(ResourceRequirements.NEW_MINIMAL_LIST));

        NEW_COMPLETE = TestUtil.clone(NEW_MINIMAL);
        NEW_COMPLETE.setPropertyTags(TestUtil.clone(PropertyTags.NEW_LIST));
        NEW_COMPLETE.setResourceRequirements(TestUtil.clone(ResourceRequirements.NEW_MINIMAL_LIST));

        SAVED_MINIMAL = new Project();
        SAVED_MINIMAL.setId(1000L);
        SAVED_MINIMAL.setName("saved project");
        SAVED_MINIMAL.setPurpose("saved project purpose");
        SAVED_MINIMAL.setConcrete(false);

        SAVED_TAGS = TestUtil.clone(SAVED_MINIMAL);
        SAVED_TAGS.setPropertyTags(TestUtil.clone(PropertyTags.NEW_LIST));

        SAVED_REQUIREMENTS = TestUtil.clone(SAVED_MINIMAL);
        SAVED_REQUIREMENTS.setResourceRequirements(TestUtil.clone(ResourceRequirements.SAVED_MINIMAL_LIST));

        SAVED_COMPLETE = TestUtil.clone(SAVED_MINIMAL);
        SAVED_COMPLETE.setPropertyTags(TestUtil.clone(PropertyTags.SAVED_LIST));
        SAVED_COMPLETE.setResourceRequirements(TestUtil.clone(ResourceRequirements.SAVED_MINIMAL_LIST));
    }

}
