package snake;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Log class that keeps track of all important events that happen during
 * gameplay
 *
 * @author Tim Barber
 */
public class Logger {

    private ArrayList<Loggable> objs;
    private String log = "JSnake Log - " + formatDateTime() + "\n\n";
    private int logs = 1;

    /**
     * Default
     */
    public Logger() {
        objs = new ArrayList<>();
    }

    /**
     * Preset list
     *
     * @param objList List of Loggable types
     */
    public Logger(ArrayList<Loggable> objList) {
        objs = objList;
    }

    /**
     * Init with one Loggable
     *
     * @param l Loggable class
     */
    public Logger(Loggable l) {
        objs = new ArrayList<>();
        objs.add(l);
    }

    /**
     * Adds a Loggable class/obj to the list
     *
     * @param l Loggable object
     */
    public void add(Loggable l) {
        objs.add(l);
    }

    /**
     * Returns a string of length two containing the input int
     *
     * @param x integer to convert
     * @return String of length 2 properly formatted
     */
    public static String twoDigit(int x) {
        String o = String.valueOf(x);
        if (o.length() == 1) {
            o = "0" + o;
        }
        return o.substring(0, 2);
    }

    /**
     * Returns a string of length len containing the input int
     *
     * @param x   integer to convert
     * @param len desired length of output string
     * @return String of length len properly formatted
     */
    public static String xDigit(int x, int len) {
        String o = String.valueOf(x);
        while (o.length() < len) {
            o = "0" + o;
        }
        return o.substring(0, len);
    }

    /**
     * Formats the date and time nicely
     *
     * @return The date and time as a String in a consistent format
     */
    public String formatDateTime() {
        return twoDigit(LocalDateTime.now().getMonth().getValue()) + "/"
                + twoDigit(LocalDateTime.now().getDayOfMonth()) + "/"
                + twoDigit(LocalDateTime.now().getYear()) + " "
                + twoDigit(LocalDateTime.now().getHour()) + ":"
                + twoDigit(LocalDateTime.now().getMinute()) + ":"
                + twoDigit(LocalDateTime.now().getSecond()) + "."
                + xDigit(LocalDateTime.now().getNano(), 3);
    }

    /**
     * Iterates the log
     */
    public void logState() {
        log += "Log " + logs + " - " + formatDateTime() + "\n";
        for (Loggable l : objs) {
            log += l.getClass().getSimpleName() + " : " + l.getState().replaceAll("\n", "  /  ") + "\n";
        }
        log += "-----\n";
        logs++;
    }

    /*
     * Ends the logging process and grabs all the events from the classes
     */
    private void endLog() {
        log += "Final log (" + logs + ") of all events - " + formatDateTime() + "\n";
        logs++;
        for (Loggable l : objs) {
            log += l.getClass().getSimpleName() + " events : " + l.getEvents().replaceAll("\n", "  /  ") + "\n";
        }
        log += "End of log.";
    }

    /**
     * Gives the current state of the log String
     *
     * @return the current log string
     */
    public String getLog() {
        return this.log;
    }

    /**
     * Saves the log string to a file (destination)
     *
     * @param destination The directory and filename of the new file
     */
    public void saveLogFile(String destination) {
        endLog();

        try {
            File logFolder = null;
            if (destination.contains("\\")) {
                logFolder = new File(destination.substring(0, destination.lastIndexOf("\\")));
            } else {
                logFolder = new File(destination.substring(0, destination.lastIndexOf("/")));
            }
            if (!logFolder.exists()) {
                logFolder.mkdir();
            }
            FileWriter creator;
            try (PrintWriter printer = new PrintWriter(destination, "UTF-8")) {
                creator = new FileWriter(new File(destination));
                for (String s : log.split("\n")) {
                    printer.print(s);
                    printer.println();
                }
            }
            creator.close();
        } catch (IOException x) {
            System.out.println("Error saving log file to \"" + destination + "\" :: " + x.getLocalizedMessage() + " | saving to clipboard\n" + log);
            StringSelection tmpSel = new StringSelection(log);
            Clipboard tmpClp = Toolkit.getDefaultToolkit().getSystemClipboard();
            tmpClp.setContents(tmpSel, null);
        }
    }

    @Override
    public String toString() {
        return objs.toString();
    }
}


/*
 * The MIT License
 *
 * Copyright (c) 2019 Tim Barber.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
