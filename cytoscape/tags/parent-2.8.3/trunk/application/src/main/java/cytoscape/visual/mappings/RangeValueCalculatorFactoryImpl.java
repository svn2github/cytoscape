package cytoscape.visual.mappings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cytoscape.logger.CyLogger;

public class RangeValueCalculatorFactoryImpl implements
		RangeValueCalculatorFactory {
	
	private final Map<Class<?>, RangeValueCalculator<?>> calcMap;
	private final Set<RangeValueCalculator<?>> calcs;
	
	private static final CyLogger logger = CyLogger.getLogger();

	public RangeValueCalculatorFactoryImpl() {
		calcMap = new HashMap<Class<?>, RangeValueCalculator<?>>();
		calcs = new HashSet<RangeValueCalculator<?>>();
	}
	
	
	public <T extends RangeValueCalculator<?>> T getRangeValueCalculator(
			Class<?> type) {
		
		RangeValueCalculator<?> calc = calcMap.get(type);
		
		if(calc == null) {
			for(RangeValueCalculator<?> c: calcs) {
				if(c.isCompatible(type)) {
					calcMap.put(type, c);
					calc = c;
					return (T) calc;
				}
			}
		}
		
		logger.warn("Could not find RVC: " + calc);
		return (T) calc;
	}


	public void registerRVC(RangeValueCalculator<?> rvc) {
		calcs.add(rvc);
	}

}
