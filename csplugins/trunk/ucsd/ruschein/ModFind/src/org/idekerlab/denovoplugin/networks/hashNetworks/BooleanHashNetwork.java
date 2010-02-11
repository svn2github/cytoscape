package org.idekerlab.denovoplugin.networks.hashNetworks;

import java.util.*;

import org.idekerlab.denovoplugin.data.StringVector;

import org.idekerlab.denovoplugin.networks.*;
import org.idekerlab.denovoplugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.denovoplugin.utilities.IIterator;
import org.idekerlab.denovoplugin.utilities.files.*;

import java.io.*;

public class BooleanHashNetwork extends SBNetwork implements Iterable<SEdge>
{
	private Map<String,Set<SEdge>> nodeMap;
	private Set<SEdge> edgeSet;
	
	public BooleanHashNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
		this.edgeSet = new HashSet<SEdge>();
		this.nodeMap = new HashMap<String,Set<SEdge>>();
	}
	
	public BooleanHashNetwork(BooleanHashNetwork net)
	{
		super(net.selfOk,net.directed);
		this.edgeSet = new HashSet<SEdge>(net.edgeSet);
		this.nodeMap = new HashMap<String,Set<SEdge>>(net.nodeMap.size());
		
		for (String n : net.nodeMap.keySet())
			this.nodeMap.put(n, new HashSet<SEdge>(net.nodeMap.get(n)));
	}
	
	public BooleanHashNetwork(String file)
	{
		super(false,false);
		load(file,0,1);
	}
	
	public BooleanHashNetwork(boolean selfOk, boolean directed, int startsize)
	{
		super(selfOk,directed);
		this.edgeSet = new HashSet<SEdge>(startsize);
		this.nodeMap = new HashMap<String,Set<SEdge>>(100);
	}
	
	public BooleanHashNetwork(String file, boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
		load(file,0,1);
	}
	
	public BooleanHashNetwork(String file, boolean selfOk, boolean directed, int n1col, int n2col)
	{
		super(selfOk,directed);
		load(file,n1col,n2col);
	}
	
	private void load(String file, int n1col, int n2col)
	{
		int numEdges = FileUtil.countLines(file);
		this.edgeSet = new HashSet<SEdge>(numEdges);
		this.nodeMap = new HashMap<String,Set<SEdge>>();
		
		for (String line : new FileIterator(file))
		{
			String[] cols = line.split("\t");
			this.add(new UndirectedSEdge(cols[n1col],cols[n2col]));
		}
	}
	
	private void updateNodeMapAdd(SEdge i)
	{
		Set<SEdge> iset = nodeMap.get(i.getI1());
		if (iset==null)
		{
			Set<SEdge> newIset = new HashSet<SEdge>();
			newIset.add(i);
			nodeMap.put(i.getI1(), newIset);
		}else iset.add(i);
		
		iset = nodeMap.get(i.getI2());
		if (iset==null)
		{
			Set<SEdge> newIset = new HashSet<SEdge>();
			newIset.add(i);
			nodeMap.put(i.getI2(), newIset);
		}else iset.add(i);
	}
	
	private void updateNodeMapRemove(SEdge i)
	{
		Set<SEdge> iset = nodeMap.get(i.getI1());
		if (iset!=null) iset.remove(i);
		
		iset = nodeMap.get(i.getI2());
		if (iset!=null) iset.remove(i);
	}
	
	public boolean isSelfOk()
	{
		return selfOk;
	}
	
	public boolean isDirected()
	{
		return directed;
	}
	
	public Iterator<SEdge> iterator()
	{
		return edgeSet.iterator();
	}
	
	public Set<String> getNodes()
	{
		return new HashSet<String>(nodeMap.keySet());
	}
	
	public void add(SEdge i)
	{
		if (!this.selfOk && i.isSelf()) return;
		this.edgeSet.add(i);
		this.updateNodeMapAdd(i);
	}
	
	public void remove(SEdge i)
	{
		this.edgeSet.remove(i);
		this.updateNodeMapRemove(i);
	}
	
	public void addAll(SNetwork net)
	{
		for (SEdge i : net.edgeIterator())
			this.add(i);
	}
	
	public void removeAll(SNetwork net)
	{
		for (SEdge i : net.edgeIterator())
			this.remove(i);
	}
	
	public void add(String n1, String n2)
	{
		if (this.directed) this.add(new DirectedSEdge(n1,n2)); 
		else this.add(new UndirectedSEdge(n1,n2));
	}
	
	public int numEdges()
	{
		return this.edgeSet.size();
	}
	
	public int numNodes()
	{
		return this.nodeMap.size();
	}
	
	public BooleanHashNetwork subNetworkExclusive(Set<String> nodes)
	{
		
		BooleanHashNetwork subnet = new BooleanHashNetwork(this.selfOk, this.directed, this.numEdges());
		
		for (SEdge i : this)
			if (nodes.contains(i.getI1()) && nodes.contains(i.getI2())) subnet.add(i);
	
		return subnet;
	}
	
	public boolean contains(String n1, String n2)
	{
		return edgeSet.contains(new UndirectedSEdge(n1,n2));
	}
	
	public boolean contains(SEdge e)
	{
		return edgeSet.contains(e);
	}
	
	public BooleanHashNetwork shuffleNodes()
	{
		StringVector nodes = new StringVector(this.getNodes());
		StringVector rnodes = nodes.sample(nodes.size(), false);
		Map<String,String> rnodeMap = new HashMap<String,String>(rnodes.size(),1); 
		
		for (int i=0;i<rnodes.size();i++)
			rnodeMap.put(nodes.get(i), rnodes.get(i));
		
		BooleanHashNetwork out = new BooleanHashNetwork(this.selfOk,this.directed,this.numEdges());
		for (SEdge i : this)
		{
			if (this.directed) out.add(new DirectedSEdge(rnodeMap.get(i.getI1()),rnodeMap.get(i.getI2())));
			else out.add(new UndirectedSEdge(rnodeMap.get(i.getI1()),rnodeMap.get(i.getI2())));
		}
		
		return out;
	}
	
	public IIterator<SEdge> edgeIterator()
	{
		return new IIterator<SEdge>(this.iterator());
	}
	
	public IIterator<String> nodeIterator()
	{
		return new IIterator<String>(this.nodeMap.keySet().iterator());
	}
	
	public Set<SEdge> getEdges()
	{
		return new HashSet<SEdge>(this.edgeSet);
	}
	
	public static BooleanHashNetwork allVsAll(Set<String> nodes)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(false,false,nodes.size()*(nodes.size()-1)/2);
		
		for (String s1 : nodes)
			for (String s2 : nodes)
				if (!s1.equals(s2)) out.add(s1, s2);
		
		return out;
	}
	
	public BooleanHashNetwork sampleEdges(int samplesize, boolean replace)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(this.selfOk,this.directed,samplesize);
		
		List<SEdge> edgeList = new ArrayList<SEdge>(this.edgeSet);
		
		java.util.Random randgen = new java.util.Random();
		
		randgen.setSeed(System.nanoTime());
		
		if (replace)
		{
			for (int r=0;r<samplesize;r++)
			{
				int rand = randgen.nextInt(edgeList.size());
				out.add(edgeList.get(rand));
			}
		}else
		{
			int lsizem1 = edgeList.size()-1;
			
			for (int i=0;i<samplesize;i++)
			{
				int swapi = lsizem1-randgen.nextInt(edgeList.size()-i);
				SEdge temp = edgeList.get(i);
				edgeList.set(i, edgeList.get(swapi));
				edgeList.set(swapi, temp);
			}
			
			for (int i=0;i<samplesize;i++)
				out.add(edgeList.get(i));
		}
		
		return out;
	}
	
	public static int intesectSize(BooleanHashNetwork net1, BooleanHashNetwork net2)
	{
		int count = 0;
		
		for (SEdge e : net1.edgeSet)
			if (net2.contains(e)) count++;
		
		return count;
	}
	
	public static BooleanHashNetwork intersect(BooleanHashNetwork net1, BooleanHashNetwork net2)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(false,false);
		
		for (SEdge i : net1.edgeIterator())
			if (net2.contains(i)) out.add(i);
		
		return out;
	}
	
	public static BooleanHashNetwork union(BooleanHashNetwork net1, BooleanHashNetwork net2)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(net1);
		out.addAll(net2);
		
		return out;
	}
}
