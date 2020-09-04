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
public class NERList {

    private static HashMap<String, Integer> nerList;

    public static HashMap<String, Integer> getNerList() {
        return nerList;
    }

    public static void setNerList(HashMap<String, Integer> nerList) {
        NERList.nerList = nerList;
    }

}
