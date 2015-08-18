package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.util.Assert;
import org.respondeco.respondeco.service.util.EntityAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional
public class OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private static final String ERROR_KEY = "organization.error";
    private static final String ERROR_ORGANIZATION_EXISTS = "exists";
    private static final String ERROR_NAME_EMPTY = "name_empty";
    private static final String ERROR_NO_OWNER = "no_owner";
    private static final String ERROR_NOT_OWNER = "not_owner";
    private static final String ERROR_NAME_CHANGE_AFTER_VERIFICATION = "name_change_after_validation";
    private static final String ERROR_NPO_CHANGE_AFTER_VERIFICATION = "npo_change_after_validation";

    private OrganizationRepository organizationRepository;
    private UserService userService;
    private UserRepository userRepository;
    private ImageRepository imageRepository;
    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private PostingFeedRepository postingFeedRepository;
    private ResourceOfferRepository resourceOfferRepository;
    private ISOCategoryRepository isoCategoryRepository;

    private ExceptionUtil.KeyBuilder errorKey;

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository,
                               UserService userService,
                               UserRepository userRepository,
                               ImageRepository imageRepository,
                               ProjectService projectService,
                               ProjectRepository projectRepository,
                               PostingFeedRepository postingFeedRepository,
                               ResourceOfferRepository resourceOfferRepository,
                               ISOCategoryRepository isoCategoryRepository) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.postingFeedRepository = postingFeedRepository;
        this.resourceOfferRepository = resourceOfferRepository;
        this.isoCategoryRepository = isoCategoryRepository;

        this.errorKey = new ExceptionUtil.KeyBuilder(ERROR_KEY);
    }

    /**
     * {@see OrganizationService#createOrganizationInformation(String,String,String,Boolean,ImageDTO}
     *
     * @param name
     * @param description
     * @param email
     * @param isNpo
     * @param logoId
     * @return
     * @throws AlreadyInOrganizationException
     * @throws OrganizationAlreadyExistsException
     */
    public Organization create(Organization organization)
            throws AlreadyInOrganizationException, OrganizationAlreadyExistsException{
        EntityAssert.isNew(organization);
        Assert.isValid(organization.getName(), errorKey.from(ERROR_NAME_EMPTY),
            "Organization name must not be empty");
        Assert.notNull(organization.getOwner(), errorKey.from(ERROR_NO_OWNER),
            "No owner was specified for creating the organization");

        if(organizationRepository.findByName(organization.getName()) != null) {
            throw new IllegalValueException(errorKey.from(ERROR_ORGANIZATION_EXISTS),
                String.format("Organization %s already exists", organization.getName()));
        }

        PostingFeed postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);
        organization.setPostingFeed(postingFeed);
        organization.getOwner().setOrganization(organizationRepository.save(organization));
        userRepository.save(organization.getOwner());

        try {
            projectService.create("ip", "", false, null, null, null, null);
        } catch (OperationForbiddenException e) {
            e.printStackTrace();
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalValueException e) {
            e.printStackTrace();
        }

        log.debug("Created Organization: {}", organization);
        return organization;
    }

    /**
     * updates an organization's information with the given data
     **/
    public Organization update(Organization updatedOrganization) throws NoSuchEntityException {
        Assert.notNull(updatedOrganization.getId(), "", "Id must not be null when updating organization information");
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        //check if the organization belongs to the current user
        if(currentOrganization == null || !currentOrganization.getId().equals(updatedOrganization.getId())) {
            throw new OperationForbiddenException(errorKey.from(ERROR_NOT_OWNER),
                String.format("The current user is not the owner of the organization"));
        }
        if(currentOrganization.getVerified()) {
            if(!currentOrganization.getName().equals(updatedOrganization.getName())) {
                throw new IllegalValueException(errorKey.from(ERROR_NAME_CHANGE_AFTER_VERIFICATION),
                    "The organization name can not be changed after it was verified");
            }
            if(!currentOrganization.getName().equals(updatedOrganization.getName())) {
                throw new IllegalValueException(errorKey.from(ERROR_NPO_CHANGE_AFTER_VERIFICATION),
                    "The NPO status can not be changed after it was verified");
            }
        }

        currentOrganization.setName(updatedOrganization.getName());
        currentOrganization.setDescription(updatedOrganization.getDescription());
        currentOrganization.setEmail(updatedOrganization.getEmail());
        currentOrganization.setIsNpo(updatedOrganization.getIsNpo());
        currentOrganization.setWebsite(updatedOrganization.getWebsite());
        currentOrganization.setIsoCategories(updatedOrganization.getIsoCategories());
        currentOrganization.setLogo(updatedOrganization.getLogo());

        log.debug("Changing Information for Organization: {}", currentOrganization);
        return organizationRepository.save(currentOrganization);
    }

    /**
     * finds and returns an organization by its name
     *
     * @param orgName the name of the organization to find
     * @return the organization with the given name
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if no organization with that name exists
     */
    @Transactional(readOnly = true)
    public Organization getOrganizationByName(String orgName) throws NoSuchEntityException {
        log.debug("getOrganizationByName(organization) called");

        Organization currentOrganization = organizationRepository.findByName(orgName);
        if(currentOrganization == null) {
            throw new NoSuchEntityException(String.format("Organization does not exist: %s", orgName));
        }

        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    /**
     * Get Organization by Id
     * @param id organization id
     * @return organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if organization with id could not be found
     */
    public Organization getById(Long id) {
        log.debug("getOfferById() with id " + id + " called");
        Optional<Organization> nullableOrganization =
            Optional.ofNullable(organizationRepository.findByIdAndActiveIsTrue(id));
        nullableOrganization.orElseThrow(() -> new NoSuchEntityException(id));
        return nullableOrganization.get();
    }

    /**
     * Returns all Organizations
     * @return a list of all active organizations
     */
    public Page<Organization> get(Pageable pageable) {
        log.debug("get() called");
        return organizationRepository.findByActiveIsTrue(pageable);
    }

    /**
     * deletes the user's organization
     *
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the user is not the owner of any organization
     */
    public void delete(Long id) throws NoSuchEntityException {
        Organization currentOrganization = organizationRepository.findByIdAndActiveIsTrue(id);
        if(currentOrganization==null) {
            throw new NoSuchEntityException(String.format("Organization %d does not exist", id));
        }
        if(currentOrganization.getMembers() != null) {
            for (User user : currentOrganization.getMembers()) {
                user.setActive(false);
                userRepository.save(user);
            }
        }
        User owner = currentOrganization.getOwner();
        owner.setActive(false);
        userRepository.save(owner);
        if(currentOrganization.getProjects() != null) {
            for (Project project : currentOrganization.getProjects()) {
                if (project.getSuccessful() == false) {
                    project.setActive(false);
                    projectRepository.save(project);
                }
            }
        }
        if(currentOrganization.getResourceOffers() != null) {
            for (ResourceOffer resourceOffer : currentOrganization.getResourceOffers()) {
                if (resourceOffer.getResourceMatches() == null) {
                    resourceOffer.setActive(false);
                    resourceOfferRepository.save(resourceOffer);
                }
            }
        }

        currentOrganization.setActive(false);
        organizationRepository.save(currentOrganization);
        log.debug("Deleted Information for Organization: {}", currentOrganization);
    }

    /**
     * removes a member from the organization
     * @param userId the id of the user to remove
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the given user does not belong to an organization
     * @throws OperationForbiddenException if the current user is not the owner of the user's organization
     */
    public void deleteMember(Long userId) throws NoSuchEntityException {
        User user = userService.getUserWithAuthorities();
        User member = userRepository.findByIdAndActiveIsTrue(userId);

        if(member == null) {
            throw new NoSuchEntityException(String.format("User %s does not exist", userId));
        }
        System.out.println(member);
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(member.getOrganization().getId());
        if(organization == null) {
            throw new NoSuchEntityException(String.format("Organization %s does not exist",
                member.getOrganization().getId()));
        }
        if(!organization.getVerified()) {
            throw new OperationForbiddenException("Organization (id: " + organization.getId() + ") not verified.");
        }
        if(!organization.getOwner().equals(user)) {
            throw new OperationForbiddenException(String.format("Current User is not owner of Organization %s ",
                organization.getOwner()));
        }
        log.debug("Deleting member from organization", user.getLogin(), organization.getName());
        member.setOrganization(null);
        userRepository.save(member);
    }

    /**
     * searches users by their organization
     * @param orgId the id of the organization to search the users for
     * @return a list of users belonging to the given organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the given organization does not exist
     */
    public Page<User> getUserByOrgId(Long orgId) throws NoSuchEntityException {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(orgId);

        if(organization == null) {
            throw new NoSuchEntityException(String.format("Organization %s does not exist", orgId));
        }

        log.debug("Finding members of organization", organization.getName());
        return userRepository.findUsersByOrganizationId(orgId, null);
    }

    /**
     * finds users which can be invited by the given organization
     * @param orgId the organization for which to search users
     * @return a list of users which can be invited by the given organization
     */
    public List<User> findInvitableUsersByOrgId(Long orgId) {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(orgId);

        // if there is no organization than all users should be returned
        if(organization != null) {
            List<User> users = userRepository.findInvitableUsers();
            User owner = null;

            // find the owner and remove him from the list
            // @TODO set the orgId of the owner when set as owner
            for (User user: users) {
                if (organization.getOwner().equals(user)) {
                    owner = user;
                    break;
                }
            }

            if (owner != null) {
                users.remove(owner);
            }

            return users;
        }
        return userRepository.findAll();
    }

    /**
     * verify or un-verify an organization
     * @param id the id of the organization
     * @param value the value of the verified flag
     * @return the updated organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the organization with the given id could not be found
     * @throws OperationForbiddenException if the current user is not administrator
     */
    public Organization verify(Long id, Boolean value) throws NoSuchEntityException, OperationForbiddenException {
        User currentUser = userService.getUserWithAuthorities();
        //if current user is not admin
        if(currentUser.getAuthorities().stream().noneMatch(auth -> auth.getName().equals(AuthoritiesConstants.ADMIN))) {
            throw new OperationForbiddenException(
                String.format("Current user %s does not have administration authorities", currentUser.getLogin()));
        }
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(id);
        if(organization == null) {
            throw new NoSuchEntityException(id);
        }
        organization.setVerified(value);
        organizationRepository.save(organization);
        return organization;
    }

    /**
     * Get the current Following State of the Organization, instead of loading all Users manually.
     * Returns TRUE: if user already follow the Organization
     * @param organizationId of which we need the following state
     * @return Boolean
     */
    public Boolean followingState(Long organizationId){

        User currentUser = userService.getUserWithAuthorities();

        return organizationRepository.findByUserIdAndOrganizationId(currentUser.getId(), organizationId) != null;
    }

    /**
     * Allow user to mark the given Organization as followed. If this become true, all newsfeed from organization
     * will be displayed in users dashboard
     * @param organizationId (organization) that user would like to follow (add to subscripotion)
     */
    public void follow(Long organizationId) throws IllegalValueException{
        User currentUser = userService.getUserWithAuthorities();

        // check if organization already exists for the current user and given project id.
        // if true, we will allow an duplicate entry that will cause primary key constraint.
        // Better to throw an exception
        if(organizationRepository.findByUserIdAndOrganizationId(currentUser.getId(), organizationId) != null){
            throw new IllegalValueException("follow.organization.rejected.error", "Cannot follow an organization that already marked as followed");
        }

        Organization selected = organizationRepository.findByIdAndActiveIsTrue(organizationId);

        // check if organization exists and is active. "Removed" organization will cause some confusion for users, so throw
        // an exception if organization is deactivated
        if(selected == null || selected.isActive() == false){
            throw new IllegalValueException("follow.organization.rejected.notfound", String.format("Could not find Organization with ID: %d", organizationId));
        }

        // add new follower
        List<Organization> followers = currentUser.getFollowOrganizations();
        followers.add(selected);
        userRepository.save(currentUser);
    }

    /**
     * Remove user from follower List and stop propagate the news from sepcific organization
     * @param organizationId (organization) to un-follow or remove newsfeed subscription
     */
    public void unfollow(Long organizationId) throws IllegalValueException{
        User currentUser = userService.getUserWithAuthorities();

        Organization selected = organizationRepository.findByUserIdAndOrganizationId(currentUser.getId(), organizationId);

        // check if organization exists and is active. "Removed" organization will cause some confusion for users, so throw
        // an exception if organization is deactivated
        if(selected == null || selected.isActive() == false){
            throw new IllegalValueException("follow.project.rejected.notfound", String.format("Could not find Project with ID: %d", organizationId));
        }

        // add new follower
        List<Organization> followers = currentUser.getFollowOrganizations();
        followers.remove(selected);
        userRepository.save(currentUser);
    }

}
