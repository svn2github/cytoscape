package cytoscape.visual.customgraphic;

import java.util.HashMap;
import java.util.Map;

public class CyCustomGraphicsParserFactoryImpl implements
		CyCustomGraphicsParserFactory {

	private final Map<Class<? extends CyCustomGraphics<?>>, CyCustomGraphicsParser> parserMap;
	
	private static final CyCustomGraphicsParserFactory factory = new CyCustomGraphicsParserFactoryImpl();
	
	public static CyCustomGraphicsParserFactory getFactory() {
		return factory;
	}
	
	private CyCustomGraphicsParserFactoryImpl() {
		parserMap = new HashMap<Class<? extends CyCustomGraphics<?>>, CyCustomGraphicsParser>();
	}
	
	@Override
	public CyCustomGraphicsParser getParser(String customGraphicsClassName) {
		if(customGraphicsClassName == null || customGraphicsClassName.trim().length() == 0)
			return null;
		
		Class<? extends CyCustomGraphics<?>> cgClass;
		try {
			cgClass = (Class<? extends CyCustomGraphics<?>>) Class.forName(customGraphicsClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			return null;
		}
		
		return parserMap.get(cgClass);
	}

	@Override
	public void registerParser(Class<? extends CyCustomGraphics<?>> cgClass,
			CyCustomGraphicsParser parser) {
		
		parserMap.put(cgClass, parser);
	}

}
