/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class CandidateRetrieverOnLucene implements CandidateRetriever {

    private ResourceRetriever resourceRetriever;
    private ClassRetriever classRetriever;
    private PredicateRetriever predicateRetriever;
    private MATOLLRetriever matollRetriever;

    private boolean loadToMemory;

    public CandidateRetrieverOnLucene(boolean loadToMemory, String path) {
        this.loadToMemory = loadToMemory;

        this.resourceRetriever = new ResourceRetriever(path, loadToMemory);
        this.classRetriever = new ClassRetriever(path, loadToMemory);
        this.matollRetriever = new MATOLLRetriever(path, loadToMemory);
        this.predicateRetriever = new PredicateRetriever(path, loadToMemory);
    }

    @Override
    public List<Instance> getAllResources(String searchTerm, int topK, Language lang) {
        List<Instance> result = resourceRetriever.getResources(searchTerm, topK, lang);
        return result;
    }

    @Override
    public List<Instance> getAllPredicates(String searchTerm, int topK, Language lang) {
        List<Instance> result = new ArrayList<Instance>();
        result = this.predicateRetriever.getPredicates(searchTerm, topK, false, lang);

        if (result.size() < topK) {
            List<Instance> resultMatoll = this.matollRetriever.getPredicates(searchTerm, topK - result.size(), lang);
            for(Instance i : resultMatoll){
                if(!result.contains(i)){
                    result.add(i);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<Instance> getPredicatesInMatoll(String searchTerm, int topK, Language lang) {
        return this.matollRetriever.getPredicates(searchTerm, topK, lang);
    }

    @Override
    public List<Instance> getAllClasses(String searchTerm, int topK, boolean partialMatch, Language lang) {
        return this.classRetriever.getClasses(searchTerm, topK, partialMatch, lang);
    }

    @Override
    public List<Instance> getPredicatesInDBpedia(String searchTerm, int topK, boolean partialMatch, Language lang) {
        return this.predicateRetriever.getPredicates(searchTerm, topK, partialMatch, lang);
    }

    @Override
    public List<Instance> getRestrictionClasses(String searchTerm, int topK, Language lang) {
        return this.matollRetriever.getRestrictionClasses(searchTerm, topK, lang);
    }

}
