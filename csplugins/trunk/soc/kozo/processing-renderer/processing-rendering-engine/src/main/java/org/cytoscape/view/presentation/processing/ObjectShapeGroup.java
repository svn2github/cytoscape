package org.cytoscape.view.presentation.processing;

import java.util.List;

public interface ObjectShapeGroup {
	
	public List<ObjectShape> getMembers();
	public void addMember(ObjectShape shape);
	
	public void draw();

}
