package org.isb.xmlrpc.db.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 */

public class SetUsernamePassword extends AbstractAction {
  
  protected JFrame frame = null;
  public boolean closed = false;
  
  public SetUsernamePassword (JFrame wind) { 
    super("Set HttpData username and password..."); 
    this.frame = wind; 
  }

  public void actionPerformed( ActionEvent e ) { setUsernameAndPassword(frame); }

  public void setUsernameAndPassword (final JFrame frame){
    
    closed = false;
    final JDialog dialog = new JDialog();
    JPanel cp = (JPanel) dialog.getContentPane();

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout( new FlowLayout() );
    centerPanel.add( new JLabel( "Username: " ) );
    final JTextField userField = new JTextField( 25 );
    if ( DataClientFactory.USERNAME != null ) userField.setText( DataClientFactory.USERNAME );
    else userField.setText( "USER" );
    userField.selectAll();
    centerPanel.add( userField );
    cp.add( centerPanel, BorderLayout.NORTH );

    centerPanel = new JPanel();
    centerPanel.setLayout( new FlowLayout() );
    centerPanel.add( new JLabel( "Password: " ) );
    final JPasswordField passField = new JPasswordField( 25 );
    if ( DataClientFactory.PASSWORD != null ) passField.setText( DataClientFactory.PASSWORD );
    else passField.setText( "PASS" );
    passField.selectAll();
    centerPanel.add( passField );
    cp.add( centerPanel, BorderLayout.CENTER );

    centerPanel = new JPanel();
    JPanel butPanel = new JPanel();
    butPanel.setLayout( new FlowLayout() );
    JButton but = new JButton( "Clear" );
    but.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) { 
          userField.setText( "" );
          passField.setText( "" );
        } } );
    butPanel.add( but );
    but = new JButton( "Cancel" );
    but.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) { dialog.dispose(); closed = true; } } );
    butPanel.add( but );
    but = new JButton( "OK" );
    but.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          try { 
            String user = userField.getText();
            String pass = passField.getText();
            DataClientFactory.setUserNamePassword( user, pass );
            dialog.dispose();
            closed = true;
          } catch ( Exception ee ) {
            ee.printStackTrace();
          }
        } } );
    butPanel.add( but );
    cp.add( butPanel, BorderLayout.SOUTH );
    butPanel.getRootPane().setDefaultButton( but );
    dialog.doLayout();
    dialog.pack();
    dialog.setLocationRelativeTo( frame );
    dialog.setModal( true );
    dialog.setVisible( true );
  }
}
