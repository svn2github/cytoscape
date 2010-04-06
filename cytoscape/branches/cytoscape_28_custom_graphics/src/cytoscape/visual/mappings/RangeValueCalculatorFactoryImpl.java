package cytoscape.visual.mappings;

import java.util.HashMap;
import java.util.Map;

public class RangeValueCalculatorFactoryImpl implements
		RangeValueCalculatorFactory {
	
	private final Map<Class<?>, RangeValueCalculator<?>> calcMap;
	

	public RangeValueCalculatorFactoryImpl() {
		calcMap = new HashMap<Class<?>, RangeValueCalculator<?>>();
	}
	
	@Override
	public <T extends RangeValueCalculator<?>> T getRangeValueCalculator(
			Class<?> type) {
		System.out.println("-------- returning RVC");
		return (T) calcMap.get(type);
	}

	@Override
	public void registerRVC(RangeValueCalculator<?> rvc) {
		calcMap.put(rvc.getRangeClass(), rvc);
	}

}
