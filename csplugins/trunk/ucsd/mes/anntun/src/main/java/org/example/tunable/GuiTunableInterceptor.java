package org.example.tunable;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.example.command.Command;
import org.example.tunable.*;
import org.example.tunable.gui.*;

/**
 * This would presumably be service. 
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
			panels.put(MAIN,createJPanel(MAIN));

			for (GuiHandler gh : lh) {
				System.out.println(gh.getField().getName());
				boolean isCollapsable = false;
				for ( String s : gh.getTunable().flags() ) {
					if ( s.equals("collapsable") ) {
						isCollapsable = true;
						System.out.println(" -- found collaps");
						break;
					}
				}

				String[] groups = gh.getTunable().groups();
				String lastGroup = MAIN; 
				for ( String g : groups ) {
					if ( !panels.containsKey(g) )	{
						System.out.println("  adding panel " + g);
						if ( isCollapsable ) {
							System.out.println("  collaps " + g);
							panels.put(g,createCollapsableJPanel(g));			
						} else {
							System.out.println("  normal " + g);
							panels.put(g,createJPanel(g));			
						}
					} else {
						System.out.println("  already contains " + g);
					}
					panels.get(lastGroup).add( panels.get(g) );
					lastGroup = g;
				}

				panels.get(lastGroup).add(gh.getJPanel());
			}
			
		 JOptionPane.showConfirmDialog(parent, panels.get(MAIN), "Set Parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE ); 

		 for ( GuiHandler h : lh )
		 	h.handle();
	}

	private JPanel createJPanel(String title) {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder(title));
		p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
		return p;
	}

	private JPanel createCollapsableJPanel(String title) {
		return new CollapsablePanel(title);
	}
}
