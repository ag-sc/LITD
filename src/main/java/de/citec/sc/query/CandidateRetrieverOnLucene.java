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

    private AnchorRetriever resourceRetriever;
    private ClassRetriever classRetriever;
    private PredicateRetriever predicateRetriever;
    private MATOLLRetriever matollRetriever;

    private boolean loadToMemory;

    public CandidateRetrieverOnLucene(boolean loadToMemory, String resourceIndexPath, String classIndexPath, String predicateIndexPath, String matollIndexPath) {
        this.loadToMemory = loadToMemory;

        this.resourceRetriever = new AnchorRetriever(resourceIndexPath, loadToMemory);
        this.classRetriever = new ClassRetriever(classIndexPath, loadToMemory);
        this.matollRetriever = new MATOLLRetriever(matollIndexPath, loadToMemory);
        this.predicateRetriever = new PredicateRetriever(predicateIndexPath, loadToMemory);
    }

    @Override
    public List<Instance> getAllResources(String searchTerm, int topK) {
        List<Instance> result = resourceRetriever.getResources(searchTerm, topK);
        return result;
    }

    @Override
    public List<Instance> getAllPredicates(String searchTerm, int topK) {
        List<Instance> result = new ArrayList<Instance>();
        result = this.predicateRetriever.getPredicates(searchTerm, topK, false);

        if (result.size() < topK) {
            List<Instance> resultMatoll = this.matollRetriever.getPredicates(searchTerm, topK - result.size());
            for(Instance i : resultMatoll){
                if(!result.contains(i)){
                    result.add(i);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<Instance> getPredicatesInMatoll(String searchTerm, int topK) {
        return this.matollRetriever.getPredicates(searchTerm, topK);
    }

    @Override
    public List<Instance> getAllClasses(String searchTerm, int topK, boolean partialMatch) {
        return this.classRetriever.getClasses(searchTerm, topK, partialMatch);
    }

    @Override
    public List<Instance> getPredicatesInDBpedia(String searchTerm, int topK, boolean partialMatch) {
        return this.predicateRetriever.getPredicates(searchTerm, topK, partialMatch);
    }

    @Override
    public List<Instance> getRestrictionClasses(String searchTerm, int topK) {
        return this.matollRetriever.getRestrictionClasses(searchTerm, topK);
    }

}
