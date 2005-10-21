package browser;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.List;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
 
import cytoscape.*;
import cytoscape.data.*;

import filter.model.FilterManager;

import cytoscape.data.attr.*;

import giny.model.GraphObject;

public class ModPanel 
  extends JPanel 
  implements ActionListener {

  CyAttributes data;
  DataTableModel tableModel;
  AttributePanel attPanel;
  int graphObjectType;

  // Set/Modify
  JComboBox attributeModifyBox;
  JTextField inputField;
  JComboBox actionBox;
  JButton applyModify;

  // Copy
  JComboBox attributeCopyFromBox;
  JComboBox attributeCopytoBox;
  JButton copyGo;

  // Delete
  JComboBox attributeDeleteBox;
  JButton deleteGo;

  static String ADD = "Add";
  static String SET = "Set";
  static String MUL = "Mul";
  static String DIV = "Div";
  static String COPY = "Copy";
  static String DELETE = "Delete";

  public ModPanel ( CyAttributes data, 
                    DataTableModel tableModel, 
                    AttributePanel attPanel,
                    int graphObjectType ) {
   
    this.data = data;
    this.tableModel = tableModel;
    this.attPanel = attPanel;
    this.graphObjectType = graphObjectType;

    JTabbedPane tabs = new JTabbedPane();
    add( tabs );


    // create Operation panel
    JPanel operation = new JPanel();
    tabs.add( "Operation", operation );
    attributeModifyBox = createAttributeBox();
    attributeModifyBox.setEditable( true );
    inputField = new JTextField( 8 );
    actionBox = new JComboBox( new Object[] {SET, ADD, MUL, DIV} );
    applyModify= new JButton( "GO" ); ;
    applyModify.addActionListener( this );

    operation.add( actionBox );
    operation.add( attributeModifyBox );
    operation.add( new JLabel( "by/to" ) );
    operation.add( inputField );
    operation.add( applyModify );

    // create Copy Panel
    JPanel copy = new JPanel();
    tabs.add( "Copy", copy );
    attributeCopyFromBox = createAttributeBox();
    attributeCopytoBox = createAttributeBox();
    attributeCopytoBox.setEditable( true );
    copyGo =  new JButton( "GO" ); ;
    copyGo.addActionListener( this );
    
    copy.add( new JLabel( "Copy From:" ) );
    copy.add( attributeCopyFromBox );
    copy.add( new JLabel( "To:" ) );
    copy.add( attributeCopytoBox );
    copy.add( copyGo );
    
    // create Delete Panel
    JPanel delete = new JPanel();
    tabs.add( "Delete", delete );
    attributeDeleteBox = createAttributeBox();
    deleteGo =  new JButton( "GO" ); ;
    deleteGo.addActionListener( this );

    delete.add( new JLabel( "Delete" ) );
    delete.add( attributeDeleteBox );
    delete.add( deleteGo );

  }

  private JComboBox createAttributeBox () {
    JComboBox box =new JComboBox( new AttributeModel( data ) );
    Dimension newSize = new Dimension( 130, (int)box.getPreferredSize().getHeight());
    box.setMaximumSize(newSize);
    box.setPreferredSize(newSize);
    return box;
  }

  private JComboBox createFilterBox () {
    JComboBox box = new JComboBox( FilterManager.defaultManager().getComboBoxModel() );
    Dimension newSize = new Dimension( 130, (int)box.getPreferredSize().getHeight());
    box.setMaximumSize(newSize);
    box.setPreferredSize(newSize);
    return box;
  }

  public void actionPerformed( ActionEvent e ) {
    
    MultiDataEditAction edit;

    //operation
    if ( e.getSource() == applyModify ) {
      edit = new MultiDataEditAction( inputField.getText(),
                                      (String)actionBox.getSelectedItem(),
                                      tableModel.getObjects(),
                                      (String)attributeModifyBox.getSelectedItem(),
                                      null,
                                      null,
                                      graphObjectType,
                                      tableModel );
    } else if ( e.getSource() == copyGo ) {
      edit = new MultiDataEditAction( null,
                                      COPY,
                                      tableModel.getObjects(),
                                      (String)attributeCopyFromBox.getSelectedItem(),
                                      (String)attributeCopytoBox.getSelectedItem(),
                                      null,
                                      graphObjectType,
                                      tableModel );
    } else {
      edit = new MultiDataEditAction( null,
                                      DELETE,
                                      tableModel.getObjects(),
                                      (String)attributeDeleteBox.getSelectedItem(),
                                      null,
                                      null,
                                      graphObjectType,
                                      tableModel );
 
    }
    Cytoscape.getDesktop().addEdit( edit );
  }
  
}
