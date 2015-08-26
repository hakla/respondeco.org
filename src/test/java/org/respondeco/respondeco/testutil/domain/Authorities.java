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

    public static final Authority ROLE_ADMIN;
    public static final Authority ROLE_USER;

    public static final HashSet<Authority> ADMIN;
    public static final HashSet<Authority> USER;

    static {
        ROLE_ADMIN = new Authority();
        ROLE_ADMIN.setName("ROLE_ADMIN");

        ROLE_USER = new Authority();
        ROLE_USER.setName("ROLE_USER");

        ADMIN = new HashSet<>();
        ADMIN.add(TestUtil.clone(ROLE_ADMIN));
        ADMIN.add(TestUtil.clone(ROLE_USER));

        USER = new HashSet<>();
        USER.add(TestUtil.clone(ROLE_USER));
    }

}
