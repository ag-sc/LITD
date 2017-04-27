/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import de.citec.sc.query.CandidateRetriever.Language;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ResourceRetriever extends LabelRetriever {

    private String indexPath = "resourceIndex";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory indexDirectoryEN;
    private Directory indexDirectoryDE;
    private Directory indexDirectoryES;

    public ResourceRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;

        initIndexDirectory(loadIntoMemory);
    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String pathEN = directory + "/en/" + this.indexPath + "/";
            String pathDE = directory + "/de/" + this.indexPath + "/";
            String pathES = directory + "/es/" + this.indexPath + "/";
            
            analyzer = new StandardAnalyzer();
            if (loadToMemory) {
                indexDirectoryEN = new RAMDirectory(FSDirectory.open(Paths.get(pathEN)), IOContext.DEFAULT);
                indexDirectoryDE = new RAMDirectory(FSDirectory.open(Paths.get(pathDE)), IOContext.DEFAULT);
                indexDirectoryES = new RAMDirectory(FSDirectory.open(Paths.get(pathES)), IOContext.DEFAULT);
            } else {
                indexDirectoryEN = FSDirectory.open(Paths.get(pathEN));
                indexDirectoryDE = FSDirectory.open(Paths.get(pathDE));
                indexDirectoryES = FSDirectory.open(Paths.get(pathES));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

    public List<Instance> getResources(String searchTerm, int k, Language lang) {
        super.comparator = super.frequencyComparator;
        List<Instance> result = new ArrayList<>();
        switch(lang){
            case EN:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryEN);
                break;
            case DE:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryDE);
                break;
            case ES:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryES);
                break;
        }

        return result;
    }

}
