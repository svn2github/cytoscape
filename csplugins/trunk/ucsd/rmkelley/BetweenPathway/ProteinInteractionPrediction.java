package ucsd.rmkelley.BetweenPathway;
import java.util.*;
import cytoscape.CyNetwork;
import giny.model.Node;
import ucsd.rmkelley.Util.OrderedPair;
import ucsd.rmkelley.Util.UnorderedPair;

public class ProteinInteractionPrediction{
  Collection models;
  CyNetwork physicalNetwork;
  CyNetwork geneticNetwork;
  public ProteinInteractionPrediction(CyNetwork physicalNetwork, CyNetwork geneticNetwork, Collection models){
    this.models = models;
    this.physicalNetwork = physicalNetwork;
    this.geneticNetwork = geneticNetwork;
  }

  public HashMap makePredictions(){
    HashMap results = new HashMap();
    for(Iterator modelIt = models.iterator();modelIt.hasNext();){
      NetworkModel model = (NetworkModel)modelIt.next();
      makePredictions(model.one,model.two,results);
      makePredictions(model.two,model.one,results);
    }
    return results;
  }

  protected void makePredictions(Set one, Set two, HashMap results){
    for(Iterator nodeIt = one.iterator();nodeIt.hasNext();){
      Node node = (Node)nodeIt.next();
      for(Iterator partnerIt = two.iterator();partnerIt.hasNext();){
	Node partner = (Node)partnerIt.next();
	if(physicalNetwork.isNeighbor(node,partner)){
	  continue;
	}
	List sharedPhysical = sharedNeighbors(node,partner,physicalNetwork,one);
	List sharedGenetic = sharedNeighbors(node,partner,geneticNetwork,two);
	if(sharedPhysical.size() == 0 || sharedGenetic.size() == 0){
	  continue;
	}
	
	UnorderedPair upair = new UnorderedPair(node,partner);
	Set pairSet = (Set)results.get(upair);
	if(pairSet == null){
	  pairSet = new HashSet();
	  results.put(upair,pairSet);
	}
	for(Iterator physicalNeighborIt = sharedPhysical.iterator();physicalNeighborIt.hasNext();){
	  Node physicalNeighbor = (Node)physicalNeighborIt.next();
	  for(Iterator geneticNeighborIt = sharedGenetic.iterator();geneticNeighborIt.hasNext();){
	    Node geneticNeighbor = (Node)geneticNeighborIt.next();
	    OrderedPair opair = new OrderedPair(physicalNeighbor,geneticNeighbor);
	    pairSet.add(opair);
	  }
	}
      }
    }
  }

  protected List sharedNeighbors(Node one, Node two, CyNetwork network, Set restriction){
    Vector result = new Vector();
    for(Iterator neighborIt = restriction.iterator();neighborIt.hasNext();){
      Node neighbor = (Node)neighborIt.next();
      if(network.isNeighbor(one,neighbor) && network.isNeighbor(two,neighbor)){
	result.add(neighbor);
      }
    }
    return result;
  }
}
