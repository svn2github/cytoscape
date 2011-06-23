package org.cytoscape.work.internal.tunables;


import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.cytoscape.io.DataCategory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.internal.tunables.utils.FileChooserFilter;
import org.cytoscape.work.internal.tunables.utils.SupportedFileTypesManager;
import org.cytoscape.work.swing.AbstractGUITunableHandler;


/**
 * Handler for the type <i>File</i> of <code>Tunable</code>
 *
 * @author pasteur
 */
public class FileHandler extends AbstractGUITunableHandler {
	private static final String DEF_DIRECTORY = System.getProperty("user.home");
	private static final String LAST_DIRECTORY = "directory.last";
	
	// Core Cytoscape props
	private final Properties props;
	
	private JFileChooser fileChooser;
	
	private JButton chooseButton;
	private JTextField fileTextField;
	private ImageIcon image;
	private JLabel titleLabel;
	private JSeparator titleSeparator;
	private MouseClick mouseClick;
	private GroupLayout layout;
	private SupportedFileTypesManager fileTypesManager;
	private boolean input;

	/**
	 * Constructs the <code>GUIHandler</code> for the <code>File</code> type
	 *
	 * It creates the GUI which displays the path of the current file in a field, and provides
	 * access to a FileChooser with filtering parameters on
	 * <i>network</i>,<i>attributes</i>, or <i>session</i> (parameters are set in the <code>Tunable</code>'s annotations of the <code>File</code>)
	 *
	 *
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 * @param fileTypesManager 
	 */
	protected FileHandler(Field f, Object o, Tunable t, final SupportedFileTypesManager fileTypesManager,
			Properties props) {
		super(f, o, t);
		this.fileTypesManager = fileTypesManager;
		this.props = props;
		init();
	}

	protected FileHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable,
			final SupportedFileTypesManager fileTypesManager, Properties props) {
		super(getter, setter, instance, tunable);
		this.fileTypesManager = fileTypesManager;
		this.props = props;
		init();
	}

	private void init() {
		//Construction of GUI
		fileChooser = new JFileChooser();
		input = isInput();
		setGui();
		setLayout();
		panel.setLayout(layout);
	}

	/**
	 * To set a path to the object <code>File</code> <code>o</code>
	 *
	 * It creates a new <code>File</code> from the selected file in the FileChooser, or from the path to a file, entered by the user in the field
	 * The initial <code>File</code> object <code>o</code> is set with this new file
	 */
	public void handle() {
		try {
			setValue(new File(fileTextField.getText()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	//construction of the GUI depending on the file type expected:
	//	-field to display the file's path
	//	-button to open the FileCHooser
	//add listener to the field and button
	private void setGui() {
		titleSeparator = new JSeparator();
		titleLabel = new JLabel();
		image = new ImageIcon(getClass().getResource("/images/ximian/stock_open.png"));
		fileTextField = new JTextField();
		fileTextField.setName("fileTextField");
		fileTextField.setEditable(true);
		fileTextField.setFont(new Font(null, Font.ITALIC,12));
		mouseClick = new MouseClick(fileTextField);
		fileTextField.addMouseListener(mouseClick);
		chooseButton = new JButton(input ? "Open a File..." : "Save a File...", image);
		chooseButton.setActionCommand(input ? "open" : "save");
		chooseButton.addActionListener(new myFileActionListener());

		//set title and textfield text for the file type
		final String fileCategory = getFileCategory().toUpperCase();
		fileTextField.setText("Please select a " + fileCategory.toLowerCase() + " file...");
		titleLabel.setText((input ? "Load " : "Save ") + initialCaps(fileCategory) + " File");
		
		final List<FileChooserFilter> filters = fileTypesManager.getSupportedFileTypes(
				DataCategory.valueOf(fileCategory), input);
		for (FileChooserFilter filter : filters)
			fileChooser.addChoosableFileFilter(filter);
	}

	private String getFileCategory() {
		return getParams().getProperty("fileCategory", "unspecified");
	}

	private boolean isInput() {
		return getParams().getProperty("input", "false").equalsIgnoreCase("true");
	}

	private String initialCaps(final String s) {
		if (s.isEmpty())
			return "";
		else
			return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}

	//diplays the panel's component in a good view
	private void setLayout() {
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

	// Click on the "open" or "save" button action listener
	private final class myFileActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			File file = null;
			final String lastDir = props.getProperty(LAST_DIRECTORY, DEF_DIRECTORY);
			
			File lastDirFile;
			try {
				lastDirFile = new File(lastDir);
			} catch (Exception e){
				lastDirFile = new File(DEF_DIRECTORY);
			}
			
			if (!lastDirFile.isDirectory())
				lastDirFile = new File(DEF_DIRECTORY);
			
			fileChooser.setCurrentDirectory(lastDirFile);
			
try_again:              {
				int ret = JFileChooser.CANCEL_OPTION;
				if (ae.getActionCommand().equals("open"))
					ret = fileChooser.showOpenDialog(panel);
				else if (ae.getActionCommand().equals("save"))
					ret = fileChooser.showSaveDialog(panel);
			
				if (ret == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					if (file != null) {
						if (ae.getActionCommand().equals("save") && file.exists()) {
							if (JOptionPane.showConfirmDialog(
								panel,
								"The file you selected already exists.  "
								+ "Are you sure you want to overwrite it?",
								"Confirmation",
								JOptionPane.YES_NO_OPTION)
							    == JOptionPane.NO_OPTION)
								break try_again;
						}

						fileTextField.setFont(new Font(null, Font.PLAIN, 10));
						fileTextField.setText(file.getAbsolutePath());
						fileTextField.removeMouseListener(mouseClick);
					}
				}
			}
			props.put(LAST_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
		}
	}

	//click on the field : removes its initial text
	private class MouseClick extends MouseAdapter implements MouseListener{
		JComponent component;

		public MouseClick(JComponent component) {
			this.component = component;
		}

		public void mouseClicked(MouseEvent e) {
			((JTextField)component).setText("");
		}
	}

}
