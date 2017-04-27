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
    public enum Language{EN, DE, ES};
    
    public List<Instance> getAllResources(String searchTerm, int topK, Language lang);
    public List<Instance> getAllPredicates(String searchTerm, int topK, Language lang);
    public List<Instance> getPredicatesInMatoll(String searchTerm, int topK, Language lang);
    public List<Instance> getPredicatesInDBpedia(String searchTerm, int topK, boolean partialMatch, Language lang);
    public List<Instance> getAllClasses(String searchTerm, int topK, boolean partialMatch, Language lang);
    public List<Instance> getRestrictionClasses(String searchTerm, int topK, Language lang);
}
