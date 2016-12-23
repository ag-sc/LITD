/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author sherzod
 */
public class PredicateRetriever extends LabelRetriever {

    private String indexPath = "predicatesindex";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory indexDirectory;

    public PredicateRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;

        initIndexDirectory(loadIntoMemory);
    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String path = directory + "/" + this.indexPath + "/";
            analyzer = new StandardAnalyzer();
            if (loadToMemory) {
                indexDirectory = new RAMDirectory(FSDirectory.open(Paths.get(path)), IOContext.DEFAULT);
            } else {
                indexDirectory = FSDirectory.open(Paths.get(path));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

    public List<Instance> getPredicates(String searchTerm, int k, boolean partialMatches) {
        super.comparator = super.frequencyComparator;

        List<Instance> result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectory);
        
        if (partialMatches && result.size() < k) {
            
            List<Instance> resultPartial = getPartialMatches(searchTerm, "labelTokenized", "URI", k - result.size(), indexDirectory, analyzer);
            for(Instance i : resultPartial){
                if(!result.contains(i)){
                    result.add(i);
                }
            }
        }

        return result;
    }

}
