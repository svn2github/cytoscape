package org.idekerlab.denovoplugin.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.idekerlab.denovoplugin.utilities.files.FileIterator;


public class DoubleVector extends DataVector {

	private double[] data;
	private int size;

	public DoubleVector() {
		Initialize(0);
	}

	public DoubleVector(double[] data) {
		this.data = data;
		size = data.length;
	}

	public DoubleVector(float[] data) {
		this.data = new double[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = data[i];

		size = data.length;
	}

	public DoubleVector(short[] data) {
		this.data = new double[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = data[i];

		size = data.length;
	}

	public DoubleVector(int[] data) {
		this.data = new double[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = data[i];

		size = data.length;
	}

	public DoubleVector(String[] data) {
		this.data = new double[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = Double.valueOf(data[i]);

		size = data.length;
	}

	public DoubleVector(List<?> vals) {
		Initialize(vals.size());

		if (vals.size() == 0)
			return;

		if (vals.get(0) instanceof String) {
			for (int i = 0; i < vals.size(); i++)
				this.add(Double.valueOf((String) vals.get(i)));
		}

		if (vals.get(0) instanceof Double) {
			for (int i = 0; i < vals.size(); i++)
				this.add((Double) vals.get(i));
		}
	}

	public DoubleVector(Collection<Double> vals) {
		Initialize(vals.size());

		if (vals.size() == 0)
			return;

		for (Double d : vals)
			this.add(d);
	}

	public DoubleVector(IntVector ail) {
		Initialize(ail.size());

		for (int i = 0; i < ail.size(); i++)
			this.add(ail.get(i));
	}

	public DoubleVector(int size, List<String> elementnames, String listname) {
		Initialize(size);

		setElementNames(elementnames);

		setListName(listname);
	}

	public DoubleVector(int size) {
		Initialize(size);
	}

	public DoubleVector(int size, double vals) {
		Initialize(size, vals);
	}

	public DoubleVector(StringVector sv) {
		Initialize(sv.size());

		if (sv.hasElementNames())
			setElementNames(sv.getElementNames());
		if (sv.hasListName())
			setListName(sv.getListName());

		for (int i = 0; i < sv.size(); i++)
			add(Double.valueOf(sv.get(i)));
	}

	public DoubleVector(String file) {
		LoadColumn(file, false, false, 0);
	}

	public DoubleVector(String file, boolean arerownames, boolean arecolname) {
		LoadColumn(file, arerownames, arecolname, 0);
	}

	public DoubleVector(String file, boolean arerownames, boolean arecolname,
			int column) {
		LoadColumn(file, arerownames, arecolname, column);
	}

	public static DoubleVector getScale(double low, double high, double interval) {
		int length = (new Double((high - low) / interval)).intValue() + 1;

		DoubleVector scale = new DoubleVector(length);

		scale.add(low);

		for (int i = 1; i < length; i++)
			scale.add(low + i * interval);

		return scale;
	}

	public void Initialize(int size) {
		data = new double[size];
		this.size = 0;
	}

	public void Initialize(int count, double val) {
		data = new double[count];

		for (int i = 0; i < count; i++)
			add(val);

		size = count;
	}

	public Object getDataAsObject() {
		return data;
	}

	/**
	 * Often gets the reference to the actual data.
	 */
	public double[] getData() {
		if (size == data.length)
			return data;
		else
			return DoubleVector.resize(data, this.size);
	}

	public static double[] resize(double[] vec, int size) {
		double[] out = new double[size];

		int n = Math.min(vec.length, size);
		for (int i = 0; i < n; i++)
			out[i] = vec[i];

		return out;
	}

	public synchronized void add(double o) {
		if (data.length == 0)
			data = new double[10];
		else if (this.size == data.length)
			data = DoubleVector.resize(data, data.length * 2);

		data[size] = o;
		size++;
	}

	public synchronized void add(String val) {
		this.add(Double.valueOf(val));
	}

	public synchronized void addAll(Collection<Double> vals) {
		if (data.length < this.size + vals.size())
			data = DoubleVector.resize(data, data.length + vals.size());

		for (Double d : vals)
			this.add(d);
	}

	public synchronized void addAll(DoubleVector vals) {
		if (data.length < this.size + vals.size())
			data = DoubleVector.resize(data, data.length + vals.size());

		for (int i = 0; i < vals.size; i++)
			this.add(vals.get(i));
	}

	public void addFromFile(String fileName, boolean arerownames,
			boolean arecolumnnames) {
		DoubleVector other = new DoubleVector(fileName, arerownames,
				arecolumnnames);
		this.addAll(other);
	}

	public void add(double o, String name) {
		this.add(o);
		addElementName(name);
	}

	public Object getAsObject(int i) {
		return data[i];
	}

	public String getAsString(int i) {
		return Double.toString(data[i]);
	}

	public double getAsDouble(int i) {
		return get(i);
	}

	public boolean getAsBoolean(int i) {
		return get(i) == 1;
	}

	public byte getAsByte(int i) {
		return new Double(get(i)).byteValue();
	}

	public int getAsInteger(int i) {
		return (int) get(i);
	}

	public float getAsFloat(int i) {
		return (float) get(i);
	}

	public double get(int i) {
		return (data[i]);
	}

	public double get(String element) {
		return data[getElementNames().indexOf(element)];
	}

	public void set(int i, double val) {
		data[i] = val;
	}

	public void set(int i, Integer val) {
		data[i] = val;
	}

	public void set(List<Integer> indices, double val) {
		for (Integer index : indices)
			data[index] = val;
	}

	public void set(String element, double val) {
		data[getElementNames().indexOf(element)] = val;
	}

	public void set(int i, String val) {
		if (val.equals("Inf"))
			data[i] = Double.POSITIVE_INFINITY;
		else if (val.equals("-Inf"))
			data[i] = Double.NEGATIVE_INFINITY;
		else
			data[i] = Double.valueOf(val);
	}

	public double getEmpiricalPvalue(double score, boolean upperTail) {
		if (this.size() == 0)
			return Double.NaN;

		int gtoe;

		if (upperTail)
			gtoe = this.greaterThanOrEqual(score).sum();
		else
			gtoe = this.lessThanOrEqual(score).sum();

		double pval = (double) gtoe / this.size();

		double min = 1.0 / this.size();
		if (pval < min)
			pval = min;

		return pval;
	}

	public double getEmpiricalValueFromSortedDist(double score) {
		int count;
		for (count = 0; count < this.size(); count++) {
			if (score < this.get(count))
				break;
		}
		return (count == (this.size()) ? (1 / (double) this.size())
				: (1.0 - ((double) (count) / this.size())));
	}

	public DoubleVector clone() {
		DoubleVector copy = new DoubleVector(DoubleVector.copy(data));
		copy.size = this.size;

		if (this.hasListName())
			copy.setListName(this.getListName());
		if (this.hasElementNames())
			copy.setElementNames(this.getElementNames());

		return (copy);
	}

	public int size() {
		return this.size;
	}

	public DoubleVector reZero(double zero) {
		DoubleVector out = this.clone();

		for (int i = 0; i < this.size(); i++)
			if (out.get(i) == 0)
				out.set(i, zero);

		return out;
	}

	public DoubleVector reOne(double one) {
		DoubleVector out = this.clone();

		for (int i = 0; i < this.size(); i++)
			if (out.get(i) == 1)
				out.set(i, one);

		return out;
	}

	public DoubleVector subtract(DoubleVector data2) {
		DoubleVector out = this.clone();

		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		for (int i = 0; i < this.size(); i++)
			out.set(i, out.get(i) - data2.get(i));

		return out;
	}

	public DoubleVector plus(DoubleVector data2) {
		return new DoubleVector(DoubleVector.plus(data, data2.data));
	}

	public static double[] plus(double[] x, double[] vec) {
		double[] out = new double[x.length];

		for (int i = 0; i < x.length; i++)
			out[i] = x[i] + vec[i];

		return out;
	}

	public DoubleVector average(DoubleVector other) {
		DoubleVector out = this.plus(other);
		return out.divideBy(2);
	}

	public DoubleVector minus(DoubleVector data2) {
		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		DoubleVector out = this.clone();

		for (int i = 0; i < this.size(); i++)
			out.set(i, out.get(i) - data2.get(i));

		return out;
	}

	public double max() {
		return max(true);
	}

	public double max(boolean keepNaN) {
		return DoubleVector.max(this.data, keepNaN);
	}

	public static double max(double[] data, boolean keepNaN) {
		if (data.length == 0)
			return Double.NaN;

		if (keepNaN) {
			double max = data[0];
			for (int i = 0; i < data.length; i++)
				if (data[i] > max)
					max = data[i];
			return max;
		} else {
			double max = data[0];
			for (int i = 0; i < data.length; i++)
				if (data[i] > max || Double.isNaN(max))
					max = data[i];
			return max;
		}
	}

	public int maxI() {
		return DoubleVector.maxI(this.data);
	}

	public static int maxI(double[] data) {
		double max = data[0];
		int index = 0;

		for (int i = 0; i < data.length; i++) {
			if (!Double.isNaN(data[i]) && data[i] > max) {
				max = data[i];
				index = i;
			}
		}

		return index;
	}

	public BooleanVector NaNs() {
		BooleanVector out = new BooleanVector(this.size());
		for (int i = 0; i < size(); i++)
			if (Double.isNaN(get(i)))
				out.add(true);
			else
				out.add(false);

		return out;
	}

	public List<Integer> NaN_indices() {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < this.size(); i++)
			if (Double.isNaN(data[i]))
				indices.add(i);

		return indices;
	}

	public IntVector maxIs(int num) {
		IntVector maxIs = new IntVector(num);

		IntVector order = this.sort_I();

		for (int i = 0; i < num; i++)
			maxIs.add(order.get(order.size() - i - 1));

		return maxIs;
	}

	public IntVector minIs(int num) {
		IntVector minIs = new IntVector(num);

		IntVector order = this.sort_I();

		for (int i = 0; i < num; i++)
			minIs.add(order.get(i));

		return minIs;
	}

	public int minI() {
		return DoubleVector.minI(this.data);
	}

	public static int minI(double[] data) {
		double min = data[0];
		int index = 0;

		for (int i = 0; i < data.length; i++) {
			if (!Double.isNaN(data[i]) && data[i] < min) {
				min = data[i];
				index = i;
			}
		}

		return index;
	}

	public double min() {
		return min(true);
	}

	public double min(boolean keepNaN) {
		if (size() == 0)
			return Double.NaN;

		if (keepNaN) {
			double min = data[0];
			for (int i = 0; i < this.size(); i++)
				if (data[i] < min)
					min = data[i];
			return (min);
		} else {
			DoubleVector newvec = this.get(this.NaNs().not());
			return newvec.min(true);
		}
	}

	public DoubleVector abs() {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			if (out.get(i) < 0)
				out.set(i, -out.get(i));

		return out;
	}

	public DoubleVector log(double base) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, java.lang.Math.log(out.get(i))
					/ java.lang.Math.log(base));

		return out;
	}

	public DoubleVector log() {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, Math.log(out.get(i)));

		return out;
	}

	public DoubleVector negative() {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, -out.get(i));

		return out;
	}

	public DoubleVector plus(double val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) + val);

		return out;
	}

	public DoubleVector minus(double val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) - val);

		return out;
	}

	public DoubleVector subtract(double val) {
		DoubleVector s = this.clone();

		for (int i = 0; i < size(); i++)
			s.set(i, s.get(i) - val);

		return s;
	}

	public DoubleVector divideBy(DoubleVector val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val.get(i));

		return out;
	}

	public DoubleVector divideBy(double val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val);

		return out;
	}

	public DoubleVector divideBy(int val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val);

		return out;
	}

	public DoubleVector times(DoubleVector val) {
		return new DoubleVector(DoubleVector.times(data, val.data));
	}

	public static double[] times(double[] x, double[] vec) {
		double[] out = new double[x.length];

		for (int i = 0; i < x.length; i++)
			out[i] = x[i] * vec[i];

		return out;
	}

	public DoubleVector times(double val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val);

		return out;
	}

	public DoubleVector times(int val) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val);

		return out;
	}

	public BooleanVector isNaN() {
		BooleanVector bv = new BooleanVector(this.size());

		for (int i = 0; i < size(); i++)
			bv.add(Double.isNaN(data[i]));

		return bv;
	}

	public BooleanVector notNaN() {
		BooleanVector bv = new BooleanVector(this.size());

		for (int i = 0; i < size(); i++)
			bv.add(!Double.isNaN(data[i]));

		return bv;
	}

	public DoubleVector normalize() {
		DoubleVector out = this.clone();

		double mn = this.mean();
		double sd = this.std();

		for (int i = 0; i < this.size(); i++)
			out.set(i, (out.get(i) - mn) / sd);

		return out;
	}

	public DoubleVector center() {
		DoubleVector out = this.clone();

		double mn = this.mean();

		for (int i = 0; i < this.size(); i++)
			out.set(i, (out.get(i) - mn));

		return out;
	}

	public ZStat Normalize() {
		ZStat zs = new ZStat(this.mean(), this.std());

		for (int i = 0; i < this.size(); i++)
			set(i, (get(i) - zs.getMean()) / zs.getSD());

		return zs;
	}

	public void UnNormalize(ZStat zs) {
		for (int i = 0; i < this.size(); i++)
			set(i, (get(i) * zs.getSD()) + zs.getMean());
	}

	public static final double pearsonCorrelation(DoubleVector v1,
			DoubleVector v2) {
		if (v1.size() != v2.size())
			throw (new java.lang.IllegalArgumentException(
					"Vectors must be the same size. " + v1.size() + "!="
							+ v2.size()));

		v1 = v1.center();
		v2 = v2.center();

		double sum_xy = 0;

		for (int i = 0; i < v1.size(); i++)
			sum_xy += v1.get(i) * v2.get(i);

		// double sumx = v1.sum();
		// double sumy = v2.sum();
		double sumx2 = v1.squaredSum();
		double sumy2 = v2.squaredSum();

		// return (v1.size()*sum_xy-sumx*sumy)/( Math.sqrt(
		// v1.size()*sumx2-sumx*sumx )*Math.sqrt( v1.size()*sumy2-sumy*sumy ) );
		return (v1.size() * sum_xy)
				/ (Math.sqrt(v1.size() * sumx2) * Math.sqrt(v1.size() * sumy2));
	}

	public double mean() {
		return DoubleVector.mean(data);
	}

	public static double mean(double[] data) {
		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < data.length; i++)
			if (!Double.isNaN(data[i])) {
				sum += data[i];
				valcount++;
			}

		if (valcount == 0)
			return Double.NaN;

		return sum / (valcount);
	}

	public double median() {
		if (this.size == 0)
			return Double.NaN;

		DoubleVector sorted = this.sort();
		int medianIndex = this.size() / 2;

		if (this.size() % 2 != 0)
			return sorted.get(medianIndex);
		else
			return (((sorted.get(medianIndex)) + (sorted.get(medianIndex - 1))) / 2);
	}

	public double std() {
		return DoubleVector.std(data);
	}

	public static double std(double[] data) {
		double avg = DoubleVector.mean(data);

		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < data.length; i++)
			if (!Double.isNaN(data[i])) {
				double diff = data[i] - avg;
				sum += diff * diff;
				valcount++;
			}

		if (valcount == 0)
			return Double.NaN;

		sum = sum / (valcount - 1);

		return Math.sqrt(sum);
	}

	public ZStat zstat() {
		double avg = mean();

		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Double.isNaN(get(i))) {
				sum += java.lang.Math.pow((get(i) - avg), 2);
				valcount++;
			}

		if (valcount == 0)
			return new ZStat(Double.NaN, Double.NaN);

		sum = sum / (valcount - 1);

		return new ZStat(avg, java.lang.Math.pow(sum, .5));
	}

	public double sum() {
		return DoubleVector.sum(data);
	}

	public static double sum(double[] x) {
		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < x.length; i++)
			if (!Double.isNaN(x[i])) {
				sum += x[i];
				valcount++;
			}

		if (valcount == 0)
			return Double.NaN;

		return sum;
	}

	public double product() {
		double output = 1;
		for (int i = 0; i < this.size(); i++)
			output *= (data[i]);

		return output;
	}

	public static DoubleVector join(DoubleVector dt1, DoubleVector dt2) {
		int newsize = dt1.size() + dt2.size();

		DoubleVector dtout = new DoubleVector();
		dtout.Initialize(newsize, 0);

		if (dt1.hasElementNames() && dt2.hasElementNames()) {
			List<String> enames = dt1.getElementNames();
			enames.addAll(dt2.getElementNames());
			dtout.setElementNames(enames);
		}

		if (dt1.hasListName())
			dtout.setListName(dt1.getListName());

		for (int i = 0; i < dt1.size(); i++)
			dtout.set(i, dt1.get(i));

		for (int i = 0; i < dt2.size(); i++)
			dtout.set(i + dt1.size(), dt2.get(i));

		return dtout;
	}

	public static DoubleVector joinAll(List<DoubleVector> dtlist) {
		if (dtlist.size() == 0)
			return null;
		if (dtlist.size() == 1)
			return dtlist.get(0);

		DoubleVector dtout = join(dtlist.get(0), dtlist.get(1));

		for (int i = 2; i < dtlist.size(); i++)
			dtout = join(dtout, dtlist.get(i));

		return dtout;
	}

	public DoubleVector Discretize(List<Double> breaks) {
		DoubleVector out = this.clone();

		for (int i = 0; i < size(); i++)
			for (int b = 0; b < breaks.size(); b++)
				if (b == 0 && get(i) <= breaks.get(0)) {
					out.set(i, 0.0);
					break;
				} else if (b == breaks.size() - 1
						&& get(i) >= breaks.get(breaks.size() - 1)) {
					out.set(i, breaks.size());
					break;
				} else if (b != 0 && get(i) >= breaks.get(b - 1)
						&& get(i) <= breaks.get(b)) {
					out.set(i, b);
					break;
				}

		return out;
	}

	public DoubleVector pow(double power) {
		DoubleVector pdt = this.clone();

		for (int i = 0; i < this.size(); i++)
			pdt.set(i, java.lang.Math.pow(get(i), power));

		return pdt;
	}

	public DoubleVector sample(int samplesize, boolean replace) {
		DoubleVector mysample = new DoubleVector(samplesize);

		DoubleVector cp = this.clone();

		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		if (!replace) {
			int lsizem1 = cp.size() - 1;

			for (int i = 0; i < samplesize; i++) {
				int swapi = lsizem1 - randgen.nextInt(cp.size() - i);
				double temp = cp.get(i);
				cp.set(i, cp.get(swapi));
				cp.set(swapi, temp);
			}

			return cp.subVector(0, samplesize);
		} else {
			for (int r = 0; r < samplesize; r++) {
				int rand = randgen.nextInt(cp.size());
				mysample.add(cp.get(rand));
			}
		}

		return mysample;
	}

	public DoubleVector subVector(int i1, int size) {
		DoubleVector out = new DoubleVector(size);

		int i1s = i1 + size;

		for (int i = i1; i < i1s; i++)
			out.add(data[i]);

		return out;
	}

	public DoubleVector get(List<?> indexes) {
		DoubleVector sub = new DoubleVector(indexes.size());

		for (int i = 0; i < indexes.size(); i++)
			sub.add(data[((Double) (indexes.get(i))).intValue()]);

		return sub;
	}

	public DoubleVector get(int[] indexes) {
		DoubleVector sub = new DoubleVector(indexes.length);

		for (int i = 0; i < indexes.length; i++)
			sub.add(data[indexes[i]]);

		return sub;
	}

	public DoubleVector get(BooleanVector bv) {
		if (bv.size() != this.size()) {
			System.err
					.println("Error DoubleVector.get(BooleanVector): The two vectors must be the same size.");
			System.err.println("this.size = " + this.size() + ", bvsize = "
					+ bv.size());

			System.exit(0);
		}

		DoubleVector sub = new DoubleVector();

		boolean found = false;
		if (this.elementnames != null) {
			sub.setElementNames(new ArrayList<String>());

			for (int i = 0; i < size(); i++)
				if (bv.get(i)) {
					sub.add(data[i], this.getElementName(i));
					found = true;
				}
		} else {
			for (int i = 0; i < size(); i++)
				if (bv.get(i)) {
					sub.add(data[i]);
					found = true;
				}
		}

		if (!found)
			sub.removeElementNames();

		return sub;
	}

	public DoubleVector not(int fullnum) {
		DoubleVector notthis = new DoubleVector();

		for (int i = 0; i < fullnum; i++)
			if (!this.contains(i))
				notthis.add(i);

		return notthis;
	}

	public boolean contains(int val) {
		for (int i = 0; i < this.size; i++)
			if (this.data[i] == val)
				return true;

		return false;
	}

	public void NRandomize() {
		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		for (int i = 0; i < size(); i++)
			set(i, randgen.nextGaussian());
	}

	public static double diffSum(DoubleVector v1, DoubleVector v2) {
		if (v1.size() != v2.size()) {
			System.out.println("diffSum Error: v1, v2 are not the same size.");
			System.exit(0);
		}

		double ds = 0.0;

		for (int i = 0; i < v1.size(); i++)
			ds += java.lang.Math.abs(v1.get(i) - v2.get(i));

		return ds;
	}

	public static BooleanVector difference(DoubleVector v1, DoubleVector v2) {
		if (v1.size() != v2.size()) {
			System.out
					.println("The two double vectors must be of the same size to perform this operation");
			System.exit(0);
		}

		BooleanVector diff = new BooleanVector(v1.size());
		diff.Initialize(v1.size(), true);

		for (int i = 0; i < v1.size(); i++)
			if (v1.get(i) != v2.get(i))
				diff.set(i, true);
			else
				diff.set(i, false);

		return diff;
	}

	public static DoubleVector seq(double min, double max, double step) {
		DoubleVector out = new DoubleVector();
		for (double i = min; i <= max; i += step)
			out.add(i);

		return out;
	}

	public double sampleRandom() {
		Random rand = new Random(System.currentTimeMillis());
		return this.get(rand.nextInt(this.size()));
	}

	public static BooleanVector similarity(DoubleVector v1, DoubleVector v2) {
		if (v1.size() != v2.size()) {
			System.out
					.println("The two double vectors must be of the same size to perform this operation");
			System.exit(0);
		}

		BooleanVector similar = new BooleanVector(v1.size());

		for (int i = 0; i < v1.size(); i++)
			if (v1.get(i) != v2.get(i))
				similar.set(i, false);
			else
				similar.set(i, true);

		return similar;
	}

	public static DoubleVector nRandoms(int num) {
		DoubleVector out = new DoubleVector(num);
		for (int i = 0; i < num; i++)
			out.add(Math.random());

		return out;
	}

	public static DoubleVector repeat(int num, double val) {
		DoubleVector out = new DoubleVector(num);

		for (int i = 0; i < num; i++)
			out.add(val);

		return out;
	}

	public double squaredMean() {
		return this.squaredSum() / this.size();
	}

	public double squaredSum() {
		double sum = 0;
		for (int i = 0; i < this.size(); i++)
			sum += data[i] * data[i];

		return sum;
	}

	public DoubleVector diff1() {
		DoubleVector out = this.clone();

		out.set(0, get(1) - get(0));

		for (int i = 1; i < size() - 1; i++)
			out.set(i, (get(i + 1) - get(i - 1)) / 2);

		out.set(out.size() - 1, get(out.size() - 1) - get(out.size() - 2));

		return out;
	}

	public DoubleVector diffLeft() {
		DoubleVector out = new DoubleVector(size());

		for (int i = 1; i < size(); i++)
			out.add(get(i) - get(i - 1));

		out.set(0, 0.0);

		return out;
	}

	public DoubleVector diffCenter() {
		int newsize = this.size() - 1;
		DoubleVector out = new DoubleVector(newsize);

		for (int i = 0; i < newsize; i++)
			out.add(get(i + 1) - get(i));

		return out;
	}

	public DoubleVector diffRight() {
		int newsize = this.size() - 1;
		DoubleVector out = new DoubleVector(newsize);

		for (int i = 0; i < newsize; i++)
			out.add(get(i + 1) - get(i));

		out.set(size() - 1, 0.0);

		return out;
	}

	public DoubleVector shift(int dist) {
		DoubleVector out = new DoubleVector(size());

		if (dist > 0)
			for (int i = dist; i < size(); i++)
				out.add(get(i - dist));
		else if (dist < 0)
			for (int i = 0; i < size() + dist; i++)
				out.add(get(i - dist));
		else if (dist == 0)
			out = this.clone();

		return out;
	}


	public DoubleVector real() {
		DoubleVector out = new DoubleVector(this.size);

		for (int i = 0; i < this.size; i++)
			if (!Double.isInfinite(data[i]) && !Double.isNaN(data[i]))
				out.add(data[i]);

		return out;
	}

	public BooleanVector isReal() {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(!(Double.isNaN(get(i)) || Double.isInfinite(get(i))));

		return out;
	}

	public DoubleVector sort() {
		DoubleVector out;

		if (!this.hasElementNames()) {
			out = this.clone();
			double[] mydata = out.asDoubleArray();
			Arrays.sort(mydata);
			out = new DoubleVector(mydata);
		} else {
			out = new DoubleVector(this.size());
			if (this.hasListName())
				out.setListName(this.listname);

			IntVector sorti = this.sort_I();

			List<String> rownames = new ArrayList<String>(this.size());
			for (int i = 0; i < sorti.size(); i++) {
				out.add(this.get(sorti.get(i)));
				rownames.add(this.getElementName(sorti.get(i)));
			}
			out.elementnames = rownames;
		}

		return out;
	}

	public IntVector sort_I() {
		return Sorter.Sort_I(this.clone());
	}

	public void set(double[] darray) {
		this.data = DoubleVector.copy(darray);
		this.size = darray.length;
	}

	public void set(BooleanVector bv, double val) {
		for (int i = 0; i < size(); i++)
			if (bv.get(i))
				set(i, val);
	}

	public void set(IntVector indexes, DoubleVector vals) {
		vals = vals.clone();

		for (int i = 0; i < indexes.size(); i++)
			this.set(indexes.get(i), vals.get(i));
	}

	/**
	 * Replaces all entries of a value in the vector with a new value.
	 */
	public void replace(double oldval, double newval) {
		if (Double.isNaN(oldval)) {
			for (int i = 0; i < size(); i++)
				if (Double.isNaN(get(i)))
					set(i, newval);
		} else {
			for (int i = 0; i < size(); i++)
				if (get(i) == oldval)
					set(i, newval);
		}
	}

	/***
	 * Generates a permuted copy of this vector.
	 */
	public DoubleVector permutation() {
		DoubleVector perm = this.clone();

		java.util.Random r = new java.util.Random();

		for (int i = 0; i < perm.size(); i++) {
			int other = r.nextInt(perm.size());

			double temp = data[i];
			perm.set(i, this.get(other));
			perm.set(other, temp);
		}

		return perm;
	}

	public DoubleVector permutation(long seed) {
		DoubleVector perm = this.clone();

		java.util.Random r = new java.util.Random();
		r.setSeed(seed);

		for (int i = 0; i < perm.size(); i++) {
			int other = r.nextInt(perm.size());

			double temp = data[i];
			perm.set(i, this.get(other));
			perm.set(other, temp);
		}

		return perm;
	}

	public DoubleVector tabulate() {
		com.google.common.collect.Multiset<Double> ms = new com.google.common.collect.HashMultiset<Double>(
				this.asDoubleList());

		DoubleVector vals = (new DoubleVector(ms.elementSet())).sort();

		DoubleVector outbin = new DoubleVector(vals.size());

		for (int i = 0; i < vals.size(); i++)
			outbin.add(ms.count(vals.get(i)));

		return outbin;
	}

	public DoubleVector cumAvg() {
		if (this.size() == 0)
			return new DoubleVector(0);

		DoubleVector out = new DoubleVector(this.size());
		double sum = 0.0;
		for (int i = 0; i < this.size(); i++) {
			sum += data[i];
			out.add((sum / (double) (i + 1)));
		}

		return out;
	}

	public DoubleVector cumSum() {
		if (this.size() == 0)
			return new DoubleVector(0);

		DoubleVector out = new DoubleVector(this.size());

		out.add(this.get(0));

		for (int i = 1; i < this.size(); i++)
			out.add(data[i] + out.get(i - 1));

		return out;
	}

	public static DoubleVector rankProductPvalue(DoubleVector dv1,
			DoubleVector dv2, int numPerms) {
		if (dv1.size() != dv2.size()) {
			System.out
					.println("Error DoubleVector.rankProductPvalue(DoubleVector, DoubleVector, int): The two vectors must be the same size.");
			System.exit(0);
		}

		DoubleVector perms = new DoubleVector(numPerms * dv1.size());

		for (int i = 0; i < numPerms; i++)
			perms.addAll(rankProduct(dv1.permutation(), dv2));

		perms = perms.sort();

		DoubleVector pvals = new DoubleVector(dv1.size());
		for (int i = 0; i < dv1.size(); i++)
			pvals.add(perms.getEmpiricalValueFromSortedDist(Math.sqrt(dv1
					.get(i)
					* dv2.get(i))));

		return pvals;
	}

	public static DoubleVector rankProduct(DoubleVector dv1, DoubleVector dv2) {
		DoubleVector rp = new DoubleVector(dv1.size());

		for (int i = 0; i < dv1.size(); i++)
			rp.add(Math.sqrt(dv1.get(i) * dv2.get(i)));

		return rp;
	}

	/**
	 * Returns the sum-squared error of this vector from its mean. SST =
	 * sum((y-mean(y))^2)
	 */
	public double SST() {
		double sst = 0;
		double m = this.mean();

		for (int i = 0; i < this.size(); i++) {
			double diff = data[i] - m;
			sst += diff * diff;
		}

		return sst;
	}

	public double sumOfSquares() {
		double out = 0;
		for (int i = 0; i < this.size(); i++) {
			out += (Math.pow(data[i], 2));
		}
		return out;
	}

	public DoubleVector squared() {
		DoubleVector out = new DoubleVector(this.size());

		for (int i = 0; i < this.size(); i++)
			out.add(data[i] * data[i]);

		return out;
	}

	public DoubleVector round() {
		DoubleVector out = new DoubleVector(this.size());

		for (int i = 0; i < this.size(); i++)
			out.add(Math.round(data[i]));

		return out;
	}

	public boolean isAnyNaN() {
		for (int i = 0; i < this.size(); i++)
			if (Double.isNaN(data[i]))
				return true;

		return false;
	}

	public DoubleVector sqrt() {
		DoubleVector out = new DoubleVector(this.size());

		for (int i = 0; i < this.size(); i++)
			out.add(Math.sqrt(data[i]));

		return out;
	}

	public static DoubleVector loadRow(String file, int row) {
		int l = 0;
		for (String line : new FileIterator(file)) {
			if (l == row) {
				String[] cols = line.split("\t");
				return (new DoubleVector(cols));
			}

			l++;
		}

		System.out.println("Error DoubleVector.loadRow: Line does not exist.");
		return null;

	}

	public boolean allEqualTo(double val) {
		for (int i = 0; i < size(); i++)
			if (get(i) != val)
				return false;

		return true;
	}

	public boolean allEqualTo(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) != val)
				return false;

		return true;
	}

	public boolean allEqualTo(int val) {
		for (int i = 0; i < size(); i++)
			if (get(i) != val)
				return false;

		return true;
	}

	public boolean allEqualTo(byte val) {
		for (int i = 0; i < size(); i++)
			if (get(i) != val)
				return false;

		return true;
	}

	public boolean allLessThan(double val) {
		for (int i = 0; i < size(); i++)
			if (get(i) >= val)
				return false;

		return true;
	}

	public boolean allLessThan(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) >= val)
				return false;

		return true;
	}

	public boolean allLessThan(int val) {
		for (int i = 0; i < size(); i++)
			if (get(i) >= val)
				return false;

		return true;
	}

	public boolean allLessThan(byte val) {
		for (int i = 0; i < size(); i++)
			if (get(i) >= val)
				return false;

		return true;
	}

	public boolean allGreaterThan(double val) {
		for (int i = 0; i < size(); i++)
			if (get(i) <= val)
				return false;

		return true;
	}

	public boolean allGreaterThan(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) <= val)
				return false;

		return true;
	}

	public boolean allGreaterThan(int val) {
		for (int i = 0; i < size(); i++)
			if (get(i) <= val)
				return false;

		return true;
	}

	public boolean allGreaterThan(byte val) {
		for (int i = 0; i < size(); i++)
			if (get(i) <= val)
				return false;

		return true;
	}

	public boolean anyLessThan(double val) {
		for (int i = 0; i < size(); i++)
			if (get(i) < val)
				return true;

		return false;
	}

	public boolean anyLessThan(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) < val)
				return true;

		return false;
	}

	public boolean anyLessThan(int val) {
		for (int i = 0; i < size(); i++)
			if (get(i) < val)
				return true;

		return false;
	}

	public boolean anyLessThan(byte val) {
		for (int i = 0; i < size(); i++)
			if (get(i) < val)
				return true;

		return false;
	}

	public boolean anyGreaterThan(double val) {
		for (int i = 0; i < size(); i++)
			if (get(i) > val)
				return true;

		return false;
	}

	public boolean anyGreaterThan(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) > val)
				return true;

		return false;
	}

	public boolean anyGreaterThan(int val) {
		for (int i = 0; i < size(); i++)
			if (get(i) > val)
				return true;

		return false;
	}

	public boolean anyGreaterThan(byte val) {
		for (int i = 0; i < size(); i++)
			if (get(i) > val)
				return true;

		return false;
	}

	public boolean anyEqualTo(double val) {
		for (int i = 0; i < size(); i++)
			if (get(i) == val)
				return true;

		return false;
	}

	public boolean anyEqualTo(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) == val)
				return true;

		return false;
	}

	public boolean anyEqualTo(int val) {
		for (int i = 0; i < size(); i++)
			if (get(i) == val)
				return true;

		return false;
	}

	public boolean anyEqualTo(byte val) {
		for (int i = 0; i < size(); i++)
			if (get(i) == val)
				return true;

		return false;
	}

	public BooleanVector equalTo(double val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) == val);

		return out;
	}

	public BooleanVector equalTo(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) == val);

		return out;
	}

	public BooleanVector equalTo(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) == val);

		return out;
	}

	public BooleanVector equalTo(byte val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) == val);

		return out;
	}

	public BooleanVector notEqualTo(double val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) != val);

		return out;
	}

	public BooleanVector notEqualTo(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) != val);

		return out;
	}

	public BooleanVector notEqualTo(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) != val);

		return out;
	}

	public BooleanVector notEqualTo(byte val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) != val);

		return out;
	}

	public BooleanVector lessThanOrEqual(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) <= val);

		return out;
	}

	public BooleanVector lessThanOrEqual(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) <= val);

		return out;
	}

	public BooleanVector lessThanOrEqual(byte val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) <= val);

		return out;
	}

	public BooleanVector lessThanOrEqual(double val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) <= val);

		return out;
	}

	public BooleanVector greaterThan(double val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) > val);

		return out;
	}

	public BooleanVector greaterThan(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) > val);

		return out;
	}

	public BooleanVector greaterThan(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) > val);

		return out;
	}

	public BooleanVector greaterThan(byte val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) > val);

		return out;
	}

	public static boolean[] lessThan(double[] data, double val) {
		boolean[] out = new boolean[data.length];

		for (int i = 0; i < data.length; i++)
			out[i] = data[i] < val;

		return out;
	}

	public BooleanVector lessThan(double val) {
		return new BooleanVector(DoubleVector.lessThan(data, val));
	}

	public BooleanVector lessThan(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) < val);

		return out;
	}

	public BooleanVector lessThan(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) < val);

		return out;
	}

	public BooleanVector lessThan(byte val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) < val);

		return out;
	}

	public BooleanVector greaterThanOrEqual(double val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) >= val);

		return out;
	}

	public BooleanVector greaterThanOrEqual(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) >= val);

		return out;
	}

	public BooleanVector greaterThanOrEqual(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) >= val);

		return out;
	}

	public BooleanVector greaterThanOrEqual(byte val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) >= val);

		return out;
	}

	public DoubleVector get(IntVector iv) {
		DoubleVector out = new DoubleVector(iv.size());

		for (int i = 0; i < iv.size(); i++)
			out.add(this.get(iv.get(i)));

		return out;
	}

	public static double euclideanDistance(DoubleVector dv1, DoubleVector dv2) {
		return DoubleVector.euclideanDistance(dv1.data, dv2.data);
	}

	public static double euclideanDistance(double[] dv1, double[] dv2) {
		if (dv1.length != dv2.length)
			throw new IllegalArgumentException("Vector sizes do not match: "
					+ dv1.length + ", " + dv2.length);

		double out = 0;
		for (int i = 0; i < dv1.length; i++) {
			double diff = dv1[i] - dv2[i];
			out += diff * diff;
		}

		return Math.sqrt(out);
	}

	public static double[] copy(double[] vec) {
		double[] out = new double[vec.length];

		for (int i = 0; i < vec.length; i++)
			out[i] = vec[i];

		return out;
	}

	/**
	 * Note! The array must be sorted!
	 */
	public static int bindarySearchFirstIndex(double[] sortedArray, double val) {
		int start = Arrays.binarySearch(sortedArray, val);

		while (start > 0 && sortedArray[start - 1] == val)
			start--;

		return start;
	}
}
