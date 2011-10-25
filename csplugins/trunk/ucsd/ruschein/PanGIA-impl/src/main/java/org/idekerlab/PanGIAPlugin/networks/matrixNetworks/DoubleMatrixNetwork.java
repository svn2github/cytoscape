package org.idekerlab.PanGIAPlugin.networks.matrixNetworks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.utilities.ByteConversion;
import org.idekerlab.PanGIAPlugin.utilities.IIterator;

import org.idekerlab.PanGIAPlugin.networks.AbstractNetwork;
import org.idekerlab.PanGIAPlugin.networks.DirectedSDEdge;
import org.idekerlab.PanGIAPlugin.networks.SDEdge;
import org.idekerlab.PanGIAPlugin.networks.SDNetwork;
import org.idekerlab.PanGIAPlugin.networks.SEdge;
import org.idekerlab.PanGIAPlugin.networks.SNetwork;
import org.idekerlab.PanGIAPlugin.networks.UndirectedSDEdge;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.DoubleHashNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.networks.util.Mergers;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.data.FloatMatrix;
import org.idekerlab.PanGIAPlugin.data.IntVector;
import org.idekerlab.PanGIAPlugin.data.StringTable;

import org.idekerlab.PanGIAPlugin.utilities.files.*;
import org.idekerlab.PanGIAPlugin.utilities.collections.SetUtil;


public class DoubleMatrixNetwork extends SDNetwork implements Iterable<SDEdge>
{
	private Map<String,Integer> nodeLookup;
	private List<String> nodeValues;
	private double[][] connectivity;
		
	public DoubleMatrixNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>();
		this.connectivity = new double[0][0];
	}
	
	public DoubleMatrixNetwork(boolean selfOk, boolean directed, Collection<String> nodeValues)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>(nodeValues);
		Initialize(nodeValues.size());
		InitializeMap();
	}
	
	public DoubleMatrixNetwork(boolean selfOk, boolean directed, Collection<String> nodeValues, double[][] data)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>(nodeValues);
		Initialize(nodeValues.size());
		SetData(data);
	}
	
	public DoubleMatrixNetwork(String filename, boolean selfOk, boolean directed, int col1, int col2, int valCol)
	{
		super(selfOk,directed);
		Load(filename, selfOk, directed, col1, col2, valCol,false,false);
	}
	
	public DoubleMatrixNetwork(String filename, boolean selfOk, boolean directed, int col1, int col2, int valCol, boolean verbose)
	{
		super(selfOk,directed);
		Load(filename, selfOk, directed, col1, col2, valCol,verbose,false);
	}
	
	public DoubleMatrixNetwork(String filename, boolean selfOk, boolean directed, int col1, int col2, int valCol, boolean verbose, boolean gzip)
	{
		super(selfOk,directed);
		Load(filename, selfOk, directed, col1, col2, valCol,verbose,gzip);
	}
	
	public DoubleMatrixNetwork(DoubleMatrixNetwork net)
	{
		super(net);
		
		this.nodeLookup = new HashMap<String,Integer>(net.nodeLookup);
		this.nodeValues = new ArrayList<String>(net.nodeValues);
				
		Initialize(nodeValues.size());
		
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = net.connectivity[i][j];
		
	}
	
	public DoubleMatrixNetwork(DoubleHashNetwork net)
	{
		super(net);
		
		this.nodeValues = new ArrayList<String>(net.getNodes());
		
		Initialize(nodeValues.size());
		InitializeMap();
		
		for (SDEdge e : net)
			this.set(e);
		
	}
	
	public double[][] getConnectivityMatrix()
	{
		return connectivity;
	}
	
	/**
	 * Returns the actual reference. Do not modify.
	 */
	public Map<String,Integer> getNodeLookupData()
	{
		return this.nodeLookup;
	}
	
	public Set<SDEdge> getEdges()
	{
		Set<SDEdge> edges = new HashSet<SDEdge>(this.numEdges());
		
		for (SDEdge e : this)
			edges.add(e);
		
		return edges;
	}
	
	public Set<String> getNodes()
	{
		return new HashSet<String>(this.nodeValues);
	}
	
	public List<String> getNodeList()
	{
		return new ArrayList<String>(this.nodeValues);
	}
	
	/**
	 * Returns the actual reference to the nodelist data. Use this carefully and only when speed is needed.
	 */
	public List<String> getNodeListData()
	{
		return this.nodeValues;
	}
	
	public IIterator<String> nodeIterator()
	{
		return new IIterator<String>(this.nodeValues.iterator());
	}
	
	public IIterator<SDEdge> edgeIterator()
	{
		return new IIterator<SDEdge>(this.iterator());
	}
	
	public Iterator<SDEdge> iterator()
	{
		return new DoubleMatrixEdgeIterator(this);
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
		
		if (selfOk || i!=j)	return !Double.isNaN(connectivity[i][j]);
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
		Integer i1 = nodeLookup.get(e.getI1());
		Integer i2 = nodeLookup.get(e.getI2());
		
		if (i1==null || i2==null) return false;
		
		return contains(i1,i2);
	}
	
	public int indexOf(String value)
	{
		Integer i = nodeLookup.get(value); 
		if (i==null) return -1;
		else return i;
	}
	
	public void set(String n1, String n2, double value)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		this.set(i1,i2,value);
	}
	
	public void set(SDEdge e)
	{
		Integer i1 = nodeLookup.get(e.getI1());
		Integer i2 = nodeLookup.get(e.getI2());
		
		this.set(i1,i2,e.value());
	}
	
	public void set(int n1, int n2, double value)
	{
		if (!directed && n2>n1)
		{
			int temp = n1;
			n1 = n2;
			n2 = temp;
		}
		
		if (selfOk || n1!=n2) this.connectivity[n1][n2] = value;
	}
	
	public void SetData(double[][] data)
	{
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = data[i][j];
	}
	
	public String getNodeValue(int i)
	{
		return this.nodeValues.get(i);
	}
	
	public double edgeValue(int i, int j)
	{
		if (j>i)
		{
			int temp = j;
			j = i;
			i = temp;
		}
		
		if (selfOk || i!=j)	return connectivity[i][j];
		else return Double.NaN;
	}
	
	public double edgeValue(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		if (i1==null || i2==null) return Double.NaN;
		
		return edgeValue(i1,i2);
	}
	
	public double edgeValue(SEdge e)
	{
		Integer i1 = nodeLookup.get(e.getI1());
		Integer i2 = nodeLookup.get(e.getI2());
		
		if (i1==null || i2==null) return Double.NaN;
		
		return edgeValue(i1,i2);
	}
	
	private void Initialize(int size)
	{
		if (directed)
		{
			connectivity = new double[size][size];
			
			for (int i=0;i<size;i++)
				for (int j=0;j<size;j++)
					connectivity[i][j] = Double.NaN;
		}
		else
		{
			connectivity = new double[size][];
			
			for (int i=0;i<size;i++)
			{
				if (selfOk) connectivity[i] = new double[i+1];
				else connectivity[i] = new double[i];
				
				for (int j=0;j<connectivity[i].length;j++)
					connectivity[i][j] = Double.NaN;
			}
		}
	}
	
	private void InitializeMap()
	{
		this.nodeLookup = new HashMap<String,Integer>(nodeValues.size());
		for (int i=0;i<nodeValues.size();i++)
			nodeLookup.put(nodeValues.get(i), i);
	}
	
	private void Load(String file, boolean selfOk, boolean directed, int col1, int col2, int valCol, boolean verbose, boolean gzip)
	{
		Set<String> nodes = new HashSet<String>();
		
		int count = 0;
		FileIterator fi = (gzip) ? new FileIterator(FileUtil.getGZBufferedReader(file)) : new FileIterator(file);
		for (String line : fi)
		{
			if (verbose && count%10000000==0) System.out.println("Loading nodes: line="+count);
			
			String[] cols = line.split("\t");
						
			nodes.add(cols[col1]);
			nodes.add(cols[col2]);
			count++;
		}
		
		this.nodeValues = new ArrayList<String>(nodes);
		
		Initialize(this.nodeValues.size());
		InitializeMap();
		
		if (directed)
		{
			count = 0;
			fi = (gzip) ? new FileIterator(FileUtil.getGZBufferedReader(file)) : new FileIterator(file);
			for (String line : fi)
			{
				if (verbose && count%10000000==0) System.out.println("Loading edges: line="+count);
				
				String[] cols = line.split("\t");
				connectivity[nodeLookup.get(cols[col1])][nodeLookup.get(cols[col2])] = Double.valueOf(cols[valCol]);
				
				count++;
			}
			
			if (!selfOk)
				for (int i=0;i<connectivity.length;i++)
					connectivity[i][i] = Double.NaN;
				
		}else
		{	
			count = 0;
			fi = (gzip) ? new FileIterator(FileUtil.getGZBufferedReader(file)) : new FileIterator(file);
			for (String line : fi)
			{
				if (verbose && count%10000000==0) System.out.println("Loading edges: line="+count);
				
				String[] cols = line.split("\t");
				
				int i1 = nodeLookup.get(cols[col1]);
				int i2 = nodeLookup.get(cols[col2]);
				
				if (selfOk || i1!=i2) this.set(i1,i2,Double.valueOf(cols[valCol]));
				
				count++;
			}
		}
	}

	public static int intersectSize(DoubleMatrixNetwork net1, DoubleMatrixNetwork net2)
	{
		int count = 0;
		
		for (int i=0;i<net1.connectivity.length;i++)
			for (int j=0;j<net1.connectivity[i].length;j++)
				if (net1.contains(i, j) && net2.contains(net1.nodeValues.get(i), net1.nodeValues.get(j)))
					count++;
		
		return count;
	}
	
	public int numEdges()
	{
		int count = 0;
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				if (!Double.isNaN(connectivity[i][j])) count++;
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
				
				if (!Double.isNaN(this.connectivity[i][j]))
				{
					try 
					{
						bw.write(nodeI+"\t"+this.getNodeValue(j)+"\t"+this.connectivity[i][j]+"\n");
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
	public void setAsSample(DoubleMatrixNetwork reference, int sampleSize)
	{
		int referenceSize = reference.numEdges();
		
		IntVector RI = IntVector.getScale(0, referenceSize-1, 1);
		
		IntVector keepI = RI.sample(sampleSize, false);
		
		this.clear();
		
		int count = 0;
		for (int i=0;i<reference.connectivity.length;i++)
			for (int j=0;j<reference.connectivity[i].length;j++)
			{
				if (!Double.isNaN(reference.connectivity[i][j]))
				{
					if (keepI.contains(count)) this.set(reference.getNodeValue(i), reference.getNodeValue(j),reference.connectivity[i][j]);
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
				connectivity[i][j] = Double.NaN;
	}
	
	/***
	 * Adds 1 to the value of a given interaction. If the interaction does not exist, it sets that value to 1.
	 */
	public void addOne(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		if (i1==null || i2==null) return;
		
		if (this.contains(i1, i2)) this.set(i1, i2, this.edgeValue(i1, i2)+1);
		else this.set(i1, i2,1);
	}
	
	public StringTable asStringTable()
	{
		StringTable out = new StringTable(this.numEdges(),3);
		
		int row = 0;
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
			{
				double score = this.edgeValue(i, j);
				if (!Double.isNaN(score))
				{
					out.add(row, this.nodeValues.get(i));
					out.add(row, this.nodeValues.get(j));
					out.add(row, score);
					row++;
				}
			}
		
		return out;
	}
	
	public void saveAsByte(String file)
	{
		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file,false));
		
			if (selfOk) bos.write(1);
			else bos.write(0);
			
			if (directed) bos.write(1);
			else bos.write(0);
			
			bos.write( ByteConversion.toByta(this.nodeValues.size()) );
			
			int numBytes = 0;
			for (String n : this.nodeValues)
				numBytes += n.getBytes().length+1;
			
			bos.write( ByteConversion.toByta(numBytes) );
			
			for (String n : this.nodeValues)
			{
				bos.write(n.getBytes());
				bos.write("\t".getBytes());
			}
			
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
						bos.write( ByteConversion.toByta(connectivity[i][j]) );
		
			bos.close();
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
	}
	
	public static DoubleMatrixNetwork loadFromByte(String file)
	{
		DoubleMatrixNetwork net = null;
		
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			boolean selfOk = (bos.read()==0) ? false : true;
			boolean directed = (bos.read()==0) ? false : true;
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numNodes = ByteConversion.toInt(f4);
			bos.read(f4);
			int numBytes = ByteConversion.toInt(f4);
			
			List<String> nodelist = new ArrayList<String>(numNodes);
			
			byte[] stringBytes = new byte[numBytes];
			bos.read(stringBytes);
			String[] nodeString = new String(stringBytes).split("\t");
			
			for (String n : nodeString)
				nodelist.add(n);
			
			net = new DoubleMatrixNetwork(selfOk,directed,nodelist);
			
			byte[] f8 = new byte[8];
			for (int i=0;i<net.connectivity.length;i++)
				for (int j=0;j<net.connectivity[i].length;j++)
				{
					bos.read(f8);
					net.connectivity[i][j] = ByteConversion.toDouble(f8);
				}
			
			bos.close();
			
					
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return net;
		
	}
	
	public static List<String> loadNodeListFromByte(String file)
	{
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			bos.read();bos.read();
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numNodes = ByteConversion.toInt(f4);
			bos.read(f4);
			int numBytes = ByteConversion.toInt(f4);
			
			List<String> nodelist = new ArrayList<String>(numNodes);
			
			byte[] stringBytes = new byte[numBytes];
			bos.read(stringBytes);
			String[] nodeString = new String(stringBytes).split("\t");
			
			for (String n : nodeString)
				nodelist.add(n);
			
			return nodelist;
			
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return null;
		
	}
	
	public float[][] asFloatMatrix()
	{
		float[][] out = new float[nodeValues.size()][nodeValues.size()];
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				out[i][j] = (float)this.connectivity[i][j];
		
		if (!this.directed)
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
					out[j][i] = (float)this.connectivity[i][j];
		
		if (!this.selfOk)
			for (int i=0;i<this.connectivity.length;i++)
				out[i][i] = Float.NaN;
		
		return out;
	}
	
	public double[][] asDoubleMatrix()
	{
		double[][] out = new double[nodeValues.size()][nodeValues.size()];
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				out[i][j] = this.connectivity[i][j];
		
		if (!this.directed)
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
					out[j][i] = this.connectivity[i][j];
		
		if (!this.selfOk)
			for (int i=0;i<this.connectivity.length;i++)
				out[i][i] = Double.NaN;
		
		return out;
	}
	
	public int numNodes()
	{
		return this.nodeValues.size();
	}
	
	/**
	 * Merges a collection of DoubleMatrixNetworks, combining only those interaction which are present in all networks.
	 * The merge object is used to define the function for combining edge scores. 
	 * @param nets
	 * @param merge
	 */
	public static DoubleHashNetwork mergeNetworksExclusive(Collection<DoubleMatrixNetwork> nets, Mergers merge)
	{
		DoubleMatrixNetwork net0 = nets.iterator().next();
		DoubleHashNetwork out = new DoubleHashNetwork(net0.selfOk, net0.directed,100);
		
		for (SEdge e : net0)
		{
			DoubleVector vals = new DoubleVector(nets.size());
			
			boolean ok = true;
			for (DoubleMatrixNetwork net : nets)
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
				if (net0.directed) out.add(new DirectedSDEdge(e.getI1(),e.getI2(),merge.merge(vals.getData())));
				else out.add(new UndirectedSDEdge(e.getI1(),e.getI2(),merge.merge(vals.getData())));
			}
		}
		
		return out;
	}
	
	/**
	 * Merges a collection of DoubleMatrixNetworks, assuming the same interactions are present in all networks.
	 * The merge object is used to define the function for combining edge scores. 
	 * @param nets
	 * @param merge
	 */
	public static DoubleMatrixNetwork mergeNetworksIdentical(Collection<DoubleMatrixNetwork> nets, Mergers merge)
	{
		DoubleMatrixNetwork net0 = nets.iterator().next();
		DoubleMatrixNetwork out = new DoubleMatrixNetwork(net0);
		
		double[] vals = new double[nets.size()];
		
		for (int i=0;i<net0.connectivity.length;i++)
		{
			if (i%100==0) System.out.println(i+" / "+net0.connectivity.length);
			for (int j=0;j<net0.connectivity[i].length;j++)
			{
				int n=0;
				for (DoubleMatrixNetwork net : nets)
				{
					vals[n] = net.connectivity[i][j];
					n++;
				}
				
				out.connectivity[i][j] = merge.merge(vals);
			}
		}
		
		return out;
	}
	
	public DoubleMatrixNetwork subNetworkExclusive(Set<String> nodes)
	{
		Set<String> newNodes = SetUtil.intersect(nodes, this.nodeValues);
		
		DoubleMatrixNetwork out = new DoubleMatrixNetwork(this.selfOk,this.directed,newNodes);
		
		for (int i=0;i<connectivity.length;i++)
			if (newNodes.contains(this.nodeValues.get(i)))
					for (int j=0;j<connectivity[i].length;j++)
						if (newNodes.contains(this.nodeValues.get(j)) && this.contains(i, j)) out.set(this.getNodeValue(i),this.getNodeValue(j),connectivity[i][j]);
		
		return out;
	}
	
	public AbstractNetwork shuffleNodes()
	{
		throw new java.lang.UnsupportedOperationException("Operation not yet implemented.");
	}
	
	public TypedLinkNetwork<String,Double> asTypedLinkNetwork()
	{
		throw new java.lang.UnsupportedOperationException("Operation not yet implemented.");
	}
	
	public double minVal()
	{
		double min = Double.POSITIVE_INFINITY;
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (connectivity[i][j]<min) min = connectivity[i][j];
		
		return min;
	}
	
	public double maxVal()
	{
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (connectivity[i][j]>max) max = connectivity[i][j];
		
		return max;
	}
	
	public double minRealVal()
	{
		double min = Double.POSITIVE_INFINITY;
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (!Double.isInfinite(connectivity[i][j]) && connectivity[i][j]<min) min = connectivity[i][j];
		
		return min;
	}
	
	public double maxRealVal()
	{
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (!Double.isInfinite(connectivity[i][j]) && connectivity[i][j]>max) max = connectivity[i][j];
		
		return max;
	}
	
	public double[][] getData()
	{
		return connectivity;
	}
	
	public DoubleHashNetwork thresholdAsHashNetwork(double threshold, boolean greaterThan)
	{
		DoubleHashNetwork out = new DoubleHashNetwork(false,false,100);
		
		if (greaterThan)
		{
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
					if (connectivity[i][j]>threshold) out.add(nodeValues.get(i),nodeValues.get(j),connectivity[i][j]);
		}else
		{
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
					if (connectivity[i][j]<threshold) out.add(nodeValues.get(i),nodeValues.get(j),connectivity[i][j]);
		}
		
		return out;
	}
	
	public void Abs()
	{
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				connectivity[i][j] = Math.abs(connectivity[i][j]);
	}
	
	public DoubleMatrixNetwork reorderNodes(List<String> nodeList)
	{
		int[] indexMap = new int[nodeList.size()];
		
		for (int i=0;i<nodeList.size();i++)
		{
			if (!nodeLookup.containsKey(nodeList.get(i)))
				System.out.println("Network does not contain key: "+nodeList.get(i));
				
			indexMap[i] = nodeLookup.get(nodeList.get(i));
		}
		
		double[][] cout = new double[nodeList.size()][];
		
		for (int i=0;i<nodeList.size();i++)
		{
			if (selfOk) cout[i] = new double[i+1];
			else cout[i] = new double[i];
			
			int a = indexMap[i];
			for (int j=0;j<cout[i].length;j++)
			{
				int b = indexMap[j];
				
				if (b>a)
				{
					int temp = a;
					a = b;
					b = temp;
				}
				
				cout[i][j] = connectivity[a][b];
			}
		}
		
		return new DoubleMatrixNetwork(selfOk,false,nodeList,cout);
	}
}


