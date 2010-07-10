package cytoscape.visual.converter;

/**
 * Provide special type of toString for the given class.
 * 
 * @author kono
 *
 */
public interface ValueToStringConverter {
	
	/**
	 * Convert given value to VizMap compatible string.
	 * 
	 * @param value 
	 * @return VizMap conversion of given object.  Otherwise, returns empty String.
	 */
	public String toString(Object value);
	
	
	/**
	 * Class supported by this converter.
	 * @return Class of supported type.
	 */
	public Class<?> getType();
}
