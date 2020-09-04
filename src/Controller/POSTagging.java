/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.News;
import NLP_ITB.POSTagger.HMM.Decoder.MainTagger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Windows 10
 */
public class POSTagging {

    News news;

    public POSTagging(News news) {
        this.news = news;
    }

    public void run() {
        for (int i = 0; i < news.getContentList().size(); i++) {
            List<String> sentence = news.getContentList().get(i).getWords();
            List<String> postag = pos_tag_NLP_ITB(joinWords(sentence));
            news.getContentList().get(i).setPOSTags(postag);
        }
    }

    public String joinWords(List<String> words) {
        return String.join(" ", words);
    }

    public List<String> pos_tag_NLP_ITB(String sentence) {
        List<String> pos_tag_result;
        List<String> POSTags = new ArrayList<>();
        int pass2 = 0;

        String fileLexicon = "src/resources/postagger/Lexicon.trn";
        String fileNGram = "src/resources/postagger/Ngram.trn";
        MainTagger mainTagger = new MainTagger(fileLexicon, fileNGram, pass2);

        if (sentence.contains("/")) {
            sentence = sentence.replaceAll("/", "");
        }

        pos_tag_result = mainTagger.taggingStr(sentence);

        for (String word_with_pos_tag : pos_tag_result) {
            String[] split = word_with_pos_tag.split("/");
            POSTags.add(split[split.length - 1]);
        }

        //this library can't handle sentence with only just one word
        //so i will create special case for this
        if (POSTags.isEmpty()) {
            if (sentence.matches("[a-zA-z]+")) {
                POSTags.add("FW");
            } else if (sentence.matches("^\\d+$")) {
                POSTags.add("CDP");
            } else if (sentence.matches("^[\\w\\W0-9]*$")) {
                POSTags.add("CDP");
            } else {
                POSTags.add("SYM");
            }
        }

        return POSTags;
    }
}
