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
 *
 * @author Tim Barber
 */
public class Logger {

    private ArrayList<Loggable> objs;
    private String log = "JSnake Log - " + formatDateTime() + "\n\n";
    private int logs = 1;

    public Logger() {
        objs = new ArrayList<>();
    }

    public Logger(ArrayList<Loggable> objList) {
        objs = objList;
    }

    public Logger(Loggable l) {
        objs = new ArrayList<>();
        objs.add(l);
    }

    public void add(Loggable l) {
        objs.add(l);
    }

    public String twoDigit(int x) {
        String o = String.valueOf(x);
        if (o.length() == 1) {
            o = "0" + o;
        }
        return o.substring(0, 2);
    }

    public String xDigit(int x, int len) {
        String o = String.valueOf(x);
        while (o.length() < len) {
            o = "0" + o;
        }
        return o.substring(0, len);
    }

    public String formatDateTime() {
        return LocalDateTime.now().getMonth().getValue() + "/"
                + LocalDateTime.now().getDayOfMonth() + "/"
                + LocalDateTime.now().getYear() + " "
                + twoDigit(LocalDateTime.now().getHour()) + ":"
                + twoDigit(LocalDateTime.now().getMinute()) + ":"
                + twoDigit(LocalDateTime.now().getSecond()) + "."
                + xDigit(LocalDateTime.now().getNano(), 3);
    }

    public void logState() {
        log += "Log " + logs + " - " + formatDateTime() + "\n";
        for (Loggable l : objs) {
            log += l.getClass().getSimpleName() + " : " + l.getState().replaceAll("\n", "  /  ") + "\n";
        }
        log += "-----\n";
        logs++;
    }

    private void endLog() {
        log += "Final log (" + logs + ") of all events - " + formatDateTime() + "\n";
        logs++;
        for (Loggable l : objs) {
            log += l.getClass().getSimpleName() + " events : " + l.getEvents().replaceAll("\n", "  /  ") + "\n";
        }
        log += "End of log.";
    }

    public String getLog() {
        return this.log;
    }

    public void saveLogFile(String destination) {
        endLog();

        try {
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
