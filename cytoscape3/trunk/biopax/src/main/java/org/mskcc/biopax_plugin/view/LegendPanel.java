package org.mskcc.biopax_plugin.view;

import cytoscape.Cytoscape;
import org.mskcc.biopax_plugin.util.net.WebFileConnect;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Displays the Default Visual Style Legend for the BioPAX Mapper.
 *
 * @author Ethan Cerami
 */
public class LegendPanel extends JPanel {

    /**
     * BioPAX Legend.
     */
    public static int BIOPAX_LEGEND = 0;

    /**
     * Binary Legend.
     */
    public static int BINARY_LEGEND = 1;

    /**
	 * Constructor.
	 *
	 */
	public LegendPanel(int mode) {
		this.setLayout(new BorderLayout());

		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        URL legendUrl;
        if (mode == BIOPAX_LEGEND) {
            //legendUrl = LegendPanel.class.getResource("/legend.html");
            legendUrl = getClass().getResource("/html/legend.html");
        } else {
            legendUrl = getClass().getResource("/html/binary_legend.html");
        }
        StringBuffer temp = new StringBuffer();
		temp.append("<HTML><BODY>");

		try {
			String legendHtml = WebFileConnect.retrieveDocument(legendUrl.toString());
			temp.append(legendHtml);
		} catch (IOException e) {
			temp.append("Could not load legend...");
		}

		temp.append("</BODY></HTML>");
		textPane.setText(temp.toString());

        textPane.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String name = hyperlinkEvent.getDescription();
                    if (name.equalsIgnoreCase("filter")) {
                        EdgeFilterUi ui = new EdgeFilterUi(Cytoscape.getCurrentNetwork());
                    }
                }
            }
        });
        BioPaxDetailsPanel.modifyStyleSheetForSingleDocument(textPane);

        JScrollPane scrollPane = new JScrollPane(textPane);
		this.add(scrollPane, BorderLayout.CENTER);
	}
}
