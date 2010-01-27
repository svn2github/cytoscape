package org.idekerlab.denovoplugin.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.idekerlab.denovoplugin.utilities.ByteConversion;
import org.idekerlab.denovoplugin.utilities.ThreadPriorityFactory;
import org.idekerlab.denovoplugin.utilities.text.DataFormatter;
import org.idekerlab.denovoplugin.data.matrixMath.CholeskyDecomposition;
import org.idekerlab.denovoplugin.data.matrixMath.XXTRunner;

public class DoubleMatrix extends DataMatrix {

	private double[][] data;

	public DoubleMatrix() {
		Initialize(0, 0, false, false);
	}

	public DoubleMatrix(DataMatrix dt) {
		this.data = new double[dt.numRows()][dt.numCols()];

		for (int i = 0; i < dt.numRows(); i++)
			for (int j = 0; j < dt.numCols(); j++)
				data[i][j] = dt.getAsDouble(i, j);

		if (dt.hasColNames())
			this.setColNames(new ArrayList<String>(dt.getColNames()));
		if (dt.hasRowNames())
			this.setRowNames(new ArrayList<String>(dt.getRowNames()));
	}

	public static double[][] copy(double[][] m) {
		double[][] out = new double[m.length][m[0].length];

		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				out[i][j] = m[i][j];

		return out;
	}

	/**
	 * Gets the reference to the data;
	 */
	public double[][] getData() {
		return data;
	}

	public DoubleMatrix(int rows, int cols) {
		Initialize(rows, cols, false, false);
	}

	public DoubleMatrix(int rows, int cols, double val) {
		Initialize(rows, cols, val);
	}

	public DoubleMatrix(int rows, int cols, boolean arerownames,
			boolean arecolnames) {
		Initialize(rows, cols, arerownames, arecolnames);
	}

	public DoubleMatrix(int rows, int cols, ArrayList<String> rownames,
			ArrayList<String> colnames) {
		Initialize(rows, cols);
		setRowNames(rownames);
		setColNames(colnames);
	}

	public DoubleMatrix(int[][] data) {
		if (data.length == 0)
			return;

		Initialize(data.length, data[0].length);

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				set(i, j, data[i][j]);
	}

	/**
	 * NOT a copy constructor.
	 */
	public DoubleMatrix(double[][] data) {
		this.data = data;
	}

	public DoubleMatrix(float[][] data) {
		this.data = new double[data.length][data[0].length];

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				this.data[i][j] = data[i][j];
	}

	public DoubleMatrix(byte[][] data) {
		this.data = new double[data.length][data[0].length];

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				this.data[i][j] = data[i][j];
	}

	public DoubleMatrix(String file, boolean arerownames, boolean arecolnames) {
		Load(file, arerownames, arecolnames, "\t");
	}

	public DoubleMatrix(String file, boolean arerownames, boolean arecolnames,
			String delimiter) {
		Load(file, arerownames, arecolnames, delimiter);
	}

	public static double[][] matrixFromDiag(double[] d) {
		double[][] out = new double[d.length][d.length];

		for (int i = 0; i < d.length; i++)
			for (int j = 0; j < d.length; j++)
				out[i][j] = 0;

		for (int i = 0; i < d.length; i++)
			out[i][i] = d[i];

		return out;
	}

	public static double[][] abs(double[][] data) {
		double[][] out = new double[data.length][data[0].length];

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				out[i][j] = (data[i][j] >= 0) ? data[i][j] : -data[i][j];

		return out;
	}

	public DoubleMatrix abs() {
		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (out.get(i, j) < 0)
					out.set(i, j, -this.get(i, j));

		return out;
	}

	public DoubleMatrix plus(double val) {
		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				out.set(i, j, this.get(i, j) + val);

		return out;
	}

	public DoubleMatrix plus(DoubleMatrix data2) {
		if (data2.dim(0) != dim(0) || data2.dim(1) != dim(1)) {
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}

		DoubleMatrix out = this.clone();

		for (int i = 0; i < this.dim(0); i++)
			for (int j = 0; j < this.dim(1); j++)
				out.set(i, j, this.get(i, j) + data2.get(i, j));

		return out;
	}

	public double[][] asdoubleArray() {
		double[][] da = new double[this.numRows()][this.numCols()];

		for (int i = 0; i < this.numRows(); i++)
			for (int j = 0; j < this.numCols(); j++)
				da[i][j] = data[i][j];

		return da;
	}

	public int[][] asintArray() {
		int[][] da = new int[dim(0)][dim(1)];

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				da[i][j] = (int) get(i, j);

		return da;
	}

	public DoubleVector getRow(String rowname) {
		return getRow(getRowIndex(rowname));
	}

	public DoubleMatrix getRow(StringVector names) {
		return getRow(getRowIs(names));
	}

	public DoubleVector getCol(String colname) {
		return getCol(getColIs(colname));
	}

	public DoubleMatrix getCol(StringVector names) {
		return getCol(getColIs(names));
	}

	public DoubleMatrix clone() {
		return new DoubleMatrix(this);
	}

	public int dim(int dimension) {
		if (dimension == 0)
			return data.length;

		if (data.length > 0 && dimension == 1)
			return data[0].length;

		return -1;
	}

	public void Discretize(List<Double> breaks) {
		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				for (int b = 0; b < breaks.size(); b++)
					if (b == 0 && get(i, j) <= breaks.get(0)) {
						set(i, j, 0);
						break;
					} else if (b == breaks.size() - 1
							&& get(i, j) >= breaks.get(breaks.size() - 1)) {
						set(i, j, breaks.size());
						break;
					} else if (b != 0 && get(i, j) >= breaks.get(b - 1)
							&& get(i, j) <= breaks.get(b)) {
						set(i, j, b);
						break;
					}
	}

	public DoubleMatrix divideBy(double val) {
		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				out.set(i, j, get(i, j) / val);

		return out;
	}

	public DoubleMatrix divideBy(DoubleMatrix dt) {
		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				out.set(i, j, get(i, j) / dt.get(i, j));

		return out;
	}

	public double get(int i, int j) {
		return (data[i][j]);
	}

	public int getAsInteger(int i, int j) {
		return ((int) data[i][j]);
	}

	public double getAsDouble(int i, int j) {
		return (data[i][j]);
	}

	public double get(int i, String col) {
		return (get(i, this.colnames.indexOf(col)));
	}

	public double get(String row, int j) {
		return (get(this.colnames.indexOf(row), j));
	}

	public double get(String row, String col) {
		return (get(this.colnames.indexOf(row), this.colnames.indexOf(col)));
	}

	public String getAsString(int row, int col) {
		return Double.toString(get(row, col));
	}

	public float getAsFloat(int i, int j) {
		return ((float) data[i][j]);
	}

	public DoubleVector getCol(int col) {
		double[] cola = new double[this.data.length];

		for (int r = 0; r < cola.length; r++)
			cola[r] = data[r][col];

		DoubleVector column = new DoubleVector(cola);
		LabelCol(column, col);

		return column;
	}

	public DoubleMatrix getCol(List<?> indexes) {
		DoubleMatrix cols = new DoubleMatrix(dim(0), indexes.size(), this
				.hasColNames(), this.hasRowNames());

		if (this.hasRowNames())
			cols.setRowNames(this.getRowNames());

		IntVector is = getColIs(indexes);

		if (indexes.size() > is.size()) {
			System.out
					.println("Error getCol(List<?> indexes): index value does not exist.");
			System.exit(0);
		}

		for (int i = 0; i < indexes.size(); i++)
			cols.setCol(i, getCol(is.get(i)));

		return cols;
	}

	public DoubleMatrix getCol(BooleanVector bv) {
		if (bv.size() != dim(1)) {
			System.out
					.println("Error getCols(BooleanVector): Vector must be the same size as number of columns.");
			System.exit(0);
		}

		return getCol(bv.asIndexes());
	}

	public DoubleMatrix getCol(IntVector indexes) {
		DoubleMatrix cols = new DoubleMatrix(this.numRows(), indexes.size(),
				this.hasRowNames(), this.hasColNames());

		if (rownames != null)
			cols.setRowNames(rownames);

		for (int i = 0; i < indexes.size(); i++)
			cols.setCol(i, getCol(indexes.get(i)));

		return cols;
	}

	public DoubleMatrix getCol(int[] indexes) {
		DoubleMatrix cols = new DoubleMatrix(this.numRows(), indexes.length,
				this.hasRowNames(), this.hasColNames());

		if (rownames != null)
			cols.setRowNames(rownames);

		for (int i = 0; i < indexes.length; i++)
			cols.setCol(i, getCol(indexes[i]));

		return cols;
	}

	public static double[][] getCol(double[][] x, int[] indexes) {
		if (indexes.length == 0)
			return new double[0][0];

		double[][] cols = new double[x.length][indexes.length];

		for (int j = 0; j < indexes.length; j++) {
			int indexj = indexes[j];
			for (int i = 0; i < x.length; i++)
				cols[i][j] = x[i][indexj];
		}

		return cols;
	}

	public static double[][] getRow(double[][] x, int[] indexes) {
		if (indexes.length == 0)
			return new double[0][0];

		double[][] rows = new double[indexes.length][x[0].length];

		for (int i = 0; i < indexes.length; i++) {
			int indexi = indexes[i];
			for (int j = 0; j < x[0].length; j++)
				rows[i][j] = x[indexi][j];
		}

		return rows;
	}

	public DoubleMatrix getCol(int j1, int j2) {
		return new DoubleMatrix(DoubleMatrix.getCol(data, j1, j2));
	}

	public static double[][] getCol(double[][] x, int j1, int j2) {
		if (j1 > j2)
			throw new java.lang.IllegalArgumentException("j1>j2");

		double[][] cols = new double[x.length][j2 - j1 + 1];

		for (int j = j1; j <= j2; j++)
			for (int i = 0; i < x.length; i++)
				cols[i][j - j1] = x[i][j];

		return cols;
	}

	public DoubleMatrix getColDistanceMatrix() {
		DoubleMatrix cdm = new DoubleMatrix(dim(1), dim(1), getColNames(),
				getColNames());

		for (int c1 = 0; c1 < dim(1); c1++)
			for (int c2 = c1 + 1; c2 < dim(1); c2++) {
				cdm.set(c1, c2, java.lang.Math.sqrt(getCol(c1).subtract(
						getCol(c2)).pow(2.0).sum()));
				cdm.set(c2, c1, cdm.get(c1, c2));
			}

		return cdm;
	}

	public DoubleVector getRow(int row) {
		DoubleVector arow = new DoubleVector(dim(1));

		for (int c = 0; c < this.numCols(); c++)
			arow.add(get(row, c));

		LabelRow(arow, row);

		return arow;
	}

	public DoubleMatrix getRow(List<?> indexes) {
		DoubleMatrix rows = new DoubleMatrix(indexes.size(), dim(1), this
				.hasRowNames(), this.hasColNames());

		if (this.hasColNames())
			rows.setColNames(this.getColNames());

		IntVector is = this.getRowIs(indexes);

		return getRow(is);
	}

	public DoubleMatrix getRow(IntVector indexes) {
		DoubleMatrix rows = new DoubleMatrix(indexes.size(), this.numCols());

		for (int i = 0; i < indexes.size(); i++)
			rows.setRow(i, getRow(indexes.get(i)));

		if (this.hasColNames())
			rows.setColNames(colnames);

		if (this.hasRowNames()) {
			List<String> newRowNames = new ArrayList<String>(indexes.size());
			for (int i = 0; i < indexes.size(); i++)
				newRowNames.add(this.getRowName(indexes.get(i)));

			rows.setRowNames(newRowNames);
		}

		return rows;
	}

	public DoubleMatrix getRow(DoubleVector indexes) {
		return getRow(indexes.asIntVector());
	}

	public DoubleMatrix getRow(BooleanVector bv) {
		if (bv.size() != dim(0)) {
			System.out
					.println("Error getRows(BooleanVector): Vector must be the same size as number of rows.");
			System.exit(0);
		}

		return getRow(bv.asIndexes());
	}

	public void Initialize(int numrows, int numcols) {
		data = new double[numrows][numcols];
	}

	public void Initialize(int numrows, int numcols, double val) {
		Initialize(numrows, numcols);

		for (int row = 0; row < numrows; row++)
			for (int col = 0; col < numcols; col++)
				set(row, col, val);
	}

	public boolean isNaN() {
		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (!Double.isNaN(get(i, j)))
					return false;

		return true;
	}

	public DoubleMatrix log(double base) {
		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				out.set(i, j, java.lang.Math.log(get(i, j))
						/ java.lang.Math.log(base));

		return out;
	}

	public double max() {
		double mx = get(0, 0);

		boolean found = false;

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++) {
				if (!Double.isNaN(get(i, j)) && get(i, j) > mx)
					mx = get(i, j);
				found = true;
			}

		if (!found)
			return (Double.NaN);
		else
			return (mx);
	}

	public DoubleVector maxByRow(boolean nanOk) {
		DoubleVector maxs = new DoubleVector(dim(0));

		for (int r = 0; r < dim(0); r++)
			maxs.set(r, getRow(r).max(nanOk));

		return maxs;
	}

	public double mean() {
		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (!Double.isNaN(get(i, j))) {
					sum += get(i, j);
					valcount++;
				}

		if (valcount == 0)
			return Double.NaN;

		return sum / (valcount);
	}

	public DoubleVector meanByRow() {
		return new DoubleVector(DoubleMatrix.meanByRow(this.data));
	}

	public DoubleVector meanByCol() {
		DoubleVector stds = new DoubleVector(dim(1));

		for (int c = 0; c < dim(1); c++)
			stds.set(c, getCol(c).mean());

		return stds;
	}

	public void MeanCenterRows() {
		for (int i = 0; i < dim(0); i++) {
			double mean = getRow(i).mean();

			for (int j = 0; j < dim(1); j++)
				set(i, j, get(i, j) - mean);
		}
	}

	public void MeanCenterCols() {
		for (int j = 0; j < dim(1); j++) {
			double mean = getCol(j).mean();

			for (int i = 0; i < dim(0); i++)
				set(i, j, get(i, j) - mean);
		}
	}

	public double min() {
		double mn = get(0, 0);

		boolean found = false;

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++) {
				if (!Double.isNaN(get(i, j)) && get(i, j) < mn)
					mn = get(i, j);
				found = true;
			}

		if (!found)
			return (Double.NaN);
		else
			return (mn);
	}

	public DoubleVector minByRow(boolean keepNaN) {
		DoubleVector mins = new DoubleVector(dim(0));

		for (int r = 0; r < dim(0); r++)
			mins.set(r, getRow(r).min(keepNaN));

		return mins;
	}

	public DoubleMatrix minus(DoubleMatrix dt) {
		return new DoubleMatrix(DoubleMatrix.minus(this.data, dt.data));
	}

	public static double[][] minus(double[][] x, double[][] y) {
		if (x.length != y.length || x[0].length != y[0].length)
			throw new java.lang.IllegalArgumentException(
					"Data tables must be the same dimensions.");

		double[][] out = new double[x.length][x[0].length];

		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y[0].length; j++)
				out[i][j] = x[i][j] - y[i][j];

		return out;
	}

	public void Negative() {
		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				set(i, j, -get(i, j));
	}

	public static double[][] log(double[][] m) {
		double[][] out = new double[m.length][m[0].length];

		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				out[i][j] = Math.log(m[i][j]);

		return out;
	}

	public static double[][] negative(double[][] m) {
		double[][] out = new double[m.length][m[0].length];

		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				out[i][j] = -m[i][j];

		return out;
	}

	public ZStat Normalize() {
		ZStat zs = new ZStat(mean(), this.std());

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				set(i, j, (get(i, j) - zs.getMean()) / zs.getSD());

		return zs;
	}

	public void NormalizeByRow() {
		for (int i = 0; i < dim(0); i++) {
			DoubleVector row = getRow(i);
			double mean = row.mean();
			double std = row.std();

			for (int j = 0; j < dim(1); j++)
				set(i, j, (get(i, j) - mean) / std);
		}
	}

	public void NormalizeByCol() {
		for (int c = 0; c < dim(1); c++) {
			DoubleVector col = getCol(c);
			double mean = col.mean();
			double std = col.std();

			for (int r = 0; r < dim(0); r++)
				set(r, c, (get(r, c) - mean) / std);
		}
	}

	public DoubleVector pearsonCorrelationByCol(DoubleVector v2) {
		if (dim(0) != v2.size()) {
			System.err
					.println("Error pearsonCorrelationByCol(DoubleVector): Vector size much match number of table rows.");
			System.exit(0);
		}

		DoubleVector corrs = new DoubleVector(dim(1));

		for (int c = 0; c < dim(1); c++)
			corrs.set(c, DoubleVector.pearsonCorrelation(getCol(c), v2));

		return corrs;
	}

	public DoubleVector pearsonCorrelationByRow(DoubleVector v2) {
		if (dim(1) != v2.size()) {
			System.err
					.println("Error pearsonCorrelationByRow(DoubleVector): Vector size much match number of table columns.");
			System.exit(0);
		}

		DoubleVector corrs = new DoubleVector(dim(0));

		for (int r = 0; r < dim(0); r++)
			corrs.set(r, DoubleVector.pearsonCorrelation(getRow(r), v2));

		return corrs;
	}

	public DoubleMatrix pow(double power) {
		DoubleMatrix pdt = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				pdt.set(i, j, java.lang.Math.pow(get(i, j), power));

		return pdt;
	}

	public void ReOne(double one) {
		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (get(i, j) == 1)
					set(i, j, one);
	}

	public void ReZero(double zero) {
		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (get(i, j) == 0)
					set(i, j, zero);
	}

	public void set(int i, int j, double val) {
		data[i][j] = val;
	}

	public void set(int i, int j, float val) {
		data[i][j] = val;
	}

	public void set(int i, int j, Integer val) {
		data[i][j] = val.doubleValue();
	}

	public void set(int row, int col, String val) {
		if (val.equals("Inf"))
			this.set(row, col, Double.POSITIVE_INFINITY);
		else if (val.equals("-Inf"))
			this.set(row, col, Double.NEGATIVE_INFINITY);
		else if (val.equals(""))
			this.set(row, col, Double.NaN);
		else
			this.set(row, col, Double.valueOf(val));
	}

	public void set(int i, String col, double val) {
		set(i, this.colnames.indexOf(col), val);
	}

	public void set(int i, String col, Integer val) {
		set(i, this.colnames.indexOf(col), val.doubleValue());
	}

	public void set(String row, int j, double val) {
		set(this.rownames.indexOf(row), j, val);
	}

	public void set(String row, int j, Integer val) {
		set(this.rownames.indexOf(row), j, val.doubleValue());
	}

	public void set(String row, String col, double val) {
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val);
	}

	public void set(String row, String col, Integer val) {
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val
				.doubleValue());
	}

	public void setCols(int startindex, DoubleMatrix dt) {
		// boolean needsalignment = AddColumns(dt);

		// if (!needsalignment)
		// {
		for (int newc = 0; newc < dt.dim(1); newc++)
			setCol(startindex + newc, dt.getCol(newc));
		/*
		 * }else { for (int r=0;r<dim(0);r++) { int ri =
		 * dt.getrownames().indexOf(rownames.get(r));
		 * 
		 * if (ri==-1) for (int newc=0;newc<dt.dim(1);newc++)
		 * data.get(r).set(startindex+newc,Double.NaN); else for (int
		 * newc=0;newc<dt.dim(1);newc++)
		 * data.get(r).set(startindex+newc,dt.get(ri, newc)); } }
		 */
	}

	public void setRow(int index, DoubleVector vec) {
		if (vec.size() != dim(1)) {
			System.err
					.println("Error setRow(int, DoubleVector): Vector size must equal number of columns.");
			System.exit(0);
		}

		if (vec.hasListName() && this.hasRowNames())
			this.setRowName(index, vec.getListName());

		for (int c = 0; c < vec.size(); c++)
			set(index, c, vec.get(c));
	}

	public void setRow(int index, double[] vec) {
		if (vec.length != dim(1)) {
			System.err
					.println("Error setRow(int, DoubleVector): Vector size must equal number of columns.");
			System.exit(0);
		}

		for (int c = 0; c < vec.length; c++)
			set(index, c, vec[c]);
	}

	public void setCol(int index, DoubleVector vec) {
		if (vec.size() != this.numRows())
			throw new java.lang.IllegalArgumentException(
					"Error setCol(int, DoubleVector): Vector size must equal number of rows. vec="
							+ vec.size() + ",rows=" + this.numRows());

		if (vec.listname != null && this.colnames != null)
			this.setColName(index, vec.listname);

		for (int r = 0; r < vec.size(); r++)
			set(r, index, vec.get(r));
	}

	public void setCol(int index, double[] vec) {
		if (vec.length != numRows()) {
			System.err
					.println("Error setCol(int, DoubleVector): Vector size must equal number of rows.");
			System.exit(0);
		}

		for (int r = 0; r < vec.length; r++)
			set(r, index, vec[r]);
	}

	public int size() {
		if (data.length == 0)
			return 0;

		return data.length * data[0].length;
	}

	public double std() {
		double avg = mean();

		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (!Double.isNaN(get(i, j))) {
					double diff = (get(i, j) - avg);
					sum += diff * diff;
					valcount++;
				}

		if (valcount == 0)
			return Double.NaN;

		sum = sum / (valcount - 1);

		return java.lang.Math.sqrt(sum);
	}

	public DoubleVector stdByRow() {
		DoubleVector stds = new DoubleVector(dim(0));

		for (int r = 0; r < dim(0); r++)
			stds.set(r, getRow(r).std());

		return stds;
	}

	public DoubleMatrix subtract(double val) {
		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				out.set(i, j, get(i, j) - val);

		return out;
	}

	public DoubleMatrix subtract(DoubleMatrix data2) {
		if (data2.dim(0) != dim(0) || data2.dim(1) != dim(1)) {
			System.out
					.println("Error subtract(DoubleTable): Data tables must be the same size.");
			System.exit(0);
		}

		DoubleMatrix out = this.clone();

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				out.set(i, j, get(i, j) - data2.get(i, j));

		return out;
	}

	public double sum() {
		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < dim(0); i++)
			for (int j = 0; j < dim(1); j++)
				if (!Double.isNaN(get(i, j))) {
					sum += get(i, j);
					valcount++;
				}

		if (valcount == 0)
			return Double.NaN;

		return sum;
	}

	public double sumSq() {
		return DoubleMatrix.sumSq(data);
	}

	public static double sumSq(double[][] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				sum += x[i][j] * x[i][j];

		return sum;
	}

	public DoubleVector sumByCol() {
		DoubleVector out = new DoubleVector(this.numCols(), 0);

		for (int c = 0; c < this.numCols(); c++)
			for (int r = 0; r < this.numRows(); r++)
				out.set(c, out.get(c) + this.get(r, c));

		return out;
	}

	public DoubleVector sumByCol_ignoreNaN() {
		DoubleVector out = new DoubleVector(this.numCols(), 0);

		for (int c = 0; c < this.numCols(); c++)
			for (int r = 0; r < this.numRows(); r++)
				if (!Double.isNaN(this.get(r, c)))
					out.set(c, out.get(c) + this.get(r, c));

		return out;
	}

	public DoubleVector sumSquareByCol() {
		DoubleVector out = new DoubleVector(this.numCols(), 0);

		for (int c = 0; c < this.numCols(); c++)
			for (int r = 0; r < this.numRows(); r++)
				out.set(c, out.get(c) + this.get(r, c) * this.get(r, c));

		return out;
	}

	public DoubleVector sumSquareByCol_ignoreNaN() {
		DoubleVector out = new DoubleVector(this.numCols(), 0);

		for (int c = 0; c < this.numCols(); c++)
			for (int r = 0; r < this.numRows(); r++)
				if (!Double.isNaN(this.get(r, c)))
					out.set(c, out.get(c) + this.get(r, c) * this.get(r, c));

		return out;
	}

	public void NRandomize() {
		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		for (int r = 0; r < dim(0); r++)
			for (int c = 0; c < dim(1); c++)
				set(r, c, randgen.nextGaussian());
	}

	public void Randomize() {
		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		for (int r = 0; r < dim(0); r++)
			for (int c = 0; c < dim(1); c++)
				set(r, c, randgen.nextDouble());
	}

	public void SortRows(int keycol) {
		if (this.dim(0) <= 1)
			return;

		DoubleVector dv = this.getCol(keycol);

		IntVector index = dv.sort_I();

		if (this.hasRowNames()) {
			List<String> newRowNames = new ArrayList<String>(dv.size());
			for (int row = 0; row < dim(0); row++)
				newRowNames.add(this.getRowName(index.get(row)));

			this.setRowNames(newRowNames);
		}

		double[][] mydata = new double[dim(0)][dim(1)];
		for (int row = 0; row < dim(0); row++)
			for (int col = 0; col < dim(1); col++)
				mydata[row][col] = this.get(index.get(row), col);

		data = mydata;
	}

	public void SortCols(int keyrow) {
		DoubleVector dv = this.getRow(keyrow);

		IntVector index = dv.sort_I();

		if (this.hasColNames())
			this.setColNames(dv.getElementNames());

		double[][] mydata = new double[dim(0)][dim(1)];
		for (int row = 0; row < dim(0); row++)
			for (int col = 0; col < dim(1); col++)
				mydata[row][col] = this.get(index.get(row), col);

		data = mydata;
	}

	public static double pearsonCorrelation(DoubleMatrix dt1, DoubleMatrix dt2) {
		org.apache.commons.math.stat.regression.SimpleRegression sr = new org.apache.commons.math.stat.regression.SimpleRegression();

		for (int i = 0; i < dt1.dim(0); i++)
			for (int j = 0; j < dt1.dim(1); j++)
				if (!Double.isNaN(dt1.get(i, j))
						&& !Double.isNaN(dt2.get(i, j)))
					sr.addData(dt1.get(i, j), dt2.get(i, j));

		return sr.getR();
	}

	public DoubleMatrix xTx() {
		return new DoubleMatrix(DoubleMatrix.xTx(data));
	}

	public static double[][] xTx(double[][] x) {
		double[][] out = new double[x[0].length][x[0].length];

		for (int i = 0; i < x[0].length; i++)
			for (int j = 0; j < x[0].length; j++) {
				double sum = 0;
				for (int k = 0; k < x.length; k++)
					sum += x[k][i] * x[k][j];

				out[i][j] = sum;
			}

		return out;
	}

	public DoubleMatrix xxT() {
		return new DoubleMatrix(DoubleMatrix.xxT(this.data));
	}

	public static double[][] xxT(double[][] M) {
		double[][] out = new double[M.length][M.length];

		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M.length; j++) {
				double sum = 0;
				for (int k = 0; k < M[0].length; k++)
					sum += M[j][k] * M[i][k];

				out[i][j] = sum;
			}
		}

		return out;
	}

	public DoubleMatrix xxT_MultiThreaded() {
		return new DoubleMatrix(DoubleMatrix.xxT_MultiThreaded(data));
	}

	public static double[][] xxT_MultiThreaded(double[][] data) {
		double[][] out = new double[data.length][data.length];

		ExecutorService exec = Executors
				.newCachedThreadPool(new ThreadPriorityFactory(
						Thread.MIN_PRIORITY));

		for (int i = 0; i < data.length; i++)
			exec.execute(new XXTRunner(data, out, i));

		exec.shutdown();

		try {
			exec.awaitTermination(3000000, TimeUnit.SECONDS);
			if (!exec.isTerminated())
				System.out.println("Did not fully terminate!");
		} catch (InterruptedException e) {
			System.out.println(e);
			exec.shutdownNow();
		}

		return out;
	}

	public DoubleVector xTy(DoubleVector y) {
		return new DoubleVector(DoubleMatrix.xTy(this.data, y.getData()));
	}

	public static double[] xTy(double[][] x, double[] vec) {
		double[] out = new double[x[0].length];

		for (int i = 0; i < x[0].length; i++) {
			double sum = 0;
			for (int j = 0; j < vec.length; j++)
				sum += x[j][i] * vec[j];

			out[i] = sum;
		}

		return out;
	}

	public int numRows() {
		return data.length;
	}

	public int numCols() {
		if (data.length == 0)
			return 0;
		return data[0].length;
	}

	public CholeskyDecomposition cholesky() {
		return new CholeskyDecomposition(this);
	}

	public DoubleMatrix transpose() {
		DoubleMatrix out = new DoubleMatrix(DoubleMatrix.transpose(data));

		if (this.hasColNames())
			out.setRowNames(this.getColNames());
		if (this.hasRowNames())
			out.setColNames(this.getRowNames());

		return out;
	}

	public static double[][] transpose(double[][] x) {
		double[][] out = new double[x[0].length][x.length];

		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				out[j][i] = x[i][j];

		return out;
	}


	public static void multiplyCols(double[][] x, double[] vec) {
		for (int j = 0; j < vec.length; j++)
			for (int i = 0; i < x.length; i++)
				x[i][j] *= vec[j];
	}

	public DoubleMatrix times(DoubleMatrix b) {
		return new DoubleMatrix(DoubleMatrix.times(this.data, b.getData()));
	}

	public static double[][] times(double[][] x, double[][] vec) {
		double[][] out = new double[x.length][vec[0].length];

		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < vec[0].length; j++) {
				double sum = 0;

				for (int k = 0; k < x[0].length; k++)
					sum += x[i][k] * vec[k][j];

				out[i][j] = sum;
			}

		return out;
	}

	public static double[][] timesXYT(double[][] x, double[][] y) {
		double[][] out = new double[x.length][y.length];

		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++) {
				double sum = 0;

				for (int k = 0; k < x[0].length; k++)
					sum += x[i][k] * y[j][k];

				out[i][j] = sum;
			}

		return out;
	}

	public DoubleVector times(DoubleVector v) {
		return new DoubleVector(DoubleMatrix.times(this.data, v.getData()));
	}

	public static double[] times(double[][] x, double[] vec) {
		if (vec.length != x[0].length)
			throw new java.lang.IllegalArgumentException(
					"Dimension mismatch: xcol!=vlength, " + x[0].length
							+ " != " + vec.length);

		double[] out = new double[x.length];

		for (int i = 0; i < x.length; i++) {
			double sum = 0;

			for (int j = 0; j < x[0].length; j++)
				sum += vec[j] * x[i][j];

			out[i] = sum;
		}

		return out;
	}

	public void shuffleRows() {
		IntVector perm = IntVector.getScale(0, this.numRows() - 1, 1)
				.permutation();

		for (int i = 0; i < this.numRows(); i++)
			for (int j = 0; j < this.numCols(); j++)
				this.set(i, j, this.get(perm.get(i), j));
	}

	public static DoubleMatrix joinRows(DoubleMatrix dm1, DoubleMatrix dm2) {
		DoubleMatrix out = new DoubleMatrix(dm1.numRows() + dm2.numRows(), dm1
				.numCols());

		for (int i = 0; i < dm1.numRows(); i++)
			for (int j = 0; j < dm1.numCols(); j++)
				out.set(i, j, dm1.get(i, j));

		for (int i = 0; i < dm2.numRows(); i++)
			for (int j = 0; j < dm2.numCols(); j++)
				out.set(i + dm1.numRows(), j, dm2.get(i, j));

		if (dm1.hasColNames())
			out.setColNames(dm1.getColNames());
		if (dm1.hasRowNames() && dm2.hasRowNames()) {
			List<String> newNames = dm1.getRowNames();
			newNames.addAll(dm2.getRowNames());
			out.setRowNames(newNames);
		}

		return out;
	}

	public static DoubleMatrix joinRows(List<DoubleMatrix> dms) {
		int totalRows = 0;
		for (DoubleMatrix dm : dms)
			totalRows += dm.numRows();

		DoubleMatrix out = new DoubleMatrix(totalRows, dms.get(0).numCols());

		int curRow = 0;
		for (int dmi = 0; dmi < dms.size(); dmi++)
			for (int i = 0; i < dms.get(dmi).numRows(); i++)
				for (int j = 0; j < dms.get(0).numCols(); j++) {
					out.set(curRow, j, dms.get(dmi).get(i, j));
					curRow++;
				}

		if (dms.get(0).hasColNames())
			out.setColNames(dms.get(0).getColNames());

		for (DoubleMatrix dm : dms)
			if (!dm.hasRowNames())
				return out;

		List<String> newNames = new ArrayList<String>(totalRows);

		for (DoubleMatrix dm : dms)
			newNames.addAll(dm.getRowNames());

		out.setRowNames(newNames);

		return out;
	}

	public static DoubleMatrix joinCols(DoubleMatrix dm1, DoubleMatrix dm2) {
		DoubleMatrix out = new DoubleMatrix(DoubleMatrix.joinCols(dm1.data,
				dm2.data));

		if (dm1.hasRowNames())
			out.setRowNames(dm1.getRowNames());
		if (dm1.hasColNames() && dm2.hasColNames()) {
			List<String> newNames = dm1.getColNames();
			newNames.addAll(dm2.getColNames());
			out.setColNames(newNames);
		}

		return out;
	}

	public static double[][] joinCols(double[][] dm1, double[][] dm2) {
		double[][] out = new double[dm1.length][dm1[0].length + dm2[0].length];

		for (int i = 0; i < dm1.length; i++)
			for (int j = 0; j < dm1[0].length; j++)
				out[i][j] = dm1[i][j];

		for (int i = 0; i < dm2.length; i++)
			for (int j = 0; j < dm2[0].length; j++)
				out[i][j + dm1[0].length] = dm2[i][j];

		return out;
	}

	public void centerRows() {
		for (int i = 0; i < this.numRows(); i++) {
			double mean = 0;
			for (int j = 0; j < this.numCols(); j++)
				mean += this.get(i, j) / this.numCols();

			for (int j = 0; j < this.numCols(); j++)
				this.set(i, j, this.get(i, j) - mean);
		}
	}

	public void centerCols_ignoreNaN() {
		for (int j = 0; j < this.numCols(); j++) {
			double mean = 0;
			for (int i = 0; i < this.numRows(); i++)
				if (!Double.isNaN(this.get(i, j)))
					mean += this.get(i, j) / this.numRows();

			for (int i = 0; i < this.numRows(); i++)
				this.set(i, j, this.get(i, j) - mean);
		}
	}

	public void centerCols() {
		for (int j = 0; j < this.numCols(); j++) {
			double mean = 0;
			for (int i = 0; i < this.numRows(); i++)
				mean += this.get(i, j);

			mean /= this.numRows();

			for (int i = 0; i < this.numRows(); i++)
				this.set(i, j, this.get(i, j) - mean);
		}
	}

	public static void centerCols(double[][] x) {
		for (int j = 0; j < x[0].length; j++) {
			double mean = 0;
			for (int i = 0; i < x.length; i++)
				mean += x[i][j];

			mean /= x.length;

			for (int i = 0; i < x.length; i++)
				x[i][j] -= mean;
		}
	}

	public DoubleMatrix corr() {
		DoubleMatrix out = new DoubleMatrix(this.numCols(), this.numCols());

		DoubleMatrix data = new DoubleMatrix(this);
		data.centerCols_ignoreNaN();

		DoubleVector colSums2 = data.sumSquareByCol_ignoreNaN();

		for (int i = 0; i < this.numCols(); i++)
			out.set(i, i, 1);

		for (int i = 0; i < this.numCols() - 1; i++)
			for (int j = i + 1; j < this.numCols(); j++) {
				double sum_xy = 0;

				for (int k = 0; k < data.numRows(); k++)
					if (!Double.isNaN(data.get(k, i))
							&& !Double.isNaN(data.get(k, j)))
						sum_xy += data.get(k, i) * data.get(k, j);

				double sumx2 = colSums2.get(i);
				double sumy2 = colSums2.get(j);

				double cor = (this.numCols() * sum_xy)
						/ (Math.sqrt(this.numCols() * sumx2) * Math.sqrt(this
								.numCols()
								* sumy2));

				out.set(i, j, cor);
				out.set(j, i, cor);
			}

		return out;
	}

	public static double[][] randNorm(int m, int n) {
		double[][] out = new double[m][n];

		java.util.Random randgen = new java.util.Random();
		randgen.setSeed(System.nanoTime());

		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				out[i][j] = randgen.nextGaussian();

		return out;
	}

	public static double[][] randUnif(int m, int n) {
		double[][] out = new double[m][n];

		java.util.Random randgen = new java.util.Random();
		randgen.setSeed(System.nanoTime());

		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				out[i][j] = randgen.nextDouble();

		return out;
	}

	public static double covByCol(double[][] x, int c1, int c2) {
		float cov = 0;
		for (int i = 0; i < x.length; i++)
			cov += x[i][c1] * x[i][c2];

		return cov / x.length;
	}

	public static double covByCol_IgnoreNaN(double[][] x, int c1, int c2) {
		int count = 0;
		double cov = 0;
		for (int i = 0; i < x.length; i++)
			if (!Double.isNaN(x[i][c1]) && !Double.isNaN(x[i][c2])) {
				cov += x[i][c1] * x[i][c2];
				count++;
			}

		return (count == 0) ? Double.NaN : cov / count;
	}

	public static double[][] corrSymmetric(double[][] m) {
		int mmm1 = m.length - 1;

		double[] means = DoubleMatrix.meanByCol(m);
		double[] sd = DoubleMatrix.stdByCol(m, means);

		double[][] m2 = new double[m.length][m.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m.length; j++)
				m2[i][j] = m[i][j] - means[j];

		double[][] out = new double[m.length][m.length];

		for (int i = 0; i < m.length; i++)
			out[i][i] = 1;

		for (int i = 0; i < mmm1; i++)
			for (int j = i + 1; j < m.length; j++) {
				double cor = covByCol(m2, i, j) / (sd[i] * sd[j]);
				out[i][j] = cor;
				out[j][i] = cor;
			}

		return out;
	}

	public static double[][] corrSymmetric_IgnoreNaN(double[][] m) {
		int mmm1 = m.length - 1;

		double[] means = DoubleMatrix.meanByCol_IgnoreNaN(m);
		double[] sd = DoubleMatrix.stdByCol_IgnoreNaN(m, means);

		double[][] m2 = new double[m.length][m.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m.length; j++)
				m2[i][j] = m[i][j] - means[j];

		double[][] out = new double[m.length][m.length];

		for (int i = 0; i < m.length; i++)
			out[i][i] = 1;

		int count = 0;
		int total = m.length * (m.length - 1) / 2;
		for (int i = 0; i < mmm1; i++)
			for (int j = i + 1; j < m.length; j++) {
				if (count % 1000000 == 0)
					System.out.println(DataFormatter.printRatio(
							count, total));

				double cor = covByCol_IgnoreNaN(m2, i, j) / (sd[i] * sd[j]);
				out[i][j] = cor;
				out[j][i] = cor;
				count++;
			}

		return out;
	}

	public static double[] meanByRow(double[][] m) {
		double[] means = new double[m.length];

		for (int r = 0; r < m.length; r++) {
			double mean = 0;
			for (int c = 0; c < m[0].length; c++)
				mean += m[r][c];

			means[r] = mean / m[0].length;
		}

		return means;
	}

	public static double[] meanByCol(double[][] m) {
		double[] means = new double[m[0].length];

		for (int c = 0; c < m[0].length; c++) {
			float mean = 0;
			for (int r = 0; r < m.length; r++)
				mean += m[r][c];

			means[c] = mean / m.length;
		}

		return means;
	}

	public static double[] meanByCol_IgnoreNaN(double[][] m) {
		double[] means = new double[m[0].length];

		for (int c = 0; c < m[0].length; c++) {
			int count = 0;
			float mean = 0;
			for (int r = 0; r < m.length; r++) {
				if (!Double.isNaN(m[r][c])) {
					mean += m[r][c];
					count++;
				}

			}

			means[c] = (count == 0) ? Double.NaN : mean / count;
		}

		return means;
	}

	public static double[] meanByRow_IgnoreNaN(double[][] m) {
		double[] means = new double[m.length];

		for (int r = 0; r < m.length; r++) {
			int count = 0;
			float mean = 0;
			for (int c = 0; c < m[0].length; c++) {
				if (!Double.isNaN(m[r][c])) {
					mean += m[r][c];
					count++;
				}

			}

			means[r] = (count == 0) ? Double.NaN : mean / count;

		}

		return means;
	}

	public static double[] stdByRow(double[][] m, double[] means) {
		double[] stds = new double[m.length];

		for (int r = 0; r < m.length; r++) {
			double sum = 0;
			for (int c = 0; c < m[0].length; c++) {
				double diff = m[r][c] - means[r];
				sum += diff * diff;
			}

			stds[r] = Math.sqrt(sum / (m[0].length));
		}

		return stds;
	}

	public static double[] stdByRow_IgnoreNaN(double[][] m, double[] means) {
		double[] stds = new double[m.length];

		for (int r = 0; r < m.length; r++) {
			int count = 0;
			float sum = 0;
			for (int c = 0; c < m[0].length; c++)
				if (!Double.isNaN(m[r][c])) {
					double diff = m[r][c] - means[c];
					sum += diff * diff;
					count++;
				}

			stds[r] = (count == 0) ? Double.NaN : Math.sqrt(sum / count);

		}

		return stds;
	}

	public static double[] stdByCol(double[][] m, double[] means) {
		double[] stds = new double[m[0].length];

		for (int c = 0; c < m[0].length; c++) {
			double sum = 0;
			for (int r = 0; r < m.length; r++) {
				double diff = m[r][c] - means[c];
				sum += diff * diff;
			}

			stds[c] = Math.sqrt(sum / (m.length));
		}

		return stds;
	}

	public static double[] stdByCol_IgnoreNaN(double[][] m, double[] means) {
		double[] stds = new double[m[0].length];

		for (int c = 0; c < m[0].length; c++) {
			int count = 0;
			double sum = 0;
			for (int r = 0; r < m.length; r++)
				if (!Double.isNaN(m[r][c])) {
					double diff = m[r][c] - means[c];
					sum += diff * diff;
					count++;
				}

			stds[c] = (count == 0) ? Double.NaN : Math.sqrt(sum / count);

		}

		return stds;
	}

	public static DoubleMatrix loadFromByte(String file) {
		DoubleMatrix out = null;

		try {
			BufferedInputStream bos = new BufferedInputStream(
					new FileInputStream(file));

			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConversion.toInt(f4);
			bos.read(f4);
			int numCols = ByteConversion.toInt(f4);

			out = new DoubleMatrix(numRows, numCols);

			byte[] f8 = new byte[8];

			for (int i = 0; i < numRows; i++)
				for (int j = 0; j < numCols; j++) {
					bos.read(f8);
					out.set(i, j, ByteConversion.toDouble(f8));
				}

			bos.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return out;

	}

	/**
	 * Ignores row and column names
	 * 
	 * @param file
	 */
	public void saveAsByte(String file) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file, false));

			bos.write(ByteConversion.toByta(this.numRows()));
			bos.write(ByteConversion.toByta(this.numCols()));

			for (int i = 0; i < this.numRows(); i++)
				for (int j = 0; j < this.numCols(); j++)
					bos.write(ByteConversion.toByta(data[i][j]));

			bos.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public static double[][] getRowCol(double[][] x, int[] rows, int[] cols) {
		if (rows.length == 0 || cols.length == 0)
			return new double[0][0];

		double[][] out = new double[rows.length][cols.length];

		for (int i = 0; i < rows.length; i++)
			for (int j = 0; j < cols.length; j++)
				out[i][j] = x[rows[i]][cols[j]];

		return out;
	}

	public static DoubleMatrix loadFromFloatByte(String file) {
		try {
			BufferedInputStream bos = new BufferedInputStream(
					new FileInputStream(file));

			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConversion.toInt(f4);
			bos.read(f4);
			int numCols = ByteConversion.toInt(f4);

			double[][] out = new double[numRows][numCols];

			for (int i = 0; i < numRows; i++)
				for (int j = 0; j < numCols; j++) {
					bos.read(f4);
					out[i][j] = ByteConversion.toFloat(f4);
				}

			bos.close();

			return new DoubleMatrix(out);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public static void fill(double[][] m, double val) {
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				m[i][j] = val;
	}

	public static void convertToEmpirical(double[][] data) {
		DoubleVector dist = new DoubleVector(data.length * (data.length - 1)
				/ 2);

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				if (!Double.isNaN(data[i][j]))
					dist.add(data[i][j]);

		double[] distSorted = dist.sort().getData();

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				if (!Double.isNaN(data[i][j]))
					data[i][j] = (distSorted.length - DoubleVector
							.bindarySearchFirstIndex(distSorted, data[i][j]))
							/ (double) distSorted.length;
	}

	public static boolean[][] sign(double[][] m) {
		boolean[][] out = new boolean[m.length][m[0].length];

		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				out[i][j] = m[i][j] > 0;

		return out;
	}
}
