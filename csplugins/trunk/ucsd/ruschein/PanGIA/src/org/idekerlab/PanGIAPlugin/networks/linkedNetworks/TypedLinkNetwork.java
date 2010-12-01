package org.idekerlab.PanGIAPlugin.networks.linkedNetworks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.utilities.IIterator;
import org.idekerlab.PanGIAPlugin.utilities.files.FileIterator;


/**
 * Note: only use node/edge types which are final or finalish
 * @author ghannum
 */
public class TypedLinkNetwork<NT,ET>
{
	private boolean selfOK;
	private boolean directed;
	
	private Map<NT,TypedLinkNode<NT,ET>> nodes;
	private Set<TypedLinkEdge<NT,ET>> edges;
		
	public TypedLinkNetwork(boolean selfOK, boolean directed)
	{
		this.selfOK = selfOK;
		this.directed = directed;
		
		nodes = new HashMap<NT,TypedLinkNode<NT,ET>>();
		edges = new HashSet<TypedLinkEdge<NT,ET>>();
	}
	
	public TypedLinkNetwork(Collection<NT> cnodes, boolean selfOK, boolean directed)
	{
		this.selfOK = selfOK;
		this.directed = directed;
		
		edges = new HashSet<TypedLinkEdge<NT,ET>>();
		nodes = new HashMap<NT,TypedLinkNode<NT,ET>>(cnodes.size());
		
		this.addAllNodes(cnodes);
	}
	
	public boolean isSelfOK()
	{
		return selfOK;
	}
	
	public boolean isDirected()
	{
		return directed;
	}
	
	public Set<TypedLinkNode<NT,ET>> nodes()
	{
		return new HashSet<TypedLinkNode<NT,ET>>(nodes.values());
	}
	
	public Set<TypedLinkEdge<NT,ET>> edges()
	{
		return new HashSet<TypedLinkEdge<NT,ET>>(edges);
	}
	
	public Set<NT> getNodeValues()
	{
		Set<NT> out = new HashSet<NT>(nodes.size());
		
		for (TypedLinkNode<NT,ET> n : this.nodes.values())
			out.add(n.value());
		
		return out;
	}
	
	public IIterator<TypedLinkNode<NT,ET>> nodeIterator()
	{
		return new IIterator<TypedLinkNode<NT,ET>>(nodes.values().iterator());
	}
	
	public IIterator<TypedLinkEdge<NT,ET>> edgeIterator()
	{
		return new IIterator<TypedLinkEdge<NT,ET>>(edges.iterator());
	}
	
	public int getConnectedness(Set<NT> m1, Set<NT> m2)
	{
		int connectedness = 0;
		
		for (NT n1 : m1)
			for (NT n2 : m2)
				if (this.containsEdge(n1, n2)) connectedness++;
	
		return connectedness;
	}
	
	public int getConnectedness(TypedLinkNodeModule<NT,?> m1, TypedLinkNodeModule<NT,?> m2)
	{
		int connectedness = 0;
		
		for (TypedLinkNode<NT,?> n : m2)
			connectedness += m1.getConnectedness(n);
	
		return connectedness;
	}
	
	public int numNodes()
	{
		return nodes.size();
	}
	
	public int numEdges()
	{
		return edges.size();
	}
	
	public TypedLinkNode<NT,ET> getNode(NT node)
	{
		return nodes.get(node);
	}
	
	public TypedLinkEdge<NT,ET> getEdge(NT n1, NT n2)
	{
		TypedLinkNode<NT,ET> node1 = nodes.get(n1);
		if (node1==null) return null;
		
		return node1.getEdge(n2);
	}
	
	public void addNode(NT node)
	{
		nodes.put(node, new TypedLinkNode<NT,ET>(node));
	}
	
	public void addNode(TypedLinkNode<NT,ET> node)
	{
		nodes.put(node.value(), node);
	}
	
	public void addAllNodes(Collection<NT> cnodes)
	{
		for (NT node : cnodes)
			this.addNode(node);
	}
	
	public void removeNode(TypedLinkNode<NT,ET> node)
	{
		nodes.remove(node.value());
		
		Set<TypedLinkEdge<NT,ET>> neighborEdges = node.edges();
		for (TypedLinkEdge<NT,ET> ed : neighborEdges)
			this.removeEdge(ed);
	}
	
	public void removeEdge(TypedLinkEdge<NT,ET> edge)
	{
		edges.remove(edge);
		edge.source().removeNeighbor(edge.target());
		edge.target().removeNeighbor(edge.source());
	}
	
	public void removeEdgeWNodeUpdate(TypedLinkEdge<NT,ET> edge)
	{
		edges.remove(edge);
		edge.source().removeNeighbor(edge.target());
		edge.target().removeNeighbor(edge.source());
		
		if (edge.source().numNeighbors()==0) this.removeNode(edge.source());
		if (edge.target().numNeighbors()==0) this.removeNode(edge.target());
	}
	
	public void removeAllNodes(Collection<TypedLinkNode<NT,ET>> nodes)
	{
		for (TypedLinkNode<NT,ET> node : nodes)
			this.removeNode(node);
	}
	
	public void removeAllEdges(Collection<TypedLinkEdge<NT,ET>> edges)
	{
		for (TypedLinkEdge<NT,ET> edge : edges)
			this.removeEdge(edge);
	}
	
	public void removeAllEdgesWNodeUpdate(Collection<TypedLinkEdge<NT,ET>> edges)
	{
		for (TypedLinkEdge<NT,ET> edge : edges)
			this.removeEdgeWNodeUpdate(edge);
	}
	
	public void addEdgeWNodeUpdate(TypedLinkNode<NT,ET> n1, TypedLinkNode<NT,ET> n2, ET value)
	{
		TypedLinkEdge<NT,ET> newEdge = new TypedLinkEdge<NT,ET>(n1,n2,value,this.directed);
		edges.add(newEdge);
		n1.addNeighbor(n2, newEdge);
		n2.addNeighbor(n1, newEdge);
	}
	
	public void addEdgeWNodeUpdate(NT n1, NT n2, ET value)
	{
		addEdgeWNodeUpdate(this.getNode(n1),this.getNode(n2),value);
	}
	
	public void addEdgeWNodeUpdate(TypedLinkEdge<NT,ET> edge)
	{
		edges.add(edge);
		edge.source().addNeighbor(edge.target(), edge);
		edge.target().addNeighbor(edge.source(), edge);
	}
	
	public void addAllEdgesWNodeUpdate(List<TypedLinkEdge<NT,ET>> edges)
	{
		for (TypedLinkEdge<NT,ET> edge : edges)
			this.addEdgeWNodeUpdate(edge);
	}
	
	public String toString()
	{
		if (this.numEdges()==0) return "{}";
		
		String out = "{";
		
		Iterator<TypedLinkEdge<NT,ET>> ei = this.edges.iterator();
		out += ei.next().toString();
		
		while (ei.hasNext()) out += ","+ei.next().toString();
		
		return out+"}";
	}
	
	public boolean containsEdge(TypedLinkEdge<NT,ET> edge)
	{
		return edges.contains(edge);
	}
	
	public boolean containsEdge(NT n1, NT n2)
	{
		return (this.getEdge(n1, n2)!=null);
	}
	
	public boolean containsNode(NT node)
	{
		return nodes.containsKey(node);
	}
	
	public static TypedLinkNetwork<String,Double> loadSD(String file, int coln1, int coln2, int coledge)
	{
		TypedLinkNetwork<String,Double> out = new TypedLinkNetwork<String,Double>(false,false);
		
		int row = 1;
		for (String line : new FileIterator(file)) {
			if (row%1000000==0) System.out.println("Row: "+row); 
			String[] cols = line.split("\t");
			
			if (!out.containsNode(cols[coln1])) out.addNode(cols[coln1]);
			if (!out.containsNode(cols[coln2])) out.addNode(cols[coln2]);
			
			if (!out.containsEdge(cols[coln1],cols[coln2])) out.addEdgeWNodeUpdate(cols[coln1], cols[coln2], Double.valueOf(cols[coledge]));
			row++;
		}
		
		return out;
	}
	
	public TypedLinkNetwork<NT,ET> subNetwork(Set<NT> nodes)
	{
		TypedLinkNetwork<NT,ET> subnet = new TypedLinkNetwork<NT,ET>(this.selfOK,this.directed); 
		
		for (NT node : nodes)
			subnet.addNode(node);
		
		for (TypedLinkEdge<NT,ET> edge : this.edges)
			if (nodes.contains(edge.source().value()) && nodes.contains(edge.target().value())) subnet.addEdgeWNodeUpdate(edge.source(),edge.target(),edge.value());
		
		return subnet;
	}
	
	public TypedLinkNetwork<NT,ET> subNetwork(Set<NT> nodes, int degree)
	{
		Set<NT> allNodes = new HashSet<NT>(nodes);
		
		if (degree>0)
		{
			Set<NT> toadd = new HashSet<NT>(100);
			for (NT node : nodes)
			{
				TypedLinkNode<NT,ET> n = this.getNode(node);
				if (n!=null) toadd.addAll(n.neighbors(degree).getMemberValues());
			}
			
			allNodes.addAll(toadd);
		}
		
		return this.subNetwork(allNodes);
	}
	
	public Set<TypedLinkEdge<NT,ET>> getAllEdgeValues(Set<NT> m1, Set<NT> m2) {
		Set<TypedLinkEdge<NT,ET>> allEdgeValues = new HashSet<TypedLinkEdge<NT,ET>>();
		for (NT n1 : m1) {
			for (NT n2 : m2)
				if (this.containsEdge(n1, n2))
					allEdgeValues.add(this.getEdge(n1, n2));
		}

		return allEdgeValues;
	}
}
