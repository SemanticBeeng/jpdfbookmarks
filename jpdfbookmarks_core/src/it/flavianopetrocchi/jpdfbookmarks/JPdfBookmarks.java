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

import it.flavianopetrocchi.jpdfbookmarks.bookmark.IBookmarksConverter;
import it.flavianopetrocchi.jpdfbookmarks.bookmark.Bookmark;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.ServiceLoader;
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

    //private IBookmarksConverter pdf;
    private enum Mode {

        DUMP,
        APPLY,
        HELP,
        GUI,
        VERSION,
        SHOW_ON_OPEN,
    }
    // <editor-fold defaultstate="expanded" desc="Member variables">
    public static final String VERSION = "2.4.0";
    public static final String APP_NAME = "JPdfBookmarks";
    protected static final String NEWLINE = System.getProperty("line.separator");
    public static final String DOWNLOAD_URL =
            "http://flavianopetrocchi.blogspot.com/2008/07/jpsdbookmarks-download-page.html";
    public static final String BLOG_URL =
            "http://flavianopetrocchi.blogspot.com";
    public static final String ITEXT_URL = "http://www.lowagie.com/iText/";
    public static final String LAST_VERSION_URL =
            "http://jpdfbookmarks.altervista.org/version/lastVersion";
    public static final String LAST_VERSION_PROPERTIES_URL =
            "http://jpdfbookmarks.altervista.org/version/jpdfbookmarks.properties";
    public static final String MANUAL_URL = "http://sourceforge.net/apps/mediawiki/jpdfbookmarks/";
    //"http://jpdfbookmarks.altervista.org";
    private Mode mode = Mode.GUI;
    private Options options = createOptions();
    private final PrintWriter out = new PrintWriter(System.out, true);
    private final PrintWriter err = new PrintWriter(System.err, true);
    private String inputFilePath = null;
    private String outputFilePath = "output.pdf";
    private String bookmarksFilePath = null;
    private String pageSeparator = "/";
    private String attributesSeparator = ",";
    private String indentationString = "\t";
    private String firstTargetString = null;
    private boolean silentMode = false;
    private String charset = Charset.defaultCharset().displayName();
    private String showOnOpenArg = null;// </editor-fold>

    //<editor-fold defaultstate="expanded" desc="public methods">
    public static void main(String[] args) {
        localizeExternalModules();
        JPdfBookmarks app = new JPdfBookmarks();
        app.start(args);
    }

    private static void localizeExternalModules() {
        Bookmark.localizeStrings(Res.getString("DEFAULT_TITLE"), Res.getString("PAGE"), Res.getString("PARSE_ERROR"));
    }

    public static IBookmarksConverter getBookmarksConverter() {
        ServiceLoader<IBookmarksConverter> s = ServiceLoader.load(IBookmarksConverter.class);
        Iterator<IBookmarksConverter> i = s.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        //return new iTextBookmarksConverter();
        return null;
    }

    private IBookmarksConverter fatalGetConverterAndOpenPdf(String inputFilePath) {
        IBookmarksConverter pdf = getBookmarksConverter();
        if (pdf != null) {
            try {
                pdf.open(inputFilePath);
            } catch (IOException ex) {
                fatalOpenFileError(inputFilePath);
            }
        } else {
            err.println(Res.getString("ERROR_BOOKMARKS_CONVERTER_NOT_FOUND"));
            System.exit(1);
        }
        return pdf;
    }

    private void apply() {
        IBookmarksConverter pdf = fatalGetConverterAndOpenPdf(inputFilePath);

        Applier applier = new Applier(pdf, indentationString,
                pageSeparator, attributesSeparator);
        try {
            applier.loadBookmarksFile(bookmarksFilePath, charset);
        } catch (Exception ex) {
            fatalOpenFileError(bookmarksFilePath);
        }

        if (outputFilePath == null || outputFilePath.equals(inputFilePath)) {
            if (getYesOrNo(Res.getString(
                    "ERR_INFILE_EQUAL_OUTFILE"))) {
                outputFilePath = inputFilePath;
            }
        } else {
            File f = new File(outputFilePath);
            if (!f.exists()
                    || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
                try {
                    applier.save(outputFilePath);
                    pdf.close();
                } catch (IOException ex) {
                    fatalSaveFileError(outputFilePath);
                }
            }
        }

    }

    private void dump() {
        IBookmarksConverter pdf = fatalGetConverterAndOpenPdf(inputFilePath);
        Dumper dumper = new Dumper(pdf, indentationString,
                pageSeparator, attributesSeparator);

        if (outputFilePath == null) {
            dumper.printBookmarksIterative(new OutputStreamWriter(System.out));
        } else {
            File f = new File(outputFilePath);
            if (!f.exists()
                    || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outputFilePath);
                    OutputStreamWriter outStream = new OutputStreamWriter(fos, charset);
                    dumper.printBookmarksIterative(outStream);
                    outStream.close();
                    pdf.close();
                } catch (FileNotFoundException ex) {
                    fatalOpenFileError(outputFilePath);
                } catch (UnsupportedEncodingException ex) {
                    //already checked in command line parsing
                } catch (IOException ex) {
                }
            }
        }
    }

    private void showOnOpen() {
        IBookmarksConverter ipdf = fatalGetConverterAndOpenPdf(inputFilePath);

        if (showOnOpenArg.equalsIgnoreCase("CHECK") || showOnOpenArg.equalsIgnoreCase("c")) {
            if (ipdf.showBookmarksOnOpen()) {
                out.println("YES");
            } else {
                out.println("NO");
            }
        } else {
            if (showOnOpenArg.equalsIgnoreCase("yes") || showOnOpenArg.equalsIgnoreCase("y")) {
                ipdf.setShowBookmarksOnOpen(true);
            } else if (showOnOpenArg.equalsIgnoreCase("no") || showOnOpenArg.equalsIgnoreCase("n")) {
                ipdf.setShowBookmarksOnOpen(false);
            }
            if (outputFilePath == null || outputFilePath.equals(inputFilePath)) {
                if (getYesOrNo(Res.getString(
                        "ERR_INFILE_EQUAL_OUTFILE"))) {
                    outputFilePath = inputFilePath;
                }
            } else {
                File f = new File(outputFilePath);
                if (!f.exists()
                        || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
                    try {
                        ipdf.save(outputFilePath);
                        ipdf.close();
                    } catch (IOException ex) {
                        fatalSaveFileError(outputFilePath);
                    }
                }
            }
        }
    }

    private void fatalOpenFileError(String filePath) {
        err.println(Res.getString("ERROR_OPENING_FILE") + " " + filePath);
        System.exit(1);
    }

    private void fatalSaveFileError(String filePath) {
        err.println(Res.getString("ERROR_SAVING_FILE") + " (" + filePath + ")");
        System.exit(1);
    }

    public void launchNewGuiInstance(String path, Bookmark bookmark) {
        EventQueue.invokeLater(new GuiLauncher(path, bookmark));
    }

    static public File getPath() {
        File f = null;
        try {
            f = new File(JPdfBookmarks.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException ex) {
        }
        return f;
    }

    /**
     * Start the application in the requested mode.
     *
     * @param args Arguments to select mode and pass files to process. Can be
     *             null.
     */
//    public void start(String[] args) {
//        if (args != null && args.length > 0) {
//            setModeByCommandLine(args);
//        }
//
//        if (mode == Mode.HELP) {
//            printHelpMessage();
//        } else if (mode == Mode.VERSION) {
//            out.println(VERSION);
//        } else {
//            try {
//                if (inputFilePath != null) {
//                    pdf = new iTextBookmarksConverter(inputFilePath);
////                    if (pdf.isEncryped()) {
////                        throw new Exception(
////                                Res.getString("ERROR_PDF_ENCRYPTED"));
////                    }
//                    if (firstTargetString != null) {
//                        StringBuffer buffer = new StringBuffer("Bookmark");
//                        buffer.append(pageSeparator).append(firstTargetString);
//                        firstTargetBookmark = Bookmark.bookmarkFromString(pdf,
//                                buffer.toString(), indentationString, pageSeparator, attributesSeparator);
//                    }
//                }
//
//                if (mode == Mode.SHOW_ON_OPEN) {
//                    if (showOnOpenArg.equalsIgnoreCase("CHECK") || showOnOpenArg.equalsIgnoreCase("c")) {
////                        PrintWriter out = new PrintWriter(System.out, true);
//                        if (pdf.showBookmarksOnOpen()) {
//                            out.println("YES");
//                        } else {
//                            out.println("NO");
//                        }
//                    } else {
//                        if (showOnOpenArg.equalsIgnoreCase("yes") || showOnOpenArg.equalsIgnoreCase("y")) {
//                            pdf.setShowBookmarksOnOpen(true);
//                        } else if (showOnOpenArg.equalsIgnoreCase("no") || showOnOpenArg.equalsIgnoreCase("n")) {
//                            pdf.setShowBookmarksOnOpen(false);
//                        }
//                        if (outputFilePath == null || outputFilePath.equals(inputFilePath)) {
//                            if (getYesOrNo(Res.getString(
//                                    "ERR_INFILE_EQUAL_OUTFILE"))) {
//                                pdf.save(inputFilePath);
//                            }
//                        } else {
//                            File f = new File(outputFilePath);
//                            if (!f.exists()
//                                    || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
//                                pdf.save(outputFilePath);
//                            }
//                        }
//                    }
//                } else if (mode == Mode.DUMP) {
//                    Dumper dumper = new Dumper(pdf, indentationString,
//                            pageSeparator, attributesSeparator);
//                    if (outputFilePath == null) {
//                        dumper.printBookmarksIterative(new OutputStreamWriter(System.out));
//                    } else {
//                        File f = new File(outputFilePath);
//                        if (!f.exists()
//                                || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
//
//                            FileOutputStream fos = new FileOutputStream(outputFilePath);
//                            OutputStreamWriter outStream = new OutputStreamWriter(fos, charset);
//                            dumper.printBookmarksIterative(outStream);
//                            outStream.close();
//                        }
//                    }
//                } else if (mode == Mode.APPLY) {
//                    Applier applier = new Applier(pdf, indentationString,
//                            pageSeparator, attributesSeparator);
//                    applier.loadBookmarksFile(bookmarksFilePath, charset);
//                    if (outputFilePath == null || outputFilePath.equals(inputFilePath)) {
//                        if (getYesOrNo(Res.getString(
//                                "ERR_INFILE_EQUAL_OUTFILE"))) {
//                            applier.save(inputFilePath);
//                        }
//                    } else {
//                        File f = new File(outputFilePath);
//                        if (!f.exists()
//                                || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
//                            applier.save(outputFilePath);
//                        }
//                    }
//                } else if (mode == Mode.GUI) {
//                    if (pdf != null) {
//                        pdf.close();
//                        pdf = null;
//                    }
//                    EventQueue.invokeLater(new GuiLauncher(inputFilePath, firstTargetBookmark));
//                }
//                if (pdf != null) {
//                    pdf.close();
//                }
//            } catch (Exception ex) {
//                if (mode == Mode.GUI) {
//                    EventQueue.invokeLater(new GuiLauncher(inputFilePath, firstTargetBookmark));
//                } else {
//                    if (inputFilePath != null) {
//                        err.println(Res.getString("ERROR_OPENING_FILE") + " "
//                                + inputFilePath);
//                    } else {
//                        err.println(Res.getString("ERROR_STARTING_JPDFBOOKMARKS"));
//                    }
//                }
//            }
//        }
//    }
    /**
     * Start the application in the requested mode.
     *
     * @param args Arguments to select mode and pass files to process. Can be
     *             null.
     */
    public void start(final String[] args) {
        if (args != null && args.length > 0) {
            setModeByCommandLine(args);
        }

        switch (mode) {
            case VERSION:
                out.println(VERSION);
                break;
            case DUMP:
                dump();
                break;
            case SHOW_ON_OPEN:
                showOnOpen();
                break;
            case APPLY:
                apply();
                break;
            case GUI:
                //launchNewGuiInstance(inputFilePath, null);
                EventQueue.invokeLater(new GuiLauncher(inputFilePath));
                break;
            case HELP:
            default:
                printHelpMessage();

        }
    }

    public class GuiLauncher implements Runnable {

        private Bookmark firstTarget;
        private String inputPath;

        public GuiLauncher(String inputPath) {
            if (inputPath != null) {
                IBookmarksConverter ipdf = fatalGetConverterAndOpenPdf(inputPath);
                if (firstTargetString != null) {
                    StringBuilder buffer = new StringBuilder("Bookmark");
                    buffer.append(pageSeparator).append(firstTargetString);
                    firstTarget = Bookmark.bookmarkFromString(null, ipdf,
                            buffer.toString(), indentationString, pageSeparator, attributesSeparator);
                }
                this.inputPath = inputPath;
                try {
                    ipdf.close();
                } catch (IOException ex) {
                }
            }
        }

        public GuiLauncher(String inputPath, Bookmark firstTarget) {
            this.firstTarget = firstTarget;
            this.inputPath = inputPath;
        }

        @Override
        public void run() {
            try {
                JPdfBookmarksGui viewer;
                viewer = new JPdfBookmarksGui();
                viewer.setVisible(true);
                if (inputPath != null) {
                    viewer.openFileAsync(new File(new File(inputPath).getAbsolutePath()),
                            this.firstTarget);
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
            System.err.println("***** printErrorForDebug End *****");
        }
    }

    public void printHelpMessage() {
        HelpFormatter help = new HelpFormatter();
        String header = Res.getString("APP_DESCR");
        String syntax = "jpdfbookmarks <input.pdf> "
                + "[--dump | --apply <bookmarks.txt> | --show-on-open <YES | NO | CHECK> "
                + "| --help | --version] [--out <output.pdf>]";
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
            } else if (cmd.hasOption('w')) {
                mode = Mode.SHOW_ON_OPEN;
                showOnOpenArg = cmd.getOptionValue('w');
                if (cmd.hasOption('o')) {
                    outputFilePath = cmd.getOptionValue('o');
                } else {
                    outputFilePath = null;
                }
            } else if (cmd.hasOption('a')) {
                mode = Mode.APPLY;
                bookmarksFilePath = cmd.getOptionValue('a');
            } else if (cmd.hasOption('d')) {
                mode = Mode.DUMP;
            } else {
                mode = Mode.GUI;
                if (cmd.hasOption('b')) {
                    firstTargetString = cmd.getOptionValue('b');
                }
            }


            if (cmd.hasOption('o')) {
                outputFilePath = cmd.getOptionValue('o');
            } else {
                outputFilePath = null;
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
            if (cmd.hasOption("f")) {
                silentMode = true;
            }
            if (cmd.hasOption("e")) {
                charset = cmd.getOptionValue("e");
                if (!Charset.isSupported(charset)) {
                    throw new ParseException(
                            Res.getString("ERR_CHARSET_NOT_SUPPORTED"));
                }
            }

            if (pageSeparator.equals(indentationString)
                    || pageSeparator.equals(attributesSeparator)
                    || indentationString.equals(attributesSeparator)) {
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
    private boolean getYesOrNo(String question) {
        if (silentMode) {
            return true;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        PrintWriter cout = new PrintWriter(System.out, true);
        boolean answer = false;
        boolean validInput = false;
        while (!validInput) {
            cout.println(question);
            try {
                String line = in.readLine();
                if (line.equalsIgnoreCase(Res.getString("SHORT_YES")) || line.equalsIgnoreCase(Res.getString("LONG_YES"))) {
                    answer = true;
                    validInput = true;
                } else if (line.equalsIgnoreCase(Res.getString("SHORT_NO")) || line.equalsIgnoreCase(Res.getString("LONG_NO"))) {
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

        appOptions.addOption("f", "force", false,
                Res.getString("FORCE_DESCR"));
        appOptions.addOption("v", "version", false,
                Res.getString("VERSION_DESCR"));
        appOptions.addOption("h", "help", false,
                Res.getString("HELP_DESCR"));
        appOptions.addOption(OptionBuilder.withLongOpt("dump").withDescription(Res.getString("DUMP_DESCR")).create('d'));
        appOptions.addOption(OptionBuilder.withLongOpt("apply").hasArg(true).withArgName("bookmarks.txt").withDescription(Res.getString("APPLY_DESCR")).create('a'));
        appOptions.addOption(OptionBuilder.withLongOpt("out").hasArg(true).withArgName("output.pdf").withDescription(Res.getString("OUT_DESCR")).create('o'));
        appOptions.addOption(OptionBuilder.withLongOpt("encoding").hasArg(true).withArgName("UTF-8").withDescription(Res.getString("ENCODING_DESCR")).create('e'));
//        appOptions.addOption(OptionBuilder.withLongOpt("show-on-open").hasArg(true)
//                .withArgName("YES | NO | CHECK")
//                .withDescription(Res.getString("SHOW_ON_OPEN_DESCR")).create('w'));

        appOptions.addOption("b", "bookmark", true,
                Res.getString("BOOKMARK_ARG_DESCR"));
        appOptions.addOption("p", "page-sep", true,
                Res.getString("PAGE_SEP_DESCR"));
        appOptions.addOption("t", "attributes-sep", true,
                Res.getString("ATTRIBUTES_SEP_DESCR"));
        appOptions.addOption("i", "indentation", true,
                Res.getString("INDENTATION_STRING_DESCR"));
        appOptions.addOption("w", "show-on-open", true,
                Res.getString("SHOW_ON_OPEN_DESCR"));

        return appOptions;
    }
    //</editor-fold>
}
