package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrgJoinRequestException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestWithActiveFlagDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrgJoinRequestService {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestService.class);

    private OrgJoinRequestRepository orgJoinRequestRepository;

    private UserService userService;

    private UserRepository userRepository;

    private OrganizationRepository organizationRepository;

    @Inject
    public OrgJoinRequestService(OrgJoinRequestRepository orgJoinRequestRepository, UserService userService, UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.orgJoinRequestRepository=orgJoinRequestRepository;
        this.userService=userService;
        this.userRepository=userRepository;
        this.organizationRepository=organizationRepository;
    }
    public OrgJoinRequest createOrgJoinRequest(String orgName, String userLogin) throws NoSuchOrganizationException, NoSuchUserException {
        User currentUser = userService.getUserWithAuthorities();
        User user = userRepository.findByLogin(userLogin);
        Organization organization = organizationRepository.findByName(orgName);
        if(user == null) {
            throw new NoSuchUserException(String.format("User does not exist", userLogin));
        }
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", orgName));
        }
        if(organization.getOwner().equals(currentUser)==false) {
            throw new IllegalArgumentException(String.format("Current user %s is not owner of organization", orgName));
        }
        OrgJoinRequest orgJoinRequest = new OrgJoinRequest();
        orgJoinRequest.setOrganization(organization);
        orgJoinRequest.setUser(user);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Created OrgJoinRequest: {}", orgJoinRequest);
        return orgJoinRequest;
    }

    public List<OrgJoinRequest> getOrgJoinRequestByOrganization(Long id) throws NoSuchOrganizationException{
        Organization organization = organizationRepository.findOne(id);

        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", id));
        }
        log.debug("Found List of OrgJoinRequest by OrgName");

        return orgJoinRequestRepository.findByOrganization(organization);
    }

    public List<OrgJoinRequest> getOrgJoinRequestByCurrentUser() {
        User user = userService.getUserWithAuthorities();

        log.debug("Get List of OrgJoinRequest by Current User");

        return orgJoinRequestRepository.findByUserAndActiveIsTrue(user);
    }

    public List<OrgJoinRequest> getOrgJoinRequestsByOwner() throws NoSuchOrganizationException {
        User user = userService.getUserWithAuthorities();
        log.debug("Get List of OrgJoinRequest by Owner");
        Organization organization = organizationRepository.findByOwner(user);
        if (organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist"));
        }
        return orgJoinRequestRepository.findByOrganization(organization);
    }

/*
    public List<OrgJoinRequestWithActiveFlagDTO> getOrgJoinRequestsByOwner() throws NoSuchOrganizationException {
        User user = userService.getUserWithAuthorities();
        log.debug("Get List of OrgJoinRequest by Owner");
        Organization organization = organizationRepository.findByOwner(user);
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist"));
        }
        List<OrgJoinRequestWithActiveFlagDTO> orgJoinRequestWithActiveFlagDTOList = new ArrayList<OrgJoinRequestWithActiveFlagDTO>();
        OrgJoinRequestWithActiveFlagDTO orgJoinRequestWithActiveFlagDTO;
        for (OrgJoinRequest orgJoinRequest:orgJoinRequestRepository.findByOrganization(organization)) {
            orgJoinRequestWithActiveFlagDTO = new OrgJoinRequestWithActiveFlagDTO();
            orgJoinRequestWithActiveFlagDTO.setId(orgJoinRequest.getId());
            orgJoinRequestWithActiveFlagDTO.setOrgName((orgJoinRequest.getOrganization()).getName());
            orgJoinRequestWithActiveFlagDTO.setUserLogin((orgJoinRequest.getUser()).getLogin());
            orgJoinRequestWithActiveFlagDTOList.add(orgJoinRequestWithActiveFlagDTO);
        }

        return orgJoinRequestWithActiveFlagDTOList;
    }*/

    public void acceptRequest(Long requestId) throws NoSuchOrgJoinRequestException, NoSuchOrganizationException, AlreadyInOrganizationException {
        User user = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findByIdAndActiveIsTrue(requestId);
        if(orgJoinRequest==null) {
            throw new NoSuchOrgJoinRequestException(String.format("OrgJoinRequest does not exist"));
        }
        if(user.equals(orgJoinRequest.getUser())==false) {
            throw new IllegalArgumentException(String.format("User %s does not match user of OrgJoinRequest", orgJoinRequest.getUser()));
        }
        Organization organization = organizationRepository.findOne(orgJoinRequest.getOrganization().getId());
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist"));
        }
        if(user.getOrganization()!=null) {
            throw new AlreadyInOrganizationException(String.format("User %s is already in an Organization", user.getLogin()));
        }
        user.setOrganization(organization);
        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Accepted Request and Deleted OrgJoinRequest: {}", requestId);
    }

    public void declineRequest(Long requestId) throws NoSuchOrgJoinRequestException, NoSuchOrganizationException {
        User user = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findByIdAndActiveIsTrue(requestId);
        if(orgJoinRequest==null) {
            throw new NoSuchOrgJoinRequestException(String.format("OrgJoinRequest does not exist"));
        }
        if(user.equals(orgJoinRequest.getUser())==false) {
            throw new IllegalArgumentException(String.format("User %s does not match user of OrgJoinRequest", orgJoinRequest.getUser()));
        }
        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Declined Request and Deleted OrgJoinRequest: {}", requestId);
    }

    public void delete(Long id) throws NoSuchOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = organizationRepository.findOne(id);
        if(organization == null) {
            throw new NoSuchOrganizationException("Organization does not exist");
        }
        if(organization.getOwner().equals(currentUser)==false) {
            throw new IllegalArgumentException("Current user is not owner of organization");
        }
        orgJoinRequestRepository.delete(id);
        log.debug("Deleted OrgJoinRequest: {}", id);
    }
}
