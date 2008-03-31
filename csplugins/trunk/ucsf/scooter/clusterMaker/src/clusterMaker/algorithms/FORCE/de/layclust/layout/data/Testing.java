package de.layclust.layout.data;

public class Testing {
	private static int node_no = 13000;
	private static String[] objectIDs = {"a", "b", "c", "d", "e",
	                                     "f", "g", "h", "i", "j"};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		float[][] matrix = initializeMatrix();
		ICCEdges ccEdges;
		
		
		long freeMemory = Runtime.getRuntime().freeMemory();
//		System.out.println(freeMemory);
		
//		System.out.println("Free memory: "+Runtime.getRuntime().freeMemory());
		
//		ccEdges = new CC2DArray(node_no);
//		System.out.println("CC2DArray:");
//		fillEdges(ccEdges, matrix, "CC2DArray:");
//		readEdges(ccEdges, "CC2DArray:");		
//		long usedMemory = freeMemory - Runtime.getRuntime().freeMemory();
////		freeMemory -= usedMemory;
//		System.out.println("used memory: "+usedMemory);
//		System.out.println(freeMemory);
		
		freeMemory = Runtime.getRuntime().freeMemory();
		ccEdges = new CCSymmetricArray(node_no);
		System.out.println("CCSymmetricArray:");
		fillEdges(ccEdges, matrix, "CCSymmetricArray:");
		readEdges(ccEdges,"CCSymmetricArray:");
//		usedMemory = freeMemory - Runtime.getRuntime().freeMemory();
//		System.out.println("used memory: "+usedMemory);
//		System.out.println(freeMemory);
		
//		ccEdges = new CCHash(node_no);
//		System.out.println("CCHash:");
//		fillEdges(ccEdges, matrix, "CCHash:");
//		readEdges(ccEdges, "CCHash:");
		
//		ccEdges = new CCFastUtilHash(node_no);
//		System.out.println("CCFastUtilHash:");
//		fillEdges(ccEdges, matrix, "CCFastUtilHash:");
//		readEdges(ccEdges, "CCFastUtilHash:");
		
//		ColtDoubleMatrix2D ccEdges2 = new ColtDoubleMatrix2D(node_no);
//		System.out.println("ColtDoubleMatrix2D:");
//		fillEdges(ccEdges, matrix, "ColtDoubleMatrix2D:");
//		readEdges(ccEdges,"ColtDoubleMatrix2D:");
		
		
//		ConnectedComponent cc = new ConnectedComponent(ccEdges, objectIDs,
//				node_no);
//		ILayoutInitialiser layoutInit = new LayoutInitHSphere(cc);
//		layoutInit.run();
	}
	
	private static float[][] initializeMatrix() {
		float[][] matrix = new float[node_no][node_no];
		for (int i = 0; i < node_no; i++) {
			for(int j = 0; j <= i; j++) {
				if (i==j){continue;} // no edge i==j
				matrix[i][j] = (float) Math.round(Math.random() * 10);
				matrix[j][i] = matrix[i][j];
			}
		}
		return matrix;
	}
	
	private static void fillEdges(ICCEdges ccEdges, float[][] matrix, String name) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < node_no; i++) {
			for (int j = 0; j < i; j++) {
//				for (int j = 0; j < node_no; j++) {
				if (i==j){continue;} // no edge i==j
				ccEdges.setEdgeCost(i, j, matrix[i][j]);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time to fill "+name +": " +time+"ms");
	}
	
	private static void printEdges(ICCEdges ccEdges) {
		for (int i = 0; i < node_no; i++) {
			for (int j = 0; j < i; j++) {
//				for (int j = 0; j < node_no; j++) {
				if (i==j){continue;} // no edge i==j
				System.out.print(ccEdges.getEdgeCost(i, j) + ", ");
			}
			System.out.println("\r\r");
		}
	}
	
	private static void readEdges(ICCEdges ccEdges, String name) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < node_no; i++) {
			for (int j = 0; j < i; j++) {
//				for (int j = 0; j < node_no; j++) {
				if (i==j){continue;} // no edge i==j
				ccEdges.getEdgeCost(i, j);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time to read "+name +": " +time+"ms");
	}



}
