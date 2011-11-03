package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import BiNGO.BingoAlgorithm;
import BiNGO.BingoParameters;
import BiNGO.CalculateCorrectionTask;
import BiNGO.ontology.OntologyTerm;

/**
 * 
 * @author stmae
 */
public class PingoAnalysis {

	public ModuleNetwork M;
	public String filterGoCat;
	public String targetGoCat;
	public String startGoCat;
	public HashSet<OntologyTerm> filterGoCats = new HashSet<OntologyTerm>();
	public HashSet<OntologyTerm> targetGoCats = new HashSet<OntologyTerm>();
	public HashSet<OntologyTerm> startGoCats = new HashSet<OntologyTerm>();
	public HashSet<OntologyTerm> targetGoCatsChildren = new HashSet<OntologyTerm>();
	public HashMap<Gene, Module> moduleMap = new HashMap<Gene, Module>();
	public PingoParameters pp;

	private HashMap<OntologyTerm, HashSet<Gene>> pred;
	private HashMap<TestInstance, Double> pvals;
	private HashMap<TestInstance, Integer> smallX;
	private HashMap<TestInstance, Integer> bigX;
	private HashMap<TestInstance, Integer> smallN;
	private HashMap<TestInstance, Integer> bigN;
	private HashMap<TestInstance, HashSet<Gene>> neighbors;

	private TaskMonitor taskMonitor;

	public PingoAnalysis(PingoParameters pp, TaskMonitor tm) throws InterruptedException, IOException {
		this.taskMonitor = tm;
		this.pp = pp;
		M = new ModuleNetwork(pp);

		M.readGeneDescription(pp.getGeneDescriptionFile());
		filterGoCat = pp.getFilterGoCats();
		targetGoCat = pp.getTargetGoCats();
		startGoCat = pp.getStartGoCats();

		if (!filterGoCat.equals("")) {
			for (String s : filterGoCat.trim().split("\\s+")) {
				filterGoCats.add(M.ontology.getTerm(Integer.parseInt(s)));
			}
		}
		if (!targetGoCat.equals("")) {
			for (String s : targetGoCat.trim().split("\\s+")) {
				targetGoCats.add(M.ontology.getTerm(Integer.parseInt(s)));
			}
		}
		if (!startGoCat.equals("")) {
			for (String s : startGoCat.trim().split("\\s+")) {
				startGoCats.add(M.ontology.getTerm(Integer.parseInt(s)));
			}
		}

		// functional prediction of genes

		HashSet<Module> NHoods = new HashSet<Module>();

		int count = 0;
		// exclude all genes with known annotation to filter or target GO Cats
		HashSet<Gene> excludeGenes = new HashSet<Gene>();
		HashSet<Gene> includeGenes = new HashSet<Gene>();
		for (OntologyTerm o : filterGoCats) {
			if (M.annotatedGenes.containsKey(o)) {
				for (Gene g : M.annotatedGenes.get(o)) {
					excludeGenes.add(g);
				}
			}
		}
		
		for (OntologyTerm o : startGoCats) {
			if (M.annotatedGenes.containsKey(o)) {
				for (Gene g : M.annotatedGenes.get(o)) {
					includeGenes.add(g);
				}
			}
		}

		if (startGoCats.isEmpty()) {
			includeGenes = new HashSet<Gene>(M.geneSet);
		}

		for (Gene g : M.G.keySet()) {
			if (!excludeGenes.contains(g) && includeGenes.contains(g)) {
				Module m = new Module(g.name, count, new HashSet<Gene>(M.G.get(g).keySet()));
				NHoods.add(m);
				moduleMap.put(g, m);
				count++;
			}
		}

		if (!NHoods.isEmpty()) {
			M.setModules(NHoods);

			// targetGoCats (with our without child categories)
			targetGoCatsChildren = new HashSet<OntologyTerm>();
			for (OntologyTerm o : targetGoCats) {
				targetGoCatsChildren.add(o);
				addChildCategories(o, targetGoCatsChildren);
			}

			BingoParameters bp = makeBingoParameters(pp);
			BiNGO b = new BiNGO(M, bp, taskMonitor);
			b.calculate();
			pred = new HashMap<OntologyTerm, HashSet<Gene>>();

			// construct BiNGO test map
			HashMap<Integer, Double> testMap = new HashMap<Integer, Double>();
			HashMap<Integer, TestInstance> testId = new HashMap<Integer, TestInstance>();
			int id = 0;
			for (Module m : M.moduleSet) {
				Gene g = M.geneMap.get(m.name);
				// novel predictions for goCats
				for (OntologyTerm o : m.allGO.keySet()) {
					// if(targetGoCatsChildren.contains(o)){
					TestInstance t = new TestInstance(m, o);
					testId.put(id, t);
					testMap.put(id, m.allGO.get(o));
					id++;
					// }
				}
			}

			HashMap<Integer, Double> correctionMap = new HashMap<Integer, Double>();
			// perform multiple testing correction
			CalculateCorrectionTask correction = b.getBingoAlgorithm().calculate_corrections(testMap);
			if (correction != null) {
				correction.calculate();
				Map<String, String> tempMap = correction.getCorrectionMap();
				for (String s : tempMap.keySet())
					correctionMap.put(Integer.parseInt(s), Double.parseDouble(tempMap.get(s)));
			} else {
				System.out.println("Multiple testing correction could not be performed...");
				correctionMap = testMap;
			}

			// reinitialize allGO
			for (Module m : M.moduleSet) {
				m.allGO = new LinkedHashMap<OntologyTerm, Double>();
			}

			// fill allGO and predictions
			for (Integer i : correctionMap.keySet()) {
				TestInstance t = testId.get(i);
				t.m.allGO.put(t.o, correctionMap.get(i));
				if (!pred.containsKey(t.o)) {
					pred.put(t.o, new HashSet<Gene>());
				}
				pred.get(t.o).add(M.geneMap.get(t.m.name));
			}

			smallX = new HashMap<TestInstance, Integer>();
			bigX = new HashMap<TestInstance, Integer>();
			smallN = new HashMap<TestInstance, Integer>();
			bigN = new HashMap<TestInstance, Integer>();
			pvals = new HashMap<TestInstance, Double>();
			neighbors = new HashMap<TestInstance, HashSet<Gene>>();

			HashMap<Gene, HashMap<Integer, String>> bingoRes = readBingoResults(bp.getFileoutput_dir(),
					pp.getCluster_name() + "_func_pred.bgo");
			for (Integer i : testId.keySet()) {
				Gene g = M.geneMap.get(testId.get(i).m.name);
				// save best test result as gene attribute
				if (g.testInstance == null || g.testInstance.m.allGO.get(g.testInstance.o) > correctionMap.get(i)) {
					g.testInstance = testId.get(i);
				}
				String[] bingoTokens = bingoRes.get(g).get(testId.get(i).o.getId()).trim().split("\\t");
				smallX.put(testId.get(i), new Integer(bingoTokens[4]));
				smallN.put(testId.get(i), new Integer(bingoTokens[5]));
				bigX.put(testId.get(i), new Integer(bingoTokens[6]));
				bigN.put(testId.get(i), new Integer(bingoTokens[7]));
				pvals.put(testId.get(i), correctionMap.get(i));
				neighbors.put(testId.get(i), new HashSet<Gene>());
				String[] neighb = bingoTokens[9].split("\\|");
				for (String s : neighb) {
					if (!s.equals("")) {
						neighbors.get(testId.get(i)).add(M.geneMap.get(s));
					}
				}
			}

			if (pp.isFileoutput()) {
				outputPred(pp.getFileoutput_dir(), pp.getCluster_name(), bingoRes, pred, pp);
			}
		} else {
			System.out
					.println("No candidate genes in network after filtering... Check selected network or start/filter GO categories");
		}

	}

	public BingoParameters makeBingoParameters(PingoParameters pp) throws IOException {
		BingoParameters bp = new BingoParameters("");
		bp.setCluster_name(pp.getCluster_name() + "_func_pred");
		bp.setFileoutput(true);
		bp.setFileoutput_dir(pp.getPingoDir());
		bp.setAnnotationFile(makeCustomAnnotationFile(pp.getPingoDir(), targetGoCatsChildren));
		bp.setAnnotation_default(false);
		bp.setOntologyFile(makeCustomOntologyFile(pp.getPingoDir(), targetGoCatsChildren));
		bp.setOntology_default(false);
		bp.setReferenceSet(BingoAlgorithm.GENOME);
		bp.setSignificance(pp.getSignificance());
		bp.setTest(pp.getTest());
		// no correction in first instance, but set correction anyway for later
		bp.setCorrection(pp.getCorrection());
		bp.setCategory(BingoAlgorithm.NONE);
		bp.setOverOrUnder("Overrepresentation");
		bp.setDeleteCodes(new HashSet());
		bp.setBingoDir(pp.getPingoDir());
		return bp;
	}

	public String makeCustomOntologyFile(String outputDir, HashSet<OntologyTerm> goCatsChildren) {
		File f = new File(outputDir, "customOntology");
		try {
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);

			pw.println("(curator=pingo)(type=process)");
			for (OntologyTerm o : goCatsChildren) {
				pw.print(o.getId() + " = " + o.getName());
				boolean ok = false;
				for (int i : o.getParentsAndContainers()) {
					if (goCatsChildren.contains(M.ontology.getTerm(i))) {
						if (ok == false) {
							pw.print("[isa: ");
							ok = true;
						}
						pw.print(i + " ");
					}
				}
				if (ok == true) {
					pw.println("]");
				} else {
					pw.println("[isa: 0 ]");
				}
			}
			pw.println("0 = root");

			fw.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
		return f.getAbsolutePath();
	}

	public String makeCustomAnnotationFile(String outputDir, HashSet<OntologyTerm> goCatsChildren) {
		File f = new File(outputDir, "customAnnotation");
		try {
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);

			pw.println("(species=custom)(type=custom)(curator=pingo)");
			for (Gene g : M.geneSet) {
				for (OntologyTerm o : g.GOannotations) {
					if (goCatsChildren.contains(o)) {
						pw.println(g.name + " = " + o.getId());
					}
				}
				pw.println(g.name + " = 0");
			}

			fw.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
		return f.getAbsolutePath();
	}

	public HashMap<Gene, HashMap<Integer, String>> readBingoResults(String dataDir, String bingoFile) {
		HashMap<Gene, HashMap<Integer, String>> r = new HashMap<Gene, HashMap<Integer, String>>();
		String fileName = new File(dataDir, bingoFile).getAbsolutePath();
		try {

			BufferedReader file = new BufferedReader(new FileReader(fileName));
			String inputline;

			while (file.readLine().startsWith("!")) {
				// skip header
			}
			// column identifiers also skipped
			// Read data from file
			while ((inputline = file.readLine()) != null) {
				String[] tokens = inputline.trim().split("\\t");
				if (!r.containsKey(M.geneMap.get(tokens[1]))) {
					r.put(M.geneMap.get(tokens[1]), new HashMap<Integer, String>());
				}
				r.get(M.geneMap.get(tokens[1])).put(new Integer(tokens[2]), inputline);
			}
			file.close();

			// clean up temp files
			File f = new File(fileName);
			f.delete();
			f = new File(pp.getPingoDir(), "customAnnotation");
			f.delete();
			f = new File(pp.getPingoDir(), "customOntology");
			f.delete();

		} catch (IOException e) {
			System.out.println("Error: IOexception: " + e);
			System.exit(1);
		}
		return r;
	}

	public void outputPred(String dataDir, String outputPrefix, HashMap<Gene, HashMap<Integer, String>> bingoRes,
			HashMap<OntologyTerm, HashSet<Gene>> pred, PingoParameters pp) {
		File f = new File(dataDir, outputPrefix + ".pgo");
		try {
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);

			String dateString = DateFormat.getDateInstance().format(new Date());
			String timeString = DateFormat.getTimeInstance().format(new Date());

			pw.print("!File created with PiNGO (c) on " + dateString + " at " + timeString + "\n");
			pw.print("!Input network : " + pp.getGraphFile() + "\n");
			pw.print("!Start GO categories : " + pp.getStartGoCats() + "\n");
			pw.print("!Filter GO categories : " + pp.getFilterGoCats() + "\n");
			pw.print("!Target GO categories : " + pp.getTargetGoCats() + "\n");
			pw.print("!Selected ontology file : " + pp.getOntologyFile() + "\n");
			pw.print("!Selected namespace : " + pp.getNameSpace() + "\n");
			pw.print("!Selected annotation file : " + pp.getAnnotationFile() + "\n");
			pw.print("!Discarded evidence codes : ");
			for (Object s : pp.getDeleteCodes()) {
				pw.print(s.toString() + "\t");
			}
			pw.print("\n!" + pp.getOverOrUnder() + "\n");
			pw.print("!Selected statistical test : " + pp.getTest() + "\n");
			pw.print("!Selected correction : " + pp.getCorrection() + "\n");
			pw.print("!Selected significance level : " + pp.getSignificance().toString() + "\n");
			pw.print("!Reference set : " + pp.getReferenceSet() + "\n");
			pw.print("GO ID\tGO description\tGene name\tGene alias\tGene description\tP value\tAnnotated neighbors(x)\tAnnotated total(X)\tNeighborhood size(n)\tRef set size(N)\tNeighbors\tExisting annotations\n");

			for (OntologyTerm o : pred.keySet()) {
				for (Gene g : pred.get(o)) {
					String[] bingoTokens = bingoRes.get(g).get(o.getId()).trim().split("\\t");
					pw.print(o.getId() + "\t" + o.getName() + "\t" + g.name + "\t" + g.alias + "\t" + g.description
							+ "\t" + moduleMap.get(g).allGO.get(o) + "\t" + bingoTokens[4] + "\t" + bingoTokens[5]
							+ "\t" + bingoTokens[6] + "\t" + bingoTokens[7] + "\t" + bingoTokens[9] + "\t");
					for (OntologyTerm o2 : g.GOannotations) {
						pw.print(o2.getName() + "(" + o2.getId() + ")|");
					}
					pw.println();
				}
			}

			fw.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

	public void addChildCategories(OntologyTerm o, HashSet<OntologyTerm> c) {
		for (Integer o2 : (Set<Integer>) M.ontology.getTerms().keySet()) {
			if (isAncestorOf(o, M.ontology.getTerm(o2))) {
				c.add(M.ontology.getTerm(o2));
			}
		}
	}

	public boolean isAncestorOf(OntologyTerm parent, OntologyTerm child) {
		if (parent.isParentOrContainerOf(child)) {
			return true;
		}

		int[] childParents = child.getParentsAndContainers();

		for (int i = 0; i < childParents.length; i++) {
			OntologyTerm childParent = M.ontology.getTerm(childParents[i]);
			if ((childParent != null) && isAncestorOf(parent, childParent)) {
				return true;
			}
		}

		return false;
	}

	public HashMap<OntologyTerm, HashSet<Gene>> getPredictions() {
		return pred;
	}

	public HashMap<TestInstance, Double> getPvalues(Integer cat) {
		OntologyTerm o = M.ontology.getTerm(cat);
		HashMap<TestInstance, Double> pv = new HashMap<TestInstance, Double>();
		for (TestInstance t : pvals.keySet()) {
			if (isAncestorOf(o, t.o) || o.equals(t.o)) {
				pv.put(t, pvals.get(t));
			}
		}
		return pv;
	}

	public HashMap<TestInstance, Integer> getSmallX(Integer cat) {
		OntologyTerm o = M.ontology.getTerm(cat);
		HashMap<TestInstance, Integer> sX = new HashMap<TestInstance, Integer>();
		for (TestInstance t : smallX.keySet()) {
			if (isAncestorOf(o, t.o) || o.equals(t.o)) {
				sX.put(t, smallX.get(t));
			}
		}
		return sX;
	}

	public HashMap<TestInstance, Integer> getBigX(Integer cat) {
		OntologyTerm o = M.ontology.getTerm(cat);
		HashMap<TestInstance, Integer> bX = new HashMap<TestInstance, Integer>();
		for (TestInstance t : bigX.keySet()) {
			if (isAncestorOf(o, t.o) || o.equals(t.o)) {
				bX.put(t, bigX.get(t));
			}
		}
		return bX;
	}

	public HashMap<TestInstance, Integer> getSmallN(Integer cat) {
		OntologyTerm o = M.ontology.getTerm(cat);
		HashMap<TestInstance, Integer> sN = new HashMap<TestInstance, Integer>();
		for (TestInstance t : smallN.keySet()) {
			if (isAncestorOf(o, t.o) || o.equals(t.o)) {
				sN.put(t, smallN.get(t));
			}
		}
		return sN;
	}

	public HashMap<TestInstance, Integer> getBigN(Integer cat) {
		OntologyTerm o = M.ontology.getTerm(cat);
		HashMap<TestInstance, Integer> bN = new HashMap<TestInstance, Integer>();
		for (TestInstance t : bigN.keySet()) {
			if (isAncestorOf(o, t.o) || o.equals(t.o)) {
				bN.put(t, bigN.get(t));
			}
		}
		return bN;
	}

	public Map<TestInstance, Set<Gene>> getNeighbors(Integer cat) {
		OntologyTerm o = M.ontology.getTerm(cat);
		Map<TestInstance, Set<Gene>> n = new HashMap<TestInstance, Set<Gene>>();
		for (TestInstance t : neighbors.keySet()) {
			if (isAncestorOf(o, t.o) || o.equals(t.o)) {
				n.put(t, neighbors.get(t));
			}
		}
		return n;
	}

	public ModuleNetwork getModuleNetwork() {
		return M;
	}

	public class TestInstance {

		public Module m;
		public OntologyTerm o;

		public TestInstance(Module m, OntologyTerm o) {
			this.m = m;
			this.o = o;
		}
	}
}
