package snake;

import java.awt.Color;
import static java.awt.image.ImageObserver.WIDTH;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * V2.0
 *
 * @author Tim Barber
 */
public class ToolPanel extends javax.swing.JPanel implements Updateable {

    private JTextField filename = new JTextField();
    private JTextField dir = new JTextField();
    private int toolNum = 0;
    private String[] colorScheme;
    private Grid grid;
    private GameState GS;
    private final MenuManager MM;
    private Board board;

    /**
     * Creates new form ToolPanel
     *
     * @param colorScheme The button colors
     * @param grid        The grid to control
     * @param mm
     * @param b
     * @param gs
     * @param x
     * @param y
     */
    public ToolPanel(String[] colorScheme, Grid grid, MenuManager mm, Board b, GameState gs, int x, int y) {
        this.grid = grid;
        initComponents();
        updateControls();
        GS = gs;
        buttons.add(blankButton);
        buttons.add(headButton);
        buttons.add(appleButton);
        buttons.add(rockButton);
        buttons.add(portalButton);
        components.add(currentLabel);
        components.add(edgeKillsBox);
        components.add(frameDelayLabel);
        components.add(frameSpeedSpinner);
        components.add(initLengthLabel);
        components.add(initLengthSpinner);
        components.add(sizeIncrementSpinner);
        components.add(sizeLabel);
        components.add(warpModeBox);
        components.add(seedSpinner);
        components.add(keepSeedBox);
        components.add(seedLabel);
        updateButtonColors(colorScheme);
        MM = mm;
        board = b;
        saveLabel.setVisible(false);
    }

    /**
     *
     * @param grid
     */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * Sets all of the buttons to the raised border value
     */
    public void raiseAllButtons() {
        buttons.forEach((jb) -> {
            jb.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        });
        loadButton.setContentAreaFilled(true);
        saveButton.setContentAreaFilled(true);
        clearButton.setContentAreaFilled(true);
    }

    /**
     * Sets the grid to the proper values and repaints
     */
    public void update() {
        updateGridSettings();
        String[] names = {"RESET", "CLEAR"};
        if (GS.isGame()) {
            clearButton.setText(names[0]);
        } else {
            clearButton.setText(names[1]);
        }
        repaint();
    }

    /**
     * Updates the grid to match the on-screen values
     */
    public void updateGridSettings() {
        if (grid.getDiffLevel() == 0) {
            grid.setFrameSpeed((int) frameSpeedSpinner.getValue());
            grid.setEdgeKills(edgeKillsBox.isSelected());
            grid.setSeed((long) seedSpinner.getValue());
            grid.setUseSameSeed(keepSeedBox.isSelected());
            grid.setExtremeStyleWarp(warpModeBox.isSelected());
            grid.setGrowBy((int) sizeIncrementSpinner.getValue());
            grid.setInitialSize((int) initLengthSpinner.getValue());
        }
    }

    /**
     *
     * @param c
     * @return
     */
    public Color invert(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        r = 255 - r;
        g = 255 - g;
        b = 255 - b;
        return new Color(r, g, b);
    }

    /**
     *
     * @param colorScheme
     */
    public void updateButtonColors(String[] colorScheme) {
        Color bg = Color.decode("0x" + colorScheme[colorScheme.length - 1]);
        this.setBackground(bg);
        this.keepSeedBox.setBackground(bg);
        this.warpModeBox.setBackground(bg);
        this.colorScheme = colorScheme;
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackground(Color.decode("0x" + colorScheme[i]));
            buttons.get(i).setForeground(invert(bg));
        }
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getClass().getName().toLowerCase().contains("label")) {
                components.get(i).setForeground(invert(bg));
            } else {
                components.get(i).setBackground(bg);
                components.get(i).setForeground(invert(bg));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
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
        keepSeedBox = new javax.swing.JCheckBox();
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
        seedLabel = new javax.swing.JLabel();
        seedSpinner = new javax.swing.JSpinner();
        edgeKillsBox = new javax.swing.JCheckBox();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Toolbox");

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jScrollPane1.setViewportView(jList1);

        jScrollPane2.setViewportView(jTextPane1);

        setBackground(new java.awt.Color(238, 238, 238));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(290, 460));
        setPreferredSize(new java.awt.Dimension(290, 460));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(20, 20, 20));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TOOLBOX");
        jLabel2.setToolTipText("");

        blankButton.setText("Blank");
        blankButton.setActionCommand("");
        blankButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        blankButton.setMaximumSize(new java.awt.Dimension(100, 50));
        blankButton.setMinimumSize(new java.awt.Dimension(100, 50));
        blankButton.setPreferredSize(new java.awt.Dimension(100, 50));
        blankButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blankButtonActionPerformed(evt);
            }
        });

        headButton.setText("Head");
        headButton.setActionCommand("");
        headButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        headButton.setMaximumSize(new java.awt.Dimension(100, 50));
        headButton.setMinimumSize(new java.awt.Dimension(100, 50));
        headButton.setPreferredSize(new java.awt.Dimension(100, 50));
        headButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headButtonActionPerformed(evt);
            }
        });

        appleButton.setText("Apple");
        appleButton.setActionCommand("");
        appleButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        appleButton.setMaximumSize(new java.awt.Dimension(100, 50));
        appleButton.setMinimumSize(new java.awt.Dimension(100, 50));
        appleButton.setPreferredSize(new java.awt.Dimension(100, 50));
        appleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appleButtonActionPerformed(evt);
            }
        });

        rockButton.setText("Rock");
        rockButton.setActionCommand("");
        rockButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rockButton.setMaximumSize(new java.awt.Dimension(100, 50));
        rockButton.setMinimumSize(new java.awt.Dimension(100, 50));
        rockButton.setPreferredSize(new java.awt.Dimension(100, 50));
        rockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rockButtonActionPerformed(evt);
            }
        });

        portalButton.setText("Portal");
        portalButton.setActionCommand("");
        portalButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        portalButton.setMaximumSize(new java.awt.Dimension(100, 50));
        portalButton.setMinimumSize(new java.awt.Dimension(100, 50));
        portalButton.setPreferredSize(new java.awt.Dimension(100, 50));
        portalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portalButtonActionPerformed(evt);
            }
        });

        saveButton.setText("SAVE");
        saveButton.setToolTipText("Save the current sandbox file");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        loadButton.setText("LOAD");
        loadButton.setToolTipText("Load a new sandbox file");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        clearButton.setText("CLEAR");
        clearButton.setToolTipText("Clear the grid");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        keepSeedBox.setText("Keep seed");
        keepSeedBox.setSelected(grid.getUseSameSeed());
        keepSeedBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keepSeedBoxActionPerformed(evt);
            }
        });

        sizeIncrementSpinner.setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        sizeIncrementSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeIncrementSpinnerStateChanged(evt);
            }
        });

        sizeLabel.setText("Size increment");

        initLengthLabel.setText("Initial length");

        warpModeBox.setText("Extreme warp");
        warpModeBox.setSelected(grid.getExtremeWarp());
        warpModeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warpModeBoxActionPerformed(evt);
            }
        });

        initLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(5, 1, null, 1));
        initLengthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                initLengthSpinnerStateChanged(evt);
            }
        });

        frameDelayLabel.setText("Frame delay");

        saveLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveLabel.setForeground(new java.awt.Color(72, 191, 58));
        saveLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveLabel.setText("SAVED");
        saveLabel.setFocusable(false);
        saveLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        currentBox.setEnabled(false);
        currentBox.setMinimumSize(new java.awt.Dimension(55, 55));
        currentBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentBoxActionPerformed(evt);
            }
        });

        currentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentLabel.setText("Current");

        frameSpeedSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 1, 30, 1));

        seedLabel.setText("Seed");

        seedSpinner.setModel(new javax.swing.SpinnerNumberModel(0L, null, null, 1000L));
        seedSpinner.setValue((long) grid.getSeed());
        seedSpinner.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                seedSpinnerPropertyChange(evt);
            }
        });

        edgeKillsBox.setText("Edge kills");
        edgeKillsBox.setSelected(grid.getEdgeKills());
        edgeKillsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeKillsBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(219, 219, 219)
                                .addComponent(currentBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(blankButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(headButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(seedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(16, 16, 16)
                                                .addComponent(currentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(seedLabel)))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(appleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(frameSpeedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sizeIncrementSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(frameDelayLabel)
                                        .addComponent(sizeLabel)))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(80, 80, 80)
                                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(rockButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(50, 50, 50)
                                                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(initLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(keepSeedBox))
                                .addGap(3, 3, 3)
                                .addComponent(initLengthLabel))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(portalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(edgeKillsBox)
                                        .addComponent(warpModeBox)))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(saveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(saveButton)
                                .addGap(32, 32, 32)
                                .addComponent(clearButton)
                                .addGap(37, 37, 37)
                                .addComponent(loadButton))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(currentBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(50, 50, 50)
                                                .addComponent(blankButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(headButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(40, 40, 40)
                                                .addComponent(seedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(currentLabel)
                                                .addGap(25, 25, 25)
                                                .addComponent(seedLabel)))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(appleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(frameSpeedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(sizeIncrementSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(frameDelayLabel)
                                                .addGap(15, 15, 15)
                                                .addComponent(sizeLabel)))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rockButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(initLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(7, 7, 7)
                                                .addComponent(keepSeedBox))
                                        .addComponent(initLengthLabel)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(30, 30, 30)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(portalButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(edgeKillsBox)
                                                .addGap(4, 4, 4)
                                                .addComponent(warpModeBox)))
                                .addGap(60, 60, 60)
                                .addComponent(saveLabel)
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(saveButton)
                                        .addComponent(clearButton)
                                        .addComponent(loadButton)))
        );

        getAccessibleContext().setAccessibleDescription("");
    }// </editor-fold>

    /**
     *
     * @param index
     */
    public void setCurrentTool(int index) {
        raiseAllButtons();
        buttons.get(index).setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        switch (index) {
            case 0:
                toolNum = 0;
                break;
            case 1:
                toolNum = 1;
                break;
            case 2:
                toolNum = 3;
                break;
            case 3:
                toolNum = 4;
                break;
            case 4:
                toolNum = 5;
                break;
        }
        currentBox.setBackground(Color.decode("0x" + colorScheme[index]));
        raiseAllButtons();
    }

    /**
     *
     * @return
     */
    public int getCurrentTool() {
        return toolNum;
    }

    /**
     *
     * @param colors
     */
    public void setColorScheme(String[] colors) {
        this.colorScheme = colors;
    }

    /**
     *
     * @return
     */
    public int getGrowBy() {
        return (int) this.sizeIncrementSpinner.getValue();
    }

    /**
     *
     * @return
     */
    public boolean getEdgeKills() {
        return this.keepSeedBox.isSelected();
    }

    private void headButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setCurrentTool(1);
    }

    private void portalButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setCurrentTool(4);
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        raiseAllButtons();
        saveButton.setContentAreaFilled(false);
        JFileChooser fChooser = new JFileChooser();
        fChooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".sandbox");
            }

            @Override
            public String getDescription() {
                return "Sandbox file";
            }
        });
        fChooser.setAcceptAllFileFilterUsed(false);
        fChooser.setDialogTitle("Save as a .sandbox file");
        fChooser.setCurrentDirectory(new File("resources/"));
        fChooser.setMultiSelectionEnabled(false);
        int rVal = fChooser.showSaveDialog(new FilePicker("sandbox", "sandbox"));
        if (rVal == JFileChooser.APPROVE_OPTION) {
            String text = fChooser.getSelectedFile().getName();
            int dot = text.lastIndexOf(".");
            if (dot == -1) {
                dot = text.length();
            }
            filename.setText(text.substring(0, dot) + ".sandbox");
            dir.setText(fChooser.getCurrentDirectory().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            filename.setText("You pressed cancel");
            dir.setText("");
        } else {
            String compiledSandboxFile = Snake.compileToSandboxFile(grid.getEdgeKills(), grid.getFrameSpeed(), grid.getInitialLength(), grid.getGrowBy(), grid.getPlayArea(), grid.getExtremeWarp(), grid.getUseSameSeed(), grid.getSeed());
            String fileLoc = "";
            try {
                fileLoc = dir.getText() + "\\" + filename.getText();

                try (BufferedWriter buffer = new BufferedWriter(new FileWriter(fileLoc))) {
                    for (String s : compiledSandboxFile.split("\n")) {
                        buffer.write(s);
                        buffer.newLine();
                    }
                }
            } catch (IOException x) {
                System.out.println("File save incomplete to \"" + fileLoc + "\"");
            }
            this.saveLabel.setVisible(true);
        }
        saveButton.setContentAreaFilled(true);
        repaint();
    }

    /**
     * Hides the text above the saveButton that says "SAVED"
     */
    public void hideSaved() {
        saveLabel.setVisible(false);
    }

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {
        raiseAllButtons();
        loadButton.setContentAreaFilled(false);
        this.saveLabel.setVisible(false);
        JFileChooser fChooser = new JFileChooser();
        fChooser.setCurrentDirectory(new File("resources/"));
        fChooser.setDialogTitle("Load a .sandbox file");
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
        loadButton.setContentAreaFilled(true);
        repaint();
    }

    /**
     * Updates the on-screen controls to match the grid settings
     */
    public void updateControls() {
        edgeKillsBox.setSelected(grid.getEdgeKills());
        this.initLengthSpinner.setValue(grid.getInitialLength());
        this.sizeIncrementSpinner.setValue(grid.getGrowBy());
        this.warpModeBox.setSelected(grid.getExtremeWarp());
        frameSpeedSpinner.setValue(grid.getFrameSpeed());
        keepSeedBox.setSelected(grid.getUseSameSeed());
        seedSpinner.setValue((long) grid.getSeed());
    }

    private void warpModeBoxActionPerformed(java.awt.event.ActionEvent evt) {
        grid.setExtremeStyleWarp(warpModeBox.isSelected());
        hideSaved();
    }

    private void currentBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void sizeIncrementSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        grid.setGrowBy((int) sizeIncrementSpinner.getValue());
        hideSaved();
    }

    private void keepSeedBoxActionPerformed(java.awt.event.ActionEvent evt) {
        grid.setUseSameSeed(keepSeedBox.isSelected());
    }

    private void initLengthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        grid.setInitialSize((int) this.initLengthSpinner.getValue());
        hideSaved();
    }

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
        raiseAllButtons();
        clearButton.setContentAreaFilled(false);
        hideSaved();
        grid.reset();
        grid.clear();
        board.drawBlocks();
        clearButton.setContentAreaFilled(true);
        repaint();
    }

    private void blankButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setCurrentTool(0);
    }

    private void appleButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setCurrentTool(2);
    }

    private void rockButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setCurrentTool(3);
    }

    private void edgeKillsBoxActionPerformed(java.awt.event.ActionEvent evt) {
        grid.setEdgeKills(edgeKillsBox.isSelected());
        hideSaved();
    }

    private void seedSpinnerPropertyChange(java.beans.PropertyChangeEvent evt) {
        grid.setSeed(WIDTH);
        grid.setUseSameSeed(keepSeedBox.isSelected());
    }

    private final java.util.ArrayList<JButton> buttons = new java.util.ArrayList<>();
    private final java.util.ArrayList<javax.swing.JComponent> components = new java.util.ArrayList<>();
    // Variables declaration - do not modify
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
    private javax.swing.JCheckBox keepSeedBox;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton portalButton;
    private javax.swing.JButton rockButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel saveLabel;
    private javax.swing.JLabel seedLabel;
    private javax.swing.JSpinner seedSpinner;
    private javax.swing.JSpinner sizeIncrementSpinner;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JCheckBox warpModeBox;
    // End of variables declaration
}
