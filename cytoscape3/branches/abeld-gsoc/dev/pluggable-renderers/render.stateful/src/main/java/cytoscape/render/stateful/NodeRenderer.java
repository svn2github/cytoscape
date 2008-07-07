package cytoscape.render.stateful;

import java.awt.Graphics2D;

public interface NodeRenderer {
	public void render(Graphics2D graphics, NodeDetails nodeDetails, float[] floatBuff1, int node);
}
