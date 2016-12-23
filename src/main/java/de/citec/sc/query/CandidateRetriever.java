/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.citec.sc.query;

import java.util.List;

/**
 *
 * @author sherzod
 */
public interface CandidateRetriever {
    public List<Instance> getAllResources(String searchTerm, int topK);
    public List<Instance> getAllPredicates(String searchTerm, int topK);
    public List<Instance> getPredicatesInMatoll(String searchTerm, int topK);
    public List<Instance> getPredicatesInDBpedia(String searchTerm, int topK, boolean partialMatch);
    public List<Instance> getAllClasses(String searchTerm, int topK, boolean partialMatch);
    public List<Instance> getRestrictionClasses(String searchTerm, int topK);
}
