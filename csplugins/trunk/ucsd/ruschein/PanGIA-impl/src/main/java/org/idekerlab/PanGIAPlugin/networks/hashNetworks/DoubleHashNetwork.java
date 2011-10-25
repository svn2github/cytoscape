package org.idekerlab.PanGIAPlugin.networks.hashNetworks;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.data.StringVector;

import org.idekerlab.PanGIAPlugin.networks.*;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.networks.util.Mergers;
import org.idekerlab.PanGIAPlugin.utilities.files.*;
import org.idekerlab.PanGIAPlugin.utilities.*;

public class DoubleHashNetwork extends SDNetwork implements Iterable<SDEdge>
{
	private Map<String,Set<SEdge>> nodeMap;
	private Map<SEdge,SDEdge> edgeMap;
		
	public DoubleHashNetwork(boolean selfOk, boolean directed, int startsize)
	{
		super(selfOk,directed);
		this.edgeMap = new HashMap<SEdge,SDEdge>(startsize);
		this.nodeMap = new HashMap<String,Set<SEdge>>(100);
	}
	
	public DoubleHashNetwork(String file, boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
		Load(file,0,1,2);
	}
	
	public DoubleHashNetwork(String file, boolean selfOk, boolean directed, int n1col, int n2col, int valcol)
	{
		super(selfOk,directed);
		Load(file,n1col,n2col,valcol);
	}
	
	protected void Load(String file, int n1col, int n2col, int valcol)
	{
		int numEdges = FileUtil.countLines(file);
		this.edgeMap = new HashMap<SEdge,SDEdge>(numEdges);
		this.nodeMap = new HashMap<String,Set<SEdge>>();
		
		for (String line : new FileIterator(file))
		{
			String[] cols = line.split("\t");
			this.add(new UndirectedSDEdge(cols[n1col],cols[n2col], Double.valueOf(cols[valcol])));
		}
	}
	
	private void updateNodeMap(SEdge i)
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
	
	public boolean isSelfOk()
	{
		return selfOk;
	}
	
	public boolean isDirected()
	{
		return directed;
	}
	
	public Iterator<SDEdge> iterator()
	{
		return edgeMap.values().iterator();
	}
	
	public Set<String> getNodes()
	{
		return new HashSet<String>(nodeMap.keySet());
	}
	
	public void add(SDEdge i)
	{
		this.edgeMap.put(i, i);
		this.updateNodeMap(i);
	}
	
	public void add(String n1, String n2, double value)
	{
		if (this.directed) this.add(new DirectedSDEdge(n1,n2,value));
		else this.add(new UndirectedSDEdge(n1,n2,value));
	}
	
	public int numEdges()
	{
		return this.edgeMap.size();
	}
	
	public int numNodes()
	{
		return this.nodeMap.size();
	}
	
	public DoubleHashNetwork subNetworkExclusive(Set<String> nodes)
	{
		
		DoubleHashNetwork subnet = new DoubleHashNetwork(this.selfOk, this.directed, this.numEdges());
		
		for (SDEdge i : this)
			if (nodes.contains(i.getI1()) && nodes.contains(i.getI2())) subnet.add(i);
	
		return subnet;
	}
	
	public double edgeValue(String n1, String n2)
	{
		return edgeValue(new UndirectedSEdge(n1,n2));
	}
	
	public double edgeValue(SEdge i)
	{
		SDEdge f = edgeMap.get(i);
		if (f==null) return Double.NaN;
		else return f.value();
	}
	
	public boolean contains(String n1, String n2)
	{
		return edgeMap.containsKey(new UndirectedSEdge(n1,n2));
	}
	
	public TypedLinkNetwork<String,Double> asTypedLinkNetwork()
	{
		TypedLinkNetwork<String,Double> out = new TypedLinkNetwork<String,Double>(this.selfOk,this.directed);
		
		for (String node : this.nodeMap.keySet())
			out.addNode(node);
				
		for (SDEdge i : this)
			out.addEdgeWNodeUpdate(i.getI1(), i.getI2(), this.edgeValue(i));
		
		return out;
	}
	
	public DoubleHashNetwork shuffleNodes()
	{
		StringVector nodes = new StringVector(this.getNodes());
		StringVector rnodes = nodes.sample(nodes.size(), false);
		Map<String,String> rnodeMap = new HashMap<String,String>(rnodes.size(),1); 
		
		for (int i=0;i<rnodes.size();i++)
			rnodeMap.put(nodes.get(i), rnodes.get(i));
		
		DoubleHashNetwork out = new DoubleHashNetwork(this.selfOk,this.directed,this.numEdges());
		for (SDEdge i : this)
			out.add(i);
		
		return out;
	}
	
	public IIterator<String> nodeIterator()
	{
		return new IIterator<String>(this.nodeMap.keySet().iterator());
	}
	
	public IIterator<SDEdge> edgeIterator()
	{
		return new IIterator<SDEdge>(this.iterator());
	}
	
	public void save(String file)
	{
		BufferedWriter bw = FileUtil.getBufferedWriter(file, false);
		
		try
		{
			for (SDEdge e : this)
				bw.write(e.getI1()+"\t"+e.getI2()+"\t"+e.value()+"\n");
			
			bw.close();
		}catch (IOException e) {System.out.println(e.getMessage()); e.printStackTrace();}
	}

	public boolean contains(SEdge e)
	{
		return this.contains(e.getI1(), e.getI2());
	}

	@Override
	public Set<? extends SEdge> getEdges()
	{
		return new HashSet<SDEdge>(edgeMap.values());
	}
	
	/**
	 * Merges a collection of DoubleHashNetworks, combining only those interaction which are present in all networks.
	 * The merge object is used to define the function for combining edge scores. 
	 * @param nets
	 * @param merge
	 */
	public static DoubleHashNetwork mergeNetworksExclusive(Collection<DoubleHashNetwork> nets, Mergers merge)
	{
		DoubleHashNetwork net0 = nets.iterator().next();
		DoubleHashNetwork out = new DoubleHashNetwork(net0.selfOk, net0.directed,100);
		
		for (SEdge e : net0)
		{
			DoubleVector vals = new DoubleVector(nets.size());
			
			boolean ok = true;
			for (DoubleHashNetwork net : nets)
			{
				double eval = net.edgeValue(e);
				if (Double.isNaN(eval))
				{
					ok = false;
					break;
				}else vals.add(eval);
			}
			
			if (ok)
			{
				if (out.directed) out.add(new DirectedSDEdge(e.getI1(),e.getI2(),merge.merge(vals.getData())));
				else out.add(new UndirectedSDEdge(e.getI1(),e.getI2(),merge.merge(vals.getData())));
			}
		}
		
		return out;
	}
}
