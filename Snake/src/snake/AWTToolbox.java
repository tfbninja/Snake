package snake;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.input.KeyCode;

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

    private final int BUTTONY = 325;
    private final int BUTTONX = 10;
    private final int BUTTONXSPACE = 10;
    private final int BUTTONW = 75;
    private final int BUTTONH = 30;

    private JButton saveButton;

    private JButton currentTool;

    private JButton loadButton;

    private JLabel savedMsg;

    /**
     * The coordinates of the tools
     */
    private Point[] toolCoords;

    public AWTToolbox(String title, int width, int height, int xPos, int yPos, ArrayList<String> toolColors, ArrayList<String> toolNames) {
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
        panel.setBounds(XPOS, YPOS, WIDTH - 20, HEIGHT - 20);
        panel.setPreferredSize(new Dimension(WIDTH - 20, HEIGHT - 20));
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

        currentTool = new JButton();
        currentTool.setText("");
        currentTool.setBounds(TOOLX + TOOLW + 10, TOOLY, 50, 50);
        panel.add(currentTool);
        currentTool.setVisible(rootPaneCheckingEnabled);

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
        savedMsg.setVisible(true);
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

    /**
     * Respond to a button click (on either the "Replace" button or the
     * "Restart" button).
     *
     * @param e the button click action event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(saveButton)) {
            repaint();
        } else if (e.getSource().equals(loadButton)) {
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
        return toolNum;
    }
}
