/**
 *
 */
package org.systemsbiology.cytoscape.dialog;


import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.JTextArea;

import org.systemsbiology.cytoscape.dialog.GooseDialog2.GooseButton;


/**
 * todo - delete this class. it is not used (GooseDialog2 is used instead)
 *
 * @author skillcoy
 */
public class GooseDialog extends JPanel {
    private JComboBox gooseChooser;
    private JComboBox layoutChooser;
    private JButton registerButton;
    //private JButton updateButton;
    private JButton setIdButton;
    private JButton showButton;
    private JButton hideButton;
    private JButton listButton;
    private JButton networkButton;
    private JButton matrixButton;
    private JButton mapButton;
    private JTextArea messageArea;
    private JLabel messageLabel;

    public GooseDialog() {
        createDialog();
    }

    public enum GooseButton {
        REGISTER("Register"), SHOW("Show"), HIDE("Hide"), MAP(
            "Map"), MATRIX("Matrix"), NETWORK("Network"), LIST("List");

        private String buttonName;

        private GooseButton(String name) {
            buttonName = name;
        }

    }

    public void displayMessage(String msg) {
        this.messageArea.setText(msg);
    }

    public void addButtonAction(GooseButton gb, ActionListener l) {
        javax.swing.JButton button = null;
        switch (gb) {
            case REGISTER:
                button = this.registerButton;
                break;
            case SHOW:
                button = this.showButton;
                break;
            case HIDE:
                button = this.hideButton;
                break;
            case MAP:
                button = this.mapButton;
                break;
            case MATRIX:
                button = this.matrixButton;
                break;
            case NETWORK:
                button = this.networkButton;
                break;
            case LIST:
                button = this.listButton;
                break;
        }
        button.addActionListener(l);
    }

    public void enableButton(GooseButton gb, boolean enabled) {
        javax.swing.JButton button = null;
        switch (gb) {
            case REGISTER:
                button = this.registerButton;
                break;
            case SHOW:
                button = this.showButton;
                break;
            case HIDE:
                button = this.hideButton;
                break;
            case MAP:
                button = this.mapButton;
                break;
            case MATRIX:
                button = this.matrixButton;
                break;
            case NETWORK:
                button = this.networkButton;
                break;
            case LIST:
                button = this.listButton;
                break;
        }
        button.setEnabled(enabled);
    }

    public javax.swing.JComboBox getGooseChooser() {
        return this.gooseChooser;
    }

    public javax.swing.JComboBox getLayoutChooser() {
        return this.layoutChooser;
    }


    private Box getDisplayControlPanel() {
        Box DisplayControl = Box.createVerticalBox();

        // drop-down menu for goose selection
        gooseChooser = new JComboBox();
        gooseChooser.addItem("Boss");

        JPanel ButtonPanel = new JPanel();

        registerButton = new JButton("Register");
        registerButton.setToolTipText("Register with the Boss"); // currently not
        // in use
        ButtonPanel.add(registerButton);

        // update button to re-populate boss and all active geese
//		updateButton = new JButton("Update");
//		updateButton.setToolTipText("Update goose list");
//		ButtonPanel.add(updateButton);

        // Show selected goose
        showButton = new JButton(" Show ");
        showButton.setToolTipText("Show selected goose");
        ButtonPanel.add(showButton);

        // Hide selected goose
        hideButton = new JButton(" Hide ");
        hideButton.setToolTipText("Hide selected goose");
        // DisplayControl.add(hideButton);
        ButtonPanel.add(hideButton);

        DisplayControl.add(gooseChooser);
        DisplayControl.add(ButtonPanel);

        return DisplayControl;
    }

    private Box getBroadcastPanel() {
        Box BroadcastPanel = Box.createHorizontalBox();

        BroadcastPanel.add(createMapButton());
        BroadcastPanel.add(createMatrixButton());
        BroadcastPanel.add(createNetworkButton());
        BroadcastPanel.add(createListButton());

        return BroadcastPanel;
    }

    // Not currently in use
    private JButton createIdButton() {
        // TODO this permits users to change the alias used by cytoscape to
        // broadcast data
        setIdButton = new JButton(" Set Alias ");
        setIdButton.setToolTipText("Set node alias to use in broadcast");
        return setIdButton;
    }

    private JButton createMapButton() {
        // Broadcast HashMap
        mapButton = new JButton("Map");
        mapButton.setToolTipText("Broadcast HashMap");
        mapButton.setForeground(Color.CYAN);
        return mapButton;
    }

    private JButton createMatrixButton() {
        // Broadcast DataMatrix
        matrixButton = new JButton("Matrix");
        matrixButton.setToolTipText("Broadcast Matrix");
        matrixButton.setForeground(Color.BLUE);
        return matrixButton;
    }

    private JButton createNetworkButton() {
        // Broadcast network
        networkButton = new JButton("Net");
        networkButton.setToolTipText("Broadcast Selected Network");
        networkButton.setForeground(Color.RED);
        return networkButton;
    }

    private JButton createListButton() {
        // Broadcast name list
        listButton = new JButton("List");
        listButton.setToolTipText("Broadcast Selected Name List");
        listButton.setForeground(Color.MAGENTA);
        return listButton;
    }

    private JTextArea createMessageArea() {
        // message area for any info the goose may wish to show
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        return messageArea;
    }

    private void createDialog() {
        Box GaggleToolBar = Box.createVerticalBox();
        JPanel Blank = new JPanel();
        Blank.setSize(5, 30);

        JPanel ConnectDisplayPane = new JPanel(new BorderLayout());
        ConnectDisplayPane.add(new JToolBar().add(getDisplayControlPanel()),
                BorderLayout.NORTH);
        ConnectDisplayPane.add(Blank, BorderLayout.SOUTH);

        JPanel Broadcast = new JPanel(new BorderLayout());
        Broadcast.add(Blank, BorderLayout.NORTH);
        Broadcast.add(new JLabel("Broadcast Data:"), BorderLayout.CENTER);
        Broadcast.add(new JToolBar().add(getBroadcastPanel()), BorderLayout.SOUTH);

        JPanel CyLayouts = new JPanel(new BorderLayout());
        CyLayouts.add(Blank, BorderLayout.NORTH);
        CyLayouts.add(new JLabel("Select Default Layout:"), BorderLayout.CENTER);
        this.layoutChooser = new JComboBox();
        CyLayouts.add(this.layoutChooser, BorderLayout.SOUTH);

        JPanel MessageDisplay = new JPanel(new BorderLayout());
        messageLabel = new JLabel("Current Data Type:");
        MessageDisplay.add(Blank, BorderLayout.NORTH);
        MessageDisplay.add(messageLabel, BorderLayout.CENTER);
        MessageDisplay.add(createMessageArea(), BorderLayout.SOUTH);

        GaggleToolBar.add(ConnectDisplayPane, BorderLayout.NORTH);
        GaggleToolBar.add(Broadcast, BorderLayout.CENTER);
        GaggleToolBar.add(CyLayouts, BorderLayout.SOUTH);
        GaggleToolBar.add(MessageDisplay, BorderLayout.SOUTH);

        add(GaggleToolBar);
    }

}
