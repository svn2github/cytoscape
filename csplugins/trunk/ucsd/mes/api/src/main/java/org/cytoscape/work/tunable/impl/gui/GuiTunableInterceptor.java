package org.cytoscape.work.tunable.impl.gui;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.cytoscape.work.tunable.*;

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
			JPanel mainPanel = new JPanel();
			for (GuiHandler gh : lh) {
				mainPanel.add(gh.getJPanel());
			}
			
		 JOptionPane.showConfirmDialog(parent, mainPanel, "Set Parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE ); 

		 for ( GuiHandler h : lh )
		 	h.handle();
	}
}
