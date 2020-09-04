/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.LabelwithLabel;
import Entity.NERList;
import Entity.News;
import Entity.NewsOutput;
import Entity.POSTagswithLabel;
import Entity.PerformanceMeasures;
import Entity.ProbabilitiesMaps;
import Entity.WordList;
import Entity.WordwithLabel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows 10
 */
public class TestingPhase {

    private News testingNews;
    private DocumentReader testingDR;
    private NewsOutput resultData;
    private PerformanceMeasures pm;

    public void readDocument(String selectedFile) {
        testingNews = new News();
        resultData = new NewsOutput();
        pm = new PerformanceMeasures();
        testingDR = new DocumentReader(selectedFile, testingNews);
        try {
            testingDR.readNews();
        } catch (IOException ex) {
            Logger.getLogger(TrainingPhase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StringBuilder printDocument() {
        return testingDR.printContent();
    }

    public void testData() {
        System.out.println("----------------------------------");
        System.out.println("TESTING PHASE");
        System.out.println("----------------------------------");

        if (checkFile() == true) {
            System.out.print("Taking values....");
            getWordAndNERList();
            getHMMParameterValues();
            System.out.println("DONE");

            System.out.print("Preprocessing....");
            preprocessing();
            System.out.println("DONE");

            System.out.print("POS-Tagging....");
            postagging();
            System.out.println("DONE");

            System.out.print("HMM Classifier....");
            classify();
            System.out.println("DONE");

            System.out.println("Evaluation....");
            evaluation();
            System.out.println("DONE");

            System.out.println("TESTING PHASE: 100%");
            System.out.println("----------------------------------");
        } else {
            System.out.println("Please do data training first");
        }

    }

    public boolean checkFile() {
        File wordlistFile = new File("wordlist.txt");
        File nerlistFile = new File("nerlist.txt");
        File transitionFile = new File("transition.txt");
        File emissionFile = new File("emission.txt");
        File emissionwfFile = new File("emissionwf.txt");

        if (wordlistFile.exists()
                && nerlistFile.exists()
                && transitionFile.exists()
                && emissionFile.exists()
                && emissionwfFile.exists()) {
            return true;
        }
        return false;
    }

    public void getWordAndNERList() {
        WordList.setWordList(getWordListFromFile());
        NERList.setNerList(getNERListFromFile());
    }

    public void getHMMParameterValues() {
        ProbabilitiesMaps.setTransitionProbMap(getTransProbFromFile());
        ProbabilitiesMaps.setEmissionProbMap(getEmissionProbFromFile());
        ProbabilitiesMaps.setEmissionProbWordFeaturesMap(getEmissionProbWFFromFile());
    }

    public void preprocessing() {
        Preprocessing preprocess = new Preprocessing(testingNews);
        preprocess.run();
    }

    public void postagging() {
        POSTagging postagging = new POSTagging(testingNews);
        postagging.run();
    }

    public void classify() {
        HMMClassifier viterbi = new HMMClassifier(testingNews, resultData);
        viterbi.run();
    }

    public void evaluation() {
        Evaluation evaluation = new Evaluation(resultData, pm);
        evaluation.run();
    }

    public HashMap<String, Integer> getWordListFromFile() {
        try {
            //read file : "Word List"
            File toRead = new File("wordlist.txt");
            FileInputStream fis = new FileInputStream(toRead);

            Scanner sc = new Scanner(fis);

            HashMap<String, Integer> mapInFile = new HashMap<>();

            //read data from file line by line:
            String currentLine;
            while (sc.hasNextLine()) {
                currentLine = sc.nextLine();
                //now tokenize the currentLine:
                Scanner scanLine = new Scanner(currentLine);
                String word = scanLine.next();
                Integer count = scanLine.nextInt();
                //put tokens ot currentLine in map
                mapInFile.put(word, count);
            }
            fis.close();

            //print All data in MAP
            //mapInFile.entrySet().forEach(m -> {
            //    System.out.println(m.getKey() + " = " + m.getValue());
            //});
            return mapInFile;
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    public HashMap<String, Integer> getNERListFromFile() {
        try {
            //read file : "NER List"
            File toRead = new File("nerlist.txt");
            FileInputStream fis = new FileInputStream(toRead);

            Scanner sc = new Scanner(fis);

            HashMap<String, Integer> mapInFile = new HashMap<>();

            //read data from file line by line:
            String currentLine;
            while (sc.hasNextLine()) {
                currentLine = sc.nextLine();
                //now tokenize the currentLine:
                Scanner scanLine = new Scanner(currentLine);
                String label = scanLine.next();
                Integer count = scanLine.nextInt();
                //put tokens ot currentLine in map
                mapInFile.put(label, count);
            }
            fis.close();

            //print All data in MAP
            //mapInFile.entrySet().forEach(m -> {
            //    System.out.println(m.getKey() + " = " + m.getValue());
            //});
            return mapInFile;
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    public HashMap<LabelwithLabel, Double> getTransProbFromFile() {
        try {
            //read file : "Transition Probability Map"
            File toRead = new File("transition.txt");
            FileInputStream fis = new FileInputStream(toRead);

            Scanner sc = new Scanner(fis);

            HashMap<LabelwithLabel, Double> mapInFile = new HashMap<>();

            //read data from file line by line:
            String currentLine;
            while (sc.hasNextLine()) {
                currentLine = sc.nextLine();
                //now tokenize the currentLine:
                Scanner scanLine = new Scanner(currentLine);
                String label = scanLine.next();
                String previousLabel = scanLine.next();
                Double value = scanLine.nextDouble();
                LabelwithLabel labelwithlabel = new LabelwithLabel(label, previousLabel);
                //put tokens ot currentLine in map
                mapInFile.put(labelwithlabel, value);
            }
            fis.close();

            //print All data in MAP
            //mapInFile.entrySet().forEach(m -> {
            //    System.out.println(m.getKey().getLabel() + " " + m.getKey().getPreviousLabel() + " = " + m.getValue());
            //});
            return mapInFile;
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    public HashMap<WordwithLabel, Double> getEmissionProbFromFile() {
        try {
            //read file : "Emission Probability Map"
            File toRead = new File("emission.txt");
            FileInputStream fis = new FileInputStream(toRead);

            Scanner sc = new Scanner(fis);

            HashMap<WordwithLabel, Double> mapInFile = new HashMap<>();

            //read data from file line by line:
            String currentLine;
            while (sc.hasNextLine()) {
                currentLine = sc.nextLine();
                //now tokenize the currentLine:
                Scanner scanLine = new Scanner(currentLine);
                String word = scanLine.next();
                String label = scanLine.next();
                Double value = scanLine.nextDouble();
                WordwithLabel wordwithlabel = new WordwithLabel(word, label);
                //put tokens ot currentLine in map
                mapInFile.put(wordwithlabel, value);
            }
            fis.close();

            //print All data in MAP
            //mapInFile.entrySet().forEach(m -> {
            //    System.out.println(m.getKey().getWord() + " " + m.getKey().getLabel() + " = " + m.getValue());
            //});
            return mapInFile;
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    public HashMap<POSTagswithLabel, Double> getEmissionProbWFFromFile() {
        try {
            //read file : "Emission Probability Word Features Map"
            File toRead = new File("emissionwf.txt");
            FileInputStream fis = new FileInputStream(toRead);

            Scanner sc = new Scanner(fis);

            HashMap<POSTagswithLabel, Double> mapInFile = new HashMap<>();

            //read data from file line by line:
            String currentLine;
            while (sc.hasNextLine()) {
                currentLine = sc.nextLine();
                //now tokenize the currentLine:
                Scanner scanLine = new Scanner(currentLine);
                String postag = scanLine.next();
                String previous_postag = scanLine.next();
                String label = scanLine.next();
                Double value = scanLine.nextDouble();
                POSTagswithLabel postagwithlabel = new POSTagswithLabel(postag, previous_postag, label);
                //put tokens ot currentLine in map
                mapInFile.put(postagwithlabel, value);
            }
            fis.close();

            //print All data in MAP
            //mapInFile.entrySet().forEach(m -> {
            //    System.out.println(m.getKey().getPostag() 
            //            + " " 
            //            + m.getKey().getPrevious_postag() 
            //            + " "
            //            + m.getKey().getLabel()
            //            + " = " + m.getValue());
            //});
            return mapInFile;
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    public int getNewsOutputSize() {
        return resultData.getOutputList().size();
    }

    public int getWordsSize(int newsIndex) {
        return resultData.getOutputList().get(newsIndex).getWords().size();
    }

    public String getTaggedWords(int newsIndex, int wordIndex) {
        return resultData.getOutputList().get(newsIndex).getTaggedWords().get(wordIndex);
    }

    public Boolean getIsMatch(int newsIndex, int wordIndex) {
        return resultData.getOutputList().get(newsIndex).getIsMatch().get(wordIndex);
    }

    public StringBuilder getOutputData() {
        StringBuilder taggedSentences = new StringBuilder();
        for (int i = 0; i < resultData.getOutputList().size(); i++) {
            String sentence = String.join(" ", resultData.getOutputList().get(i).getTaggedWords());
            taggedSentences.append(i).append(". ");
            taggedSentences.append(sentence);
            taggedSentences.append("\n");
        }
        return taggedSentences;
    }

    public HashMap<String, Integer> getLabelCount() {
        return pm.getLabelCount();
    }

    public HashMap<String, Integer> getLabelTrueCount() {
        return pm.getLabelTrueCount();
    }

    public HashMap<String, Double> getPerformanceMeasures() {
        return pm.getPerformanceMeasure();
    }

}
