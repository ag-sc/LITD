/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.litd;

import de.citec.sc.index.*;

/**
 *
 * @author sherzod
 */
public class CreateIndex {

    public static void main(String[] args) {
        AnchorTextLoader a1 = new AnchorTextLoader();
        a1.load(true, "luceneIndex/resourceIndex", "rawFiles/resourceFiles/");

        PredicateLoader p1 = new PredicateLoader();
        p1.load(true, "luceneIndex/predicateIndex", "rawFiles/predicateFiles/");

        de.citec.sc.index.ClassLoader c1 = new de.citec.sc.index.ClassLoader();
        c1.load(true, "luceneIndex/classIndex", "rawFiles/classFiles/");

        MATOLLTextLoader m1 = new MATOLLTextLoader();
        m1.load(true, "luceneIndex/matollIndex", "rawFiles/matollFiles/");
    }

}
