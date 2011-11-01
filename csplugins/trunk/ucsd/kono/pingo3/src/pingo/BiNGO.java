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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import BiNGO.AnnotationParser;
import BiNGO.BingoAlgorithm;
import BiNGO.BingoParameters;
import BiNGO.CalculateTestTask;
import BiNGO.ontology.OntologyTerm;

public class BiNGO {

	public static final String GRAPH = BingoAlgorithm.GRAPH;
	public static final String GENOME = BingoAlgorithm.GENOME;

	private BingoParameters bp;
	private BingoAlgorithm algorithm;
	private CreateBingoFile file;
	private ModuleNetwork M;
	// Keep track of progress for monitoring:
	private int maxValue;
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;

	// constructor
	public BiNGO(ModuleNetwork M, BingoParameters bp) throws IOException, InterruptedException {

		this.maxValue = -1;
		this.M = M;
		this.bp = bp;

		HashSet<String> geneSet = new HashSet<String>(M.geneMap.keySet());
		AnnotationParser annParser = bp.initializeAnnotationParser(geneSet);
		annParser.calculate();
		if (annParser.getStatus()) {
			bp.setAnnotation(annParser.getAnnotation());
			bp.setOntology(annParser.getOntology());
			bp.setAlias(annParser.getAlias());
		} else {
			System.out.println("Something wrong while parsing annotation...");
		}
		if (bp.getAnnParser().getOrphans()) {
			System.out.println("WARNING : Some category labels in the annotation file" + "\n"
					+ "are not defined in the ontology. Please check the compatibility of" + "\n"
					+ "these files. For now, these labels will be ignored and calculations" + "\n" + "will proceed.");
		}

		if (bp.getReferenceSet().equals(BingoAlgorithm.GENOME)) {
			HashSet geneNames = new HashSet();
			for (Gene g : M.geneSet) {
				geneNames.add(g.name.toUpperCase());
			}
			bp.setAllNodes(this.getAllCanonicalNamesFromAnnotation(geneNames));
		} else {
			HashSet allNodes = new HashSet();
			for (Gene g : M.geneSet) {
				allNodes.add(g.name.toUpperCase());
			}
			bp.setAllNodes(allNodes);
		}

		if (bp.isFileoutput()) {
			file = new pingo.CreateBingoFile(M, bp.getSignificance().toString(), bp.getAnnotation(),
					bp.getDeleteCodes(), bp.getAlias(), bp.getOntology(), bp.getAnnotationFile().toString(), bp
							.getOntologyFile().toString(), bp.getTest() + "", bp.getCorrection() + "",
					bp.getOverOrUnder() + "", bp.getFileoutput_dir(), bp.getCluster_name() + ".bgo",
					bp.getReferenceSet() + "", bp.getCategory() + "");
		}
	}

	void calculate() throws InterruptedException {
		int currentProgress = 0;
		maxValue = M.moduleSet.size();
		for (Module mod : M.moduleSet) {
			// Calculate Percentage. This must be a value between 0..1.
			int percentComplete = (int) (((double) currentProgress / maxValue));

			// Update the Task Monitor.
			// This automatically updates the UI Component w/ progress bar.
			if (taskMonitor != null) {
				taskMonitor.setProgress(percentComplete);
				taskMonitor.setStatusMessage("Gene " + currentProgress + " of " + maxValue);
			}

			currentProgress++;

			HashSet selectedNodes = new HashSet();
			for (Gene g : mod.Genes) {
				// Node node = (Node) Cytoscape.getCyNode(g.name, true);
				selectedNodes.add(g.name.toUpperCase());
			}
			bp.setSelectedNodes(selectedNodes);
			calculateGoEnrichment(bp.getSelectedNodes(), bp.getAllNodes(), mod);
		}
	}

	/**
	 * method that gets the canonical names for the wole annotation.
	 * 
	 * @return HashSet containing the canonical names.
	 */
	public HashSet getAllCanonicalNamesFromAnnotation(HashSet selectedNodes) {
		String[] nodes = bp.getAnnotation().getNames();
		// HashSet for storing the canonical names
		HashSet canonicalNameVector = new HashSet();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null && (nodes[i].length() != 0)) {
				canonicalNameVector.add(nodes[i].toUpperCase());
			}
		}

		// replace canonical names in annotation that match one of the canonical
		// names in the selected cluster, to get rid of e.g. splice variants if
		// the non-splice-specific gene is part of the selection
		Map<String, Set<String>> alias = bp.getAlias();
		Iterator it2 = selectedNodes.iterator();
		while (it2.hasNext()) {
			String name = it2.next() + "";
			Set tmp = alias.get(name);
			if (tmp != null) {
				Iterator it = tmp.iterator();
				while (it.hasNext()) {
					canonicalNameVector.remove(it.next() + "");
				}
				// add selected node name
				canonicalNameVector.add(name);
			}
		}
		return canonicalNameVector;
	}

	public void calculateGoEnrichment(Set selectedNodes, Set allNodes, Module m) throws InterruptedException {

		Map testMap = null;
		Map correctionMap = null;
		Map mapSmallX = null;
		Map mapSmallN = null;
		Map mapBigX = null;
		Map mapBigN = null;
		Set noClassificationsSet = new HashSet();

		// Use bing function
		algorithm = new BingoAlgorithm(bp);
		CalculateTestTask test = algorithm.calculate_distribution();
		test.calculate();

		testMap = test.getTestMap();

		mapSmallX = test.getMapSmallX();
		mapSmallN = test.getMapSmallN();
		mapBigX = test.getMapBigX();
		mapBigN = test.getMapBigN();

		for (Object ob : testMap.keySet()) {
			if (Double.parseDouble(testMap.get(ob).toString()) < bp.getSignificance().doubleValue()) {
				String s = ob.toString();
				OntologyTerm o = M.ontology.getTerm(Integer.parseInt(s));
				m.allGO.put(o, new Double(testMap.get(Integer.parseInt(s)).toString()));
			}
		}

		if (bp.isFileoutput()) {
			file.appendFile(m.name, m.number, testMap, correctionMap, mapSmallX, mapSmallN, mapBigX, mapBigN,
					selectedNodes, noClassificationsSet);
		}
	}

	public BingoAlgorithm getBingoAlgorithm() {
		return algorithm;
	}
}
