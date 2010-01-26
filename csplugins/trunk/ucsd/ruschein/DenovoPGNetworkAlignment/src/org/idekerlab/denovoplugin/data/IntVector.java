package org.idekerlab.denovoplugin.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.primitives.ArrayIntList;

public class IntVector extends DataVector {

	private int[] data;
	private int size;

	public IntVector() {
		Initialize(0);
	}

	public IntVector(double[] data) {
		this.data = new int[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = (int) data[i];

		size = data.length;
	}

	public IntVector(float[] data) {
		this.data = new int[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = (int) data[i];

		size = data.length;
	}

	public IntVector(short[] data) {
		this.data = new int[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = (int) data[i];

		size = data.length;
	}

	public IntVector(int[] data) {
		this.data = data;
		this.size = data.length;
	}

	public IntVector(List<?> vals) {
		this.data = new int[vals.size()];

		size = vals.size();

		if (vals.size() == 0)
			return;

		if (vals.get(0) instanceof String) {
			for (int i = 0; i < vals.size(); i++)
				data[i] = Integer.valueOf((String) vals.get(i));
		}

		if (vals.get(0) instanceof Double) {
			for (int i = 0; i < vals.size(); i++)
				data[i] = (Integer) vals.get(i);
		}

		if (vals.get(0) instanceof Integer) {
			for (int i = 0; i < vals.size(); i++)
				data[i] = (Integer) vals.get(i);
		}
	}

	public IntVector(Set<Integer> vals) {
		this.data = new int[vals.size()];
		this.size = 0;

		if (vals.size() == 0)
			return;

		for (Integer i : vals)
			this.add(i);
	}

	public IntVector(int size, List<String> elementnames, String listname) {
		Initialize(size);

		setElementNames(elementnames);

		setListName(listname);
	}

	public IntVector(int size) {
		Initialize(size);
	}

	public IntVector(int size, int vals) {
		Initialize(size, vals);
	}

	public IntVector(StringVector sv) {
		Initialize(sv.size());

		if (sv.hasElementNames())
			setElementNames(sv.getElementNames());
		if (sv.hasListName())
			setListName(sv.getListName());

		for (int i = 0; i < sv.size(); i++)
			add(Integer.valueOf(sv.get(i)));
	}

	public IntVector(String file, boolean arerownames, boolean arecolname) {
		LoadColumn(file, arerownames, arecolname, 0);
	}

	public IntVector(String file, boolean arerownames, boolean arecolname,
			int column) {
		LoadColumn(file, arerownames, arecolname, column);
	}

	public static IntVector getScale(int low, int high, int interval) {
		int length = (new Double((high - low) / interval)).intValue() + 1;

		IntVector scale = new IntVector(length);

		scale.add(low);

		for (int i = 1; i < length; i++)
			scale.add(low + i * interval);

		return scale;
	}

	public int[] getData() {
		if (size == data.length)
			return data;
		else
			return IntVector.resize(data, this.size);
	}

	public void Initialize(int size) {
		data = new int[size];
		this.size = 0;
	}

	public void Initialize(int count, int val) {
		data = new int[count];

		for (int i = 0; i < count; i++)
			data[i] = val;

		this.size = count;
	}

	protected Object getDataAsObject() {
		return data;
	}

	public void add(int integer) {
		if (data.length == 0)
			data = new int[10];
		else if (this.size == data.length)
			data = IntVector.resize(data, data.length * 2);

		data[size] = integer;
		size++;
	}

	public void add(String val) {
		this.add(Integer.valueOf(val));
	}

	public void addAll(Collection<Integer> vals) {
		if (data.length < this.size + vals.size())
			data = IntVector.resize(data, data.length + vals.size());

		for (Integer i : vals)
			this.add(i);
	}

	public void add(IntVector vals) {
		if (data.length < this.size + vals.size())
			data = IntVector.resize(data, data.length + vals.size());

		for (int i = 0; i < vals.size; i++)
			this.add(vals.get(i));
	}

	public static int[] resize(int[] vec, int size) {
		int[] out = new int[size];

		int n = Math.min(vec.length, size);
		for (int i = 0; i < n; i++)
			out[i] = vec[i];

		return out;
	}

	public void addFromFile(String fileName, boolean arerownames,
			boolean arecolumnnames) {
		IntVector other = new IntVector(fileName, arerownames, arecolumnnames);
		this.add(other);
	}

	public void add(int integer, String name) {
		this.add(integer);
		addElementName(name);
	}

	public Object getAsObject(int i) {
		return data[i];
	}

	public String getAsString(int i) {
		return Integer.toString(data[i]);
	}

	public double getAsDouble(int i) {
		return get(i);
	}

	public boolean getAsBoolean(int i) {
		return get(i) == 1;
	}

	public byte getAsByte(int i) {
		return (byte) get(i);
	}

	public int getAsInteger(int i) {
		return get(i);
	}

	public float getAsFloat(int i) {
		return get(i);
	}

	public int get(int i) {
		return (data[i]);
	}

	public int get(String element) {
		return (data[getElementNames().indexOf(element)]);
	}

	public int set(int i, int val) {
		return data[i] = val;
	}

	public void set(List<Integer> indices, int val) {
		for (Integer index : indices)
			data[index] = val;
	}

	public void set(String element, int val) {
		data[getElementNames().indexOf(element)] = val;
	}

	public void set(int i, String val) {
		data[i] = Integer.valueOf(val);
	}

	public static double getEmpiricalPvalue(int[] data, double score,
			boolean upperTail) {
		if (data.length == 0)
			return Double.NaN;

		int gtoe = 0;

		if (upperTail) {
			for (int i = 0; i < data.length; i++)
				if (data[i] >= score)
					gtoe++;
		} else {
			for (int i = 0; i < data.length; i++)
				if (data[i] <= score)
					gtoe++;
		}

		double pval = (double) gtoe / data.length;

		double min = 1.0 / data.length;
		if (pval < min)
			pval = min;

		return pval;
	}

	public double getEmpiricalPvalue(double score, boolean upperTail) {
		return IntVector.getEmpiricalPvalue(data, score, upperTail);
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

	public IntVector clone() {
		IntVector copy = new IntVector(IntVector.copy(data));
		copy.size = this.size;

		if (this.hasListName())
			copy.setListName(this.getListName());
		if (this.hasElementNames())
			copy.setElementNames(this.getElementNames());

		return (copy);
	}

	public static int[] copy(int[] vec) {
		int[] out = new int[vec.length];

		for (int i = 0; i < vec.length; i++)
			out[i] = vec[i];

		return out;
	}

	public int size() {
		return this.size;
	}

	public IntVector reZero(int zero) {
		IntVector out = this.clone();

		for (int i = 0; i < this.size; i++)
			if (out.get(i) == 0)
				out.set(i, zero);

		return out;
	}

	public IntVector reOne(int one) {
		IntVector out = this.clone();

		for (int i = 0; i < this.size; i++)
			if (out.get(i) == 1)
				out.set(i, one);

		return out;
	}

	public IntVector subtract(IntVector data2) {
		IntVector out = this.clone();

		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		for (int i = 0; i < this.size; i++)
			out.set(i, out.get(i) - data2.get(i));

		return out;
	}

	public IntVector plus(IntVector data2) {
		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		IntVector out = this.clone();

		for (int i = 0; i < this.size; i++)
			out.set(i, out.get(i) + data2.get(i));

		return out;
	}

	public IntVector minus(IntVector data2) {
		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		IntVector out = this.clone();

		for (int i = 0; i < this.size; i++)
			out.set(i, out.get(i) - data2.get(i));

		return out;
	}

	public int max() {
		if (size() == 0)
			return -1;

		int max = data[0];
		for (int i = 0; i < this.size; i++)
			if (data[i] > max)
				max = data[i];
		return (max);
	}

	public IntVector maxIs(int num) {
		IntVector maxes = new IntVector(num);

		while (maxes.size() < num) {
			double max = Double.NaN;
			int maxI = -1;

			for (int i = 0; i < this.size(); i++) {
				if (maxes.contains(i))
					continue;

				if (this.get(i) > max || Double.isNaN(max)) {
					maxI = i;
					max = this.get(i);
				}
			}

			maxes.add(maxI);
		}

		return maxes;
	}

	public IntVector minIs(int num) {
		IntVector minIs = new IntVector(num);

		IntVector order = this.sort_I();

		for (int i = 0; i < num; i++)
			minIs.add(order.get(i));

		return minIs;
	}

	public Integer minI() {
		double min = data[0];
		Integer index = 0;

		for (int i = 0; i < this.size; i++) {
			if (!Double.isNaN(data[i]) && data[i] < min) {
				min = data[i];
				index = i;
			}
		}

		return index;
	}

	public int min() {
		if (size() == 0)
			return -1;

		int min = data[0];
		for (int i = 0; i < this.size; i++)
			if (data[i] < min)
				min = data[i];
		return (min);
	}

	public IntVector abs() {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			if (out.get(i) < 0)
				out.set(i, -out.get(i));

		return out;
	}

	public IntVector negative() {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, -out.get(i));

		return out;
	}

	public IntVector plus(int val) {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) + val);

		return out;
	}

	public IntVector minus(int val) {
		return new IntVector(IntVector.minus(data, val));
	}

	public static int[] minus(int[] vec, int val) {
		int[] out = new int[vec.length];

		for (int i = 0; i < vec.length; i++)
			out[i] = vec[i] - val;

		return out;
	}

	public IntVector divideBy(IntVector val) {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val.get(i));

		return out;
	}

	public IntVector divideBy(int val) {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val);

		return out;
	}

	public DoubleVector divideBy(double val) {
		DoubleVector out = new DoubleVector(this.size());

		for (int i = 0; i < size(); i++)
			out.add(this.get(i) / val);

		return out;
	}

	public IntVector times(IntVector val) {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val.get(i));

		return out;
	}

	public IntVector times(int val) {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val);

		return out;
	}

	public boolean isNaN() {
		for (int i = 0; i < size(); i++)
			if (!Double.isNaN(data[i]))
				return false;

		return true;
	}

	public static final double pearsonCorrelation(IntVector v1, IntVector v2) {
		if (v1.size() != v2.size()) {
			System.err
					.println("Error pearsonCorrelation: Vectors must be the same size.");
			System.exit(0);
		}

		org.apache.commons.math.stat.regression.SimpleRegression sr = new org.apache.commons.math.stat.regression.SimpleRegression();

		for (int i = 0; i < v1.size(); i++)
			sr.addData(v1.get(i), v2.get(i));

		return sr.getR();
	}

	public static double mean(int[] data) {
		double sum = 0;
		int valcount = 0;

		for (int i = 0; i < data.length; i++)
			if (!Double.isNaN(data[i])) {
				sum += data[i];
				valcount++;
			}

		if (valcount == 0)
			return Double.NaN;

		return sum / valcount;
	}

	public double mean() {
		return IntVector.mean(data);
	}

	public double std() {
		double avg = mean();

		double sum = 0.0;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Double.isNaN(get(i))) {
				sum += java.lang.Math.pow((get(i) - avg), 2);
				valcount++;
			}

		if (valcount == 0)
			return Double.NaN;

		sum = sum / (valcount - 1);

		return java.lang.Math.pow(sum, .5);
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

	public int sum() {
		int sum = 0;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Double.isNaN(data[i])) {
				sum += data[i];
				valcount++;
			}

		if (valcount == 0)
			return 0;

		return sum;
	}

	public static IntVector join(IntVector dt1, IntVector dt2) {
		int newsize = dt1.size() + dt2.size();

		IntVector dtout = new IntVector();
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

	public static IntVector joinAll(List<IntVector> dtlist) {
		if (dtlist.size() == 0)
			return null;
		if (dtlist.size() == 1)
			return dtlist.get(0);

		IntVector dtout = join(dtlist.get(0), dtlist.get(1));

		for (int i = 2; i < dtlist.size(); i++)
			dtout = join(dtout, dtlist.get(i));

		return dtout;
	}

	public IntVector Discretize(List<Double> breaks) {
		IntVector out = this.clone();

		for (int i = 0; i < size(); i++)
			for (int b = 0; b < breaks.size(); b++)
				if (b == 0 && get(i) <= breaks.get(0)) {
					out.set(i, 0);
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

	public IntVector pow(double power) {
		IntVector pdt = this.clone();

		for (int i = 0; i < this.size; i++)
			pdt.set(i, (int) java.lang.Math.pow(get(i), power));

		return pdt;
	}

	public Set<Integer> asIntSet() {
		Set<Integer> intset = new HashSet<Integer>(this.size());

		for (int i = 0; i < this.size(); i++)
			intset.add(this.get(i));

		return intset;
	}

	public IntVector sample(int samplesize, boolean replace) {
		IntVector mysample = new IntVector(samplesize);

		IntVector cp = this.clone();

		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		if (!replace) {
			int lsizem1 = cp.size() - 1;

			for (int i = 0; i < samplesize; i++) {
				int swapi = lsizem1 - randgen.nextInt(cp.size() - i);
				int temp = cp.get(i);
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

	/**
	 * Replace = false
	 */
	public IntVector sample(int sampleSize, boolean replace,
			DoubleVector weights) {
		if (replace) {
			IntVector out = new IntVector(sampleSize);

			for (int s = 0; s < sampleSize; s++) {
				int maxI = -1;
				double maxVal = -1;

				for (int i = 0; i < this.size(); i++) {
					double score = Math.pow(Math.random(), 1 / weights.get(i));
					if (score > maxVal) {
						maxI = i;
						maxVal = score;
					}
				}

				out.add(maxI);
			}

			return out;
		} else {
			DoubleVector keys = new DoubleVector(this.size());

			for (int i = 0; i < this.size(); i++)
				keys.add(Math.pow(Math.random(), 1 / weights.get(i)));

			return keys.maxIs(sampleSize);
		}
	}

	public IntVector subVector(int i1, int size) {
		IntVector out = new IntVector(size);

		int i1s = i1 + size;

		for (int i = i1; i < i1s; i++)
			out.add(this.get(i));

		return out;
	}

	public IntVector get(List<?> indexes) {
		IntVector sub = new IntVector(indexes.size());

		for (int i = 0; i < indexes.size(); i++)
			sub.add(data[((Double) (indexes.get(i))).intValue()]);

		return sub;
	}

	public IntVector get(int[] indexes) {
		IntVector sub = new IntVector(indexes.length);

		for (int i = 0; i < indexes.length; i++)
			sub.add(data[indexes[i]]);

		return sub;
	}

	public IntVector get(IntVector indexes) {
		IntVector sub = new IntVector(indexes.size());

		for (int i = 0; i < indexes.size(); i++)
			sub.add(data[indexes.get(i)]);

		return sub;
	}

	public IntVector get(BooleanVector bv) {
		if (bv.size() != this.size()) {
			System.err
					.println("Error DoubleVector.get(BooleanVector): The two vectors must be the same size.");
			System.err.println("this.size = " + this.size() + ", bvsize = "
					+ bv.size());
			System.exit(0);
		}

		IntVector sub = new IntVector();

		boolean found = false;
		if (this.elementnames != null) {
			sub.setElementNames(new ArrayList<String>());

			for (int i = 0; i < size(); i++)
				if (bv.get(i)) {
					sub.add(this.get(i), this.getElementName(i));
					found = true;
				}
		} else {
			for (int i = 0; i < size(); i++)
				if (bv.get(i)) {
					sub.add(this.get(i));
					found = true;
				}
		}

		if (!found)
			sub.removeElementNames();

		return sub;
	}

	public IntVector not(int fullnum) {
		IntVector notthis = new IntVector();

		for (int i = 0; i < fullnum; i++)
			if (!this.contains(i))
				notthis.add(i);

		return notthis;
	}

	public static double diffSum(IntVector v1, IntVector v2) {
		if (v1.size() != v2.size()) {
			System.out.println("diffSum Error: v1, v2 are not the same size.");
			System.exit(0);
		}

		double ds = 0.0;

		for (int i = 0; i < v1.size(); i++)
			ds += java.lang.Math.abs(v1.get(i) - v2.get(i));

		return ds;
	}

	public static BooleanVector difference(IntVector v1, IntVector v2) {
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

	public static BooleanVector similarity(IntVector v1, IntVector v2) {
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

	public double squaredMean() {
		return this.pow(2.0).mean();
	}

	public IntVector diff1() {
		IntVector out = this.clone();

		out.set(0, get(1) - get(0));

		for (int i = 1; i < size() - 1; i++)
			out.set(i, (get(i + 1) - get(i - 1)) / 2);

		out.set(out.size() - 1, get(out.size() - 1) - get(out.size() - 2));

		return out;
	}

	public IntVector diffLeft() {
		IntVector out = new IntVector(size());

		for (int i = 1; i < size(); i++)
			out.add(get(i) - get(i - 1));

		out.set(0, 0);

		return out;
	}

	public IntVector diffCenter() {
		int newsize = this.size() - 1;
		IntVector out = new IntVector(newsize);

		for (int i = 0; i < newsize; i++)
			out.add(get(i + 1) - get(i));

		return out;
	}

	public IntVector diffRight() {
		int newsize = this.size() - 1;
		IntVector out = new IntVector(newsize);

		for (int i = 0; i < newsize; i++)
			out.add(get(i + 1) - get(i));

		out.set(size() - 1, 0);

		return out;
	}

	public IntVector shift(int dist) {
		IntVector out = new IntVector(size());

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

	public BooleanVector isReal() {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(!(Double.isNaN(get(i)) || Double.isInfinite(get(i))));

		return out;
	}

	public IntVector sort() {
		IntVector out = this.clone();
		Sorter.Sort_I(out);

		return out;
	}

	public IntVector sort_I() {
		return Sorter.Sort_I(this.clone());
	}

	public void set(int[] iarray) {
		data = IntVector.copy(iarray);
		this.size = iarray.length;
	}

	public void set(BooleanVector bv, int val) {
		for (int i = 0; i < size(); i++)
			if (bv.get(i))
				set(i, val);
	}

	public void set(ArrayIntList indexes, IntVector vals) {
		vals = vals.clone();

		for (int i = 0; i < indexes.size(); i++)
			this.set(indexes.get(i), vals.get(i));
	}

	public void replace(int oldval, int newval) {
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

	public IntVector permutation() {
		IntVector perm = this.clone();

		java.util.Random r = new java.util.Random();

		for (int i = 0; i < perm.size(); i++) {
			int other = r.nextInt(perm.size());

			int temp = perm.get(i);
			perm.set(i, perm.get(other));
			perm.set(other, temp);
		}

		return perm;
	}

	public IntVector tabulate() {
		com.google.common.collect.Multiset<Integer> ms = new com.google.common.collect.HashMultiset<Integer>(
				this.asIntegerList());

		IntVector vals = (new IntVector(ms.elementSet())).sort();

		IntVector outbin = new IntVector(vals.size());

		for (int i = 0; i < vals.size(); i++)
			outbin.add(ms.count(vals.get(i)));

		return outbin;
	}

	public IntVector cumSum() {
		if (this.size() == 0)
			return new IntVector(0);

		IntVector out = new IntVector(this.size());

		out.add(this.get(0));

		for (int i = 1; i < this.size(); i++)
			out.add(this.get(i) + out.get(i - 1));

		return out;
	}

	public IntVector cumSum(ArrayIntList order) {
		if (this.size() == 0)
			return new IntVector(0);

		IntVector out = new IntVector(this.size(), 0);

		int sum = 0;

		for (int i = 0; i < order.size(); i++) {
			sum += this.get(order.get(i));
			out.set(order.get(i), sum);
		}

		return out;
	}

	public BooleanVector isEqual(int val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) == val);

		return out;
	}

	public int indexOf(int val) {
		for (int i = 0; i < this.size; i++)
			if (data[i] == val)
				return i;

		return -1;
	}

	public IntVector reverse() {
		IntVector out = new IntVector(this.size());

		for (int i = this.size() - 1; i >= 0; i--)
			out.add(this.get(i));

		return out;
	}

	public static IntVector repeat(int num, int val) {
		IntVector out = new IntVector(num);

		for (int i = 0; i < num; i++)
			out.add(val);

		return out;
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

	public BooleanVector lessThan(double val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) < val);

		return out;
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

	public static int[] inverseOrderMap(int[] order) {
		int[] out = new int[order.length];

		for (int i = 0; i < order.length; i++)
			out[order[i]] = i;

		return out;
	}

	public int maxI() {
		return IntVector.maxI(this.data);
	}

	public static int maxI(int[] data) {
		int max = data[0];
		int index = 0;

		for (int i = 0; i < data.length; i++) {
			if (data[i] > max) {
				max = data[i];
				index = i;
			}
		}

		return index;
	}

	public void removeElement(int value) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == value)
				this.removeElementAt(i);
			break;
		}
	}

	public void removeElementAt(int index) {
		int[] newdata = new int[data.length - 1];

		for (int i = 0; i < index; i++)
			newdata[i] = data[i];

		for (int i = index + 1; i < data.length; i++)
			newdata[i - 1] = data[i];
	}
}
