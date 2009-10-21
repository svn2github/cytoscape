package org.cytoscape.work.internal.tunables;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.FileTypeChoice;
import org.cytoscape.io.CyFileFilter;

import cytoscape.Cytoscape;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * Handler for the type <i>File</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class FileHandler extends AbstractGuiHandler {

	private File file;
	private JFileChooser fileChooser;
	private JButton chooseButton;
	private JTextField fileTextField;
	private ImageIcon image;
	private JLabel titleLabel;
	private JSeparator titleSeparator;
	private MouseClic mouseClic;
	private GroupLayout layout;
	private FileTypeChoice fileTypeChoice;
	private Map<FileFilter,CyFileFilter> fileFilterMap;
	//private FileUtil flUtil;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>File</code> type
	 * 
	 * It creates the GUI which displays the path of the current file in a field, and provides access to a FileChooser with filtering parameters on 
	 * <i>network</i>,<i>attributes</i>, or <i>session</i> (parameters are set in the <code>Tunable</code>'s annotations of the <code>File</code>)
	 * 
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	protected FileHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
//		this.flUtil = flUtil;
		
		//Construction of GUI
		fileChooser = new JFileChooser();
		setGui();
		setLayout();
		panel.setLayout(layout);
		
		try{
			this.file=(File)f.get(o);
		}catch(Exception e){e.printStackTrace();}		

		detectFileTypeChoice();

	}

	void detectFileTypeChoice()
	{
		String fieldName = t.fileTypeChoiceName();
		if (fieldName == null || fieldName.length() == 0)
		{
			fileTypeChoice = null;
			fileFilterMap = null;
			return;
		}

		try
		{
			Field field = o.getClass().getDeclaredField(fieldName);
			if (!field.getType().equals(FileTypeChoice.class))
				throw new Exception("the variable is declared as " + field.getType().toString() + ", not org.cytoscape.work.util.FileTypeChoice");
			fileTypeChoice = (FileTypeChoice) field.get(o);

			fileFilterMap = new HashMap<FileFilter,CyFileFilter>();
			for (CyFileFilter cyFileFilter : fileTypeChoice.getChoices())
			{
				String[] extensions = cyFileFilter.getExtensions().toArray(new String[0]);
				FileFilter filter = new FileNameExtensionFilter(cyFileFilter.getDescription(), extensions);
				fileChooser.addChoosableFileFilter(filter);
				fileFilterMap.put(filter, cyFileFilter);
			}
			fileChooser.setAcceptAllFileFilterUsed(fileTypeChoice.allowAcceptAll());

		}
		catch (Exception e)
		{
			logger.debug("Could not access variable \'" + fieldName + "\' in Tunable \'" + t.toString() + "\'  because: " + e.getMessage());
			fileTypeChoice = null;
			fileFilterMap = null;
		}
	}

	
	/**
	 * To set a path to the object <code>File</code> <code>o</code>
	 * 
	 * It creates a new <code>File</code> from the selected file in the FileChooser, or from the path to a file, entered by the user in the field
	 * The initial <code>File</code> object <code>o</code> is set with this new file 
	 */
	public void handle() {
		try{
			f.set(o,new File(fileTextField.getText()));
		}catch(Exception e){e.printStackTrace();}
		logger.debug("File selected is : " + fileTextField.getText());
	}

	
	/**
	 * To reset the current file, and set it to the initial one with no path
	 */
	public void resetValue() {
		try{
			f.set(o, new File(""));
		}catch(Exception e){e.printStackTrace();}
	} 
	
	
	/**
	 * To get the string representing the <code>File</code> contained in <code>FileHandler</code> : 
	 * 
	 * @return the representation of the object <code>o</code> contained in <code>f</code>
	 */
    public String getState() {
		String state;
		try {
			Object obj = f.get(o);
			if ( obj == null )
				state = "";
			else
				state = obj.toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			state = "";
		}
		return state;
    }

    
    
    
    //construction of the GUI depending on the file type expected: 
    //	-field to display the file's path
    //	-button to open the FileCHooser
    //add listener to the field and button
    private void setGui(){
		titleSeparator = new JSeparator();
		titleLabel = new JLabel();
		image = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_open.png"));
		fileTextField = new JTextField();
		fileTextField.setName("fileTextField");
		fileTextField.setEditable(true);
		fileTextField.setFont(new Font(null, Font.ITALIC,12));
		mouseClic = new MouseClic(fileTextField);
		fileTextField.addMouseListener(mouseClic);
		chooseButton = new JButton("Open a File...",image);
		chooseButton.setActionCommand("open");
		chooseButton.addActionListener(new myFileActionListener());
		

		//set title and textfield text for attribute type
		fileTextField.setText("Please select a file...");
		titleLabel.setText("import file");
    }
   
   
	//diplays the panel's component in a good view
	private void setLayout(){
		layout = new GroupLayout(panel);
		
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(titleLabel,GroupLayout.PREFERRED_SIZE,350,GroupLayout.PREFERRED_SIZE)
								.addComponent(titleSeparator,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(fileTextField,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(chooseButton))
						)
						.addContainerGap()));
		
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(titleLabel)
						.addGap(8, 8, 8)
						.addComponent(titleSeparator,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.addGap(7, 7, 7)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(chooseButton)
								.addComponent(fileTextField))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,3, Short.MAX_VALUE)
						.addContainerGap()));
	}
    

	
	//Click on the "open" button actionlistener
    private class myFileActionListener implements ActionListener{
    	public void actionPerformed(ActionEvent ae){
    		if(ae.getActionCommand().equals("open")){
			if (fileChooser.getChoosableFileFilters().length != 0)
				fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);
    			int ret = fileChooser.showOpenDialog(panel);
    			if (ret == JFileChooser.APPROVE_OPTION) {
    				File file = fileChooser.getSelectedFile();
//					File file = flUtil.getFile("TEST",flUtil.LOAD);
    				if ( file != null ) {
    					fileTextField.setFont(new Font(null, Font.PLAIN,10));
    					fileTextField.setText(file.getAbsolutePath());
    					fileTextField.removeMouseListener(mouseClic);
					if (fileTypeChoice != null)
					{
						CyFileFilter cyFileFilter = fileFilterMap.get(fileChooser.getFileFilter());
						fileTypeChoice.setSelectedFileFilter(cyFileFilter);
					}
    				}
    			}
    		}
    	}
    }
    
    
    //click on the field : removes its initial text
    private class MouseClic extends MouseAdapter implements MouseListener{
    	JComponent component;
    	public MouseClic(JComponent component) {
    		this.component = component;
    	}
    	public void mouseClicked(MouseEvent e){
    		((JTextField)component).setText("");
    	}
    }
    
}
