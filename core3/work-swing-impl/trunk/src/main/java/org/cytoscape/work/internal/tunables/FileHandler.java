package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import java.io.File;

import org.cytoscape.work.Tunable;

public class FileHandler extends AbstractGuiHandler {

	JButton button;
	File myFile;
	JFileChooser fileChooser;
	boolean filechoosen;
	JTextField path;
	
	public FileHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		System.out.println("hello world");
		filechoosen = false;
		fileChooser = new JFileChooser();

		try{
			this.myFile=(File)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		path = new JTextField("path :",12);
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
				if ( file != null ) {
					myFile = file;
					try{
						f.set(o,file);
					}catch (Exception e) { e.printStackTrace();}
					path.setText("path : "+file.getAbsolutePath());
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
