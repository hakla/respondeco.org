package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
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

    private ProfilePictureRepository profilePictureRepository;
    private UserService userService;
    private UserRepository userRepository;

    @Inject
    public ProfilePictureService(ProfilePictureRepository profilePictureRepository,
                                 UserService userService, UserRepository userRepository) {
        this.profilePictureRepository = profilePictureRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public ProfilePicture createProfilePicture(String label, byte[] data) throws UnsupportedEncodingException {
        User currentUser = userService.getUserWithAuthorities();
        log.debug("current user is {}", currentUser);
        ProfilePicture newProfilePicture = null;
        if(currentUser != null) {
            if(profilePictureRepository.exists(currentUser.getId())) {
                profilePictureRepository.delete(currentUser.getId());
            }
            newProfilePicture = new ProfilePicture();
            newProfilePicture.setUserId(currentUser.getId());
            newProfilePicture.setLabel(label);
            newProfilePicture.setData(data);
            log.debug("Creating profile picture : {}", newProfilePicture);
            profilePictureRepository.save(newProfilePicture);
            log.debug("Created profile picture for {}", currentUser);
        }
        return newProfilePicture;
    }

    public void deleteProfilePictureCurrentUser() {
        User currentUser = userService.getUserWithAuthorities();
        profilePictureRepository.delete(currentUser.getId());
        log.debug("Deleted profile picture for {}", currentUser);
    }

    public void deleteProfilePicture(String userlogin) throws NoSuchUserException {
        User user = userRepository.findByLogin(userlogin);
        if(user == null) {
            throw new NoSuchUserException("user " + userlogin + " does not exist.");
        }
        profilePictureRepository.delete(user.getId());
        log.debug("Deleted profile picture for {}", userlogin);
    }

}
