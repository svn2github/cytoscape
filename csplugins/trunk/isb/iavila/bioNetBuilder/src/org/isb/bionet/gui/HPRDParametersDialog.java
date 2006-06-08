package org.isb.bionet.gui;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;

import org.isb.bionet.datasource.interactions.*;

public class HPRDParametersDialog extends JDialog implements InteractionsSourceGui {
	
	public static final String TITLE = "HPRD Parameters";
	
	protected Map detectionMethodToCheckBox;
	protected JCheckBox vidalCheckBox;
	protected JCheckBox baitCheckBox;
	
	/**
	 * Constructor
	 *
	 */
	public HPRDParametersDialog (){
		setTitle(TITLE);
		create();
	}
	
	/**
	 * Returns the currently set parameters
	 * 
	 * @see org.isb.bionet.datasource.interactions.HPRDInteractionsSource
	 */
	public Hashtable getArgsTable (){
		Hashtable args = new Hashtable();
		
		Vector selectedMethods = new Vector();
		Iterator it = this.detectionMethodToCheckBox.keySet().iterator();
		while(it.hasNext()){
			String detectionMethodName = (String)it.next();
			JCheckBox cb = (JCheckBox)this.detectionMethodToCheckBox.get(detectionMethodName);
			if(cb.isSelected()) selectedMethods.add(detectionMethodName); 
		}//while it
		
		args.put(HPRDInteractionsSource.DETECTION_METHODS, selectedMethods);
		
		args.put(HPRDInteractionsSource.USE_VIDAL, new Boolean(this.vidalCheckBox.isSelected()));
		args.put(HPRDInteractionsSource.INCLUDE_NON_HUMAN_BAITS, new Boolean(this.baitCheckBox.isSelected()));
		
		return args;
	}
	
	/**
	 * Creates the dialog
	 */
	protected void create (){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		
		// Detection methods
		JPanel dmethodsPanel = new JPanel();
		int numCols = 2;
		int numRows = (HPRDInteractionsSource.DETECTION_METHOD_ARRAY.length / numCols) + HPRDInteractionsSource.DETECTION_METHOD_ARRAY.length % numCols;
		dmethodsPanel.setLayout(new GridLayout(numRows,numCols));
		
		this.detectionMethodToCheckBox = new HashMap();
		
		for(int i = 0; i < HPRDInteractionsSource.DETECTION_METHOD_ARRAY.length; i++){
			String detectionMethod = HPRDInteractionsSource.DETECTION_METHOD_ARRAY[i];
			JCheckBox cb = new JCheckBox(detectionMethod);
			cb.setSelected(true);
			dmethodsPanel.add(cb);
			this.detectionMethodToCheckBox.put(detectionMethod,cb);
		}//for
		
		dmethodsPanel.setBorder(BorderFactory.createTitledBorder("Detection Methods"));
		mainPanel.add(dmethodsPanel);
		
		JPanel otherOptionsPanel = new JPanel();
		otherOptionsPanel.setLayout(new BoxLayout(otherOptionsPanel,BoxLayout.Y_AXIS));
		
		this.vidalCheckBox = new JCheckBox("Include Vidal et al. interactions");
		this.baitCheckBox = new JCheckBox("<html>Include interactions that contain<br>non-human proteins<br>Example: human-mouse interaction</html>");
		
		otherOptionsPanel.add(this.vidalCheckBox);
		otherOptionsPanel.add(this.baitCheckBox);
		
		this.vidalCheckBox.setSelected(true);
		this.baitCheckBox.setSelected(true);
		
		mainPanel.add(otherOptionsPanel);
		
		JPanel buttonsPanel = new JPanel();
		
		JButton okB = new JButton("OK");
		okB.addActionListener(new AbstractAction(){
			public void actionPerformed (ActionEvent e){
				HPRDParametersDialog.this.dispose();
			}
		});
		
		buttonsPanel.add(okB);
		mainPanel.add(buttonsPanel);
		
		setContentPane(mainPanel);
		
	}
}