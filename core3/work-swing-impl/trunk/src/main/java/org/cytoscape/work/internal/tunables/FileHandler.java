package org.cytoscape.work.internal.tunables;

import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import java.io.File;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
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

		for(Param s :t.flag())if(s.equals(Param.network)){
			String[] biopax = {".xml",".rdf",".owl"};
			fileChooser.addChoosableFileFilter(new MyFilter("BioPAX files",biopax));
			String[] xgmml ={".xml",".xgmml"};
			fileChooser.addChoosableFileFilter(new MyFilter("XGMML files",xgmml));
			String[] psi ={".xml"};
			fileChooser.addChoosableFileFilter(new MyFilter("PSI-MI",psi));
			String[] sif={".sif"};
			fileChooser.addChoosableFileFilter(new MyFilter("SIF files",sif));
			String[] gml={".gml"};
			fileChooser.addChoosableFileFilter(new MyFilter("GML files",gml));
			String[] sbml={".xml",".sbml"};
			fileChooser.addChoosableFileFilter(new MyFilter("SBML files",sbml));
			String[] allnetworks = {".xml",".rdf",".owl",".xgmml",".sif",".sbml"};
			fileChooser.addChoosableFileFilter(new MyFilter("All network files(*.xml, *.rdf, *.owl, *.xgmml, *.sif, *.sbml)",allnetworks));
		}
		for(Param s :t.flag())if(s.equals(Param.session)){
			fileChooser.addChoosableFileFilter(new MyFilter("Session files",".cys"));
		}
		for(Param s :t.flag())if(s.equals(Param.attributes)){
			fileChooser.addChoosableFileFilter(new MyFilter("Attributes files",""));
		}
	
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
					filechoosen=true;
				}
			}
		}
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
    
    private class MyFilter extends javax.swing.filechooser.FileFilter {
        	
    	private final String description;
    	private String extension;
    	private String[] extensions;
 
    	public MyFilter(String description, String extension){
    		super();
    		this.description = description;
    		this.extension = extension;
    	}
    
    	public MyFilter(String description, String[] extensions) {
    		super();
    		this.description = description;
    		this.extensions = extensions;
    	}
 
    	public boolean accept(File file){
    		if (file.isDirectory()) return true; 
        
    		String fileName = file.getName().toLowerCase(); 
        
    		if (extensions != null){
    			boolean accept = false;
    			for (int i=0; i<extensions.length; i++){
    				if (fileName.endsWith(extensions[i])){
    					accept = true;
    				}
    			}
    			return accept;
    		}
    		else {
    			return fileName.endsWith(extension);
    		}        
    	}
    	
	    public String getDescription(){
	        return description;
	    }
	 
	    public String getExtension(){
	        return extension;
	    }
	 
	    public String[] getExtensions() {
	        return extensions;
	    }
    } 
}
