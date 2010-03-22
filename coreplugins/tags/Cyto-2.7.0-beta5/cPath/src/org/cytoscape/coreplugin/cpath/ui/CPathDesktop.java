/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.coreplugin.cpath.ui;

import org.cytoscape.coreplugin.cpath.action.ExecuteQuery;
import org.cytoscape.coreplugin.cpath.action.UpdateSearchRequest;
import org.cytoscape.coreplugin.cpath.model.*;
import org.cytoscape.coreplugin.cpath.util.CPathProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * cPath Frame with Search Box and Search Results.
 *
 * @author Ethan Cerami
 */
public class CPathDesktop extends JFrame implements Observer {

    /**
     * The Current Search Query (Data Model Object)
     */
    private SearchRequest searchRequest;

    /**
     * List of All Searches (Data Model Object)
     */
    private static SearchBundleList searchList = new SearchBundleList();

    /**
     * Console Panel.
     */
    private ConsolePanel consolePanel;

    /**
     * Current User Node / Edge Selection.
     */
    private UserSelection userSelection;

    /**
     * CyMap of all Interactors/Interactions Index by Node/Edge Id.
     */
    private HashMap cyMap;

    /**
     * Search Button.
     */
    private JButton searchButton;

    /**
     * Preferred Width of Component.
     */
    private static final int WIDTH = 400;

    /**
     * Preferred Height of Component.
     */
    private static final int HEIGHT = 400;

    /**
     * Constructor.
     *
     * @param parent Parent Frame.3
     */
    public CPathDesktop (JFrame parent) {
        super("cPath PlugIn");

        //  Initialize User Selection Object and CyMap
        userSelection = new UserSelection();
        cyMap = new HashMap();

        //  Create Empty Search Query Object
        searchRequest = new SearchRequest();

        //  Register to Listen to changes to SearchList
        searchList.addObserver(this);

        //  Use Border Layout
        Container cPane = getContentPane();
        cPane.setLayout(new BorderLayout());

        //  Create Center Panel (Console plus Details)
        consolePanel = new ConsolePanel();
        String url = CPathProperties.getCPathUrl();
        consolePanel.logMessage("Plugin is currently set to retrieve data "
                + "from:  " + url);
        cPane.add(consolePanel, BorderLayout.CENTER);

        //  Create Northern Panel (cPath Search)
        JPanel northPanel = createPanelNorth();
        cPane.add(northPanel, BorderLayout.NORTH);

        //  Pack it, Size it, Center it.
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Creates Nothern Panel with cPath Search Box.
     */
    private JPanel createPanelNorth () {
        int hspace = 5;
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(2, 1));

        //  Create Titled Border
        TitledBorder border = new TitledBorder("Search cPath");
        northPanel.setBorder(border);

        JPanel buttonBar = new JPanel();
        buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.X_AXIS));

        //  Create Listener
        searchButton = new JButton("Search");
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        ExecuteQuery queryListener = new ExecuteQuery(cyMap, searchRequest,
                searchList, consolePanel, searchButton, this);
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));

        //  Create Search Text Field
        JTextField textField = new JTextField("", 20);
        Font font = textField.getFont();
        textField.setFont(new Font(font.getName(), Font.PLAIN, 11));
        textField.setToolTipText("Enter Search Term(s)");
        UpdateSearchRequest textListener =
                new UpdateSearchRequest(searchRequest);
        textField.addFocusListener(textListener);
        textField.addKeyListener(queryListener);
        textField.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        textField.setMinimumSize(new Dimension(100, 50));
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));
        buttonBar.add(textField);
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));

        //  Create Organism Combo Box
        JComboBox orgCombo = createOrganismComboBox();
        orgCombo.setFont(new Font(font.getName(), Font.PLAIN, 11));
        orgCombo.setToolTipText("Filter by Organism");
        //  Used to specify a default size for pull down menu
        orgCombo.setPrototypeDisplayValue
                (new String("Saccharomyces cerevisiae"));
        orgCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        buttonBar.add(orgCombo);
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));

        //  Create Result Limit Combo Box
        JComboBox limitCombo = createResultLimitComboBox();
        limitCombo.setFont(new Font(font.getName(), Font.PLAIN, 11));
        limitCombo.setToolTipText("Limit Result Set or Get All");
        limitCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        //  Used to specify a default size for pull down menu;
        //  Particularly important for Windows, see Bug #520.
        limitCombo.setPrototypeDisplayValue
                (new String("Get All --- Get All"));
        buttonBar.add(limitCombo);
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));

        //  Create Search Button
        searchButton.setToolTipText("Execute Search Query");
        searchButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        buttonBar.add(searchButton);
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));
        searchButton.addActionListener(queryListener);

        JButton helpButton = new JButton("Help");
        helpButton.setToolTipText("View Quick Reference Manual");
        helpButton.addActionListener(new QuickReferenceDialog((JFrame) this));
        helpButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        buttonBar.add(helpButton);
        buttonBar.add(Box.createRigidArea(new Dimension(hspace, 0)));

        JButton aboutButton = new JButton("About");
        aboutButton.setToolTipText("About the cPath PlugIn");
        aboutButton.addActionListener(new AboutDialog((JFrame) this));
        aboutButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        buttonBar.add(aboutButton);
        buttonBar.add(Box.createHorizontalGlue());

        northPanel.add(buttonBar);

        //  Create Search Examples Label
        JLabel examples = new JLabel("Examples:  p53  |  rad51");
        examples.setBorder(new EmptyBorder(hspace, 10, 5, 5));
        northPanel.add(examples);
        return northPanel;
    }

    /**
     * Creates Result Set Limit Pull-Down Menu.
     */
    private JComboBox createResultLimitComboBox () {
        Vector options = MaxHitsOption.getAllOptions();
        JComboBox limitCombo = new JComboBox(options);
        UpdateSearchRequest maxHitsListener =
                new UpdateSearchRequest(searchRequest);
        limitCombo.addActionListener(maxHitsListener);

        return limitCombo;
    }

    /**
     * Creates Organism Pull-Down Menu.
     */
    private JComboBox createOrganismComboBox () {
        Vector options = OrganismOption.getAllOptions();
        JComboBox orgCombo = new JComboBox(options);
        UpdateSearchRequest organismListener =
                new UpdateSearchRequest(searchRequest);
        orgCombo.addActionListener(organismListener);
        return orgCombo;
    }

    /**
     * Receive Notification of Changes to the Search List.
     *
     * @param o   Observable Object.
     * @param arg Observable Arguments.
     */
    public void update (Observable o, Object arg) {
        this.setVisible(true);
        int numSearches = searchList.getNumSearchBundles();
        SearchBundle bundle = searchList.getSearchBundleByIndex
                (numSearches - 1);
        SearchResponse searchResponse = bundle.getResponse();
        Throwable exception = searchResponse.getException();
        if (exception != null) {
            if (exception instanceof InterruptedException) {
                //  Do Nothing
            } else {
                showError(exception);
            }
        }

        // Re-enable search button.
        searchButton.setEnabled(true);
    }

    /**
     * Show Error Message.
     *
     * @param exception Exception.
     */
    private void showError (Throwable exception) {
        ErrorDisplay errorDisplay = new ErrorDisplay(this);
        errorDisplay.displayError(exception, consolePanel);
    }
}
