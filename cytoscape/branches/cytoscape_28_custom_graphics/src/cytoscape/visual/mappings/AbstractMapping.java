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

public abstract class AbstractMapping extends SubjectBase implements
		ObjectMapping {
	
	protected final Class<?> rangeClass;
	protected String controllingAttrName;
	protected Class<?>[] acceptedClasses;

	
	public AbstractMapping(final Class<?> rangeClass, final String controllingAttrName) {
		this.acceptedClasses = new Class<?>[]{ Object.class };
		this.controllingAttrName = controllingAttrName;
		this.rangeClass = rangeClass;
	}
	
	@Override
	public void applyProperties(Properties props, String baseKey,
			ValueParser<?> parser) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object calculateRangeValue(Map<String, Object> attrBundle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?>[] getAcceptedDataClasses() {
		return this.acceptedClasses;
	}

	@Override
	public String getControllingAttributeName() {
		return this.controllingAttrName;
	}

	@Override
	public JPanel getLegend(VisualPropertyType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties(String baseKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getRangeClass() {
		return this.rangeClass;
	}

	@Override
	public void setControllingAttributeName(String attrName, CyNetwork network,
			boolean preserveMapping) {
		this.setControllingAttributeName(attrName);
	}

	@Override
	public void setControllingAttributeName(String controllingAttrName) {
		this.controllingAttrName = controllingAttrName;
	}
	
	abstract public Object clone();
	
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
		JPanel ret = new JPanel();
		ret.add(new JLabel("this ui is no longer used"));

		return ret;
	}

}
