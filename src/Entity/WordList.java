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
public class WordList {

    private static HashMap<String, Integer> wordList;

    public static HashMap<String, Integer> getWordList() {
        return wordList;
    }

    public static void setWordList(HashMap<String, Integer> wordList) {
        WordList.wordList = wordList;
    }

}
