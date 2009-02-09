package Factory;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.io.CyFileFilter;


import cytoscape.util.FileUtil;

import Tunable.*;
import Utils.myFileChooseDialog;
import GuiInterception.AbstractGuiHandler;



public class FileHandler extends AbstractGuiHandler {

	JTextField jtf;
	Double value = null;
	File myFile;
	myFileChooseDialog FCD;
	String newline = System.getProperty("line.separator");
	private File[] networkFiles;
	private FileUtil fileUtil;
	private CyFileFilter[] tempCFF;
	
	public FileHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.FCD= (myFileChooseDialog) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		//JLabel label = new JLabel(t.description());
		//label.setFont(new Font(null, Font.PLAIN,12));
		//panel.add(label,BorderLayout.WEST );
		jtf = new JTextField("Please select a network file");		
		panel.add(jtf,BorderLayout.WEST);
		JButton selectbutton = new JButton("select");
		selectbutton.addActionListener(new myActionListener());
		selectbutton.setActionCommand("select");
		panel.add(selectbutton,BorderLayout.EAST);
	}

	public void handle() {
		try{
			jtf.setBackground(Color.white);
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
			try{
				jtf.setBackground(Color.red);
				value = Double.parseDouble(f.get(o).toString());
				JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value.intValue(), "Error",JOptionPane.ERROR_MESSAGE);
			}catch(Exception e){e.printStackTrace();}
		}
		try {
			f.set(o,value.intValue());
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public void returnPanel(){
		panel.removeAll();
		panel.add(new JLabel(t.description()));
		panel.add(new JTextField(Integer.toString(value.intValue())));
	}
	
	
	
	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getActionCommand().equals("select")){
				
				networkFiles = FileUtil.getFiles(panel,"Import Network Files", FileUtil.LOAD, tempCFF);
				if (networkFiles != null) {
					/*
					 * Accept multiple files
					 */
					StringBuffer fileNameSB = new StringBuffer();
					StringBuffer tooltip = new StringBuffer();
					tooltip.append("<html><body><strong><font color=RED>The following files will be loaded:</font></strong><br>");

					for (int i = 0; i < networkFiles.length; i++) {
						fileNameSB.append(networkFiles[i].getAbsolutePath() + ", ");
						tooltip.append("<p>" + networkFiles[i].getAbsolutePath() + "</p>");
					}

					tooltip.append("</body></html>");
					jtf.setText(fileNameSB.toString());
					jtf.setToolTipText(tooltip.toString());

				}
			}
		}
	}

    public String getState() {
		String s;
		try {
			s = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
    }
}
