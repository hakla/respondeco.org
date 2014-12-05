package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.OrganizationAlreadyExistsException;
import org.respondeco.respondeco.service.util.RandomUtil;
import org.respondeco.respondeco.web.rest.dto.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional
public class OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private OrganizationRepository organizationRepository;

    private UserService userService;

    private UserRepository userRepository;

    private ImageRepository imageRepository;

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, UserService userService, UserRepository userRepository, ImageRepository imageRepository) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Organization createOrganizationInformation(String name, String description, String email, Boolean isNpo, Long logoId) throws AlreadyInOrganizationException, OrganizationAlreadyExistsException {
        if(organizationRepository.findByName(name)!=null) {
            throw new OrganizationAlreadyExistsException(String.format("Organization %s already exists", name));
        }
        if(name=="" || name==null){
            throw new IllegalArgumentException(String.format("Organization name must not be empty"));
        }
        User currentUser = userService.getUserWithAuthorities();
        Organization newOrganization = new Organization();

        newOrganization.setName(name);
        newOrganization.setDescription(description);
        newOrganization.setEmail(email);
        newOrganization.setIsNpo(isNpo);
        if(logoId != null) {
            newOrganization.setLogo(imageRepository.findOne(logoId));
        }

        if (organizationRepository.findByOwner(currentUser) != null) {
            throw new AlreadyInOrganizationException(String.format("Current User is already owner of an organization"));
        }
        newOrganization.setOwner(currentUser);

        currentUser.setOrganization(organizationRepository.save(newOrganization));
        userRepository.save(currentUser);
        log.debug("Created Information for Organization: {}", newOrganization);
        return newOrganization;
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByName(String orgName) throws NoSuchOrganizationException {
        log.debug("getOrganizationByName(organization) called");

        Organization currentOrganization = organizationRepository.findByName(orgName);
        if(currentOrganization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", orgName));
        }

        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByOwner() throws NoSuchOrganizationException {
        log.debug("getOrganizationByOwner() called");

        User currentUser = userService.getUserWithAuthorities();

        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", currentUser.getLogin()));
        }
        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    /**
     * Get Organization by Id
     * @param id organization id
     * @return organizationDTO
     * @throws NoSuchOrganizationException if organization with id could not be found
     */
    @Transactional(readOnly=true)
    public OrganizationDTO getOrganizationById(Long id) throws NoSuchOrganizationException {
        log.debug("getOrganizationById() with id " + id + " called");
        OrganizationDTO organizationDTO;
        Organization org = organizationRepository.findOne(id);
        organizationDTO = new OrganizationDTO(org);

        return organizationDTO;
    }

    /**
     * Returns all Organizations
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrganizationDTO> getOrganizations() {
        log.debug("getOrganizations() called");

        OrganizationDTO orgDTO;
        List<Organization> organizations = organizationRepository.findByActiveIsTrue();
        List<OrganizationDTO> organizationDTOs = new ArrayList<OrganizationDTO>();

        for(Organization org: organizations) {
            organizationDTOs.add(new OrganizationDTO(org));
        }

        return organizationDTOs;
    }

    public void updaterOrganizationInformation(String name, String description, String email, Boolean isNpo, Long logoId) throws NoSuchOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization==null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist for %s", currentUser.getLogin()));
        }
        if(name=="") {
            throw new IllegalArgumentException(String.format("Name must not be an empty string"));
        }

        currentOrganization.setName(name);
        currentOrganization.setDescription(description);
        currentOrganization.setEmail(email);
        currentOrganization.setIsNpo(isNpo);
        currentOrganization.setOwner(currentUser);
        if(logoId != null) {
            currentOrganization.setLogo(imageRepository.findOne(logoId));
        }

        organizationRepository.save(currentOrganization);
        log.debug("Changed Information for Organization: {}", currentOrganization);
    }

    public void deleteOrganizationInformation() throws NoSuchOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization==null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", currentUser.getLogin()));
        }
        currentUser.setOrganization(null);
        userRepository.save(currentUser);
        organizationRepository.delete(currentOrganization);
        log.debug("Deleted Information for Organization: {}", currentOrganization);
    }
}
