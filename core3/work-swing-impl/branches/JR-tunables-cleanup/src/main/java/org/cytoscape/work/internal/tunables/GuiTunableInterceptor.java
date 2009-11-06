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



/**
 * Interceptor of <code>Tunable</code> that will be applied on <code>Guihandlers</code>.
 * 
 * <p><pre>
 * To set the new value to the original objects contained in the <code>Guihandlers</code> : 
 * <ul>
 * <li>Creates the parent container for the GUI, or use the one that is specified </li>
 * <li>Creates a GUI with swing components for each intercepted <code>Tunable</code> </li>
 * <li>Displays the GUI to the user, following the layout construction rule specified in the <code>Tunable</code> annotations, and the dependencies to enable or not the graphic components</li>
 * <li>Applies the new <i>value,item,string,state...</i> to the object contained in the <code>Guihandler</code>, if the modifications have been validated by the user </li>
 * </ul>
 * </pre></p>
 * 
 * @author pasteur
 *
 */
public class GuiTunableInterceptor extends SpringTunableInterceptor<Guihandler> {

	private Component parent = null;
	private Map<java.util.List<Guihandler>,JPanel> panelMap;
	private java.util.List<Guihandler> lh;
	private boolean newValuesSet;
	private Object[] objs;
	

	/**
	 * Creates an Interceptor that will use the <code>Guihandlers</code> created in a <code>HandlerFactory</code> from intercepted <code>Tunables</code>.
	 * @param factory
	 */
	public GuiTunableInterceptor(HandlerFactory<Guihandler> factory) {
		super( factory );
		panelMap = new HashMap<java.util.List<Guihandler>,JPanel>();
	}

	
	/**
	 * set the parent JPanel that will contain the GUI
	 * If no parent JPanel is specified, the GUI will be made using a JOptionPane
	 * 
	 * @param parent component for the <code>Guihandlers</code>'s panels
	 */
	public void setParent(Object o) {
		if(o instanceof JPanel)
			this.parent = (Component)o;
		else throw new IllegalArgumentException("Not a JPanel");
	}
	

	
	//get the value(Handle) of the Tunable if its JPanel is enabled(Dependency) and check if we have to validate the values of tunables
	/**
	 * get the <i>value,item,string,state...</i> from the GUI component, and check with the dependencies, if it can be set as the new one
	 * 
	 * <p><pre>
	 * If the <code>TunableValidator</code> interface is implemented by the class that contains the <code>Tunables</code> : 
	 * <ul>
	 * <li>a validate method has to be applied</li>
	 * <li>it checks the conditions that have been declared about the chosen <code>Tunable(s)</code> </li>
	 * <li>if validation fails, it displays an error to the user, and new values are not set</li>
	 * </ul>
	 * </pre></p>
	 * @return success or not of the <code>TunableValidator</code> validate method
	 */
	public boolean handle(){
		for(Guihandler h: lh)h.handleDependents();
		return validateTunableInput();
	}

	
	
	//Create the GUI
	/**
	 * Creates a GUI for the detected <code>Tunables</code>, following the graphic rules specified in <code>Tunable</code>' annotations
	 * 
	 * The new values that have been entered for the Object contained in <code>Guihandlers</code> are also set if the user clicks on <i>"OK"</i>
	 * 
	 * @param an Object Array that contains <code>Tunables</code>
	 * 
	 * @return if new values has been successfully set
	 */
	public boolean createUI(Object... proxyObjs){
		this.objs = convertSpringProxyObjs( proxyObjs );
		lh = new ArrayList<Guihandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			lh.addAll( handlerMap.get(o).values() );
		}

		if ( lh.size() <= 0 ){
			if(parent != null){
				((JPanel)parent).removeAll();
				((JPanel)parent).repaint();
				//parent = null;
			}
			return true;			
		}

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
		if(parent == null){
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
	

	/**
	 * Creation of a JPanel that will contain panels of <code>Guihandler</code>
	 * This panel will have special features like, ability to collapse, ability to be displayed depending on another panel
	 * A layout will be set for this <i>"container"</i> of panels(horizontally or vertically), and a title(displayed or not)
	 * 
	 * 
	 * @param title of the panel
	 * @param gh provides access to <code>Tunable</code> annotations : see if this panel can be collapsable, and if its content will switch between different <code>Guihandler</code> panels(depending on <code>xorKey</code>) 
	 * @param alignment the way the panels will be set in this <i>"container</i> panel 
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 * 
	 * @return a container for <code>Guihandler</code>' panels with special features if it is requested, or a simple one if not
	 */
	private JPanel createJPanel(String title, Guihandler gh,Param alignment,Param groupTitle) {
		if ( gh == null ) 
			return getSimplePanel(title,alignment,groupTitle);

		// See if we need to create a XOR panel
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
	
	
	
	/**
	 * Creation of a JPanel that will contain panels of <code>Guihandler</code>
	 * A layout will be set for this <i>"container"</i> of panels(horizontally or vertically), and a title(displayed or not)
	 * 
	 * @param title of the panel
	 * @param alignment the way the panels will be set in this <i>"container</i> panel 
	 * @param groupTitle parameter to choose whether or not the title of the panel has to be displayed
	 * 
	 * @return a container for <code>Guihandler</code>' panels
	 */
	private JPanel getSimplePanel(String title,Param alignment,Param groupTitle) {
		JPanel outPanel = new JPanel();
		TitledBorder titleborder = BorderFactory.createTitledBorder(title);
		titleborder.setTitleColor(Color.BLUE);
		
		if(groupTitle == Param.displayed || groupTitle == null) outPanel.setBorder(titleborder);
		if(alignment == Param.vertical || alignment == null) outPanel.setLayout(new BoxLayout(outPanel,BoxLayout.PAGE_AXIS));
		else if(alignment == Param.horizontal)outPanel.setLayout(new BoxLayout(outPanel,BoxLayout.LINE_AXIS));
		return outPanel;
	}

	
	/**
	 * Displays the JPanels of each <code>Guihandler</code> in a <code>JOptionPane</code>
	 * 
	 * Set the new <i>"value"</i> to <code>Tunable</code> object if the user clicked on <i>OK</i>, and if the validate method from <code>TunableValidator</code> interface succeeded
	 */
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
	
	
	
	/**
	 * Check if the conditions set in validate method from <code>TunableValidator</code> are met
	 * 
	 * If an exception is thrown, or something's wrong, it will be displayed to the user
	 * 
	 * @return success(true) or failure(false) for the validation
	 */
	private boolean validateTunableInput(){
		for(Object o : objs){
				Object[] interfaces = o.getClass().getInterfaces();
				for(Object inter : interfaces){
					if(inter.equals(TunableValidator.class)){
						try {
							((TunableValidator)o).validate();

/*							if(parent!=null){
								((JPanel)parent).removeAll();
								((JPanel)parent).add(panelMap.get(lh));
								parent.repaint();
							}
*/
						} catch (Exception e) {
							JOptionPane.showMessageDialog(new JFrame(),e.toString(),"TunableValidator problem",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
							if(parent==null)displayOptionPanel();
							return false;
						}
					}
				}
			}
		parent = null;		
		newValuesSet = true;
		return true;
	}
}
