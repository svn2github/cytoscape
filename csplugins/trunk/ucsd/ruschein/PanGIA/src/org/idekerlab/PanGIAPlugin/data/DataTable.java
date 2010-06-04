package org.idekerlab.PanGIAPlugin.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DataTable {

	protected List<String> rownames;
	protected List<String> colnames;
	
	protected abstract void add(int row, String val);
	protected abstract void set(int row, int col, String val);
	protected abstract void removeDataRow(int row); 
	protected abstract void removeDataCol(int col);
	protected abstract void TransposeData();
	
	public abstract void Initialize(int numrows, int numcols);
	public abstract void InitializeRows(int numrows, int numcols);
	public abstract void addtoRow(int row, String val);
	
	public abstract int dim(int dimension);
	public abstract int numRows();
	public abstract int numCols();
	public abstract String getAsString(int row, int col);
	public abstract double getAsDouble(int row, int col);
	
	public abstract DataVector getCol(int col);
	public abstract DataVector getCol(String colname);
	public abstract DataTable getCol(StringVector names);
	public abstract DataTable getCol(IntVector indexes);
	
	public abstract DataVector getRow(int row);
	public abstract DataVector getRow(String rowname);
	public abstract DataTable getRow(StringVector names);
	public abstract DataTable getRow(IntVector indexes);
	
	public abstract int getRowLength(int row);
	
	
	/**
	 * Sets the rownames.
	 * Note: Makes a copy of the passed list.
	 * @param rownames
	 */
	public void setRowNames(List<String> rownames)
	{	
		if (rownames.size()!=this.dim(0))
		{
			System.err.println("Error DataTable.setRowNames(List<String>): rownames size does not match the number of rows.");
			System.err.println("rownames size: "+rownames.size());
			System.err.println("number of rows: "+this.dim(0));
			System.exit(0);
		}
		
		this.rownames = new ArrayList<String>(rownames);
	}
	
	public void setRowNames(StringVector rownames)
	{	
		if (rownames.size()!=this.dim(0))
		{
			System.err.println("Error DataTable.setRowNames(List<String>): rownames size does not match the number of rows.");
			System.err.println("rownames size: "+rownames.size());
			System.err.println("number of rows: "+this.dim(0));
			System.exit(0);
		}
		
		this.rownames = rownames.asStringList();
	}
	
	public void removeRowNames()
	{
		rownames = null;
	}
	
	public void removeColNames()
	{
		colnames = null;
	}
	
	/**
	 * Sets the colnames.
	 * Note: Makes a copy of the passed list.
	 * @param colnames
	 */
	public void setColNames(List<String> colnames)
	{
		if (colnames.size()!=this.numCols())
			throw new IllegalArgumentException("Colnames size ("+colnames.size()+") does not match the number of columns ("+this.numCols()+").");
		
		this.colnames = new ArrayList<String>(colnames);
	}
	
	/**
	 * Get the name of the row at the given index.
	 * @param index
	 */
	public String getRowName(int index)
	{
		if (this.hasRowNames())	return(rownames.get(index));
		else return null;
	}
	
	/**
	 * Get the name of the column at the given index.
	 * @param index
	 */
	public String getColName(int index)
	{
		if (this.hasColNames())	return(colnames.get(index));
		else return null;
	}
	
	/**
	 * Gets a copy of the rownames list.
	 */
	public ArrayList<String> getRowNames()
	{
		return(new ArrayList<String>(rownames));
	}
	
	/**
	 * Gets a copy of the colnames list.
	 */
	public ArrayList<String> getColNames()
	{
		return(new ArrayList<String>(colnames));
	}
	
	/**
	 * Sets the row name at the given index.
	 * @param index
	 * @param name
	 */
	public void setRowName(int index, String name)
	{
		rownames.set(index, name);
	}
	
	protected void addRowName(String name)
	{
		rownames.add(name);
	}
	
	/**
	 * Returns whether this DataTable has column names.
	 */
	public boolean hasColNames()
	{
		return colnames!=null;
	}
	
	/**
	 * Returns whether this DataTable has row names.
	 */
	public boolean hasRowNames()
	{
		return rownames!=null;
	}
	
	public boolean hasSpecificRow(String rowName)
	{
		if(this.hasRowNames())
			return this.rownames.contains(rowName);
		else
		{
			System.out.println("Warning: Table does not have any row names!");
			System.exit(0);
			return false;
		}
	}
	
	/**
	 * Sets the column name at the given index.
	 * @param index
	 * @param val
	 */
	public void setColName(int index, String val)
	{
		colnames.set(index, val);
	}
	
	/**
	 * Gets the row index corresponding to a the first row with the given name.
	 * @param rname
	 */
	protected int getRowIndex(String rname)
	{
		if (!this.hasRowNames())
		{
			System.err.println("Error getRowIndex(String): this DataTable does not have rownames.");
			System.exit(0);
		}
		
		return DataUtil.getSListIs(rownames,rname);
	}
	
	/**
	 * Gets the row indexes, each corresponding to the first row with the given name.
	 * @param rnames
	 */
	protected IntVector getRowIs(String[] rnames)
	{
		if (!this.hasRowNames())
		{
			System.err.println("Error getRowIs(String[]): this DataTable does not have rownames.");
			System.exit(0);
		}
		
		IntVector is = DataUtil.getSListIs(rownames,rnames);
		
		if (is.contains(-1))
		{
			System.err.println("Error getRowIs(String[]): rowname does not exist.");
			System.err.println(rnames[is.indexOf(-1)]);
			System.exit(0);
		}
		
		return is;
	}
	
	/**
	 * Gets the row indexes, each corresponding to the first row with the given name.
	 * @param rnames
	 */
	protected IntVector getRowIs(StringVector rnames)
	{
		if (!this.hasRowNames())
		{
			System.err.println("Error getRowIs(StringVector): this DataTable does not have rownames.");
			System.exit(0);
		}
		
		IntVector is = DataUtil.getSListIs(rownames,rnames);
		
		if (is.contains(-1))
		{
			System.err.println("Error getRowIs(StringVector): rowname does not exist.");
			System.err.println(rnames.get(is.indexOf(-1)));
			System.exit(0);
		}
		
		return is;
	}

	/**
	 * Gets the row indexes corresponding to a list of indexes.
	 * Accepts lists of types: Double, Integer, String (rownames)
	 * @param indexes
	 */
	@SuppressWarnings("unchecked")
	protected IntVector getRowIs(List<?> indexes)
	{
		IntVector is = new IntVector(indexes.size());
		
		if (indexes.size()==0) return is;
		
		if (indexes.get(0) instanceof Double)
			for (int i=0;i<indexes.size();i++)
				is.add(((Double)indexes.get(i)).intValue());
		
		if (indexes.get(0) instanceof Integer)
			for (int i=0;i<indexes.size();i++)
				is.add(((Integer)indexes.get(i)).intValue());
		
		if (indexes.get(0) instanceof String)
		{
			if (!this.hasRowNames())
			{
				System.err.println("Error getRowIs(List<?=String> indexes): this DataTable does not have rownames.");
				System.exit(0);
			}
			
			is = DataUtil.getSListIs(rownames,(List<String>)indexes);
		}
		
		if (is.contains(-1))
		{
			System.err.println("Error getRowIs(List<?> indexes): row does not exist.");
			System.err.println(is.get(is.indexOf(-1)));
			System.exit(0);
		}
		
		return is;
	}
	
	protected int getColIs(String rname)
	{
		int lis = DataUtil.getSListIs(colnames,rname);
		
		return lis;
	}
	
	protected IntVector getColIs(String[] rnames)
	{
		IntVector lis = DataUtil.getSListIs(colnames,rnames);
		
		while (lis.contains(-1)) lis.removeElement(-1);
		
		return lis;
	}
	
	protected IntVector getColIs(StringVector rnames)
	{
		IntVector lis = DataUtil.getSListIs(colnames,rnames);
		
		while (lis.contains(-1)) lis.removeElement(-1);
		
		return lis;
	}
	
	@SuppressWarnings("unchecked")
	protected IntVector getColIs(List<?> indexes)
	{
		IntVector is = new IntVector(indexes.size());
		
		if (indexes.size()==0) return is;
		
		if (indexes.get(0) instanceof Double)
			for (int i=0;i<indexes.size();i++)
				is.add(((Double)indexes.get(i)).intValue());
		
		if (indexes.get(0) instanceof Integer)
			for (int i=0;i<indexes.size();i++)
				is.add(((Integer)indexes.get(i)).intValue());
		
		if (indexes.get(0) instanceof String)
			is = DataUtil.getSListIs(colnames,(List<String>)indexes);
			
		while (is.contains(-1)) is.removeElement(-1);
		
		return is;
	}
	
	protected void Initialize(int numrows, int numcols, boolean arerownames, boolean arecolnames)
	{
		Initialize(numrows, numcols);
		
		if (arerownames)
		{
			rownames = new ArrayList<String>(numrows);
			for (int i=0;i<numrows;i++)
				rownames.add("");
		}
		if (arecolnames)
		{
			colnames = new ArrayList<String>(numcols);
			for (int i=0;i<numcols;i++)
				colnames.add("");
		}
	}
	
	public void removeCol(int col)
	{
		removeDataCol(col);
		
		if (colnames!=null) colnames.remove(col);
	}
	
	public void removeRow(int row)
	{
		removeDataRow(row);
		
		if (rownames!=null) rownames.remove(row);
	}
	
	public void removeRow(int[] rows)
	{
		List<Integer> ls = new ArrayList<Integer>(rows.length);
		
		for (int i=0;i<rows.length;i++)
			ls.add(rows[i]);
		
		Collections.sort(ls);
		
		for (int i=ls.size()-1;i>=0;i--)
			removeRow(ls.get(i));
	}
	
	
	public void removeRow(IntVector rows)
	{
		rows = rows.sort();
		
		for (int i=rows.size()-1;i>=0;i--)
			removeRow(rows.get(i));
	}
	
	public void removeCol(IntVector cols)
	{
		cols = cols.sort();
		
		for (int i=cols.size()-1;i>=0;i--)
			removeCol(cols.get(i));
	}
	
	public void removeRow(List<?> indexes)
	{
		removeRow(getRowIs(indexes));
	}
	
	public void removeRow(String[] names)
	{
		removeRow(getRowIs(names));
	}
	
	public void removeRow(StringVector names)
	{
		removeRow(getRowIs(names));
	}
	
	protected void LabelRow(DataVector row, int index)
	{
		if (this.hasRowNames()) row.setListName(rownames.get(index));
		
		if (this.hasColNames()) row.setElementNames(new ArrayList<String>(colnames));
	}
	
	protected void LabelCol(DataVector col, int index)
	{
		if (this.hasColNames()) col.setListName(colnames.get(index));
		
		if (this.hasRowNames()) col.setElementNames(new ArrayList<String>(rownames));
	}
	
	public void Load(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Initialize(0,0);
		
		//Create a filereader and open the file
		FileReader fr = null;
		try
		{
			fr = new FileReader(file);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File: "+file);
			System.exit(0);
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		//Determine the number of rows and columns, and build the column names
		String line="";
		int numrows=-2;
		int numcols=-1;
		ArrayList<String> newcolnames = new ArrayList<String>();
		do 
		{
			//Read each line
			try
			{
				line = br.readLine();
			}catch (IOException e)
			{
				System.out.println(e.getMessage());
				System.out.println("File: "+file);
				System.exit(0);
			}
			
			//Skip the first blank line?
			if (line==null && numrows==-2 && numcols==-1) return;
			
			if (numrows == -2)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+""+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+""+delimiter);
				if (line.endsWith(delimiter)) line+="";
				
				if (arecolnames)
				{
					newcolnames.addAll(Arrays.asList(line.split(delimiter,-1)));
					if (arerownames) newcolnames.remove(0);
				}
				else numrows++;
				
				numcols = line.split(delimiter).length;
				
				if (arerownames) numcols--;
			}
			
			numrows++;
		}while (line!=null);
						
		InitializeRows(numrows, numcols);
		
		ArrayList<String> newrownames = new ArrayList<String>(numrows-1);
		
		try
		{
			fr.close();
		}catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
		
		try
		{
			fr = new FileReader(file);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
		br = new BufferedReader(fr);
		
		int i = 0;
		
		if (!arecolnames) i = 1;
		
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
			
			if (line!=null && i!=0)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+""+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+""+delimiter);
				if (line.endsWith(delimiter)) line+="";
				
				String[] cols = line.split(delimiter,-1);
				
				int collength = cols.length;
				if (arerownames) collength--;
				
				if (collength!=numcols)
				{
					System.err.println("Error DataTable.Load(String,boolean,boolean): Row "+i+" does not have the correct number of columns.");
					System.err.println("Cols in row: "+cols.length);
					System.err.println("Rows in table: "+numcols);
					System.err.println(file);
					System.exit(0);
				}
				
				if (arerownames)
				{
					newrownames.add(cols[0]);
					for (int j=1;j<cols.length;j++)
						add(i-1, cols[j]);
				}else
				{
					for (int j=0;j<cols.length;j++)
						add(i-1, cols[j]);
				}
			}
			
			i++;
		}while (line!=null);
		
		try
		{
			br.close();
			fr.close();
		}catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
				
		if (arecolnames) setColNames(newcolnames);
		if (arerownames) setRowNames(newrownames);
	}

	public void appendFile(String file)
	{
		WriteOut(file,true,"\t");
	}
	
	public void appendFileWithNoLegends(String file)
	{
		this.WriteOutNoColOrRowNames(file, true);
	}
	
	public void save(String file)
	{
		WriteOut(file, false, "\t");
	}
	
	public void save(String file, String delim)
	{
		WriteOut(file, false, delim);
	}
	
	public void saveWithNoLegends(String file)
	{
		this.WriteOutNoColOrRowNames(file, false);
	}
	
	public void saveColumnLegend(String file)
	{
		new StringVector(this.colnames).save(file);
	}
	
	public void saveRowLegend(String file)
	{
		new StringVector(this.rownames).save(file);
	}
	
	private void WriteOut(String file, boolean append, String delim)
	{
		//Open/Create file for writing. If no file exists append->false
		File outfile = new File(file);
		if (!outfile.exists())
		{
			append = false;
			
			try
			{
				outfile.createNewFile();
			}catch (IOException e)
			{
				System.out.println(e.getMessage());
				System.out.println("File:"+file);
				System.exit(0);
			}
		}
		
		FileWriter fw = null;
		
		try
		{
			fw = new FileWriter(file, append);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}catch (IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		//Check to see if there is no data to write
		if (dim(0)==0 || dim(1)==0) return;
		
		if (this.hasColNames() && !append)
		{
			try
			{
				if (this.hasRowNames()) bw.write(delim);
				
				bw.write(colnames.get(0));
				for (int i=1;i<dim(1);i++)
					bw.write(delim + colnames.get(i));
				
				bw.write("\n");
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		
		for (int i=0;i<dim(0);i++)
		{
			if (getRowLength(i)!=dim(1))
			{
				System.out.println("Error DataTable.writeOut(String,boolean): Row length does not match the number of columns.");
				System.out.println("Error: Row index: "+i);
				System.out.println("Error: Row length: "+getRowLength(i));
				System.out.println("Number of columns: "+numCols());
				new Exception("").printStackTrace();
				return;
			}
			
			try
			{
				if (this.hasRowNames()) bw.write(rownames.get(i)+delim);
				
				bw.write(getAsString(i,0));
				for (int j=1;j<dim(1);j++)
					bw.write(delim + getAsString(i,j));
							
				bw.write("\n");
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
				System.out.println("File:"+file);
				System.exit(0);
			}
		}
		
		try {bw.close();}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
	}
	
	private void WriteOutNoColOrRowNames(String file, boolean append)
	{
		//Open/Create file for writing. If no file exists append->false
		File outfile = new File(file);
		if (!outfile.exists())
		{
			append = false;
			
			try
			{
				outfile.createNewFile();
			}catch (IOException e)
			{
				System.out.println(e.getMessage());
				System.out.println("File:"+file);
				System.exit(0);
			}
		}
		
		FileWriter fw = null;
		
		try
		{
			fw = new FileWriter(file, append);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}catch (IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		//Check to see if there is no data to write
		if (dim(0)==0 || dim(1)==0) return;
		
		for (int i=0;i<dim(0);i++)
		{
			if (getRowLength(i)!=dim(1))
			{
				System.out.println("Error DataTable.writeOut(String,boolean): Row length does not match the number of columns.");
				System.out.println("Error: Row index: "+i);
				System.out.println("Error: Row length: "+getRowLength(i));
				System.out.println("Number of columns: "+numCols());
				new Exception("").printStackTrace();
				return;
			}
			
			try
			{
				bw.write(getAsString(i,0));
				for (int j=1;j<dim(1);j++)
					bw.write("\t" + getAsString(i,j));
							
				bw.write("\n");
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
				System.out.println("File:"+file);
				System.exit(0);
			}
		}
		
		try {bw.close();}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("File:"+file);
			System.exit(0);
		}
	}
	
	public void print()
	{
		if (dim(0)==0 || dim(1)==0) return;
		
		if (this.hasColNames())
		{
			if (this.hasRowNames()) System.out.print("\t");
				
			System.out.print(colnames.get(0));
			
			for (int i=1;i<dim(1);i++)
				System.out.print("\t" + colnames.get(i));
				
			System.out.print("\n");
		}
		
		for (int i=0;i<dim(0);i++)
		{
			if (this.hasRowNames()) System.out.print(getRowNames().get(i)+"\t");
			
			System.out.print(getAsString(i,0));
			for (int j=1;j<dim(1);j++)
				System.out.print("\t" + getAsString(i,j));
						
			System.out.print("\n");
		
		}
	}
	
	public void Transpose()
	{
		List<String> temp = rownames;
		rownames = colnames;
		colnames = temp;
		
		TransposeData();
	}
	
	public void TrimByRow(List<String> rows)
	{
		List<Integer> removei = new ArrayList<Integer>();
		
		for (int r=0;r<rownames.size();r++)
			if (rows.indexOf(rownames.get(r))==-1)
				removei.add(r);
		
		for (int i=removei.size()-1;i>=0;i--)
			removeRow(removei.get(i).intValue());
	}
	
	protected boolean addColumns(DataTable dt)
	{
		boolean needsalignment = false;
		
		if (colnames!=null) colnames.addAll(dt.getColNames());
		
		if (rownames!=null && dt.getRowNames().size()>0)
		{
			for (int r=0;r<dt.dim(0);r++)
				if (!rownames.contains(dt.getRowName(r))) rownames.add(dt.getRowName(r));
			
			needsalignment = true;
		}else if (dt.dim(0) != dim(0))
		{
			System.out.println("Table rowsizes do not match.");
			System.exit(0);
		}
		
		return needsalignment;
	}
	
	public StringTable getComparisonMatrix(int keycol1, int keycol2, int datacol)
	{
		int numKeys = (1+(int)Math.sqrt(1+8*this.dim(0)))/2;
		StringTable out = new StringTable(numKeys,numKeys);
		out.Initialize(numKeys, numKeys);
		Set<String> terms = new HashSet<String>(this.getCol(keycol1).asStringList());
		terms.addAll(this.getCol(keycol2).asStringList());
		List<String> keys = new ArrayList<String>(terms);
		out.setColNames(keys);
		out.setRowNames(keys);
		
		for (int r=0;r<this.dim(0);r++)
		{
			int i = keys.indexOf(this.getAsString(r,keycol1));
			int j = keys.indexOf(this.getAsString(r,keycol2));
			
			String val = this.getAsString(r, datacol);
			
			out.set(i, j, val);
			out.set(j, i, val);
		}
		
		for (int d=0;d<out.dim(0);d++)
			out.set(d, d, "NaN");
		
		return out;
	}
}
