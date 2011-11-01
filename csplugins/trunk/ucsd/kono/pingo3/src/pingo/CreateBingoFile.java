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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import BiNGO.BingoAlgorithm;
import BiNGO.ontology.Annotation;
import BiNGO.ontology.Ontology;

/**
 * *****************************************************************
 * CreateBiNGOFile.java Steven Maere & Karel Heymans (c) 2005-2010
 * --------------------
 * 
 * Class which creates a file with information about the selected cluster:
 * ontology type and curator, time of creation, alpha, sort of test and
 * correction, p-values and corrected p-values, term id and name, x, X, n, N.
 * ******************************************************************
 */

public class CreateBingoFile {

	private ModuleNetwork M;

	private File results;
	/**
	 * hashmap with key termID and value pvalue.
	 */
	private Map testMap;
	/**
	 * hashmap with key termID and value corrected pvalue.
	 */
	private Map correctionMap;
	/**
	 * hashmap with key termID and value x.
	 */
	private Map mapSmallX;
	/**
	 * hashmap with key termID and value n.
	 */
	private Map mapSmallN;
	/**
	 * hashmap with X.
	 */
	private Map mapBigX;
	/**
	 * hashmap with N.
	 */
	private Map mapBigN;
	/**
	 * String with alpha value.
	 */
	private String alphaString;
	/**
	 * String with used test.
	 */
	private String testString;
	/**
	 * String with used correction.
	 */
	private String correctionString;
	/**
	 * String for over- or underrepresentation.
	 */
	private String overUnderString;
	/**
	 * the annotation (remapped, i.e. including all parent annotations)
	 */
	private Annotation annotation;

	private Set deleteCodes;
	/**
	 * the ontology.
	 */
	private Ontology ontology;
	/**
	 * the annotation file path.
	 */
	private String annotationFile;
	/**
	 * the ontology file path.
	 */
	private String ontologyFile;
	/**
	 * the dir for saving the data file.
	 */
	private String dirName;
	/**
	 * the file name for the data file.
	 */
	private String fileName;
	/**
	 * the clusterVsString.
	 */
	private String clusterVsString;
	/**
	 * the categoriesString.
	 */
	private String catString;
	/**
	 * HashSet with the names of the selected nodes.
	 */
	private Set selectedCanonicalNameVector;
	/**
	 * hashmap with keys the GO categories and values HashSets of test set genes
	 * annotated to that category
	 */
	private Set noClassificationsSet;

	private Map annotatedGenes;

	private Map<String, Set<String>> alias;

	private static final String NONE = BingoAlgorithm.NONE;
	/**
	 * constant string for the checking of numbers of categories, before
	 * correction.
	 */
	private static final String CATEGORY_BEFORE_CORRECTION = BingoAlgorithm.CATEGORY_BEFORE_CORRECTION;
	/**
	 * constant string for the checking of numbers of categories, after
	 * correction.
	 */
	private static final String CATEGORY_CORRECTION = BingoAlgorithm.CATEGORY_CORRECTION;


	/**
	 * Constructor for an overrepresentation calculation with a correction.
	 * 
	 * @param testMap
	 *            HashMap with key: termID and value: pvalue.
	 * @param correctionMap
	 *            HashMap with key: termID and value: corrected pvalue.
	 * @param mapSmallX
	 *            HashMap with key: termID and value: #.
	 * @param mapSmallN
	 *            HashMap with key: termID and value: n.
	 * @param bigX
	 *            int with value of X.
	 * @param bigN
	 *            int with value of N.
	 * @param alphaString
	 *            String with value for significance level.
	 * @param ontology
	 *            the Ontology.
	 * @param testString
	 *            String with the name of the test.
	 * @param correctionString
	 *            String with the name of the correction.
	 * @param fileName
	 *            String with the name for the data-file.
	 * @param clusterVsString
	 *            String with option against what cluster must be tested.
	 * @param selectedCanonicalNameVector
	 *            HashSet with the selected genes.
	 */
	public CreateBingoFile(ModuleNetwork M, String alphaString, Annotation annotation, Set deleteCodes,
			Map alias, Ontology ontology, String annotationFile, String ontologyFile, String testString,
			String correctionString, String overUnderString, String dirName, String fileName, String clusterVsString,
			String catString) {

		this.M = M;
		this.alphaString = alphaString;
		this.annotation = annotation;
		this.ontology = ontology;
		this.annotationFile = annotationFile;
		this.ontologyFile = ontologyFile;
		this.testString = testString;
		this.correctionString = correctionString;
		this.overUnderString = overUnderString;
		this.dirName = dirName;
		this.fileName = fileName;
		this.clusterVsString = clusterVsString;
		this.catString = catString;
		this.annotatedGenes = new HashMap();
		this.alias = alias;
		this.deleteCodes = deleteCodes;
		this.results = new File(dirName, fileName);
		results.delete();
		try {
			results.createNewFile();
		} catch (IOException e) {
			System.out.println(e);
		}
		// date and time for filename uniqueness.
		String dateString = DateFormat.getDateInstance().format(new Date());
		String timeString = DateFormat.getTimeInstance().format(new Date());

		// actual writing of the file.

		try {

			BufferedWriter output = new BufferedWriter(new FileWriter(results, true));

			output.write("!File created with BiNGO (c) on " + dateString + " at " + timeString + "\n");
			output.write("!Selected ontology file : " + ontologyFile + "\n");
			output.write("!Selected annotation file : " + annotationFile + "\n");
			output.write("!Discarded evidence codes : ");
			Iterator it = deleteCodes.iterator();
			while (it.hasNext()) {
				output.write(it.next().toString() + "\t");
			}
			output.write("\n!" + overUnderString + "\n");
			output.write("!Selected statistical test : " + testString + "\n");
			output.write("!Selected correction : " + correctionString + "\n");
			output.write("!Selected significance level : " + alphaString + "\n");
			output.write("!Testing option : " + clusterVsString + "\n");
			if (testString.equals(NONE)) {
				output.write("Module_number" + "\t" + "Module_name" + "\t" + "GO-ID" + "\t" + "# in test set" + "\t"
						+ "# in reference set" + "\t" + "# total test set" + "\t" + "# total reference set" + "\t"
						+ "GO Description" + "\n");
			} else if (correctionString.equals(NONE)) {
				output.write("Module_number" + "\t" + "Module_name" + "\t" + "GO-ID" + "\t" + "P-value" + "\t"
						+ "# in test set" + "\t" + "# in reference set" + "\t" + "# total test set" + "\t"
						+ "# total reference set" + "\t" + "GO Description" + "\n");
			} else {
				output.write("Module_number" + "\t" + "Module_name" + "\t" + "GO-ID" + "\t" + "corr P-value" + "\t"
						+ "# in test set" + "\t" + "# in reference set" + "\t" + "# total test set" + "\t"
						+ "# total reference set" + "\t" + "GO Description" + "\n");
			}
			output.close();
			System.out.println("BiNGO results file : " + results.getPath());

		} catch (Exception e) {
			System.out.println(e);
		}

	}


	/**
	 * Method that creates the file with information about the cluster.
	 * <p/>
	 * without correction: ------------------- termID <tab> pvalue <tab> x <tab>
	 * n <tab> X <tab> N <tab> description <tab> test set genes in GO category
	 * <\n>
	 * <p/>
	 * with correction: ---------------- termID <tab> pvalue <tab> corrected
	 * pvalues <tab> x <tab> n <tab> X <tab> N <tab> description <tab> test set
	 * genes in GO category <\n>
	 */
	public void appendFile(String clusterName, int clusterNumber, Map testMap, Map correctionMap,
			Map mapSmallX, Map mapSmallN, Map mapBigX, Map mapBigN,
			Set selectedCanonicalNameVector, Set noClassificationsSet) {

		this.testMap = testMap;
		this.correctionMap = correctionMap;
		this.mapSmallX = mapSmallX;
		this.mapSmallN = mapSmallN;
		this.mapBigX = mapBigX;
		this.mapBigN = mapBigN;
		this.selectedCanonicalNameVector = selectedCanonicalNameVector;
		this.noClassificationsSet = noClassificationsSet;
		this.annotatedGenes = new HashMap();

		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(results, true));

			for (Object gene : selectedCanonicalNameVector) {
				String name = gene + "";
				Set tmp = alias.get(name);
				if (tmp != null) {
					Iterator it2 = tmp.iterator();
					while (it2.hasNext()) {
						int[] nodeClassifications = annotation.getClassifications(it2.next() + "");
						for (int k = 0; k < nodeClassifications.length; k++) {
							String cat = new Integer(nodeClassifications[k]).toString();
							if (!annotatedGenes.containsKey(cat)) {
								HashSet catset = new HashSet();
								annotatedGenes.put(cat, catset);
							}
							((HashSet) annotatedGenes.get(cat)).add(M.geneMap.get(name).name);
						}
					}
				}
			}

			// orden GO labels by increasing corrected p-value or increasing
			// smallX

			HashSet keySet;
			if (!testString.equals(NONE)) {
				keySet = new HashSet(testMap.keySet());
			} else {
				keySet = new HashSet(mapSmallX.keySet());
			}
			Iterator it = keySet.iterator();
			String[] keyLabels = new String[keySet.size()];
			for (int i = 0; it.hasNext(); i++) {
				keyLabels[i] = it.next().toString();
			}
			String[] ordenedKeySet;
			if (!testString.equals(NONE)) {
				ordenedKeySet = ordenKeysByPvalues(keyLabels);
			} else {
				ordenedKeySet = ordenKeysBySmallX(keyLabels);
			}
			boolean ok = true;

			for (int i = 0; (i < ordenedKeySet.length) && (ok == true); i++) {

				String termID = ordenedKeySet[i];
				String pvalue = "";
				String correctedPvalue = "";
				String smallX;
				String smallN;
				String bigX;
				String bigN;
				String description;
				// pvalue
				if (!testString.equals(NONE)) {
					try {
						pvalue = SignificantFigures.sci_format(testMap.get(new Integer(termID)).toString(), 5);
					} catch (Exception e) {
						pvalue = "N/A";
					}
				} else {
					pvalue = "N/A";
				}
				// corrected pvalue
				if (!correctionString.equals(NONE)) {
					try {
						correctedPvalue = SignificantFigures.sci_format(correctionMap.get(termID).toString(), 5);
					} catch (Exception e) {
						correctedPvalue = "N/A";
					}
				} else {
					correctedPvalue = "N/A";
				}
				// x
				try {
					smallX = mapSmallX.get(new Integer(termID)).toString();
				} catch (Exception e) {
					smallX = "N/A";
				}
				// n
				try {
					smallN = mapSmallN.get(new Integer(termID)).toString();
				} catch (Exception e) {
					smallN = "N/A";
				}
				// X
				try {
					bigX = mapBigX.get(new Integer(termID)).toString();
				} catch (Exception e) {
					bigX = "N/A";
				}
				// N
				try {
					bigN = mapBigN.get(new Integer(termID)).toString();
				} catch (Exception e) {
					bigN = "N/A";
				}
				// name
				try {
					description = ontology.getTerm(Integer.parseInt(termID)).getName();
				} catch (Exception e) {
					description = "?";
				}

				if (testString.equals(NONE)) {
					output.write(clusterNumber + "\t" + clusterName + "\t" + termID + "\t" + smallX + "\t" + smallN
							+ "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
					if (annotatedGenes.containsKey(termID)) {
						Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
						while (k.hasNext()) {
							output.write(k.next().toString());
							if (k.hasNext()) {
								output.write('|');
							}
						}
					}
					output.write("\n");
				} else if (correctionString.equals(NONE)) {
					if (catString.equals(CATEGORY_BEFORE_CORRECTION)) {
						if ((new BigDecimal(testMap.get(new Integer(ordenedKeySet[i])).toString()))
								.compareTo(new BigDecimal(alphaString)) < 0) {
							output.write(clusterNumber + "\t" + clusterName + "\t" + termID + "\t" + pvalue + "\t"
									+ smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
							if (annotatedGenes.containsKey(termID)) {
								Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
								while (k.hasNext()) {
									output.write(k.next().toString());
									if (k.hasNext()) {
										output.write('|');
									}
								}
							}
							output.write("\n");
						} else {
							ok = false;
						}
					} else {
						output.write(clusterNumber + "\t" + clusterName + "\t" + termID + "\t" + pvalue + "\t" + smallX
								+ "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
						if (annotatedGenes.containsKey(termID)) {
							Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
							while (k.hasNext()) {
								output.write(k.next().toString());
								if (k.hasNext()) {
									output.write('|');
								}
							}
						}
						output.write("\n");
					}
				} else {
					if (catString.equals(CATEGORY_CORRECTION)) {
						if ((new BigDecimal(correctionMap.get(ordenedKeySet[i]).toString())).compareTo(new BigDecimal(
								alphaString)) < 0) {
							output.write(clusterNumber + "\t" + clusterName + "\t" + termID + "\t" + correctedPvalue
									+ "\t" + smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description
									+ "\t");
							if (annotatedGenes.containsKey(termID)) {
								Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
								while (k.hasNext()) {
									output.write(k.next().toString());
									if (k.hasNext()) {
										output.write('|');
									}
								}
							}
							output.write("\n");
						} else {
							ok = false;
						}
					} else if (catString.equals(CATEGORY_BEFORE_CORRECTION)) {
						if ((new BigDecimal(testMap.get(new Integer(ordenedKeySet[i])).toString()))
								.compareTo(new BigDecimal(alphaString)) < 0) {
							output.write(clusterNumber + "\t" + clusterName + "\t" + termID + "\t" + correctedPvalue
									+ "\t" + smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description
									+ "\t");
							if (annotatedGenes.containsKey(termID)) {
								Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
								while (k.hasNext()) {
									output.write(k.next().toString());
									if (k.hasNext()) {
										output.write('|');
									}
								}
							}
							output.write("\n");
						} else {
							ok = false;
						}
					} else {
						output.write(clusterNumber + "\t" + clusterName + "\t" + termID + "\t" + correctedPvalue + "\t"
								+ smallX + "\t" + smallN + "\t" + bigX + "\t" + bigN + "\t" + description + "\t");
						if (annotatedGenes.containsKey(termID)) {
							Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
							while (k.hasNext()) {
								output.write(k.next().toString());
								if (k.hasNext()) {
									output.write('|');
								}
							}
						}
						output.write("\n");
					}
				}
			}

			output.close();
		}

		catch (Exception e) {
			System.out.println("Error: " + e);

		}

	}

	public String[] ordenKeysByPvalues(String[] labels) {

		for (int i = 1; i < labels.length; i++) {
			int j = i;
			// get the first unsorted value ...
			String insert_label = labels[i];
			BigDecimal val = new BigDecimal(testMap.get(new Integer(labels[i])).toString());
			// ... and insert it among the sorted
			while ((j > 0) && (val.compareTo(new BigDecimal(testMap.get(new Integer(labels[j - 1])).toString())) < 0)) {
				labels[j] = labels[j - 1];
				j--;
			}
			// reinsert value
			labels[j] = insert_label;
		}
		return labels;
	}

	public String[] ordenKeysBySmallX(String[] labels) {

		for (int i = 1; i < labels.length; i++) {
			int j = i;
			// get the first unsorted value ...
			String insert_label = labels[i];
			BigDecimal val = new BigDecimal(mapSmallX.get(new Integer(labels[i])).toString());
			// ... and insert it among the sorted
			while ((j > 0) && (val.compareTo(new BigDecimal(mapSmallX.get(new Integer(labels[j - 1])).toString())) > 0)) {
				labels[j] = labels[j - 1];
				j--;
			}
			// reinsert value
			labels[j] = insert_label;
		}
		return labels;
	}

}
