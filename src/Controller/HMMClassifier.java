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
import Entity.ProbabilitiesMaps;
import Entity.WordList;
import Entity.WordwithLabel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows 10
 */
public class HMMClassifier {

    News testingNews;
    NewsOutput resultData;
    int unknownWordCount;

    public HMMClassifier(News testingNews, NewsOutput resultData) {
        this.testingNews = testingNews;
        this.resultData = resultData;
        unknownWordCount = 0;
    }

    public void run() {
        System.out.println("");
        for (int i = 0; i < testingNews.getContentList().size(); i++) {
            List<String> sentence = new ArrayList<>(testingNews.getContentList().get(i).getWords());
            List<String> NERLabels = new ArrayList<>(NERList.getNerList().keySet());
            List<String> POSTags = new ArrayList<>(testingNews.getContentList().get(i).getPOSTags());
            List<String> NERLabelsFromTestData = new ArrayList<>(testingNews.getContentList().get(i).getNERLabels());
            List<String> labelResult;

            labelResult = viterbi(sentence, NERLabels, POSTags);
            createOutputData(sentence, NERLabelsFromTestData, labelResult);

            System.out.println("---------------Kalimat ke-"+i+"---------------");
            System.out.println(resultData.getOutputList().get(i).getWords());
            System.out.println(POSTags);
            System.out.println(resultData.getOutputList().get(i).getNERLabelsFromTestData());
            System.out.println(resultData.getOutputList().get(i).getNERLabelsUsingHMM());
            System.out.println(resultData.getOutputList().get(i).getIsMatch());
            System.out.println(resultData.getOutputList().get(i).getTaggedWords().toString());
            System.out.println("");
        }
        int totalUnknownWords = unknownWordCount / 5;
        System.out.println("Unkown Word Count: "+totalUnknownWords);
    }

    public List<String> viterbi(List<String> words, List<String> NERLabels, List<String> POSTags) {
        //viterbi Table size is KxT : K is the size of label, T is how much the word has in a sentence
        //the same goes to backpointer
        int NERLabelSize = NERLabels.size();
        int wordsSize = words.size();
        double[][] viterbiTable = new double[NERLabelSize][wordsSize];
        int[][] backpointer = new int[NERLabelSize][wordsSize];
        double bestpathprob;
        int[] bestpathpointer = new int[wordsSize];
        List<String> labelResult = new ArrayList<>();

        //Create a path probability matrix viterbi [K,T]
        //initialization step
        for (int i = 0; i < NERLabelSize; i++) {
            String currentLabel = NERLabels.get(i);
            String prevLabel = "start";
            String currentWord = words.get(0);
            LabelwithLabel labelwithlabel = new LabelwithLabel(currentLabel, prevLabel);
            WordwithLabel wordwithlabel = new WordwithLabel(currentWord, currentLabel);

            double startTransition;
            if (ProbabilitiesMaps.getTransitionProbMap().containsKey(labelwithlabel)) {
                startTransition = ProbabilitiesMaps.getTransitionProbMap().get(labelwithlabel);
            } else {
                startTransition = 0.0;
            }

            double startEmission;
            boolean isWordExist = false;
            if (WordList.getWordList().containsKey(currentWord)) {
                isWordExist = true;
            }
            if (isWordExist) {
                if (ProbabilitiesMaps.getEmissionProbMap().containsKey(wordwithlabel)) {
                    startEmission = ProbabilitiesMaps.getEmissionProbMap().get(wordwithlabel);
                } else {
                    startEmission = 0.0;
                }
            } else {
                unknownWordCount++;
                String currentPOSTag = POSTags.get(0);
                String previousPOSTag = "start";
                POSTagswithLabel postagwithlabel
                        = new POSTagswithLabel(currentPOSTag, previousPOSTag, currentLabel);
                if (ProbabilitiesMaps.getEmissionProbWordFeaturesMap().containsKey(postagwithlabel)) {
                    startEmission = ProbabilitiesMaps.getEmissionProbWordFeaturesMap().get(postagwithlabel);
                } else {
                    startEmission = 0.0;
                }
            }

            viterbiTable[i][0] = startTransition * startEmission;
            backpointer[i][0] = -1; //start point
            //System.out.println("viterbiTable[" + NERLabels.get(i) + "]" + "[" + words.get(0) + "]" + "= " + viterbiTable[i][0]);
            //System.out.println("backpointer[" + NERLabels.get(i) + "]" + "[" + words.get(0) + "]" + "= " + backpointer[i][0]);
        }

        //recursion step
        for (int j = 1; j < wordsSize; j++) {
            for (int i = 0; i < NERLabelSize; i++) {
                double[] probResult = new double[NERLabelSize];
                for (int k = 0; k < NERLabelSize; k++) {
                    String currentLabel = NERLabels.get(i);
                    String prevLabel = NERLabels.get(k);
                    String currentWord = words.get(j);
                    LabelwithLabel labelwithlabel = new LabelwithLabel(currentLabel, prevLabel);
                    WordwithLabel wordwithlabel = new WordwithLabel(currentWord, currentLabel);

                    double transitionProb;
                    if (ProbabilitiesMaps.getTransitionProbMap().containsKey(labelwithlabel)) {
                        transitionProb = ProbabilitiesMaps.getTransitionProbMap().get(labelwithlabel);
                    } else {
                        transitionProb = 0.0;
                    }

                    double emissionProb;
                    boolean isWordExist = false;
                    if (WordList.getWordList().containsKey(currentWord)) {
                        isWordExist = true;
                    }
                    if (isWordExist) {
                        if (ProbabilitiesMaps.getEmissionProbMap().containsKey(wordwithlabel)) {
                            emissionProb = ProbabilitiesMaps.getEmissionProbMap().get(wordwithlabel);
                        } else {
                            emissionProb = 0.0;
                        }

                    } else {
                        unknownWordCount++;
                        String currentPOSTag = POSTags.get(j);
                        String previousPOSTag = POSTags.get(j - 1);
                        POSTagswithLabel postagwithlabel
                                = new POSTagswithLabel(currentPOSTag, previousPOSTag, currentLabel);
                        if (ProbabilitiesMaps.getEmissionProbWordFeaturesMap().containsKey(postagwithlabel)) {
                            emissionProb = ProbabilitiesMaps.getEmissionProbWordFeaturesMap()
                                    .get(postagwithlabel);
                        } else {
                            emissionProb = 0.0;
                        }
                    }

                    probResult[k] = viterbiTable[k][j - 1] * transitionProb * emissionProb;
                }

                viterbiTable[i][j] = max(probResult);
                backpointer[i][j] = argmax(probResult);
                //System.out.println("viterbiTable[" + NERLabels.get(i) + "]" + "[" + words.get(j) + "]" + "= " + viterbiTable[i][j]);
                //System.out.println("backpointer[" + NERLabels.get(i) + "]" + "[" + words.get(j) + "]" + "= " + backpointer[i][j]);
            }
        }

        //termination step
        int lastWordIndex = wordsSize - 1;
        bestpathprob = maxLastProb(viterbiTable);
        bestpathpointer[lastWordIndex] = argmaxLastPointer(viterbiTable);
        
        //check value
        //System.out.println("bestpathprob = "+bestpathprob);
        //System.out.println("last pointer = "+bestpathpointer[lastWordIndex]);

        //generate viterbi path
        for (int j = lastWordIndex; j > 0; j--) {
            bestpathpointer[j - 1] = backpointer[bestpathpointer[j]][j];
        }

        //convert viterbi path di NER label sequence
        for (int pointer : bestpathpointer) {
            labelResult.add(NERLabels.get(pointer));
        }

        return labelResult;
    }

    double max(double[] probResult) {
        double highestValue = 0.0;
        for (double value : probResult) {
            if (value > highestValue) {
                highestValue = value;
            }
        }
        return highestValue;
    }

    double maxLastProb(double[][] viterbiTable) {
        double highestValue = 0.0;
        int lastWordIndex = viterbiTable[0].length - 1;

        for (int i = 0; i < viterbiTable.length; i++) {
            if (viterbiTable[i][lastWordIndex] > highestValue) {
                highestValue = viterbiTable[i][lastWordIndex];
            }
        }
        return highestValue;
    }

    //this function will return which label NER that can make equation into maximum value
    int argmax(double[] probResult) {
        double highestValue = 0.0;
        //if all of the probResult value is not higher than 0.0, return 1, the index of label OTH
        int indexNER = 1;
        for (int i = 0; i < probResult.length; i++) {
            if (probResult[i] > highestValue) {
                highestValue = probResult[i];
                indexNER = i;
            }
        }
        return indexNER;
    }

    int argmaxLastPointer(double[][] viterbiTable) {
        double highestValue = 0.0;
        int lastBackPointer = 1;
        int lastWordIndex = viterbiTable[0].length - 1;
        for (int i = 0; i < viterbiTable.length; i++) {
            if (viterbiTable[i][lastWordIndex] > highestValue) {
                lastBackPointer = i;
            }
        }
        return lastBackPointer;
    }

    public void createOutputData(List<String> words, List<String> NERLabelsFromTestDataList, List<String> labelResult) {
        NewsOutput tempOutput = new NewsOutput();
        tempOutput.setWords(words);
        tempOutput.setNERLabelsFromTestData(NERLabelsFromTestDataList);
        tempOutput.setNERLabelsUsingHMM(labelResult);

        List<String> taggedWords = new ArrayList<>();
        List<Boolean> isMatch = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {

            taggedWords.add(words.get(i).concat("/").concat(labelResult.get(i)));

            boolean temp = false;
            if (NERLabelsFromTestDataList.get(i).equals(labelResult.get(i))) {
                temp = true;
            }
            isMatch.add(temp);
        }

        tempOutput.setIsMatch(isMatch);
        tempOutput.setTaggedWords(taggedWords);

        resultData.setOutputList(tempOutput);
    }

}
