package de.clusteranalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import de.layclust.taskmanaging.TaskConfig;

public class Fmeassure {

	
	public static double fMeassure(Hashtable<String,Hashtable<String,Boolean>> clusterReference,Hashtable<String,Hashtable<String,Boolean>> cluster){
		
		int proteins = countProteins(clusterReference);

		double fmeasure = 0;
		
		int count = 0;
		Enumeration<String> e = clusterReference.keys();
		while(e.hasMoreElements()){
			String clusterID = e.nextElement();
			Hashtable<String,Boolean> h = clusterReference.get(clusterID);
			int proteinsInReference = h.size();
			double maxValue = findMax(proteinsInReference,cluster,h);
			fmeasure +=(maxValue*proteinsInReference);
			count++;
		}
		fmeasure /= proteins;
		
		
		
		
		return fmeasure;
	}
	
	public static double fMeassure() throws IOException{
		
		
		Hashtable<String,Hashtable<String,Boolean>> clusterReference=readCluster(TaskConfig.goldstandardPath);
		int proteins = countProteins(clusterReference);
		String fileName = TaskConfig.clustersPath;

		Hashtable<String,Hashtable<String,Boolean>> cluster=readCluster(fileName);

		double fmeasure = 0;
		
		int count = 0;
		Enumeration<String> e = clusterReference.keys();
		while(e.hasMoreElements()){
			String clusterID = e.nextElement();
			Hashtable<String,Boolean> h = clusterReference.get(clusterID);
			int proteinsInReference = h.size();
			double maxValue = findMax(proteinsInReference,cluster,h);
			fmeasure +=(maxValue*proteinsInReference);
			count++;
		}
		fmeasure /= proteins;
		
		
		
		
		return fmeasure;
		
	}
	
	public static double fMeassure(String goldStandardFile, String clustersFile){
		
		double value = 0;
		
		
		return value;
		
	}
	

	private static  double findMax(int proteinsInReference, Hashtable<String,Hashtable<String,Boolean>> cluster,Hashtable<String,Boolean> h){
		double max = 0;
		Enumeration<String> e = cluster.keys();
		while(e.hasMoreElements()){
			double dummy = 0;
			int common = 0;
			String id = e.nextElement();
			Hashtable h2 = cluster.get(id);
			if(h.size()<h2.size()){
				common = calculateCommonProteins(h,h2);
			}else{
				common = calculateCommonProteins(h2,h);
			}
//			System.out.println(common);
			double dummy2 = 2*common;
//			System.out.println(dummy2);
			double dummy3 = h.size()+h2.size();
//			System.out.println(dummy3);
			dummy = (2*common)/(h.size()+h2.size());
			dummy = dummy2/dummy3;
//			System.out.println(dummy);
			if(dummy>max){
				max = dummy;
			}
			if(max>=(h.size()/2)){
				return max;
			}
		}
		return max;
	}
	
	private static  int countProteins(Hashtable<String,Hashtable<String,Boolean>> c){
		int proteins = 0;
		Enumeration<String> e = c.keys();
		while(e.hasMoreElements()){
			String id = e.nextElement();
			Hashtable<String,Boolean> h = c.get(id);
			proteins += h.size();
		}
		return proteins;
	}
	

	private static  int calculateCommonProteins(Hashtable<String,Boolean> c1,Hashtable<String,Boolean> c2 ){
		int common = 0;
		Enumeration<String> e = c1.keys();
		while(e.hasMoreElements()){
			String id = e.nextElement();
			if(c2.containsKey(id)){
				common++;
			}
		}
		return common;
	}

	
	private static  Hashtable<String,Hashtable<String,Boolean>> readCluster(String fileName) throws IOException{	
		Hashtable<String,Hashtable<String,Boolean>> cluster = new Hashtable<String,Hashtable<String,Boolean>>();
		BufferedReader br = myBufferedReader(fileName);
		String line;
		while((line=br.readLine())!=null){
			String tokens[] = line.split("\t");
			Hashtable<String,Boolean> h = new Hashtable<String,Boolean>();
			if(cluster.containsKey(tokens[1])){
				h = cluster.get(tokens[1]);			
			}
			h.put(tokens[0], true);
			cluster.put(tokens[1], h);
		}
		return cluster;
	}
	
	
	private static BufferedReader myBufferedReader(String file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		return br;
	}
	
	private BufferedWriter myBufferedWriter(String file) throws IOException {
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		return bw;
	}
	
}
