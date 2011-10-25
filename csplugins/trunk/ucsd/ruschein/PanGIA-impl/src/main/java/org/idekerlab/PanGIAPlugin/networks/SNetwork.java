package org.idekerlab.PanGIAPlugin.networks;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import org.idekerlab.PanGIAPlugin.utilities.*;
import org.idekerlab.PanGIAPlugin.utilities.files.*;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.*;

public abstract class SNetwork extends AbstractNetwork
{
	public abstract IIterator<String> nodeIterator();
	public abstract IIterator<? extends SEdge> edgeIterator();
	
	public abstract int numEdges();
	public abstract int numNodes();
	
	public abstract Set<String> getNodes();
	public abstract Set<? extends SEdge> getEdges();
	
	public abstract boolean contains(String n1, String n2);
	public abstract boolean contains(SEdge e);
	public abstract SNetwork subNetworkExclusive(Set<String> nodes);
	
	public SNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
	}
	
	public SNetwork(SNetwork net)
	{
		super(net);
	}
	
	public void save(String file)
	{
		BufferedWriter bw = FileUtil.getBufferedWriter(file, false);
		
		try
		{
			for (SEdge e : this.edgeIterator())
				bw.write(e.getI1()+"\t"+e.getI2()+"\n");
			
			bw.close();
		}catch (IOException e) {System.out.println(e.getMessage()); e.printStackTrace();}
	}
	
	public static List<SEdge> loadEdgeList(String file, int col1, int col2)
	{
		List<SEdge> out = new ArrayList<SEdge>(FileUtil.countLines(file));
		
		for (String line : new FileIterator(file))
		{
			String[] cols = line.split("\t");
			out.add(new DirectedSEdge(cols[col1],cols[col2]));
		}
		
		return out;
	}
}
