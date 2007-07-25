package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.ExcentricLabels;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

// AJK: 07/22/06 substitute for Visualization because I can't seem to be able to add Visualization to a JComponent
public class CyExcentricLabelsWrapper extends JPanel {

	private ExcentricLabels excentric;

	protected CyExcentricVisualizationInteractor interactor;

	protected JComponent parent;

	private boolean isInstalled = false;

	private CyLabeledComponent labeledComponent;

	public CyExcentricLabelsWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CyExcentricLabelsWrapper(ExcentricLabels excentric,
			JComponent component, CyLabeledComponent labeledComponent) {
		super();
		this.setParent(component);
		this.excentric = excentric;
		this.labeledComponent = labeledComponent;
		interactor = new CyExcentricVisualizationInteractor(this);
		this.setEnabled(true);
//		System.out.println("setting visible=true for " + this);
		this.setVisible(true);
	}

	public void paint(Graphics g) {
		excentric.setVisible(true);
		excentric.setEnabled(true);
//		Rectangle2D hBox = labeledComponent.getHitBox();
//		if (hBox != null) {
//			g.setClip(Math.max(((int) hBox.getMinX()) - 50, 0), Math.max(
//					((int) hBox.getMinY()) - 50, 0), 100, 100);
//		}

//		System.out.println("Excentric labels paint called on " + excentric + " for bounds " + this.bounds());
		
		// AJK: 07/21/07 BEGIN
		//    try cutting down on number of paint events by exiting 9 out of 10 times
		double kount = Math.random();
		if (kount < 0.9d)
		{
			return;
		}
		// AJK: 07/21/07 END
		
		excentric.paint((Graphics2D) g, this.bounds());
	}

	public ExcentricLabels getExcentric() {
		return excentric;
	}

	public void setParent(JComponent parent) {
		this.parent = parent;
	}

	public CyExcentricVisualizationInteractor getInteractor() {
		return interactor;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

}
