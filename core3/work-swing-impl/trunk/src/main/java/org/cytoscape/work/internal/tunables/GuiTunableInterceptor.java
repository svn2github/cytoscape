package org.cytoscape.work.internal.tunables;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cytoscape.work.internal.tunables.utils.*;
import org.cytoscape.work.*;
import org.cytoscape.work.Tunable.Param;

import org.springframework.core.InfrastructureProxy; // see comment where this is used!

public class GuiTunableInterceptor extends AbstractTunableInterceptor<Guihandler> {

	private Component parent;
	private Map<java.util.List<Guihandler>,JPanel> panelMap;

	public GuiTunableInterceptor() {
		super( new GuiHandlerFactory());
		panelMap = new HashMap<java.util.List<Guihandler>,JPanel>();
	}

	public void setParent(Component c) {
		parent = c;
	}

	// This hack exists to handle Spring's proxy framework.  Since Spring returns
	// a proxy object rather than the original object when requesting an OSGi
	// service, we need this check to get at the original object where tunables
	// are actually defined.  This code can be safely omitted if this class isn't
	// being used with Spring.
	public void loadTunables(Object obj) {
		if ( obj instanceof InfrastructureProxy )
			super.loadTunables( ((InfrastructureProxy)obj).getWrappedObject() );
		else
			super.loadTunables( obj );
	}

	// This hack exists to handle Spring's proxy framework.  Since Spring returns
	// a proxy object rather than the original object when requesting an OSGi
	// service, we need this check to get at the original object where tunables
	// are actually defined.  This code can be safely omitted if this class isn't
	// being used with Spring.
	private Object[] convertProxyObjs(Object... proxyObjs) {
		Object[] objs = new Object[proxyObjs.length];
		int i = 0;
		for ( Object o : proxyObjs )
		if ( o instanceof InfrastructureProxy )
			objs[i++] = ((InfrastructureProxy)o).getWrappedObject();
		else
			objs[i++] = o;

		return objs;
	}

	public boolean createUI(Object... proxyObjs) {
		Object[] objs = convertProxyObjs( proxyObjs ); 

		java.util.List<Guihandler> lh = new ArrayList<Guihandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			lh.addAll( handlerMap.get(o).values() );

		}
		if ( !panelMap.containsKey( lh ) ) {
			final String MAIN = "";
			Map<String, JPanel> panels = new HashMap<String,JPanel>();
			panels.put(MAIN,createJPanel(MAIN,null));

			// construct the gui
			for (Guihandler gh : lh) {
				//System.out.println("handler: " + gh.getName());
		
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

				// find the proper group to put the handler panel in
				String[] group = gh.getTunable().group();
				String lastGroup = MAIN; 
				for ( String g : group ) {
					if ( !panels.containsKey(g) ) {
						panels.put(g,createJPanel(g,gh));			
						panels.get(lastGroup).add( panels.get(g), gh.getTunable().xorKey() );
					}

					lastGroup = g;
				}

				panels.get(lastGroup).add(gh.getJPanel());
			}
			panelMap.put(lh,panels.get(MAIN));
		}

		// get the gui into the proper state
		for ( Guihandler h : lh ) 
			h.notifyDependents();
			
		//Custom button text
		Object[] buttons = {"OK","Cancel"};
		int n = JOptionPane.showOptionDialog(parent,
		    panelMap.get(lh),
		    "Set Parameters",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    buttons,
		    buttons[0]);
		
		// process the values set in the gui : USELESS BECAUSE OF LISTENERS
	
		if ( n == JOptionPane.OK_OPTION ){
			for ( Guihandler h : lh ) h.handle();
			return true;
		}
		else
			return false;
	}

	private JPanel createJPanel(String title, Guihandler gh) {

		if ( gh == null )
			return getSimplePanel(title);

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
			return getSimplePanel(title);
		}
	}
	private JPanel getSimplePanel(String title) {
		JPanel ret = new JPanel();
		TitledBorder titleborder = BorderFactory.createTitledBorder(title);
		titleborder.setTitleColor(Color.BLUE);
		ret.setBorder(titleborder);
//		ret.setBorder(BorderFactory.createTitledBorder(title));
		ret.setLayout(new BoxLayout(ret,BoxLayout.PAGE_AXIS));
		return ret;
	}
}
