package filter.view;
import filter.model.*;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener; 
import java.awt.BorderLayout;
import java.awt.event.*;



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
	filterEditorList = new JList();
	Vector listItems = new Vector();
	for (Iterator editorIt = filterManager.getEditors(); editorIt.hasNext();){
	    listItems.add(editorIt.next());
	}
	filterEditorList = new JList(listItems);
	
	getContentPane().add(filterEditorList,BorderLayout.WEST);
	getContentPane().add(new DescriptionPanel(filterEditorList),BorderLayout.NORTH);
	JButton goButton = new JButton("OK");
	getContentPane().add(goButton,BorderLayout.SOUTH);
	goButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    //first get the selected editor type
		    FilterEditor selectedEditor = (FilterEditor)filterEditorList.getSelectedValue();
		    Filter newFilter = selectedEditor.createDefaultFilter();
		    FilterManager.defaultManager().addFilter(newFilter);
		    CreateFilterDialog.this.hide();
		}
	    });
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
	    setLayout(new BorderLayout());
	    getContentPane().add(descriptionField,BorderLayout.CENTER);
	    
	}

	public void valueChanged(ListSelectionEvent e){
	    JList filterEditorList = (JList)e.getSource();
	    FilterEditor currentEditor = (FilterEditor)filterEditorList.getSelectedValue();
	    descriptionField.setText(currentEditor.getDescription());
	    System.err.println("List selection value ahs changed");
	}

	
    }
}
