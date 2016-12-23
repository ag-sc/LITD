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
public class MATOLLRetriever extends LabelRetriever {

    private String indexPath1 = "classesindex";
    private String indexPath2 = "predicatesindex";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory indexDirectory1;
    private Directory indexDirectory2;

    public MATOLLRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;

        initIndexDirectory(loadIntoMemory);
    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String path1 = directory + "/" + this.indexPath1 + "/";
            String path2 = directory + "/" + this.indexPath2 + "/";
            analyzer = new StandardAnalyzer();
            
            if (loadToMemory) {
                indexDirectory1 = new RAMDirectory(FSDirectory.open(Paths.get(path1)), IOContext.DEFAULT);
                indexDirectory2 = new RAMDirectory(FSDirectory.open(Paths.get(path2)), IOContext.DEFAULT);
            } else {
                indexDirectory1 = FSDirectory.open(Paths.get(path1));
                indexDirectory2 = FSDirectory.open(Paths.get(path2));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

    public List<Instance> getRestrictionClasses(String searchTerm, int k) {
        super.comparator = super.frequencyComparator;

        List<Instance> result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectory1);

        return result;
    }
    
    public List<Instance> getPredicates(String searchTerm, int k) {
        super.comparator = super.frequencyComparator;

        List<Instance> result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectory2);

        return result;
    }

}
