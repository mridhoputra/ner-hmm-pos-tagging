/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Boundary;

import Controller.TestingPhase;
import Controller.TrainingPhase;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Windows 10
 */
public class JFrameNER extends javax.swing.JFrame {

    private String selectedFile;
    private TrainingPhase trainingPhase;
    private TestingPhase testingPhase;
    URL loadingIcon = getClass().getResource("/resources/images/ajax-loader.gif");
    URL doneIcon = getClass().getResource("/resources/images/tick_transparent.png");

    /**
     * Creates new form NERGUI
     */
    public JFrameNER() {
        initComponents();
        trainingPhase = new TrainingPhase();
        testingPhase = new TestingPhase();
        
        jTrainButton.setEnabled(false);
        jTestButton.setEnabled(false);
        
        jTrainLoadingLabel.setVisible(false);
        jTestLoadingLabel.setVisible(false);
        jCheckFileLabel.setVisible(false);

        jTrainProgressBar.setStringPainted(true);

        jTestProgressBar.setStringPainted(true);
    }

    class TrainingPhaseWorker extends SwingWorker {

        @Override
        protected TrainingPhase doInBackground() throws Exception {
            jTrainButton.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jTrainLoadingLabel.setVisible(true);

            jTrainLoadingLabel.setIcon(new javax.swing.ImageIcon(loadingIcon));
            jTrainLoadingLabel.setText("Preprocessing....");
            trainingPhase.preprocessing();
            jTrainProgressBar.setValue(33);
            Thread.sleep(50);

            jTrainLoadingLabel.setText("POS-Tagging....");
            trainingPhase.postagging();
            jTrainProgressBar.setValue(67);
            Thread.sleep(50);

            jTrainLoadingLabel.setText("Training Data with HMM....");
            trainingPhase.trainWithHMM();

            trainingPhase.writeWordListFile();
            trainingPhase.writeNERListFile();
            trainingPhase.writeHMMParameterValues();

            jTrainProgressBar.setValue(100);
            Thread.sleep(50);

            jTrainLoadingLabel.setIcon(new javax.swing.ImageIcon(doneIcon));;
            jTrainLoadingLabel.setText("Selesai. Nilai Parameter HMM Tersimpan.");
            setCursor(null); //turn off the wait cursor

            jTrainButton.setText("Pelatihan Selesai");
            return trainingPhase;
        }

        @Override
        protected void done() {
            //print total label count
            jPERCountTextField.setText(String.valueOf(trainingPhase.printTotalLabelCount("PER")));
            jLOCCountTextField.setText(String.valueOf(trainingPhase.printTotalLabelCount("LOC")));
            jORGCountTextField.setText(String.valueOf(trainingPhase.printTotalLabelCount("ORG")));
            jTIMECountTextField.setText(String.valueOf(trainingPhase.printTotalLabelCount("TIME")));
            jOTHCountTextField.setText(String.valueOf(trainingPhase.printTotalLabelCount("OTH")));

            //list word and value for each label
            jPERDataTextArea.setText(trainingPhase.printWordLabelCountList("PER").toString());
            jLOCDataTextArea.setText(trainingPhase.printWordLabelCountList("LOC").toString());
            jORGDataTextArea.setText(trainingPhase.printWordLabelCountList("ORG").toString());
            jTIMEDataTextArea.setText(trainingPhase.printWordLabelCountList("TIME").toString());
            jOTHDataTextArea.setText(trainingPhase.printWordLabelCountList("OTH").toString());

            //list word features
            jPERWordFeaturesTextArea.setText(trainingPhase.printPOSTagsLabelCountList("PER").toString());
            jLOCWordFeaturesTextArea.setText(trainingPhase.printPOSTagsLabelCountList("LOC").toString());
            jORGWordFeaturesTextArea.setText(trainingPhase.printPOSTagsLabelCountList("ORG").toString());
            jTIMEWordFeaturesTextArea.setText(trainingPhase.printPOSTagsLabelCountList("TIME").toString());
            jOTHWordFeaturesTextArea.setText(trainingPhase.printPOSTagsLabelCountList("OTH").toString());

            //enable load test file button
            jLoadTestingFileButton.setEnabled(true);
        }
    }

    class TestingPhaseWorker extends SwingWorker {

        @Override
        protected Boolean doInBackground() throws Exception {
            jTestButton.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jTestLoadingLabel.setVisible(true);
            jTestLoadingLabel.setIcon(new javax.swing.ImageIcon(loadingIcon));

            jTestProgressBar.setValue(0);
            
            jTestLoadingLabel.setText("Load Word List and NER List....");
            testingPhase.getWordAndNERList();
            jTestProgressBar.setValue(5);

            jTestLoadingLabel.setText("Loading HMM Parameter Values....");
            testingPhase.getHMMParameterValues();
            jTestProgressBar.setValue(10);

            jTestLoadingLabel.setText("Preprocessing....");
            testingPhase.preprocessing();
            jTestProgressBar.setValue(25);
            Thread.sleep(50);

            jTestLoadingLabel.setText("POS-Tagging....");
            testingPhase.postagging();
            jTestProgressBar.setValue(50);
            Thread.sleep(50);

            jTestLoadingLabel.setText("Testing Data with HMM....");
            testingPhase.classify();
            jTestProgressBar.setValue(75);
            Thread.sleep(50);

            jTestLoadingLabel.setText("Evaluation....");
            testingPhase.evaluation();
            jTestProgressBar.setValue(100);
            Thread.sleep(50);

            jTestLoadingLabel.setIcon(new javax.swing.ImageIcon(doneIcon));;
            jTestLoadingLabel.setText("Done.");
            setCursor(null); //turn off the wait cursor

            jTestButton.setText("Pengujian Selesai");
            return true;
        }

        @Override
        protected void done() {
            jDataResultTextPane.setText("");
            for (int i = 0; i < testingPhase.getNewsOutputSize(); i++) {
                for (int j = 0; j < testingPhase.getWordsSize(i); j++) {
                    String taggedWord = testingPhase.getTaggedWords(i, j);
                    Boolean isMatch = testingPhase.getIsMatch(i, j);
                    if (isMatch) {
                        // StyleContext
                        StyleContext sc = StyleContext.getDefaultStyleContext();
                        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                                StyleConstants.Foreground, Color.BLACK);

                        int len = jDataResultTextPane.getDocument().getLength(); // same value as
                        // getText().length();
                        jDataResultTextPane.setCaretPosition(len); // place caret at the end (with no selection)
                        jDataResultTextPane.setCharacterAttributes(aset, false);
                        jDataResultTextPane.replaceSelection(taggedWord.concat(" ")); // there is no selection, so inserts at caret
                    } else {
                        // StyleContext
                        StyleContext sc = StyleContext.getDefaultStyleContext();
                        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                                StyleConstants.Foreground, Color.RED);

                        int len = jDataResultTextPane.getDocument().getLength(); // same value as
                        // getText().length();
                        jDataResultTextPane.setCaretPosition(len); // place caret at the end (with no selection)
                        jDataResultTextPane.setCharacterAttributes(aset, false);
                        jDataResultTextPane.replaceSelection(taggedWord.concat(" ")); // there is no selection, so inserts at caret
                    }
                }
                // StyleContext
                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                        StyleConstants.Foreground, Color.BLACK);

                int len = jDataResultTextPane.getDocument().getLength(); // same value as
                // getText().length();
                jDataResultTextPane.setCaretPosition(len); // place caret at the end (with no selection)
                jDataResultTextPane.setCharacterAttributes(aset, false);
                jDataResultTextPane.replaceSelection("\n"); // there is no selection, so inserts at caret
            }

            JPanel noWrapPanel = new JPanel(new BorderLayout());
            noWrapPanel.add(jDataResultTextPane);
            jResultScrollPane.setViewportView(noWrapPanel);

            HashMap<String, Integer> labelCount = new HashMap<>(testingPhase.getLabelCount());
            HashMap<String, Integer> labelTrueCount = new HashMap<>(testingPhase.getLabelTrueCount());
            HashMap<String, Double> pm = new HashMap<>(testingPhase.getPerformanceMeasures());

            //PER
            jTotalPERTextField.setText(String.valueOf(labelCount.get("totalPER")));
            jTrueCountPERTextField.setText(String.valueOf(labelTrueCount.get("trueCountPER")));
            jRecallPERTextField.setText(String.format("%.2f", (pm.get("recallPER") * 100)));
            jPrecisionPERTextField.setText(String.format("%.2f", (pm.get("precisionPER") * 100)));
            jFmeasurePERTextField.setText(String.format("%.2f", (pm.get("fmeasurePER") * 100)));

            //LOC
            jTotalLOCTextField.setText(String.valueOf(labelCount.get("totalLOC")));
            jTrueCountLOCTextField.setText(String.valueOf(labelTrueCount.get("trueCountLOC")));
            jRecallLOCTextField.setText(String.format("%.2f", (pm.get("recallLOC") * 100)));
            jPrecisionLOCTextField.setText(String.format("%.2f", (pm.get("precisionLOC") * 100)));
            jFmeasureLOCTextField.setText(String.format("%.2f", (pm.get("fmeasureLOC") * 100)));

            //ORG
            jTotalORGTextField.setText(String.valueOf(labelCount.get("totalORG")));
            jTrueCountORGTextField.setText(String.valueOf(labelTrueCount.get("trueCountORG")));
            jRecallORGTextField.setText(String.format("%.2f", (pm.get("recallORG") * 100)));
            jPrecisionORGTextField.setText(String.format("%.2f", (pm.get("precisionORG") * 100)));
            jFmeasureORGTextField.setText(String.format("%.2f", (pm.get("fmeasureORG") * 100)));

            //TIME
            jTotalTIMETextField.setText(String.valueOf(labelCount.get("totalTIME")));
            jTrueCountTIMETextField.setText(String.valueOf(labelTrueCount.get("trueCountTIME")));
            jRecallTIMETextField.setText(String.format("%.2f", (pm.get("recallTIME") * 100)));
            jPrecisionTIMETextField.setText(String.format("%.2f", (pm.get("precisionTIME") * 100)));
            jFmeasureTIMETextField.setText(String.format("%.2f", (pm.get("fmeasureTIME") * 100)));

            //AVG
            jRecallAVGTextField.setText(String.format("%.2f", (pm.get("recallAVG") * 100)));
            jPrecisionAVGTextField.setText(String.format("%.2f", (pm.get("precisionAVG") * 100)));
            jFmeasureAVGTextField.setText(String.format("%.2f", (pm.get("fmeasureAVG") * 100)));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BigTitle = new javax.swing.JLabel();
        SmallTitle = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jTrainingPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jInputTrainingFileLabel = new javax.swing.JLabel();
        jLoadTrainingFileButton = new javax.swing.JButton();
        jTrainButton = new javax.swing.JButton();
        jTrainProgressBar = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jDataTrainScrollPane = new javax.swing.JScrollPane();
        jDataTrainTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPERCountTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLOCCountTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jORGCountTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTIMECountTextField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jOTHCountTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPERDataTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLOCDataTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jORGDataTextArea = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTIMEDataTextArea = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jOTHDataTextArea = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPERWordFeaturesTextArea = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jLOCWordFeaturesTextArea = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        jORGWordFeaturesTextArea = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTIMEWordFeaturesTextArea = new javax.swing.JTextArea();
        jScrollPane10 = new javax.swing.JScrollPane();
        jOTHWordFeaturesTextArea = new javax.swing.JTextArea();
        jTrainLoadingLabel = new javax.swing.JLabel();
        jTestingPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jInputTestingFileLabel = new javax.swing.JLabel();
        jLoadTestingFileButton = new javax.swing.JButton();
        jTestButton = new javax.swing.JButton();
        jTestProgressBar = new javax.swing.JProgressBar();
        jScrollPane11 = new javax.swing.JScrollPane();
        jDataTestTextArea = new javax.swing.JTextArea();
        jLabel24 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jRecallPERTextField = new javax.swing.JTextField();
        jRecallLOCTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jRecallORGTextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jRecallTIMETextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jRecallAVGTextField = new javax.swing.JTextField();
        jPrecisionPERTextField = new javax.swing.JTextField();
        jPrecisionORGTextField = new javax.swing.JTextField();
        jPrecisionLOCTextField = new javax.swing.JTextField();
        jPrecisionTIMETextField = new javax.swing.JTextField();
        jPrecisionAVGTextField = new javax.swing.JTextField();
        jFmeasurePERTextField = new javax.swing.JTextField();
        jFmeasureLOCTextField = new javax.swing.JTextField();
        jFmeasureORGTextField = new javax.swing.JTextField();
        jFmeasureTIMETextField = new javax.swing.JTextField();
        jFmeasureAVGTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jTestLoadingLabel = new javax.swing.JLabel();
        jResultScrollPane = new javax.swing.JScrollPane();
        jDataResultTextPane = new javax.swing.JTextPane();
        jLabel19 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTotalPERTextField = new javax.swing.JTextField();
        jTotalLOCTextField = new javax.swing.JTextField();
        jTotalORGTextField = new javax.swing.JTextField();
        jTotalTIMETextField = new javax.swing.JTextField();
        jTrueCountPERTextField = new javax.swing.JTextField();
        jTrueCountLOCTextField = new javax.swing.JTextField();
        jTrueCountORGTextField = new javax.swing.JTextField();
        jTrueCountTIMETextField = new javax.swing.JTextField();
        jCheckFileLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BigTitle.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        BigTitle.setText("Named-Entity Recognition");

        SmallTitle.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        SmallTitle.setText("Menggunakan Hidden Markov Model dan POS-Tagging");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("Tahap Pelatihan");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Dataset :");

        jInputTrainingFileLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jInputTrainingFileLabel.setForeground(new java.awt.Color(0, 0, 0));
        jInputTrainingFileLabel.setText("Input File Dataset");
        jInputTrainingFileLabel.setToolTipText("");
        jInputTrainingFileLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jInputTrainingFileLabel.setMaximumSize(new java.awt.Dimension(315, 19));
        jInputTrainingFileLabel.setMinimumSize(new java.awt.Dimension(315, 19));
        jInputTrainingFileLabel.setName(""); // NOI18N
        jInputTrainingFileLabel.setPreferredSize(new java.awt.Dimension(315, 19));

        jLoadTrainingFileButton.setText("Load File");
        jLoadTrainingFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLoadTrainingFileButtonActionPerformed(evt);
            }
        });

        jTrainButton.setText("Lakukan Pelatihan");
        jTrainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTrainButtonActionPerformed(evt);
            }
        });

        jTrainProgressBar.setStringPainted(true);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Data Latih");

        jDataTrainTextArea.setColumns(20);
        jDataTrainTextArea.setRows(5);
        jDataTrainScrollPane.setViewportView(jDataTrainTextArea);

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setText("Hasil Pelatihan :");

        jLabel6.setText("Jumlah PER :");

        jLabel7.setText("Jumlah LOC :");

        jLabel8.setText("Jumlah ORG :");

        jLabel9.setText("Jumlah TIME :");

        jLabel22.setText("Jumlah OTH :");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel23.setText("Jumlah sebuah Kata dengan Label tertentu : ");

        jPERDataTextArea.setColumns(5);
        jPERDataTextArea.setRows(5);
        jScrollPane1.setViewportView(jPERDataTextArea);

        jLOCDataTextArea.setColumns(5);
        jLOCDataTextArea.setRows(5);
        jScrollPane2.setViewportView(jLOCDataTextArea);

        jORGDataTextArea.setColumns(5);
        jORGDataTextArea.setRows(5);
        jScrollPane3.setViewportView(jORGDataTextArea);

        jTIMEDataTextArea.setColumns(5);
        jTIMEDataTextArea.setRows(5);
        jScrollPane4.setViewportView(jTIMEDataTextArea);

        jOTHDataTextArea.setColumns(5);
        jOTHDataTextArea.setRows(5);
        jScrollPane5.setViewportView(jOTHDataTextArea);

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel10.setText("Daftar Word Features (POS-Tag dengan POS-Tag pada Kata Sebelumnya)");

        jPERWordFeaturesTextArea.setColumns(5);
        jPERWordFeaturesTextArea.setRows(5);
        jScrollPane6.setViewportView(jPERWordFeaturesTextArea);

        jLOCWordFeaturesTextArea.setColumns(5);
        jLOCWordFeaturesTextArea.setRows(5);
        jScrollPane7.setViewportView(jLOCWordFeaturesTextArea);

        jORGWordFeaturesTextArea.setColumns(5);
        jORGWordFeaturesTextArea.setRows(5);
        jScrollPane8.setViewportView(jORGWordFeaturesTextArea);

        jTIMEWordFeaturesTextArea.setColumns(5);
        jTIMEWordFeaturesTextArea.setRows(5);
        jScrollPane9.setViewportView(jTIMEWordFeaturesTextArea);

        jOTHWordFeaturesTextArea.setColumns(5);
        jOTHWordFeaturesTextArea.setRows(5);
        jScrollPane10.setViewportView(jOTHWordFeaturesTextArea);

        jTrainLoadingLabel.setText("jTrainLoadingLabel");

        javax.swing.GroupLayout jTrainingPanelLayout = new javax.swing.GroupLayout(jTrainingPanel);
        jTrainingPanel.setLayout(jTrainingPanelLayout);
        jTrainingPanelLayout.setHorizontalGroup(
            jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTrainingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jDataTrainScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addGroup(jTrainingPanelLayout.createSequentialGroup()
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTrainProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTrainButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jInputTrainingFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLoadTrainingFileButton)
                            .addComponent(jTrainLoadingLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel23)
                    .addGroup(jTrainingPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPERCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLOCCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jORGCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTIMECountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jOTHCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jTrainingPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10)
                    .addGroup(jTrainingPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jTrainingPanelLayout.setVerticalGroup(
            jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jTrainingPanelLayout.createSequentialGroup()
                .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTrainingPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jInputTrainingFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLoadTrainingFileButton))
                        .addGap(3, 3, 3)
                        .addComponent(jTrainButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTrainProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTrainLoadingLabel))
                        .addGap(30, 30, 30)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDataTrainScrollPane))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTrainingPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jPERCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLOCCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jORGCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jTIMECountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jOTHCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane4)
                            .addComponent(jScrollPane5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jTrainingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane9)
                            .addComponent(jScrollPane10))))
                .addContainerGap())
        );

        jTabbedPane.addTab("Training", jTrainingPanel);

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel11.setText("Hasil Pengujian:");

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel12.setText("Recall");

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel13.setText(" Precision");

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel14.setText("F-Measure");

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel20.setText("Tahap Pengujian");

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel21.setText("Dataset :");

        jInputTestingFileLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jInputTestingFileLabel.setForeground(new java.awt.Color(0, 0, 0));
        jInputTestingFileLabel.setText("Input File Dataset");
        jInputTestingFileLabel.setToolTipText("");
        jInputTestingFileLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jInputTestingFileLabel.setMaximumSize(new java.awt.Dimension(315, 19));
        jInputTestingFileLabel.setMinimumSize(new java.awt.Dimension(315, 19));
        jInputTestingFileLabel.setName(""); // NOI18N
        jInputTestingFileLabel.setPreferredSize(new java.awt.Dimension(315, 19));

        jLoadTestingFileButton.setText("Load File");
        jLoadTestingFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLoadTestingFileButtonActionPerformed(evt);
            }
        });

        jTestButton.setText("Lakukan Pengujian");
        jTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTestButtonActionPerformed(evt);
            }
        });

        jTestProgressBar.setStringPainted(true);

        jDataTestTextArea.setColumns(20);
        jDataTestTextArea.setRows(5);
        jScrollPane11.setViewportView(jDataTestTextArea);

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel24.setText("Data Uji");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setText("PER");

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel16.setText("LOC");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel17.setText("ORG");

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel18.setText("TIME");

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel25.setText("Rata-rata");

        jRecallAVGTextField.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        jPrecisionAVGTextField.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        jFmeasureAVGTextField.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jTestLoadingLabel.setText("jTestLoadingLabel");

        jResultScrollPane.setViewportView(jDataResultTextPane);

        jLabel19.setText("*Label berwarna merah adalah Label NER yang tidak tepat");

        jLabel26.setText("Jumlah Label");

        jLabel27.setText(" Total Benar");

        jCheckFileLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jCheckFileLabel.setText("jCheckFileLabel");

        javax.swing.GroupLayout jTestingPanelLayout = new javax.swing.GroupLayout(jTestingPanel);
        jTestingPanel.setLayout(jTestingPanelLayout);
        jTestingPanelLayout.setHorizontalGroup(
            jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTestingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addGroup(jTestingPanelLayout.createSequentialGroup()
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTestProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTestButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jInputTestingFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLoadTestingFileButton)
                            .addGroup(jTestingPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTestLoadingLabel)
                                    .addComponent(jCheckFileLabel)))))
                    .addComponent(jLabel24)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jResultScrollPane)
                    .addGroup(jTestingPanelLayout.createSequentialGroup()
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(46, 46, 46)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jTestingPanelLayout.createSequentialGroup()
                                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTotalTIMETextField)
                                    .addComponent(jTotalORGTextField)
                                    .addComponent(jTotalLOCTextField)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTotalPERTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(47, 47, 47)
                                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTrueCountTIMETextField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27)
                                    .addComponent(jTrueCountPERTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTrueCountLOCTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTrueCountORGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jTestingPanelLayout.createSequentialGroup()
                                .addGap(127, 127, 127)
                                .addComponent(jLabel25)))
                        .addGap(47, 47, 47)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jTestingPanelLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel12))
                            .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jRecallAVGTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRecallTIMETextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRecallORGTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRecallLOCTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRecallPERTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(46, 46, 46)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPrecisionPERTextField)
                            .addComponent(jPrecisionORGTextField)
                            .addComponent(jPrecisionLOCTextField)
                            .addComponent(jPrecisionTIMETextField)
                            .addComponent(jPrecisionAVGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jFmeasurePERTextField)
                            .addComponent(jFmeasureLOCTextField)
                            .addComponent(jFmeasureORGTextField)
                            .addComponent(jFmeasureTIMETextField)
                            .addComponent(jFmeasureAVGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE))
                    .addGroup(jTestingPanelLayout.createSequentialGroup()
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel19))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jTestingPanelLayout.setVerticalGroup(
            jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTestingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jInputTestingFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLoadTestingFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTestButton)
                    .addComponent(jCheckFileLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTestProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTestLoadingLabel))
                .addGap(8, 8, 8)
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTestingPanelLayout.createSequentialGroup()
                        .addComponent(jResultScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRecallPERTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jPrecisionPERTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFmeasurePERTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTrueCountPERTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTotalPERTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jRecallLOCTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPrecisionLOCTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFmeasureLOCTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTotalLOCTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTrueCountLOCTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jRecallORGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPrecisionORGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFmeasureORGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTotalORGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTrueCountORGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jRecallTIMETextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPrecisionTIMETextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFmeasureTIMETextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTotalTIMETextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTrueCountTIMETextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jTestingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jRecallAVGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPrecisionAVGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFmeasureAVGTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 26, Short.MAX_VALUE))
                    .addComponent(jScrollPane11))
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jTabbedPane.addTab("Testing", jTestingPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(484, 484, 484)
                        .addComponent(SmallTitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(562, 562, 562)
                        .addComponent(BigTitle))
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1360, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BigTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SmallTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTestButtonActionPerformed
        // TODO add your handling code here:
        TestingPhaseWorker testingWorker = new TestingPhaseWorker();
        testingWorker.execute();
    }//GEN-LAST:event_jTestButtonActionPerformed

    private void jLoadTestingFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLoadTestingFileButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih File");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = String.valueOf(chooser.getSelectedFile());
            jInputTestingFileLabel.setText(selectedFile.substring(selectedFile.lastIndexOf("\\") + 1));
            System.out.println("Selected File: " + selectedFile);
            testingPhase.readDocument(selectedFile);
            jDataTestTextArea.setText(testingPhase.printDocument().toString());
            if (testingPhase.checkFile()) {
                jCheckFileLabel.setVisible(false);
                jTestButton.setEnabled(true);
            } else {
                jCheckFileLabel.setVisible(true);
                jCheckFileLabel.setText("<html>File Nilai Parameter HMM Belum Ada.<br>Silahkan Lakukan Pelatihan Terlebih Dahulu</html>");
            }
        } else {
            System.out.println("Failed");
        }
    }//GEN-LAST:event_jLoadTestingFileButtonActionPerformed

    private void jTrainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTrainButtonActionPerformed
        TrainingPhaseWorker trainWorker = new TrainingPhaseWorker();
        trainWorker.execute();
    }//GEN-LAST:event_jTrainButtonActionPerformed

    private void jLoadTrainingFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLoadTrainingFileButtonActionPerformed
        // TODO add your handling code here:
        jTestButton.setText("Lakukan Pengujian");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih File");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = String.valueOf(chooser.getSelectedFile());
            jInputTrainingFileLabel.setText(selectedFile.substring(selectedFile.lastIndexOf("\\") + 1));
            System.out.println("Selected File: " + selectedFile);
            trainingPhase.readDocument(selectedFile);
            jDataTrainTextArea.setText(trainingPhase.printDocument().toString());
            jTrainButton.setEnabled(true);
        } else {
            System.out.println("Failed");
        }
    }//GEN-LAST:event_jLoadTrainingFileButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrameNER.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameNER.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameNER.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameNER.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameNER().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BigTitle;
    private javax.swing.JLabel SmallTitle;
    private javax.swing.JLabel jCheckFileLabel;
    private javax.swing.JTextPane jDataResultTextPane;
    private javax.swing.JTextArea jDataTestTextArea;
    private javax.swing.JScrollPane jDataTrainScrollPane;
    private javax.swing.JTextArea jDataTrainTextArea;
    private javax.swing.JTextField jFmeasureAVGTextField;
    private javax.swing.JTextField jFmeasureLOCTextField;
    private javax.swing.JTextField jFmeasureORGTextField;
    private javax.swing.JTextField jFmeasurePERTextField;
    private javax.swing.JTextField jFmeasureTIMETextField;
    private javax.swing.JLabel jInputTestingFileLabel;
    private javax.swing.JLabel jInputTrainingFileLabel;
    private javax.swing.JTextField jLOCCountTextField;
    private javax.swing.JTextArea jLOCDataTextArea;
    private javax.swing.JTextArea jLOCWordFeaturesTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLoadTestingFileButton;
    private javax.swing.JButton jLoadTrainingFileButton;
    private javax.swing.JTextField jORGCountTextField;
    private javax.swing.JTextArea jORGDataTextArea;
    private javax.swing.JTextArea jORGWordFeaturesTextArea;
    private javax.swing.JTextField jOTHCountTextField;
    private javax.swing.JTextArea jOTHDataTextArea;
    private javax.swing.JTextArea jOTHWordFeaturesTextArea;
    private javax.swing.JTextField jPERCountTextField;
    private javax.swing.JTextArea jPERDataTextArea;
    private javax.swing.JTextArea jPERWordFeaturesTextArea;
    private javax.swing.JTextField jPrecisionAVGTextField;
    private javax.swing.JTextField jPrecisionLOCTextField;
    private javax.swing.JTextField jPrecisionORGTextField;
    private javax.swing.JTextField jPrecisionPERTextField;
    private javax.swing.JTextField jPrecisionTIMETextField;
    private javax.swing.JTextField jRecallAVGTextField;
    private javax.swing.JTextField jRecallLOCTextField;
    private javax.swing.JTextField jRecallORGTextField;
    private javax.swing.JTextField jRecallPERTextField;
    private javax.swing.JTextField jRecallTIMETextField;
    private javax.swing.JScrollPane jResultScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTIMECountTextField;
    private javax.swing.JTextArea jTIMEDataTextArea;
    private javax.swing.JTextArea jTIMEWordFeaturesTextArea;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JButton jTestButton;
    private javax.swing.JLabel jTestLoadingLabel;
    private javax.swing.JProgressBar jTestProgressBar;
    private javax.swing.JPanel jTestingPanel;
    private javax.swing.JTextField jTotalLOCTextField;
    private javax.swing.JTextField jTotalORGTextField;
    private javax.swing.JTextField jTotalPERTextField;
    private javax.swing.JTextField jTotalTIMETextField;
    private javax.swing.JButton jTrainButton;
    private javax.swing.JLabel jTrainLoadingLabel;
    private javax.swing.JProgressBar jTrainProgressBar;
    private javax.swing.JPanel jTrainingPanel;
    private javax.swing.JTextField jTrueCountLOCTextField;
    private javax.swing.JTextField jTrueCountORGTextField;
    private javax.swing.JTextField jTrueCountPERTextField;
    private javax.swing.JTextField jTrueCountTIMETextField;
    // End of variables declaration//GEN-END:variables
}
