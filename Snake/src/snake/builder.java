package snake;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Timothy
 */
public class builder {

    private static final String RSRCLOC = "Snake\\src\\snake\\resources";
    private static final String EXEC = "Snake\\dist\\Snake.jar";
    private static final String EXECLOC = "Snake\\dist\\";
    private static final String TARGETLOC = "Executable JAR\\";

    public static void main(String[] args) {
        System.out.println(copyFile(RSRCLOC, EXECLOC));
        System.out.println(copyFile(EXEC, TARGETLOC));
        System.out.println(copyFile(RSRCLOC, TARGETLOC));
    }

    /**
     *
     * @param srcName
     * @param destName
     * @return
     */
    public static boolean copyFile(String srcName, String destName) {
        try {
            File src = new File(srcName);
            File dest = new File(destName);
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                File src = new File(srcName);
                File dest = new File(destName);
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException b) {
                System.out.println(b.getLocalizedMessage());
                return false;
            }
            return false;
        }
        return true;
    }

    /**
     *
     * @param filename
     * @return
     */
    public static boolean checkFileExists(String filename) {
        try {
            Scanner temp = new Scanner(new File(filename));
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     *
     * @param filename
     * @param score
     */
    public static void writeToFile(String filename, String text) {
        try {
            FileWriter creator;
            try (PrintWriter printer = new PrintWriter(filename, "UTF-8")) {
                creator = new FileWriter(new File(filename));
                printer.print(text);
            }
            creator.close();
        } catch (IOException x) {
            System.out.println(x.getLocalizedMessage() + " oof.");
        }
    }
}
