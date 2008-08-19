package cytoscape.view;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;



public class PropertySheetView extends LayoutProperties implements TunableListener

{
	private ArrayList<Tunable> myTunables = new ArrayList<Tunable>();
    private CyAttributes attributes = Cytoscape.getNodeAttributes();
    private CyNode selectedNode = null;
    private String identifier = null;
    private boolean initializing = false;
    private boolean tunableChanging = false;
   


	public PropertySheetView (String prefix) {
		super(prefix);
		initializeProperties();

	}
	
	public PropertySheetView (String prefix, CyNode node) {
		super(prefix);
		initializePropertiesFromSelectedNode(node);

	}
	
	
	
	
	public void initializeProperties()
	{

		initializing = true;
		String [] names = attributes.getAttributeNames();
		Tunable tunable;
		System.out.println ("found " + names.length + " attributes");
		this.add(new Tunable("Properties", "properties",
                Tunable.GROUP, new Integer(names.length)));

		for (int i = 0; i < names.length; i++)
		{
			tunable = null;
			System.out.println("Processing attribute: " + names[i] + " of type: "  + attributes.getType(names[i]));
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING)
			{
				tunable = new Tunable(names[i], names[i], Tunable.DOUBLE, new Double(0.0d));
			}
			else if (attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER)
			{
				tunable = new Tunable (names[i], names[i], Tunable.INTEGER, new Integer (0));
			}
			else if (attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN) 
			{
				tunable = new Tunable (names[i], names[i], Tunable.BOOLEAN, new Boolean(true));
			}
			else if (attributes.getType(names[i]) == CyAttributes.TYPE_STRING) 
			{
				tunable = new Tunable (names[i], names[i], Tunable.STRING, new String(" "));
			}
			
			if (tunable != null)
			{
				myTunables.add(tunable);
				this.add(tunable);
				tunable.addTunableValueListener(this);
			}
		}
		System.out.println ("Tunable panel = " + this.getTunablePanel());
		initializing = false;
		
	}

	public void initializePropertiesFromSelectedNode(CyNode selectedNode)
	{

		initializing = true;
		String identifier = selectedNode.getIdentifier();
		String [] names = attributes.getAttributeNames();
		Tunable tunable;
		System.out.println ("found " + names.length + " attributes, identifier: " + identifier);
		this.add(new Tunable("Properties", "properties",
                Tunable.GROUP, new Integer(names.length)));

		
		for (int i = 0; i < names.length; i++)
		{
			tunable = null;
			System.out.println("Processing attribute: " + names[i] + " of type: "  + attributes.getType(names[i]));
			// DO I need to deal with null values here?
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING)
			{
				tunable = new Tunable(names[i], names[i], Tunable.DOUBLE, 
						attributes.getDoubleAttribute(identifier, names[i]));
			}
			else if (attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER)
			{
				tunable = new Tunable (names[i], names[i], Tunable.INTEGER, 
						attributes.getIntegerAttribute(identifier, names[i]));
			}
			else if (attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN) 
			{
				tunable = new Tunable (names[i], names[i], Tunable.BOOLEAN, 
						attributes.getBooleanAttribute(identifier, names[i]));
			}
			else if (attributes.getType(names[i]) == CyAttributes.TYPE_STRING) 
			{
				tunable = new Tunable (names[i], names[i], Tunable.STRING, 
						attributes.getStringAttribute(identifier, names[i]));
			}
			
			System.out.println ("Got tunable: " + tunable);
			if (tunable != null)
			{
				myTunables.add(tunable);
				this.add(tunable);
				tunable.addTunableValueListener(this);
			}
		}
		initializing = false;
		
	}
	



	
	public void fillValuesFromSelectedNode(CyNode cyNode)
	{
		initializing = true;
		String identifier = cyNode.getIdentifier();
		List<Tunable> tunables = this.getTunables();
		Iterator<Tunable> it = tunables.iterator();
		Tunable tunable = null;
		String name = null;
		int type;
		while (it.hasNext())
		{
			tunable = it.next();
			name = tunable.getName();
			if (tunable.getType() == Tunable.BOOLEAN)
			{
				tunable.setValue(attributes.getBooleanAttribute(identifier, name));
			}
			else if (tunable.getType() == Tunable.STRING)
			{
				tunable.setValue(attributes.getStringAttribute(identifier, name));
			}
			else if (tunable.getType() == Tunable.DOUBLE)
			{
				tunable.setValue(attributes.getDoubleAttribute(identifier, name));
			}
			else if (tunable.getType() == Tunable.INTEGER)
			{
				tunable.setValue(attributes.getIntegerAttribute(identifier, name));
			}
		}
		initializing = false;
	}


	public void tunableChanged (Tunable tunable)
	{
		// first see if we are in the middle of initializing the property sheet, if so, then don't update
		if (initializing)
		{
			return;
		}
		//  see if there is a singly selected node, if not, then do nothing
		Set nodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
		CyNode myNode = null;
		if (nodes.size() == 1)
		{
			Iterator it = nodes.iterator();
			while (it.hasNext()) // loop will stop after 1 get
			{
				myNode = (CyNode) it.next();
				
			}
		}
		if (myNode == null)
		{
			return;
		}
		tunableChanging = true;
		Object value = tunable.getValue();
		int type = tunable.getType();
		String name = tunable.getName();
		String identifier = myNode.getIdentifier();
		
		

		if (tunable.getType() == Tunable.BOOLEAN)
		{
			
			attributes.setAttribute(identifier, name, (Boolean) value);
		}
		else if (tunable.getType() == Tunable.STRING)
		{
			System.out.println("setting value for tunable: " + tunable + " for Node: " + identifier + " to value: " + value);
			attributes.setAttribute(identifier, name, value.toString());
		}
		else if (tunable.getType() == Tunable.DOUBLE)
		{
			attributes.setAttribute(identifier, name, (Double) value);
		}
		else if (tunable.getType() == Tunable.INTEGER)
		{
			attributes.setAttribute(identifier, name, (Integer) value);
		}
//		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, Cytoscape.getCurrentNetwork(), Cytoscape.getCurrentNetwork());
		tunableChanging = false;
	}

	public CyNode getSelectedNode() {
		return selectedNode;
	}



	public void setSelectedNode(CyNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public boolean isInitializing() {
		return initializing;
	}

	public void setInitializing(boolean initializing) {
		this.initializing = initializing;
	}

	public boolean isTunableChanging() {
		return tunableChanging;
	}

	public void setTunableChanging(boolean tunableChanging) {
		this.tunableChanging = tunableChanging;
	}


	
}
	



