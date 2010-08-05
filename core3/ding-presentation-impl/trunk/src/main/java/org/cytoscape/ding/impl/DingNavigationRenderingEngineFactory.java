package org.cytoscape.ding.impl;

import java.awt.Component;

import javax.swing.JPanel;

import org.cytoscape.ding.BirdsEyeView;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.spacial.SpacialIndex2DFactory;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.UndoSupport;

public class DingNavigationRenderingEngineFactory extends
		DingRenderingEngineFactory {

	public DingNavigationRenderingEngineFactory(
			CyDataTableFactory dataTableFactory,
			CyRootNetworkFactory rootNetworkFactory, UndoSupport undo,
			SpacialIndex2DFactory spacialFactory, RootVisualLexicon vpc,
			VisualLexicon dingLexicon, TunableInterceptor ti, TaskManager tm,
			CyServiceRegistrar registrar) {
		super(dataTableFactory, rootNetworkFactory, undo, spacialFactory, vpc,
				dingLexicon, ti, tm, registrar);
	}
	
	
	@Override
	public RenderingEngine<CyNetwork> render(Object visualizationContainer, View<CyNetwork> view) {
		
		if(visualizationContainer == null || view == null) {
			return null;
		}
		// TODO: FIX this!!
		final RenderingEngine<CyNetwork> renderer = super.render(visualizationContainer, view);
		
		if ( !(visualizationContainer instanceof Component) ) 
			throw new IllegalArgumentException("navBounds object is not of type Component, which is invalid for this implementation of PresentationFactory");
		
		JPanel target = new JPanel();
		BirdsEyeView bev = new BirdsEyeView((Component) visualizationContainer, this);	
		target.add( bev );

		return renderer;
	}

}
