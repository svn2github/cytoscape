package org.idekerlab.PanGIAPlugin.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringMatrix extends DataMatrix{

	private String[][] data;
	
	public StringMatrix()
	{
		Initialize(0,0,false,false);
	}
	
	public StringMatrix(StringMatrix dt)
	{
		this.data = new String[dt.dim(0)][dt.dim(1)];
		
		for (int i=0;i<dt.dim(0);i++)
			for (int j=0;j<dt.dim(1);j++)
				data[i][j] = dt.get(i, j);
		
		if (dt.hasColNames()) this.setColNames(new ArrayList<String>(dt.getColNames()));
		if (dt.hasRowNames()) this.setRowNames(new ArrayList<String>(dt.getRowNames()));
	}
	
	public StringMatrix(int rows, int cols)
	{
		Initialize(rows,cols,false,false);
	}
	
	public StringMatrix(int rows, int cols, String val)
	{
		Initialize(rows,cols,val);
	}
	
	public StringMatrix(int rows, int cols, int val)
	{
		Initialize(rows,cols,String.valueOf(val));
	}
	
	public StringMatrix(int rows, int cols, boolean arerownames, boolean arecolnames)
	{
		Initialize(rows,cols,arerownames,arecolnames);
	}
	
	public StringMatrix(int rows, int cols, ArrayList<String> rownames, ArrayList<String> colnames)
	{
		Initialize(rows,cols);
		setRowNames(rownames);
		setColNames(colnames);
	}
	
	public StringMatrix(int[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public StringMatrix(String[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public StringMatrix(double[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public StringMatrix(String file, boolean arerownames, boolean arecolnames)
	{
		Load(file,arerownames,arecolnames,"\t");
	}

	public StringMatrix(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Load(file,arerownames,arecolnames,delimiter);
	}
	
	public StringMatrix plus(String val)
	{
		StringMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, this.get(i, j)+val);
		
		return out;
	}
	
	public StringMatrix plus(StringMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		StringMatrix out = this.clone();
		
		for (int i=0;i<this.dim(0);i++)
			for (int j=0;j<this.dim(1);j++)
				out.set(i,j, this.get(i,j)+data2.get(i,j));
		
		return out;
	}
	
	public String[][] getData()
	{
		return data;
	}
	
	public String[][] asStringArray()
	{
		String[][] da = new String[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = data[i][j];
		
		return da;
	}
	
	public double[][] asdoubleArray()
	{
		double[][] da = new double[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = Double.valueOf(data[i][j]);
		
		return da;
	}
	
	public int[][] asintArray()
	{
		int[][] da = new int[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = Integer.valueOf(data[i][j]);
		
		return da;
	}
	
	public StringVector getRow(String rowname)
	{
		return getRow(getRowIndex(rowname));
	}
	
	public StringMatrix getRow(StringVector names)
	{
		return getRow(getRowIs(names));
	}
	
	public StringVector getCol(String colname)
	{
		return getCol(getColIs(colname));
	}
	
	public StringMatrix getCol(StringVector names)
	{
		return getCol(getColIs(names));
	}
	
	public StringMatrix clone()
	{
		return new StringMatrix(this);
	}
	
	public int dim(int dimension)
	{	
		if (dimension==0) return data.length;
		
		if (data.length>0 && dimension==1) return data[0].length;
		
		return -1;
	}
	
	public String get(int i, int j)
	{
		return(data[i][j]);
	}
	
	public double getAsDouble(int i, int j)
	{
		return(Double.valueOf(data[i][j]));
	}
	
	public float getAsFloat(int i, int j)
	{
		return(Float.valueOf(data[i][j]));
	}
	
	public String get(int i, String col)
	{
		return(get(i,this.colnames.indexOf(col)));
	}
	
	public String get(String row, int j)
	{
		return(get(this.colnames.indexOf(row),j));
	}
	
	public String get(String row, String col)
	{
		return(get(this.colnames.indexOf(row),this.colnames.indexOf(col)));
	}
	
	public String getAsString(int row, int col)
	{
		return data[row][col];
	}
	
	public StringVector getCol(int col)
	{
		String[] cola = new String[data.length];
		
		for (int r=0;r<data.length;r++)
			cola[r] = data[r][col];
		
		StringVector column = new StringVector(cola);
		
		LabelCol(column,col);
		
		return column;
	}
	
	public StringMatrix getCol(int col0, int col1)
	{
		int numCols = col1-col0+1;
		StringMatrix cols = new StringMatrix(this.numRows(),numCols,this.hasColNames(),this.hasRowNames());
		
		if (this.hasRowNames()) cols.setRowNames(this.getRowNames());
		if (this.hasColNames()) cols.setColNames(this.colnames.subList(col0, col1));
		
		for (int r=0;r<this.numRows();r++)
			for (int c=col0;c<col1;c++)
				cols.set(r,c-col0,this.get(r,c));
		
		return cols;
	}
	
	public StringMatrix getCol(List<?> indexes)
	{
		StringMatrix cols = new StringMatrix(dim(0),indexes.size(),this.hasColNames(),this.hasRowNames());
		
		if (this.hasRowNames()) cols.setRowNames(this.getRowNames());
		
		IntVector is = getColIs(indexes);
		
		if (indexes.size()>is.size())
		{
			System.out.println("Error getCol(List<?> indexes): index value does not exist.");
			System.exit(0);
		}
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(is.get(i)));
		
		return cols;
	}
	
	public StringMatrix getCol(BooleanVector bv)
	{
		if (bv.size()!=this.numCols()) throw new java.lang.IllegalArgumentException("Vector must be the same size as number of columns. vec="+bv.size()+", cols="+this.numCols());
		
		return getCol(bv.asIndexes());
	}
	
	public StringMatrix getCol(IntVector indexes)
	{
		StringMatrix cols = new StringMatrix(this.numRows(),indexes.size(),this.hasRowNames(),this.hasColNames());
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(indexes.get(i)));
		
		return cols;
	}
	
	public StringMatrix getCol(Set<Integer> indexes)
	{
		StringMatrix cols = new StringMatrix(this.numRows(),indexes.size(),this.hasRowNames(),this.hasColNames());
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		int j=0;
		for (int i=0;i<this.numCols();i++)
			if (indexes.contains(i))
			{
				cols.setCol(j,getCol(i));
				j++;
			}
		
		return cols;
	}
	
	public StringMatrix getCol(int[] indexes)
	{
		StringMatrix cols = new StringMatrix(this.numRows(),indexes.length,this.hasRowNames(),this.hasColNames());
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.length;i++)
			cols.setCol(i,getCol(indexes[i]));
		
		return cols;
	}
	
	public StringVector getRow(int row)
	{
		StringVector arow = new StringVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			arow.add(get(row,c));
		
		LabelRow(arow,row);
		
		return arow;
	}
	
	public StringMatrix getRow(List<?> indexes)
	{
		StringMatrix rows = new StringMatrix(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setColNames(this.getColNames());
		
		IntVector is = this.getRowIs(indexes);
		
		return getRow(is);
	}
	
	public StringMatrix getRow(int[] indexes)
	{
		StringMatrix rows = new StringMatrix(indexes.length,dim(1));
		
		for (int i=0;i<indexes.length;i++)
			rows.setRow(i,getRow(indexes[i]));
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(indexes.length);
			for (int i=0;i<indexes.length;i++)
				newRowNames.add(this.getRowName(indexes[i]));
			
			rows.setRowNames(newRowNames);
		}
		
		return rows;
	}
	
	public StringMatrix getRow(IntVector indexes)
	{
		StringMatrix rows = new StringMatrix(indexes.size(),dim(1));
		
		for (int i=0;i<indexes.size();i++)
			rows.setRow(i,getRow(indexes.get(i)));
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(indexes.size());
			for (int i=0;i<indexes.size();i++)
				newRowNames.add(this.getRowName(indexes.get(i)));
			
			rows.setRowNames(newRowNames);
		}
		
		return rows;
	}
	
	public StringMatrix getRow(DoubleVector indexes)
	{
		return getRow(indexes.asIntVector());
	}
	
	public StringMatrix getRow(BooleanVector bv)
	{
		if (bv.size()!=dim(0))
		{
			System.out.println("Error getRows(BooleanVector): Vector must be the same size as number of rows.");
			System.exit(0);
		}
		
		return getRow(bv.asIndexes());
	}
	
	public void Initialize(int numrows, int numcols)
	{
		data = new String[numrows][numcols];
	}
	
	public void Initialize(int numrows, int numcols, String val)
	{
		Initialize(numrows,numcols);
		
		for (int row=0;row<numrows;row++)
			for (int col=0;col<numcols;col++)
				data[row][col] = val;
	}
	
	public void set(int i, int j, String val)
	{
		data[i][j] = val;
	}
	
	public void set(int i, int j, double val)
	{
		data[i][j] = String.valueOf(val);
	}
	
	public void set(int i, int j, int val)
	{
		data[i][j] = String.valueOf(val);
	}
	
	public void set(int i, int j, float val)
	{
		data[i][j] = String.valueOf(val);
	}
	
	public void set(int i, String col, String val)
	{
		set(i, this.colnames.indexOf(col), val);
	}
	
	public void set(int i, String col, Integer val)
	{
		set(i, this.colnames.indexOf(col), val.doubleValue());
	}
	
	public void set(String row, int j, String val)
	{
		set(this.rownames.indexOf(row), j, val);
	}
	
	public void set(String row, int j, Integer val)
	{
		set(this.rownames.indexOf(row), j, val.doubleValue());
	}
	
	public void set(String row, String col, String val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val);
	}
	
	public void set(String row, String col, Integer val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val.doubleValue());
	}
	
	public void setCols(int startindex, StringMatrix dt)
	{
		//boolean needsalignment = AddColumns(dt);
		
		//if (!needsalignment)
		//{
			for (int newc=0;newc<dt.numCols();newc++)
				setCol(startindex+newc,dt.getCol(newc));
		/*}else
		{
			for (int r=0;r<dim(0);r++)
			{
				int ri = dt.getrownames().indexOf(rownames.get(r));
				
				if (ri==-1)
					for (int newc=0;newc<dt.dim(1);newc++)
						data.get(r).set(startindex+newc,Double.NaN);
				else
					for (int newc=0;newc<dt.dim(1);newc++)
						data.get(r).set(startindex+newc,dt.get(ri, newc));
			}		
		}*/
	}
	
	public void setRow(int index, StringVector vec)
	{
		if (vec.size()!=dim(1))
		{
			System.err.println("Error setRow(int, DoubleVector): Vector size must equal number of columns.");
			System.exit(0);
		}
		
		if (vec.hasListName() && this.hasRowNames())
			this.setRowName(index, vec.getListName());
		
		for (int c=0;c<vec.size();c++)
			set(index,c,vec.get(c));
	}
	
	public void setCol(int index, StringVector vec)
	{
		if (vec.size()!=dim(0))
		{
			System.err.println("Error setCol(int, DoubleVector): Vector size must equal number of rows.");
			System.err.println("Vecsize = "+vec.size()+", Numrows = "+this.numRows());
			System.exit(0);
		}
		
		if (vec.listname!=null && this.colnames!=null)
			this.setColName(index, vec.listname);
		
		for (int r=0;r<vec.size();r++)
			set(r,index,vec.get(r));
	}
	
	public int size()
	{
		return this.numRows() * this.numCols();
	}
	
	public void SortRows(int keycol)
	{
		if (this.dim(0)<=1) return;
		
		StringVector dv = this.getCol(keycol);
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(dv.size());
			for (int row=0;row<dim(0);row++)
				newRowNames.add(this.getRowName(index.get(row)));
			
			this.setRowNames(newRowNames);
		}
		
		String[][] mydata = new String[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
					
		data = mydata;
	}
	
	public void SortCols(int keyrow)
	{
		StringVector dv = this.getRow(keyrow);
		
		IntVector index = dv.sort_I();
		
		if (this.hasColNames())	this.setColNames(dv.getElementNames());

		String[][] mydata = new String[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
		
		data = mydata;
	}
	
	public int numRows()
	{
		return data.length;
	}
	
	public int numCols()
	{
		return data[0].length;
	}
	
	public StringMatrix transpose()
	{
		StringMatrix out = new StringMatrix(this.numCols(),this.numRows());
		
		for (int i=0;i<this.numRows();i++)
			for (int j=0;j<this.numCols();j++)
				out.set(j, i, this.get(i, j));
		
		if (this.hasColNames()) out.setRowNames(this.getColNames());
		if (this.hasRowNames()) out.setColNames(this.getRowNames());
		
		return out;
	}
	
	public void shuffleRows()
	{
		IntVector perm = IntVector.getScale(0, this.numRows()-1, 1).permutation();
		
		for (int i=0;i<this.numRows();i++)
			for (int j=0;j<this.numCols();j++)
				this.set(i, j, this.get(perm.get(i), j));
	}

	public static StringMatrix joinRows(StringMatrix dm1, StringMatrix dm2)
	{
		StringMatrix out = new StringMatrix(dm1.numRows()+dm2.numRows(),dm1.numCols());
		
		for (int i=0;i<dm1.numRows();i++)
			for (int j=0;j<dm1.numCols();j++)
				out.set(i, j, dm1.get(i, j));
		
		for (int i=0;i<dm2.numRows();i++)
			for (int j=0;j<dm2.numCols();j++)
				out.set(i+dm1.numRows(), j, dm2.get(i, j));
		
		if (dm1.hasColNames()) out.setColNames(dm1.getColNames());
		if (dm1.hasRowNames() && dm2.hasRowNames())
		{
			List<String> newNames = dm1.getRowNames();
			newNames.addAll(dm2.getRowNames());
			out.setRowNames(newNames);
		}
		
		return out;
	}
	
	public static StringMatrix joinRows(List<StringMatrix> dms)
	{
		int totalRows = 0;
		for (StringMatrix dm : dms)
			totalRows+=dm.numRows();
		
		StringMatrix out = new StringMatrix(totalRows,dms.get(0).numCols());
		
		int curRow = 0;
		for (int dmi=0;dmi<dms.size();dmi++)
			for (int i=0;i<dms.get(dmi).numRows();i++)
				for (int j=0;j<dms.get(0).numCols();j++)
				{
					out.set(curRow, j, dms.get(dmi).get(i, j));
					curRow++;
				}
		
		
		if (dms.get(0).hasColNames()) out.setColNames(dms.get(0).getColNames());
		
		for (StringMatrix dm : dms)
			if (!dm.hasRowNames()) return out;
		
		List<String> newNames = new ArrayList<String>(totalRows);
		
		for (StringMatrix dm : dms)
			newNames.addAll(dm.getRowNames());
		
		out.setRowNames(newNames);
		
		return out;
	}

	
	public int getAsInteger(int i, int j)
	{
		return Integer.valueOf(data[i][j]);
	}
}


