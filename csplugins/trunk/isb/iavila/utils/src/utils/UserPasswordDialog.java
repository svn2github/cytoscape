
package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author <a href="mailto:dreiss@systemsbiology.org">Iliana Avila-Campillo</a>
 */

public class UserPasswordDialog extends JDialog {

  protected JTextField userField;
  protected JPasswordField passField;
  
  public UserPasswordDialog (String title) { 
    setTitle(title);
    create();
  }

  public String getUserName (){
      return this.userField.getText();
  }
  
  public char[] getPassword (){
      return this.passField.getPassword();
  }
  
  protected void create (){
    
    
    JPanel cp = (JPanel)getContentPane();

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout( new FlowLayout() );
    centerPanel.add( new JLabel( "Username: " ) );
    userField = new JTextField( 25 );
    //userField.setText( "USER" );
    userField.selectAll();
    centerPanel.add( userField );
    cp.add( centerPanel, BorderLayout.NORTH );

    centerPanel = new JPanel();
    centerPanel.setLayout( new FlowLayout() );
    centerPanel.add( new JLabel( "Password: " ) );
    passField = new JPasswordField( 25 );
    //passField.setText( "PASS" );
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
        public void actionPerformed( ActionEvent e ) { dispose();} } );
    butPanel.add( but );
    but = new JButton( "OK" );
    but.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dispose();
            }
    }
    );
    butPanel.add( but );
    cp.add( butPanel, BorderLayout.SOUTH );
    butPanel.getRootPane().setDefaultButton( but );
    doLayout();
    pack();
    setModal( true );
  }
}
