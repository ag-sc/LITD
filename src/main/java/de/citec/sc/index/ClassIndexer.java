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
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class ClassIndexer implements Indexer {

    private Path predicatesIndexPath;

    private IndexWriter predicatesIndexWriter;

    private Document predicatesDoc;

    public void addClass(String label, String uri, int freq) throws IOException {
        predicatesDoc = new Document();

        Field labelField = new StringField("label", label, Field.Store.YES);
        predicatesDoc.add(labelField);
        
        Field labelField2 = new TextField("labelTokenized", label, Field.Store.YES);
        predicatesDoc.add(labelField2);

        Field uriField = new StringField("URI", uri, Field.Store.YES);
        predicatesDoc.add(uriField);

        Field freqField = new LegacyIntField("freq", freq, Field.Store.YES);
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

            predicatesIndexPath = Paths.get(folderPath, "classIndex");
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
            Logger.getLogger(ClassIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClassIndexer(String filePath) {
        initIndex(filePath);
    }
    
    
}
