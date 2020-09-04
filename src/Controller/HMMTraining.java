/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.LabelwithLabel;
import Entity.NERList;
import Entity.News;
import Entity.POSTagswithLabel;
import Entity.WordList;
import Entity.WordwithLabel;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author Windows 10
 */
public class HMMTraining {

    News news;
    HashMap<String, Integer> wordCount;
    HashMap<String, Integer> labelCount;
    HashMap<LabelwithLabel, Integer> labelwithlabelCount;
    HashMap<WordwithLabel, Integer> wordlabelCount;
    HashMap<POSTagswithLabel, Integer> postagwithpostagCount;
    HashMap<LabelwithLabel, Double> transitionProbabilityMap;
    HashMap<WordwithLabel, Double> emissionProbabilityMap;
    HashMap<POSTagswithLabel, Double> emissionProbabilityWordFeaturesMap;

    public HMMTraining(News news) {
        this.news = news;
    }

    public void run() {
        countWordWithSpesificLabel();
        createWordLists();
        createLabelLists();
        countLabelWithPreviousLabel();
        countPOSTagWithPreviousPOSTag();
        createTransitionProbabilityMap();
        createEmissionProbabilityMap();
        createEmissionProbabilityWordFeaturesMap();
    }

    public void countWordWithSpesificLabel() {
        wordlabelCount = new HashMap<>();
        wordCount = new HashMap<>();
        labelCount = new HashMap<>();

        for (int i = 0; i < news.getContentList().size(); i++) {
            for (int j = 0; j < news.getContentList().get(i).getWords().size(); j++) {
                String word = news.getContentList().get(i).getWords().get(j);
                String label = news.getContentList().get(i).getNERLabels().get(j);
                WordwithLabel wordwithlabel = new WordwithLabel(word, label);

                if (wordCount.containsKey(word)) {
                    wordCount.put(word, wordCount.get(word) + 1);
                } else {
                    wordCount.put(word, 1);
                }

                if (labelCount.containsKey(label)) {
                    labelCount.put(label, labelCount.get(label) + 1);
                } else {
                    labelCount.put(label, 1);
                }

                if (wordlabelCount.containsKey(wordwithlabel)) {
                    wordlabelCount.put(wordwithlabel, wordlabelCount.get(wordwithlabel) + 1);
                } else {
                    wordlabelCount.put(wordwithlabel, 1);
                }
            }
        }

        //print check
        /*
        wordlabelCount.keySet().forEach((wordwithlabel) -> {
            System.out.println(wordwithlabel.toString() + " value = " + wordlabelCount.get(wordwithlabel).toString());
        });
         */
    }

    public void createWordLists() {
        WordList.setWordList(wordCount);
    }

    public void createLabelLists() {
        //this will create list of distinct NER labels gathered from system
        NERList.setNerList(labelCount);
    }

    public void countLabelWithPreviousLabel() {
        labelwithlabelCount = new HashMap<>();

        for (int i = 0; i < news.getContentList().size(); i++) {
            for (int j = 0; j < news.getContentList().get(i).getWords().size(); j++) {
                LabelwithLabel labelwithlabel;
                if (j == 0) {
                    //start probability (start of the sentences)
                    String label = news.getContentList().get(i).getNERLabels().get(j);
                    String previousLabel = "start";
                    labelwithlabel = new LabelwithLabel(label, previousLabel);
                } else {
                    //transition probability (Cti|Cti-1)
                    String label = news.getContentList().get(i).getNERLabels().get(j);
                    String previousLabel = news.getContentList().get(i).getNERLabels().get(j - 1);
                    labelwithlabel = new LabelwithLabel(label, previousLabel);
                }

                if (labelwithlabelCount.containsKey(labelwithlabel)) {
                    labelwithlabelCount.put(labelwithlabel, labelwithlabelCount.get(labelwithlabel) + 1);
                } else {
                    labelwithlabelCount.put(labelwithlabel, 1);
                }
            }
        }

        //print check
        /*
        labelwithlabelCount.keySet().forEach((labelwithlabel) -> {
            System.out.println(labelwithlabel.toString() + " value = " + labelwithlabelCount.get(labelwithlabel).toString());
        });
         */
    }

    public void countPOSTagWithPreviousPOSTag() {
        postagwithpostagCount = new HashMap<>();

        for (int i = 0; i < news.getContentList().size(); i++) {
            for (int j = 0; j < news.getContentList().get(i).getWords().size(); j++) {
                POSTagswithLabel postagwithpostag;
                if (j == 0) {
                    //start probability (start of the sentences)
                    String postag = news.getContentList().get(i).getPOSTags().get(j);
                    String previousPostag = "start";
                    String label = news.getContentList().get(i).getNERLabels().get(j);
                    postagwithpostag = new POSTagswithLabel(postag, previousPostag, label);
                } else {
                    //transition probability (POSTagti|POSTagti-1)
                    String postag = news.getContentList().get(i).getPOSTags().get(j);
                    String previousPostag = news.getContentList().get(i).getPOSTags().get(j - 1);
                    String label = news.getContentList().get(i).getNERLabels().get(j);
                    postagwithpostag = new POSTagswithLabel(postag, previousPostag, label);
                }

                if (postagwithpostagCount.containsKey(postagwithpostag)) {
                    postagwithpostagCount.put(postagwithpostag, postagwithpostagCount.get(postagwithpostag) + 1);
                } else {
                    postagwithpostagCount.put(postagwithpostag, 1);
                }
            }
        }

        //print check
        /*
        postagwithpostagCount.keySet().forEach((postagwithpostag) -> {
            System.out.println(postagwithpostag.toString() + " value = " + postagwithpostagCount.get(postagwithpostag).toString());
        });
         */
    }

    public void createTransitionProbabilityMap() {
        transitionProbabilityMap = new HashMap<>();
        labelwithlabelCount.keySet().forEach((labelwithlabel) -> {
            Integer labelsBigram = labelwithlabelCount.get(labelwithlabel);
            Integer currentLabelCount = labelCount.get(labelwithlabel.getLabel());
            double result = (double) labelsBigram / currentLabelCount;
            //System.out.println(labelwithlabel.getLabel() + " " + labelwithlabel.getPreviousLabel() + " value = " + labelwithlabelCount.get(labelwithlabel));
            //System.out.println(labelsBigram + " / " + currentLabelCount + " = " + result);
            transitionProbabilityMap.put(labelwithlabel, result);
        });
    }

    public void createEmissionProbabilityMap() {
        emissionProbabilityMap = new HashMap<>();
        wordlabelCount.keySet().forEach((wordwithlabel) -> {
            Integer wordwithlabelCount = wordlabelCount.get(wordwithlabel);
            Integer currentLabelCount = labelCount.get(wordwithlabel.getLabel());
            double result = (double) wordwithlabelCount / currentLabelCount;
            //System.out.println(wordwithlabel.getWord() + " " + wordwithlabel.getLabel() + " value = " + wordlabelCount.get(wordwithlabel));
            //System.out.println(wordwithlabelCount + " / " + currentLabelCount + " = " + result);
            emissionProbabilityMap.put(wordwithlabel, result);
        });
    }

    public void createEmissionProbabilityWordFeaturesMap() {
        emissionProbabilityWordFeaturesMap = new HashMap<>();
        postagwithpostagCount.keySet().forEach((postagwithpostag) -> {
            Integer postaglabelcount = postagwithpostagCount.get(postagwithpostag);
            Integer currentLabelCount = labelCount.get(postagwithpostag.getLabel());
            double result = (double) postaglabelcount / currentLabelCount;
            //System.out.println(postagwithpostag.getPostag() + " " + postagwithpostag.getPrevious_postag() + " " + postagwithpostag.getLabel() + " value = " + postagwithpostagCount.get(postagwithpostag));
            //System.out.println(postagwithpostagCount + " / " + currentLabelCount + " = " + result);
            emissionProbabilityWordFeaturesMap.put(postagwithpostag, result);

            //print check
            System.out.println(postagwithpostag.toString() + " value = " + postagwithpostagCount.get(postagwithpostag).toString());

        });

    }

    public int printTotalLabelCount(String label) {
        if (labelCount.containsKey(label)) {
            return labelCount.get(label);
        } else {
            return 0;
        }
    }

    public StringBuilder printWordLabelCountList(String label) {
        StringBuilder sb = new StringBuilder();

        //using treemap so word list can be printed in ascending order
        TreeMap<WordwithLabel, Integer> sortedWordLabelCount = new TreeMap<>(wordlabelCount);

        sortedWordLabelCount.keySet().forEach((wordwithLabel) -> {
            if (wordwithLabel.getLabel().equals(label)) {
                sb.append(wordwithLabel.getWord());
                sb.append(" - ");
                sb.append(wordlabelCount.get(wordwithLabel).toString());
                sb.append("\n");

                //print check
                //System.out.println(wordwithLabel.toString() + " value = " + wordlabelCount.get(wordwithLabel).toString());
            }
        });

        return sb;
    }

    public StringBuilder printPOSTagsLabelCountList(String label) {
        StringBuilder sb = new StringBuilder();

        //using treemap so word list can be printed in ascending order
        TreeMap<POSTagswithLabel, Integer> sortedPOSTagsLabelCount = new TreeMap<>(postagwithpostagCount);

        sortedPOSTagsLabelCount.keySet().forEach((postagwithpostag) -> {
            if (postagwithpostag.getLabel().equals(label)) {
                sb.append(postagwithpostag.getPostag());
                sb.append(" - ");
                sb.append(postagwithpostag.getPrevious_postag());
                sb.append(" = ");
                sb.append(postagwithpostagCount.get(postagwithpostag).toString());
                sb.append("\n");
            }
        });

        return sb;
    }

    public HashMap<LabelwithLabel, Double> getTransitionProbability() {
        return transitionProbabilityMap;
    }

    public HashMap<WordwithLabel, Double> getEmissionProbability() {
        return emissionProbabilityMap;
    }

    public HashMap<POSTagswithLabel, Double> getEmissionProbabilityWordFeatures() {
        return emissionProbabilityWordFeaturesMap;
    }

}
