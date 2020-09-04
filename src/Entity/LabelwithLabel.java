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
public class LabelwithLabel implements Comparable<LabelwithLabel> {

    private String label;
    private String previousLabel;

    public LabelwithLabel(String label, String previousLabel) {
        this.label = label;
        this.previousLabel = previousLabel;
    }

    public String getLabel() {
        return label;
    }

    public String getPreviousLabel() {
        return previousLabel;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.label);
        hash = 19 * hash + Objects.hashCode(this.previousLabel);
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
        final LabelwithLabel other = (LabelwithLabel) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.previousLabel, other.previousLabel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LabelwithLabel {" + "label=" + label + ", previousLabel=" + previousLabel + '}';
    }

    @Override
    public int compareTo(LabelwithLabel o) {
        if (this.getLabel().equals(o.getLabel())) {
            return this.getPreviousLabel().compareTo(o.getPreviousLabel());
        } else {
            return this.getLabel().compareTo(o.getLabel());
        }
    }

}
