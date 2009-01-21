package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.example.command.Command;
import org.example.tunable.*;

/**
 * This would get registered as a TunableInterceptor service. 
 */
public class GuiTunableInterceptor extends AbstractTunableInterceptor<GuiHandler> {

	private Component parent;

	public GuiTunableInterceptor(Component parent) {
		super( new GuiHandlerFactory() );
		this.parent = parent;
	}

	protected void process(java.util.List<GuiHandler> lh) {
		final String MAIN = "";
		Map<String, JPanel> panels = new HashMap<String,JPanel>();
		panels.put(MAIN,createJPanel(MAIN,false));

		// construct the gui
		for (GuiHandler gh : lh) {
			
			// figure out if the collapsable flag is set
			boolean isCollapsable = false;
			for ( String s : gh.getTunable().flags() ) {
				if ( s.equals("collapsable") ) {
					isCollapsable = true;
					break;
				}
			}

			// find the proper group to put the handler panel in
			String[] groups = gh.getTunable().groups();
			String lastGroup = MAIN; 
			for ( String g : groups ) {
				if ( !panels.containsKey(g) )	
					panels.put(g,createJPanel(g,isCollapsable));			

				panels.get(lastGroup).add( panels.get(g) );
				lastGroup = g;
			}

			// add the handler panel to the group panel
			panels.get(lastGroup).add(gh.getJPanel());
		}
			
		JOptionPane.showConfirmDialog(parent, panels.get(MAIN), "Set Parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE ); 

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
