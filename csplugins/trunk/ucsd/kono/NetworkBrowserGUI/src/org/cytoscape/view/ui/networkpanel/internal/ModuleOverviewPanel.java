package org.cytoscape.view.ui.networkpanel.internal;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import org.cytoscape.view.ui.networkpanel.MetaNetworkGenerator;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import ding.view.InnerCanvas;

public class ModuleOverviewPanel extends JPanel {
	
	private MetaNetworkGenerator generator;
	private ModuleVisualStyleBuilder vsg;
	
	private DingNetworkView view;
	CyNetwork network;
	
	private final VisualMappingManager vmm = Cytoscape.getVisualMappingManager();

	public ModuleOverviewPanel(MetaNetworkGenerator generator) {
		
		this.setBackground(new Color(200, 200, 200));
		this.generator = generator;
		this.vsg = new ModuleVisualStyleBuilder();
		this.setLayout(new BorderLayout());
		network = generator.getMetaNetwork();
		
		view = new DingNetworkView(network, network.getTitle());
		view.setGraphLOD(new CyGraphLOD());
		view.setIdentifier(network.getIdentifier());
		view.setTitle(network.getTitle());
		view.setVisualStyle(vsg.getMosuleVisualStyle().getName());
		
		CyNetwork old = vmm.getNetwork();
		CyNetworkView oldView = vmm.getNetworkView();
		VisualStyle oldStyle = vmm.getVisualStyle();
		
		vmm.setNetworkView(view);
		vmm.setVisualStyle(vsg.getMosuleVisualStyle());
		vmm.applyAppearances();
		
		CyLayoutAlgorithm layout = CyLayouts.getDefaultLayout();

		layout.doLayout(view);

		view.fitContent();

		view.redrawGraph(false, true);
		InnerCanvas canvas = view.getCanvas();
		
		this.add(canvas);
		
		vmm.setNetworkView(oldView);
		vmm.setVisualStyle(oldStyle);
	}
	
	public void update() {
		view = new DingNetworkView(network, network.getTitle());
		view.setGraphLOD(new CyGraphLOD());
		view.setIdentifier(network.getIdentifier());
		view.setTitle(network.getTitle());
		
		view.setVisualStyle(vsg.getMosuleVisualStyle().getName());
		
		CyNetwork old = vmm.getNetwork();
		CyNetworkView oldView = vmm.getNetworkView();
		VisualStyle oldStyle = vmm.getVisualStyle();
		
		vmm.setNetworkView(view);
		vmm.setVisualStyle(vsg.getMosuleVisualStyle());
		vmm.applyAppearances();

		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");

		layout.doLayout(view);

		view.fitContent();

		view.redrawGraph(false, true);
		InnerCanvas canvas = view.getCanvas();
		
		this.removeAll();
		this.add(canvas);
		repaint();
		
		vmm.setNetworkView(oldView);
		vmm.setVisualStyle(oldStyle);
	}
}
