package GuiInterception;

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

import Tunable.Tunable.Param;
import Utils.CollapsablePanel;
import Utils.XorPanel;


public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {

	private Component parent;
	private Map<java.util.List<Guihandler>,JPanel> panelMap;

	public GuiTunableInterceptor(Component parent) {
		super( new GuiHandlerFactory<Guihandler>());
		this.parent = parent;
		panelMap = new HashMap<java.util.List<Guihandler>,JPanel>();
	}

	public int createUI(Object... objs) {
		
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
		
		
		// process the values set in the gui 
		//USELESS BECAUSE OF LISTENERS
		for ( Guihandler h : lh )
			h.handle();
		
		return n;
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
			
	
	/*protected void getResultsPanels(java.util.List<Guihandler> lh) {
		for ( Guihandler h : lh ) {
			h.notifyDependents();
			h.returnPanel();
		}
			
		JOptionPane.showMessageDialog(parent,
	    panelMap.get(lh),
	    "Results",JOptionPane.PLAIN_MESSAGE);
	}
*/
	
	protected void getResultsPanels(List<Guihandler> handlerList) {

		for ( Guihandler h : handlerList ) {
			h.notifyDependents();
			h.returnPanel();
		}			
		JOptionPane.showMessageDialog(parent,
	    panelMap.get(handlerList),
	    "Results",JOptionPane.PLAIN_MESSAGE);
	}



	protected void processProps(List<Guihandler> handlers) {}
	public void createProperties(Object... obs) {}
}
