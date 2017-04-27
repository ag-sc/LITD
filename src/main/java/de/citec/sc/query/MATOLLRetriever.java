/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import de.citec.sc.query.CandidateRetriever.Language;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private String indexPath1 = "matollClassIndex";
    private String indexPath2 = "matollPredicateIndex";
    private String directory;
    private StandardAnalyzer analyzer;
    private Directory indexDirectoryClassDirEN;
    private Directory indexDirectoryPredicateDirEN;
    private Directory indexDirectoryClassDirDE;
    private Directory indexDirectoryPredicateDirDE;
    private Directory indexDirectoryClassDirES;
    private Directory indexDirectoryPredicateDirES;

    public MATOLLRetriever(String directory, boolean loadIntoMemory) {
        this.directory = directory;

        initIndexDirectory(loadIntoMemory);
    }

    private void initIndexDirectory(boolean loadToMemory) {
        try {
            String pathClassEN = directory + "/en/" + this.indexPath1 + "/";
            String pathPredicateEN = directory + "/en/" + this.indexPath2 + "/";
            
            String pathClassDE = directory + "/de/" + this.indexPath1 + "/";
            String pathPredicateDE = directory + "/de/" + this.indexPath2 + "/";
            
            String pathClassES = directory + "/es/" + this.indexPath1 + "/";
            String pathPredicateES = directory + "/es/" + this.indexPath2 + "/";
            
            analyzer = new StandardAnalyzer();
            
            if (loadToMemory) {
                indexDirectoryClassDirEN = new RAMDirectory(FSDirectory.open(Paths.get(pathClassEN)), IOContext.DEFAULT);
                indexDirectoryPredicateDirEN = new RAMDirectory(FSDirectory.open(Paths.get(pathPredicateEN)), IOContext.DEFAULT);
                
                indexDirectoryClassDirDE = new RAMDirectory(FSDirectory.open(Paths.get(pathClassDE)), IOContext.DEFAULT);
                indexDirectoryPredicateDirDE = new RAMDirectory(FSDirectory.open(Paths.get(pathPredicateDE)), IOContext.DEFAULT);
                
                indexDirectoryClassDirES = new RAMDirectory(FSDirectory.open(Paths.get(pathClassES)), IOContext.DEFAULT);
                indexDirectoryPredicateDirES = new RAMDirectory(FSDirectory.open(Paths.get(pathPredicateES)), IOContext.DEFAULT);
            } else {
                indexDirectoryClassDirEN = FSDirectory.open(Paths.get(pathClassEN));
                indexDirectoryPredicateDirEN = FSDirectory.open(Paths.get(pathPredicateEN));
                
                indexDirectoryClassDirDE = FSDirectory.open(Paths.get(pathClassDE));
                indexDirectoryPredicateDirDE = FSDirectory.open(Paths.get(pathPredicateDE));
                
                indexDirectoryClassDirES = FSDirectory.open(Paths.get(pathClassES));
                indexDirectoryPredicateDirES = FSDirectory.open(Paths.get(pathPredicateES));
            }

        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

    public List<Instance> getRestrictionClasses(String searchTerm, int k, Language lang) {
        super.comparator = super.frequencyComparator;

        List<Instance> result = new ArrayList<>();
        switch(lang){
            case EN:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryClassDirEN);
                break;
            case DE:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryClassDirDE);
                break;
            case ES:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryClassDirES);
                break;
        }

        return result;
    }
    
    public List<Instance> getPredicates(String searchTerm, int k, Language lang) {
        super.comparator = super.frequencyComparator;
        List<Instance> result = new ArrayList<>();
        switch(lang){
            case EN:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryPredicateDirEN);
                break;
            case DE:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryPredicateDirDE);
                break;
            case ES:
                result = getDirectMatches(searchTerm, "label", "URI", k, indexDirectoryPredicateDirES);
                break;
        }

        return result;
    }

}
