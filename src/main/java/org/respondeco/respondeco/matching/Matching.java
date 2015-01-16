package org.respondeco.respondeco.matching;

import java.util.Set;

/**
 * Created by Klaus on 15.01.2015.
 */
public interface Matching {

    /**
     * Set entities which are used by the algorithm
     * @param entities
     */
    public void setEntities(Set<MatchingEntity> entities);
    public Set<MatchingEntity> getEntities();

    /**
     * Set the tags which will be used to calculate the probabilities
     * @param tags
     */
    public void setTags(Set<MatchingTag> tags);
    public Set<MatchingTag> getTags();

    /**
     * The A-Priori parameter (default is 1)
     * @param value
     */
    public void setAPriori(double value);
    public double getAPriori();

    /**
     * Calculate the probability of for a given tag
     * @param tag
     * @return
     */
    public Set<Probability> evaluate(MatchingTag tag);

}
