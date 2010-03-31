package cytoscape.visual.mappings;

import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import cytoscape.CyNetwork;
import cytoscape.logger.CyLogger;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.parsers.ValueParser;

public abstract class CustomGraphicsPassThroughMapping<T> implements ObjectMapping {
	
	protected Class<?> rangeClass; // the class of values held by this mapping
	protected String attrName; // the name of the controlling data attribute
	
	protected final Class<?>[] ACCEPTED_CLASS = { Object.class };
	
	protected CustomGraphicsRangeValueRenderer<T> renderer;
	
	public CustomGraphicsPassThroughMapping(final String controllingAttrName, final T defObj) {
		this.rangeClass = defObj.getClass();
		this.attrName = controllingAttrName;
		
	}
	
	
	@Override
	public void addChangeListener(ChangeListener l) {
	}

	@Override
	public void applyProperties(Properties props, String baseKey,
			ValueParser parser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object calculateRangeValue(Map<String, Object> attrBundle) {

		final CyCustomGraphics<?> cg;
		
		return null;
	}

	@Override
	public Class<?>[] getAcceptedDataClasses() {
		return ACCEPTED_CLASS;
	}

	@Override
	public String getControllingAttributeName() {
		return this.attrName;
	}

	@Override
	public JPanel getLegend(VisualPropertyType type) {
		return new JPanel();
	}

	@Override
	public Properties getProperties(String baseKey) {
		return null;
	}

	@Override
	public Class<?> getRangeClass() {
		return rangeClass;
	}

	@Override
	@Deprecated
	public JPanel getUI(JDialog parent, CyNetwork network) {
		return null;
	}

	@Override
	public void removeChangeListener(ChangeListener l) {		
	}

	@Override
	public void setControllingAttributeName(String attrName, CyNetwork network,
			boolean preserveMapping) {
		this.attrName = attrName;
	}
	
	public Object clone() {
		final CustomGraphicsPassThroughMapping copy;

		try {
			copy = (CustomGraphicsPassThroughMapping) super.clone();
		} catch (CloneNotSupportedException e) {
			CyLogger.getLogger().warn(
					"Critical error in CustomPassthroughMapping - was not cloneable",
					e);

			return null;
		}

		copy.attrName = new String(attrName);

		// don't need to explicitly clone rangeClass since cloned calculator
		// has same type as original.
		return copy;
	}

}
