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
public class ActualAndPredicted {

    private String actualLabel;
    private String predictedLabel;

    public ActualAndPredicted(String actualLabel, String predictedLabel) {
        this.actualLabel = actualLabel;
        this.predictedLabel = predictedLabel;
    }

    public String getActualLabel() {
        return actualLabel;
    }

    public String getPredictedLabel() {
        return predictedLabel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.actualLabel);
        hash = 79 * hash + Objects.hashCode(this.predictedLabel);
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
        final ActualAndPredicted other = (ActualAndPredicted) obj;
        if (!Objects.equals(this.actualLabel, other.actualLabel)) {
            return false;
        }
        if (!Objects.equals(this.predictedLabel, other.predictedLabel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActualAndPredicted [" + actualLabel + "][" + predictedLabel + "]";
    }

}
