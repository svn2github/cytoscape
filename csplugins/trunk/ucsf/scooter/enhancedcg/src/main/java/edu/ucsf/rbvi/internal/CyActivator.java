package edu.ucsf.rbvi.enhancedcg.internal;

import java.util.Properties;

import org.osgi.framework.BundleContext;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphicsFactory;

import edu.ucsf.rbvi.enhancedcg.internal.gradients.linear.LinearGradientCGFactory;
import edu.ucsf.rbvi.enhancedcg.internal.gradients.radial.RadialGradientCGFactory;
import edu.ucsf.rbvi.enhancedcg.internal.charts.bar.BarChartFactory;
import edu.ucsf.rbvi.enhancedcg.internal.charts.line.LineChartFactory;
import edu.ucsf.rbvi.enhancedcg.internal.charts.pie.PieChartFactory;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		// We'll eventually need the CyApplicationManager to get current network, etc.
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);

		CyCustomGraphicsFactory linGradFactory = new LinearGradientCGFactory();
		Properties linGradProps = new Properties();
		registerService(bc, linGradFactory, CyCustomGraphicsFactory.class, linGradProps);

		CyCustomGraphicsFactory radGradFactory = new RadialGradientCGFactory();
		Properties radGradProps = new Properties();
		registerService(bc, radGradFactory, CyCustomGraphicsFactory.class, linGradProps);

		CyCustomGraphicsFactory pieChartFactory = new PieChartFactory();
		Properties pieChartProps = new Properties();
		registerService(bc, pieChartFactory, CyCustomGraphicsFactory.class, pieChartProps);

		CyCustomGraphicsFactory barChartFactory = new BarChartFactory();
		Properties barChartProps = new Properties();
		registerService(bc, barChartFactory, CyCustomGraphicsFactory.class, barChartProps);

		CyCustomGraphicsFactory lineChartFactory = new LineChartFactory();
		Properties lineChartProps = new Properties();
		registerService(bc, lineChartFactory, CyCustomGraphicsFactory.class, lineChartProps);
		// CyCustomGraphicsFactory stripeChartFactory = new StripeChartCustomGraphicsFactory();
		// CyCustomGraphicsFactory stripChartFactory = new StripChartCustomGraphicsFactory();
		System.out.println("Enhanced Custom Graphics started");
	}
}

