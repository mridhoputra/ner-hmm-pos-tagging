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
public class POSTagswithLabel implements Comparable<POSTagswithLabel> {

    String postag;
    String previous_postag;
    String label;

    public POSTagswithLabel(String postag, String previous_postag, String label) {
        this.postag = postag;
        this.previous_postag = previous_postag;
        this.label = label;
    }

    public String getPostag() {
        return postag;
    }

    public String getPrevious_postag() {
        return previous_postag;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.postag);
        hash = 79 * hash + Objects.hashCode(this.previous_postag);
        hash = 79 * hash + Objects.hashCode(this.label);
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
        final POSTagswithLabel other = (POSTagswithLabel) obj;
        if (!Objects.equals(this.postag, other.postag)) {
            return false;
        }
        if (!Objects.equals(this.previous_postag, other.previous_postag)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "POSTagswithLabel {" + "postag=" + postag + ", previous_postag=" + previous_postag + ", label=" + label + '}';
    }

    @Override
    public int compareTo(POSTagswithLabel o) {
        if (this.getPostag().equals(o.getPostag())) {
            if (this.getPrevious_postag().equals(o.getPrevious_postag())) {
                return this.getLabel().compareTo(o.getLabel());
            } else {
                return this.getPrevious_postag().compareTo(o.getPrevious_postag());
            }
        } else {
            return this.getPostag().compareTo(o.getPostag());
        }
    }

}
