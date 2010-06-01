package cytoscape.visual.converter;

import java.util.HashMap;
import java.util.Map;


/**
 * Replacement for ObjectToString.
 * Manages special "toString()" equivalent method for specific Classes.
 * 
 * Users can add special converter when necessary.  
 * If not specified, toString() will be used for the object type.
 * 
 * @author kono
 *
 */
public class ValueToStringConverterManager {
	
	public static final ValueToStringConverterManager manager = new ValueToStringConverterManager();
	
	
	private final Map<Class<?>, ValueToStringConverter> converters;
	
	private ValueToStringConverterManager() {
		converters = new HashMap<Class<?>, ValueToStringConverter>();
		
		registerDefaultConverters();
	}
	
	public void register(ValueToStringConverter converter) {
		this.converters.put(converter.getType(), converter);
	}
	
	public String toString(final Object value) {
		if(value == null)
			return "";
		
		final ValueToStringConverter converter = this.converters.get(value.getClass());
		
		if(converter == null)
			return value.toString();
		else
			return converter.toString(value);
	}
	
	private void registerDefaultConverters() {
		final ColorConverter color = new ColorConverter();
		converters.put(color.getType(), color);
		
		final NodeShapeConverter nodeShape = new NodeShapeConverter();
		converters.put(nodeShape.getType(), nodeShape);
		
		final ObjectPositionConverter objectPosition = new ObjectPositionConverter();
		converters.put(objectPosition.getType(), objectPosition);
		
		final FontConverter font = new FontConverter();
		converters.put(font.getType(), font);
		
	}

}
