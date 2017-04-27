/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.util.Objects;

/**
 *
 * @author sherzod
 */
public class Instance implements Comparable<Instance> {

    private String uri;//born	in	adjective	AdjectivePredicateFrame	http://dbpedia.org/property/placeOfBirth	copulativeArg	prepositionalObject	6155
    private String preposition;
    private String pos;
    private String frame;
    private String subj;
    private String obj;
    private int freq;

    public Instance(String uri, String preposition, String pos, String frame, String subj, String obj, int freq) {
        this.uri = uri;
        this.preposition = preposition;
        this.pos = pos;
        this.frame = frame;
        this.subj = subj;
        this.obj = obj;
        this.freq = freq;
    }

    public Instance(String uri, int freq) {
        this.uri = uri;
        this.freq = freq;
        
        this.frame = "";
        this.obj = "";
        this.subj = "";
        this.pos = "";
        this.preposition = "";
    }
    



    public String getPreposition() {
        return preposition;
    }

    public void setPreposition(String preposition) {
        this.preposition = preposition;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getSubj() {
        return subj;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

   

    

    

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.uri);
        hash = 67 * hash + Objects.hashCode(this.preposition);
        hash = 67 * hash + Objects.hashCode(this.pos);
        hash = 67 * hash + Objects.hashCode(this.frame);
        hash = 67 * hash + Objects.hashCode(this.subj);
        hash = 67 * hash + Objects.hashCode(this.obj);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instance other = (Instance) obj;
        if (!Objects.equals(this.uri, other.uri)) {
            return false;
        }
        if (!Objects.equals(this.preposition, other.preposition)) {
            return false;
        }
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        if (!Objects.equals(this.frame, other.frame)) {
            return false;
        }
        if (!Objects.equals(this.subj, other.subj)) {
            return false;
        }
        if (!Objects.equals(this.obj, other.obj)) {
            return false;
        }
        return true;
    }

    public Instance clone(){
        return new Instance(uri, preposition, pos, frame, subj, obj, freq);
    }
    



    @Override
    public int compareTo(Instance o) {
        if (freq > o.freq) {
            return -1;
        } else if (freq < o.freq) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        String s = uri + " " + freq;
        
        if(!(preposition == null || preposition.equals(""))){
            s += " prep : " + preposition;
        }
        return s;
    }
}
