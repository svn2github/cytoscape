package de.layclust.iterativeclustering;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ThreadFactory;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import de.clusteranalysis.Fmeassure;
import de.costmatrixcreation.main.ArgsParseException;
import de.costmatrixcreation.main.Config;
import de.costmatrixcreation.main.CostMatrixCreator;
import de.layclust.start.TransClust;
import de.layclust.taskmanaging.ClusteringManager;
import de.layclust.taskmanaging.ClusteringManagerTask;
import de.layclust.taskmanaging.ClusteringTask;
import de.layclust.taskmanaging.InvalidInputFileException;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;
import de.layclust.taskmanaging.TaskUtility;
import de.layclust.taskmanaging.gui.TransClustGui;
import de.layclust.taskmanaging.gui.TransClustGuiListener;

public class IteratorThread extends Thread {
	
	
	private TransClustGui gui;
	
	private boolean hierarchical;
	
	private boolean reducedMatrices;
	
	private float upperBound;
	
	private String resultsPath;
	
	private File iterativeClusteringTempDir;
	
	public IteratorThread(TransClustGui gui, boolean hierarchical, boolean reducedMatrices, float upperBound){
		
		this.gui = gui;
		this.hierarchical = hierarchical;
		this.reducedMatrices = reducedMatrices;
		this.upperBound = upperBound;
		this.resultsPath = TaskConfig.clustersPath;
	}
	
	
	 public void run(){
		try{
			if(!TaskConfig.tempDir.exists()){
				TaskConfig.tempDir.mkdir();
			}
			
			iterativeClusteringTempDir = new File(TaskConfig.tempDir.getAbsolutePath() + File.separator + "iterativeCLusteringTempDir");
			if(iterativeClusteringTempDir.exists()){
				this.gui.deleteDirectoryRecursively(iterativeClusteringTempDir);
			}
			iterativeClusteringTempDir.mkdir();
			
			 
			if(!hierarchical){
				BufferedWriter bw = new BufferedWriter(new FileWriter(TaskConfig.clustersPath));
				this.gui.popUp = new JFrame();
				this.gui.popUp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				this.gui.popUp.setVisible(true);
				JPanel jp = new JPanel();
				JProgressBar jpb = new JProgressBar((int) Math.rint(TaskConfig.minThreshold), (int) Math.rint(TaskConfig.minThreshold));
				jp.add(jpb);
				this.gui.popUp.add(jp);
				this.gui.popUp.pack();
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int top = (screenSize.height - this.gui.popUp.getHeight()) / 2;
				int left = (screenSize.width - this.gui.popUp.getWidth()) / 2; 
				this.gui.popUp.setLocation(left, top);
				
				for (double threshold = TaskConfig.minThreshold; threshold <= TaskConfig.maxThreshold; threshold+=TaskConfig.thresholdStepSize) {
				
					jpb.setValue((int) Math.rint(threshold));
					jpb.setStringPainted(true);
					jpb.setString(threshold+"");
					
					File costmatrices = new File(iterativeClusteringTempDir.getAbsolutePath() + File.separator + "costmatrices");
					costmatrices.mkdir();
					
					String[] args = {"-s",Config.similarityFile,"-c",costmatrices.getAbsolutePath(),"-t", new Double(threshold).toString(), "-cs", "false","-gui","false","-rm", this.reducedMatrices+"", "-ub", this.upperBound+""};
					
					CostMatrixCreator.main(args);
					
					TaskConfig.mode = TaskConfig.CLUSTERING_MODE;
					ClusteringManager cm = new ClusteringManager(costmatrices.getAbsolutePath());
					cm.initParametersAndCCs();
					if(this.gui!=null){
						this.gui.guiListener.setGeneralGuiVariablesToConfig();
					}
					TaskConfig.clustersPath = iterativeClusteringTempDir.getAbsolutePath() + File.separator + "results_" + threshold + ".txt";
					cm.runClustering();
					TaskConfig.mode = TaskConfig.COMPARISON_MODE;
					
					double fmeasure = -1;
					try {
						fmeasure = Fmeassure.fMeassure();
					} catch (Exception e) {
					}
				
					
					bw.write(threshold + "\t");
					if(fmeasure!=-1){
						bw.write(fmeasure+"\t");
					}else{
						bw.write("-\t");
					}
					
					BufferedReader br = new BufferedReader(new FileReader(TaskConfig.clustersPath));
					String cluster = null;
					String line;
					while((line = br.readLine())!=null){
						if(!line.trim().equals("")){
							String tabs[] = line.split("\t");
							if(cluster==null){
								bw.write(tabs[0]);
								cluster= tabs[1];
							}else if(!tabs[1].equals(cluster)){
								bw.write(";"+tabs[0]);
								cluster= tabs[1];
							}else {
								bw.write("," + tabs[0]);
							}
						}
					}
					br.close();
					bw.newLine();
					this.gui.deleteDirectoryRecursively(costmatrices);
					
				}
				this.gui.popUp.dispose();
				bw.close();
				 
			}else if(hierarchical){
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(TaskConfig.clustersPath));
				
				String similarityFileLocation = Config.similarityFile;
				String similarityFileLocation2 = Config.similarityFile;
				
				
				if(TaskConfig.clusterHierarchicalComplete){
					
					HashSet<Double> thresholds = new HashSet<Double>();
					BufferedReader br = new BufferedReader(new FileReader(similarityFileLocation));
					String line;
					while((line=br.readLine())!=null){
						if(line.equalsIgnoreCase("")) continue;
						String tabs[] = line.split("\t");
						thresholds.add(Double.parseDouble(tabs[2]));
					}
					double[] thresholdsArray = new double[thresholds.size()];
					int count = 0;
					for (Iterator iterator = thresholds.iterator(); iterator.hasNext();count++) {
						Double double1 = (Double) iterator.next();
						thresholdsArray[count] = double1;
					}
					Arrays.sort(thresholdsArray);
					
					for (int i = 0; i < thresholdsArray.length; i++) {
						
						double threshold = thresholdsArray[i];
						
						System.out.println("-------------------------" + threshold + "/" + thresholdsArray.length);
						
						similarityFileLocation2 = calculateHierarichal(threshold,bw,similarityFileLocation,similarityFileLocation2,iterativeClusteringTempDir);
						
						
					}
					
					bw.close();
						
					
					
					
				}else{
					for (double threshold = TaskConfig.minThreshold; threshold <= TaskConfig.maxThreshold; threshold+=TaskConfig.thresholdStepSize) {
						similarityFileLocation2 = calculateHierarichal(threshold,bw,similarityFileLocation,similarityFileLocation2,iterativeClusteringTempDir);
					}
					TaskConfig.clustersPath = this.resultsPath;
					bw.close();
				}
				
				
				
				Config.similarityFile =  similarityFileLocation ;
				
				
				if(TaskConfig.gui){
//					this.gui.visualizationTab.g2dView.fitContent();
				}
				
			}
		
			this.gui.deleteDirectoryRecursively(iterativeClusteringTempDir);
			TaskConfig.clustersPath = TaskConfig.tempDir.getAbsolutePath() + File.separator + "results.txt";
			
//			if(!tempDirExists){
//				deleteDirectoryRecursively(TaskConfig.tempDir);
//			}
			 
		}catch (Exception e) {
			TaskConfig.gui = true;
		}
		TaskConfig.gui = true;
		gui.runStopButton.setActionCommand(TransClustGui.EXECUTE_RUN);
		gui.runStopButton.setText("RUN");
		
		gui.guiListener.buildResultsView();
		
		gui.validate();		
		
	
	 }


	private String calculateHierarichal(double threshold, BufferedWriter bw, String similarityFileLocation, String similarityFileLocation2, File iterativeClusteringTempDir) throws IOException, ArgsParseException, InvalidInputFileException, InvalidTypeException {
		
		File costmatrices = new File(iterativeClusteringTempDir.getAbsolutePath() + File.separator + "costmatrices");
		costmatrices.mkdir();
		
		File similarityFile = new File(similarityFileLocation2);
		
		if(similarityFile.isDirectory()){
			
			File singletons = new File(iterativeClusteringTempDir.getAbsolutePath()+File.separator+"singletons.txt");
			BufferedReader singletonReader = new BufferedReader(new FileReader(singletons));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(costmatrices.getAbsolutePath() + File.separator + "transitiveccs_format_1.tcc"));
			String line2;
			int clusternr = 0;
			while((line2=singletonReader.readLine())!=null){
				if(line2.trim().equals("")) continue;
				bw2.write(line2);
				bw2.newLine();
				clusternr++;
			}
			singletonReader.close();
			singletons.delete();
			
			File[] similarityFiles = similarityFile.listFiles();
			
			
			
			for (int i = 0; i < similarityFiles.length; i++) {
				
				File file = similarityFiles[i];
				
				String[] args = {"-s",file.getAbsolutePath(),"-c",costmatrices.getAbsolutePath(),"-t", new Double(threshold).toString(), "-cs", "false","-gui","false","-rm", this.reducedMatrices+"", "-ub", this.upperBound+""};
				
				CostMatrixCreator.main(args);
				
				file = new File(costmatrices.getAbsolutePath() + File.separator + "transitive_connected_components_format_1.tcc");
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				
				String line = br.readLine();
				
				while((line=br.readLine())!=null){
					if(!line.trim().equalsIgnoreCase("")){
						clusternr++;
						bw2.write(line);
						bw2.newLine();
					}
				}
				br.close();
				file.delete();
				
			}
			
			bw2.close();
			
			BufferedReader br = new BufferedReader(new FileReader(costmatrices.getAbsolutePath() + File.separator + "transitiveccs_format_1.tcc"));
			bw2 = new BufferedWriter(new FileWriter(costmatrices.getAbsolutePath() + File.separator + "transitive_connected_components_format_1.tcc"));
			bw2.write("Number of connected components: " + clusternr);
			bw2.newLine();
			
			String line;
			
			while((line=br.readLine())!=null){
				if(!line.trim().equalsIgnoreCase("")){
					bw2.write(line);
					bw2.newLine();
				}
			}
			br.close();
			bw2.close();
			new File(costmatrices.getAbsolutePath() + File.separator + "transitiveccs_format_1.tcc").delete();
			
		}else{
			String[] args = {"-s",similarityFile.getAbsolutePath(),"-c",costmatrices.getAbsolutePath(),"-t", new Double(threshold).toString(), "-cs", "false","-gui","false","-rm", this.reducedMatrices+"", "-ub", this.upperBound+""};
			
			CostMatrixCreator.main(args);
		}
		
		TaskConfig.mode = TaskConfig.CLUSTERING_MODE;
		ClusteringManager cm = new ClusteringManager(costmatrices.getAbsolutePath());
		cm.initParametersAndCCs();
		if(this.gui!=null){
			this.gui.guiListener.setGeneralGuiVariablesToConfig();
		}
		TaskConfig.clustersPath = iterativeClusteringTempDir.getAbsolutePath() + File.separator + "results_" + threshold + ".txt";
		cm.runClustering();
		TaskConfig.mode = TaskConfig.HIERARICHAL_MODE;
		
		double fmeasure = -1;
		try {
			fmeasure = Fmeassure.fMeassure();
		} catch (Exception e) {
		}
		
		bw.write(threshold + "\t");
		if(fmeasure!=-1){
			bw.write(fmeasure+"\t");
		}else{
			bw.write("-\t");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(TaskConfig.clustersPath));
		String cluster = null;
		String line;
		int clusterNumber = 0;
		Vector<HashSet<String>> v = new Vector<HashSet<String>>();
		Hashtable<String, String> clusters = new Hashtable<String, String>();
		Hashtable<String,Vector<String>> clusters2 = new Hashtable<String, Vector<String>>();
		while((line = br.readLine())!=null){
			if(!line.trim().equals("")){
				String tabs[] = line.split("\t");
				clusters.put(tabs[0], tabs[1]);
				if(clusters2.containsKey(tabs[1])){
					clusters2.get(tabs[1]).add(tabs[0]);
				}else{
					Vector<String> v2 = new Vector<String>();
					v2.add(tabs[0]);
					clusters2.put(tabs[1], v2);
				}
				if(cluster==null){
					clusterNumber++;
					HashSet<String> hs = new HashSet<String>();
					hs.add(tabs[0]);
					bw.write(tabs[0]);
					v.add(hs);
					cluster= tabs[1];
				}else if(!tabs[1].equals(cluster)){
					clusterNumber++;
					HashSet<String> hs = new HashSet<String>();
					hs.add(tabs[0]);
					v.add(hs);
					bw.write(";"+tabs[0]);
					cluster= tabs[1];
				}else {
					v.get(v.size()-1).add(tabs[0]);
					bw.write("," + tabs[0]);
				}
			}
		}
		
		File simFileDirectory = new File(iterativeClusteringTempDir + File.separator + "simFiles");
		if(simFileDirectory.exists()){
			this.gui.deleteDirectoryRecursively(simFileDirectory);
		}
		simFileDirectory.mkdir();
		
		Hashtable<String, BufferedWriter> writerHash = new Hashtable<String, BufferedWriter>();
		for (int i = 0; i < clusterNumber; i++) {
			if(v.get(i).size()>1) writerHash.put("" + i, new BufferedWriter(new FileWriter(simFileDirectory.getAbsolutePath() + File.separator + "simFile" + i + ".txt")));
		}
		
		BufferedReader simFileReader = new BufferedReader(new FileReader(similarityFileLocation));
		
		while((line = simFileReader.readLine())!=null){
			if(!line.trim().equals("")){
				String tabs[] = line.split("\t");
				if(clusters.get(tabs[0]).equals(clusters.get(tabs[1]))){
					writerHash.get(clusters.get(tabs[0])).write(line);
					writerHash.get(clusters.get(tabs[0])).newLine();
				}
			}
		}
		for (int i = 0; i < clusterNumber; i++) {
			if(v.get(i).size()>1) writerHash.get("" + i).close();
		}
		
		BufferedWriter bw3 = new BufferedWriter(new FileWriter(iterativeClusteringTempDir.getAbsolutePath()+File.separator+"singletons.txt"));
		
		for (Iterator<String> iterator = clusters2.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Vector<String> v2 = clusters2.get(key);
			if(v2.size()==1){
				bw3.write("1\t" + v2.get(0));
				bw3.newLine();
			}
			
		}
		bw3.close();
		br.close();
		bw.newLine();
		this.gui.deleteDirectoryRecursively(costmatrices);
		return simFileDirectory.getAbsolutePath();
	}


	

}

