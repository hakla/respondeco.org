package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
