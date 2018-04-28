import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jagacy.AbstractSession;
import com.jagacy.Key;
import com.jagacy.Seconds;
import com.jagacy.Session3270;
import com.jagacy.ui.AbstractPanel;
import com.jagacy.ui.AbstractSwing;
import com.jagacy.ui.Panel3270;
import com.jagacy.ui.Swing3270;
import com.jagacy.ui.UserInterface;
import com.jagacy.util.I18n;
import com.jagacy.util.JagacyException;
import com.jagacy.util.LinkedHashMap;

/**
 * Implements a Patent session.
 * 
 * @author Robert M. Preston
 * 
 */
class PatentSession extends Session3270 implements Seconds {
    private UserInterface myUi;

    /**
     * Creates a Patent session.
     * 
     * @param name The session name (also the name of the .properties file).
     * @param ui The UI used to display the session.
     * @throws JagacyException If an error occurs.
     */
    PatentSession(String name, UserInterface ui) throws JagacyException {
        super(name, "mainframe.ipaustralia.gov.au", "IBM-3279-2-E");
        myUi = ui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.Session3270#createUi()
     */
    protected UserInterface createUi() throws JagacyException {
        return myUi;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.AbstractSession#logon()
     */
    protected boolean logon() throws JagacyException {
        if (!myUi.waitForPosition(6, 41, "AUSTRALIA", THIRTY_SECONDS)) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.AbstractSession#logoff()
     */
    protected void logoff() throws JagacyException {
        if (logon()) {
            return;
        }
        
        if (myUi.waitForPosition(3, 17, "To continue,", 0)) {
            return;
        }
        int count = 10;
        while (--count >= 0) {
            if (myUi.waitForPosition(2, 72, "ZMENUM2", 0)) {
                break;
            }
            writeKey(Key.PF3);
            myUi.waitForChange(TEN_SECONDS);
        }
        writeKey(Key.PF3);
    }
}

/**
 * Implements a specialized Swing GUI.
 * 
 * @author Robert M. Preston
 * 
 */
public class MyEmulator extends AbstractSwing {

    private static final long serialVersionUID = 8831901593363366980L;

    private static final ImageIcon ICON = new ImageIcon(MyEmulator.class
        .getResource("images/book.jpg"));

    private LinkedHashMap<String, String> myLookAndFeelMap = new LinkedHashMap<String, String>();

    private boolean myIsCancelled;

    private AbstractSession mySession;

    /**
     * Creates the GUI.
     * 
     * @throws JagacyException If an error occurs.
     */
    private MyEmulator() throws JagacyException {
        super("Patents");

        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();

        for (int i = 0; i < lafs.length; i++) {
            UIManager.LookAndFeelInfo laf = lafs[i];
            myLookAndFeelMap.put(laf.getName(), laf.getClassName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#createSession(java.lang.String)
     */
    protected AbstractSession createSession(String name) throws JagacyException {
        mySession = new PatentSession(name, this);
        return mySession;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#createPanel()
     */
    protected AbstractPanel createPanel() throws JagacyException {
        return new Panel3270(this) {
            private static final long serialVersionUID = 299834078527771447L;

            /*
             * (non-Javadoc)
             * 
             * @see com.jagacy.ui.AbstractPanel#processRightClick(int, int)
             */
            protected void processRightClick(int row, int column) {
            }

            /*
             * (non-Javadoc)
             * 
             * @see com.jagacy.ui.AbstractPanel#processKey(java.awt.event.KeyEvent)
             */
            public void processKey(KeyEvent event) {
                super.processKey(event);
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#getProductName()
     */
    protected String getProductName() {
        return "MyEmulator";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#getIcon()
     */
    protected ImageIcon getIcon() {
        return ICON;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#connect()
     */
    protected boolean connect() {
        boolean connected = false;
        try {
            mySession.open();
            connected = true;
        } catch (JagacyException e) {
            AbstractSwing.printExceptions(e);
        }

        return connected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#addMenu(javax.swing.JMenu)
     */
    protected JMenu addMenu(JMenu menu) {
        JMenu newMenu = null;
        if (menu.getActionCommand().equals(I18n.getText("gui.menu.edit"))) {
            newMenu = createMenu(I18n.getText("patents.options"), KeyEvent.VK_O);
            newMenu.add(createMenuItem(I18n.getText("patents.look_and_feel"),
                "Look and Feel", KeyEvent.VK_L));
        }
        return newMenu;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#addMenuItem(javax.swing.JMenuItem)
     */
    protected JMenuItem addMenuItem(JMenuItem menuItem) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#addUserComponent(int)
     */
    protected JComponent addUserComponent(int index) {
        switch (index) {
        case 0:
            return getCursorComponent();
        case 1:
            return createEmptyComponent();
            // return getTimeComponent();
        case 2:
            return createEmptyComponent();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#addButton(javax.swing.JButton)
     */
    protected JButton addButton(JButton button) {
        JButton newButton = null;
        if (button.getActionCommand().equals("Paste")) {
            newButton = createButton(MyEmulator.class, "images/duke.gif",
                "Look and Feel", I18n.getText("patents.tip"));
        }
        return newButton;
    }

    /**
     * Implements the Look and Feel dialog box.
     */
    private void changeLookAndFeel() {
        myIsCancelled = false;
        final JDialog dialog = new JDialog(this, I18n.getText("patents.look_and_feel"), true);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setResizable(false);
        KeyAdapter finished = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dialog.setVisible(false);
                }
            }
        };
        JButton ok = new JButton(I18n.getText("gui.dialog.ok"));
        ok.setFont(CONTROL_FONT);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
            }
        });
        ok.addKeyListener(finished);

        JButton cancel = new JButton(I18n.getText("gui.dialog.cancel"));
        cancel.setFont(CONTROL_FONT);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
                myIsCancelled = true;
            }
        });

        JPanel panel = new JPanel();
        panel.add(Box.createHorizontalGlue());
        panel.add(ok);
        panel.add(cancel);
        panel.add(Box.createHorizontalGlue());
        dialog.getContentPane().add(panel, BorderLayout.SOUTH);

        panel = new JPanel();
        final JComboBox combo = new JComboBox();
        combo.setFont(CONTROL_FONT);
        for (String key : myLookAndFeelMap.keySet()) {
            try {
                Class.forName((String)myLookAndFeelMap.get(key));
                combo.addItem(key);
            } catch (ClassNotFoundException e) {
            }
        }
        combo.setSelectedIndex(0);
        panel.add(combo);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        ok.grabFocus();

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (myIsCancelled) {
            return;
        }

        try {
            UIManager.setLookAndFeel((String)myLookAndFeelMap.get(combo
                .getSelectedItem()));
            SwingUtilities.updateComponentTreeUI(this);
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jagacy.ui.AbstractSwing#processAction(java.awt.event.ActionEvent)
     */
    protected boolean processAction(ActionEvent event) {
        boolean processed = false;
        String command = event.getActionCommand();
        if (command.startsWith("About ")) {
            JOptionPane.showMessageDialog(this, getProductName() + "\n"
                + "Copyright " + COPYRIGHT_SIGN_SYMBOL
                + " My Software.\nAll Rights Reserved.\n"
                + "(Portions copyright " + COPYRIGHT_SIGN_SYMBOL
                + " Jagacy Software)", "About " + getProductName(),
                JOptionPane.INFORMATION_MESSAGE, ICON);
            processed = true;
        } else if (command.equals("Look and Feel")) {
            changeLookAndFeel();
            processed = true;
        }
        return processed;
    }

    /**
     * Creates a specialized Swing GUI.
     * 
     * @param args Command line parameters.
     */
    public static void main(String[] args) {
        try {
            new MyEmulator();
        } catch (JagacyException e) {
            AbstractSwing.notify(new Swing3270(), ERROR_LEVEL,
                "MyEmulator Error", e.getMessage() + "\n");
            AbstractSwing.printExceptions(e);
            System.exit(1);
        }
    }
}
