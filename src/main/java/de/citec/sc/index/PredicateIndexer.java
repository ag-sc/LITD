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
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class PredicateIndexer implements Indexer {

    private Path predicatesIndexPath;

    private IndexWriter predicatesIndexWriter;

    private Document predicatesDoc;

    public void addPredicate(String label, String uri, int freq) throws IOException {
        predicatesDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        predicatesDoc.add(labelField);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        predicatesDoc.add(uriField);
        
        Field labelTextField = new TextField("labelTokenized", uri, Field.Store.YES);
        predicatesDoc.add(labelTextField);

        Field freqField = new IntField("freq", freq, Field.Store.YES);
        predicatesDoc.add(freqField);

        predicatesIndexWriter.addDocument(predicatesDoc);
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

            predicatesIndexPath = Paths.get(folderPath, "predicateIndex");
            predicatesIndexWriter = initIndexWriter(predicatesIndexPath);
            predicatesDoc = new Document();
        } catch (Exception e) {

        }
        
    }

    @Override
    public void finilize() {
        try {
            predicatesIndexWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(PredicateIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PredicateIndexer(String filePath) {
        initIndex(filePath);
    }
    
    
}
