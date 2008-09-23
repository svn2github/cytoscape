package org.mskcc.csplugins.ExpressionCorrelation;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The action to show the Expression Correlation help - links to the documentation web site
 */
public class ExpressionCorrelationHelpAction implements ActionListener {
    private String expressionCorrelationHelpURL = "http://www.baderlab.org/Software/ExpressionCorrelation";

    /**
     * Invoked when the about action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        //open documentation web site
        cytoscape.util.OpenBrowser.openURL(expressionCorrelationHelpURL);
    }
}
