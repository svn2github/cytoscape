package org.cytoscape.work.internal.tunables;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.internal.tunables.utils.CollapsablePanel;
import org.cytoscape.work.internal.tunables.utils.XorPanel;
import org.cytoscape.work.spring.SpringTunableInterceptor;


public class GuiTunableInterceptor extends SpringTunableInterceptor<Guihandler> {

	private Component parent = null;
	private Map<java.util.List<Guihandler>,JPanel> panelMap;
	private java.util.List<Guihandler> lh;
	private boolean newValuesSet;
	private Object[] objs;
	
	
	public GuiTunableInterceptor(HandlerFactory<Guihandler> factory) {
		super( factory );
		panelMap = new HashMap<java.util.List<Guihandler>,JPanel>();
	}

	
	//set the parent JPanel that will contain the GUI
	public void setParent(Object o) {
		if(o instanceof JPanel)
			this.parent = (Component)o;
		else throw new IllegalArgumentException("Not a JPanel");
	}
	
	
	//get the value(Handle) of the Tunable if its JPanel is enabled(Dependency) and check if we have to validate the values of tunables
	public void handle(){
		for(Guihandler h: lh)h.handleDependents();
		validateTunableInput();
	}

	
	//Create the GUI
	public boolean createUI(Object... proxyObjs) {
		this.objs = convertSpringProxyObjs( proxyObjs );
		lh = new ArrayList<Guihandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			lh.addAll( handlerMap.get(o).values() );
		}

		if ( lh.size() <= 0 )
			return true;

		if ( !panelMap.containsKey( lh ) ) {
			final String MAIN = " ";
			Map<String, JPanel> panels = new HashMap<String,JPanel>();
			panels.put(MAIN,createJPanel(MAIN,null,null,Param.hidden));

			
			// construct the GUI
			for (Guihandler gh : lh) {
				// hook up dependency listeners
				String dep = gh.getDependency();
				if ( dep != null && !dep.equals("") ) {
					for ( Guihandler gh2 : lh ) {
						if ( gh2.getName().equals(dep) ) {
							gh2.addDependent(gh);
							break;
						}
					}
				}

				
				//Get informations about the Groups and alignment from Tunables Annotations in order to create the proper GUI
				Map<String,Param> groupAlignement = new HashMap<String,Param>();
				Map<String,Param> groupTitles = new HashMap<String,Param>();
				
				String[] group = gh.getTunable().group();
				Param[] alignments = gh.getTunable().alignment();
				Param[] titles = gh.getTunable().groupTitles();
				
				if(group.length==alignments.length)for(int i = 0; i < group.length; i++)groupAlignement.put(group[i], alignments[i]);
				if(group.length>alignments.length){
					for(int i = 0; i < alignments.length; i++)groupAlignement.put(group[i], alignments[i]);
					for(int i=alignments.length;i<group.length;i++)groupAlignement.put(group[i], Param.vertical);
				}
				if(alignments.length>group.length)for(int i = 0; i < group.length; i++)groupAlignement.put(group[i], alignments[i]);
				
				if(group.length==titles.length)for(int i = 0; i < group.length; i++)groupTitles.put(group[i], titles[i]);
				if(group.length>titles.length){
					for(int i = 0; i < titles.length; i++)groupTitles.put(group[i], titles[i]);
					for(int i=titles.length;i<group.length;i++)groupTitles.put(group[i], Param.displayed);
				}
				if(titles.length>group.length)for(int i = 0; i < group.length; i++)groupTitles.put(group[i], titles[i]);
				
				
				
				// find the proper group to put the handler panel in given the Alignment/Group parameters
				String lastGroup = MAIN;
				String groupNames = null;
				for ( String g : group ) {
					if(g.equals(""))throw new IllegalArgumentException("The group's name cannot be set to \"\"");
					groupNames = groupNames + g;
					if ( !panels.containsKey(groupNames) ) {
						panels.put(groupNames,createJPanel(g,gh,groupAlignement.get(g),groupTitles.get(g)));						
						panels.get(lastGroup).add( panels.get(groupNames), gh.getTunable().xorKey() );
					}
					lastGroup = groupNames;
				}
				panels.get(lastGroup).add(gh.getJPanel());
			}
			panelMap.put(lh,panels.get(MAIN));
		}

		// get the gui into the proper state
		for ( Guihandler h : lh ) 
			h.notifyDependents();

		//if no parent is defined, then create a new JDialog to display the Tunables' panels
		if(parent==null){
			displayOptionPanel();
			return newValuesSet;
		}
		else{//else add them to the "parent" JPanel
			((JPanel)parent).removeAll();
			((JPanel)parent).add(panelMap.get(lh));
			parent.repaint();
			parent = null;
			return true;
		}
	}
	

	private JPanel createJPanel(String title, Guihandler gh,Param alignment,Param groupTitle) {
		if ( gh == null ) 
			return getSimplePanel(title,alignment,groupTitle);

		// See if we need to create an XOR panel
		if ( gh.getTunable().xorChildren() ) {
			JPanel p = new XorPanel(title,gh);
			return p;
		} else {
			// Figure out if the collapsable flag is set
			for ( Param s : gh.getTunable().flag() ) {
				if(s.equals(Param.collapsed)){
					return new CollapsablePanel(title,false);
				}
				else if(s.equals(Param.uncollapsed)){
					return new CollapsablePanel(title,true);
				}
			}
			// We're not collapsable, so return a normal jpanel
			return getSimplePanel(title,alignment,groupTitle);
		}
	}
	private JPanel getSimplePanel(String title,Param alignment,Param groupTitle) {
		JPanel outPanel = new JPanel();
		TitledBorder titleborder = BorderFactory.createTitledBorder(title);
		titleborder.setTitleColor(Color.BLUE);
		
		if(groupTitle == Param.displayed || groupTitle == null) outPanel.setBorder(titleborder);
		if(alignment == Param.vertical || alignment == null) outPanel.setLayout(new BoxLayout(outPanel,BoxLayout.PAGE_AXIS));
		else if(alignment == Param.horizontal)outPanel.setLayout(new BoxLayout(outPanel,BoxLayout.LINE_AXIS));
		return outPanel;
	}

	
	private void displayOptionPanel(){
		Object[] buttons = {"OK","Cancel"};
		int n = JOptionPane.showOptionDialog(parent,
		    panelMap.get(lh),
		    "Set Parameters",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    buttons,
		    buttons[0]);
		if ( n == JOptionPane.OK_OPTION ){
			for ( Guihandler h : lh )h.handleDependents();
			validateTunableInput();
		}
		else newValuesSet = false;
	}
	
	
	private void validateTunableInput(){
		String valid = null;
		for(Object o : objs){
			 Object[] interfaces = o.getClass().getInterfaces();
			 for(int i=0;i<interfaces.length;i++){
				if(interfaces[i].equals(TunableValidator.class))valid=((TunableValidator)o).validate();
			 }
		}
		if(valid == null){
			newValuesSet = true;
		}
		else{
			JOptionPane.showMessageDialog(new JFrame(),valid,"TunableValidator problem",JOptionPane.ERROR_MESSAGE);
			for(Guihandler h : lh)h.resetValue();
			if(parent == null)
				displayOptionPanel();
			else{
				((JPanel)parent).removeAll();
				((JPanel)parent).add(panelMap.get(lh));
				parent.repaint();
			}
		}
		parent = null;
	}

}
