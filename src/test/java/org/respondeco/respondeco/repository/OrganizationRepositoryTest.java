package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.DatabaseBackedTest;
import org.respondeco.respondeco.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 21/11/14.
 */

public class OrganizationRepositoryTest extends DatabaseBackedTest {

    @Test
    public void testDeleteId_shouldSetActiveFalse() throws Exception {
        organizationRepository.delete(model.ORGANIZATION_SAVED_MINIMAL.getId());
        assertFalse(organizationRepository.findOneIgnoreActive(model.ORGANIZATION_SAVED_MINIMAL.getId()).getActive());
    }

    @Test
    public void testDeleteObject_shouldSetActiveFalse() throws Exception {
        organizationRepository.delete(model.ORGANIZATION_SAVED_MINIMAL);
        assertFalse(organizationRepository.findOneIgnoreActive(model.ORGANIZATION_SAVED_MINIMAL.getId()).getActive());
    }

    @Test
    public void testDeleteIterable_shouldSetActiveFalse() throws Exception {
        organizationRepository.delete(Arrays.asList(model.ORGANIZATION_SAVED_MINIMAL, model.ORGANIZATION1_GOVERNS_P1));
        assertFalse(organizationRepository.findOneIgnoreActive(model.ORGANIZATION_SAVED_MINIMAL.getId()).getActive());
        assertFalse(organizationRepository.findOneIgnoreActive(model.ORGANIZATION1_GOVERNS_P1.getId()).getActive());
    }

    @Test
    public void testFindAll_shouldReturnOnlyActive() throws Exception {
        organizationRepository.delete(model.ORGANIZATION_SAVED_MINIMAL);
        List<Organization> organizations = organizationRepository.findAll();
        assertTrue(organizations.contains(model.ORGANIZATION1_GOVERNS_P1));
        assertFalse(organizations.contains(model.ORGANIZATION_SAVED_MINIMAL));
    }

    @Test
    public void testFindAllPageable_shouldReturnOnlyActive() throws Exception {
        model.ORGANIZATION1_GOVERNS_P1.setActive(false);
        organizationRepository.save(model.ORGANIZATION1_GOVERNS_P1);

        Page<Organization> organizations = organizationRepository.findAll(new PageRequest(0, 10));
        assertFalse(organizations.getContent().contains(model.ORGANIZATION1_GOVERNS_P1));
        assertTrue(organizations.getContent().contains(model.ORGANIZATION_SAVED_MINIMAL));
    }

    @Test
    public void testFindAllSorted_shouldReturnOnlyActive() throws Exception {
        model.ORGANIZATION1_GOVERNS_P1.setActive(false);
        organizationRepository.save(model.ORGANIZATION1_GOVERNS_P1);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<Organization> organizations = organizationRepository.findAll(sort);
        assertFalse(organizations.contains(model.ORGANIZATION1_GOVERNS_P1));
        assertTrue(organizations.contains(model.ORGANIZATION_SAVED_MINIMAL));
    }

    @Test
    public void testFindOne_shouldReturnActiveOrganization() throws Exception {
        Organization savedOrganization =
            organizationRepository.findOne(model.ORGANIZATION1_GOVERNS_P1.getId());
        assertTrue(savedOrganization.equals(model.ORGANIZATION1_GOVERNS_P1));
    }

    @Test
    public void testFindOne_shouldNotReturnInactiveOrganization() throws Exception {
        model.ORGANIZATION1_GOVERNS_P1.setActive(false);
        organizationRepository.save(model.ORGANIZATION1_GOVERNS_P1);
        Organization savedOrganization =
            organizationRepository.findOne(model.ORGANIZATION1_GOVERNS_P1.getId());
        assertNull(savedOrganization);
    }

    @Test
    public void testFindByName_shouldReturnOrganizationByName() throws Exception {
        Organization savedOrganization = organizationRepository.findByName(model.ORGANIZATION1_GOVERNS_P1.getName());
        assertEquals(model.ORGANIZATION1_GOVERNS_P1, savedOrganization);
    }

    @Test
    public void testFindByName_shouldNotReturnInactiveOrganization() throws Exception {
        organizationRepository.delete(model.ORGANIZATION1_GOVERNS_P1);
        Organization savedOrganization = organizationRepository.findByName(model.ORGANIZATION1_GOVERNS_P1.getName());
        assertNull(savedOrganization);
    }

    @Test
    public void testFindByOwner_shouldReturnOrganizationByOwner() throws Exception {
        Organization savedOrganization = organizationRepository.findByOwner(model.ORGANIZATION_SAVED_MINIMAL.getOwner());
        assertNotNull(savedOrganization);
        assertEquals(model.ORGANIZATION_SAVED_MINIMAL, savedOrganization);
        assertEquals(model.ORGANIZATION_SAVED_MINIMAL.getOwner(), savedOrganization.getOwner());
    }

    @Test
    public void testFindByOwner_shouldNotReturnInactiveOrganization() throws Exception {
        organizationRepository.delete(model.ORGANIZATION_SAVED_MINIMAL);
        Organization savedOrganization = organizationRepository.findByOwner(model.ORGANIZATION_SAVED_MINIMAL.getOwner());
        assertNull(savedOrganization);
    }

}
