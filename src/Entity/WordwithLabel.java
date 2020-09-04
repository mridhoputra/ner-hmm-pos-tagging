/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.Objects;

/**
 *
 * @author Windows 10
 */
public class WordwithLabel implements Comparable<WordwithLabel> {

    private String word;
    private String label;

    public WordwithLabel(String word, String label) {
        this.word = word;
        this.label = label;
    }

    public String getWord() {
        return word;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "WordwithLabel {" + "word=" + word + ", label=" + label + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.word);
        hash = 11 * hash + Objects.hashCode(this.label);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WordwithLabel other = (WordwithLabel) obj;
        if (!Objects.equals(this.word, other.word)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(WordwithLabel o) {
        if (this.getWord().equals(o.getWord())) {
            return this.getLabel().compareTo(o.getLabel());
        } else {
            return this.getWord().compareTo(o.getWord());
        }
    }

}
