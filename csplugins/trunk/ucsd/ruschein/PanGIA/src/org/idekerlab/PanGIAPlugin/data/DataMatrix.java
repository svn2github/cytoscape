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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.utilities.files.*;

public abstract class DataMatrix {

	protected List<String> rownames;
	protected List<String> colnames;
	
	public abstract void set(int row, int col, String val);
	public abstract void set(int row, int col, double val);
	public abstract void set(int row, int col, float val);
	
	public abstract void Initialize(int numrows, int numcols);
	
	public abstract int dim(int dimension);
	public abstract int numRows();
	public abstract int numCols();
	public abstract int size();
	public abstract String getAsString(int row, int col);
	public abstract double getAsDouble(int row, int col);
	public abstract float getAsFloat(int row, int col);
	public abstract int getAsInteger(int row, int col);
	
	public abstract DataVector getCol(int col);
	public abstract DataMatrix getCol(StringVector names);
	public abstract DataMatrix getCol(IntVector indexes);
	public abstract DataMatrix getCol(int[] indexes);
	
	public abstract DataVector getRow(int row);
	public abstract DataVector getRow(String rowname);
	public abstract DataMatrix getRow(StringVector names);
	public abstract DataMatrix getRow(IntVector indexes);
	
	
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
		if (colnames.size()!=this.dim(1))
		{
			System.err.println("Error DataTable.setColNames(List<String>): colnames size does not match the number of columns.");
			System.err.println("colnames size: "+colnames.size());
			System.err.println("number of columns: "+this.dim(1));
			System.exit(0);
		}
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
	
	
	
	protected void LoadCols(String file, boolean arerownames, boolean arecolnames, String delimiter, int[] colIndexes)
	{
		Initialize(0,0);
		
		//Determine the number of rows and columns, and build the column names
		int numrows = arecolnames ? -1 : 0;
		int numcols=-1;
		List<String> newcolnames = new ArrayList<String>();
		
		for (String line : new FileIterator(file))
		{
			//Skip the first blank line?
			if (numrows == -1)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				if (line.endsWith(delimiter)) line+="NaN";
				
				if (arecolnames)
				{
					String[] cols = line.split(delimiter);
					
					for (int i=0;i<colIndexes.length;i++)
						newcolnames.add(cols[colIndexes[i]]);
					if (arerownames) newcolnames.remove(0);
				}
				else numrows++;
				
			}
			
			if (numrows==0) numcols = colIndexes.length;
			
			numrows++;
		}
						
		Initialize(numrows, numcols);
		
		List<String> newrownames = new ArrayList<String>(numrows-1);
		
		int i = 0;
		
		if (!arecolnames) i = 1;
		
		for (String line : new FileIterator(file)) 
		{
			if (i!=0)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				if (line.endsWith(delimiter)) line+="NaN";
				
				String[] cols = line.split(delimiter);
				
				if (arerownames)
				{
					newrownames.add(cols[0]);
					for (int j=0;j<colIndexes.length;j++)
						set(i-1, j-1, cols[colIndexes[j]]);
				}else
				{
					for (int j=0;j<colIndexes.length;j++)
						set(i-1, j, cols[colIndexes[j]]);
				}
			}
			
			i++;
		}
			
		if (arecolnames) setColNames(newcolnames);
		if (arerownames) setRowNames(newrownames);
	}
	
	public void Load(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Initialize(0,0);
		
		//Determine the number of rows and columns, and build the column names
		int numrows = arecolnames ? -1 : 0;
		int numcols=-1;
		List<String> newcolnames = new ArrayList<String>();
		
		for (String line : new FileIterator(file))
		{
			//Skip the first blank line?
			if (numrows == -1)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				if (line.endsWith(delimiter)) line+="NaN";
				
				if (arecolnames)
				{
					newcolnames.addAll(Arrays.asList(line.split(delimiter)));
					if (arerownames) newcolnames.remove(0);
				}
				else numrows++;
				
			}
			
			if (numrows==0) numcols = line.split(delimiter).length;
			
			numrows++;
		}
						
		Initialize(numrows, numcols);
		
		List<String> newrownames = new ArrayList<String>(numrows-1);
		
		int i = 0;
		
		if (!arecolnames) i = 1;
		
		for (String line : new FileIterator(file)) 
		{
			if (i!=0)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				if (line.endsWith(delimiter)) line+="NaN";
				
				String[] cols = line.split(delimiter);
				
				int collength = cols.length;
				if (arerownames) collength--;
				
				if (collength!=numcols) throw new java.lang.AssertionError("Row "+i+" does not have the correct number of columns.");
								
				if (arerownames)
				{
					newrownames.add(cols[0]);
					for (int j=1;j<cols.length;j++)
						set(i-1, j-1, cols[j]);
				}else
				{
					for (int j=0;j<cols.length;j++)
						set(i-1, j, cols[j]);
				}
			}
			
			i++;
		}
			
		if (arecolnames) setColNames(newcolnames);
		if (arerownames) setRowNames(newrownames);
	}
	
	public static DoubleMatrix loadTranspose(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		DoubleMatrix out = new DoubleMatrix();
		
		out.Initialize(0,0);
		
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
		List<String> newcolnames = new ArrayList<String>();
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
			if (line==null && numrows==-2 && numcols==-1) return out;
			
			if (numrows == -2)
			{
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				if (line.endsWith(delimiter)) line+="NaN";
				
				if (arecolnames)
				{
					newcolnames.addAll(Arrays.asList(line.split(delimiter)));
					newcolnames.remove(0);
				}
				else numrows++;
				
				numcols = line.split(delimiter).length;
				
				if (arerownames) numcols--;
			}
			
			numrows++;
		}while (line!=null);
						
		out.Initialize(numcols, numrows);
		
		List<String> newrownames = new ArrayList<String>(numrows-1);
		
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
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				line=line.replaceAll(delimiter+delimiter, delimiter+"NaN"+delimiter);
				if (line.endsWith(delimiter)) line+="NaN";
				
				String[] cols = line.split(delimiter);
				
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
						out.set(j-1, i-1, cols[j]);
				}else
				{
					for (int j=0;j<cols.length;j++)
						out.set(j, i-1, cols[j]);
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
				
		if (arecolnames) out.setRowNames(newcolnames);
		if (arerownames) out.setColNames(newrownames);
		
		return out;
	}

	public void appendFile(String file)
	{
		WriteOut(file,true);
	}
	
	public void save(String file)
	{
		WriteOut(file, false);
	}
	
	private void WriteOut(String file, boolean append)
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
			System.exit(0);
		}catch (IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		//Check to see if there is no data to write
		if (this.numRows()==0 || this.numCols()==0) return;
		
		if (this.hasColNames() && !append)
		{
			try
			{
				if (this.hasRowNames()) bw.write("\t");
				
				bw.write(colnames.get(0));
				for (int i=1;i<dim(1);i++)
					bw.write("\t" + colnames.get(i));
				
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
			try
			{
				if (this.hasRowNames()) bw.write(rownames.get(i)+"\t");
				
				bw.write(getAsString(i,0));
				for (int j=1;j<dim(1);j++)
					bw.write("\t" + getAsString(i,j));
							
				bw.write("\n");
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		
		try {bw.close();}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	public void print()
	{
		if (this.size()==0) return;
		
		if (this.hasColNames())
		{
			if (this.hasRowNames()) System.out.print("\t");
				
			System.out.print(colnames.get(0));
			
			for (int i=1;i<dim(1);i++)
				System.out.print("\t" + colnames.get(i));
				
			System.out.print("\n");
		}
		
		for (int i=0;i<this.numRows();i++)
		{
			if (this.hasRowNames()) System.out.print(getRowNames().get(i)+"\t");
			
			System.out.print(getAsString(i,0));
			for (int j=1;j<dim(1);j++)
				System.out.print("\t" + getAsString(i,j));
						
			System.out.print("\n");
		
		}
	}
	
	public void printHead()
	{
		if (this.size()==0) return;
		
		if (this.hasColNames())
		{
			if (this.hasRowNames()) System.out.print("\t");
				
			System.out.print(colnames.get(0));
			
			for (int i=1;i<dim(1);i++)
				System.out.print("\t" + colnames.get(i));
				
			System.out.print("\n");
		}
		
		for (int i=0;i<10;i++)
		{
			if (this.hasRowNames()) System.out.print(getRowNames().get(i)+"\t");
			
			System.out.print(getAsString(i,0));
			for (int j=1;j<dim(1);j++)
				System.out.print("\t" + getAsString(i,j));
						
			System.out.print("\n");
		
		}
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
	
	public double sumColumnAsDouble(int col)
	{
		double sum = 0;
		
		for (int i=0;i<this.numRows();i++)
			sum += this.getAsDouble(i, col);
		
		return sum;
	}
	
	public double sumRowAsDouble(int row)
	{
		double sum = 0;
		
		for (int i=0;i<this.numCols();i++)
			sum += this.getAsDouble(row, i);
		
		return sum;
	}
	
	public BooleanVector colEqualTo(int col, double value)
	{
		BooleanVector out = new BooleanVector(this.numRows());
		
		for (int i=0;i<this.numRows();i++)
			out.add(this.getAsDouble(i, col)==value);
		
		return out;
	}
	
	public DoubleMatrix corr()
	{
		DoubleMatrix out = new DoubleMatrix(this.numCols(), this.numCols());
		
		DoubleMatrix data = new DoubleMatrix(this);
		data.centerCols_ignoreNaN();
		
		DoubleVector colSums2 = data.sumSquareByCol_ignoreNaN();
		
		for (int i=0;i<this.numCols();i++)
			out.set(i, i, 1);
		
		for (int i=0;i<this.numCols()-1;i++)
			for (int j=i+1;j<this.numCols();j++)
			{
				double sum_xy = 0;
				
				for (int k=0;k<data.numRows();k++)
					if (!Double.isNaN(data.get(k,i)) && !Double.isNaN(data.get(k,j))) sum_xy+=data.get(k,i)*data.get(k,j);
				
				double sumx2 = colSums2.get(i);
				double sumy2 = colSums2.get(j);
				
				double cor = (this.numCols()*sum_xy)/(  Math.sqrt(  this.numCols()*sumx2  )*Math.sqrt(  this.numCols()*sumy2  )  );
				
				out.set(i, j, cor);
				out.set(j, i, cor);
			}
		
		return out;
	}
}
