package org.cytoscape.work.util;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.io.CyFileFilter;



@SuppressWarnings("serial")
public class ChooseFilePanel extends JPanel implements ActionListener{

	JTextField jtf;
	File[] myNetworkFile;
	myFile file;
	java.util.List<String> paths;
	CyFileFilter[] tempCFF;
	int LOAD = FileDialog.LOAD;
	
	public ChooseFilePanel(myFile infile){
		
		this.file = infile;
		setLayout(new BorderLayout());
		tempCFF = file.getCyFileFilter();
		jtf = new JTextField("Please select a network file...",10);	
		add(jtf,BorderLayout.WEST);
		JButton selectbutton = new JButton("select");
		selectbutton.addActionListener(this);
		selectbutton.setActionCommand("select");
		add(selectbutton,BorderLayout.EAST);
		paths = new ArrayList<String>();
	}
	
	public myFile getFile(){
		return file;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("select")){
			//FileUtil temp = new FileUtilImpl(null);
			//myNetworkFile = temp.getFiles(this,"Import Network Files", LOAD, tempCFF);
			if (myNetworkFile != null) {
				/*
				 * Accept multiple files
				 */
				StringBuffer fileNameSB = new StringBuffer();
				StringBuffer tooltip = new StringBuffer();
				tooltip.append("<html><body><strong><font color=RED>The following files will be loaded:</font></strong><br>");

				for (int i = 0; i < myNetworkFile.length; i++) {
					fileNameSB.append(myNetworkFile[i].getAbsolutePath() + ", ");
					tooltip.append("<p>" + myNetworkFile[i].getAbsolutePath() + "</p>");
					paths.add(myNetworkFile[i].getAbsolutePath());
				}

				tooltip.append("</body></html>");
				jtf.setText(fileNameSB.toString());
				jtf.setToolTipText(tooltip.toString());
				file.setFiles(myNetworkFile);
				file.setPaths(paths);
			}
		}
	}

}