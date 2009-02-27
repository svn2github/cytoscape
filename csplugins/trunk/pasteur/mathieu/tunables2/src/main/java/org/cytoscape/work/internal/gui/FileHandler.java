package org.cytoscape.work.internal.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import java.io.File;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.myFile;

public class FileHandler extends AbstractGuiHandler {

	JButton button;
	myFile myfile;
	JFileChooser fileChooser;
	JTextField path;
	boolean filechoosen;
	
	public FileHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		System.out.println("hello world");
		filechoosen=false;
		fileChooser = new JFileChooser();

		try{
			this.myfile=(myFile)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		path = new JTextField("File: ",10);
		path.setFont(new Font(null, Font.PLAIN,12));
		panel.add(path,BorderLayout.WEST);
		button = new JButton("Select File...");
		button.addActionListener(this);
		System.out.println("wtf");
		panel.add(button,BorderLayout.EAST);		
	}

	public void handle() {
		if(!filechoosen){
			int ret = fileChooser.showOpenDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try{
					if ( file != null ) {
					//myfile = new File(file.getAbsolutePath());
//					System.out.println("path3 = "+myfile.getAbsolutePath());
					myfile.setFile(file);
					f.set(o,myfile);
					path.setText("File: " + myfile.getPath());
					}
				}catch (Exception e){}
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
