package nct.graph.basic;

import nct.graph.WeightedNode;

public class BasicWeightedNode<IDType extends Comparable<? super IDType>,
                               WeightType extends Comparable<? super WeightType>> 
			       implements WeightedNode<IDType,WeightType> {

	WeightType weight;
	IDType id;

	public BasicWeightedNode(IDType id, WeightType weight){
		this.weight = weight;
		this.id = id;
	}
	public WeightType getWeight(){
		return weight;
	}
	
	public IDType getID(){
		return id;
	}
	
	public int compareTo(WeightedNode<IDType,WeightType> other) {
		return id.compareTo(other.getID());
	}

	public String toString() {
		return "["+ id.toString() + "," + weight.toString() + "]";
	}
}

