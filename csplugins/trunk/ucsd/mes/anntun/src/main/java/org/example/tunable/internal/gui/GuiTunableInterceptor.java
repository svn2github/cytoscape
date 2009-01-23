package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.example.tunable.*;

/**
 * This would get registered as a TunableInterceptor service. 
 */
public class GuiTunableInterceptor extends AbstractTunableInterceptor<GuiHandler> {

	private Component parent;
	private Map<java.util.List<GuiHandler>,JPanel> panelMap;

	public GuiTunableInterceptor(Component parent) {
		super( new GuiHandlerFactory() );
		this.parent = parent;
		panelMap = new HashMap<java.util.List<GuiHandler>,JPanel>();
	}

	protected void process(java.util.List<GuiHandler> lh) {
		if ( !panelMap.containsKey( lh ) ) {
			//System.out.println("creating new JPanel");
			final String MAIN = "";
			Map<String, JPanel> panels = new HashMap<String,JPanel>();
			panels.put(MAIN,createJPanel(MAIN,false));

			// construct the gui
			for (GuiHandler gh : lh) {
			
				String dep = gh.getDependency();

				//System.out.println("for gh " + gh.getName());
				//System.out.println("  got dependency: " + dep);
				if ( dep != null && !dep.equals("") ) {
					for ( GuiHandler gh2 : lh ) {
						//System.out.println("  checking : " + gh2.getName());
						if ( gh2.getName().equals(dep) ) {
							gh2.addDependent(gh);
							break;
						}
					}
				}

				//System.out.println("handling: " + gh.getField().getName());
			
				// figure out if the collapsable flag is set
				boolean isCollapsable = false;
				for ( String s : gh.getTunable().flags() ) {
					if ( s.equals("collapsable") ) {
						isCollapsable = true;
						break;
					}
				}

				// find the proper group to put the handler panel in
				String[] group = gh.getTunable().group();
				String lastGroup = MAIN; 
				for ( String g : group ) {
					if ( !panels.containsKey(g) )	
						panels.put(g,createJPanel(g,isCollapsable));			
	
					panels.get(lastGroup).add( panels.get(g) );
					lastGroup = g;
				}

				// add the handler panel to the group panel
				panels.get(lastGroup).add(gh.getJPanel());
			}

			panelMap.put(lh,panels.get(MAIN));
		}

		// get the gui into the proper state
		for ( GuiHandler h : lh ) 
			h.notifyDependents();
			
		JOptionPane.showConfirmDialog(parent, panelMap.get(lh), "Set Parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE ); 

		// process the values set in the gui 
		for ( GuiHandler h : lh )
			h.handle();
	}

	private JPanel createJPanel(String title, boolean collapse) {
		if ( collapse )
			return new CollapsablePanel(title);

		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder(title));
		p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
		return p;
	}
}
