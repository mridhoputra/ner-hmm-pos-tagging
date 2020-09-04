/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.News;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Windows 10
 */
public class DocumentReader {
    
    private String filePath;
    private News news;
    
    public DocumentReader(String filePath, News news){
        this.filePath = filePath;
        this.news = news;
    }
    
    public void readNews()throws FileNotFoundException, IOException{
        
        FileInputStream file = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(file);
        
        if (isr != null){
            BufferedReader in = new BufferedReader(isr);
            
            String content;
            
            while((content = in.readLine()) != null){
                News tempNews = new News();
                tempNews.setContent(content);
                news.setContentList(tempNews);
            }
            
            in.close();
            isr.close();
            file.close();
        }
    }
    
    public StringBuilder printContent(){
        StringBuilder forprinting = new StringBuilder();
        for(int i=0; i< news.getContentList().size();i++) {
            News tempNews = news.getContentList().get(i);
            forprinting.append(tempNews.getContent());
            forprinting.append("\n");
        }
        return forprinting;
    }
    
}
