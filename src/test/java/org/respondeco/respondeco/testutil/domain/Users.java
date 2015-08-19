package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.testutil.TestUtil;

/**
 * Created by clemens on 19/08/15.
 */
public class Users {

    public static final User NEW_MINIMAL = new User();
    public static final User SAVED_MINIMAL;
    public static final User SAVED_COMPLETE;

    static {
        NEW_MINIMAL.setLogin("newuser@respondeco.org");
        NEW_MINIMAL.setEmail("newuser@respondeco.org");
        NEW_MINIMAL.setPassword("newuser");

        SAVED_MINIMAL = TestUtil.clone(NEW_MINIMAL);
        SAVED_MINIMAL.setId(1000L);

        SAVED_COMPLETE = TestUtil.clone(NEW_MINIMAL);
        SAVED_COMPLETE.setId(1001L);
        SAVED_COMPLETE.setLogin("complete@respondeco.org");
        SAVED_COMPLETE.setEmail("complete@respondeco.org");
        SAVED_COMPLETE.setPassword("complete");
        SAVED_COMPLETE.setTitle("Dr.");
        SAVED_COMPLETE.setFirstName("bob");
        SAVED_COMPLETE.setLastName("ross");
        SAVED_COMPLETE.setGender(Gender.MALE);
        SAVED_COMPLETE.setDescription("a short description");
        SAVED_COMPLETE.setActive(true);
        SAVED_COMPLETE.setActivated(true);
    }

}
