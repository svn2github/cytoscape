package GenericPlugin;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.filechooser.*;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;//TablemodelListener, TableModel, etc.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;//HashMap,Vector,Iterator

/* 
 * ColorEditor.java (compiles with releases 1.3 and 1.4) is used by 
 * TableDialogEditDemo.java.
 */



class FileCellEditor extends AbstractCellEditor
                         implements TableCellEditor,
			            ActionListener {
    Vector argVals;

    File[] currentFiles;
    JButton button;
    JFileChooser fileChooser;
    //JDialog dialog;

    //protected static final String EDIT = "edit";

    public FileCellEditor(Vector argVals) {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
	this.argVals=argVals;
        button = new JButton();
	//button = new JButton("Open a File...",createImageIcon("images/Open16.gif"));
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up (FILES_ONLY file chooser).
        fileChooser= new JFileChooser();
	//dialog = JColorChooser.createDialog(button,"Select Files",true/*modal*/,fileChooser,this/*OK button handler*/,null/*no CANCEL button handler*/);
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */

    public void actionPerformed(ActionEvent e) {
	//Handle open button action.
	if (e.getSource() == button) {	    
	    if (currentFiles.length>0){fileChooser.setSelectedFiles(currentFiles);}
	    int returnVal = fileChooser.showOpenDialog(button);
	    
	    if (returnVal == JFileChooser.APPROVE_OPTION){ 
		if (fileChooser.isMultiSelectionEnabled()==true){
		    currentFiles=fileChooser.getSelectedFiles();
		}
		else{ 
		    File newFile=(File)fileChooser.getSelectedFile();
		    currentFiles=new File[1];
		    currentFiles[0]=newFile;
		}
		//This is where a real application would open the file. - but this isnt a real application
		
	    } else {
		//log.append("Open command cancelled by user." + newline);
	    }
	}
    }
    
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
	if (fileChooser.isMultiSelectionEnabled()==true){
	    return currentFiles;
	}
	else{
	    return currentFiles[0];
	}

    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
	//NOTE:  This routine assumes the column chosen corresponds to the argVals Vector.  It ignores the current value of the cell (value), assuming that the correct value can be found in argVals

	//choose whether to look for a file or a file array (multiple files)
	if (argVals.elementAt(row).getClass().equals(File.class)){
	    fileChooser.setMultiSelectionEnabled(false);
	    File[] newFile={(File)argVals.elementAt(row)};
	    currentFiles=newFile;
	}
	else if (argVals.elementAt(row).getClass().equals(File[].class)){
	    fileChooser.setMultiSelectionEnabled(true);
	    currentFiles=(File[])argVals.elementAt(row);
	
	}
	else {throw new RuntimeException("Applied a FileEditor to the wrong cell type");}

	Vector validFiles=new Vector(currentFiles.length);
	for (int i=0;i<currentFiles.length;i++){
	    if (currentFiles[i]!=null && currentFiles[i].isFile()){
		validFiles.add(currentFiles[i]);
	    }
	}
	currentFiles=new File[validFiles.size()];
	for (int i=0;i<currentFiles.length;i++){
	    currentFiles[i]=(File)validFiles.elementAt(i);
	}
	
	button.setText("Choose file(s)...");
	button.repaint();
	System.out.println("about to return button");
        return button;
    }
}
