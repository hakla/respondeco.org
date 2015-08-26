package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.testutil.TestUtil;

import static org.respondeco.respondeco.testutil.TestUtil.blank;

/**
 * Created by clemens on 19/08/15.
 */
public class Users {

    public static final User NEW_MINIMAL;
    public static final User SAVED_MINIMAL;
    public static final User SAVED_COMPLETE;
    public static final User SAVED_INACTIVE;
    public static final User ADMIN_COMPLETE;

    static {
        NEW_MINIMAL = blank(User.class);
        NEW_MINIMAL.setLogin("newuser@respondeco.org");
        NEW_MINIMAL.setEmail("newuser@respondeco.org");
        NEW_MINIMAL.setPassword("newuser");

        SAVED_MINIMAL = new User();
        SAVED_MINIMAL.setId(1000L);
        SAVED_MINIMAL.setLogin("newminimal@respondeco.org");
        SAVED_MINIMAL.setEmail("newminimal@respondeco.org");
        SAVED_MINIMAL.setPassword("newminimal");
        SAVED_MINIMAL.setGender(Gender.MALE);
        SAVED_MINIMAL.setAuthorities(Authorities.USER);

        SAVED_COMPLETE = new User();
        SAVED_COMPLETE.setId(1001L);
        SAVED_COMPLETE.setLogin("complete@respondeco.org");
        SAVED_COMPLETE.setEmail("complete@respondeco.org");
        SAVED_COMPLETE.setPassword("complete");
        SAVED_COMPLETE.setTitle("Dr.");
        SAVED_COMPLETE.setFirstName("bob");
        SAVED_COMPLETE.setLastName("ross");
        SAVED_COMPLETE.setGender(Gender.MALE);
        SAVED_COMPLETE.setDescription("a short description");
        SAVED_COMPLETE.setActivationKey("abcdefghijklm");
        SAVED_COMPLETE.setActive(true);
        SAVED_COMPLETE.setActivated(true);
        SAVED_COMPLETE.setAuthorities(Authorities.USER);

        SAVED_INACTIVE = new User();
        SAVED_INACTIVE.setId(1002L);
        SAVED_INACTIVE.setLogin("complete_inactive@respondeco.org");
        SAVED_INACTIVE.setEmail("complete_inactive@respondeco.org");
        SAVED_INACTIVE.setPassword("complete_inactive");
        SAVED_INACTIVE.setTitle("Dr.");
        SAVED_INACTIVE.setFirstName("totally");
        SAVED_INACTIVE.setLastName("deactivated");
        SAVED_INACTIVE.setGender(Gender.MALE);
        SAVED_INACTIVE.setDescription("a short description");
        SAVED_INACTIVE.setActive(false);
        SAVED_INACTIVE.setActivated(true);
        SAVED_INACTIVE.setAuthorities(Authorities.USER);

        ADMIN_COMPLETE = new User();
        ADMIN_COMPLETE.setId(1003L);
        ADMIN_COMPLETE.setLogin("admin@respondeco.org");
        ADMIN_COMPLETE.setEmail("admin@respondeco.org");
        ADMIN_COMPLETE.setPassword("admin");
        ADMIN_COMPLETE.setTitle("Dr.");
        ADMIN_COMPLETE.setFirstName("admin");
        ADMIN_COMPLETE.setLastName("administrator");
        ADMIN_COMPLETE.setGender(Gender.MALE);
        ADMIN_COMPLETE.setDescription("lorem ipsum");
        ADMIN_COMPLETE.setActive(true);
        ADMIN_COMPLETE.setActivated(true);
        ADMIN_COMPLETE.setAuthorities(Authorities.ADMIN);
    }

}
