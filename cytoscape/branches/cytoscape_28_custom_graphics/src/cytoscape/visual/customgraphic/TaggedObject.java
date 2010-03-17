package cytoscape.visual.customgraphic;

import java.util.Collection;

public interface TaggedObject {
	public Collection<String> getTags();
	
	public void addTag(String tag);
	
	public void removeTag(String tag);
	
	public boolean isTaggedWith(String tag);
}
