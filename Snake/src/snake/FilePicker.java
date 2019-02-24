package snake;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Timothy
 *
 * Majority of code in this class taken from
 * http://www.java2s.com/Code/Java/Swing-JFC/DemonstrationofFiledialogboxes.htm
 */
public class FilePicker extends JFrame {

    private JTextField filename = new JTextField();
    private JTextField dir = new JTextField();

    private JButton open = new JButton("Open");
    private JButton save = new JButton("Save");

    /**
     *
     */
    public FilePicker() {
        JPanel panel = new JPanel();
        open.addActionListener(new OpenListener());
        panel.add(open);
        save.addActionListener(new SaveListener());
        panel.add(save);
        Container cp = getContentPane();
        cp.add(panel, BorderLayout.SOUTH);
        dir.setEditable(false);
        filename.setText("sandbox");
        filename.setEditable(false);
        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(filename);
        panel.add(dir);
        cp.add(panel, BorderLayout.NORTH);
    }

    /**
     *
     * @param initialName
     * @param type
     */
    public FilePicker(String initialName, String type) {
        JPanel panel = new JPanel();
        open.addActionListener(new OpenListener());
        panel.add(open);
        save.addActionListener(new SaveListener());
        panel.add(save);
        Container cp = getContentPane();
        cp.add(panel, BorderLayout.SOUTH);
        dir.setEditable(false);
        filename.setText(initialName + "." + type);
        filename.setEditable(false);
        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(filename);
        panel.add(dir);
        cp.add(panel, BorderLayout.NORTH);
    }

    class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fChooser = new JFileChooser();
            // Demonstrate "Open" dialog:
            int rVal = fChooser.showOpenDialog(FilePicker.this);
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

    class SaveListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser fChooser = new JFileChooser();
            // Demonstrate "Save" dialog:
            int rVal = fChooser.showSaveDialog(FilePicker.this);
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

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        run(new FilePicker(), 250, 110);
    }

    /**
     *
     * @param frame
     * @param width
     * @param height
     */
    public static void run(JFrame frame, int width, int height) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
    }
}
