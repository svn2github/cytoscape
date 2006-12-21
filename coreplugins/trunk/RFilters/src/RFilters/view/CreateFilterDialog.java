package filter.view;
import filter.model.*;
import javax.swing.border.TitledBorder;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener; 
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Dimension;



public class CreateFilterDialog extends JDialog{
    FilterEditorManager filterManager;
    JList filterEditorList;
    JTextField nameField;
    public CreateFilterDialog(FilterEditorManager filterManager){
	this.filterManager = filterManager;
	initializeDialog();
    }
    
    public void initializeDialog(){
	setModal(true);
	setTitle("Filter Creation Dialog");
	getContentPane().setLayout(new BorderLayout());
	filterEditorList = new JList();
	Vector listItems = new Vector();
	for (Iterator editorIt = filterManager.getEditors(); editorIt.hasNext();){
	    listItems.add(editorIt.next());
	}
	filterEditorList = new JList(listItems);
	JPanel westPanel = new JPanel(new BorderLayout());
	westPanel.add(new JScrollPane(filterEditorList),BorderLayout.CENTER);
	westPanel.setBorder(new TitledBorder("Filter types"));
	getContentPane().add(westPanel,BorderLayout.WEST);
	//westPanel.setPreferredSize(new Dimension(100,150));

        DescriptionPanel descriptionPanel = new DescriptionPanel(filterEditorList);
	filterEditorList.addListSelectionListener(descriptionPanel);
	getContentPane().add(descriptionPanel,BorderLayout.CENTER);

	JPanel southPanel = new JPanel();
	JButton goButton = new JButton("OK");
	//getContentPane().add(goButton,BorderLayout.SOUTH);
	goButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    //first get the selected editor type
		    FilterEditor selectedEditor = (FilterEditor)filterEditorList.getSelectedValue();
		    Filter newFilter = selectedEditor.createDefaultFilter();
		    FilterManager.defaultManager().addFilter(newFilter);
		    CreateFilterDialog.this.setVisible(false);
		    
		    //NodeTopologyFilterEditor is a special case, since there is only one instance for each editor,
		    //and the model for the combobox in NodeTopologyFilterEditor must keep update if more than one filter
		    //is created
		    if (selectedEditor.getClass().getName().equals("filter.cytoscape.NodeTopologyFilterEditor")) {
		    	((filter.cytoscape.NodeTopologyFilterEditor)filterEditorList.getSelectedValue()).resetFilterBoxModel();
		    }
		}
	    });
	southPanel.add(goButton);
	getContentPane().add(southPanel,BorderLayout.SOUTH);
	pack();
	filterEditorList.setSelectedIndex(0);
    }



  class DescriptionPanel extends JPanel implements ListSelectionListener{
    JTextArea descriptionField;
    public DescriptionPanel(JList filterEditorList){
      filterEditorList.addListSelectionListener(this);
      descriptionField = new JTextArea();
      descriptionField.setLineWrap(true);
      descriptionField.setWrapStyleWord(true);
      descriptionField.setBackground(getBackground());
      setLayout(new BorderLayout());
      add(descriptionField,BorderLayout.CENTER);
      setBorder(new TitledBorder("Filter Type Description"));
      setPreferredSize(new Dimension(200,150));
    }
    
    public void valueChanged(ListSelectionEvent e){
      
      JList filterEditorList = (JList)e.getSource();
      FilterEditor currentEditor = (FilterEditor)filterEditorList.getSelectedValue();
      descriptionField.setText(currentEditor.getDescription());
    }
    
    
  }
}
