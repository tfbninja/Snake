package snake;

import javax.swing.JTextField;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Tim Barber
 */
public class TestPanel extends javax.swing.JPanel {

    private JTextField filename = new JTextField();
    private JTextField dir = new JTextField();
    private int toolNum = 0;
    private String[] colorScheme;
    private Grid grid;

    /**
     * Creates new form TestPanel
     *
     * @param colorScheme The button colors
     * @param grid The grid to control
     */
    public TestPanel(String[] colorScheme, Grid grid) {
        this.colorScheme = colorScheme;
        this.grid = grid;
        initComponents();
        buttons.add(blankButton);
        buttons.add(headButton);
        buttons.add(appleButton);
        buttons.add(rockButton);
        buttons.add(portalButton);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        blankButton = new javax.swing.JButton();
        headButton = new javax.swing.JButton();
        appleButton = new javax.swing.JButton();
        rockButton = new javax.swing.JButton();
        portalButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        edgeKillsBox = new javax.swing.JCheckBox();
        sizeIncrementSpinner = new javax.swing.JSpinner();
        sizeLabel = new javax.swing.JLabel();
        initLengthLabel = new javax.swing.JLabel();
        warpModeBox = new javax.swing.JCheckBox();
        initLengthSpinner = new javax.swing.JSpinner();
        frameDelayLabel = new javax.swing.JLabel();
        saveLabel = new javax.swing.JLabel();
        currentBox = new javax.swing.JTextField();
        currentLabel = new javax.swing.JLabel();
        frameSpeedSpinner = new javax.swing.JSpinner();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Toolbox");

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jScrollPane2.setViewportView(jTextPane1);

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(290, 480));
        setPreferredSize(new java.awt.Dimension(290, 480));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TOOLBOX");
        jLabel2.setToolTipText("");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 290, 40));

        blankButton.setText("Blank");
        blankButton.setActionCommand("");
        blankButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        blankButton.setMaximumSize(new java.awt.Dimension(100, 50));
        blankButton.setMinimumSize(new java.awt.Dimension(100, 50));
        blankButton.setPreferredSize(new java.awt.Dimension(100, 50));
        add(blankButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 55));

        headButton.setText("Head");
        headButton.setActionCommand("");
        headButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        headButton.setMaximumSize(new java.awt.Dimension(100, 50));
        headButton.setMinimumSize(new java.awt.Dimension(100, 50));
        headButton.setPreferredSize(new java.awt.Dimension(100, 50));
        headButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headButtonActionPerformed(evt);
            }
        });
        add(headButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        appleButton.setText("Apple");
        appleButton.setActionCommand("");
        appleButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        appleButton.setMaximumSize(new java.awt.Dimension(100, 50));
        appleButton.setMinimumSize(new java.awt.Dimension(100, 50));
        appleButton.setPreferredSize(new java.awt.Dimension(100, 50));
        add(appleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        rockButton.setText("Rock");
        rockButton.setActionCommand("");
        rockButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rockButton.setMaximumSize(new java.awt.Dimension(100, 50));
        rockButton.setMinimumSize(new java.awt.Dimension(100, 50));
        rockButton.setPreferredSize(new java.awt.Dimension(100, 50));
        add(rockButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        portalButton.setText("Portal");
        portalButton.setActionCommand("");
        portalButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        portalButton.setMaximumSize(new java.awt.Dimension(100, 50));
        portalButton.setMinimumSize(new java.awt.Dimension(100, 50));
        portalButton.setPreferredSize(new java.awt.Dimension(100, 50));
        portalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portalButtonActionPerformed(evt);
            }
        });
        add(portalButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));

        saveButton.setText("SAVE");
        saveButton.setToolTipText("Save the current sandbox file");
        saveButton.setContentAreaFilled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 431, -1, -1));

        loadButton.setText("LOAD");
        loadButton.setToolTipText("Load a new sandbox file");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        add(loadButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(212, 431, -1, -1));

        clearButton.setText("CLEAR");
        clearButton.setToolTipText("Clear the grid");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(107, 431, -1, -1));

        edgeKillsBox.setText("Edge kills");
        edgeKillsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeKillsBoxActionPerformed(evt);
            }
        });
        add(edgeKillsBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 290, -1, -1));

        sizeIncrementSpinner.setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        sizeIncrementSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeIncrementSpinnerStateChanged(evt);
            }
        });
        add(sizeIncrementSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 200, 69, -1));

        sizeLabel.setText("Size increment");
        add(sizeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 200, -1, -1));

        initLengthLabel.setText("Initial length");
        add(initLengthLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 230, -1, -1));

        warpModeBox.setText("Extreme warp");
        warpModeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warpModeBoxActionPerformed(evt);
            }
        });
        add(warpModeBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 320, -1, -1));

        initLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        initLengthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                initLengthSpinnerStateChanged(evt);
            }
        });
        add(initLengthSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 69, -1));

        frameDelayLabel.setText("Frame delay");
        add(frameDelayLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, -1, -1));

        saveLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveLabel.setForeground(new java.awt.Color(72, 191, 58));
        saveLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveLabel.setText("SAVED");
        saveLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(saveLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 409, 63, -1));

        currentBox.setEnabled(false);
        currentBox.setMinimumSize(new java.awt.Dimension(55, 55));
        currentBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentBoxActionPerformed(evt);
            }
        });
        add(currentBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(219, 37, 55, 55));

        currentLabel.setText("Current");
        add(currentLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(228, 98, -1, -1));

        frameSpeedSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 30, 1));
        add(frameSpeedSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 170, 69, -1));
        add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 260, -1, -1));
        add(filler2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, -1, -1));

        getAccessibleContext().setAccessibleDescription("");
    }// </editor-fold>//GEN-END:initComponents

    /**
     *
     * @param index
     */
    public void setCurrentTool(int index) {
        toolNum = index;
        for (javax.swing.JButton jb : buttons) {
            jb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        }
        buttons.get(index).setBorder(javax.swing.BorderFactory.createEtchedBorder());
        System.out.println("Color: " + colorScheme[index]);
        currentBox.setBackground(Color.decode("0x" + colorScheme[index]));
    }

    public int getCurrentTool() {
        return toolNum;
    }

    public void setColorScheme(String[] colors) {
        this.colorScheme = colors;
    }

    public int getGrowBy() {
        return (int) this.sizeIncrementSpinner.getValue();
    }

    public boolean getEdgeKills() {
        return this.edgeKillsBox.isSelected();
    }


    private void headButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headButtonActionPerformed
        // TODO add your handling code here:
        setCurrentTool(1);
    }//GEN-LAST:event_headButtonActionPerformed

    private void portalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portalButtonActionPerformed
        // TODO add your handling code here:
        setCurrentTool(5);
    }//GEN-LAST:event_portalButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fChooser = new JFileChooser();
        fChooser.setAcceptAllFileFilterUsed(false);
        fChooser.setDialogTitle("Pick a .sandbox file to open");
        fChooser.setCurrentDirectory(new File("resources/"));
        fChooser.setMultiSelectionEnabled(false);
        int rVal = fChooser.showSaveDialog(new FilePicker("sandbox", "sandbox"));
        if (rVal == JFileChooser.APPROVE_OPTION) {
            String text = fChooser.getSelectedFile().getName();
            int dot = text.lastIndexOf(".");
            if (dot == -1) {
                dot = text.length() - 1;
            }
            filename.setText(text.substring(0, dot) + ".sandbox");
            dir.setText(fChooser.getCurrentDirectory().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            filename.setText("You pressed cancel");
            dir.setText("");
        } else {
            String compiledSandboxFile = Snake.compileToSandboxFile(grid.getEdgeKills(), grid.getFrameSpeed(), grid.getInitialLength(), grid.getGrowBy(), grid.getPlayArea());
            String fileLoc = "";
            try {
                fileLoc = dir.getText() + "\\" + filename.getText();

                System.out.println(fileLoc);
                BufferedWriter buffer = new BufferedWriter(new FileWriter(fileLoc));
                for (String s : compiledSandboxFile.split("\n")) {
                    buffer.write(s);
                    buffer.newLine();
                }
                buffer.close();
            } catch (Exception x) {
                System.out.println("File save incomplete to \"" + fileLoc + "\"");
            }
            this.saveLabel.setVisible(true);
        }
        repaint();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        // TODO add your handling code here:
        this.saveLabel.setVisible(false);
        JFileChooser fChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Sandbox files", "sandbox");
        fChooser.setFileFilter(filter);
        int rVal = fChooser.showOpenDialog(new FilePicker());
        if (rVal == JFileChooser.APPROVE_OPTION) {
            filename.setText(fChooser.getSelectedFile().getName());
            dir.setText(fChooser.getCurrentDirectory().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            filename.setText("You pressed cancel");
            dir.setText("");
        } else {
            String fileLoc = dir.getText() + "\\" + filename.getText();
            grid.overwrite(Snake.loadSandboxFile(new File(fileLoc)));
            updateControls();
        }
        repaint();
    }//GEN-LAST:event_loadButtonActionPerformed

    /**
     *
     */
    public void updateControls() {
        edgeKillsBox.setSelected(grid.getEdgeKills());
        this.initLengthSpinner.setValue(grid.getInitialLength());
        this.sizeIncrementSpinner.setValue(grid.getGrowBy());
        frameSpeedSpinner.setValue(grid.getFrameSpeed());
    }

    private void warpModeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warpModeBoxActionPerformed
        // TODO add your handling code here:
        grid.setExtremeStyleWarp(warpModeBox.isSelected());
    }//GEN-LAST:event_warpModeBoxActionPerformed

    private void currentBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currentBoxActionPerformed

    private void sizeIncrementSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeIncrementSpinnerStateChanged
        // TODO add your handling code here:
        grid.setGrowBy((int) sizeIncrementSpinner.getValue());
    }//GEN-LAST:event_sizeIncrementSpinnerStateChanged

    private void edgeKillsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeKillsBoxActionPerformed
        // TODO add your handling code here:
        grid.setEdgeKills(edgeKillsBox.isSelected());
    }//GEN-LAST:event_edgeKillsBoxActionPerformed

    private void initLengthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_initLengthSpinnerStateChanged
        // TODO add your handling code here:
        grid.setInitialSize((int) this.initLengthSpinner.getValue());
    }//GEN-LAST:event_initLengthSpinnerStateChanged

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        // TODO add your handling code here:
        grid.clear();
        repaint();
    }//GEN-LAST:event_clearButtonActionPerformed

    private java.util.ArrayList<javax.swing.JButton> buttons = new java.util.ArrayList<>();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appleButton;
    private javax.swing.JButton blankButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField currentBox;
    private javax.swing.JLabel currentLabel;
    private javax.swing.JCheckBox edgeKillsBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel frameDelayLabel;
    private javax.swing.JSpinner frameSpeedSpinner;
    private javax.swing.JButton headButton;
    private javax.swing.JLabel initLengthLabel;
    private javax.swing.JSpinner initLengthSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton portalButton;
    private javax.swing.JButton rockButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel saveLabel;
    private javax.swing.JSpinner sizeIncrementSpinner;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JCheckBox warpModeBox;
    // End of variables declaration//GEN-END:variables
}
