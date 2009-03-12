package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
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
	private JPanel r = new JPanel(new BorderLayout());
	private java.util.List<Guihandler> lh;
	private boolean m;
	
	public GuiTunableInterceptor(HandlerFactory<Guihandler> factory) {
		super( factory );
		panelMap = new HashMap<java.util.List<Guihandler>,JPanel>();
		//this.parent=parent;
	}

	public void setParent(Component c) {
		parent = c;
	}

	public boolean createUI(Object... proxyObjs) {
		Object[] objs = convertSpringProxyObjs( proxyObjs );

		//java.util.List<Guihandler> lh = new ArrayList<Guihandler>();
		lh = new ArrayList<Guihandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			lh.addAll( handlerMap.get(o).values() );
		}

		if ( lh.size() <= 0 )
			return true;

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
			
		
		if(parent==null){
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
		else{
//			JPanel buttonPanel = new JPanel();
//			JButton okButton = new JButton("OK");
//			okButton.setActionCommand("ok");
//			okButton.addActionListener(new myActionListener());
//			okButton.setToolTipText("Click to validate");
//			JButton cancelButton = new JButton("Cancel");
//			cancelButton.setActionCommand("cancel");
//			cancelButton.addActionListener(new myActionListener());
//			cancelButton.setToolTipText("Cancel all previous actions");
//			buttonPanel.add(okButton);
//			buttonPanel.add(cancelButton);

			int nbPanel = ((Container) parent).getComponentCount()-1;
			JPanel buttonBox = (JPanel) ((Container) parent).getComponent(nbPanel);
			((JPanel)parent).remove(nbPanel);
			((JPanel)parent).add(panelMap.get(lh));
			((JPanel)parent).add(buttonBox);
			return m;
		}
	}
	
	public void Handle(){
		for(Guihandler h: lh)h.handle();
	}
	
//	private class myActionListener implements ActionListener{
//		public void actionPerformed(ActionEvent ae){
//			if(ae.getActionCommand() == "ok"){ for(Guihandler h: lh)h.handle();m=true;}
//			else m=false;
//			((JPanel)parent).remove(r);
//			((JPanel)parent).repaint();
//		}
//	}


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
