package Factory;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
//import java.lang.reflect.*;
import javax.swing.*;

import Tunable.*;
import Utils.ChooseFilePanel;
import Utils.myFile;
import GuiInterception.AbstractGuiHandler;



public class FileHandler extends AbstractGuiHandler {

	JTextField jtf;
	File[] myNetworkFile;
	myFile file;
	java.util.List<String> paths;
	//private CyFileFilter[] tempCFF;
	//private boolean modal;
	ChooseFilePanel test;
	
	public FileHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.file= (myFile) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		//tempCFF = file.getCyFileFilter();
		//modal = file.getModal();
		panel = new JPanel(new BorderLayout());
		test = new ChooseFilePanel(file);
		panel.add(test);
/*		jtf = new JTextField("Please select a network file...",10);	
		panel.add(jtf,BorderLayout.WEST);
		JButton selectbutton = new JButton("select");
		selectbutton.addActionListener(new myActionListener());
		selectbutton.setActionCommand("select");
		panel.add(selectbutton,BorderLayout.EAST);
		paths = new ArrayList<String>();
*/	
	}

	public void handle() {	
		/*if (myNetworkFile == null && jtf.getText() != null) {
			file.setFiles(myNetworkFile);
			file.setPaths(paths);*/
			file = test.getFile();
			try {
				f.set(o,file);
			} catch (Exception e) { e.printStackTrace();}
		//}
	}

	public void returnPanel(){
		panel.removeAll();
		panel.add(new JLabel("has been imported"),BorderLayout.EAST);
		panel.add(new JTextField(file.getPaths().toString()),BorderLayout.WEST);
	}
	
	
	
/*	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getActionCommand().equals("select")){
				myNetworkFile = FileUtil.getFiles(panel,"Import Network Files", FileUtil.LOAD, tempCFF);
				if (myNetworkFile != null) {
					
					 * Accept multiple files
					 
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
					
				}
			}
		}
	}
*/
	
    public String getState() {
    	String s;
    	if(file!=null)
    		s = file.toString();
    	else
    		s="";
    	return s;
    }
}
