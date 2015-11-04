package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.testutil.TestUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by clemens on 25/08/15.
 */
public class Authorities {

    public static final String ADMIN_ROLE_ID = "ROLE_ADMIN";
    public static final String USER_ROLE_ID = "ROLE_USER";

    public static Authority userAuthority() {
        return new Authority(USER_ROLE_ID);
    }

    public static Authority adminAuthority() {
        return new Authority(ADMIN_ROLE_ID);
    }

    public static Set<Authority> userAuthorities() {
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority());
        return authorities;
    }

    public static Set<Authority> adminAuthorities() {
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority());
        authorities.add(adminAuthority());
        return authorities;
    }

}
