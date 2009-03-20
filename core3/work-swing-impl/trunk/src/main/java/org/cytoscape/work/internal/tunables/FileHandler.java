package org.cytoscape.work.internal.tunables;

import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import java.io.File;

import org.cytoscape.work.Tunable;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import cytoscape.Cytoscape;


public class FileHandler extends AbstractGuiHandler {

	File myFile;
	private JFileChooser fileChooser;
	private JButton button;
	private boolean filechoosen;
	private JTextField networkFileTextField;
	private ImageIcon image;
	private JLabel titleLabel;
	private JSeparator titleSeparator;
	//FileUtil flUtil;
	
	protected FileHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		//this.flUtil = flUtil;
		filechoosen = false;
		fileChooser = new JFileChooser();
		titleSeparator = new JSeparator();
		titleLabel = new JLabel("Import URL file");
		image = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_open.png"));
		networkFileTextField = new JTextField();
		networkFileTextField.setText("Please select a network file...");
		networkFileTextField.setName("networkFileTextField");
		networkFileTextField.setEditable(false);
		networkFileTextField.setFont(new Font(null, Font.ITALIC,12));
		button = new JButton("Open a File...",image);
		button.addActionListener(this);

		try{
			this.myFile=(File)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
				
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(layout.createParallelGroup(GroupLayout.LEADING)
							.add(titleLabel,GroupLayout.PREFERRED_SIZE,350,GroupLayout.PREFERRED_SIZE)
							.add(titleSeparator,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
							)
						.addContainerGap()
						.add(layout.createSequentialGroup()
								.add(networkFileTextField,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(button))
						.addContainerGap()));
		
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(titleLabel)
						.add(8, 8, 8)
						.add(titleSeparator,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.add(7, 7, 7)
						.addPreferredGap(LayoutStyle.RELATED)
						.add(networkFileTextField,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.RELATED,3, Short.MAX_VALUE)
						.add(button)
						.addContainerGap()));
		
		
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(layout.createParallelGroup(GroupLayout.LEADING)
								.add(titleLabel,GroupLayout.PREFERRED_SIZE,350,GroupLayout.PREFERRED_SIZE)
								.add(titleSeparator,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
								.add(layout.createSequentialGroup()
										.add(networkFileTextField,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
										.addPreferredGap(LayoutStyle.RELATED)
										.add(button))
						)
						.addContainerGap()));
		
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(titleLabel)
						.add(8, 8, 8)
						.add(titleSeparator,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.add(7, 7, 7)
						.addPreferredGap(LayoutStyle.RELATED)
						.add(layout.createParallelGroup(GroupLayout.BASELINE)
								.add(button)
								.add(networkFileTextField))
						.addPreferredGap(LayoutStyle.RELATED,3, Short.MAX_VALUE)
						.addContainerGap()));
	}

	
	public void handle() {
		if(!filechoosen){
			int ret = fileChooser.showOpenDialog(panel);
			if (ret == JFileChooser.APPROVE_OPTION) {
			    File file = fileChooser.getSelectedFile();
				//File file = flUtil.getFile("TEST",FileUtil.LOAD);
				if ( file != null ) {
					try{
						f.set(o,file);
					}catch (Exception e) { e.printStackTrace();}
					networkFileTextField.setFont(new Font(null, Font.PLAIN,10));
					networkFileTextField.setText(file.getAbsolutePath());
				}
			}
		}
		filechoosen=true;
	}

    public String getState() {
		String s;
		try {
			Object obj = f.get(o);
			if ( obj == null )
				s = "";
			else
				s = obj.toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
    }
}
