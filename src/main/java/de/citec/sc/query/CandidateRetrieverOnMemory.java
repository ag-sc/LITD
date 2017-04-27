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

    private ConcurrentHashMap<String, List<Instance>> resourceIndexEN;
    private ConcurrentHashMap<String, List<Instance>> classIndexEN;
    private ConcurrentHashMap<String, List<Instance>> predicateIndexEN;
    private ConcurrentHashMap<String, List<Instance>> matollPredicateIndexEN;
    private ConcurrentHashMap<String, List<Instance>> matollRestrictionClassIndexEN;

    private ConcurrentHashMap<String, List<Instance>> resourceIndexDE;
    private ConcurrentHashMap<String, List<Instance>> classIndexDE;
    private ConcurrentHashMap<String, List<Instance>> predicateIndexDE;
    private ConcurrentHashMap<String, List<Instance>> matollPredicateIndexDE;
    private ConcurrentHashMap<String, List<Instance>> matollRestrictionClassIndexDE;

    private ConcurrentHashMap<String, List<Instance>> resourceIndexES;
    private ConcurrentHashMap<String, List<Instance>> classIndexES;
    private ConcurrentHashMap<String, List<Instance>> predicateIndexES;
    private ConcurrentHashMap<String, List<Instance>> matollPredicateIndexES;
    private ConcurrentHashMap<String, List<Instance>> matollRestrictionClassIndexES;

    public CandidateRetrieverOnMemory(String directory) {
        System.out.println("Loading index files ...");
        
        //English files
        resourceIndexEN = new ConcurrentHashMap<>(5000000);
        loadFiles(directory + "/en/resourceFiles", resourceIndexEN, Language.EN);
        classIndexEN = new ConcurrentHashMap<>(1000);
        loadFiles(directory + "/en/classFiles", classIndexEN, Language.EN);
        predicateIndexEN = new ConcurrentHashMap<>(5000);
        loadFiles(directory + "/en/predicateFiles", predicateIndexEN, Language.EN);
        matollPredicateIndexEN = new ConcurrentHashMap<>(10000);
        loadFiles(directory + "/en/matollFiles", matollPredicateIndexEN, Language.EN);
        matollRestrictionClassIndexEN = new ConcurrentHashMap<>(1000);
        loadFiles(directory + "/en/matollAdjectiveFiles", matollRestrictionClassIndexEN, Language.EN);
        
        //German files
        resourceIndexDE = new ConcurrentHashMap<>(5000000);
        loadFiles(directory + "/de/resourceFiles", resourceIndexDE, Language.DE);
        classIndexDE = new ConcurrentHashMap<>(1000);
        loadFiles(directory + "/de/classFiles", classIndexDE, Language.DE);
        predicateIndexDE = new ConcurrentHashMap<>(5000);
        loadFiles(directory + "/de/predicateFiles", predicateIndexDE, Language.DE);
        matollPredicateIndexDE = new ConcurrentHashMap<>(10000);
        loadFiles(directory + "/de/matollFiles", matollPredicateIndexDE, Language.DE);
        matollRestrictionClassIndexDE = new ConcurrentHashMap<>(1000);
        
        //English files
        resourceIndexES = new ConcurrentHashMap<>(5000000);
        loadFiles(directory + "/es/resourceFiles", resourceIndexES, Language.ES);
        classIndexES = new ConcurrentHashMap<>(1000);
        loadFiles(directory + "/es/classFiles", classIndexES, Language.ES);
        predicateIndexES = new ConcurrentHashMap<>(5000);
        loadFiles(directory + "/es/predicateFiles", predicateIndexES, Language.ES);
        matollPredicateIndexES = new ConcurrentHashMap<>(10000);
        loadFiles(directory + "/es/matollFiles", matollPredicateIndexES, Language.ES);
        matollRestrictionClassIndexES = new ConcurrentHashMap<>(1000);
        
        
    }

    @Override
    public List<Instance> getAllResources(String searchTerm, int topK, Language lang) {

        List<Instance> matches = new ArrayList<>();

        switch (lang) {
            case EN:
                if (resourceIndexEN.containsKey(searchTerm)) {
                    matches = resourceIndexEN.get(searchTerm);
                }
                break;
            case DE:
                if (resourceIndexDE.containsKey(searchTerm)) {
                    matches = resourceIndexDE.get(searchTerm);
                }
                break;
            case ES:
                if (resourceIndexES.containsKey(searchTerm)) {
                    matches = resourceIndexES.get(searchTerm);
                }
                break;
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
    public List<Instance> getAllPredicates(String searchTerm, int topK, Language lang) {
        List<Instance> matches = new ArrayList<>();

        switch (lang) {
            case EN:
                if (predicateIndexEN.containsKey(searchTerm)) {
                    matches = predicateIndexEN.get(searchTerm);
                }

                if (matollPredicateIndexEN.containsKey(searchTerm)) {
                    matches.addAll(matollPredicateIndexEN.get(searchTerm));
                }
                break;
            case DE:
                if (predicateIndexDE.containsKey(searchTerm)) {
                    matches = predicateIndexDE.get(searchTerm);
                }

                if (matollPredicateIndexDE.containsKey(searchTerm)) {
                    matches.addAll(matollPredicateIndexDE.get(searchTerm));
                }
                break;
            case ES:
                if (predicateIndexES.containsKey(searchTerm)) {
                    matches = predicateIndexES.get(searchTerm);
                }

                if (matollPredicateIndexES.containsKey(searchTerm)) {
                    matches.addAll(matollPredicateIndexES.get(searchTerm));
                }
                break;
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
    public List<Instance> getPredicatesInMatoll(String searchTerm, int topK, Language lang) {

        List<Instance> matches = new ArrayList<>();

        switch (lang) {
            case EN:
                if (matollPredicateIndexEN.containsKey(searchTerm)) {
                    matches = matollPredicateIndexEN.get(searchTerm);
                }
                break;
            case DE:
                if (matollPredicateIndexDE.containsKey(searchTerm)) {
                    matches = matollPredicateIndexDE.get(searchTerm);
                }
                break;
            case ES:
                if (matollPredicateIndexES.containsKey(searchTerm)) {
                    matches = matollPredicateIndexES.get(searchTerm);
                }
                break;
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
    public List<Instance> getAllClasses(String searchTerm, int topK, boolean partialMatch, Language lang) {
        List<Instance> result = new ArrayList<>();
        List<Instance> matches = new ArrayList<>();

        if (!partialMatch) {

            switch (lang) {
                case EN:
                    if (classIndexEN.containsKey(searchTerm)) {
                        matches = classIndexEN.get(searchTerm);
                    }
                    break;
                case DE:
                    if (classIndexDE.containsKey(searchTerm)) {
                        matches = classIndexDE.get(searchTerm);
                    }
                    break;
                case ES:
                    if (classIndexES.containsKey(searchTerm)) {
                        matches = classIndexES.get(searchTerm);
                    }
                    break;
            }

        } else {

            //partial match
            switch (lang) {
                case EN:
                    for (String label : classIndexEN.keySet()) {
                        if (label.contains(searchTerm) || searchTerm.contains(label)) {
                            matches.addAll(classIndexEN.get(label));
                        }
                    }
                    break;
                case DE:
                    for (String label : classIndexDE.keySet()) {
                        if (label.contains(searchTerm) || searchTerm.contains(label)) {
                            matches.addAll(classIndexDE.get(label));
                        }
                    }
                    break;
                case ES:
                    for (String label : classIndexES.keySet()) {
                        if (label.contains(searchTerm) || searchTerm.contains(label)) {
                            matches.addAll(classIndexES.get(label));
                        }
                    }
                    break;
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
    public List<Instance> getRestrictionClasses(String searchTerm, int topK, Language lang) {

        List<Instance> matches = new ArrayList<>();

        switch (lang) {
            case EN:
                if (matollRestrictionClassIndexEN.containsKey(searchTerm)) {
                    matches = matollRestrictionClassIndexEN.get(searchTerm);
                }
                break;
            case DE:
                if (matollRestrictionClassIndexDE.containsKey(searchTerm)) {
                    matches = matollRestrictionClassIndexDE.get(searchTerm);
                }
                break;
            case ES:
                if (matollRestrictionClassIndexES.containsKey(searchTerm)) {
                    matches = matollRestrictionClassIndexES.get(searchTerm);
                }
                break;
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

    private void loadFiles(String directory, ConcurrentHashMap<String, List<Instance>> map, Language lang) {

        File indexFolder = new File(directory);
        File[] listOfFiles = indexFolder.listFiles();

        for (int d = 0; d < listOfFiles.length; d++) {
            if (listOfFiles[d].isFile() && !listOfFiles[d].isHidden()) {

                String fileExtension = listOfFiles[d].getName().substring(listOfFiles[d].getName().lastIndexOf(".") + 1);

                if (fileExtension.equals("ttl") || fileExtension.equals("txt")) {

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
                            if (c.length == 4) {

                                label = c[0].toLowerCase();
                                String preposition = c[1];
                                String pos = "";
                                String frame = "";
                                String uri = c[2];
                                String subj = "";
                                String obj = "";
                                int freq = Integer.parseInt(c[3]);
                                
                                instance = new Instance(uri, preposition, pos, frame, subj, obj, freq);

                            }
                            if (c.length == 2) {

                                label = c[1].toLowerCase();
                                
                                label = label.replace("@"+lang.name().toLowerCase(), "");
                                
                                String uri = c[0];
                                int freq = 1;

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
    public List<Instance> getPredicatesInDBpedia(String searchTerm, int topK, boolean partialMatch, Language lang) {

        List<Instance> result = new ArrayList<>();
        List<Instance> matches = new ArrayList<>();

        if (!partialMatch) {

            switch (lang) {
                case EN:
                    if (predicateIndexEN.containsKey(searchTerm)) {
                        matches = predicateIndexEN.get(searchTerm);
                    }
                    break;
                case DE:
                    if (predicateIndexDE.containsKey(searchTerm)) {
                        matches = predicateIndexDE.get(searchTerm);
                    }
                    break;
                case ES:
                    if (predicateIndexES.containsKey(searchTerm)) {
                        matches = predicateIndexES.get(searchTerm);
                    }
                    break;
            }

        } else {

            //partial match
            switch (lang) {
                case EN:
                    for (String label : predicateIndexEN.keySet()) {
                        if (label.contains(searchTerm) || searchTerm.contains(label)) {
                            matches.addAll(predicateIndexEN.get(label));
                        }
                    }
                    break;
                case DE:
                    for (String label : predicateIndexDE.keySet()) {
                        if (label.contains(searchTerm) || searchTerm.contains(label)) {
                            matches.addAll(predicateIndexDE.get(label));
                        }
                    }
                    break;
                case ES:
                    for (String label : predicateIndexES.keySet()) {
                        if (label.contains(searchTerm) || searchTerm.contains(label)) {
                            matches.addAll(predicateIndexES.get(label));
                        }
                    }
                    break;
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
