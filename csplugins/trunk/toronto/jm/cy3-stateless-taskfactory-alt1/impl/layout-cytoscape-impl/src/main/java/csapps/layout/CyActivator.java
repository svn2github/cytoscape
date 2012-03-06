



package csapps.layout; 

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.osgi.framework.BundleContext;

import csapps.layout.algorithms.GroupAttributesLayout;
import csapps.layout.algorithms.StackedNodeLayout;
import csapps.layout.algorithms.bioLayout.BioLayoutFRAlgorithm;
import csapps.layout.algorithms.bioLayout.BioLayoutKKAlgorithm;
import csapps.layout.algorithms.circularLayout.CircularLayoutAlgorithm;
import csapps.layout.algorithms.graphPartition.AttributeCircleLayout;
import csapps.layout.algorithms.graphPartition.DegreeSortedCircleLayout;
import csapps.layout.algorithms.graphPartition.ISOMLayout;
import csapps.layout.algorithms.hierarchicalLayout.HierarchicalLayoutAlgorithm;



public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {

		CircularLayoutAlgorithm circularLayoutAlgorithm = new CircularLayoutAlgorithm();
		HierarchicalLayoutAlgorithm hierarchicalLayoutAlgorithm = new HierarchicalLayoutAlgorithm();
		AttributeCircleLayout attributeCircleLayout = new AttributeCircleLayout();
		DegreeSortedCircleLayout degreeSortedCircleLayout = new DegreeSortedCircleLayout();
		ISOMLayout ISOMLayout = new ISOMLayout();
		BioLayoutKKAlgorithm bioLayoutKKAlgorithmFALSE = new BioLayoutKKAlgorithm(false);
		BioLayoutKKAlgorithm bioLayoutKKAlgorithmTRUE = new BioLayoutKKAlgorithm(true);
		BioLayoutFRAlgorithm bioLayoutFRAlgorithm = new BioLayoutFRAlgorithm(true);
		StackedNodeLayout stackedNodeLayout = new StackedNodeLayout();
		GroupAttributesLayout groupAttributesLayout = new GroupAttributesLayout();
		
		
		Properties circularLayoutAlgorithmProps = new Properties();
		circularLayoutAlgorithmProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		circularLayoutAlgorithmProps.setProperty("preferredTaskManager","menu");
		circularLayoutAlgorithmProps.setProperty("title",circularLayoutAlgorithm.toString());
		registerService(bc,circularLayoutAlgorithm,CyLayoutAlgorithm.class, circularLayoutAlgorithmProps);

		Properties hierarchicalLayoutAlgorithmProps = new Properties();
		hierarchicalLayoutAlgorithmProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		hierarchicalLayoutAlgorithmProps.setProperty("preferredTaskManager","menu");
		hierarchicalLayoutAlgorithmProps.setProperty("title",hierarchicalLayoutAlgorithm.toString());
		registerService(bc,hierarchicalLayoutAlgorithm,CyLayoutAlgorithm.class, hierarchicalLayoutAlgorithmProps);

		Properties attributeCircleLayoutProps = new Properties();
		attributeCircleLayoutProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		attributeCircleLayoutProps.setProperty("preferredTaskManager","menu");
		attributeCircleLayoutProps.setProperty("title",attributeCircleLayout.toString());
		registerService(bc,attributeCircleLayout,CyLayoutAlgorithm.class, attributeCircleLayoutProps);

		Properties degreeSortedCircleLayoutProps = new Properties();
		degreeSortedCircleLayoutProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		degreeSortedCircleLayoutProps.setProperty("preferredTaskManager","menu");
		degreeSortedCircleLayoutProps.setProperty("title",degreeSortedCircleLayout.toString());
		registerService(bc,degreeSortedCircleLayout,CyLayoutAlgorithm.class, degreeSortedCircleLayoutProps);

		Properties ISOMLayoutProps = new Properties();
		ISOMLayoutProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		ISOMLayoutProps.setProperty("preferredTaskManager","menu");
		ISOMLayoutProps.setProperty("title",ISOMLayout.toString());
		registerService(bc,ISOMLayout,CyLayoutAlgorithm.class, ISOMLayoutProps);

		Properties bioLayoutKKAlgorithmFALSEProps = new Properties();
		bioLayoutKKAlgorithmFALSEProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		bioLayoutKKAlgorithmFALSEProps.setProperty("preferredTaskManager","menu");
		bioLayoutKKAlgorithmFALSEProps.setProperty("title",bioLayoutKKAlgorithmFALSE.toString());
		registerService(bc,bioLayoutKKAlgorithmFALSE,CyLayoutAlgorithm.class, bioLayoutKKAlgorithmFALSEProps);

		Properties bioLayoutKKAlgorithmTRUEProps = new Properties();
		bioLayoutKKAlgorithmTRUEProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		bioLayoutKKAlgorithmTRUEProps.setProperty("preferredTaskManager","menu");
		bioLayoutKKAlgorithmTRUEProps.setProperty("title",bioLayoutKKAlgorithmTRUE.toString());
		registerService(bc,bioLayoutKKAlgorithmTRUE,CyLayoutAlgorithm.class, bioLayoutKKAlgorithmTRUEProps);

		Properties bioLayoutFRAlgorithmProps = new Properties();
		bioLayoutFRAlgorithmProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		bioLayoutFRAlgorithmProps.setProperty("preferredTaskManager","menu");
		bioLayoutFRAlgorithmProps.setProperty("title",bioLayoutFRAlgorithm.toString());
		registerService(bc,bioLayoutFRAlgorithm,CyLayoutAlgorithm.class, bioLayoutFRAlgorithmProps);

		Properties groupAttributesLayoutProps = new Properties();
		groupAttributesLayoutProps.setProperty("preferredMenu","Layout.Cytoscape Layouts");
		groupAttributesLayoutProps.setProperty("preferredTaskManager","menu");
		groupAttributesLayoutProps.setProperty("title",groupAttributesLayout.toString());
		registerService(bc,groupAttributesLayout,CyLayoutAlgorithm.class, groupAttributesLayoutProps);
	}
}

