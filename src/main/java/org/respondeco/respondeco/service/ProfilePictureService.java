package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

@Service
@Transactional
public class ProfilePictureService {

    private final Logger log = LoggerFactory.getLogger(ProfilePictureService.class);

    @Inject
    private ProfilePictureRepository profilePictureRepository;

    @Inject
    private UserService userService;

    public ProfilePicture createProfilePicture(String label, byte[] data) throws UnsupportedEncodingException {
        User currentUser = userService.getUserWithAuthorities();
        log.debug("current user is {}", currentUser);
        ProfilePicture newProfilePicture = null;
        if(currentUser != null) {
            newProfilePicture = new ProfilePicture();
            newProfilePicture.setUserlogin(currentUser.getLogin());
            newProfilePicture.setLabel(label);
            newProfilePicture.setData(data);
            profilePictureRepository.save(newProfilePicture);
            log.debug("Created profile picture for {}", currentUser);
        }
        return newProfilePicture;
    }

    public void deleteProfilePictureCurrentUser() {
        User currentUser = userService.getUserWithAuthorities();
        profilePictureRepository.delete(currentUser.getLogin());
        log.debug("Deleted profile picture for {}", currentUser);
    }

    public void deleteProfilePicture(String userlogin) {
        profilePictureRepository.delete(userlogin);
        log.debug("Deleted profile picture for {}", userlogin);
    }

}
