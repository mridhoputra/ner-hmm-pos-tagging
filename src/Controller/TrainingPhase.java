/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.NERList;
import Entity.News;
import Entity.WordList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows 10
 */
public class TrainingPhase {

    private News news;
    private HMMTraining hmm;
    private DocumentReader dr;

    public void readDocument(String selectedFile) {
        news = new News();
        dr = new DocumentReader(selectedFile, news);
        try {
            dr.readNews();
        } catch (IOException ex) {
            Logger.getLogger(TrainingPhase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StringBuilder printDocument() {
        return dr.printContent();
    }

    public void trainData() {
        System.out.println("----------------------------------");
        System.out.println("TRAINING PHASE");
        System.out.println("----------------------------------");

        System.out.print("Preprocessing....");
        preprocessing();
        System.out.println("DONE");

        System.out.print("POS-Tagging....");
        postagging();
        System.out.println("DONE");

        System.out.print("HMM Training....");
        trainWithHMM();
        writeWordListFile();
        writeNERListFile();
        writeHMMParameterValues();
        System.out.println("DONE");

        System.out.println("TRAINING PHASE: COMPLETED");
        System.out.println("----------------------------------");
    }

    public void preprocessing() {
        Preprocessing preprocess = new Preprocessing(news);
        preprocess.run();
    }

    public void postagging() {
        POSTagging postagging = new POSTagging(news);
        postagging.run();
    }

    public void trainWithHMM() {
        hmm = new HMMTraining(news);
        hmm.run();
    }

    public void writeWordListFile() {
        //write to file : "Transition Probability Map"
        try {
            File file = new File("wordlist.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);

            WordList.getWordList().entrySet().forEach(m -> {
                pw.println(m.getKey() + " " + m.getValue());
            });

            pw.flush();
            pw.close();
            fos.close();

        } catch (IOException e) {
            e.getLocalizedMessage();
        }
    }

    public void writeNERListFile() {
        //write to file : "Transition Probability Map"
        try {
            File file = new File("nerlist.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);

            NERList.getNerList().entrySet().forEach(m -> {
                pw.println(m.getKey() + " " + m.getValue());
            });

            pw.flush();
            pw.close();
            fos.close();

        } catch (IOException e) {
            e.getLocalizedMessage();
        }
    }
    
    public void writeHMMParameterValues() {
        writeTransProbFile();
        writeEmissionProbFile();
        writeEmissionProbWFFile();
    }

    public void writeTransProbFile() {
        //write to file : "Transition Probability Map"
        try {
            File file = new File("transition.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);

            hmm.getTransitionProbability().entrySet().forEach(m -> {
                pw.println(m.getKey().getLabel()
                        + " "
                        + m.getKey().getPreviousLabel()
                        + " "
                        + m.getValue());
            });

            pw.flush();
            pw.close();
            fos.close();

        } catch (IOException e) {
            e.getLocalizedMessage();
        }
    }

    public void writeEmissionProbFile() {
        //write to file : "Emission Probability Map"
        try {
            File file = new File("emission.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);

            hmm.getEmissionProbability().entrySet().forEach(m -> {
                pw.println(m.getKey().getWord()
                        + " "
                        + m.getKey().getLabel()
                        + " "
                        + m.getValue());
            });

            pw.flush();
            pw.close();
            fos.close();

        } catch (IOException e) {
            e.getLocalizedMessage();
        }
    }

    public void writeEmissionProbWFFile() {
        //write to file : "Emission Probability Word Features Map"
        try {
            File file = new File("emissionwf.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);

            hmm.getEmissionProbabilityWordFeatures().entrySet().forEach(m -> {
                pw.println(m.getKey().getPostag()
                        + " "
                        + m.getKey().getPrevious_postag()
                        + " "
                        + m.getKey().getLabel()
                        + " " + m.getValue());
            });

            pw.flush();
            pw.close();
            fos.close();

        } catch (IOException e) {
            e.getLocalizedMessage();
        }
    }

    public int printTotalLabelCount(String label) {
        return hmm.printTotalLabelCount(label);
    }

    public StringBuilder printWordLabelCountList(String label) {
        return hmm.printWordLabelCountList(label);
    }

    public StringBuilder printPOSTagsLabelCountList(String label) {
        return hmm.printPOSTagsLabelCountList(label);
    }

}
