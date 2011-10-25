package org.idekerlab.PanGIAPlugin.networks.matrixNetworks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.idekerlab.PanGIAPlugin.networks.*;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.*;
import org.idekerlab.PanGIAPlugin.data.IntVector;
import org.idekerlab.PanGIAPlugin.data.StringVector;
import org.idekerlab.PanGIAPlugin.utilities.IIterator;
import org.idekerlab.PanGIAPlugin.utilities.files.FileIterator;
import org.idekerlab.PanGIAPlugin.utilities.collections.SetUtil;


public class BooleanMatrixNetwork extends SBNetwork
{
	private Map<String,Integer> nodeLookup;
	private List<String> nodeValues;
	private boolean[][] connectivity;
		
	public BooleanMatrixNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>();
		this.connectivity = new boolean[0][0];
	}
	
	public BooleanMatrixNetwork(boolean selfOk, boolean directed, Collection<String> nodeValues)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>(nodeValues);
		Initialize(nodeValues.size());
		InitializeMap();
	}
	
	public BooleanMatrixNetwork(String filename, boolean selfOk, boolean directed, int col1, int col2)
	{
		super(selfOk,directed);
		
		Load(filename, selfOk, directed, col1, col2);
	}
	
	public BooleanMatrixNetwork(BooleanMatrixNetwork net)
	{
		super(net.selfOk,net.directed);
		this.nodeLookup = new HashMap<String,Integer>(net.nodeLookup);
		this.nodeValues = new ArrayList<String>(net.nodeValues);
				
		Initialize(nodeValues.size());
		
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = net.connectivity[i][j];
		
	}
	
	public BooleanMatrixNetwork(SNetwork net)
	{
		super(net);
		
		this.nodeValues = new ArrayList<String>(net.getNodes());
		Initialize(nodeValues.size());
		InitializeMap();
		
		for (SEdge e : net.edgeIterator())
			this.add(e);
		
	}
	
	public boolean isDirected()
	{
		return directed;
	}
	
	public boolean isSelfOk()
	{
		return selfOk;
	}
	
	public boolean contains(int i,int j)
	{
		if (j>i)
		{
			int temp = j;
			j = i;
			i = temp;
		}
		
		if (selfOk || i!=j)	return connectivity[i][j];
		else return false;
	}
	
	public boolean contains(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		if (i1==null || i2==null) return false;
		
		return contains(i1,i2);
	}
	
	public boolean contains(SEdge e)
	{
		return contains(e.getI1(),e.getI2());
	}
	
	public int indexOf(String value)
	{
		Integer i = nodeLookup.get(value); 
		if (i==null) return -1;
		else return i;
	}
	
	public void add(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		this.add(i1,i2);
	}
	
	public void addAll(SNetwork n)
	{
		for (SEdge i : n.edgeIterator())
			this.add(i);
	}
	
	public void add(SEdge i)
	{
		Integer i1 = nodeLookup.get(i.getI1());
		Integer i2 = nodeLookup.get(i.getI2());
		
		this.add(i1,i2);
	}
	
	public void remove(SEdge i)
	{
		Integer i1 = nodeLookup.get(i.getI1());
		Integer i2 = nodeLookup.get(i.getI2());
		
		if (i1==null || i2==null) return;
		
		this.remove(i1,i2);
	}
	
	public void removeAll(SNetwork n)
	{
		for (SEdge i : n.edgeIterator())
			this.remove(i);
	}
	
	public void remove(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		this.remove(i1,i2);
	}
	
	/*Whoa is this slow...
	public void add(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		if (i1==null)
		{
			nodeValues.add(n1);
			int newIndex = nodeValues.size()-1;
			nodeLookup.put(n1, newIndex);
			i1 = newIndex;
		}
		
		if (i2==null)
		{
			nodeValues.add(n2);
			int newIndex = nodeValues.size()-1;
			nodeLookup.put(n2, newIndex);
			i2 = newIndex;
		}
		
		//EXTEND ALL CONNECTIVITY BOOLEANS!
		
		this.add(i1,i2);
	}*/
	
	public void add(int n1, int n2)
	{
		if (!directed && n2>n1)
		{
			int temp = n1;
			n1 = n2;
			n2 = temp;
		}
		
		if (selfOk || n1!=n2) this.connectivity[n1][n2] = true;
	}
	
	public void remove(int n1, int n2)
	{
		if (n1<0 || n2<0) return;
		
		if (!directed && n2>n1)
		{
			int temp = n1;
			n1 = n2;
			n2 = temp;
		}
		
		if (selfOk || n1!=n2) this.connectivity[n1][n2] = false;
	}
	
	public String getNodeValue(int i)
	{
		return this.nodeValues.get(i);
	}
	
	public static BooleanMatrixNetwork allVsAll(Collection<String> nodes)
	{
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(false,false,nodes);		
		out.fill();
		
		return out;
	}
	
	public static BooleanMatrixNetwork aVsB(Collection<String> a, Collection<String> b)
	{
		Set<String> pooled = new HashSet<String>(a);
		pooled.addAll(b);
		
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(false,false,pooled);		
		
		for (String s1 : a)
			for (String s2 : b)
				out.add(s1,s2);
		
		return out;
	}
	
	private void Initialize(int size)
	{
		if (directed)
		{
			connectivity = new boolean[size][size];
			
			for (int i=0;i<size;i++)
				for (int j=0;j<size;j++)
					connectivity[i][j] = false;
		}
		else
		{
			connectivity = new boolean[size][];
			
			for (int i=0;i<size;i++)
			{
				if (selfOk) connectivity[i] = new boolean[i+1];
				else connectivity[i] = new boolean[i];
				
				for (int j=0;j<connectivity[i].length;j++)
					connectivity[i][j] = false;
			}
		}
	}
	
	private void InitializeMap()
	{
		this.nodeLookup = new HashMap<String,Integer>(nodeValues.size());
		for (int i=0;i<nodeValues.size();i++)
			nodeLookup.put(nodeValues.get(i), i);
	}
	
	private void Load(String file, boolean selfOk, boolean directed, int col1, int col2)
	{
		Set<String> nodes = new HashSet<String>();
		
		for (String line : new FileIterator(file))
		{
			String[] cols = line.split("\t");
			if (selfOk || !cols[col1].equals(cols[col2]))
			{
				nodes.add(cols[col1]);
				nodes.add(cols[col2]);
			}

		}
		
		//Initialize
		this.nodeValues = new ArrayList<String>(nodes);
		Initialize(nodeValues.size());
		InitializeMap();
		
		//Load network
		for (String line : new FileIterator(file))
		{
			String[] cols = line.split("\t");
			
			if (directed) this.add(cols[col1], cols[col2]);
			else 
			{
				int i1 = nodeLookup.get(cols[col1]);
				int i2 = nodeLookup.get(cols[col2]);
				
				if (selfOk || i1!=i2) this.add(i1,i2);
			}
		}
		
				
		if (directed && !this.selfOk)
			for (int i=0;i<connectivity.length;i++)
				connectivity[i][i] = false;
	}
	
	private void Load(SNetwork net)
	{	
		//Determine the set of all nodes
		this.nodeValues = new ArrayList<String>(net.getNodes());
		
		//Initialize
		Initialize(nodeValues.size());
		InitializeMap();
		
		//Load network
		for (SEdge inter : net.edgeIterator())
			this.add(inter.getI1(), inter.getI2());
				
		if (directed && !this.selfOk)
			for (int i=0;i<connectivity.length;i++)
				connectivity[i][i] = false;
	}
	
	private void Load(SNetwork net, List<String> nodes)
	{	
		//Determine the set of all nodes
		this.nodeValues = new ArrayList<String>(nodes);
		
		//Initialize
		Initialize(nodeValues.size());
		InitializeMap();
		
		//Load network
		for (SEdge inter : net.edgeIterator())
			this.add(inter.getI1(), inter.getI2());
				
		if (directed && !this.selfOk)
			for (int i=0;i<connectivity.length;i++)
				connectivity[i][i] = false;
	}

	public static int intersectSize(BooleanMatrixNetwork net1, BooleanMatrixNetwork net2)
	{
		int count = 0;
		
		for (int i=0;i<net1.connectivity.length;i++)
			for (int j=0;j<net1.connectivity[i].length;j++)
				if (net1.contains(i, j) && net2.contains(net1.nodeValues.get(i), net1.nodeValues.get(j)))
					count++;
		
		return count;
	}
	
	public int numNodes()
	{
		return this.nodeValues.size();
	}
	
	public int numEdges()
	{
		int count = 0;
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				if (connectivity[i][j]) count++;
		return count;
	}
	
	public static BooleanMatrixNetwork intersect(BooleanMatrixNetwork net1, BooleanMatrixNetwork net2)
	{
		Set<String> bothNodes = new HashSet<String>(net1.nodeValues);
		bothNodes.retainAll(net2.nodeValues);
		
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(net1.selfOk,net1.directed,bothNodes);
		
		for (int i=0;i<net1.connectivity.length;i++)
			for (int j=0;j<net1.connectivity[i].length;j++)
				if (net1.contains(i, j) && net2.contains(net1.nodeValues.get(i), net1.nodeValues.get(j)))
					out.add(net1.nodeValues.get(i), net1.nodeValues.get(j));
		
		return out;
	}
	
	public static BooleanMatrixNetwork union(BooleanMatrixNetwork net1, BooleanMatrixNetwork net2)
	{
		List<String> eitherNodes = new ArrayList<String>(net1.nodeValues);
		eitherNodes.addAll(net2.nodeValues);
		
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(net1.selfOk,net1.directed,eitherNodes);
		
		for (int i=0;i<net1.connectivity.length;i++)
			for (int j=0;j<net1.connectivity[i].length;j++)
				if (net1.contains(i, j)) out.add(net1.nodeValues.get(i), net1.nodeValues.get(j));
		
		for (int i=0;i<net2.connectivity.length;i++)
			for (int j=0;j<net2.connectivity[i].length;j++)
				if (net2.contains(i, j)) out.add(net2.nodeValues.get(i), net2.nodeValues.get(j));
		
		return out;
	}
	
	public static int unionSize(BooleanMatrixNetwork net1, BooleanMatrixNetwork net2)
	{
		int count = 0;
		for (int i=0;i<net1.connectivity.length;i++)
			for (int j=0;j<net1.connectivity[i].length;j++)
				if (net1.contains(i, j) || net2.contains(i, j)) count++;
		
		return count;
	}
	
	
	
	public void save(String file)
	{
		// Open file stream
		FileWriter fw = null;
		try 
		{
			fw = new FileWriter(file);
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("Error MNetwork.Save(String)");
			System.exit(0);
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("Error MNetwork.Save(String)");
			System.exit(0);
		}

		// Write interactions to file
		BufferedWriter bw = new BufferedWriter(fw);
		
		
		for (int i=0;i<this.connectivity.length;i++)
		{
			String nodeI = this.getNodeValue(i);
			for (int j=0;j<this.connectivity[i].length;j++)
			{
				//System.out.println(i+","+j+"  | "+this.connectivity.length+","+this.connectivity[i].length);
				
				if (this.connectivity[i][j])
				{
					try 
					{
						bw.write(nodeI+"\t"+this.getNodeValue(j)+"\n");
					} 
					catch (IOException e) 
					{
						System.out.println(e.getMessage());
						System.exit(0);
					}
				}
			}
		}
		
		// Close writer
		try 
		{
			bw.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			System.out.println("Error MNetwork.Save(String)");
			System.exit(0);
		}
	}
	
	/**
	 * Sets the connectivity of this network to a sample of another network. 
	 */
	public void setAsSample(BooleanMatrixNetwork reference, int sampleSize)
	{
		int referenceSize = reference.numEdges();
		
		IntVector RI = IntVector.getScale(0, referenceSize-1, 1);
		
		IntVector keepI = RI.sample(sampleSize, false);
		
		this.clear();
		
		int count = 0;
		for (int i=0;i<reference.connectivity.length;i++)
			for (int j=0;j<reference.connectivity[i].length;j++)
			{
				if (reference.connectivity[i][j])
				{
					if (keepI.contains(count)) this.add(reference.getNodeValue(i), reference.getNodeValue(j));
					count++;
				}
			}
		
	}
	
	/**
	 * Removes all connectivity from the matrix.
	 */
	public void clear()
	{
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = false;
	}
	
	/**
	 * Completely connects the matrix.
	 */
	public void fill()
	{
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = true;
	}
	
	public SBNetwork getNeighborhood(int nodeIndex)
	{
		SBNetwork outnet = new BooleanHashNetwork(this.selfOk,this.directed);
		
		for (int i=0;i<connectivity.length;i++)
			if (this.contains(i, nodeIndex)) outnet.add(this.getEdge(i,nodeIndex));
		
		return outnet;
	}
	
	/**
	 * Gets the neighborhood of a node, excluding interactions which contain a node (other than the anchor) in the given set
	 * @param nodeIndex
	 */
	public SBNetwork getNeighborhoodExclusive(int nodeIndex, Set<Integer> exclude)
	{
		SBNetwork outnet = new BooleanHashNetwork(this.selfOk,this.directed);
		
		for (int i=0;i<connectivity.length;i++)
		{
			if (exclude.contains(i)) continue;
			if (this.contains(i, nodeIndex)) outnet.add(this.getEdge(i,nodeIndex));
		}
		
		return outnet;
	}
	
	/**
	 * Gets the nodes which are neighboring the given node
	 */
	public Set<Integer> getNeighboringNodeIndexes(int nodeIndex)
	{
		Set<Integer> distalI = new HashSet<Integer>();
		
		for (int i=0;i<connectivity.length;i++)
			if (this.contains(i, nodeIndex)) distalI.add(i);
		
		return distalI;
	}
	
	public SEdge getEdge(int i1, int i2)
	{
		if (this.directed) return new DirectedSEdge(this.getNodeValue(i1),this.getNodeValue(i2));
		else return new UndirectedSEdge(this.getNodeValue(i1),this.getNodeValue(i2));
	}
	
	/**
	 * Generates a network of all interactions not specifically listed
	 */
	public static BooleanMatrixNetwork generateReverse(List<String> possibleNodes, SNetwork notInteractions)
	{
		BooleanMatrixNetwork out = BooleanMatrixNetwork.allVsAll(possibleNodes);
		
		for (SEdge i : notInteractions.edgeIterator())
			out.remove(i);
		
		return out;
	}
	
	public boolean containsNode(String node)
	{
		return nodeLookup.get(node)!=null;
	}
	
	/**
	 * Caution: For speed, this returns the actual reference, not a copy. Do not modify it.
	 */
	public List<String> nodeValues()
	{
		return nodeValues;
	}
	
	public Set<String> getNodes()
	{
		return new HashSet<String>(nodeValues);
	}
	
	/**
	 * Caution: For speed, this returns the actual connectivity matrix, not a copy. Do not modify it.
	 */
	public boolean[][] connectivity()
	{
		return connectivity;
	}
	
	public static double Jaccard(BooleanMatrixNetwork bmn1, BooleanMatrixNetwork bmn2)
	{
		return BooleanMatrixNetwork.intersectSize(bmn1, bmn2)/(double)BooleanMatrixNetwork.unionSize(bmn1,bmn2);
	}
	
	public String toString()
	{
		String out = "[";
		
		for (int i=0;i<connectivity.length;i++)
		{
			for (int j=0;j<connectivity[i].length;j++)
				if (this.contains(i, j))
				{
					if (out.length()!=1) out += ",";
					out+=this.nodeValues().get(i)+"-"+this.nodeValues().get(j);
					
					if (out.length()>2000)
					{
						out+= ",...";
						break;
					}
				}
				
			if (out.length()>2000) break;
		}
		
		
		out+= "]";
		
		return out;
	}
	
	public BooleanMatrixNetwork subNetworkExclusive(Set<String> nodes)
	{
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(this.selfOk, this.directed, SetUtil.intersect(nodes, this.nodeValues()));
		
		for (int i=0;i<connectivity.length;i++)
			if (out.containsNode(this.nodeValues.get(i)))
					for (int j=0;j<connectivity[i].length;j++)
						if (out.containsNode(this.nodeValues.get(j)) && this.contains(i, j)) out.add(this.getNodeValue(i),this.getNodeValue(j));
		
		return out;
				
	}
	
	public BooleanMatrixNetwork shuffleNodes()
	{
		StringVector nodes = new StringVector(this.nodeValues);
		StringVector rnodes = nodes.sample(nodes.size(), false);
		Map<String,String> rnodeMap = new HashMap<String,String>(rnodes.size(),1); 
		
		for (int i=0;i<rnodes.size();i++)
			rnodeMap.put(nodes.get(i), rnodes.get(i));
		
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(this.selfOk,this.directed,rnodes.asStringList());
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (this.connectivity[i][j]) out.add(i, j);
		
		return out;
	}
	
	public IIterator<String> nodeIterator()
	{
		return new IIterator<String>(this.nodeValues.iterator());
	}
	
	public IIterator<SEdge> edgeIterator()
	{
		throw new java.lang.UnsupportedOperationException("Matrix networks do not currently support edge iteration.");
	}
	
	public Set<SEdge> getEdges()
	{
		throw new java.lang.UnsupportedOperationException("Matrix networks do not currently support getEdges().");
	}

	public int[][] shortestPaths()
	{
		int[][] dists = new int[this.connectivity.length][];
		for (int i=0;i<this.connectivity.length;i++)
		{
			dists[i] = new int[this.connectivity[i].length];
			for (int j=0;j<dists[i].length;j++)
				dists[i][j] = (this.connectivity[i][j]) ? 1 : Integer.MAX_VALUE;
		}
				
		for (int k=0;k<this.connectivity.length;k++)
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
				{
					if (k==i || k==j) continue;
					
					int d1 = (!this.directed && k>i) ? dists[k][i] : dists[i][k];
					int d2 = (!this.directed && j>k) ? dists[j][k] : dists[k][j];
					
					int d1d2;
					if (Integer.MAX_VALUE-d1<=d2) d1d2 = Integer.MAX_VALUE;
					else d1d2 = d1+d2;
					
					dists[i][j] = Math.min(dists[i][j], d1d2);
					//dists[i][j] = Math.min(dists[i][j], dists[i][k]+dists[k][j]);
				}
		
		return dists;
	} 

}


