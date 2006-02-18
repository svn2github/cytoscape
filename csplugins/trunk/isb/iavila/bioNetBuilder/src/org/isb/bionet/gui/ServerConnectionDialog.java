package org.isb.bionet.gui;
import javax.swing.*;
import java.awt.event.ActionEvent;


import org.isb.xmlrpc.client.*;

public class ServerConnectionDialog extends JDialog{
    
    protected JTextField urlTextField;
    
    /**
     * Default constructor
     *
     */
    public ServerConnectionDialog (){
        create();
    }
    
    /**
     * 
     * @param url the host URL
     */
    public ServerConnectionDialog (String url){
        create();
        setURL(url);
    }
    
    public void setURL (String url){
        this.urlTextField.setText(url);
        DataClientFactory.setHost(url);
        // need to get new clients for the different host
        
    }
    
    public String getURL (){ return this.urlTextField.getText();}
    
    
    /**
     * Creates the dialog
     */
    protected void create (){
        

        JPanel urlPanel = new JPanel();
        JLabel urlLabel = new JLabel("Server URL:");
        this.urlTextField = new JTextField();
        this.urlTextField.setColumns(50);
        urlPanel.add(urlLabel);
        urlPanel.add(Box.createHorizontalStrut(5));
        urlPanel.add(this.urlTextField);
        
        
        JPanel buttonsPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        JButton helpButton = new JButton("Help");
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createHorizontalStrut(5));
        buttonsPanel.add(cancelButton);
        okButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        setURL(urlTextField.getText());
                        ServerConnectionDialog.this.dispose();
                    }
            
                }
        );
        
        cancelButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        ServerConnectionDialog.this.dispose();
                    }
                    
                }
        );
        final String helpText = "<html><b>Server URL:</b> Enter the URL of the server and its listening port.<br>"+
                                "For example: http://db.systemsbiology.net:80<br></html>";
        helpButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        
                        JOptionPane.showMessageDialog(ServerConnectionDialog.this,helpText,"BioNet Builder Help",JOptionPane.INFORMATION_MESSAGE);
                    }
                }
        );
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.add(urlPanel);
        mainPanel.add(buttonsPanel);
        setContentPane(mainPanel);
    }
    
}