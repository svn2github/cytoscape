package de.mpg.mpi_inf.bioinf.netanalyzer.data.settings;

import org.jdom.Element;

/**
 * Storage class for flag setting.
 * 
 * @author Yassen Assenov
 */
public final class BooleanSettings extends Settings {

	/**
	 * Initializes a new instance of <code>BooleanSettings</code>.
	 * 
	 * @param aElement Node in the XML settings file that identifies general settings.
	 */
	public BooleanSettings(Element aElement) {
		super();
		name = aElement.getName();
		value = aElement.getText().equals("true");
	}

	/**
	 * Produces an exact copy of the settings instance.
	 * 
	 * @return Copy of the settings instance.
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return new BooleanSettings(name, value);
	}

	/**
	 * Gets the value of this flag.
	 * 
	 * @return Current value of the flag in the form of a <code>boolean</code>.
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * Sets the value of this flag.
	 * 
	 * @param aValue New value of the flag in the form of a <code>boolean</code>.
	 */
	public void setValue(boolean aValue) {
		value = aValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.data.settings.XMLSerializable#toXmlNode()
	 */
	@Override
	public Element toXmlNode() {
		return new Element(name).setText(value ? "true" : "false");
	}

	/**
	 * Initializes a new instance of <code>BooleanSettings</code>.
	 * <p>
	 * This constructor is used by the {@link #clone()} method only.
	 * </p>
	 * 
	 * @param aName Name of the tag to be used for this flag.
	 * @param aValue Value of the flag.
	 */
	private BooleanSettings(String aName, boolean aValue) {
		super();
		name = aName;
		value = aValue;
	}

	/**
	 * Name of the tag for this flag.
	 */
	private String name;

	/**
	 * Value of the flag.
	 */
	private boolean value;
}
