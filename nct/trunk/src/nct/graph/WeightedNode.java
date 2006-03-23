package nct.graph;

public interface WeightedNode<IDType extends Comparable<? super IDType>,
                              WeightType extends Comparable<? super WeightType>> 
			      extends Comparable<WeightedNode<IDType,WeightType>> {
	public IDType getID();	
	public WeightType getWeight();			
}

