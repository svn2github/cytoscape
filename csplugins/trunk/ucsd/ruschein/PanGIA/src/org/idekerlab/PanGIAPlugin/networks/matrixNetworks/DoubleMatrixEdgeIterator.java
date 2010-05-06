package org.idekerlab.PanGIAPlugin.networks.matrixNetworks;

import java.util.*;

import org.idekerlab.PanGIAPlugin.networks.DirectedSDEdge;
import org.idekerlab.PanGIAPlugin.networks.SDEdge;
import org.idekerlab.PanGIAPlugin.networks.UndirectedSDEdge;

public class DoubleMatrixEdgeIterator implements Iterator<SDEdge>
{
	private final List<String> nodeValues;
	private final double[][] connectivity;
	private final boolean directed;
	private int nexti;
	private int nextj;
	
	private int lasti;
	private int lastj;
	
	
	public DoubleMatrixEdgeIterator(DoubleMatrixNetwork mnet)
	{
		this.nodeValues = mnet.getNodeListData();
		this.connectivity = mnet.getConnectivityMatrix();
		this.directed = mnet.isDirected();
		
		lasti=-1;
		lastj=-1;
		
		nexti=0;
		nextj=0;
		
		getNextIJ();
	}
	
	public boolean hasNext()
	{
		return nexti!=-1;
	}
	
	public SDEdge next()
	{
		if (nexti==-1) throw new java.util.NoSuchElementException();
		
		SDEdge out = (directed) ? new DirectedSDEdge(nodeValues.get(nexti),nodeValues.get(nextj),connectivity[nexti][nextj]) : new UndirectedSDEdge(nodeValues.get(nexti),nodeValues.get(nextj),connectivity[nexti][nextj]);
		
		getNextIJ();
		
		return out;
	}
	
	public void remove()
	{
		if (nexti==-1) throw new java.lang.IllegalStateException();
		
		connectivity[lasti][lastj] = Double.NaN;
	}
	
	private void getNextIJ()
	{
		lasti = nexti;
		lastj = nextj;
		
		for (int i=nexti;i<connectivity.length;i++)
			for (int j=nextj;j<connectivity[i].length;j++)
				if (!Double.isNaN(connectivity[i][j]))
				{	
					nexti = i;
					nextj = j;
					return;
				}
		
		nexti = -1;
		nextj = -1;
	}
}
