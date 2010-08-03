package cytoscape.visual.mappings;

public interface RangeValueCalculatorFactory {
	public <T extends RangeValueCalculator<?>> T getRangeValueCalculator(final Class<?> type);
	
	public void registerRVC(final RangeValueCalculator<?> rvc);
}
