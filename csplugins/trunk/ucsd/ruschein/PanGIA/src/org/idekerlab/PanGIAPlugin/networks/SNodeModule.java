package org.idekerlab.PanGIAPlugin.networks;

import java.io.*;
import java.util.*;

import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.networks.matrixNetworks.*;
import org.idekerlab.PanGIAPlugin.networks.SEdge;
import org.idekerlab.PanGIAPlugin.networks.UndirectedSEdge;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.*;

public class SNodeModule implements java.lang.Iterable<String>, Comparable<SNodeModule>{

	private String id;
	private Set<String> members = new HashSet<String>();
	
	public SNodeModule()
	{
		
	}
	
	public SNodeModule(SNodeModule c)
	{
		this.id = c.id;
		this.members = new HashSet<String>(c.members);
	}
	
	public SNodeModule(String name)
	{
		this.id = name;
	}
	
	public SNodeModule(String name, Collection<String> members)
	{
		this.id = name;
		this.members = new HashSet<String>(members);
	}
	
	public void setID(String id)
	{
		this.id = id;
	}
	
	public SNodeModule clone()
	{
		return new SNodeModule(this.id, new HashSet<String>(this.members));
	}
	
	public boolean equals(Object c)
	{
		if (c == null) return false;
		if (c instanceof SNodeModule)
		{
			SNodeModule other = (SNodeModule)c;
			if (other.id.equals(this.id) && other.members.equals(this.members)) return true;
			else return false;
		}else return false;
	}
	
	public int hashCode()
	{
		return members.hashCode();
	}
	
	public void addMember(String member)
	{
		members.add(member);
	}
	
	public void retainAll(Set<String> members)
	{
		this.members.retainAll(members);
	}
	
	public void setMembers(Collection<String> members)
	{
		this.members = new HashSet<String>(members);
	}
	
	public Set<String> getMembers()
	{
		return new HashSet<String>(members);
	}
	
	public Set<String> getMemberData()
	{
		return members;
	}
	
	public String getID()
	{
		return id;
	}
	
	public String toString()
	{
		return id;
	}
	
	public Iterator<String> iterator()
	{
		return members.iterator();
	}
	
	public int size()
	{
		return members.size();
	}
	
	public boolean contains(SNodeModule complex)
	{
		for (String member : complex.members)
			if (!this.members.contains(member)) return false;
		
		return true;
	}
	
	public void add(String g)
	{
		this.members.add(g);
	}
	
	public void add(SNodeModule c)
	{
		for (String s : c.members)
			this.add(s);
	}
	
	public int compareTo(SNodeModule other)
	{
		return this.members.size()-other.members.size();
	}
	
	public static Map<String,SNodeModule> loadComplexMap(String filename)
	{
		List<SNodeModule> listComplexes = loadComplexes(filename);
		Map<String,SNodeModule> name2complex = new HashMap<String,SNodeModule>();
		for(SNodeModule eachComplex : listComplexes)
			name2complex.put(eachComplex.getID(), eachComplex);
		
		return name2complex;
	}
	
	public static List<SNodeModule> loadComplexes(String filename)
	{
		return loadComplexes(filename,"|");
	}
	
	public static List<SNodeModule> loadComplexes(String filename, String delimiter)
	{
		if (delimiter.equals("|")) delimiter = "\\|";
		
		ArrayList<SNodeModule> complexes = new ArrayList<SNodeModule>();
		
		//Create a filereader and open the file
		FileReader fr = null;
		try
		{
			fr = new FileReader(filename);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File: "+filename);
			System.exit(0);
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		String line = "";
		do 
		{
			try
			{
				line = br.readLine();
			}catch (IOException e)
			{
				System.out.println(e.getMessage());
				System.exit(0);
			}
			
			if (line!=null)
			{
				String[] cols = line.split("\t");
				
				if (cols.length>1)
				{
					String[] genes = cols[1].split(delimiter);
					
					SNodeModule newcomplex = new SNodeModule(cols[0]);
					
					for (int i=0;i<genes.length;i++)
						newcomplex.addMember(genes[i]);
					
					complexes.add(newcomplex);
				}
			}
		}while (line!=null);
		
		try {br.close();}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		return complexes;
	}
	
	public static void saveComplexes(String filename, Collection<SNodeModule> cc)
	{
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filename);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}catch (IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		try
		{
			for (SNodeModule c : cc)
			{
				bw.write(c.getID()+"\t");
				Iterator<String> si = c.getMembers().iterator();
				while (si.hasNext())
				{
					bw.write(si.next());
					if (si.hasNext()) bw.write("|");
				}
				bw.write("\n");
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		
		try {bw.close();}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
	
	public static SBNetwork getBetween(SNodeModule c1, SNodeModule c2, SNetwork net)
	{
		SBNetwork between = new BooleanHashNetwork(false,false);
		SBNetwork possibleBetween = getBetweenExclusive(c1,c2);
		
	
		for (SEdge eachPossibleBetween : possibleBetween.edgeIterator())
		{
			if (net.contains(eachPossibleBetween))
			{
				SEdge newOne = new UndirectedSEdge(eachPossibleBetween);
				between.add(newOne);
			}
		}
		
		return between;
	}
	
	public static SBNetwork getBetweenExclusive(SNodeModule c1, SNodeModule c2)
	{
		SBNetwork net = new BooleanHashNetwork(false,false);
		
		Set<String> c1s = c1.getMembers();
		c1s.removeAll(c2.getMembers());
		
		Set<String> c2s = c2.getMembers();
		c2s.removeAll(c1.getMembers());
		
		for(String cm1 : c1s)
			for (String cm2 : c2s)
				net.add(new UndirectedSEdge(cm1,cm2));
		
		return net;
	}
	
	public static int getBetweenExclusiveCount(SNodeModule c1, SNodeModule c2)
	{
		Set<String> c1s = c1.getMembers();
		c1s.removeAll(c2.getMembers());
		
		Set<String> c2s = c2.getMembers();
		c2s.removeAll(c1.getMembers());
		
		return c1s.size()*c2s.size();
	}
	
	
	
	public static SBNetwork getWithin(SNodeModule c1, SNetwork net)
	{
		SBNetwork within = new BooleanHashNetwork(false,false);
		for (String node1 : c1)
			for(String node2 : c1)
			{
				if (node1.equals(node2)) continue;
				
				SEdge pos = new UndirectedSEdge(node1,node2);
				if (net.contains(pos)) within.add(pos);
			}
		
		return within;
	}
	
	public static int getWithinCount(SNodeModule c1, boolean[][] connectivityTriangle, List<String> nodes)
	{
		int score = 0;
		
		for (String node1 : c1)
			for(String node2 : c1)
			{	
				int i1 = nodes.indexOf(node1);
				int i2 = nodes.indexOf(node2);
				
				if (i1==-1 || i2==-1 || i1==i2) continue;
				
				if (i2>i1)
				{
					int temp = i1;
					i1 = i2;
					i2 = temp;
				}
				
				if (connectivityTriangle[i1][i2]) score++;
			}
		
		return score;
	}
	
	public static double[] getWithinValues(SNodeModule c1, double[][] connectivityTriangle, List<String> nodes)
	{
		DoubleVector vals = new DoubleVector(c1.size()*(c1.size()-1)/2);
		
		for (String node1 : c1)
			for(String node2 : c1)
			{	
				int i1 = nodes.indexOf(node1);
				int i2 = nodes.indexOf(node2);
				
				if (i1==-1 || i2==-1 || i1==i2) continue;
				
				if (i2>i1)
				{
					int temp = i1;
					i1 = i2;
					i2 = temp;
				}
				
				if (!Double.isNaN(connectivityTriangle[i1][i2])) vals.add(connectivityTriangle[i1][i2]);
			}
		
		return vals.getData();
	}
	
	public static double[] getBetweenValues(SNodeModule c1, SNodeModule c2, double[][] connectivityTriangle, List<String> nodes)
	{
		Set<String> c1s = c1.getMembers();
		c1s.removeAll(c2.getMemberData());
		
		Set<String> c2s = c2.getMembers();
		c2s.removeAll(c1s);
		
		DoubleVector vals = new DoubleVector(c1.size()*c2.size());
		
		for (String node1 : c1)
			for(String node2 : c2)
			{	
				int i1 = nodes.indexOf(node1);
				int i2 = nodes.indexOf(node2);
				
				if (i1==-1 || i2==-1 || i1==i2) continue;
				
				if (i2>i1)
				{
					int temp = i1;
					i1 = i2;
					i2 = temp;
				}
				
				if (!Double.isNaN(connectivityTriangle[i1][i2])) vals.add(connectivityTriangle[i1][i2]);
			}
		
		return vals.getData();
	}
	
	/**
	 * Creates a network object which is a complete graph between all complex members
	 * @return Network that is a complex graph between all complex members
	 */
	public SBNetwork asNetwork()
	{
		SBNetwork net = new BooleanHashNetwork(false,false);
		
		for (String s1 : members)
			for (String s2 : members)
			{
				if (s1.equals(s2)) continue;
				net.add(new UndirectedSEdge(s1,s2));
			}
		
		return net;
	}
	
	public static SNodeModule union(SNodeModule c1, SNodeModule c2)
	{
		SNodeModule out = new SNodeModule(c1);
		out.add(c2);
		return out;
	}
	
	public static SNodeModule intersection(SNodeModule c1, SNodeModule c2)
	{
		SNodeModule out = new SNodeModule(c1);
		out.members.retainAll(c2.members);
		return out;
	}
	
	public static double jaccard(SNodeModule c1, SNodeModule c2)
	{
		int intersection = SNodeModule.intersection(c1,c2).size();
		int union = SNodeModule.union(c1, c2).size();
		
		return intersection / (double) union;
	}
	
	public BooleanMatrixNetwork asBooleanMatrixNetwork()
	{
		BooleanMatrixNetwork bmn = new BooleanMatrixNetwork(false,false,this.members);
		
		for (String s1 : this.members)
			for (String s2 : this.members)
				if (!s1.equals(s2)) bmn.add(s1,s2);
		
		return bmn;
	}
	
	public static Set<SNodeModule> selectComplexes(Set<SNodeModule> clist, Set<String> ids)
	{
		Set<SNodeModule> clout = new HashSet<SNodeModule>(ids.size());
		
		for (SNodeModule c : clist)
			if (ids.contains(c.getID())) clout.add(c);
		
		return clout;
	}
	
	public static List<SNodeModule> selectComplexes(List<SNodeModule> clist, List<String> ids)
	{
		List<SNodeModule> clout = new ArrayList<SNodeModule>(ids.size());
		
		for (String id : ids)
			for (SNodeModule c : clist)
				if (c.getID().equals(id)) clout.add(c);
		
		return clout;
	}
	
	
	
	public void remove(String member)
	{
		this.members.remove(member);
	}
	
	public boolean contains(String member)
	{
		return members.contains(member);
	}
}
