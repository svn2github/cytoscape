
package org.cytoscape.venneuler;

import org.dishevelled.venn.VennModel;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class FakeVennModel implements VennModel<Integer> {

	private final Set<Integer>[] sets;

	public FakeVennModel(Set<Integer> ... sets) {
		this.sets = sets;
	}

	public FakeVennModel() {
		this.sets = new Set[]{};
	}

	public FakeVennModel(int[] ... arraySets) {
		this.sets = new Set[arraySets.length];
		for ( int i = 0; i < arraySets.length; i++ ) 
			this.sets[i] = makeSet(arraySets[i]);
	}

	private Set<Integer> makeSet(int[] s) {
		Set<Integer> set =  new HashSet<Integer>();
		for ( int i : s )
			set.add(i);
		return set;
	}

	public int size() {
		return sets.length;
	}

	public Set<Integer> get(int index) {
		return sets[index]; 
	}

	public Set<Integer> union() {
		return Collections.emptySet();
	}

	public Set<Integer> intersection() {
		return Collections.emptySet();
	}

	public Set<Integer> exclusiveTo(int index, int... additional) {
		return Collections.emptySet();
	}
}
