package de.mpg.mpi_inf.bioinf.netanalyzer.data.settings;

import org.jdom.Element;

/**
 * Interface identifying the ability of a class to store its state in an XML tree using the JDOM
 * library.
 * <p>
 * A class implementing this interface is recommended to have a constructor with parameters
 * {@link #constructorParams}.
 * </p>
 * 
 * @author Yassen Assenov
 */
public interface XMLSerializable {

	/**
	 * Parameters of a constructor of class that implements this interface. This identifies a
	 * constructor with a single parameter of type <code>Element</code>.
	 * <p>
	 * Every class that implements this interface should by able to create instances given an XML
	 * tree as returned by the {@link #toXmlNode()} method. This field is used when classes that
	 * implement this interface are initialized through reflection.
	 * </p>
	 */
	public static final Class<?>[] constructorParams = new Class<?>[] { Element.class };

	/**
	 * Saves the state of the class instance to an XML tree.
	 * <p>
	 * The resulting tree can be used to create identical copies of this instance.
	 * </p>
	 * 
	 * @return Root of the created XML tree.
	 */
	public Element toXmlNode();
}
