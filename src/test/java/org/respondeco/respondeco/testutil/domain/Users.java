package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.testutil.TestUtil;

import static org.respondeco.respondeco.testutil.TestUtil.blank;

/**
 * Created by clemens on 19/08/15.
 */
public class Users {

    public static User newMinimalUser() {
        User user = blank(User.class);
        user.setLogin(Strings.randomLowercaseLetters());
        user.setEmail(Strings.lastRandomLowercaseLetters());
        user.setPassword(Strings.randomLowercaseLetters());
        return user;
    }

    public static User savedMinimalUser() {
        User user = new User();
        user.setId(Numerics.nextId());
        user.setLogin(Strings.randomLowercaseLetters());
        user.setEmail(Strings.lastRandomLowercaseLetters());
        user.setPassword(Strings.randomLowercaseLetters());
        user.setGender(Gender.UNSPECIFIED);
        user.setAuthorities(Authorities.userAuthorities());
        return user;
    }

    public static User savedCompleteUser() {
        User user = savedMinimalUser();
        user.setFirstName(Strings.randomLowercaseLetters());
        user.setLastName(Strings.randomLowercaseLetters());
        user.setDescription(Strings.randomLowercaseLetters(10, 200));
        user.setActivationKey(Strings.randomLowercaseLetters());
        user.setActivated(true);
        user.setActive(true);
        return user;
    }

    public static User savedInactiveUser() {
        User user = savedCompleteUser();
        user.setActive(false);
        return user;
    }

    public static User newAdmin() {
        User user = savedCompleteUser();
        user.setAuthorities(Authorities.adminAuthorities());
        return user;
    }

}
