package org.idekerlab.PanGIAPlugin.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringTable extends DataTable{

	private List<List<String>> data;
	
	public StringTable()
	{
		data = new ArrayList<List<String>>();
	}
	
	public StringTable(int rows, int cols)
	{
		InitializeRows(rows,cols);
	}
	
	public StringTable(int rows, int cols, String val)
	{
		Initialize(rows,cols,val);
	}
	
	public StringTable(int rows, int cols, boolean arerownames, boolean arecolnames)
	{
		Initialize(rows,cols,arerownames,arecolnames);
	}
	
	public StringTable(List<String> rownames, List<String> colnames)
	{
		Initialize(rownames.size(),colnames.size(),"");
			
		setRowNames(rownames);
		setColNames(colnames);
	}
	
	public StringTable(String file, boolean arecolnames, boolean arerownames)
	{
		Load(file,arecolnames,arerownames,"\t");
	}
	
	public StringTable(String file, boolean arecolnames, boolean arerownames, String delimiter)
	{
		Load(file,arecolnames,arerownames,delimiter);
	}
	
	public StringTable(List<StringVector> rows)
	{
		data = new ArrayList<List<String>>(rows.size());
		for (StringVector row : rows)
			data.add(row.asStringList());
	}
	
	public void Initialize(int numrows, int numcols)
	{
		data = new ArrayList<List<String>>(numrows);
	}
	
	public void Initialize(int numrows, int numcols, String val)
	{
		data = new ArrayList<List<String>>(numrows);
		for (int row=0;row<numrows;row++)
		{
			ArrayList<String> newlist = new ArrayList<String>(numcols);
			for (int col=0;col<numcols;col++)
				newlist.add("");
			data.add(newlist);
		}
	}
	
	public void InitializeRows(int numrows, int numcols)
	{
		data = new ArrayList<List<String>>(numrows);
		for (int row=0;row<numrows;row++)
		{
			ArrayList<String> newlist = new ArrayList<String>(numcols);
			data.add(newlist);
		}
	}
	
	public void addRow(int numcols)
	{
		ArrayList<String> newlist = new ArrayList<String>(numcols);
		data.add(newlist);
	}
	
	public void add(int row, String val)
	{
		data.get(row).add(val);
	}
	
	public void add(int row, int val)
	{
		data.get(row).add(String.valueOf(val));
	}
	
	public void add(int row, char val)
	{
		data.get(row).add(String.valueOf(val));
	}
	
	public void add(int row, double val)
	{
		data.get(row).add(String.valueOf(val));
	}
	
	public void addtoRow(int row, String val)
	{
		data.get(row).add(val);
	}

	public void addtoRow(int row, int val)
	{
		data.get(row).add(val+"");
	}
	
	public void addtoRow(int row, Object o)
	{
		data.get(row).add(o.toString());
	}
	
	public void removeDataRow(int row)
	{
		data.remove(row);
	}
	
	public int getRowLength(int row)
	{
		return data.get(row).size();
	}
	
	protected void removeDataCol(int col)
	{
		if (col<0 || col>=dim(1))
		{
			System.out.println("RemoveCol: Column does not exist.");
			System.exit(0);
		}
		
		for (int r=0;r<data.size();r++)
			data.get(r).remove(col);
					
	}
	
	public String getAsString(int row, int col)
	{
		return data.get(row).get(col);
	}
	
	public double getAsDouble(int row, int col)
	{
		try
		{
			return Double.valueOf(data.get(row).get(col));
		} catch (java.lang.NumberFormatException e)
		{
			return Double.NaN;
		} 
	}
	
	public String get(int i, int j)
	{
		return(data.get(i).get(j));
	}
	
	public String get(String row, int j)
	{
		return(data.get(getRowNames().indexOf(row)).get(j));
	}
	
	public String get(int i, String col)
	{
		return(data.get(i).get(getColNames().indexOf(col)));
	}
	
	public String get(String row, String col)
	{
		return(data.get(getRowNames().indexOf(row)).get(getColNames().indexOf(col)));
	}
	
	public StringVector getCol(int col)
	{
		StringVector column = new StringVector(this.numRows());
		
		LabelCol(column,col);
		
		for (int r=0;r<this.numRows();r++)
			column.add(data.get(r).get(col));
		
		return column;
	}
	
	public StringVector getCol(String colname)
	{
		return getCol(getColIs(colname));
	}
	
	public StringTable getCol(List<?> indexes)
	{
		StringTable cols = new StringTable(dim(0),indexes.size(),!this.hasRowNames(),!this.hasColNames());
		
		if (!this.hasRowNames()) cols.setRowNames(rownames);
		
		IntVector is = getColIs(indexes);
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(is.get(i)));
		
		return cols;
	}
	
	public StringTable getCol(IntVector indexes)
	{
		return getCol(indexes.asIntArray());
	}
	
	public StringTable getCol(int[] indexes)
	{
		StringTable cols = new StringTable(dim(0),indexes.length,this.hasRowNames(),this.hasColNames());
		
		cols.Initialize(dim(0),indexes.length,"");
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.length;i++)
			cols.setCol(i,getCol(indexes[i]));
		
		return cols;
	}
	
	public StringTable getRow(IntVector indexes)
	{
		StringTable rows = new StringTable(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		for (int i=0;i<indexes.size();i++)
			rows.addRow(getRow(indexes.get(i)));
		
		if (this.hasRowNames())
		{
			List<String> rownames = new ArrayList<String>(indexes.size());
			for (int i=0;i<indexes.size();i++)
				rownames.add(this.getRowName(indexes.get(i)));
			rows.setRowNames(rownames);
		}
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		return rows;
	}
	
	public StringTable getRow(int firstIndex, int lastIndex)
	{
		int numRows = lastIndex-firstIndex+1;
		StringTable rows = new StringTable(numRows,dim(1),this.hasRowNames(),this.hasColNames());
		
		for (int i=0;i<numRows;i++)
			rows.addRow(getRow(i+firstIndex));
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		return rows;
	}
	
	public StringTable getCol(StringVector names)
	{
		ArrayList<Integer> is = new ArrayList<Integer>(names.size());
		
		for (int i=0;i<names.size();i++)
			is.add(this.colnames.indexOf(names.get(i)));
					
		return getCol(is);
	}
	
	public StringVector getRow(String rowname)
	{
		return getRow(getRowIndex(rowname));
	}
	
	public StringTable getRow(List<?> indexes)
	{
		StringTable rows = new StringTable(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setRowNames(colnames);
		
		IntVector is = getRowIs(indexes);
		
		return getRow(is);
	}
	
	public StringTable getRow(BooleanVector bv)
	{
		if (bv.size()!=this.numRows()) throw new java.lang.IllegalArgumentException("Vector must be the same size as number of rows. vec="+bv.size()+", rows="+this.numRows());
		
		return getRow(bv.asIndexes());
	}

	
	public StringTable getRow(StringVector names)
	{
		ArrayList<Integer> is = new ArrayList<Integer>(names.size());
		
		for (int i=0;i<names.size();i++)
			is.add(this.rownames.indexOf(names.get(i)));
					
		return getRow(is);
	}
	
	public StringVector getRow(int row)
	{
		StringVector arow = new StringVector(this.numCols());
		
		LabelRow(arow,row);
		
		for (int c=0;c<this.numCols();c++)
			arow.add(data.get(row).get(c));
		
		return arow;
	}
	
	public void set(String row, int j, String val)
	{
		data.get(getRowNames().indexOf(row)).set(j, val);
	}
	
	public void set(int i, String col, String val)
	{
		data.get(i).set(getColNames().indexOf(col), val);
	}
	
	public void set(String row, String col, String val)
	{
		data.get(getRowNames().indexOf(row)).set(getColNames().indexOf(col), val);
	}
	
	public void set(int row, int col, String val)
	{
		data.get(row).set(col, val);
	}
	
	public void set(int row, int col, double val)
	{
		data.get(row).set(col, val+"");
	}
	
	public void set(int row, int col, int val)
	{
		data.get(row).set(col, val+"");
	}
	
	public void setCol(int col, StringVector st)
	{
		if (st.size()!=dim(0))
		{
			System.err.println("Error StringTable.setCol(int, StringVector): Vector length must equal number of rows.");
			System.exit(0);
		}
		
		if (st.hasListName() && this.hasColNames())
			this.setColName(col, st.listname);
		
		for (int r=0;r<dim(0);r++)
			set(r,col,st.get(r));
	}
	
	public void setCol(int col, double[] list)
	{
		if (list.length!=dim(0))
		{
			System.err.println("Error StringTable.setCol(int, StringVector): Vector length must equal number of rows.");
			System.exit(0);
		}
		
		for (int r=0;r<this.numRows();r++)
			set(r,col,list[r]);
	}
	
	public synchronized void addRow(StringVector vec)
	{
		if (vec.size()!=numCols() && numRows()!=0)
		{
			System.err.println("Error StringTable.addRow(StringVector): Vector size must equal number of columns.");
			System.err.println("Vector size: "+vec.size()+", numCols: "+numCols());
			System.err.println("NumRows: "+numRows());
			System.exit(0);
		}
		
		if (vec.hasListName() && this.hasRowNames())
			this.addRowName(vec.getListName());
		
		List<String> newRow = vec.asStringList();
		
		data.add(newRow);
	}
	
	public void setRow(int row, StringVector st)
	{
		if (st.size()!=dim(1))
		{
			System.err.println("Error StringTable.setRow(int, StringVector): Vector length must equal number of cols.");
			System.err.println("Vector length: "+st.size()+", Dim(1): "+dim(1));
			System.exit(0);
		}
		
		if (st.hasListName() && this.hasRowNames())
			this.setRowName(row, st.listname);
		
		for (int c=0;c<dim(1);c++)
			set(row,c,st.get(c));
	}
	
	public List<String> asList()
	{
		List<String> array = new ArrayList<String>();
		
		if (dim(0)==1)
		{
			for (int i=0;i<dim(1);i++)
				array.add(get(0,i));
		}else if (dim(1)==1)
		{
			for (int i=0;i<dim(0);i++)
				array.add(get(i,0));
		}else
		{
			for (int i=0;i<dim(0);i++)
				for (int j=0;j<dim(1);j++)
					array.add(get(i,j));
		}
		
		return array;
	}
	
	public StringTable clone()
	{
		StringTable copy = new StringTable();
		
		int rows = data.size();
		int cols = data.get(0).size();
		
		copy.data = new ArrayList<List<String>>(rows);
		
		for (int row=0;row<rows;row++)
		{
			List<String> temp = new ArrayList<String>(cols);
			for (int col=0;col<cols;col++)
				temp.add(get(row,col));
			
			copy.data.add(temp);
		}
		
		if (this.hasColNames()) copy.setColNames(new ArrayList<String>(this.getColNames()));
		if (this.hasRowNames()) copy.setRowNames(new ArrayList<String>(this.getRowNames()));
		
		return(copy);
	}
	
	public int dim(int dimension)
	{
		if (data.size()==0) return 0;
		
		if (dimension==0) return data.size();
		
		if (data.size()>0 && dimension==1)  return data.get(0).size();
		
		return -1;
	}
	
	public String getMax()
	{
		String max = data.get(0).get(0);
		
		boolean found = false;
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				{
					if (data.get(i).get(j).compareTo(max)>0) max = data.get(i).get(j);
					found = true;
				}
		
		if (!found) return "";
		else return (max);
	}
	
	public String getMin()
	{
		String min = data.get(0).get(0);
		
		boolean found = false;
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				{
					if (data.get(i).get(j).compareTo(min)<0) min = data.get(i).get(j);
					found = true;
				}
		
		if (!found) return "";
		else return (min);
	}

	public static StringTable join(StringTable dt1, StringTable dt2)
	{
		int numcols = dt1.dim(1);
		
		ArrayList<String> rownames = dt1.getRowNames();
		rownames.addAll(dt2.getRowNames());
		
		StringTable dtout = new StringTable(rownames, dt1.getColNames());
		
		for (int i=0;i<dt1.dim(0);i++)
			for (int j=0;j<numcols;j++)
				dtout.set(i, j, dt1.get(i, j));
		
		for (int i=0;i<dt2.dim(0);i++)
			for (int j=0;j<numcols;j++)
				dtout.set(i+dt1.dim(0), j, dt2.get(i, j));
		
		return dtout;
	}
	
	public static StringTable joinAll(List<StringTable> dtlist)
	{
		if (dtlist.size()==0) return null;
		if (dtlist.size()==1) return dtlist.get(0);
		
		StringTable dtout = join(dtlist.get(0), dtlist.get(1));
		
		for (int i=2;i<dtlist.size();i++)
			dtout = join(dtout, dtlist.get(i));
		
		return dtout;
	}
	
	protected void TransposeData()
	{
		ArrayList<List<String>> tempdata = new ArrayList<List<String>>(dim(1));
		for (int row=0;row<dim(1);row++)
		{
			ArrayList<String> newlist = new ArrayList<String>(dim(0));
			for (int col=0;col<dim(0);col++)
				newlist.add(get(col,row));
			
			tempdata.add(newlist);
		}
		
		data = tempdata;
	}
	
	public void SortRows(int keycol)
	{
		StringVector sv = this.getCol(keycol);
		
		IntVector index = sv.sort_I();
		
		if (this.hasRowNames())	this.setRowNames(sv.getElementNames(index));

		List<List<String>> mydata = new ArrayList<List<String>>(dim(0));
		for (int row=0;row<dim(0);row++)
		{
			List<String> newlist = new ArrayList<String>(dim(1));
			for (int col=0;col<dim(1);col++)
				newlist.add(this.get(index.get(row),col));
			
			mydata.add(newlist);
		}
		
		data = mydata;
	}
	
	public void SortRowsAsDouble(int keycol)
	{
		DoubleVector dv = this.getCol(keycol).asDoubleVector();
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())
		{
			this.setRowNames(dv.getElementNames(index));
			
		}
		List<List<String>> mydata = new ArrayList<List<String>>(dim(0));
		for (int row=0;row<dim(0);row++)
		{
			List<String> newlist = new ArrayList<String>(dim(1));
			for (int col=0;col<dim(1);col++)
				newlist.add(this.get(index.get(row),col));
			
			mydata.add(newlist);
		}
		
		data = mydata;
	}
	
	public void SortRowsAsInt(int keycol)
	{
		IntVector dv = this.getCol(keycol).asIntVector();
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())	this.setRowNames(dv.getElementNames(index));

		List<List<String>> mydata = new ArrayList<List<String>>(dim(0));
		for (int row=0;row<dim(0);row++)
		{
			List<String> newlist = new ArrayList<String>(dim(1));
			for (int col=0;col<dim(1);col++)
				newlist.add(this.get(index.get(row),col));
			
			mydata.add(newlist);
		}
		
		data = mydata;
	}
	
	public StringTable buildMatrix(List<String> rows, List<String> cols, int rowKeyCol, int colKeyCol, int dataCol)
	{
		StringTable mat = new StringTable(rows,cols);
				
		for (int i=0;i<this.dim(0);i++)
			if (rows.contains(this.get(i,rowKeyCol)) && cols.contains(this.get(i,colKeyCol)))
				mat.set(rows.indexOf(this.get(i,rowKeyCol)), cols.indexOf(this.get(i,colKeyCol)), this.get(i, dataCol));
		
		return mat;
	}
	
	public StringTable replace(String oldVal, String newVal)
	{
		StringTable out = this.clone();
		
		for (int i=0;i<this.dim(0);i++)
			for (int j=0;j<this.dim(1);j++)
				if (this.get(i, j).equals(oldVal)) out.set(i, j, newVal);
		
		return out;
	}
	
	public void ReverseRows()
	{
		for (int i=0;i<this.dim(0)/2;i++)
		{
			int otheri = this.dim(0)-i-1;
			List<String> temp = data.get(otheri);
			data.set(otheri, data.get(i));
			data.set(i, temp);
		}
	}
	
	public int numRows()
	{
		if (data!=null) return data.size();
		else return 0;
	}
	
	public int numCols()
	{
		if (data!=null && data.size()>0) return data.get(0).size();
		else return 0;
	}
	
	public Map<String,String> hash2columns (int keyColIndex, int valueColIndex)
	{
		Map<String,String> hashedColumns = new HashMap<String,String>(this.dim(0));
		for(int i=0; i<this.dim(0); i++)
			hashedColumns.put(this.get(i, keyColIndex),this.get(i, valueColIndex));
		
		return hashedColumns;
	}
	
	public void addCol(DoubleVector v)
	{
		for (int i=0;i<v.size();i++)
			data.get(i).add(v.getAsString(i));
	}
}
