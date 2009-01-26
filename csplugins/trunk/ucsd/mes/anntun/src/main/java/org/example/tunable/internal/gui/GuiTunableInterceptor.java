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
			panels.put(MAIN,createJPanel(MAIN,null));

			// construct the gui
			for (GuiHandler gh : lh) {
		
				// hook up dependency listeners
				String dep = gh.getDependency();
				if ( dep != null && !dep.equals("") ) {
					for ( GuiHandler gh2 : lh ) {
						if ( gh2.getName().equals(dep) ) {
							gh2.addDependent(gh);
							break;
						}
					}
				}


				// find the proper group to put the handler panel in
				String[] group = gh.getTunable().group();
				String lastGroup = MAIN; 
				for ( String g : group ) {
					if ( !panels.containsKey(g) ) {
						panels.put(g,createJPanel(g,gh));			
						//System.out.println("creating " + gh.getName());
						panels.get(lastGroup).add( panels.get(g), gh.getTunable().xorKey() );
					}

					lastGroup = g;
				}

				//System.out.println("appending " + gh.getName());
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

	private JPanel createJPanel(String title, GuiHandler gh) {

		if ( gh == null )
			return getSimplePanel(title);

		JPanel ret = null;

		// figure out if the collapsable flag is set
		for ( String s : gh.getTunable().flags() ) {
			if ( s.equals("collapsable") ) {
				ret = new CollapsablePanel(title);
			}
		}

		if ( ret == null ) {
			ret = getSimplePanel(title);
		}

		if ( gh.getTunable().xorChildren() ) {
			JPanel p = new XorPanel(gh);
			return p;
		} else {
			return ret;
		}
	}

	private JPanel getSimplePanel(String title) {
		JPanel ret = new JPanel();
		ret.setBorder(BorderFactory.createTitledBorder(title));
		ret.setLayout(new BoxLayout(ret,BoxLayout.PAGE_AXIS));
		return ret;
	}
}
