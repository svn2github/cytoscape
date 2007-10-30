package org.mskcc.pathway_commons.view;

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Summary Panel.
 *
 * @author Ethan Cerami.
 */
public class PhysicalEntityDetailsPanel extends JXPanel {
    private Document doc;
    private JTextPane textPane;
    private PopupDaemon daemon;

    /**
     * Constructor.
     */
    public PhysicalEntityDetailsPanel() {
        daemon = new PopupDaemon(this, 1000);
        this.setLayout(new BorderLayout());
        textPane = createTextPane();
        doc = textPane.getDocument();
        JScrollPane scrollPane = encloseInJScrollPane (textPane);

        GradientHeader header = new GradientHeader("Gene Summary");
        add (header, BorderLayout.NORTH);
        this.setAlpha(0.0f);
        this.setVisible(false);
        add(scrollPane, BorderLayout.CENTER);
        attachMouseListener(this, daemon);
    }

    /**
     * Attaches appropriate mouse listeners.
     * @param daemon PopupDaemon Object.
     */
    private void attachMouseListener(PhysicalEntityDetailsPanel detailsPanel,
            final PopupDaemon daemon) {
        MouseAdapter mouseAdapter = new MouseAdapter() {

            /**
             * When mouse enters frame, stop daemon.  Frame will persist.
             * @param mouseEvent MouseEvent Object.
             */
            public void mouseEntered(MouseEvent mouseEvent) {
                System.out.println("Mouse entered");
                //daemon.stop();
            }


            public void mousePressed(MouseEvent mouseEvent) {
                System.out.println("Mouse pressed");
            }

            /**
             * When mouse exits frame, restart deamon.  Frame will disappear after XX milliseconds.
             * @param mouseEvent Mouse Event Object.
             */
            public void mouseExited(MouseEvent mouseEvent) {
                System.out.println("Mouse exited");
                //daemon.restart();
            }
        };
    }

    /**
     * Gets the summary document model.
     * @return Document object.
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * Gets the summary text pane object.
     * @return JTextPane Object.
     */
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     * Encloses the specified JTextPane in a JScrollPane.
     *
     * @param textPane JTextPane Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane(JTextPane textPane) {
        JScrollPane scrollPane = new JScrollPane(textPane);
        return scrollPane;
    }

    /**
     * Creates a JTextPane with correct line wrap settings.
     *
     * @return JTextPane Object.
     */
    private JTextPane createTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBorder(new EmptyBorder(7,7,7,7));
        textPane.setContentType("text/html");
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        textPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                System.out.println("URL:  " + hyperlinkEvent.getURL());
            }
        });

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("h2 {color: #663333; font-size: 120%; font-weight: bold; "
                + "margin-bottom:3px}");
        styleSheet.addRule("h3 {color: #663333; font-size: 105%; font-weight: bold;"
                + "margin-bottom:7px}");
        styleSheet.addRule("ul { list-style-type: none; margin-left: 5px; "
                + "padding-left: 1em;	text-indent: -1em;}");
        styleSheet.addRule("b {color: #66333; font-weight: bold;}");
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        htmlEditorKit.setStyleSheet(styleSheet);
        textPane.setEditorKit(htmlEditorKit);
        return textPane;
    }
}

/**
 * Daemon Thread to automatically hide Pop-up Window after xxx milliseconds.
 *
 * @author Ethan Cerami
 */
class PopupDaemon implements ActionListener {
    private Timer timer;
    private PhysicalEntityDetailsPanel detailsPanel;

    /**
     * Constructor.
     *
     * @param detailsPanel PhysicalEntityDetailsPanel Object.
     * @param delay  Delay until pop-up window is hidden.
     */
    public PopupDaemon(PhysicalEntityDetailsPanel detailsPanel, int delay) {
        this.detailsPanel = detailsPanel;
        timer = new Timer(delay, this);
        timer.setRepeats(false);
    }

    /**
     * Restart timer.
     */
    public void restart() {
        timer.restart();
    }

    public void stop() {
        timer.stop();
    }

    /**
     * Timer Event:  Hide popup now.
     *
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {
        System.out.println("HIDE");
        //detailsPanel.setVisible(false);
    }
}
