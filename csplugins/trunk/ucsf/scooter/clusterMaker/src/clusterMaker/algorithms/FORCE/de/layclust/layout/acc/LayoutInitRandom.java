package de.layclust.layout.acc;

import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.taskmanaging.TaskConfig;


public class LayoutInitRandom implements ILayoutInitialiser {

	private ConnectedComponent cc;
	
	public void initLayoutInitialiser(ConnectedComponent cc) {
		this.cc = cc;
	}

	public void run() {

		int dimension = ACCConfig.dimension;
		double[][] ccPositions = cc.getCCPositions();
		int no_nodes = cc.getNodeNumber();
		ccPositions = new double[no_nodes][dimension];
		for (int i = 0; i < ccPositions.length; i++) {
			for (int j = 0; j < ccPositions[i].length; j++) {
				ccPositions[i][j] = Math.random();
			}
			
		}
		cc.setCCPositions(ccPositions);
	}
	
//	public static void main(String[] args) {
//		CostMatrixReader reader = new CostMatrixReader("/homes/nkleinbo/workspace/ACC/de/layclust/data/cm/cost_matrix_component_nr_1_size_9_cutoff_20.0.cm");
//		ConnectedComponent comp = reader.getConnectedComponent();
//		ILayoutInitialiser layIni = new LayoutInitRandom();
//		layIni.initLayoutInitialiser(comp);
//		layIni.run();
//		double[][] ccPos = comp.getCCPositions();
//		for (int i = 0; i < ccPos.length; i++) {
//			for (int j = 0; j < ccPos[i].length; j++) {
//				System.out.print(ccPos[i][j]+",");
//			}
//			System.out.println();
//		}
//	}
}
