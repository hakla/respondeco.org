package org.respondeco.respondeco.service;

/**
 * Created by Clemens Puehringer on 17/08/15.
 */

import org.respondeco.respondeco.domain.ISOCategory;
import org.respondeco.respondeco.repository.ISOCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service for various functionality.
 * Only functionality that is not sufficiently extensive to warrant its own
 * Service should go here
 */
@Service
public class UtilityService {

    private ISOCategoryRepository isoCategoryRepository;

    @Inject
    public UtilityService(ISOCategoryRepository isoCategoryRepository) {
        this.isoCategoryRepository = isoCategoryRepository;
    }

    public Page<ISOCategory> getISOCategories() {
        return isoCategoryRepository.findBySuperCategoryIsNull(null);
    }

}
