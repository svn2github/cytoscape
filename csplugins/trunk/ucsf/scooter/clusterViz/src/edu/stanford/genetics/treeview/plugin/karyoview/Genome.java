/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: Genome.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:50 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular,
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER
 */
package edu.stanford.genetics.treeview.plugin.karyoview;
import edu.stanford.genetics.treeview.*;
/**
 *  this class encapsulates the position of things. No expression data or headers
 *  or other such nonsense allowed.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 */
class Genome {

	/**
	 *  Adds an element. Multiple elements with the same cdtIndex are not allowed, so
	 *  don't do it.
	 *
	 * @param  l  The locus to add
	 */
	public void addLocus(ChromosomeLocus l) {
		loci[l.getCdtIndex()] = l;
		setStructValid(false);
	}

	
	/**
	 *  finds the actual position of the i'th locus. 
	 *  XXX This makes more sense to put in Chromsome
	 *
	 * @param  chromosome                          Which chromosme it's on
	 * @param  arm                                 Which arm it's on
	 * @param  index                               How many loci from the centromere it is
	 * @return                                     The position value
	 * @exception  ArrayIndexOutOfBoundsException  Description of the Exception
	 */
	public double getPosition(int chromosome, int arm, int index)
			 throws ArrayIndexOutOfBoundsException {
		if (isStructValid() == false) {
			buildTree();
		}
		ChromosomeLocus temp  = chromosomes[chromosome - 1].getLocus(arm, index);
		if (temp == null) {
			throw new ArrayIndexOutOfBoundsException();
		} else {
			return temp.getPosition();
		}
	}


	/**
	 *  Given a location, performs a binary search to find the closest locus.
	 * XXX - this belongs in Chromosome
	 * 
	 * @param  chromosome                          Which chromosme
	 * @param  arm                                 Which arm
	 * @param  position    						   Which position
	 * @return             The closest ChromosomeLocus
	 */
	public ChromosomeLocus getClosestLocus(int chromosome, int arm, double position) {
		return chromosomes[chromosome - 1].getClosestLocus(arm, position);
	}


	/**
	 *  Gets the ith locus
	 *
	 * @param  i  An index into the Cdt file
	 * @return    The ChromosomeLocus at that position.
	 */
	public ChromosomeLocus getLocus(int i) {
		return loci[i];
	}
	public int getNumLoci() {
		return loci.length;
	}

	/**
	 *  returns the maximum distance from centromere in Loci
	 *
	 * XXX - this belongs in Chromosome
	 * 
	 * @param  chromosome                          Which chromosme
	 * @param  arm                                 Which arm
	 * @return             The maxPosition value
	 */
	public double getMaxPosition(int chromosome, int arm) {
		if (isStructValid() == false) {
			buildTree();
		}
		return getChromosome(chromosome).getMaxPosition(arm);
	}
	public Chromosome getChromosome(int chromosome) {
		if (isStructValid() == false) {
			buildTree();
		}
		return chromosomes[chromosome - 1];
	}
	public int getNonemptyCount() {
		int count = 0;
		for (int i=1; i < getMaxChromosome(); i++) {
			Chromosome t = getChromosome(i);
			if (t.isEmpty() == false) {
				count ++;
			}
		}
		return count;
	}
	/**
	 *  returns the maximum distance from centromere in Loci, looking over all chromosomes.
	 *
	 * @return    The max distance in map units
	 */
	public double getMaxPosition() {
		if (isStructValid() == false) {
			buildTree();
		}
		double maxPos  = -1.0;
		for (int i = 0; i < chromosomes.length; i++) {
			double thisMax  = chromosomes[i].getMaxPosition();
			if (thisMax > maxPos) {
				maxPos = thisMax;
			}
		}
		return maxPos;
	}


	/**
	 *  returns largest chromosome number in loci.
	 *
	 * @return    The largest chromosome number
	 */
	public int getMaxChromosome() {
		if (isStructValid() == false) {
			buildTree();
		}
		return chromosomes.length;
	}


	/**  Stores list of Loci in Cdt order (must match double KaryoDrawer.dataValues[] ) */
	private ChromosomeLocus[] loci;
	/**  This is just an array of all the chromosomes...  */
	private Chromosome[] chromosomes;
	/** Is chromosomes valid? */
	private boolean structValid;
	/** Setter for structValid */
	private void setStructValid(boolean structValid) {
		this.structValid = structValid;
	}
	/** Getter for structValid */
	private boolean isStructValid() {
		return structValid;
	}

	private FileSet fileSet = null;
	/** Setter for fileSet */
	public void setFileSet(FileSet fileSet) {
		this.fileSet = fileSet;
	}
	/** Getter for fileSet */
	public FileSet getFileSet() {
		return fileSet;
	}

	/**
	 *  usually know how many elements we'll need...
	 *
	 * @param  n  Total number of Loci in this genome
	 */
	Genome(int n) {
		loci = new ChromosomeLocus[n];
		structValid = false;
	}


	/**
	 *  Constructs genome with loci from the DataModel. This defines the Cdt Index, although the actual
	 *  position can be loaded from another DataModel later (with loadPositions(DataModel);
	 *
	 * @param  tvmodel  A DataModel to extract loci from. 
	 */
	public Genome(DataModel tvmodel) {
		this(tvmodel.getDataMatrix().getNumRow());
		
		HeaderInfo geneInfo  = tvmodel.getGeneHeaderInfo();
		/**  must build loci  */
		int chrIndex         = geneInfo.getIndex("CHROMOSOME");
		int armIndex         = geneInfo.getIndex("ARM");
		int posIndex         = geneInfo.getIndex("POSITION");
		int orfIndex         = geneInfo.getIndex("YORF");
		int numRow           = tvmodel.getDataMatrix().getNumRow();
		fileSet = tvmodel.getFileSet();
		double orfInfo[]       = new double[3];
		for (int i = 0; i < numRow; i++) {
			ChromosomeLocus tmp  = null;
			try {
				if (chrIndex != -1) {
					orfInfo[0] = makeDouble(geneInfo.getHeader(i)[chrIndex]);
					orfInfo[1] = makeArm(geneInfo.getHeader(i)[armIndex]);
					orfInfo[2] = makeDouble(geneInfo.getHeader(i)[posIndex]);
				} else {
					parseYorf(geneInfo.getHeader(i)[orfIndex], orfInfo);
				}

				int chr;

				int arm;
				double pos;
				chr = (int) orfInfo[0];
				arm = (int) orfInfo[1];
				pos = orfInfo[2];
				tmp = new ChromosomeLocus(chr, arm, pos, i);

			} catch (KaryoParseException e) {
				tmp = new ChromosomeLocus(-1, -1, -1, i);
			}
			addLocus(tmp);
		}
		//	  buildTree() tree gets built when needed (may reassign loci using lookup...)
	}



	/**a
	 *  utility routine to help with parsing Cdt files.
	 *
	 * @param  sval                     String to parse
	 * @return                          Extacted double
	 * @exception  KaryoParseException  thrown on failed conversion
	 */
	private final static double makeArm(String sval) throws KaryoParseException {
		try {
			return makeDouble(sval);
		} catch (KaryoParseException e) {
			if (sval.indexOf('r') >= 0) return ChromosomeLocus.RIGHT;
			if (sval.indexOf('R') >= 0) return ChromosomeLocus.RIGHT;

			if (sval.indexOf('l') >= 0) return ChromosomeLocus.LEFT;
			if (sval.indexOf('L') >= 0) return ChromosomeLocus.LEFT;

			if (sval.indexOf('c') >= 0) return ChromosomeLocus.CIRCULAR;
			if (sval.indexOf('C') >= 0) return ChromosomeLocus.CIRCULAR;

			throw e;
		}
	}
	/**
	 *  utility routine to help with parsing Cdt files.
	 *
	 * @param  sval                     String to parse
	 * @return                          Extacted double
	 * @exception  KaryoParseException  thrown on failed conversion
	 */
	private final static double makeDouble(String sval) throws KaryoParseException {
		try {
			Double d  = new Double(sval);
			return d.doubleValue();
		} catch (Exception e) {
			throw new KaryoParseException(e.getMessage());
		}
	}


	/**
	 *  utility routine to help with parsing Cdt files.
	 *
	 * @param  sval                     String to parse - should be like YAL134C
	 * @param  array                    array to be filled with chromosome, arm, position
	 * @exception  KaryoParseException  thrown on failed conversion
	 */
	private final static void parseYorf(String sval, double[] array)
			 throws KaryoParseException {
		//	System.out.println("parseYorf called on " + sval);
		try {
			array[0] = 0;
			array[1] = 0;
			array[2] = 0;
			char chr  = sval.charAt(1);
			// second char determines chromosome
			switch (sval.charAt(1)) {
							case 'A':
								array[0] = 1;
								break;
							case 'B':
								array[0] = 2;
								break;
							case 'C':
								array[0] = 3;
								break;
							case 'D':
								array[0] = 4;
								break;
							case 'E':
								array[0] = 5;
								break;
							case 'F':
								array[0] = 6;
								break;
							case 'G':
								array[0] = 7;
								break;
							case 'H':
								array[0] = 8;
								break;
							case 'I':
								array[0] = 9;
								break;
							case 'J':
								array[0] = 10;
								break;
							case 'K':
								array[0] = 11;
								break;
							case 'L':
								array[0] = 12;
								break;
							case 'M':
								array[0] = 13;
								break;
							case 'N':
								array[0] = 14;
								break;
							case 'O':
								array[0] = 15;
								break;
							case 'P':
								array[0] = 16;
			}

			// third char determines arm
			switch (sval.charAt(2)) {
							case 'L':
								array[1] = 1;
								break;
							case 'R':
								array[1] = 2;
			}

			// next three digits indicate position
			array[2] = makeDouble(sval.substring(3, 6));
		} catch (Exception e) {
			throw new KaryoParseException(e.getMessage());
		}
	}


	/**  internal method to build fast datastructure  */
	private synchronized void buildTree() {
		if (isStructValid() == true) {
			return;
		}

		allocateDataStructure();
		loadDataStructure();
		setStructValid(true);
	}


	/**  loads loci into allocated data structure.  */
	private void loadDataStructure() {
		// going to insertion sort, since I'm lazy.
		for (int i = 0; i < loci.length; i++) {
			ChromosomeLocus locus  = loci[i];
			if (locus.getChromosome() > 0) {
//				System.out.println("adding locus " + locus);
				chromosomes[locus.getChromosome() - 1].insertLocus(locus);
			}
		}
	}


	/**
	 *  This routine allocates the proper space for chromosomes. 
	 * It must be called immediately before loadDataStructure();
	 */
	private void allocateDataStructure() {
		// find max chromosome...
		int maxChr              = 0;
		for (int i = 0; i < loci.length; i++) {
			if (loci[i] == null) {
				continue;
			}
			if (loci[i].getChromosome() > maxChr) {
				maxChr = loci[i].getChromosome();
			}
		}

		//  counts of arms
		int[] leftArmCount      = new int[maxChr];
		int[] rightArmCount     = new int[maxChr];
		int[] circularArmCount  = new int[maxChr];
		for (int i = 0; i < maxChr; i++) {
			leftArmCount[i] = 0;
			rightArmCount[i] = 0;
			circularArmCount[i] = 0;
		}
		for (int i = 0; i < loci.length; i++) {
			int chr  = loci[i].getChromosome();
			int arm  = loci[i].getArm();
			if (arm == ChromosomeLocus.LEFT) {
				leftArmCount[chr - 1]++;
			}
			if (arm == ChromosomeLocus.RIGHT) {
				rightArmCount[chr - 1]++;
			}
			if (arm == ChromosomeLocus.CIRCULAR) {
				circularArmCount[chr - 1]++;
			}
		}

		chromosomes = new Chromosome[maxChr];
		for (int i = 0; i < maxChr; i++) {
			if (circularArmCount[i] != 0) {
				chromosomes[i] = new CircularChromosome(circularArmCount[i]);
			} else {
				chromosomes[i] = new LinearChromosome(leftArmCount[i], rightArmCount[i]);
			}
		}
	}// end allocateDataStructure

}


