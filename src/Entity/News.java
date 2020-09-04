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
public class News {

    private String content;
    private final List<News> contentList;
    private List<String> words;
    private List<String> NERLabels;
    private List<String> POSTags;

    public News() {
        this.contentList = new ArrayList<>();
        this.words = new ArrayList<>();
        this.NERLabels = new ArrayList<>();
        this.POSTags = new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<News> getContentList() {
        return contentList;
    }

    public void setContentList(News news) {
        contentList.add(news);
    }

    public List<String> getWords() {
        return words;
    }

    public void setSentence(List<String> words) {
        this.words = words;
    }

    public List<String> getNERLabels() {
        return NERLabels;
    }

    public void setNERLabels(List<String> NERLabels) {
        this.NERLabels = NERLabels;
    }

    public List<String> getPOSTags() {
        return POSTags;
    }

    public void setPOSTags(List<String> POSTags) {
        this.POSTags = POSTags;
    }

}
