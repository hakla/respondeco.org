package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Logo;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.LogoRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@Transactional
public class LogoService {

    private final Logger log = LoggerFactory.getLogger(LogoService.class);

    @Inject
    private LogoRepository logoRepository;

    @Inject
    private UserService userService;

    @Inject
    private OrganizationRepository organizationRepository;


    public Logo createLogo(String label, byte[] data, String orgName) throws UnsupportedEncodingException {
        Organization organization = organizationRepository.findOne(orgName);
        User currentUser = userService.getUserWithAuthorities();
        log.debug("current user is {}", currentUser);
        Logo newLogo = null;
        if(currentUser != null) {
            newLogo = new Logo();
            if(currentUser.getLogin().equals(organization.getOwner())) {
                newLogo.setLabel(label);
                newLogo.setOrgName(orgName);
                newLogo.setData(data);
                logoRepository.save(newLogo);
                log.debug("Created organization logo for {}", orgName);
            }
            else {
                log.debug("Cannot create logo/Wrong owner", orgName);
                //TODO ADD EXCEPTION
            }

        }
        return newLogo;
    }

    public void deleteLogoCurrentUser() {
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = organizationRepository.findByOwner(currentUser.getLogin());

        logoRepository.delete(organization.getName());
        log.debug("Deleted organization logo for {}", organization.getName());
    }

    public void deleteLogo(String orgName) {
        logoRepository.delete(orgName);
        log.debug("Deleted organization logo for {}", orgName);
    }

}
