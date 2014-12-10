package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.ResourceMatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Roman Kern on 09.12.14.
 */
@Data
public class ResourceMatchOfferResourceOfferDTO {

    BigDecimal amount;
    Long resourceOfferId;
    Long resourceRequirementId;
    Long organizationId;
    Long projectId;
}
