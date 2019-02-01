package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Checkbox;
import java.awt.Container;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class AWTToolbox extends JFrame implements ActionListener {

    /**
     * Height of the game frame.
     */
    private final int HEIGHT;
    /**
     * Width of the game frame.
     */
    private final int WIDTH;

    private static final int TOOLX = 10;

    private static final int TOOLY = 10;

    private static final int TOOLW = 100;

    private static final int TOOLH = 30;

    private static final int TOOLYSPACE = 15;

    private static int toolNum = 0;

    private final int XPOS;
    private final int YPOS;

    /**
     * The main panel
     */
    private JPanel panel;

    private ArrayList<JButton> tools;

    private final int BUTTONY = 375;
    private final int BUTTONX = 10;
    private final int BUTTONXSPACE = 30;
    private final int BUTTONW = 75;
    private final int BUTTONH = 30;

    private JTextField filename = new JTextField();
    private JTextField dir = new JTextField();

    private JButton saveButton;

    private JButton currentTool;
    private JLabel currentToolLabel;

    private JButton loadButton;

    private JLabel savedMsg;

    private JSpinner growBySpinner;
    private JLabel growByLabel;

    private JSpinner initialSizeSpinner;
    private JLabel initialSizeLabel;

    private JSpinner frameSpeedSpinner;
    private JLabel frameSpeedLabel;

    private Checkbox edgeKillsBox;

    private Grid grid;

    /**
     * The coordinates of the tools
     */
    private Point[] toolCoords;

    public AWTToolbox(String title, int width, int height, int xPos, int yPos, ArrayList<String> toolColors, ArrayList<String> toolNames, Grid grid) {
        this.grid = grid;
        Image icon = Toolkit.getDefaultToolkit().getImage("resources/art/icon16.jpg");
        this.setIconImage(icon);
        GraphicsConfiguration gc = this.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Dimension size = this.getPreferredSize();
        this.setLocation(xPos, yPos);
        this.setTitle(title);

        XPOS = xPos;
        YPOS = yPos;
        WIDTH = width;
        HEIGHT = height;
        tools = new ArrayList<>();
        for (int i = 0; i < toolNames.size(); i++) {
            //System.out.println(i + " " + toolNames.get(i) + " " + hexToColor(toolColors.get(i)));
            tools.add(new JButton(toolNames.get(i)));
            tools.get(tools.size() - 1).setBackground(hexToColor(toolColors.get(i)));
        }

        initDisplay();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        repaint();
    }

    public Color hexToColor(String hex) {
        float[] HSBvals = new float[3];
        int r = Integer.valueOf(hex.substring(0, 2), 16);
        int g = Integer.valueOf(hex.substring(2, 4), 16);
        int b = Integer.valueOf(hex.substring(4, 6), 16);
        Color.RGBtoHSB(r, g, b, HSBvals);
        return Color.getHSBColor(HSBvals[0], HSBvals[1], HSBvals[2]);
    }

    /**
     * Draw the display (cards and messages).
     */
    @Override
    public void repaint() {
        panel.setFocusable(true);
        pack();
        panel.repaint();
    }

    public int getGrowBy() {
        return (int) growBySpinner.getValue();
    }

    public boolean getEdgeKills() {
        return edgeKillsBox.getState();
    }

    /**
     * Initialize the display.
     */
    private void initDisplay() {
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };

        panel.setLayout(null);
        panel.setBounds(XPOS, YPOS, WIDTH, HEIGHT);
        this.setResizable(false);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setFocusable(true);
        panel.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent ke) {
            }

            public void keyPressed(KeyEvent ke) {
                int keyCode = ke.getKeyCode();
                if (keyCode == KeyEvent.VK_H) {

                }
            }

            public void keyReleased(KeyEvent ke) {
            }
        });
        panel.requestFocus();

        for (int i = 0; i < tools.size(); i++) {
            // add JButtons
            tools.get(i).setBounds(TOOLX, TOOLY + (i * (TOOLH + TOOLYSPACE)), TOOLW, TOOLH);
            tools.get(i).addActionListener(this);
            panel.add(tools.get(i));
            tools.get(i).setVisible(true);
        }

        growBySpinner = new JSpinner();
        growBySpinner.setName("Grow amount");
        growBySpinner.setValue(1);
        growBySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (int) growBySpinner.getValue();
                //if (val >= 1) {
                grid.setGrowBy((int) growBySpinner.getValue());
                //}
            }
        });
        growBySpinner.setBounds(TOOLX, (int) (TOOLY + ((TOOLH + TOOLYSPACE) * 5.5)), TOOLW / 2, 20);
        panel.add(growBySpinner);

        growByLabel = new JLabel("Grow amt");
        growByLabel.setFont(new Font("sansserif", 0, 13));
        growByLabel.setForeground(Color.DARK_GRAY);
        growByLabel.setVisible(true);
        growByLabel.setBounds(growBySpinner.getX() + growBySpinner.getWidth() + 2, growBySpinner.getY() - 5, 75, 30);
        panel.add(growByLabel);

        frameSpeedSpinner = new JSpinner();
        frameSpeedSpinner.setName("Frame wait");
        frameSpeedSpinner.setValue(1);
        frameSpeedSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (int) frameSpeedSpinner.getValue();
                if (val >= 1) {
                    grid.setSandboxFrameSpeed(val);
                } else {
                    frameSpeedSpinner.setValue(1);
                }

            }
        });
        frameSpeedSpinner.setBounds(TOOLX, (int) (TOOLY + ((TOOLH + TOOLYSPACE) * 6.5)), TOOLW / 2, 20);
        panel.add(frameSpeedSpinner);

        frameSpeedLabel = new JLabel("Frame wait");
        frameSpeedLabel.setFont(new Font("sansserif", 0, 13));
        frameSpeedLabel.setForeground(Color.DARK_GRAY);
        frameSpeedLabel.setVisible(true);
        frameSpeedLabel.setBounds(growBySpinner.getX() + frameSpeedSpinner.getWidth() + 2, frameSpeedSpinner.getY() - 5, 100, 30);
        panel.add(frameSpeedLabel);

        initialSizeSpinner = new JSpinner();
        initialSizeSpinner.setName("Initial size");
        initialSizeSpinner.setValue(5);
        initialSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (int) initialSizeSpinner.getValue();
                if (val >= 1) {
                    grid.setInitialSize((int) initialSizeSpinner.getValue());
                } else {
                    initialSizeSpinner.setValue(1);
                }
            }
        });
        initialSizeSpinner.setBounds(TOOLX, (int) (TOOLY + ((TOOLH + TOOLYSPACE) * 6)), TOOLW / 2, 20);
        panel.add(initialSizeSpinner);

        initialSizeLabel = new JLabel("Initial size");
        initialSizeLabel.setFont(new Font("sansserif", 0, 13));
        initialSizeLabel.setForeground(Color.DARK_GRAY);
        initialSizeLabel.setVisible(true);
        initialSizeLabel.setBounds(initialSizeSpinner.getX() + initialSizeSpinner.getWidth() + 2, initialSizeSpinner.getY() - 5, 75, 30);
        panel.add(initialSizeLabel);

        edgeKillsBox = new Checkbox("Edge Kills");
        edgeKillsBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                //statusLabel.setText("Mango Checkbox: " +   (e.getStateChange() == 1 ? "checked" : "unchecked"));
                grid.setEdgeKills(e.getStateChange() == 1 ? true : false);
            }
        });
        edgeKillsBox.setBounds(TOOLX, TOOLY + ((TOOLH + TOOLYSPACE) * 5), 20, 20);
        edgeKillsBox.setSize(TOOLW, 20);
        panel.add(edgeKillsBox);
        edgeKillsBox.setVisible(true);

        currentTool = new JButton();
        currentTool.setText("");
        currentTool.setBounds(TOOLX + TOOLW + 30, TOOLY, 50, 50);
        currentTool.setEnabled(false);
        panel.add(currentTool);
        currentTool.setVisible(rootPaneCheckingEnabled);

        currentToolLabel = new JLabel("Current tool");
        currentToolLabel.setFont(new Font("sansserif", 0, 13));
        currentToolLabel.setForeground(Color.DARK_GRAY);
        currentToolLabel.setVisible(true);
        currentToolLabel.setBounds(currentTool.getX() - 15, currentTool.getY() + currentTool.getHeight(), 100, 30);
        panel.add(currentToolLabel);

        saveButton = new JButton();
        saveButton.setText("SAVE");
        panel.add(saveButton);
        saveButton.setBounds(BUTTONX, BUTTONY, BUTTONW, BUTTONH);
        saveButton.setDefaultCapable(true);
        saveButton.addActionListener(this);
        saveButton.setVisible(true);

        loadButton = new JButton();
        loadButton.setText("LOAD");
        panel.add(loadButton);
        loadButton.setBounds(BUTTONX + BUTTONW + BUTTONXSPACE, BUTTONY, BUTTONW, BUTTONH);
        loadButton.addActionListener(this);
        loadButton.setVisible(true);

        savedMsg = new JLabel("Saved!");
        savedMsg.setFont(new Font("Sansserif", 0, 20));
        savedMsg.setForeground(Color.blue);
        savedMsg.setVisible(false);
        panel.add(savedMsg);
        savedMsg.setBounds(BUTTONX + 8, BUTTONY - 30, 250, 30);
        pack();
        getContentPane().add(panel);
        getRootPane().setDefaultButton(saveButton);
        panel.setVisible(true);
    }

    /**
     * Deal with the user clicking on something other than a button or a card.
     */
    private void signalError() {
        Toolkit t = panel.getToolkit();
        t.beep();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(saveButton)) {
            this.savedMsg.setVisible(true);
            repaint();
        } else if (e.getSource().equals(loadButton)) {
            this.savedMsg.setVisible(false);
            JFileChooser fChooser = new JFileChooser();
            // Demonstrate "Open" dialog:
            int rVal = fChooser.showOpenDialog(new FilePicker());
            if (rVal == JFileChooser.APPROVE_OPTION) {
                filename.setText(fChooser.getSelectedFile().getName());
                dir.setText(fChooser.getCurrentDirectory().toString());
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                filename.setText("You pressed cancel");
                dir.setText("");
            }
            repaint();
        } else {
            for (int k = 0; k < tools.size(); k++) {
                if (e.getSource().equals(tools.get(k))) {
                    setCurrentTool(k);
                    System.out.println("Current tool: " + tools.get(k).getText());
                    repaint();
                }
            }
            signalError();
        }
    }

    public void setCurrentTool(int index) {
        toolNum = index;
        currentTool.setBackground(tools.get(index).getBackground());
    }

    public int getCurrentTool() {
        this.savedMsg.setVisible(false);
        return toolNum;
    }

    class OpenListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser fChooser = new JFileChooser();
            // Demonstrate "Open" dialog:
            int rVal = fChooser.showOpenDialog(new FilePicker());
            if (rVal == JFileChooser.APPROVE_OPTION) {
                filename.setText(fChooser.getSelectedFile().getName());
                dir.setText(fChooser.getCurrentDirectory().toString());
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                filename.setText("You pressed cancel");
                dir.setText("");
            }
        }
    }
}
