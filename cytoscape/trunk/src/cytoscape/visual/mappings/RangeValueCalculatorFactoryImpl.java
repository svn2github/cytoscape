package cytoscape.visual.mappings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RangeValueCalculatorFactoryImpl implements
		RangeValueCalculatorFactory {
	
	private final Map<Class<?>, RangeValueCalculator<?>> calcMap;
	private final Set<RangeValueCalculator<?>> calcs;

	public RangeValueCalculatorFactoryImpl() {
		calcMap = new HashMap<Class<?>, RangeValueCalculator<?>>();
		calcs = new HashSet<RangeValueCalculator<?>>();
	}
	
	@Override
	public <T extends RangeValueCalculator<?>> T getRangeValueCalculator(
			Class<?> type) {
		
		RangeValueCalculator<?> calc = calcMap.get(type);
		
		if(calc == null) {
			for(RangeValueCalculator<?> c: calcs) {
				if(c.isCompatible(type)) {
					calcMap.put(type, c);
					calc = c;
					System.out.println("-------- returning RVC: " + calc);
					return (T) calc;
				}
			}
		}
		
		System.out.println("-------- Could not find RVC: " + calc);
		return (T) calc;
	}

	@Override
	public void registerRVC(RangeValueCalculator<?> rvc) {
		calcs.add(rvc);
	}

}
