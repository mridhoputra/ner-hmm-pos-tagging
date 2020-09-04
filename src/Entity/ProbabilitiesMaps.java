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
public class ProbabilitiesMaps {

    private static HashMap<LabelwithLabel, Double> transitionProbMap;
    private static HashMap<WordwithLabel, Double> emissionProbMap;
    private static HashMap<POSTagswithLabel, Double> emissionProbWordFeaturesMap;

    public static HashMap<LabelwithLabel, Double> getTransitionProbMap() {
        return transitionProbMap;
    }

    public static void setTransitionProbMap(HashMap<LabelwithLabel, Double> transitionProbMap) {
        ProbabilitiesMaps.transitionProbMap = transitionProbMap;
    }

    public static HashMap<WordwithLabel, Double> getEmissionProbMap() {
        return emissionProbMap;
    }

    public static void setEmissionProbMap(HashMap<WordwithLabel, Double> emissionProbMap) {
        ProbabilitiesMaps.emissionProbMap = emissionProbMap;
    }

    public static HashMap<POSTagswithLabel, Double> getEmissionProbWordFeaturesMap() {
        return emissionProbWordFeaturesMap;
    }

    public static void setEmissionProbWordFeaturesMap(HashMap<POSTagswithLabel, Double> emissionProbWordFeaturesMap) {
        ProbabilitiesMaps.emissionProbWordFeaturesMap = emissionProbWordFeaturesMap;
    }

}
