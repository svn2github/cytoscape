package org.cytoscape.edge.bundler.internal;

import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;


public class EdgeBundlerTask extends AbstractNetworkViewTask {

	@Tunable(description="value of K")
	public double K;

	EdgeBundlerTask(CyNetworkView v) {
		super(v);
	}

	public void run(TaskMonitor tm) {
			
	}

	private double force(CyEdge p, List<CyEdge> allEdges) {
	}

	private double compatibility(View<CyEdge> p, View<CyEdge> q) {
		return (angleCompat(p,q) * scaleCompat(p,q) * positionCompat(p,q) * visibilityCompat(p,q));
	}

	private double angleCompat(View<CyEdge> p, View<CyEdge> q) {
		View<CyNode> pSource = view.getNodeView(p.getSource());
		View<CyNode> pTarget = view.getNodeView(p.getSource());

		View<CyNode> qSource = view.getNodeView(q.getSource());
		View<CyNode> qTarget = view.getNodeView(q.getSource());

		double psx = pSource.getVisualProperty(NODE_X_LOCATION);
		double psy = pSource.getVisualProperty(NODE_Y_LOCATION);
		double ptx = pTarget.getVisualProperty(NODE_X_LOCATION);
		double pty = pTarget.getVisualProperty(NODE_Y_LOCATION);
		double a = ptx - psx;
		double b = pty - psy;

		double qsx = qSource.getVisualProperty(NODE_X_LOCATION);
		double qsy = qSource.getVisualProperty(NODE_Y_LOCATION);
		double qtx = qTarget.getVisualProperty(NODE_X_LOCATION);
		double qty = qTarget.getVisualProperty(NODE_Y_LOCATION);
		double c = qtx - qsx;
		double d = qty - qsy;

		double cosAlpha = ((a*c) + (b*d))/(Math.sqrt(a*a + b*b)*Math.sqrt(c*c + d*d));

		return Math.round(cosAlpha);
	}

	private double scaleCompat(View<CyEdge> p, View<CyEdge> q) {
		double lavg = (len(p) + len(q))/2.0;
		return 2.0/((lavg*Math.min(len(p),len(q))) + (Math.max(len(p),len(q))/lavg));
	}

	private double positionCompat(View<CyEdge> p, View<CyEdge> q) {
		double lavg = (len(p) + len(q))/2.0;

		return lavg / (lavg + normMinus( mid(p),mid(q) )); 
	}

	private Point2D.Double mid(View<CyEdge> p) {
		View<CyNode> pSource = view.getNodeView(p.getSource());
		View<CyNode> pTarget = view.getNodeView(p.getSource());
		double psx = pSource.getVisualProperty(NODE_X_LOCATION);
		double psy = pSource.getVisualProperty(NODE_Y_LOCATION);
		double ptx = pTarget.getVisualProperty(NODE_X_LOCATION);
		double pty = pTarget.getVisualProperty(NODE_Y_LOCATION);
		return new Point2D.Double((psx-ptx)/2.0,(psy-pty)/2.0);
	}

	private double normMinus(Point2D.Double p, Point2D.Double q) {
		double x = p.getX() - q.getX()
		double y = p.getY() - q.getY()
		return Math.sqrt(x*x + y*y);
	}

	private double visibilityCompat(View<CyEdge> p, View<CyEdge> q) {
		return Math.min(vis(p,q), vis(q,p));
	}

	private double vis(View<CyEdge> p, View<CyEdge> q) {
		return Math.max(1.0 - 
	}
}
