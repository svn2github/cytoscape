package org.cytoscape.work.internal.tunables;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.cytoscape.io.DataCategory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.AbstractGUITunableHandler;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.internal.tunables.utils.FileChooserFilter;
import org.cytoscape.work.internal.tunables.utils.SupportedFileTypesManager;


/**
 * Handler for the type <i>File</i> of <code>Tunable</code>
 *
 * @author pasteur
 */
public class FileHandler extends AbstractGUITunableHandler {
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
	 * It creates the GUI which displays the path of the current file in a field, and provides access to a FileChooser with filtering parameters on
	 * <i>network</i>,<i>attributes</i>, or <i>session</i> (parameters are set in the <code>Tunable</code>'s annotations of the <code>File</code>)
	 *
	 *
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 * @param fileTypesManager 
	 */
	protected FileHandler(Field f, Object o, Tunable t, final SupportedFileTypesManager fileTypesManager) {
		super(f, o, t);
		this.fileTypesManager = fileTypesManager;
		init();
	}

	protected FileHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable, final SupportedFileTypesManager fileTypesManager) {
		super(getter, setter, instance, tunable);
		this.fileTypesManager = fileTypesManager;
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
		titleLabel.setText("Import " + initialCaps(fileCategory) + " File");
		List<FileChooserFilter> filters = fileTypesManager.getSupportedFileTypes(DataCategory.valueOf(fileCategory), input);
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

	//Click on the "open" button actionlistener
	private class myFileActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			if (ae.getActionCommand().equals("open")) {
				int ret = fileChooser.showOpenDialog(panel);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (file != null) {
						fileTextField.setFont(new Font(null, Font.PLAIN,10));
						fileTextField.setText(file.getAbsolutePath());
						fileTextField.removeMouseListener(mouseClick);
					}
				}
			} else if (ae.getActionCommand().equals("save")) {
				int ret = fileChooser.showSaveDialog(panel);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (file != null) {
						fileTextField.setFont(new Font(null, Font.PLAIN,10));
						fileTextField.setText(file.getAbsolutePath());
						fileTextField.removeMouseListener(mouseClick);
					}
				}
			}
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
