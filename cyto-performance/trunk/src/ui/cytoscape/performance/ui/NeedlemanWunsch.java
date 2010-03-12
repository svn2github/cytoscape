/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.performance.ui;

import java.util.List;
import java.util.LinkedList;


public class NeedlemanWunsch<T> {

	private List<T> seq1;
	private List<T> seq2;
	private LinkedList<T> aligned1;
	private LinkedList<T> aligned2;

	private LinkedList<T> merged;

	private int gapPenalty;
	private int match;
	private int mismatch;

	private int optimalScore;

	private static final int END = 0;
	private static final int DOWN = 1;
	private static final int ACROSS = 2;
	private static final int DIAG = 4;
	
	public NeedlemanWunsch(List<T> s1, List<T> s2) {

		seq1  = s1;
		seq2  = s2;
		aligned1 = new LinkedList<T>(); 
		aligned2 = new LinkedList<T>(); 
		merged = new LinkedList<T>(); 

		gapPenalty = 0;
		match = 2;
		mismatch = -2;

		if ( s1 != null )
			align();
		else {
			aligned1 = null;
			aligned2 = new LinkedList<T>(s2);
			merged = new LinkedList<T>(s2);
			optimalScore = 0;
		}
	}

	public List<T> getAligned1() { return aligned1; }
	public List<T> getAligned2() { return aligned2; }
	public List<T> getMerged() { return merged; }
	public int getAlignmentScore() { return optimalScore; }

	private void align() {
	
		int[][] S = new int[ seq1.size()+1 ][ seq2.size()+1 ];
		int[][] T = new int[ seq1.size()+1 ][ seq2.size()+1 ];
	
		// init matrices
		S[0][0] = 0;
		for ( int j = 1; j < S[0].length; j++ ) {
			S[0][j] = S[0][j - 1] + gapPenalty;
			T[0][j] = ACROSS;
		}

		int optI = 0;
		int optJ = 0;
		for ( int i = 1; i < S.length; i++ ) {

			// init
			S[i][0] = S[i-1][0] + gapPenalty;
			T[i][0] = DOWN;

			for ( int j = 1; j < S[i].length; j++ ) {
				int ijmatch = mismatch; 
				if ( seq1.get(i-1).equals( seq2.get(j-1) ) )
					ijmatch = match;

				S[i][j] = Math.max( S[i-1][j-1] + ijmatch ,
				                    Math.max( S[i-1][j] + gapPenalty, 
				                              S[i][j-1] + gapPenalty ) );

				if ( S[i][j] == (S[i-1][j] + gapPenalty) )
					T[i][j] += DOWN;
				if ( S[i][j] == (S[i][j-1] + gapPenalty) )
					T[i][j] += ACROSS;
				if ( S[i][j] == (S[i-1][j-1] +  ijmatch) )
					T[i][j] += DIAG;

				optJ = j;
			}
			optI = i;
		}

		optimalScore = S[seq1.size()][seq2.size()];

		// trace back
		int i = optI;
		int j = optJ;

		while ( i >= 0 && j >= 0 ) {
			int origI = i;
			int origJ = j;

			if ( (T[i][j] & DIAG) == DIAG && i > 0 && j > 0 ) {
				aligned1.addFirst( seq1.get(i-1) );
				aligned2.addFirst( seq2.get(j-1) );
				i--;
				j--;
			} else if ( (T[i][j] & ACROSS) == ACROSS && j > 0 ) {
				aligned1.addFirst( null );
				aligned2.addFirst( seq2.get(j-1) );
				j--;
			} else if ( (T[i][j] & DOWN) == DOWN && i > 0 ) {
				aligned1.addFirst( seq1.get(i-1) );
				aligned2.addFirst( null );
				i--;
			} else {
				// hacky
				break;
			}
		}

		mergeAligned();
	}

	private void mergeAligned() {
		for ( int i = 0; i < aligned1.size(); i++ ) {
			T t = aligned1.get(i);
			if ( t == null )
				t = aligned2.get(i);
			merged.add(t);
		}
	}
}
