/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.News;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;

/**
 *
 * @author Windows 10
 */
public class Preprocessing {

    News news;

    public Preprocessing(News news) {
        this.news = news;
    }

    public void run() {
        String cleanSentence;
        List<List<String>> word_and_label;
        List<String> sentence;
        List<String> labels;
        List<String> stemmed_sentence;

        for (int i = 0; i < news.getContentList().size(); i++) {

            //raw data
            String rawSentence = news.getContentList().get(i).getContent();

            //removePunctuation
            cleanSentence = removePunctuation(rawSentence);

            //print 'clean' sentence
            System.out.println(i + ". " + cleanSentence);
            //tokenization
            word_and_label = tokenization(cleanSentence);
            sentence = word_and_label.get(0);
            labels = word_and_label.get(1);

            //stemming
            stemmed_sentence = stemming(sentence, labels);

            //put sentence and labels to Entity
            news.getContentList().get(i).setSentence(stemmed_sentence);
            news.getContentList().get(i).setNERLabels(labels);

        }
    }

    private String removePunctuation(String sentence) {
        String SpecialCharacter = "[!\"$%&'()*\\+,.;:<=>?\\[\\]^~_\\`{|}…]";
        String DashWithSpace = "- ";
        String SlashWithSpace = " / ";
        String UnnecessarySpaces = " +";
        return sentence.replaceAll(SpecialCharacter, "")
                .replaceAll(DashWithSpace, "")
                .replaceAll(SlashWithSpace, " ")
                .replaceAll(UnnecessarySpaces, " ")
                .trim();
    }

    private List<List<String>> tokenization(String sentence) {

        String token[] = sentence.split("\\s");
        String splitcontainer[];
        List<String> word = new ArrayList<>();
        List<String> label = new ArrayList<>();
        List<List<String>> word_and_label = new ArrayList<>();

        //add tag OTH to Not-Named Entity words
        for (int j = 0; j < token.length; j++) {
            if (token[j].contains("/PER")
                    || token[j].contains("/LOC")
                    || token[j].contains("/ORG")
                    || token[j].contains("/TIME")) {
                //do nothing
            } else {
                token[j] = token[j].concat("/OTH");
            }
        }

        for (int j = 0; j < token.length; j++) {
            //split word and tag
            if (token[j].contains("/PER")) {
                splitcontainer = token[j].split("/PER");
                word.add(splitcontainer[0]);
                label.add("PER");
            } else if (token[j].contains("/LOC")) {
                splitcontainer = token[j].split("/LOC");
                word.add(splitcontainer[0]);
                label.add("LOC");
            } else if (token[j].contains("/ORG")) {
                splitcontainer = token[j].split("/ORG");
                word.add(splitcontainer[0]);
                label.add("ORG");
            } else if (token[j].contains("/TIME")) {
                splitcontainer = token[j].split("/TIME");
                word.add(splitcontainer[0]);
                label.add("TIME");
            } else if (token[j].contains("/OTH")) {
                splitcontainer = token[j].split("/OTH");
                word.add(splitcontainer[0]);
                label.add("OTH");
            } else {
                splitcontainer = token[j].split("/");
                System.out.println(Arrays.toString(splitcontainer));
            }
        }

        word_and_label.add(word);
        word_and_label.add(label);

        return word_and_label;
    }

    public List<String> stemming(List<String> words, List<String> labels) {
        List<String> stemmed_words = new ArrayList<>();
        // Mulai setup JSastrawi, cukup dijalankan 1 kali

        // JSastrawi lemmatizer membutuhkan kamus kata dasar
        // dalam bentuk Set<String>
        Set<String> dictionary = new HashSet<>();

        // Memuat file kata dasar dari distribusi JSastrawi
        // Jika perlu, anda dapat mengganti file ini dengan kamus anda sendiri
        InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        try {
            while ((line = br.readLine()) != null) {
                dictionary.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Preprocessing.class.getName()).log(Level.SEVERE, null, ex);
        }

        Lemmatizer lemmatizer = new DefaultLemmatizer(dictionary);
        // Selesai setup JSastrawi

        // lemmatizer bisa digunakan pada setiap kata
        // lemmatizer hanya dilakukan pada kata yang diawali huruf bukan kapital
        // lemmatizer juga hanya dilakukan pada kata label OTH di awal kalimat
        // hal diatas dilakukan karena output lemmatizer yang membuat setiap huruf menjadi lowercase
        for (int i = 0; i < words.size(); i++) {
            if (i == 0) {
                if (Character.isUpperCase(words.get(0).codePointAt(0))) {
                    if (labels.get(0).contains("OTH")) {
                        stemmed_words.add(lemmatizer.lemmatize(words.get(0)));
                    } else {
                        stemmed_words.add(words.get(0));
                    }
                } else {
                    stemmed_words.add(lemmatizer.lemmatize(words.get(0)));
                }
            } else {
                if (Character.isUpperCase(words.get(i).codePointAt(0))) {
                    stemmed_words.add(words.get(i));
                } else {
                    stemmed_words.add(lemmatizer.lemmatize(words.get(i)));
                }
            }
        }

        return stemmed_words;
    }
}
