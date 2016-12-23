package de.citec.sc.index;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;

public class MATOLLTextLoader implements Loader {

    @Override
    public void load(boolean deleteIndexFiles, String indexDirectory, String anchorFilesDirectory) {

        //delete old indice files
        try {
            File indexFolder = new File(indexDirectory);
            if (!indexFolder.exists()) {
                indexFolder.mkdir();
                System.out.println(indexDirectory + " directory is created!");
            }
            //delete old indice files        
            if (deleteIndexFiles) {
                File[] listOfIndexFiles = indexFolder.listFiles();

                for (int i = 0; i < listOfIndexFiles.length; i++) {
                    if (listOfIndexFiles[i].isDirectory()) {
                        deleteFolder(listOfIndexFiles[i]);
                    }
                }
            }

            //load files
            File folder = new File(anchorFilesDirectory);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !listOfFiles[i].isHidden()) {
                    String fileExtension = listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf(".") + 1);
                    if (fileExtension.equals("ttl")) {

                        try {
                            MATOLLIndexer indexer = new MATOLLIndexer(indexDirectory);

                            long startTime = System.currentTimeMillis();

                            System.out.println(anchorFilesDirectory + listOfFiles[i].getName());
                            indexData(anchorFilesDirectory + listOfFiles[i].getName(), indexer);

                            indexer.finilize();

                            long endTime = System.currentTimeMillis();
                            System.out.println((endTime - startTime) / 1000 + " sec.");

                        } catch (Exception e) {
                            System.err.println("Problem loading : " + listOfFiles[i].getName());
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // reads chunks of data from filePath
    @Override
    public void indexData(String filePath, Indexer indexer) {
        try {

            MATOLLIndexer anchorIndexer = (MATOLLIndexer) indexer;

            BufferedReader wpgk = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            String line = "";

            while ((line = wpgk.readLine()) != null) {

                String[] data = line.split("\t");

                if (data.length == 8) {

                    try {

                        //born	in	adjective	AdjectivePredicateFrame	http://dbpedia.org/property/placeOfBirth	copulativeArg	prepositionalObject	6155
                        String label = data[0];
                        String prep = data[1];
                        String pos = data[2];
                        String frame = data[3];
                        String uri = data[4];
                        String subj = data[5];
                        String obj = data[6];

                        int freq = Integer.parseInt(data[7]);

                        if (freq <= 1) {
                            continue;
                        }

                        if (!uri.contains("Category:") && !uri.contains("(disambiguation)") && !uri.contains("File:")) {
                            label = label.toLowerCase();

                            anchorIndexer.addPredicate(label, uri, pos, prep, frame, subj, obj, freq);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (data.length == 3) {
                    String label = data[0];

                    String uri = data[1];

                    int freq = Integer.parseInt(data[2]);
                    
                    label = label.toLowerCase();
                    
                    if (uri.contains("###")) {
                        anchorIndexer.addClass(label, uri, freq);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
