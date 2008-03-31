package de.costmatrixcreation.main;

import de.costmatrixcreation.gui.Console;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import de.costmatrixcreation.dataTypes.BlastFile;


public class Creator {

	
	public Creator(){
		
	}
	
	public void run(HashMap<Integer, String> proteins2integers, HashMap<String, Integer> integers2proteins) throws IOException{
		
		
		if(Config.gui) Console.println("Read Fasta File ... ");
		else System.out.println("Read Fasta File ... ");
		int[] proteinLengths = InOut.readFastaFile(Config.fastaFile, proteins2integers, integers2proteins);
		if(Config.gui) Console.println();
		else System.out.println();
		
		if(Config.gui) Console.println("Read Blast File ... ");
		else System.out.println("Read Blast File ... ");
		BlastFile bf = InOut.readBlastFileWithArray(Config.blastFile, integers2proteins);
		if(Config.gui) Console.println();
		else System.out.println();
		
		if(Config.gui) Console.println("Create Similarity File ...");
		else System.out.println("Create Similarity File ...");
		createSimilarityFileFromArray(Config.similarityFile, bf, proteins2integers, proteinLengths, Config.costModel);
		if(Config.gui) Console.println();
		else System.out.println();
		
		if(Config.splitAndWriteCostMatrices){
			bf = null;
			System.gc();
		}
		
		
	}
	
	private void createSimilarityFileFromArray(String outputFile, BlastFile bf, HashMap<Integer,String> proteins2integers, int[] proteinLengths, int costModel) throws IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		double percent = 0;
		double percentOld = 0;
		
		if(Config.gui){
			Console.println("start calculating similarity and writing file ...");
			Console.setBarValue(0);
			Console.restartBarTimer();
			Console.setBarText("calculating similarity and writing file");
		}else System.out.println("start calculating similarity and writing file ...");
		for (int i = 0; i < bf.size; ) {
			
			if(i%100000==0&&i>0){
				percent  = Math.rint(((double) i / bf.size)*10000)/100;
				if(percent>percentOld+1){
					percentOld = percent;
					if(Config.gui){
						Console.setBarValue((int) Math.rint(percent));
						Console.setBarTextPlusRestTime("calculating similarity and writing file  " + percent + " %");
					}else System.out.print(percent + " %\t" );
				}
//				System.out.print("\t" + percent + "%");
			}
						
			Vector<Integer> v = new Vector<Integer>();
			int source = bf.getSource(i);
			int target = bf.getTarget(i);
			
			if(source==target) {
				i++;
				continue;
			}
			
			v.add(i);
			
			int j = 1;
			try {
				
				while(true){
					
					int source2 = bf.getSource(i+j);
					int target2 = bf.getTarget(i+j);
					if(source==source2&&target==target2){
						v.add(i+j);
						j++;
					}else{
						i = i+j;
						break;
					}
					
				}
				
			}catch (Exception e) {
				i = i+j;
			}
			

			switch (costModel) {
			
			case 0:{//BeH
							
				double similarity = calculateBeH(v, bf);
				
				
				String sourceString = proteins2integers.get(source);

				String targetString  = proteins2integers.get(target);
				
				bw.write(sourceString + InOut.TAB + targetString + InOut.TAB + Double.toString(similarity));
				bw.newLine();
				Config.linesInSimilarityFile++;
				
				break;
			}
			
			case 1:{//SoH
				
				double similarity = calculateSoH(v, bf);
				
				String sourceString = proteins2integers.get(source);

				String targetString  = proteins2integers.get(target);
					
				bw.write(sourceString + InOut.TAB + targetString + InOut.TAB + Double.toString(similarity));
				bw.newLine();
				Config.linesInSimilarityFile++;
				
				break;
			}
			
			case 2:{//coverage BeH
				
				double similarity = calculateBeH(v, bf);
				
				double coverage = calculateCoverage(v,bf,proteinLengths, source, target);
				
				String sourceString = proteins2integers.get(source);

				String targetString  = proteins2integers.get(target);
					
				bw.write(sourceString + InOut.TAB + targetString + InOut.TAB + Double.toString(similarity+coverage));
				bw.newLine();
				Config.linesInSimilarityFile++;

				break;
			}
			
			case 3:{//coverage SoH
				
				double similarity = calculateSoH(v, bf);
				
				double coverage = calculateCoverage(v,bf,proteinLengths, source, target);
				
				String sourceString = proteins2integers.get(source);

				String targetString  = proteins2integers.get(target);
					
				bw.write(sourceString + InOut.TAB + targetString + InOut.TAB + Double.toString(similarity+coverage));
				bw.newLine();
				Config.linesInSimilarityFile++;
				
				break;
			}
			
			default:
				break;
			}//end switch
			
		}//end for	
		
		bw.close();
		
	}//end method

	private static double calculateBeH(Vector<Integer> v, BlastFile bf){
		
		double bestEvalue = 0;
		
		for (int k = 0; k < v.size(); k++) {
			int line = v.get(k);
			double evalue = bf.getEvalue(line);
		
			if(evalue>bestEvalue){
				bestEvalue = evalue;
			}		
		}
		
		if(bestEvalue<0) Console.println("" + bestEvalue);
		
		return bestEvalue;
		
	}
	
	private static double calculateSoH(Vector<Integer> v, BlastFile bf){
		
		double similarity = 0;
		
		for (int k = 0; k < v.size(); k++) {
			int line = v.get(k);
			double evalue = bf.getEvalue(line);
			similarity +=evalue;	
		}
//		double penalty = Math.pow(Config.penaltyForMultipleHighScoringPairs,v.size()-1);
//		similarity *= (1/penalty);
//		similarity = -Math.log10(similarity);
		
		return similarity;
		
	}
	
	private static double calculateCoverage(Vector<Integer> v, BlastFile bf, int[] proteinLengths, int source, int target){
		
		double coverage = 0;
		
		int sourceLength = proteinLengths[source];
		int targetLength = proteinLengths[target];
		
		boolean query[] = new boolean[sourceLength];
		boolean subject[] = new boolean[targetLength];
		
		for (int k = 0; k < v.size(); k++) {
			int line = v.get(k);
			int startQuery = bf.getStartQuery(line);
			int endQuery = bf.getEndQuery(line);
			int startSubject = bf.getStartSubject(line);
			int endSubject = bf.getEndSubject(line);
			
		
			for (int i = startQuery; i < endQuery; i++) {
				query[i] = true;
			}
			for (int i = startSubject; i < endSubject; i++) {
				subject[i] = true;
			}
			
		}
		
		double queryCoverage = 0;
		for (int i = 0; i < query.length; i++) {
			if(query[i]) queryCoverage++;
		}
		
		double subjectCoverage = 0;
		for (int i = 0; i < subject.length; i++) {
			if(subject[i]) subjectCoverage++;
		}
		
		queryCoverage/=sourceLength;
		
		subjectCoverage/=targetLength;
		
		coverage = Math.min(queryCoverage, subjectCoverage)*Config.coverageFactor;
		
		return coverage;
		
	}
	
}
