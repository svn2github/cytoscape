package de.layclust.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Nils Kleinbölting
 *
 * This class creates artificial datasets based on normal distributed values.
 */
public class cmCreator {
	public static Random rand;
	public static String dir = "/vol/assb/graph_cluster_files/FORCEnD_tests_sita/artificial2/data_20/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		rand = new Random();
		//create data with 2 Clusters:
//		int[] sizes2C = {10,100,500,2000};
//		double[] steps = {2.0,1.5,1.0,0.5};
//		for (int i = 0; i < sizes2C.length; i++) {
//			int clustersize = sizes2C[i] / 2;
//			for (int j = 0; j < steps.length; j++) {
//				double[][] data = new double[2][clustersize];
//				String fileName = "data_2Clusters_"+sizes2C[i]+"_"+steps[j]+".cm";
//				for (int k = 0; k < data.length; k++) {
//					for (int k2 = 0; k2 < data[k].length; k2++) {
//						if(k == 0) {
//							data[k][k2] = getRandomVar(-steps[j],1);
//						} else {
//							data[k][k2] = getRandomVar(steps[j],1);
//						}
//					}
//				}
//				writeFile2C(data, fileName, steps[j]);
//			}
//		}
//
//		int[] sizes4C = {20,40,100,500};
//		double[] steps2 = {4.0,3.0,2.0,1.0};
//		for (int h = 0; h < noMatr; h++) {
//			for (int i = 0; i < sizes4C.length; i++) {
//				int clustersize = sizes4C[i] / 4;
//				for (int j = 0; j < steps2.length; j++) {
//					double[][][] data = new double[4][clustersize][2];
//					String fileName = "data_4Clusters_"+sizes4C[i]+"_"+steps2[j]+"_"+h+".cm";
//					for (int k = 0; k < data.length; k++) {
//						for (int k2 = 0; k2 < data[k].length; k2++) {
//							if(k == 0) {
//								data[k][k2][0] = getRandomVar(-steps2[j],1);
//								data[k][k2][1] = getRandomVar(steps2[j],1);
//							} else if (k == 1 ){
//								data[k][k2][0] = getRandomVar(steps2[j],1);
//								data[k][k2][1] = getRandomVar(steps2[j],1);
//							} else if (k == 2) {
//								data[k][k2][0] = getRandomVar(-steps2[j],1);
//								data[k][k2][1] = getRandomVar(-steps2[j],1);
//							} else {
//								data[k][k2][0] = getRandomVar(steps2[j],1);
//								data[k][k2][1] = getRandomVar(-steps2[j],1);	
//							}
//							}
//					}
//					writeFile4C(data, fileName, 2);

//		int[] sizes4C = {20,40,100};
//		double[] means_intra = {21};
//		double[] means_inter = {-21};
//		double[] abw = {20};
//		int nr = 0;
//		int anzahl = 10;
//		for (int i = 0; i < sizes4C.length; i++) {
//			int clustersize = sizes4C[i] / 4;
//			for (int j = 0; j < means_intra.length; j++) {
//				for(int x = 0; x < anzahl; x++) {
//					double[][] data = new double[sizes4C[i]][sizes4C[i]];
//					nr++;
//					String fileName = "data_4Clusters_nr_"+nr+"_size_"+sizes4C[i]+"_conf_"+j+".cm";
//					for(int k = 0; k < 4; k++) {
//						int low = k * clustersize;
//						int high  = (k+1) * clustersize;
//						for(int v = low; v < high; v++) {
//							for(int w = 0; w < sizes4C[i]; w++) {
//								if(w >= low && w < high) {
//									data[v][w] = getRandomVar(means_intra[j],abw[j]);
//									System.out.println(means_intra[j]+"/"+abw[j]+"/"+data[v][w]);
//								} else {
//									data[v][w] = getRandomVar(means_inter[j],abw[j]);
//								}
//							}
//						}
//					}
//					writeFile4C(data, fileName);
//				}
//			}
//		}
		createRandomMatrices(100, 100, "/vol/assb/graph_cluster_files/FORCEnD_tests_sita/artificial3/data_10/", 21, 10, 2, 10);
		createRandomMatrices(100, 100, "/vol/assb/graph_cluster_files/FORCEnD_tests_sita/artificial3/data_20/", 21, 20, 2, 10);
		createRandomMatrices(100, 100, "/vol/assb/graph_cluster_files/FORCEnD_tests_sita/artificial3/data_30/", 21, 30, 2, 10);

	}
	
	private static void createRandomMatrices(int size, int number, String directory, int mean, int abw, int minCluster, int maxCluster){
		for(int n = 0; n < number; n++) {
			int noOfClusters = minCluster + rand.nextInt(maxCluster - minCluster+1);
			int[] clustersizes = new int[noOfClusters];
			double costs = 0.0;
			for(int i = 0; i < size; i++) {
				int r = rand.nextInt(noOfClusters);
				clustersizes[r]++;
			}
			double[][] sims = new double[size][size];
			for(int i = 0; i < noOfClusters; i++) {
				int lowest = 0;
				int highest = 0;
				for(int j = 0; j <= i; j++) {
					if(i != j) lowest += clustersizes[j];
					highest += clustersizes[j];
				}
				for(int x = lowest; x < highest; x++) {
					for(int y = 0; y < size; y++) {
						if(sims[x][y] != 0.0) continue;
						if(x == y) continue;
						if(y >= lowest && y < highest) {
							sims[x][y] = getRandomVar(mean,abw);
							sims[y][x] = sims[x][y];
							if(sims[x][y] < 0) costs -= sims[x][y];
						} else {
							sims[x][y] = getRandomVar(-mean,abw);
							sims[y][x] = sims[x][y];
							if(sims[x][y] > 0) 	costs += sims[x][y];
						}
					}
				}
			}
			writeFile4C(sims,directory+"cm_nr_"+n+"_size_"+size+".cm");
			writeCostFile(costs,directory+"cm_nr_"+n+"_size_"+size+".costs");
		}
	}
	
	private static void writeCostFile(double costs, String filename){
		File datei = new File(filename);
		System.out.println(costs);
	    FileWriter fw;
		try {
			fw = new FileWriter(datei);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(costs+"");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	private static double getRandomVar(double med, double abw) {
		double norm = rand.nextGaussian();
//		System.out.println(norm);
		double norm2 = med +abw * norm;
		return norm2;
	}
	
	private static void writeFile2C (double[][] data, String filename, double step) {
		File datei = new File(filename);

	    double [][] matrix = new double[data.length*data[0].length][data.length*data[0].length];
		for(int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				for(int k = 0; k < data.length; k++) {
					for (int l = 0; l < data[k].length; l++) {
						if(i == k && j == l) continue;
						double distance = Math.abs(data[i][j] - data[k][l]);
						distance = step - distance;
						int pos1 = (i == 0) ? j : j + data[0].length;
						int pos2 = (k == 0) ? l : l + data[0].length;
						matrix[pos1][pos2] = distance;
						//System.out.println(pos1+"/"+pos2+":"+distance);
					}
				}
			}
		}
		
	    FileWriter fw;
		try {
			fw = new FileWriter(datei);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(matrix.length+"\n");
			for (int i = 0; i < matrix.length; i++) {
				bw.write(i+"\n");
			}
			for (int i = 0; i < matrix.length; i++) {
				for (int j = i+1; j < matrix.length; j++) {
					if(j == (matrix.length -1 )) {
						bw.write(matrix[i][j]+"\n");
					} else {
						bw.write(matrix[i][j]+"\t");
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private static void writeFile4C (double[][]matrix, String filename) {
		File datei = new File(filename);
		
	    FileWriter fw;
		try {
			fw = new FileWriter(datei);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(matrix.length+"\n");
			for (int i = 0; i < matrix.length; i++) {
				bw.write(i+"\n");
			}
			for (int i = 0; i < matrix.length; i++) {
				for (int j = i+1; j < matrix.length; j++) {
					if(j == (matrix.length -1 )) {
						bw.write(matrix[i][j]+"\n");
					} else {
						bw.write(matrix[i][j]+"\t");
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
