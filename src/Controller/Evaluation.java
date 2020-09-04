/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.ActualAndPredicted;
import Entity.NewsOutput;
import Entity.PerformanceMeasures;
import java.util.HashMap;

/**
 *
 * @author Windows 10
 */
public class Evaluation {

    NewsOutput resultData;
    PerformanceMeasures pm;
    HashMap<ActualAndPredicted, Integer> confusionMatrix;

    public Evaluation(NewsOutput resultData, PerformanceMeasures pm) {
        this.resultData = resultData;
        this.pm = pm;
    }

    public void run() {
        createConfusionMatrix();
        calculatePerformanceMeasures();
    }

    public void createConfusionMatrix() {
        confusionMatrix = new HashMap<>();

        for (int i = 0; i < resultData.getOutputList().size(); i++) {
            for (int j = 0; j < resultData.getOutputList().get(i).getWords().size(); j++) {
                String actualLabel = resultData.getOutputList().get(i).getNERLabelsFromTestData().get(j);
                String predictedLabel = resultData.getOutputList().get(i).getNERLabelsUsingHMM().get(j);
                ActualAndPredicted actualpredicted = new ActualAndPredicted(actualLabel, predictedLabel);

                if (confusionMatrix.containsKey(actualpredicted)) {
                    confusionMatrix.put(actualpredicted, confusionMatrix.get(actualpredicted) + 1);
                } else {
                    confusionMatrix.put(actualpredicted, 1);
                }
            }
        }

        //print check
        confusionMatrix.keySet().forEach((actualpredicted) -> {
            System.out.println(actualpredicted.toString() + " = " + confusionMatrix.get(actualpredicted).toString());
        });

    }

    public void calculatePerformanceMeasures() {
        int tpPER, tnPER, fpPER, fnPER;
        int tpLOC, tnLOC, fpLOC, fnLOC;
        int tpORG, tnORG, fpORG, fnORG;
        int tpTIME, tnTIME, fpTIME, fnTIME;
        int totalPER, totalLOC, totalORG, totalTIME;

        double precisionPER, recallPER, fmeasurePER;
        double precisionLOC, recallLOC, fmeasureLOC;
        double precisionORG, recallORG, fmeasureORG;
        double precisionTIME, recallTIME, fmeasureTIME;
        double precisionAVG, recallAVG, fmeasureAVG;

        String PER = "PER";
        String LOC = "LOC";
        String ORG = "ORG";
        String TIME = "TIME";
        String OTH = "OTH";

        //PER
        tpPER = getCount(PER, PER);
        tnPER = getCount(LOC, LOC) + getCount(ORG, ORG) + getCount(TIME, TIME) + getCount(OTH, OTH);
        fpPER = getCount(LOC, PER) + getCount(ORG, PER) + getCount(TIME, PER) + getCount(OTH, PER);
        fnPER = getCount(PER, LOC) + getCount(PER, ORG) + getCount(PER, TIME) + getCount(PER, OTH);
        totalPER = tpPER + fnPER;
        precisionPER = precision(tpPER, fpPER);
        recallPER = recall(tpPER, fnPER);
        fmeasurePER = fmeasure(precisionPER, recallPER);
        pm.setLabelCount("totalPER", totalPER);
        pm.setLabelTrueCount("trueCountPER", tpPER);
        pm.setPerformanceMeasure("precisionPER", precisionPER);
        pm.setPerformanceMeasure("recallPER", recallPER);
        pm.setPerformanceMeasure("fmeasurePER", fmeasurePER);

        //LOC
        tpLOC = getCount(LOC, LOC);
        tnLOC = getCount(PER, PER) + getCount(ORG, ORG) + getCount(TIME, TIME) + getCount(OTH, OTH);
        fpLOC = getCount(PER, LOC) + getCount(ORG, LOC) + getCount(TIME, LOC) + getCount(OTH, LOC);
        fnLOC = getCount(LOC, PER) + getCount(LOC, ORG) + getCount(LOC, TIME) + getCount(LOC, OTH);
        totalLOC = tpLOC + fnLOC;
        precisionLOC = precision(tpLOC, fpLOC);
        recallLOC = recall(tpLOC, fnLOC);
        fmeasureLOC = fmeasure(precisionLOC, recallLOC);
        pm.setLabelCount("totalLOC", totalLOC);
        pm.setLabelTrueCount("trueCountLOC", tpLOC);
        pm.setPerformanceMeasure("precisionLOC", precisionLOC);
        pm.setPerformanceMeasure("recallLOC", recallLOC);
        pm.setPerformanceMeasure("fmeasureLOC", fmeasureLOC);

        //ORG
        tpORG = getCount(ORG, ORG);
        tnORG = getCount(PER, PER) + getCount(LOC, LOC) + getCount(TIME, TIME) + getCount(OTH, OTH);
        fpORG = getCount(PER, ORG) + getCount(LOC, ORG) + getCount(TIME, ORG) + getCount(OTH, ORG);
        fnORG = getCount(ORG, PER) + getCount(ORG, LOC) + getCount(ORG, TIME) + getCount(ORG, OTH);
        totalORG = tpORG + fnORG;
        precisionORG = precision(tpORG, fpORG);
        recallORG = recall(tpORG, fnORG);
        fmeasureORG = fmeasure(precisionORG, recallORG);
        pm.setLabelCount("totalORG", totalORG);
        pm.setLabelTrueCount("trueCountORG", tpORG);
        pm.setPerformanceMeasure("precisionORG", precisionORG);
        pm.setPerformanceMeasure("recallORG", recallORG);
        pm.setPerformanceMeasure("fmeasureORG", fmeasureORG);

        //TIME
        tpTIME = getCount(TIME, TIME);
        tnTIME = getCount(PER, PER) + getCount(LOC, LOC) + getCount(ORG, ORG) + getCount(OTH, OTH);
        fpTIME = getCount(PER, TIME) + getCount(LOC, TIME) + getCount(ORG, TIME) + getCount(OTH, TIME);
        fnTIME = getCount(TIME, PER) + getCount(TIME, LOC) + getCount(TIME, ORG) + getCount(TIME, OTH);
        totalTIME = tpTIME + fnTIME;
        precisionTIME = precision(tpTIME, fpTIME);
        recallTIME = recall(tpTIME, fnTIME);
        fmeasureTIME = fmeasure(precisionTIME, recallTIME);
        pm.setLabelCount("totalTIME", totalTIME);
        pm.setLabelTrueCount("trueCountTIME", tpTIME);
        pm.setPerformanceMeasure("precisionTIME", precisionTIME);
        pm.setPerformanceMeasure("recallTIME", recallTIME);
        pm.setPerformanceMeasure("fmeasureTIME", fmeasureTIME);

        //AVG
        precisionAVG = (precisionPER + precisionLOC + precisionORG + precisionTIME) / 4;
        recallAVG = (recallPER + recallLOC + recallORG + recallTIME) / 4;
        fmeasureAVG = (fmeasurePER + fmeasureLOC + fmeasureORG + fmeasureTIME) / 4;
        pm.setPerformanceMeasure("precisionAVG", precisionAVG);
        pm.setPerformanceMeasure("recallAVG", recallAVG);
        pm.setPerformanceMeasure("fmeasureAVG", fmeasureAVG);
    }

    public int getCount(String label, String anotherLabel) {
        ActualAndPredicted actualpredicted = new ActualAndPredicted(label, anotherLabel);
        int valueCount;
        if (confusionMatrix.containsKey(actualpredicted)) {
            valueCount = confusionMatrix.get(actualpredicted);
        } else {
            valueCount = 0;
        }
        return valueCount;
    }

    public double precision(int tp, int fp) {
        if (tp == 0 && fp == 0) {
            return 0;
        } else {
            return ((double) tp) / ((double) (tp + fp));
        }
    }

    public double recall(int tp, int fn) {
        if (tp == 0 && fn == 0) {
            return 0;
        } else {
            return ((double) tp) / ((double) (tp + fn));
        }
    }

    public double fmeasure(double precision, double recall) {
        if (precision == 0 && recall == 0) {
            return 0;
        } else {
            return (2 * precision * recall) / (precision + recall);
        }
    }

    public double accuracy(int tp, int tn, int fp, int fn) {
        if (tp == 0 && tn == 0 && fp == 0 && fn == 0) {
            return 0;
        } else {
            return ((double) (tp + tn)) / ((double) (tp + tn + fp + fn));
        }
    }
}
