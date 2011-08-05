package cytoscape.visual.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cytoscape.Cytoscape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.mappings.ObjectMapping;

public class NewMappingBuilder {

	/**
	 * Create new mapping.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws  
	 */
	static void createNewCalculator(
			final VisualPropertyType type,
			final String newMappingName, final String newCalcName,
			final String controllingAttrName) {
		
		final VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		final CalculatorCatalog catalog = vmm.getCalculatorCatalog();

		Class<?> mapperClass = catalog.getMapping(newMappingName);

		if (mapperClass == null)
			return;

		// create the selected mapper
		final Class<?>[] conTypes = { Class.class, String.class };
		Constructor<?> mapperCon = null;
		try {
			mapperCon = mapperClass.getConstructor(conTypes);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if(mapperCon == null)
			return;
		
		final Object defaultObj = type.getDefault(vmm.getVisualStyle());

		final Object[] invokeArgs = { defaultObj.getClass(),
				controllingAttrName };
		ObjectMapping mapper = null;
		try {
			mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mapper == null)
			return;
		
		// Register new Calc
		vmm.getCalculatorCatalog().addCalculator(new BasicCalculator(newCalcName, mapper, type));
	}
	
	

}