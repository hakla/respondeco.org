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


    public Logo createLogo(String label, byte[] data, Long orgId) throws UnsupportedEncodingException {
        Organization organization = organizationRepository.findOne(orgId);
        User currentUser = userService.getUserWithAuthorities();
        log.debug("current user is {}", currentUser);
        Logo newLogo = null;
        if(organization != null && currentUser != null) {
            newLogo = new Logo();
            if(currentUser.getLogin().equals(organization.getOwner())) {
                newLogo.setLabel(label);
                newLogo.setOrgId(organization.getId());
                newLogo.setData(data);
                logoRepository.save(newLogo);
                log.debug("Created organization logo for {}", organization.getName());
            }
            else {
                log.debug("Cannot create logo/Wrong owner", organization.getName());
                //TODO ADD EXCEPTION
            }

        }
        return newLogo;
    }

    public void deleteLogoCurrentUser() {
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = organizationRepository.findByOwner(currentUser.getId());

        logoRepository.delete(organization.getId());
        log.debug("Deleted organization logo for {}", organization.getName());
    }

    public void deleteLogo(String orgName) {
        Organization organization = organizationRepository.findByName(orgName);
        logoRepository.delete(organization.getId());
        log.debug("Deleted organization logo for {}", orgName);
    }

    public Logo findLogoByOrgName(String orgName) {
        Organization organization = organizationRepository.findByName(orgName);
        Logo logo = logoRepository.findOne(organization.getId());
        log.debug("Find organization logo for {}", orgName);
        return logo;
    }

}
