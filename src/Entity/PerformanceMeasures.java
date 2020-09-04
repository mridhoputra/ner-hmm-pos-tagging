/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.HashMap;

/**
 *
 * @author Windows 10
 */
public class PerformanceMeasures {

    HashMap<String, Integer> labelCount;
    HashMap<String, Integer> labelTrueCount;
    HashMap<String, Double> performanceMeasure;

    public PerformanceMeasures() {
        labelCount = new HashMap<>();
        labelTrueCount = new HashMap<>();
        performanceMeasure = new HashMap<>();
    }

    public HashMap<String, Integer> getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(String label, Integer totalLabel) {
        labelCount.put(label, totalLabel);
    }

    public HashMap<String, Integer> getLabelTrueCount() {
        return labelTrueCount;
    }

    public void setLabelTrueCount(String label, Integer totalTrueCount) {
        labelTrueCount.put(label, totalTrueCount);
    }

    public HashMap<String, Double> getPerformanceMeasure() {
        return performanceMeasure;
    }

    public void setPerformanceMeasure(String performanceAndNERLabel, Double value) {
        performanceMeasure.put(performanceAndNERLabel, value);
    }

}
