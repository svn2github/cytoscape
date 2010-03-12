
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
import java.util.Vector;
import java.util.Collections;


public class MultipleAlign<T> {

	List<List<T>> aligned;
	List<List<T>> results;

	public MultipleAlign(List<List<T>> l) {

		aligned = new LinkedList<List<T>>();
		results = new LinkedList<List<T>>();

        // progressively align the lists
        List<T> merged = null;
        for ( int i = 0; i < l.size(); i++ ) {
            NeedlemanWunsch<T> nw = new NeedlemanWunsch<T>(merged, l.get(i));
            aligned.add(nw.getAligned1());
            aligned.add(nw.getAligned2());
            merged = nw.getMerged();
        }
	
		// now go back and add gaps to the previously aligned lists
		int i = aligned.size()-1;
		Vector<T> a = null; 
		Vector<T> b = null;
		while ( i > 1 ) {
			a = new Vector<T>(aligned.get(i--));
			b = new Vector<T>(aligned.get(i--));

			if ( a.size() != merged.size() ) {
				for ( int x = 0; x < merged.size(); x++ ) { 
					T t = merged.get(x);
					if ( t == null ) {
						a.insertElementAt(null,x);
						b.insertElementAt(null,x);
					}
				}
			}

			results.add(a);
			merged = b; 
		}
		results.add(b);
		Collections.reverse(results);
	}

	public List<List<T>> getAlignment() {
		return results; 
	}
}
