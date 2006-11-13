/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.dataviewer.prototype;

import csplugins.dataviewer.action.DisplayDetails;
import csplugins.dataviewer.action.ExecuteQuery;
import csplugins.dataviewer.action.UpdateSearchRequest;
import csplugins.dataviewer.model.*;
import csplugins.dataviewer.ui.ConsolePanel;
import csplugins.dataviewer.ui.ErrorDisplay;
import cytoscape.CyNetwork;
import cytoscape.data.SelectFilter;
import org.mskcc.dataservices.core.EmptySetException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * CPath Search Panel.
 *
 * @author Ethan Cerami.
 */
public class CPathSearchPanel extends JPanel implements Observer {
    /**
     * Frame Owner.
     */
    private JFrame owner;

    /**
     * Search Button.
     */
    private JButton searchButton;

    /**
     * Encapsulates Search Request Parameters (to be sent to cPath).
     */
    private SearchRequest searchRequest;

    /**
     * Console Panel for Displaying Status/Error/Warning Messages.
     */
    private ConsolePanel console;

    /**
     * Complete List of all Searches performed by the End-User.
     */
    private SearchBundleList searchBundleList;

    /**
     * Listens for User-Initiated Searches
     */
    private ExecuteQuery queryListener;

    /**
     * HashMap of Cytoscape Nodes/Edges.
     */
    private HashMap cyMap;

    private UserSelection userSelection;

    /**
     * Constructor.
     *
     * @param userSelection User Selection Object.
     * @param owner         JFrame Owner Object.
     */
    public CPathSearchPanel(UserSelection userSelection, JFrame owner) {
        this.userSelection = userSelection;
        this.owner = owner;
        initDataModel();
        initGui();
    }

    private void initDataModel() {

        //  Initialize Empty SearchBundleList
        searchBundleList = new SearchBundleList();

        //  Create Empty Search Query Object
        searchRequest = new SearchRequest();

        //  Register to Listen to changes to SearchList
        searchBundleList.addObserver(this);

        cyMap = new HashMap();
        console = new ConsolePanel();
//        queryListener = new ExecuteQuery(searchRequest,
//                searchBundleList, cyMap, console, this);
    }

    /**
     * Enables/Disables the Search Button.
     *
     * @param enabled Enabled Flag.
     */
    public void setSearchButtonEnabled(boolean enabled) {
        searchButton.setEnabled(enabled);
    }

    /**
     * Creates Nothern Panel with cPath Search Box.
     */
    private void initGui() {
        //  Use Box Layout along Y-Axis (will stack components vertically)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create Text Field and Button Panel
        JPanel textAndButtonPanel = createTextFieldAndButtonPanel();
        textAndButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(textAndButtonPanel);

        //  Create Organism Combo Box
        JComboBox orgCombo = createOrganismComboBox();
        orgCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(orgCombo);

        //  Create Result Limit Combo Box
        JComboBox limitCombo = createResultLimitComboBox();
        limitCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(limitCombo);

        //  Create Examples Panel
        JPanel examplePanel = createExamplePanel();
        examplePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(examplePanel);

        //  Add Vertical Glue to Bottom
        this.add(Box.createVerticalGlue());
    }

    /**
     * Creates Text Field and Search Button.
     */
    private JPanel createTextFieldAndButtonPanel() {
        JPanel textAndButtonPanel = new JPanel();
        textAndButtonPanel.setLayout(new BoxLayout(textAndButtonPanel,
                BoxLayout.X_AXIS));

        //  Create Search Text Field
        JTextField textField = new JTextField(10);
        Font font = textField.getFont();
        textField.setFont(new Font(font.getName(), Font.PLAIN, 11));
        textField.setToolTipText("Enter Search Term(s)");
        UpdateSearchRequest textListener =
                new UpdateSearchRequest(searchRequest);
        textField.addFocusListener(textListener);

        //  Set Min/Max Size
        textField.setMinimumSize(textField.getPreferredSize());
        textField.setMaximumSize(textField.getPreferredSize());
        textAndButtonPanel.add(textField);

        //  Create Search Button
        URL iconURL = Demo1.class.getResource
                ("resources/run_tool.gif");
        System.out.println("ICON URL:  " + iconURL);
        ImageIcon icon = new ImageIcon(iconURL);
        searchButton = new JButton(icon);
        searchButton.setToolTipText("Execute Search Query");
        searchButton.addActionListener(queryListener);
        textAndButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        textAndButtonPanel.add(searchButton);

        //  Set Alignment and Border
        textAndButtonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return textAndButtonPanel;
    }

    /**
     * Creates Search Examples Text
     */
    private JPanel createExamplePanel() {
        JLabel exampleHeader = new JLabel("Example Searches:");
        JLabel example1 = new JLabel("- dna repair");
        JLabel example2 = new JLabel("- dna AND repair");
        JLabel example3 = new JLabel("- \"dna repair\"");
        JPanel examplePanel = new JPanel();
        BoxLayout box = new BoxLayout(examplePanel, BoxLayout.Y_AXIS);
        examplePanel.setLayout(box);
        examplePanel.add(exampleHeader);
        examplePanel.add(example1);
        examplePanel.add(example2);
        examplePanel.add(example3);

        //  Set Border
        examplePanel.setBorder(new EmptyBorder(10, 2, 5, 5));
        return examplePanel;
    }

    /**
     * Creates Result Set Limit Pull-Down Menu.
     */
    private JComboBox createResultLimitComboBox() {
        Vector options = MaxHitsOption.getAllOptions();
        JComboBox limitCombo = new JComboBox(options);
        Font font = limitCombo.getFont();
        UpdateSearchRequest maxHitsListener =
                new UpdateSearchRequest(searchRequest);
        limitCombo.addActionListener(maxHitsListener);

        limitCombo.setFont(new Font(font.getName(), Font.PLAIN, 11));
        limitCombo.setToolTipText("Limit Number of Results");
        limitCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        //  Used to specify a default size for pull down menu;
        //  Particularly important for Windows, see Bug #520.
        limitCombo.setPrototypeDisplayValue
                (new String("Saccharomyces cerevisiae"));

        //  Set Max Size
        limitCombo.setMaximumSize(limitCombo.getPreferredSize());

        return limitCombo;
    }

    /**
     * Creates Organism Pull-Down Menu.
     */
    private JComboBox createOrganismComboBox() {
        Vector options = OrganismOption.getAllOptions();
        JComboBox orgCombo = new JComboBox(options);
        Font font = orgCombo.getFont();
        UpdateSearchRequest organismListener =
                new UpdateSearchRequest(searchRequest);
        orgCombo.addActionListener(organismListener);
        orgCombo.setFont(new Font(font.getName(), Font.PLAIN, 11));
        orgCombo.setToolTipText("Filter by Organism");

        //  Used to specify a default size for pull down menu
        orgCombo.setPrototypeDisplayValue
                (new String("Saccharomyces cerevisiae"));

        //  Set Max Size
        orgCombo.setMaximumSize(orgCombo.getPreferredSize());

        return orgCombo;
    }

    /**
     * Receive Notification of Changes to the Search List.
     *
     * @param o   Observable Object.
     * @param arg Observable Arguments.
     */
    public void update(Observable o, Object arg) {
        int numSearches = searchBundleList.getNumSearchBundles();
        SearchBundle bundle = searchBundleList.getSearchBundleByIndex
                (numSearches - 1);
        SearchResponse searchResponse = bundle.getResponse();
        Throwable exception = searchResponse.getException();
        if (exception != null) {
            if (exception instanceof EmptySetException) {
                String msg = "No Matching Results Found.  Please Try Again.";
                JOptionPane.showMessageDialog(this, msg, "cPath PlugIn",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (exception instanceof InterruptedException) {
                //  Do Nothing
            } else {
                showError(exception);
            }
        } else {
            CyNetwork cyNetwork = searchResponse.getCyNetwork();
            SelectFilter selectFilter = cyNetwork.getSelectFilter();
            DisplayDetails displayDetails = new DisplayDetails(cyMap,
                    userSelection);
            selectFilter.addSelectEventListener(displayDetails);

            //  Add the cPath Display Details Listener to the Network;
            //  This enables other plugins to retrieve the listener, and
            //  possibly reattach it to subnetworks, as we do with the
            //  Activity Center.  This is a bit of a hack as the current
            //  Cytoscape API does not allow clients to access a complete
            //  list of SelectListeners.
            cyNetwork.putClientData("CPATH_LISTENER", displayDetails);
        }

        // Re-enable search button.
        searchButton.setEnabled(true);
    }

    /**
     * Shows Error Message.
     *
     * @param exception Exception.
     */
    public void showError(Throwable exception) {
        ErrorDisplay errorDisplay = new ErrorDisplay(owner);
    }

    /**
     * Shows Message in Dialog Box.
     *
     * @param msg                Information Message
     * @param informationMessage Type of Message to be displayed.
     */
    public void showMessageDialog(String msg, int informationMessage) {
        JOptionPane.showMessageDialog(this, msg, "cPath PlugIn",
                informationMessage);
    }
}
