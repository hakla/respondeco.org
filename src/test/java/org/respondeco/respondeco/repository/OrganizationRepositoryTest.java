package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.DatabaseBackedTest;
import org.respondeco.respondeco.domain.*;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 21/11/14.
 */

public class OrganizationRepositoryTest extends DatabaseBackedTest {

    @Before
    public void setup() {

    }

    @Test
    public void testFindByActiveIsTrue_shouldReturnOnlyActive() throws Exception {
        model.ORGANIZATION_SAVED_MINIMAL.setActive(false);
        organizationRepository.save(model.ORGANIZATION_SAVED_MINIMAL);

        Page<Organization> organizations = organizationRepository.findByActiveIsTrue(null);
        assertTrue(organizations.getContent().contains(model.ORGANIZATION1_GOVERNS_P1));
        assertFalse(organizations.getContent().contains(model.ORGANIZATION_SAVED_MINIMAL));
    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldReturnActiveOrganization() throws Exception {
        Organization savedOrganization =
            organizationRepository.findByIdAndActiveIsTrue(model.ORGANIZATION1_GOVERNS_P1.getId());
        assertTrue(savedOrganization.equals(model.ORGANIZATION1_GOVERNS_P1));
    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldNotReturnInactiveOrganization() throws Exception {
        model.ORGANIZATION1_GOVERNS_P1.setActive(false);
        organizationRepository.save(model.ORGANIZATION1_GOVERNS_P1);
        Organization savedOrganization =
            organizationRepository.findByIdAndActiveIsTrue(model.ORGANIZATION1_GOVERNS_P1.getId());
        assertNull(savedOrganization);
    }

    @Test
    public void testFindByName_shouldReturnOrganizationByName() throws Exception {

        List<Organization> all = organizationRepository.findAll();
        for(Organization organization : all) {
            log.debug("loaded organization {}", organization);
        }

        Organization savedOrganization = organizationRepository.findByName(model.ORGANIZATION1_GOVERNS_P1.getName());
        assertEquals(model.ORGANIZATION1_GOVERNS_P1, savedOrganization);
    }

    @Test
    public void testFindByOwner_shouldReturnOrganizationByOwner() throws Exception {

        List<Organization> all = organizationRepository.findAll();
        for(Organization organization : all) {
            log.debug("loaded organization {}", organization);
        }

        Organization savedOrganization = organizationRepository.findByOwner(model.ORGANIZATION_SAVED_MINIMAL.getOwner());
        assertNotNull(savedOrganization);
        assertEquals(model.ORGANIZATION_SAVED_MINIMAL, savedOrganization);
        assertEquals(model.ORGANIZATION_SAVED_MINIMAL.getOwner(), savedOrganization.getOwner());
    }

}
