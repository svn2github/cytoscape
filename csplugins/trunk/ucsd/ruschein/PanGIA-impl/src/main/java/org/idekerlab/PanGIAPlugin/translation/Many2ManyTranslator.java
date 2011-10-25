package org.idekerlab.PanGIAPlugin.translation;

import java.util.*;

import org.idekerlab.PanGIAPlugin.networks.matrixNetworks.BooleanMatrixNetwork;
import org.idekerlab.PanGIAPlugin.networks.*;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.*;
import org.idekerlab.PanGIAPlugin.data.BooleanVector;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.data.StringTable;
import org.idekerlab.PanGIAPlugin.data.StringVector;

import org.idekerlab.PanGIAPlugin.utilities.files.*;
import org.idekerlab.PanGIAPlugin.utilities.collections.HashMapUtil;


/**
 * A class for the general translation of strings.
 * Provides for loading multiple maps and many-2-many mappings.
 * @author ghannum
 */
public class Many2ManyTranslator {
	
	private List<Map<String,Set<String>>> maps = new ArrayList<Map<String,Set<String>>>();
	boolean casesensitive = true;
	
	/**
	 * Empty constructor.
	 */
	public Many2ManyTranslator() {}
	
	public Many2ManyTranslator(Map<String,Set<String>> hm)
	{
		maps = new ArrayList<Map<String,Set<String>>>(1);
		maps.add(hm);
	}
	
	/**
	 * Loads a single map.
	 * @param file      The filepath
	 * @param reversed  Is the map reversed? (columns need to be swapped)
	 */
	public Many2ManyTranslator(String file, boolean reversed)
	{
		maps.add(LoadMap(file, reversed, "|"));
	}
	
	/**
	 * Loads two maps, in priority order.
	 * @param file1     The first filepath
	 * @param file2     The second filepath
	 * @param reversed  Are the maps reversed? (columns need to be swapped)
	 */
	public Many2ManyTranslator(String file1,String file2, boolean reversed)
	{
		maps.add(LoadMap(file1, reversed, "|"));
		maps.add(LoadMap(file2, reversed, "|"));
	}
	
	public Many2ManyTranslator(String file1,String file2, boolean reversed, boolean caseSensitive)
	{
		this.casesensitive = caseSensitive;
		maps.add(LoadMap(file1, reversed, "|"));
		maps.add(LoadMap(file2, reversed, "|"));
	}
	
	public Many2ManyTranslator(String file1,String file2, boolean reversed, boolean caseSensitive, String delimiter)
	{
		this.casesensitive = caseSensitive;
		maps.add(LoadMap(file1, reversed, delimiter));
		maps.add(LoadMap(file2, reversed, delimiter));
	}
	
	public Many2ManyTranslator(String file, boolean reversed, String delimiter)
	{
		maps.add(LoadMap(file, reversed, delimiter));
	}
	
	public Many2ManyTranslator(String file, boolean reversed, boolean casesensitive)
	{
		this.casesensitive = casesensitive;
		maps.add(LoadMap(file, reversed, "|"));
	}
	
	public void addMap(String file, boolean reversed)
	{
		maps.add(LoadMap(file,reversed,"|"));
	}
	
	/**
	 * Loads a map from a file.
	 */
	public Map<String,Set<String>> LoadMap(String file, boolean reversed, String delimiter)
	{
		if (delimiter.equals("|")) delimiter = "\\|"; 
		
		Map<String,Set<String>> hm = new HashMap<String,Set<String>>();
		
		for (String line : new FileIterator(file))
		{
			String[] cols = line.split("\t");
			if (cols.length<2) continue;
			String[] inmap; 
			String[] out;
			
			if (!reversed)
			{
				if (!casesensitive) cols[0]=cols[0].toLowerCase();
				inmap = cols[0].split(delimiter);
				out = cols[1].split(delimiter);
				if(out[0].equals("")) continue;
			}else
			{
				if (!casesensitive) cols[1]=cols[1].toLowerCase();
				inmap = cols[1].split(delimiter);
				out = cols[0].split(delimiter);
				
				if(out[0].equals("")) continue;
			}

			Set<String> outvals = new HashSet<String>(out.length);
			
			for (int a=0;a<out.length;a++)
				outvals.add(out[a]);
			
			for (int a=0;a<inmap.length;a++)
			{
				Set<String> sset = hm.get(inmap[a]);
				
				if (sset==null) hm.put(inmap[a], new HashSet<String>(outvals));
				else sset.addAll(outvals);
			}
		}
		
		return hm;
	}
	

	/**
	 * Provides all translations of a word in the highest priority map which recognizes that word.
	 */
	public Set<String> translate(String word)
	{
		if (!casesensitive) word = word.toLowerCase();
		
		Set<String> out = new HashSet<String>();
		
		for (Map<String,Set<String>> hm : maps)
			if (hm.containsKey(word))
			{
				out = new HashSet<String>(hm.get(word));
				break;
			}
		
		return out;
	}
	
	public Set<String> translate(StringVector wordSet)
	{
		Set<String> out = new HashSet<String>();
		
		for (String word : wordSet)
		{
			if (!casesensitive) word = word.toLowerCase();
			
			for (Map<String,Set<String>> hm : maps)
				if (hm.containsKey(word))
				{
					out.addAll(hm.get(word));
					break;
				}
		}
		
		return out;
	}
	
	/**
	 * Returns the first translation of a word in the mappings.
	 * If no translation is found, the word is returned as-is.
	 */
	public String translateFirstInclusive(String word)
	{
		Set<String> out = translate(word);
		
		for (String s : out)
			return s;
		
		return word;
	}
	
	/**
	 * Returns the first translation of a word in the mappings.
	 * If no translation is found, null is returned.
	 */
	public String translateFirstExclusive(String word)
	{
		Set<String> out = translate(word);
		
		for (String s : out)
			return s;
		
		return null;
	}
	
	/**
	 * Translates an interaction. All possible interactions which can be formed in the highest priority
	 * map will be made.
	 * @return A network of possible interactions.
	 */
	public BooleanHashNetwork translate(SEdge inter)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(false,false);
		
		Set<String> i1 = translate(inter.getI1());
		Set<String> i2 = translate(inter.getI2());
		
		if (i1.size()>0 && i2.size()>0)
		{
			for (String s1 : i1)
				for (String s2 : i2)
					out.add(new UndirectedSEdge(s1,s2));
		}
		
		return out;
	}
	
	/**
	 * Translates a network. All possible interactions which can be formed in the highest priority
	 * map will be made.
	 * @return A network of possible interactions.
	 */
	public BooleanHashNetwork translate(SNetwork net)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(false,false);
		
		for (SEdge inter : net.edgeIterator())
		{
			BooleanHashNetwork t = this.translate(inter);
			
			out.addAll(t);
		}
						
		return out;
	}
	
	public BooleanMatrixNetwork translate(BooleanMatrixNetwork bmn)
	{
		List<String> nodes = bmn.nodeValues();
		BooleanMatrixNetwork out = new BooleanMatrixNetwork(false,false,this.translate(nodes));
		
		for (int i=0;i<bmn.connectivity().length;i++)
			for (int j=0;j<bmn.connectivity()[i].length;j++)
				if (bmn.contains(i, j))
					for (SEdge inter : this.translate(new UndirectedSEdge(nodes.get(i),nodes.get(j))))
						out.add(inter);
		
		return out;
	}
	
	public BooleanHashNetwork translateFirst(SNetwork net)
	{
		BooleanHashNetwork out = new BooleanHashNetwork(net.isSelfOk(),net.isDirected());
		
		for (SEdge inter : net.edgeIterator())
			out.add(this.translateFirst(inter));
						
		return out;
	}
	
	public SEdge translateFirst(SEdge inter)
	{
		if (inter.isDirected()) return new DirectedSEdge(this.translateFirstInclusive(inter.getI1()),this.translateFirstInclusive(inter.getI2()));
		else return new UndirectedSEdge(this.translateFirstInclusive(inter.getI1()),this.translateFirstInclusive(inter.getI2()));
	}
	
	public DoubleVector getMapDistribution()
	{
		return HashMapUtil.getMapDistributionSS(maps.get(0));
	}
	
	/**
	 * Builds a deep copy of the primary map.
	 */
	public Map<String,List<String>> getPrimaryMap()
	{
		Map<String,List<String>> outmap = new HashMap<String,List<String>>(maps.get(0).size());
		
		for (String s : maps.get(0).keySet())
			outmap.put(s, new ArrayList<String>(maps.get(0).get(s)));
		
		return outmap;
	}
	
	public int hashCode()
	{
		return maps.hashCode();
	}
	
	/**
	 * Returns true if the word has a mapping.
	 */
	public boolean isTranslateable(String word)
	{
		for (Map<String,Set<String>> hm : maps)
			if (hm.containsKey(word))
				return true;
		
		return false;
	}
	
	/**
	 * Returns a BooleanVector indicating which words have a mapping.
	 */
	public BooleanVector areTranslateable(List<String> words)
	{
		BooleanVector out = new BooleanVector(words.size());
		
		for (String s : words)
			out.add(isTranslateable(s));
		
		return out;
	}
	
	public List<String> translate(List<String> words)
	{
		List<String> out = new ArrayList<String>(words.size());
		
		for (String s : words)
			out.addAll(translate(s));
		
		return out;
	}
	
	public Set<String> translate(Set<String> words)
	{
		Set<String> out = new HashSet<String>(words.size());
		
		for (String s : words)
			out.addAll(translate(s));
		
		return out;
	}
	
	/**
	 * Returns the first translation of each word in the mappings for each word in the list.
	 */
	public List<String> translateFirstInclusive(List<String> words)
	{
		List<String> out = new ArrayList<String>(words.size());
		
		for (String s : words)
			out.add(translateFirstInclusive(s));
		
		return out;
	}
	
	/**
	 * Returns the first translation of each word in the mappings for each word in the set.
	 * Words with no mapping are left unchanged.
	 */
	public Set<String> translateFirstInclusive(Set<String> words)
	{
		Set<String> out = new HashSet<String>(words.size());
		
		for (String s : words)
			out.add(translateFirstInclusive(s));
		
		return out;
	}
	
	/**
	 * Returns the first translation of each word in the mappings for each word in the StringVector.
	 * Words with no mapping are left unchanged.
	 */
	public StringVector translateFirstInclusive(StringVector words)
	{
		StringVector out = new StringVector(words.size());
		
		for (String s : words)
			out.add(translateFirstInclusive(s));
		
		return out;
	}
	
	/**
	 * Returns the first translation of each word in the mappings for each word in the table.
	 */
	public StringTable translateFirst(StringTable st)
	{
		StringTable out = new StringTable(st.dim(0),st.dim(1));
		
		for (int i=0;i<st.dim(0);i++)
			for (int j=0;j<st.dim(1);j++)
				out.addtoRow(i, this.translateFirstInclusive(st.get(i, j)));
		
		return out;
	}
	
	/**
	 * Returns the first translation of each word in the mappings for each word in the vector.
	 */
	public StringVector translateFirst(StringVector sv)
	{
		StringVector out = new StringVector(sv.size());
		
		for (int i=0;i<sv.size();i++)
			out.add(this.translateFirstInclusive(sv.get(i)));
		
		return out;
	}
	
	public void printContradictions()
	{
		Set<String> allWords = new HashSet<String>();
		for (Map<String,Set<String>> map : maps)
			allWords.addAll(map.keySet());
		
		for (int m1=0;m1<maps.size()-1;m1++)
			for (int m2=m1+1;m2<maps.size();m2++)
				for (String word : allWords)
					if (maps.get(m1).containsKey(word) && maps.get(m2).containsKey(word) && !maps.get(m1).get(word).equals(maps.get(m2).get(word)))
						System.out.println(word+" | "+maps.get(m1).get(word)+" - "+maps.get(m2).get(word));			
	}
	
	/**
	 * Gets the size of the primary map.
	 */
	public int firstSize()
	{
		return maps.get(0).size();
	}
	
	public Set<String> getPrimaryKeys()
	{
		return maps.get(0).keySet();
	}
	
	/**
	 * This specifically is meant for creating a translation for annotations of the form: <gene_1>|<gene_2>...
	 * @param toTranslate List of genes to translate
	 * @return A single string containing translated genes in annotation file format. Genes that cannot be translated are not returned in the string.
	 */
	public String translateAnnotationDef (List<String> toTranslate)
	{
		String translated = "";
		BooleanVector translatable = this.areTranslateable(toTranslate);
		List<String> toTranslate2 = new ArrayList<String>(translatable.sum());
		for(int i=0; i<translatable.size(); i++)
			if(translatable.get(i))
				toTranslate2.add(toTranslate.get(i));
		
		for(int i=0; i<toTranslate2.size(); i++)
		{
			String s = this.translateFirstExclusive(toTranslate2.get(i));
			
			if((i<(toTranslate2.size()-1)))
				translated+=(this.translateFirstExclusive(toTranslate2.get(i))+"|");
			else
				translated+=(this.translateFirstExclusive(toTranslate2.get(i)));
		}
		
		return translated;
	}
	
}
