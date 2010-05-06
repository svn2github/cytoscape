package org.idekerlab.PanGIAPlugin.networks.matrixNetworks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.idekerlab.PanGIAPlugin.utilities.ByteConversion;
import org.idekerlab.PanGIAPlugin.utilities.IIterator;
import org.idekerlab.PanGIAPlugin.utilities.collections.SetUtil;

import org.idekerlab.PanGIAPlugin.networks.*;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.*;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.networks.util.Mergers;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.data.IntVector;
import org.idekerlab.PanGIAPlugin.data.StringTable;
import org.idekerlab.PanGIAPlugin.data.StringVector;


public class FloatMatrixNetwork extends SFNetwork
{
	private Map<String,Integer> nodeLookup;
	private List<String> nodeValues;
	private float[][] connectivity;
		
	public FloatMatrixNetwork(boolean selfOk, boolean directed)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>();
		this.connectivity = new float[0][0];
	}
	
	public FloatMatrixNetwork(boolean selfOk, boolean directed, Collection<String> nodeValues)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>(nodeValues);
		Initialize(nodeValues.size());
		InitializeMap();
	}
	
	public FloatMatrixNetwork(boolean selfOk, boolean directed, Collection<String> nodeValues, float[][] data)
	{
		super(selfOk,directed);
		this.nodeValues = new ArrayList<String>(nodeValues);
		Initialize(nodeValues.size());
		SetData(data);
	}
	
	public FloatMatrixNetwork(String filename, boolean selfOk, boolean directed, int col1, int col2, int valCol)
	{
		super(selfOk,directed);
		
		Load(new StringTable(filename,false,false), selfOk, directed, col1, col2, valCol);
	}
	
	public FloatMatrixNetwork(FloatMatrixNetwork net)
	{
		super(net);
		this.nodeLookup = new HashMap<String,Integer>(net.nodeLookup);
		this.nodeValues = new ArrayList<String>(net.nodeValues);
		
		Initialize(nodeValues.size());
		
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = net.connectivity[i][j];
		
	}
	
	public FloatMatrixNetwork(DoubleMatrixNetwork net)
	{
		super(net);
		this.nodeLookup = new HashMap<String,Integer>(net.getNodeLookupData());
		this.nodeValues = new ArrayList<String>(net.getNodeListData());
		
		Initialize(nodeValues.size());
		
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = (float)net.edgeValue(i,j);
		
	}
	
	public FloatMatrixNetwork(SNetwork net)
	{
		super(net);
		
		this.nodeValues = new ArrayList<String>(net.getNodes());
		Initialize(nodeValues.size());
		InitializeMap();
		
		if (net instanceof SFNetwork)
			for (SFEdge e : ((SFNetwork)net).edgeIterator())
				this.set(e);
		else if (net instanceof SDNetwork) 
			for (SDEdge e : ((SDNetwork)net).edgeIterator())
				this.set(e.getI1(),e.getI2(),(float)e.value());
		else for (SEdge e : net.edgeIterator())
			this.set(e.getI1(),e.getI2(),1);
		
	}
	
	public Set<String> getNodes()
	{
		return new HashSet<String>(nodeValues);
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
	
	public float[][] asFloatMatrix()
	{
		float[][] out = new float[nodeValues.size()][nodeValues.size()];
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				out[i][j] = this.connectivity[i][j];
		
		if (!this.directed)
			for (int i=0;i<this.connectivity.length;i++)
				for (int j=0;j<this.connectivity[i].length;j++)
					out[j][i] = this.connectivity[i][j];
		
		if (!this.selfOk)
			for (int i=0;i<this.connectivity.length;i++)
				out[i][i] = Float.NaN;
		
		return out;
	}
	
	public boolean contains(int i,int j)
	{
		if (j>i)
		{
			int temp = j;
			j = i;
			i = temp;
		}
		
		if (selfOk || i!=j)	return !Float.isNaN(connectivity[i][j]);
		else return false;
	}
	
	public boolean contains(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		if (i1==null || i2==null) return false;
		
		return contains(i1,i2);
	}
	
	public int indexOf(String value)
	{
		Integer i = nodeLookup.get(value); 
		if (i==null) return -1;
		else return i;
	}
	
	public void set(SFEdge e)
	{
		this.set(e.getI1(), e.getI2(), e.value());
	}
	
	public void set(String n1, String n2, float value)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		this.set(i1,i2,value);
	}
	
	public void set(int n1, int n2, float value)
	{
		if (!directed && n2>n1)
		{
			int temp = n1;
			n1 = n2;
			n2 = temp;
		}
		
		if (selfOk || n1!=n2) this.connectivity[n1][n2] = value;
	}
	
	public void set(UndirectedSEdge inter, float value)
	{
		this.set(inter.getI1(), inter.getI2(), value);
	}
	
	public void SetData(float[][] data)
	{
		for (int i=0;i<connectivity.length;i++)
			for (int j=0;j<connectivity[i].length;j++)
				connectivity[i][j] = data[i][j];
	}
	
	public String getNodeValue(int i)
	{
		return this.nodeValues.get(i);
	}
	
	public float edgeValue(int i, int j)
	{
		if (j>i)
		{
			int temp = j;
			j = i;
			i = temp;
		}
		
		if (selfOk || i!=j)	return connectivity[i][j];
		else return Float.NaN;
	}
	
	public float edgeValue(String n1, String n2)
	{
		Integer i1 = nodeLookup.get(n1);
		Integer i2 = nodeLookup.get(n2);
		
		if (i1==null || i2==null) return Float.NaN;
		
		return edgeValue(i1,i2);
	}
	
	private void Initialize(int size)
	{
		if (directed)
		{
			connectivity = new float[size][size];
			
			for (int i=0;i<size;i++)
				for (int j=0;j<size;j++)
					connectivity[i][j] = Float.NaN;
		}
		else
		{
			connectivity = new float[size][];
			
			for (int i=0;i<size;i++)
			{
				if (selfOk) connectivity[i] = new float[i+1];
				else connectivity[i] = new float[i];
				
				for (int j=0;j<connectivity[i].length;j++)
					connectivity[i][j] = Float.NaN;
			}
		}
	}
	
	private void InitializeMap()
	{
		this.nodeLookup = new HashMap<String,Integer>(nodeValues.size());
		for (int i=0;i<nodeValues.size();i++)
			nodeLookup.put(nodeValues.get(i), i);
	}
	
	protected void Load(String file, int col1, int col2, int valCol)
	{
		this.Load(new StringTable(file,false,false), this.selfOk, this.directed, col1, col2, valCol);
	}
	
	private void Load(StringTable data, boolean selfOk, boolean directed, int col1, int col2, int valCol)
	{
		
		Set<String> nodes = new HashSet<String>();
		
		for (int i=0;i<data.dim(0);i++)
		{
			nodes.add(data.get(i, col1));
			nodes.add(data.get(i, col2));
		}
		
		this.nodeValues = new ArrayList<String>(nodes);
		
		Initialize(this.nodeValues.size());
		InitializeMap();
		
		if (directed)
		{
			for (int i=0;i<data.dim(0);i++)
				connectivity[nodeLookup.get(data.get(i, col1))][nodeLookup.get(data.get(i, col2))] = Float.valueOf(data.get(i, valCol));
			
			if (!selfOk)
				for (int i=0;i<connectivity.length;i++)
					connectivity[i][i] = Float.NaN;
				
		}else
		{	
			for (int i=0;i<data.dim(0);i++)
			{
				int i1 = nodeLookup.get(data.get(i, col1));
				int i2 = nodeLookup.get(data.get(i, col2));
				
				if (selfOk || i1!=i2) this.set(i1,i2,Float.valueOf(data.get(i, valCol)));
			}
		}
	}

	public static int intersectSize(FloatMatrixNetwork net1, FloatMatrixNetwork net2)
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
				if (!Float.isNaN(connectivity[i][j])) count++;
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
				
				if (!Float.isNaN(this.connectivity[i][j]))
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
	public void setAsSample(FloatMatrixNetwork reference, int sampleSize)
	{
		int referenceSize = reference.numEdges();
		
		IntVector RI = IntVector.getScale(0, referenceSize-1, 1);
		
		IntVector keepI = RI.sample(sampleSize, false);
		
		this.clear();
		
		int count = 0;
		for (int i=0;i<reference.connectivity.length;i++)
			for (int j=0;j<reference.connectivity[i].length;j++)
			{
				if (!Float.isNaN(reference.connectivity[i][j]))
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
				connectivity[i][j] = Float.NaN;
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
	
	/***
	 * Adds 1 to the value of a given interaction. If the interaction does not exist, it sets that value to 1.
	 */
	public void addOne(UndirectedSEdge inter)
	{
		this.addOne(inter.getI1(), inter.getI2());
	}
	
	public StringTable asStringTable()
	{
		StringTable out = new StringTable(this.numEdges(),3);
		
		int row = 0;
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
			{
				float score = this.edgeValue(i, j);
				if (!Float.isNaN(score))
				{
					out.add(row, this.nodeValues.get(i));
					out.add(row, this.nodeValues.get(j));
					out.add(row, score);
					row++;
				}
			}
		
		return out;
	}
	
	public FloatMatrixNetwork subNetwork(Set<String> nodes)
	{
		FloatMatrixNetwork subNet = new FloatMatrixNetwork(selfOk,directed,nodes);
		
		Set<String> nCopy = new HashSet<String>(nodes);
		nCopy.retainAll(this.nodeValues);
		
		for (String n1 : nCopy)
			for (String n2 : nodes)
			{
				subNet.set(n1, n2, this.edgeValue(n1, n2));
			}
		
		return subNet;
	}
	
	public void setNodeValue(String oldVal, String newVal)
	{
		this.nodeValues.set(this.nodeLookup.get(oldVal),newVal);
	}
	
	public TypedLinkNetwork<String,Float> asTypedLinkNetwork()
	{
		TypedLinkNetwork<String,Float> net = new TypedLinkNetwork<String,Float>(this.nodeValues,false,this.directed);
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
			{
				float score = this.edgeValue(i, j);
				if (!Float.isNaN(score)) 
					net.addEdgeWNodeUpdate(this.getNodeValue(i), this.getNodeValue(j), score);
			}
		
		return net;
	}
	
	public FloatMatrixNetwork shuffleNodes()
	{
		StringVector nodes = new StringVector(this.nodeValues);
		StringVector rnodes = nodes.sample(nodes.size(), false);
		Map<String,String> rnodeMap = new HashMap<String,String>(rnodes.size(),1); 
		
		for (int i=0;i<rnodes.size();i++)
			rnodeMap.put(nodes.get(i), rnodes.get(i));
		
		FloatMatrixNetwork out = new FloatMatrixNetwork(this.selfOk,this.directed,rnodes.asStringList());
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				out.set(i, j, this.connectivity[i][j]);
		
		return out;
	}
	
	public IIterator<String> nodeIterator()
	{
		return new IIterator<String>(this.nodeValues.iterator());
	}

	public IIterator<SFEdge> edgeIterator()
	{
		throw new java.lang.UnsupportedOperationException("Matrix networks do not currently support edge iteration.");
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
	
	public static FloatMatrixNetwork loadFromByte(String file)
	{
		FloatMatrixNetwork net = null;
		
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
			
			net = new FloatMatrixNetwork(selfOk,directed,nodelist);
			
			for (int i=0;i<net.connectivity.length;i++)
				for (int j=0;j<net.connectivity[i].length;j++)
				{
					bos.read(f4);
					net.connectivity[i][j] = ByteConversion.toFloat(f4);
				}
			
			bos.close();
			
					
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return net;
		
	}
	
	public float minVal()
	{
		float min = Float.POSITIVE_INFINITY;
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (connectivity[i][j]<min) min = connectivity[i][j];
		
		return min;
	}
	
	public float maxVal()
	{
		float max = Float.NEGATIVE_INFINITY;
		
		for (int i=0;i<this.connectivity.length;i++)
			for (int j=0;j<this.connectivity[i].length;j++)
				if (connectivity[i][j]>max) max = connectivity[i][j];
		
		return max;
	}
	
	public float[][] getData()
	{
		return connectivity;
	}
	
	
	public Set<SEdge> getEdges()
	{
		throw new java.lang.UnsupportedOperationException("Matrix networks do not currently support getEdges().");
	}
	
	public FloatMatrixNetwork subNetworkExclusive(Set<String> nodes)
	{
		Set<String> newNodes = SetUtil.intersect(nodes, this.nodeValues);
		
		FloatMatrixNetwork out = new FloatMatrixNetwork(this.selfOk,this.directed,newNodes);
		
		for (int i=0;i<connectivity.length;i++)
			if (newNodes.contains(this.nodeValues.get(i)))
					for (int j=0;j<connectivity[i].length;j++)
						if (newNodes.contains(this.nodeValues.get(j)) && this.contains(i, j)) out.set(this.getNodeValue(i),this.getNodeValue(j),connectivity[i][j]);
		
		return out;
				
	}
	
	public boolean contains(SEdge e)
	{
		return contains(e.getI1(),e.getI2());
	}
}


