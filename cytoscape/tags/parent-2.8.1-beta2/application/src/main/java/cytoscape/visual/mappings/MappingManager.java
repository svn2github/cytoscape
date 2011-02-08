package cytoscape.visual.mappings;

public interface MappingManager {
	
	public void register(Class<? extends ObjectMapping> type);
	
	public ObjectMapping createMapping(final byte objectType, final Object defObject);

}
