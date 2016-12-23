/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author sherzod
 */
public class CandidateRetrieverOnMemory implements CandidateRetriever {

    private ConcurrentHashMap<String, List<Instance>> resourceIndex;
    private ConcurrentHashMap<String, List<Instance>> classIndex;
    private ConcurrentHashMap<String, List<Instance>> predicateIndex;
    private ConcurrentHashMap<String, List<Instance>> matollPredicateIndex;
    private ConcurrentHashMap<String, List<Instance>> matollRestrictionClassIndex;

    public CandidateRetrieverOnMemory(String resourceDirectory, String classDirectory, String predicateDirectory, String matollPredicateDirectory, String matollRestrictionClassDirectory) {
        System.out.println("Loading index files ...");

        resourceIndex = new ConcurrentHashMap<>(15000000);
        loadFiles(resourceDirectory, resourceIndex);

        classIndex = new ConcurrentHashMap<>(20000);
        loadFiles(classDirectory, classIndex);

        predicateIndex = new ConcurrentHashMap<>(20000);
        loadFiles(predicateDirectory, predicateIndex);

        matollPredicateIndex = new ConcurrentHashMap<>(50000);
        loadFiles(matollPredicateDirectory, matollPredicateIndex);

        matollRestrictionClassIndex = new ConcurrentHashMap<>(50000);
        loadFiles(matollRestrictionClassDirectory, matollRestrictionClassIndex);
    }

    @Override
    public List<Instance> getAllResources(String searchTerm, int topK) {

        List<Instance> matches = new ArrayList<>();
        
        if(resourceIndex.containsKey(searchTerm)){
            matches = resourceIndex.get(searchTerm);
        }

        List<Instance> result = new ArrayList<>();

        if (!matches.isEmpty()) {
            //sort by frequency
            Collections.sort(matches);

            for (Instance i : matches) {
                i.setUri("http://dbpedia.org/resource/" + i.getUri());
                result.add(i);

                if (result.size() == topK) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public List<Instance> getAllPredicates(String searchTerm, int topK) {
        List<Instance> matches = new ArrayList<>();
        
        if(predicateIndex.containsKey(searchTerm)){
            matches = predicateIndex.get(searchTerm);
        }
        
        if(matollPredicateIndex.containsKey(searchTerm)){
            matches.addAll(matollPredicateIndex.get(searchTerm));
        }


        List<Instance> result = new ArrayList<>();

        if (!matches.isEmpty()) {
            //sort by frequency
            Collections.sort(matches);

            for (Instance i : matches) {
                result.add(i);

                if (result.size() == topK) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public List<Instance> getPredicatesInMatoll(String searchTerm, int topK) {

        List<Instance> matches = new ArrayList<>();
        
        if(matollPredicateIndex.containsKey(searchTerm)){
            matches = matollPredicateIndex.get(searchTerm);
        }

        List<Instance> result = new ArrayList<>();

        if (!matches.isEmpty()) {
            //sort by frequency
            Collections.sort(matches);

            for (Instance i : matches) {
                result.add(i);

                if (result.size() == topK) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public List<Instance> getAllClasses(String searchTerm, int topK, boolean partialMatch) {
        List<Instance> result = new ArrayList<>();
        List<Instance> matches = new ArrayList<>();

        if (!partialMatch) {

            if(classIndex.containsKey(searchTerm)){
                matches = classIndex.get(searchTerm);
            }

        } else {

            //partial match
            for (String label : classIndex.keySet()) {
                if (label.contains(searchTerm) || searchTerm.contains(label)) {
                    matches.addAll(classIndex.get(label));
                }
            }
        }

        if (!matches.isEmpty()) {
            //sort by frequency
            Collections.sort(matches);

            for (Instance i : matches) {
                result.add(i);

                if (result.size() == topK) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public List<Instance> getRestrictionClasses(String searchTerm, int topK) {

        List<Instance> matches = new ArrayList<>();
        
        if(matollRestrictionClassIndex.containsKey(searchTerm)){
            matches = matollRestrictionClassIndex.get(searchTerm);
        }

        List<Instance> result = new ArrayList<>();

        if (!matches.isEmpty()) {
            //sort by frequency
            Collections.sort(matches);

            for (Instance i : matches) {
                result.add(i);

                if (result.size() == topK) {
                    break;
                }
            }
        }

        return result;

    }

    private void loadFiles(String directory, ConcurrentHashMap<String, List<Instance>> map) {

        File indexFolder = new File(directory);
        File[] listOfFiles = indexFolder.listFiles();

        for (int d = 0; d < listOfFiles.length; d++) {
            if (listOfFiles[d].isFile() && !listOfFiles[d].isHidden()) {

                String fileExtension = listOfFiles[d].getName().substring(listOfFiles[d].getName().lastIndexOf(".") + 1);

                if (fileExtension.equals("ttl")) {

                    String filePath = listOfFiles[d].getPath();

                    System.out.println("Loading " + filePath);

                    try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
                        stream.parallel().forEach(item -> {

                            String[] c = item.toString().split("\t");
                            Instance instance = null;
                            String label = "";

                            if (c.length == 3) {

                                label = c[0].toLowerCase();
                                String uri = c[1];
                                int freq = Integer.parseInt(c[2]);

                                instance = new Instance(uri, freq);

                            }
                            if (c.length == 8) {

                                label = c[0].toLowerCase();
                                String preposition = c[1];
                                String pos = c[2];
                                String frame = c[3];
                                String uri = c[4];
                                String subj = c[5];
                                String obj = c[6];
                                int freq = Integer.parseInt(c[7]);

                                instance = new Instance(uri, preposition, pos, frame, subj, obj, freq);

                            }

                            if (instance != null) {
                                if (map.containsKey(label)) {

                                    List<Instance> instances = map.get(label);
                                    List<Instance> newInstances = new ArrayList<>();

                                    if (!instances.contains(instance)) {
                                        newInstances.add(instance);
                                        newInstances.addAll(instances);
                                    } else {
                                        //update frequency by adding the new freq to the prev. one
                                        for (Instance i : instances) {
                                            if (i.equals(instance)) {
                                                //update freq.
                                                instance.setFreq(i.getFreq() + instance.getFreq());

                                                newInstances.add(instance);
                                            } else {
                                                newInstances.add(i);
                                            }
                                        }
                                    }

                                    map.put(label, newInstances);

                                } else {

                                    List<Instance> instances = new ArrayList<>();

                                    instances.add(instance);

                                    map.put(label, instances);
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Map size: " + map.size());
    }

    @Override
    public List<Instance> getPredicatesInDBpedia(String searchTerm, int topK, boolean partialMatch) {

        List<Instance> result = new ArrayList<>();
        List<Instance> matches = new ArrayList<>();

        if (!partialMatch) {

            if(predicateIndex.containsKey(searchTerm)){
                matches = predicateIndex.get(searchTerm);
            }

        } else {

            //partial match
            for (String label : predicateIndex.keySet()) {
                if (label.contains(searchTerm) || searchTerm.contains(label)) {
                    matches.addAll(predicateIndex.get(label));
                }
            }
        }

        
        if (!matches.isEmpty()) {
            //sort by frequency
            Collections.sort(matches);

            for (Instance i : matches) {
                result.add(i);

                if (result.size() == topK) {
                    break;
                }
            }
        }

        return result;
    }

}
