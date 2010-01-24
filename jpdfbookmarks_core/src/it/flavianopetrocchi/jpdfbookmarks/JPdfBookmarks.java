/*
 * JPdfBookmarks.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.flavianopetrocchi.jpdfbookmarks;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * This is the main class of the application. It parses the command line and
 * chooses the appropriate mode to start. The default mode is GUI mode.
 */
class JPdfBookmarks {

    public static final boolean DEBUG = false;
    private IBookmarksConverter pdf;

    private enum Mode {

        DUMP,
        APPLY,
        HELP,
        GUI,
        VERSION,
    }
    // <editor-fold defaultstate="expanded" desc="Member variables">
    public static final String VERSION = "2.0.2";
    public static final String APP_NAME = "JPdfBookmarks";
    public static final String DOWNLOAD_URL =
            "http://flavianopetrocchi.blogspot.com/2008/07/jpsdbookmarks-download-page.html";
    public static final String BLOG_URL =
            "http://flavianopetrocchi.blogspot.com";
    public static final String ITEXT_URL = "http://www.lowagie.com/iText/";
    public static final String ICEPDF_URL = "http://www.icepdf.org/";
    public static final String LAST_VERSION_URL =
            "http://jpdfbookmarks.altervista.org/version/lastVersion";
    public static final String LAST_VERSION_PROPERTIES_URL =
            "http://jpdfbookmarks.altervista.org/version/jpdfbookmarks.properties";
    public static final String MANUAL_URL =
            "http://jpdfbookmarks.altervista.org";
    private Mode mode = Mode.GUI;
    private Options options = createOptions();
    private final PrintWriter out = new PrintWriter(System.out, true);
    private final PrintWriter err = new PrintWriter(System.err, true);
    private String inputFilePath = null;
    private String outputFilePath = "output.pdf";
    private String bookmarksFilePath = null;
    private String pageSeparator = "/";
    private String attributesSeparator = ",";
    private String indentationString = "\t";// </editor-fold>

    //<editor-fold defaultstate="expanded" desc="public methods">
    public static void main(String[] args) {
        java.util.Locale.setDefault(java.util.Locale.ENGLISH);
        JPdfBookmarks app = new JPdfBookmarks();
        app.start(args);
    }

    /**
     * Start the application in the requested mode.
     *
     * @param args Arguments to select mode and pass files to process. Can be
     *             null.
     */
    public void start(String[] args) {
        if (args != null && args.length > 0) {
            setModeByCommandLine(args);
        }

        if (mode == Mode.HELP) {
            printHelpMessage();
        } else if (mode == Mode.VERSION) {
            out.println(VERSION);
        } else {
            try {
                if (inputFilePath != null) {
                    pdf = new iTextBookmarksConverter(inputFilePath);
                    if (pdf.isEncryped()) {
                        throw new Exception(
                                Res.getString("ERROR_PDF_ENCRYPTED"));
                    }
                }
                if (mode == Mode.DUMP) {
                    Dumper dumper = new Dumper(pdf, indentationString,
                            pageSeparator, attributesSeparator);
                    dumper.printBookmarks();
                } else if (mode == Mode.APPLY) {
                    Applier applier = new Applier(pdf, indentationString,
                            pageSeparator, attributesSeparator);
                    applier.loadBookmarksFile(bookmarksFilePath);
                    if (outputFilePath.equals(inputFilePath)) {
                        if (getYesOrNo(Res.getString(
                                "ERR_INFILE_EQUAL_OUTFILE"))) {
                            applier.save(outputFilePath);
                        }
                    } else {
                        applier.save(outputFilePath);
                    }
                } else if (mode == Mode.GUI) {
                    if (pdf != null) {
                        pdf.close();
                        pdf = null;
                    }
                    EventQueue.invokeLater(new GuiLauncher());
                }
                if (pdf != null) {
                    pdf.close();
                }
            } catch (Exception ex) {
                if (mode == Mode.GUI) {
                    EventQueue.invokeLater(new GuiLauncher());
                } else {
                    err.println(ex.getMessage());
                }
            }
        }
    }

    private class GuiLauncher implements Runnable {

        public void run() {
            try {
                JPdfBookmarksGui viewer;
                viewer = new JPdfBookmarksGui();
                viewer.setVisible(true);
                if (inputFilePath != null) {
                    viewer.openFileAsync(new File(new File(inputFilePath).getAbsolutePath()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void printErrorForDebug(Exception e) {
        if (DEBUG) {
            System.err.println("***** printErrorForDebug Start *****");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.err.println("***** printErrorForDebug End *****");
        }
    }

    public void printHelpMessage() {
        HelpFormatter help = new HelpFormatter();
        String header = Res.getString("APP_DESCR");
        String syntax = "jpdfbookmarks [--dump | --apply <bookmarks.txt> " +
                "--out <output.pdf> | --help | --version] [input.pdf]";
        int width = 80;
        int leftPad = 1, descPad = 2;
        String footer = Res.getString("BOOKMARKS_DESCR");
        help.printHelp(out, width, syntax, header, options,
                leftPad, descPad, footer);
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="private methods">
    /**
     * Sets the mode by the command line arguments and initializes files to
     * process if passed as arguments.
     *
     * @param args Arguments to process
     */
    private void setModeByCommandLine(String[] args) {
        Prefs userPrefs = new Prefs();
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption('h')) {
                mode = Mode.HELP;
            } else if (cmd.hasOption('v')) {
                mode = Mode.VERSION;
            } else if (cmd.hasOption('a')) {
                mode = Mode.APPLY;
                bookmarksFilePath = cmd.getOptionValue('a');
                if (cmd.hasOption('o')) {
                    outputFilePath = cmd.getOptionValue('o');
                } else {
                    throw new ParseException(
                            Res.getString("ERR_NO_OUT_FOR_APPLY"));
                }
            } else if (cmd.hasOption('d')) {
                mode = Mode.DUMP;
            } else {
                mode = Mode.GUI;
            }

            String[] leftOverArgs = cmd.getArgs();
            if (leftOverArgs.length > 0) {
                inputFilePath = leftOverArgs[0];
            } else if (mode == Mode.DUMP || mode == Mode.APPLY) {
                throw new ParseException(
                        Res.getString("ERR_NO_INPUT_FILE"));
            }

            if (cmd.hasOption("p")) {
                pageSeparator = cmd.getOptionValue("p");
            } else {
                pageSeparator = userPrefs.getPageSeparator();
            }
            if (cmd.hasOption("i")) {
                indentationString = cmd.getOptionValue("i");
            } else {
                indentationString = userPrefs.getIndentationString();
            }
            if (cmd.hasOption("t")) {
                attributesSeparator = cmd.getOptionValue("t");
            } else {
                attributesSeparator = userPrefs.getAttributesSeparator();
            }

            if (pageSeparator.equals(indentationString) ||
                    pageSeparator.equals(attributesSeparator) ||
                    indentationString.equals(attributesSeparator)) {
                throw new ParseException(
                        Res.getString("ERR_OPTIONS_CONTRAST"));
            }


        } catch (ParseException ex) {
            mode = Mode.GUI;
            err.println(ex.getLocalizedMessage());
            System.exit(1);
        }

    }

    /**
     * Get the user answer yes or no, on the command line. It recognize as a yes
     * y or yes and as a no n or no. Not case sensitive.
     *
     * @param question Question to the user.
     * @return Yes will return true and No will return false.
     */
    private static boolean getYesOrNo(String question) {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out, true);
        boolean answer = false;
        boolean validInput = false;
        while (!validInput) {
            out.println(question);
            try {
                String line = in.readLine();
                if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes")) {
                    answer = true;
                    validInput = true;
                } else if (line.equalsIgnoreCase("n") || line.equalsIgnoreCase("no")) {
                    answer = false;
                    validInput = true;
                }
            } catch (IOException ex) {
            }
        }
        return answer;
    }

    @SuppressWarnings("static-access")
    private Options createOptions() {
        Options appOptions = new Options();

        appOptions.addOption("v", "version", false,
                Res.getString("VERSION_DESCR"));
        appOptions.addOption("h", "help", false,
                Res.getString("HELP_DESCR"));
        appOptions.addOption(OptionBuilder.withLongOpt("dump").withDescription(Res.getString("DUMP_DESCR")).create('d'));
        appOptions.addOption(OptionBuilder.withLongOpt("apply").hasArg(true).withArgName("bookmarks.txt").withDescription(Res.getString("APPLY_DESCR")).create('a'));
        appOptions.addOption(OptionBuilder.withLongOpt("out").hasArg(true).withArgName("output.pdf").withDescription(Res.getString("OUT_DESCR")).create('o'));

        appOptions.addOption("p", "page-sep", true,
                Res.getString("PAGE_SEP_DESCR"));
        appOptions.addOption("t", "attributes-sep", true,
                Res.getString("ATTRIBUTES_SEP_DESCR"));
        appOptions.addOption("i", "indentation", true,
                Res.getString("INDENTATION_STRING_DESCR"));

        return appOptions;
    }
    //</editor-fold>
}
