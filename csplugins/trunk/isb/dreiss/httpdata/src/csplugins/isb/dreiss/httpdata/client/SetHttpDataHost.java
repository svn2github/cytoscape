package csplugins.isb.dreiss.httpdata.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SetHttpDataHost extends AbstractAction {
   JFrame frame = null;

   public SetHttpDataHost( JFrame wind ) { super( "Set HttpData Host..." ); this.frame = wind; }

   public void actionPerformed( ActionEvent e ) { setHost( frame ); }

   protected static void setHost( JFrame frame ) {
      final JDialog dialog = new JDialog();
      JPanel cp = (JPanel) dialog.getContentPane();
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout( new FlowLayout() );
      centerPanel.add( new JLabel( "Use this host: " ) );
      final JTextField portField = new JTextField( 40 );
      portField.setText( DataClientFactory.STATIC_HOST );
      centerPanel.add( portField );
      cp.add( centerPanel, BorderLayout.NORTH );
      centerPanel = new JPanel();
      JPanel butPanel = new JPanel();
      butPanel.setLayout( new FlowLayout() );
      JButton but = new JButton( "Default" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) { portField.setText( DataClientFactory.DEFAULT_HOST ); } } );
      butPanel.add( but );
      but = new JButton( "Cancel" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) { dialog.dispose(); } } );
      butPanel.add( but );
      but = new JButton( "OK" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
	       try { 
		  String host = portField.getText();
		  dialog.dispose();
		  DataClientFactory.STATIC_HOST = host;
	       } catch ( Exception ee ) {
		  ee.printStackTrace();
	       }
	 } } );
	 butPanel.add( but );
	 cp.add( butPanel, BorderLayout.SOUTH );
	 dialog.doLayout();
	 dialog.pack();
	 dialog.setLocationRelativeTo( frame );
	 dialog.setVisible( true );
   }
}
