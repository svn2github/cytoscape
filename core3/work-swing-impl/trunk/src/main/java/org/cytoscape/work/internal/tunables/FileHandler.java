package org.cytoscape.work.internal.tunables;

import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import java.io.File;

import org.cytoscape.work.Tunable;

import cytoscape.util.*;

public class FileHandler extends AbstractGuiHandler {

	JButton button;
	File myFile;
	JFileChooser fileChooser;
	boolean filechoosen;
	JTextField path;
	FileUtil flUtil;
	
	public FileHandler(Field f, Object o, Tunable t,FileUtil flUtil) {
		super(f,o,t);
		this.flUtil = flUtil;
		filechoosen = false;
		fileChooser = new JFileChooser();

		try{
			this.myFile=(File)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel.add(new JLabel("Path :"));
		path = new JTextField("select file",12);
		path.setFont(new Font(null, Font.ITALIC,10));
		panel.add(path);
		button = new JButton("Select File...");
		button.addActionListener(this);
		panel.add(button);
			
	}

	public void handle() {
		if(!filechoosen){
			int ret = fileChooser.showOpenDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION) {
			    File file = fileChooser.getSelectedFile();
				//File file = flUtil.getFile("TEST",FileUtil.LOAD);
				if ( file != null ) {
					try{
						f.set(o,file);
					}catch (Exception e) { e.printStackTrace();}
					path.setFont(new Font(null, Font.PLAIN,10));
					path.setText(file.getAbsolutePath());
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
