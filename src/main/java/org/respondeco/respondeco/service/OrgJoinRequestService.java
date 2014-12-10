package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.OrganizationResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
    public OrgJoinRequest createOrgJoinRequest(OrganizationResponseDTO organizationDTO, UserDTO userDTO) throws NoSuchOrganizationException, NoSuchUserException, AlreadyInvitedToOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        User user = userRepository.findOne(userDTO.getId());
        Organization organization = organizationRepository.findOne(organizationDTO.getId());
        if(user == null) {
            throw new NoSuchUserException(String.format("User does not exist"));
        }
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist"));
        }
        if(organization.getOwner().equals(currentUser)==false) {
            throw new IllegalArgumentException(String.format("Current user %s is not owner of organization", currentUser));
        }

        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findByUserAndOrganizationAndActiveIsTrue(user, organization);

        if (orgJoinRequest != null) {
            throw new AlreadyInvitedToOrganizationException(String.format("User %s has an open invitation to join organization %s", user, organization));
        }

        orgJoinRequest = new OrgJoinRequest();
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

        return orgJoinRequestRepository.findByOrganizationAndActiveIsTrue(organization);
    }

    public List<OrgJoinRequest> getOrgJoinRequestByCurrentUser() {
        log.debug("Get List of OrgJoinRequest by Current User");

        return getOrgJoinRequestByUser(userService.getUserWithAuthorities());
    }

    public List<OrgJoinRequest> getOrgJoinRequestByUser(User user) {
        log.debug("Get List of OrgJoinRequest by User");
        return orgJoinRequestRepository.findByUserAndActiveIsTrue(user);
    }

    public List<OrgJoinRequest> getAll() {
        return orgJoinRequestRepository.findAll();
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

    public void acceptRequest(Long requestId, User user) throws NoSuchOrgJoinRequestException, NoSuchOrganizationException {
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
        // TODO talk with the team about this --> should a user really have to leave the formerly organization just to accept a new invitation?
//        if(user.getOrganization()!=null) {
//            throw new AlreadyInOrganizationException(String.format("User %s is already in an Organization", user.getLogin()));
//        }
        user.setOrganization(organization);
        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Accepted Request and Deleted OrgJoinRequest: {}", requestId);
    }

    public void acceptRequest(Long requestId) throws NoSuchOrgJoinRequestException, NoSuchOrganizationException {
        acceptRequest(requestId, userService.getUserWithAuthorities());
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

    public void delete(Long id) throws NoSuchOrganizationException, NotOwnerOfOrganizationException, NoSuchOrgJoinRequestException {
        User currentUser = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findOne(id);
        if (orgJoinRequest == null) {
            throw new NoSuchOrgJoinRequestException(String.format("No OrgJoinRequest %s found", id));
        }
        if(currentUser.getOrganization() == null || currentUser.getOrganization().getOwner().equals(currentUser) == false) {
            throw new NotOwnerOfOrganizationException("Current user is not owner of organization");
        }
        if (orgJoinRequest.getOrganization() != currentUser.getOrganization()) {
            throw new IllegalArgumentException("Current user is not owner of organization");
        }
        orgJoinRequestRepository.delete(id);
        log.debug("Deleted OrgJoinRequest: {}", id);
    }
}
