package csplugins.isb.dreiss.getInteractions;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InteractionsDialog extends JDialog {
   protected GetInteractions getInteractions;
   protected boolean valid = false;
   protected Map arguments;
   protected Vector sourcecbs = new Vector();
   protected Vector homologcbs = new Vector();

   public InteractionsDialog( GetInteractions gi,
			      Map args, Map sources, Map homologs ) {
      super();
      this.getInteractions = gi;
      this.arguments = args;

      setTitle( "Fetch Interactions" );
      getContentPane().setLayout( new BorderLayout() );
      JPanel panel = createGui( sources, homologs );
      if ( panel == null ) { valid = false; return; }
      valid = true;
      getContentPane().add( panel, BorderLayout.CENTER );

      JPanel buttonPanel = new JPanel();
      JButton dismissButton = new JButton( "Dismiss" );
      buttonPanel.add( dismissButton );
      dismissButton.addActionListener( new DismissAction() );
      JButton goButton = new JButton( "GO" );
      buttonPanel.add( goButton );
      goButton.addActionListener( new GoAction() );
      getContentPane().add( buttonPanel, BorderLayout.SOUTH );
   }

   public boolean isValid() { return valid; }

   JPanel createGui( Map sources, Map homologs ) {
      JPanel contentPanel = new JPanel();
      contentPanel.setLayout( new BorderLayout() );

      JPanel chooserPanel = new JPanel();
      chooserPanel.setBorder( BorderFactory.createTitledBorder( "Choose the interaction data sources to include:" ) );
      chooserPanel.setLayout( new GridLayout( 5, 2 ) );
      if ( sources != null && sources.size() > 0 ) {
	 for ( Iterator it = sources.keySet().iterator(); it.hasNext(); ) {
	    String source = (String) it.next();
	    JCheckBox cb = new JCheckBox( (String) sources.get( source ), false );
	    cb.addItemListener( new CBAction( source, false ) );
	    chooserPanel.add( cb );
	    sourcecbs.add( cb );
	 }
	 JButton jb = new JButton( "Select All" );
	 jb.addActionListener( new SelectAllAction( sourcecbs ) );
	 chooserPanel.add( jb );
      } else {
	 chooserPanel.add( new JLabel( "None available!" ) );
      }
      
      contentPanel.add( chooserPanel, BorderLayout.NORTH );

      JPanel configPanel = new JPanel();
      configPanel.setBorder( BorderFactory.createTitledBorder( "General Options:" ) );
      JPanel radioPanel = new JPanel();
      radioPanel.setLayout( new GridLayout( 3, 1 ) );
      radioPanel.setBorder( BorderFactory.createEtchedBorder() );
      ButtonGroup bg = new ButtonGroup();
      JRadioButton rb = new JRadioButton( "Add internal edges only", false );
      radioPanel.add( rb );
      rb.addItemListener( new CBAction( "internalOnly", false ) );
      bg.add( rb );
      rb = new JRadioButton( "Add edges only between selected nodes", false );
      radioPanel.add( rb );
      rb.addItemListener( new CBAction( "onlyBetweenSelected", false ) );
      bg.add( rb );
      rb = new JRadioButton( "Add new connected nodes", true );
      radioPanel.add( rb );
      rb.addItemListener( new CBAction( "addNewNodes", true ) );
      bg.add( rb );
      configPanel.add( radioPanel, BorderLayout.NORTH );
      JPanel cbPanel = new JPanel();
      cbPanel.setLayout( new GridLayout( 4, 1 ) );
      JCheckBox cb = new JCheckBox( "Add ortholog-inferred interactions", false );
      cbPanel.add( cb );
      cb.addItemListener( new CBAction( "useHomologs", false ) );
      if ( ! getInteractions.getHandler().graphHasNodeAttribute( "homolog" ) ) cb.setEnabled( false );
      cb = new JCheckBox( "Use the synonyms of each node", true );
      cbPanel.add( cb );
      cb.addItemListener( new CBAction( "useSynonyms", true ) );
      if ( ! getInteractions.getHandler().graphHasNodeAttribute( "synonym" ) ) {
	 cb.setSelected( false );
	 cb.setEnabled( false );
      }
      cb = new JCheckBox( "Re-layout the network when done", true );
      cbPanel.add( cb );
      cb.addItemListener( new CBAction( "relayout", true ) );
      cb = new JCheckBox( "Hilight newly-added edges/nodes", true );
      cbPanel.add( cb );
      cb.addItemListener( new CBAction( "selectNew", true ) );
      configPanel.add( cbPanel, BorderLayout.CENTER );
      contentPanel.add( configPanel, BorderLayout.CENTER );

      if ( homologs != null && homologs.size() > 0 ) {
	 boolean hasHomologs = getInteractions.getHandler().graphHasNodeAttribute( "homolog" );
	 JPanel homologPanel = new JPanel();
	 homologPanel.setBorder( BorderFactory.createTitledBorder( "Use orthologs with these species:" ) );
	 homologPanel.setLayout( new GridLayout( 5, 2 ) );
	 for ( Iterator it = homologs.keySet().iterator(); it.hasNext(); ) {
	    String shortName = (String) it.next();
	    String longName = (String) homologs.get( shortName );
	    cb = new JCheckBox( shortName, false );
	    cb.addItemListener( new CBAction( "HOMOLOG " + longName, false ) );
	    cb.setToolTipText( longName );
	    cb.setEnabled( hasHomologs );
	    homologPanel.add( cb );
	    homologcbs.add( cb );
	 }
	 JButton jb = new JButton( "Select All" );
	 jb.addActionListener( new SelectAllAction( homologcbs ) );
	 jb.setEnabled( hasHomologs );
	 homologPanel.add( jb );
	 contentPanel.add( homologPanel, BorderLayout.SOUTH );
	 if ( ! hasHomologs ) homologPanel.setEnabled( false );
      }

      return contentPanel;
   }

   class DismissAction extends AbstractAction {
      DismissAction() { super(); }
      public void actionPerformed (ActionEvent e) {
         InteractionsDialog.this.dispose(); }
   }

   class GoAction extends AbstractAction {
      GoAction() { super(); }
      public void actionPerformed( ActionEvent e ) {
         InteractionsDialog.this.getInteractions.findBindingPartners(); } 
   }

   class CBAction implements ItemListener {
      String param; Map args;
      CBAction( String param, boolean val ) { 
	 super(); 
	 this.param = param;
	 this.args = InteractionsDialog.this.arguments;
	 args.put( param, new Boolean( val ) );
      }
      public void itemStateChanged( ItemEvent e ) {
	 args.remove( param );
	 args.put( param, new Boolean( e.getStateChange() == ItemEvent.SELECTED ) );
      }
   }

   class SelectAllAction extends AbstractAction {
      protected Vector cbs;
      SelectAllAction( Vector cbs ) { super(); this.cbs = cbs; }
      public void actionPerformed( ActionEvent e ) {
	 JButton source = (JButton) e.getSource();
	 boolean doOn = false;
	 if ( source.getText().equals( "Select All" ) ) {
	    doOn = true;
	    source.setText( "Select None" );
	 } else if ( source.getText().equals( "Select None" ) ) {
	    doOn = false;
	    source.setText( "Select All" );
	 }
	 for ( int i = 0; i < cbs.size(); i ++ ) {
	    JCheckBox cb = (JCheckBox) cbs.get( i );
	    if ( cb.isSelected() != doOn ) cb.doClick();
	 }
      }
   }
}
