/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.citec.sc.litd;

import de.citec.sc.query.AnchorRetriever;
import de.citec.sc.query.CandidateRetrieverOnLucene;
import de.citec.sc.query.CandidateRetrieverOnMemory;
import de.citec.sc.query.ClassRetriever;
import de.citec.sc.query.MATOLLRetriever;
import de.citec.sc.query.PredicateRetriever;

/**
 *
 * @author sherzod
 */
public class TestQuery {
    
    public static void main(String[] args) {
        
        CandidateRetrieverOnLucene lucene = new CandidateRetrieverOnLucene(false, "luceneIndex/resourceIndex","luceneIndex/classIndex", "luceneIndex/predicateIndex", "luceneIndex/matollIndex");
        System.out.println(lucene.getAllPredicates("cross", 10));
        System.out.println(lucene.getPredicatesInDBpedia("cross~0.8", 10, true));
        System.out.println(lucene.getAllClasses("actor", 10, true));
        System.out.println(lucene.getAllClasses("player", 10, true));
        System.out.println(lucene.getAllResources("jordan", 10));
        System.out.println(lucene.getPredicatesInDBpedia("author", 10, false));
        System.out.println(lucene.getPredicatesInMatoll("married", 10));
        System.out.println(lucene.getRestrictionClasses("catholic", 10));
        
        
//        CandidateRetrieverOnMemory memory = new CandidateRetrieverOnMemory("resourceFiles", "classFiles", "predicateFiles", "matollFiles");
//        System.out.println(memory.getAllClasses("river", 10, true));
//        System.out.println(memory.getPredicatesInDBpedia("author", 10));
//        System.out.println(memory.getPredicatesInMatoll("married", 10));
//        System.out.println(memory.getRestrictionClasses("catholic", 10));
//        System.out.println(memory.getAllPredicates("author", 10));
//        System.out.println(memory.getAllResources("Jordan", 10));
//        
        
        
        int mb = 1024 * 1024;
        
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        // Print used memory
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        // Print free memory
        System.out.println("Free Memory:" + runtime.freeMemory() / mb);

        // Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);
    }
}
