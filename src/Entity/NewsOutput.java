/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows 10
 */
public class NewsOutput {

    private final List<NewsOutput> outputList;
    private List<String> words;
    private List<String> NERLabelsFromTestData;
    private List<String> NERLabelsUsingHMM;
    private List<Boolean> isMatch;
    private List<String> taggedWords;

    public NewsOutput() {
        this.outputList = new ArrayList<>();
        this.words = new ArrayList<>();
        this.NERLabelsFromTestData = new ArrayList<>();
        this.NERLabelsUsingHMM = new ArrayList<>();
        this.taggedWords = new ArrayList<>();
    }

    public List<NewsOutput> getOutputList() {
        return outputList;
    }
    
    public void setOutputList(NewsOutput newsOutput){
        outputList.add(newsOutput);
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<String> getNERLabelsFromTestData() {
        return NERLabelsFromTestData;
    }

    public void setNERLabelsFromTestData(List<String> NERLabelsFromTestData) {
        this.NERLabelsFromTestData = NERLabelsFromTestData;
    }

    public List<String> getNERLabelsUsingHMM() {
        return NERLabelsUsingHMM;
    }

    public void setNERLabelsUsingHMM(List<String> NERLabelsUsingHMM) {
        this.NERLabelsUsingHMM = NERLabelsUsingHMM;
    }

    public List<Boolean> getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(List<Boolean> isMatch) {
        this.isMatch = isMatch;
    }

    public List<String> getTaggedWords() {
        return taggedWords;
    }

    public void setTaggedWords(List<String> taggedWords) {
        this.taggedWords = taggedWords;
    }

}
