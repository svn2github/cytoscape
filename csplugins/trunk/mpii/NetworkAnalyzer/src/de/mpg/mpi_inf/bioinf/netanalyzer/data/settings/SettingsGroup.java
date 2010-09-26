package de.mpg.mpi_inf.bioinf.netanalyzer.data.settings;

import org.jdom.Element;
import org.w3c.dom.DOMException;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.Decorators;
import de.mpg.mpi_inf.bioinf.netanalyzer.dec.Decorator;

/**
 * Base class for the group of settings for a complex parameter.
 * 
 * @author Yassen Assenov
 */
public abstract class SettingsGroup
	implements XMLSerializable {

	/**
	 * Tag name used in XML settings file to identify complex parameter settings group.
	 */
	public static final String tag = "complexparam";

	/**
	 * Gets the complex parameter ID for this settings group.
	 * 
	 * @return ID of the complex parameter this settings group is applied to.
	 */
	public String getParamID() {
		return paramID;
	}

	/**
	 * Initializes the fields of a <code>SettingsGroup</code> instance.
	 * 
	 * @param aElement Node in the XML settings file that identifies complex parameter settings
	 *        group.
	 * @throws DOMException When the given element is not an element node with the expected
	 *         attributes.
	 */
	protected SettingsGroup(Element aElement) {
		paramID = aElement.getAttributeValue("name");
		if (paramID == null) {
			throw new DOMException(DOMException.NOT_FOUND_ERR, "");
		}
		Element decorators = aElement.getChild("decorators");
		if (decorators != null) {
			Decorators.set(paramID, decorators);
		}
	}

	/**
	 * Initializes the fields of a <code>SettingsGroup</code> instance.
	 * 
	 * @param aParamID ID of the complex parameter this settings group is applied to.
	 */
	protected SettingsGroup(String aParamID) {
		paramID = aParamID;
	}

	/**
	 * Attaches nodes describing the decorators for the complex parameter this setting group is
	 * applied to.
	 * <p>
	 * If no decorators are defined for this complex parameter, calling this method has no effect.
	 * </p>
	 * 
	 * @param aNode Root XML node for this setting group, to which the decorators are to be
	 *        attached.
	 */
	protected void attachDecoratorsTo(Element aNode) {
		Decorator[] decs = Decorators.get(paramID);
		if (decs != null) {
			Element decsEl = new Element("decorators");
			for (int i = 0; i < decs.length; ++i) {
				decsEl.addContent(decs[i].toXmlNode());
			}
			aNode.addContent(decsEl);
		}
	}

	/**
	 * ID of the complex parameter this settings group is applied to.
	 */
	private String paramID;
}
