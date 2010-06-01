package cytoscape.visual.mappings;

import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cytoscape.CyNetwork;
import cytoscape.visual.SubjectBase;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.parsers.ValueParser;

public abstract class AbstractMapping<V> extends SubjectBase implements
		ObjectMapping<V> {
	
	private JPanel dummyPanel;
	
	// Mapped value class.
	protected final Class<V> rangeClass;
	
	// Attribute name associated with this mapping.
	protected String controllingAttrName;
	
	// Attribute value types compatible withi this mapping.
	protected Class<?>[] acceptedClasses;
	
	public AbstractMapping(final Class<V> rangeClass, final String controllingAttrName) {
		this.acceptedClasses = new Class<?>[]{ Object.class };
		this.controllingAttrName = controllingAttrName;
		this.rangeClass = rangeClass;
	}
	
	
	@Override
	abstract public void applyProperties(Properties props, String baseKey,
			ValueParser<V> parser);
	
	
	@Override
	abstract public Properties getProperties(String baseKey);

	
	@Override
	public abstract V calculateRangeValue(Map<String, Object> attrBundle);

	
	@Override
	public Class<?>[] getAcceptedDataClasses() {
		return this.acceptedClasses;
	}

	
	@Override
	public String getControllingAttributeName() {
		return this.controllingAttrName;
	}
	
	
	@Override
	public Class<V> getRangeClass() {
		return this.rangeClass;
	}


	@Override
	public void setControllingAttributeName(String controllingAttrName) {
		this.controllingAttrName = controllingAttrName;
	}
	
	
	abstract public Object clone();
	
	
	@Override
	public JPanel getLegend(VisualPropertyType type) {
		return getDummyPanel();
	}
	
	
	private JPanel getDummyPanel() {
		if(dummyPanel == null) {
			dummyPanel = new JPanel();
			dummyPanel.add(new JLabel("GUI is not configured for this mapping."));
		}
		return dummyPanel;
	}
	
	
	/**
	 * Gets the UI Object Associated with the Mapper. Required by the
	 * ObjectMapping interface.
	 * 
	 * @param parent
	 *            Parent Dialog.
	 * @param network
	 *            CyNetwork.
	 * @return JPanel Object.
	 */
	@Deprecated
	public JPanel getUI(JDialog parent, CyNetwork network) {
		return getDummyPanel();
	}
	
	
	@Deprecated
	public void setControllingAttributeName(String attrName, CyNetwork network,
			boolean preserveMapping) {
		this.setControllingAttributeName(attrName);
	}
}