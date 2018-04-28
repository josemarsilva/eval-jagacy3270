import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jagacy.Key;
import com.jagacy.Session3270;
import com.jagacy.util.JagacyException;
import com.jagacy.util.JagacyProperties;
import com.jagacy.util.Loggable;

/**
 * This class uses positions to retrieve the first faculty member.
 * 
 * @author Robert M. Preston
 * 
 */
public class Example1 extends Session3270 {

    private JagacyProperties props;

    private Loggable logger;

    private Example1() throws JagacyException {
        super("example1");
        props = getProperties();
        logger = getLoggable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.Session3270#logon()
     */
    protected boolean logon() throws JagacyException {
        // Notice that you don't have to prefix each property with 'example1'.
        // Jagacy will do this for you.
        if (!waitForPosition("logon.wait", "logon.timeout.seconds")) {
            logger.fatal("Not logon screen");
            return false;
        }

        if (getCrc32() != props.getLongCardinal("logon.crc")) {
            throw new JagacyException(
                "Screen has changed, contact mainframe support");
        }

        writePosition("logon.entry", "phonbook");
        writeKey(Key.ENTER);

        if (!waitForPosition("main.wait", "main.timeout.seconds")) {
            logger.fatal("Not main screen");
            return false;
        }

        if (getCrc32() != props.getLongCardinal("main.crc")) {
            throw new JagacyException(
                "Screen has changed, contact mainframe support");
        }

        writePosition("main.entry", "f");
        writeKey(Key.ENTER);

        if (!waitForPosition("phonebook.wait", "phonebook.timeout.seconds")) {
            logger.fatal("Not phonebook screen");
            return false;
        }

        if (getCrc32() != props.getLongCardinal("phonebook.crc")) {
            throw new JagacyException(
                "Screen has changed, contact mainframe support");
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.Session3270#logoff()
     */
    protected void logoff() throws JagacyException {

        writeKey(Key.TAB);
        writeKey(Key.TAB);
        writeString("q");
        writeKey(Key.ENTER);
        waitForChange("logoff.timeout");
    }

    /**
     * Retrieves the first faculty name that begins with the user's query and
     * writes it to System.out.
     * 
     * @throws JagacyException If an error occurs.
     */
    private void printFacultyName() throws JagacyException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String query = "";

        System.out.println("Enter a faculty name to query or ENTER to abort:");
        try {
            query = in.readLine();
        } catch (IOException e) {
            throw new JagacyException(e);
        }
        query = query.trim();

        if (!"".equals(query)) {
            writePosition("query.entry", query);
            writeKey(Key.ENTER);

            if (!waitForChange("result.timeout.seconds")) {
                logger.fatal("Not result screen");
            }

            int startColumn = props.getCardinal("result.startColumn");
            int endColumn = props.getCardinal("result.endColumn");
            int row = props.getCardinal("result.row");
           
            System.out.println(readPosition(row, startColumn, endColumn).trim());
        }
    }

    /**
     * Print exception(s) to System.err.
     * 
     * @param e JagacyException
     */
    private static void printExceptions(JagacyException e) {
        System.err.println(e);
        if (e.hasException()) {
            System.err.println(e.getException());
        }
    }

    public static void main(String[] args) {
        Example1 example = null;
        try {
            example = new Example1();
            example.open();
            example.printFacultyName();
            example.close();
        } catch (JagacyException e) {
            printExceptions(e);
            if (example != null) {
                example.abort();
            }
        }

        if ((example != null) && example.props.getBoolean("window", false)) {
            // Swing requires this if window is enabled.
            System.exit(0);
        }
    }
}