package org.cytoscape.work.internal.tunables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cytoscape.work.internal.tunables.utils.*;
import org.cytoscape.work.*;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.spring.SpringTunableInterceptor;


public class GuiTunableInterceptor extends SpringTunableInterceptor<Guihandler> {

	private Component parent=null;
	private Map<java.util.List<Guihandler>,JPanel> panelMap;
	private java.util.List<Guihandler> lh;
	
	public GuiTunableInterceptor(HandlerFactory<Guihandler> factory) {
		super( factory );
		panelMap = new HashMap<java.util.List<Guihandler>,JPanel>();
	}

	public void setParent(Object o) {
		if(o instanceof JPanel)
			this.parent = (Component)o;
		else throw new IllegalArgumentException("Not a JPanel");
	}
	
	public void handle(){
		for(Guihandler h: lh)h.notifyDependents();//h.handle();
	}

	public boolean createUI(Object... proxyObjs) {
		Object[] objs = convertSpringProxyObjs( proxyObjs );
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
			panels.put(MAIN,createJPanel(MAIN,null,null));

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

				Map<String,Param> groupalignement = new HashMap<String,Param>();
				String[] group = gh.getTunable().group();
				Param[] alignments = gh.getTunable().alignment();
				
				if(group.length==alignments.length){
					for(int i = 0; i < group.length; i++)groupalignement.put(group[i], alignments[i]);
				}
				if(group.length>alignments.length){
					for(int i = 0; i < alignments.length; i++)groupalignement.put(group[i], alignments[i]);
					for(int i=alignments.length;i<group.length;i++)groupalignement.put(group[i], Param.vertical);
				}
				if(alignments.length>group.length){
					for(int i = 0; i < group.length; i++)groupalignement.put(group[i], alignments[i]);
				}

				
				
				// find the proper group to put the handler panel in
				String lastGroup = MAIN; 
				for ( String g : group ) {
					if ( !panels.containsKey(g) ) {
						panels.put(g,createJPanel(g,gh,groupalignement.get(g)));			
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


		if(parent==null){
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
				for ( Guihandler h : lh )h.notifyDependents();// h.handle();
				return true;
			}
			else
				return false;
		}
		else{
			int nbPanel = ((Container) parent).getComponentCount()-1;
			JPanel buttonBox = (JPanel) ((Container) parent).getComponent(nbPanel);
			((JPanel)parent).remove(nbPanel);
			((JPanel)parent).add(panelMap.get(lh));
			((JPanel)parent).add(buttonBox);
			parent = null;
			return true;
		}
	}
	

	private JPanel createJPanel(String title, Guihandler gh,Param alignment) {
		if ( gh == null )
			return getSimplePanel(title,alignment);

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
			return getSimplePanel(title,alignment);
		}
	}
	private JPanel getSimplePanel(String title,Param alignment) {
		JPanel ret = new JPanel();
		TitledBorder titleborder = BorderFactory.createTitledBorder(title);
		titleborder.setTitleColor(Color.BLUE);
		if(title!="" && title!=" "){
			if(alignment==Param.vertical || alignment==null){
				ret.setBorder(titleborder);
				ret.setLayout(new BoxLayout(ret,BoxLayout.PAGE_AXIS));
			}
			else if(alignment==Param.horizontal){
				ret.setBorder(titleborder);
				ret.setLayout(new BoxLayout(ret,BoxLayout.LINE_AXIS));
			}
		}
		else {
			if(alignment==Param.vertical || alignment==null){
				ret.setLayout(new BoxLayout(ret,BoxLayout.PAGE_AXIS));
			}
			else if(alignment==Param.horizontal){
				
				ret.setLayout(new BoxLayout(ret,BoxLayout.LINE_AXIS));
			}
		}
		return ret;
	}


}
