package de.citec.sc.index;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LegacyIntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MATOLLIndexer implements Indexer {
    
    private Path classesIndexPath;

    private IndexWriter classesIndexWriter;

    private Document classesDoc;
    
    

    private Path predicatesIndexPath;

    private IndexWriter predicatesIndexWriter;

    private Document predicatesDoc;

    public void addPredicate(String label, String uri, String POS, String preposition, String frame, String subject, String object, int freq) throws IOException {
        predicatesDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        predicatesDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        predicatesDoc.add(uriField);
        
        Field posField = new StringField("POS", POS, Field.Store.YES);
        predicatesDoc.add(posField);
        
        Field prepField = new StringField("preposition", preposition, Field.Store.YES);
        predicatesDoc.add(prepField);
        
        Field frameField = new StringField("frame", frame, Field.Store.YES);
        predicatesDoc.add(frameField);
        
        Field subjectField = new StringField("Subject", subject, Field.Store.YES);
        predicatesDoc.add(subjectField);
        
        Field objectField = new StringField("Object", object, Field.Store.YES);
        predicatesDoc.add(objectField);

        Field freqField = new LegacyIntField("freq", freq, Field.Store.YES);
        predicatesDoc.add(freqField);

        predicatesIndexWriter.addDocument(predicatesDoc);
    }
    
    public void addClass(String label, String uri, int freq) throws IOException {
        classesDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        classesDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        classesDoc.add(uriField);

        Field freqField = new LegacyIntField("freq", freq, Field.Store.YES);
        classesDoc.add(freqField);

        classesIndexWriter.addDocument(classesDoc);
    }

    private IndexWriter initIndexWriter(Path path) throws IOException {
        Directory dir = FSDirectory.open(path);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

    @Override
    public void initIndex(String folderPath) {
        try {
            if (folderPath == null) {
                throw new RuntimeException("The indexes directory path must be specified");
            }

            predicatesIndexPath = Paths.get(folderPath, "matollPredicateIndex");
            predicatesIndexWriter = initIndexWriter(predicatesIndexPath);
            predicatesDoc = new Document();
            
            
            classesIndexPath = Paths.get(folderPath, "matollClassIndex");
            classesIndexWriter = initIndexWriter(classesIndexPath);
            classesDoc = new Document();
            
        } catch (Exception e) {

        }
        
    }

    @Override
    public void finilize() {
        try {
            predicatesIndexWriter.close();
            classesIndexWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MATOLLIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MATOLLIndexer(String filePath) {
        initIndex(filePath);
    }
    
    
}
