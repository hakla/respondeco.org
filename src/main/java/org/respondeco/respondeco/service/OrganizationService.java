package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
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

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private OrganizationRepository organizationRepository;
    private UserService userService;
    private UserRepository userRepository;
    private ImageRepository imageRepository;
    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private PostingFeedRepository postingFeedRepository;
    private ResourceOfferRepository resourceOfferRepository;
    private ISOCategoryRepository isoCategoryRepository;

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
    }

    /**
     * register an organization account
     * @param name name of the organization
     * @param email email of the organization, used as login
     * @param password the password for the account
     * @param npo indicates if the organization is an npo
     * @param langKey the default language of the account
     * @return the created organization
     * @throws OrganizationAlreadyExistsException if an organization with that name already exists
     */
    public Organization registerOrganization(String name, String email, String password, Boolean npo, String langKey)
        throws OrganizationAlreadyExistsException {
        if(organizationRepository.findByName(name)!=null) {
            throw new OrganizationAlreadyExistsException(String.format("Organization %s already exists", name));
        }
        User user = userService.createUserInformation(email.toLowerCase(),
            password, null, null, null, email.toLowerCase(),
            "UNSPECIFIED", null, langKey, null);
        Organization organization = new Organization();
        organization.setName(name);
        organization.setEmail(email.toLowerCase());
        organization.setIsNpo(npo);
        organization.setOwner(user);
        PostingFeed postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);
        organization.setPostingFeed(postingFeed);
        organizationRepository.save(organization);
        user.setOrganization(organization);
        userRepository.save(user);
        return organization;
    }

    /**
     * creates a new organization with the given information
     *
     * @param name the name of the new organization
     * @param description a description of the organization
     * @param email an official email for the organization
     * @param isNpo indicator if the organization is an NPO
     * @param logo a logo dto with the id of the project logo
     * @return the newly created project
     * @throws AlreadyInOrganizationException if the user is already in an organization
     * @throws OrganizationAlreadyExistsException if an organization with that name already exists
     */
    public Organization createOrganizationInformation(String name, String description, String email,
                                                      Boolean isNpo, ImageDTO logo, List<ISOCategory> isoCategories)
        throws AlreadyInOrganizationException, OrganizationAlreadyExistsException {
        Long logoId = null;

        if (logo != null) {
            logoId = logo.getId();
        }

        return createOrganizationInformation(name, description, email, isNpo, logoId, isoCategories);
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
    public Organization createOrganizationInformation(String name, String description, String email,
                                                      Boolean isNpo, Long logoId, List<ISOCategory> isoCategories)
            throws AlreadyInOrganizationException, OrganizationAlreadyExistsException{
        if(organizationRepository.findByName(name)!=null) {
            throw new OrganizationAlreadyExistsException(String.format("Organization %s already exists", name));
        }
        if(name == null || name.length() == 0){
            throw new IllegalValueException("organization.error.nameempty", "Organization name must not be empty");
        }
        User currentUser = userService.getUserWithAuthorities();
        Organization newOrganization = new Organization();

        newOrganization.setName(name);
        newOrganization.setDescription(description);
        newOrganization.setEmail(email);
        newOrganization.setIsNpo(isNpo);
        newOrganization.setIsoCategories(isoCategories);
        if(logoId != null) {
            newOrganization.setLogo(imageRepository.findOne(logoId));
        }

        if (organizationRepository.findByOwner(currentUser) != null) {
            throw new AlreadyInOrganizationException(String.format("Current User is already owner of an organization"));
        }
        newOrganization.setOwner(currentUser);
        PostingFeed postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);
        newOrganization.setPostingFeed(postingFeed);
        currentUser.setOrganization(organizationRepository.save(newOrganization));
        userRepository.save(currentUser);

        try {
            projectService.create("ip", "", false, null, null, null, null);
        } catch (OperationForbiddenException e) {
            e.printStackTrace();
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalValueException e) {
            e.printStackTrace();
        }

        log.debug("Created Information for Organization: {}", newOrganization);
        return newOrganization;
    }

    /**
     * finds and returns an organization by its namne
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
     * updates an organization's information with the given data
     *
     * @param name a new name for the organization
     * @param description the new description for the organization
     * @param email a new organization email
     * @param isNpo flag if the organization is an NPO
     * @param logo an ImageDTO containing the id of the organization's logo
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the user is not the owner of any organization
     */
    public Organization update(String name, String description, String email,
                               Boolean isNpo, ImageDTO logo, String website, List<ISOCategory> isoCategories)
        throws NoSuchEntityException {
        Long logoId = null;

        if (logo != null) {
            logoId = logo.getId();
        }

        return update(name, description, email, isNpo, logoId, website, isoCategories);
    }

    /**
     * {@see OrganizationService#update(String,String,String,Boolean,ImageDTO}
     *
     * @param name
     * @param description
     * @param email
     * @param isNpo
     * @param logoId
     * @throws NoSuchOrganizationException
     */
    public Organization update(String name, String description, String email, Boolean isNpo, Long logoId,
                               String website, List<ISOCategory> isoCategories) throws NoSuchEntityException {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if (null == currentOrganization) {
            throw new NoSuchEntityException(String.format("Organization does not exist for %s", currentUser.getLogin()));
        }
        if(name.length() == 0) {
            throw new IllegalValueException("organization.error.nameempty", "Name must not be an empty string");
        }

        currentOrganization.setName(name);
        currentOrganization.setDescription(description);
        currentOrganization.setEmail(email);
        currentOrganization.setIsNpo(isNpo);
        currentOrganization.setOwner(currentUser);
        currentOrganization.setWebsite(website);
        currentOrganization.setIsoCategories(isoCategories);
        if(logoId != null) {
            currentOrganization.setLogo(imageRepository.findOne(logoId));
        }

        log.debug("Changing Information for Organization: {}", currentOrganization);
        return organizationRepository.save(currentOrganization);
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
                if (!project.getSuccessful()) {
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
    public List<User> getUserByOrgId(Long orgId) throws NoSuchEntityException {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(orgId);

        if(organization == null) {
            throw new NoSuchEntityException(String.format("Organization %s does not exist", orgId));
        }

        log.debug("Finding members of organization", organization.getName());
        List<User> members = userRepository.findUsersByOrganizationId(orgId);
        members.remove(organization.getOwner());
        return members;
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

    public Page<ISOCategory> getAllSuperCategories() {
        return isoCategoryRepository.findBySuperCategoryIsNull(null);
    }

    public Page<ISOCategory> getSubCategoriesOf(Long superCategoryId) {
        ISOCategory superCategory = new ISOCategory();
        superCategory.setId(superCategoryId);
        return isoCategoryRepository.findBySuperCategory(superCategory, null);
    }
}
