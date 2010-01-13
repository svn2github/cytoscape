package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.primitives.ArrayIntList;

public class FloatVector extends DataVector {

	private float[] data;
	private int size;

	public FloatVector() {
		Initialize(0);
	}

	public FloatVector(double[] data) {
		this.data = new float[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = (float) data[i];

		size = data.length;
	}

	public FloatVector(float[] data) {
		this.data = data;

		this.size = data.length;
	}

	public FloatVector(short[] data) {
		this.data = new float[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = data[i];

		size = data.length;
	}

	public FloatVector(int[] data) {
		this.data = new float[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = data[i];

		size = data.length;
	}

	public FloatVector(String[] data) {
		this.data = new float[data.length];

		for (int i = 0; i < data.length; i++)
			this.data[i] = Float.valueOf(data[i]);

		size = data.length;
	}

	public FloatVector(List<?> vals) {
		Initialize(vals.size());

		if (vals.size() == 0)
			return;

		if (vals.get(0) instanceof String) {
			for (int i = 0; i < vals.size(); i++)
				this.add(Float.valueOf((String) vals.get(i)));
		}

		if (vals.get(0) instanceof Float) {
			for (int i = 0; i < vals.size(); i++)
				this.add((Float) vals.get(i));
		}
	}

	public FloatVector(Set<Float> vals) {
		Initialize(vals.size());

		if (vals.size() == 0)
			return;

		for (float d : vals)
			this.add(d);
	}

	public FloatVector(IntVector ail) {
		Initialize(ail.size());

		for (int i = 0; i < ail.size(); i++)
			this.add(ail.get(i));
	}

	public FloatVector(int size, List<String> elementnames, String listname) {
		Initialize(size);

		setElementNames(elementnames);

		setListName(listname);
	}

	public FloatVector(int size) {
		Initialize(size);
	}

	public FloatVector(int size, float vals) {
		Initialize(size, vals);
	}

	public FloatVector(StringVector sv) {
		Initialize(sv.size());

		if (sv.hasElementNames())
			setElementNames(sv.getElementNames());
		if (sv.hasListName())
			setListName(sv.getListName());

		for (int i = 0; i < sv.size(); i++)
			add(Float.valueOf(sv.get(i)));
	}

	public FloatVector(String file) {
		LoadColumn(file, false, false, 0);
	}

	public FloatVector(String file, boolean arerownames, boolean arecolname) {
		LoadColumn(file, arerownames, arecolname, 0);
	}

	public FloatVector(String file, boolean arerownames, boolean arecolname,
			int column) {
		LoadColumn(file, arerownames, arecolname, column);
	}

	public static FloatVector getScale(float low, float high, float interval) {
		int length = (new Float((high - low) / interval)).intValue() + 1;

		FloatVector scale = new FloatVector(length);

		scale.add(low);

		for (int i = 1; i < length; i++)
			scale.add(low + i * interval);

		return scale;
	}

	public void Initialize(int size) {
		data = new float[size];
		this.size = 0;
	}

	public void Initialize(int count, double val) {
		data = new float[count];

		for (int i = 0; i < count; i++)
			add(val);

		size = count;
	}

	protected Object getDataAsObject() {
		return data;
	}

	/**
	 * Often gets the reference to the actual data.
	 */
	public float[] getData() {
		if (size == data.length)
			return data;
		else
			return FloatVector.resize(data, this.size);
	}

	public static float[] resize(float[] vec, int size) {
		float[] out = new float[size];

		int n = Math.min(vec.length, size);
		for (int i = 0; i < n; i++)
			out[i] = vec[i];

		return out;
	}

	public synchronized void add(float o) {
		if (data.length == 0)
			data = new float[10];
		else if (this.size == data.length)
			data = FloatVector.resize(data, data.length * 2);

		data[size] = o;
		size++;
	}

	public synchronized void add(double o) {
		if (data.length == 0)
			data = new float[10];
		else if (this.size == data.length)
			data = FloatVector.resize(data, data.length * 2);

		data[size] = (float) o;
		size++;
	}

	public synchronized void add(String val) {
		this.add(Double.valueOf(val));
	}

	public synchronized void addAll(Collection<Float> vals) {
		if (data.length < this.size + vals.size())
			data = FloatVector.resize(data, data.length + vals.size());

		for (Float d : vals)
			this.add(d);
	}

	public synchronized void addAll(FloatVector vals) {
		if (data.length < this.size + vals.size())
			data = FloatVector.resize(data, data.length + vals.size());

		for (int i = 0; i < vals.size; i++)
			this.add(vals.get(i));
	}

	public Object getAsObject(int i) {
		return data[i];
	}

	public String getAsString(int i) {
		return Float.toString(data[i]);
	}

	public double getAsDouble(int i) {
		return get(i);
	}

	public boolean getAsBoolean(int i) {
		return get(i) == 1;
	}

	public byte getAsByte(int i) {
		return new Float(get(i)).byteValue();
	}

	public int getAsInteger(int i) {
		return (int) get(i);
	}

	public float getAsFloat(int i) {
		return get(i);
	}

	public float get(int i) {
		return (data[i]);
	}

	public float get(String element) {
		return (data[getElementNames().indexOf(element)]);
	}

	public void set(int i, float val) {
		data[i] = val;
	}

	public void set(int i, double val) {
		data[i] = (float) val;
	}

	public void set(int i, Integer val) {
		data[i] = new Float(val);
	}

	public void set(List<Integer> indices, float val) {
		for (Integer index : indices)
			data[index] = val;
	}

	public void set(String element, float val) {
		data[getElementNames().indexOf(element)] = val;
	}

	public void set(int i, String val) {
		if (val.equals("Inf"))
			data[i] = Float.POSITIVE_INFINITY;
		else if (val.equals("-Inf"))
			data[i] = Float.NEGATIVE_INFINITY;
		else
			data[i] = Float.valueOf(val);
	}

	public float getEmpiricalPvalue(float score, boolean upperTail) {
		if (this.size() == 0)
			return Float.NaN;

		int gtoe;

		if (upperTail)
			gtoe = this.greaterThanOrEqual(score).sum();
		else
			gtoe = this.lessThanOrEqual(score).sum();

		float pval = (float) gtoe / this.size();

		float min = 1.0f / this.size();
		if (pval < min)
			pval = min;

		return pval;
	}

	public float getEmpiricalValueFromSortedDist(float score) {
		int count;
		for (count = 0; count < this.size(); count++) {
			if (score < this.get(count))
				break;
		}
		return (count == (this.size()) ? (1 / (float) this.size())
				: (1.0f - ((float) (count) / this.size())));
	}

	public FloatVector clone() {
		FloatVector copy = new FloatVector(FloatVector.copy(data));
		copy.size = this.size;

		if (this.hasListName())
			copy.setListName(this.getListName());
		if (this.hasElementNames())
			copy.setElementNames(this.getElementNames());

		return (copy);
	}

	public int size() {
		return size;
	}

	public FloatVector reZero(float zero) {
		FloatVector out = this.clone();

		for (int i = 0; i < data.length; i++)
			if (out.get(i) == 0)
				out.set(i, zero);

		return out;
	}

	public FloatVector reOne(float one) {
		FloatVector out = this.clone();

		for (int i = 0; i < data.length; i++)
			if (out.get(i) == 1)
				out.set(i, one);

		return out;
	}

	public FloatVector subtract(FloatVector data2) {
		FloatVector out = this.clone();

		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		for (int i = 0; i < data.length; i++)
			out.set(i, out.get(i) - data2.get(i));

		return out;
	}

	public FloatVector plus(FloatVector data2) {
		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		FloatVector out = this.clone();

		for (int i = 0; i < data.length; i++)
			out.set(i, out.get(i) + data2.get(i));

		return out;
	}

	public FloatVector minus(FloatVector data2) {
		if (data2.size() != size()) {
			System.out
					.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}

		FloatVector out = this.clone();

		for (int i = 0; i < data.length; i++)
			out.set(i, out.get(i) - data2.get(i));

		return out;
	}

	public float max() {
		return max(true);
	}

	public float max(boolean keepNaN) {
		if (size() == 0)
			return Float.NaN;

		if (keepNaN) {
			float max = data[0];
			for (int i = 0; i < data.length; i++)
				if (data[i] > max)
					max = data[i];
			return (max);
		} else {
			FloatVector newvec = this.get(this.NaNs().not());
			return newvec.max(true);
		}
	}

	public int maxI() {
		float max = data[0];
		int index = 0;

		for (int i = 0; i < data.length; i++) {
			if (!Float.isNaN(data[i]) && data[i] > max) {
				max = data[i];
				index = i;
			}
		}

		return index;
	}

	public BooleanVector NaNs() {
		BooleanVector out = new BooleanVector(this.size());
		for (int i = 0; i < size(); i++)
			if (Float.isNaN(get(i)))
				out.add(true);
			else
				out.add(false);

		return out;
	}

	public List<Integer> NaN_indices() {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < this.size(); i++)
			if (Float.isNaN(data[i]))
				indices.add(i);

		return indices;
	}

	public IntVector maxIs(int num) {
		IntVector maxes = new IntVector(num);

		while (maxes.size() < num) {
			float max = Float.NaN;
			int maxI = -1;

			for (int i = 0; i < this.size(); i++) {
				if (maxes.contains(i))
					continue;

				if (data[i] > max || Float.isNaN(max)) {
					maxI = i;
					max = data[i];
				}
			}

			maxes.add(maxI);
		}

		return maxes;
	}

	public IntVector minIs(int num) {
		IntVector mins = new IntVector(num);

		while (mins.size() < num) {
			float min = Float.NaN;
			int minI = -1;

			for (int i = 0; i < this.size(); i++) {
				if (mins.contains(i))
					continue;

				if (data[i] < min || Float.isNaN(min)) {
					minI = i;
					min = data[i];
				}
			}

			mins.add(minI);
		}

		return mins;
	}

	public Integer minI() {
		if (this.size() == 0)
			return -1;

		float min = data[0];
		Integer index = 0;

		for (int i = 0; i < data.length; i++) {
			if (!Float.isNaN(data[i]) && data[i] < min) {
				min = data[i];
				index = i;
			}
		}

		return index;
	}

	public float min() {
		return min(true);
	}

	public float min(boolean keepNaN) {
		if (size() == 0)
			return Float.NaN;

		if (keepNaN) {
			float min = data[0];
			for (int i = 0; i < data.length; i++)
				if (data[i] < min)
					min = data[i];
			return (min);
		} else {
			FloatVector newvec = this.get(this.NaNs().not());
			return newvec.min(true);
		}
	}

	public FloatVector abs() {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			if (out.get(i) < 0)
				out.set(i, -out.get(i));

		return out;
	}

	public FloatVector log(float base) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, java.lang.Math.log(out.get(i))
					/ java.lang.Math.log(base));

		return out;
	}

	public FloatVector negative() {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, -out.get(i));

		return out;
	}

	public FloatVector plus(float val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) + val);

		return out;
	}

	public FloatVector minus(float val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) - val);

		return out;
	}

	public FloatVector subtract(float val) {
		FloatVector s = this.clone();

		for (int i = 0; i < size(); i++)
			s.set(i, s.get(i) - val);

		return s;
	}

	public FloatVector divideBy(FloatVector val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val.get(i));

		return out;
	}

	public FloatVector divideBy(float val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val);

		return out;
	}

	public FloatVector divideBy(int val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) / val);

		return out;
	}

	public FloatVector times(FloatVector val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val.get(i));

		return out;
	}

	public FloatVector times(float val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val);

		return out;
	}

	public FloatVector times(int val) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			out.set(i, out.get(i) * val);

		return out;
	}

	public BooleanVector isNaN() {
		BooleanVector bv = new BooleanVector(this.size());

		for (int i = 0; i < size(); i++)
			bv.add(Float.isNaN(data[i]));

		return bv;
	}

	public BooleanVector notNaN() {
		BooleanVector bv = new BooleanVector(this.size());

		for (int i = 0; i < size(); i++)
			bv.add(!Float.isNaN(data[i]));

		return bv;
	}

	public FloatVector normalize() {
		FloatVector out = this.clone();

		float mn = this.mean();
		float sd = this.std();

		for (int i = 0; i < this.size(); i++)
			out.set(i, (out.get(i) - mn) / sd);

		return out;
	}

	public FloatVector center() {
		FloatVector out = this.clone();

		float mn = this.mean();

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

	public static final float pearsonCorrelation(FloatVector v1, FloatVector v2) {
		if (v1.size() != v2.size())
			throw (new java.lang.IllegalArgumentException(
					"Vectors must be the same size. " + v1.size() + "!="
							+ v2.size()));

		v1 = v1.center();
		v2 = v2.center();

		float sum_xy = 0;

		for (int i = 0; i < v1.size(); i++)
			sum_xy += v1.get(i) * v2.get(i);

		// float sumx = v1.sum();
		// float sumy = v2.sum();
		float sumx2 = v1.squaredSum();
		float sumy2 = v2.squaredSum();

		// return (v1.size()*sum_xy-sumx*sumy)/( Math.sqrt(
		// v1.size()*sumx2-sumx*sumx )*Math.sqrt( v1.size()*sumy2-sumy*sumy ) );
		return (v1.size() * sum_xy)
				/ ((float) Math.sqrt(v1.size() * sumx2) * (float) Math.sqrt(v1
						.size()
						* sumy2));
	}

	public float mean() {
		float sum = 0.0f;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Float.isNaN(data[i])) {
				sum += data[i];
				valcount++;
			}

		if (valcount == 0)
			return Float.NaN;

		return sum / (valcount);
	}

	public float median() {
		FloatVector sorted = this.sort();
		int medianIndex = this.size() / 2 + 1;

		if (this.size() % 2 != 0)
			return sorted.get(medianIndex);
		else
			return (((sorted.get(medianIndex)) + (sorted.get(medianIndex + 1))) / 2);
	}

	public float std() {
		float avg = mean();

		float sum = 0.0f;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Float.isNaN(get(i))) {
				sum += java.lang.Math.pow((get(i) - avg), 2);
				valcount++;
			}

		if (valcount == 0)
			return Float.NaN;

		sum = sum / (valcount - 1);

		return (float) java.lang.Math.pow(sum, .5);
	}

	public ZStat zstat() {
		float avg = mean();

		float sum = 0.0f;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Float.isNaN(get(i))) {
				sum += java.lang.Math.pow((get(i) - avg), 2);
				valcount++;
			}

		if (valcount == 0)
			return new ZStat(Float.NaN, Float.NaN);

		sum = sum / (valcount - 1);

		return new ZStat(avg, java.lang.Math.pow(sum, .5));
	}

	public float sum() {
		float sum = 0.0f;
		int valcount = 0;

		for (int i = 0; i < size(); i++)
			if (!Float.isNaN(data[i])) {
				sum += data[i];
				valcount++;
			}

		if (valcount == 0)
			return Float.NaN;

		return sum;
	}

	public static FloatVector join(FloatVector dt1, FloatVector dt2) {
		int newsize = dt1.size() + dt2.size();

		FloatVector dtout = new FloatVector();
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

	public static FloatVector joinAll(List<FloatVector> dtlist) {
		if (dtlist.size() == 0)
			return null;
		if (dtlist.size() == 1)
			return dtlist.get(0);

		FloatVector dtout = join(dtlist.get(0), dtlist.get(1));

		for (int i = 2; i < dtlist.size(); i++)
			dtout = join(dtout, dtlist.get(i));

		return dtout;
	}

	public FloatVector Discretize(List<Float> breaks) {
		FloatVector out = this.clone();

		for (int i = 0; i < size(); i++)
			for (int b = 0; b < breaks.size(); b++)
				if (b == 0 && get(i) <= breaks.get(0)) {
					out.set(i, 0.0f);
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

	public FloatVector pow(float power) {
		FloatVector pdt = this.clone();

		for (int i = 0; i < data.length; i++)
			pdt.set(i, java.lang.Math.pow(get(i), power));

		return pdt;
	}

	public FloatVector sample(int samplesize, boolean replace) {
		FloatVector mysample = new FloatVector(samplesize);

		FloatVector cp = this.clone();

		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		if (!replace) {
			int lsizem1 = cp.size() - 1;

			for (int i = 0; i < samplesize; i++) {
				int swapi = lsizem1 - randgen.nextInt(cp.size() - i);
				float temp = cp.get(i);
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

	public FloatVector subVector(int i1, int size) {
		FloatVector out = new FloatVector(size);

		int i1s = i1 + size;

		for (int i = i1; i < i1s; i++)
			out.add(data[i]);

		return out;
	}

	public FloatVector get(List<?> indexes) {
		FloatVector sub = new FloatVector(indexes.size());

		for (int i = 0; i < indexes.size(); i++)
			sub.add(data[((Double) (indexes.get(i))).intValue()]);

		return sub;
	}

	public FloatVector get(int[] indexes) {
		FloatVector sub = new FloatVector(indexes.length);

		for (int i = 0; i < indexes.length; i++)
			sub.add(data[indexes[i]]);

		return sub;
	}

	public FloatVector get(BooleanVector bv) {
		if (bv.size() != this.size()) {
			System.err
					.println("Error floatVector.get(BooleanVector): The two vectors must be the same size.");
			System.err.println("this.size = " + this.size() + ", bvsize = "
					+ bv.size());

			System.exit(0);
		}

		FloatVector sub = new FloatVector();

		boolean found = false;
		if (this.elementnames != null) {
			List<String> elementNames = new ArrayList<String>();

			for (int i = 0; i < size(); i++)
				if (bv.get(i)) {
					elementNames.add(this.getElementName(i));
					sub.add(data[i]);
					found = true;
				}

			sub.setElementNames(elementNames);
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

	public FloatVector not(int fullnum) {
		FloatVector notthis = new FloatVector();

		for (int i = 0; i < fullnum; i++)
			if (!this.contains(i))
				notthis.add(i);

		return notthis;
	}

	public void NRandomize() {
		java.util.Random randgen = new java.util.Random();

		randgen.setSeed(System.nanoTime());

		for (int i = 0; i < size(); i++)
			set(i, (float) randgen.nextGaussian());
	}

	public static float diffSum(FloatVector v1, FloatVector v2) {
		if (v1.size() != v2.size()) {
			System.out.println("diffSum Error: v1, v2 are not the same size.");
			System.exit(0);
		}

		float ds = 0.0f;

		for (int i = 0; i < v1.size(); i++)
			ds += java.lang.Math.abs(v1.get(i) - v2.get(i));

		return ds;
	}

	public static BooleanVector difference(FloatVector v1, FloatVector v2) {
		if (v1.size() != v2.size()) {
			System.out
					.println("The two float vectors must be of the same size to perform this operation");
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

	public static BooleanVector similarity(FloatVector v1, FloatVector v2) {
		if (v1.size() != v2.size()) {
			System.out
					.println("The two float vectors must be of the same size to perform this operation");
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

	public static FloatVector nRandoms(int num) {
		FloatVector out = new FloatVector(num);
		out.Initialize(num, 0.0f);
		out.NRandomize();

		return out;
	}

	public static FloatVector repeat(int num, float val) {
		FloatVector out = new FloatVector(num);

		for (int i = 0; i < num; i++)
			out.add(val);

		return out;
	}

	public float squaredMean() {
		return this.squaredSum() / this.size();
	}

	public float squaredSum() {
		float sum = 0;
		for (int i = 0; i < this.size(); i++)
			sum += data[i] * data[i];

		return sum;
	}

	public FloatVector diff1() {
		FloatVector out = this.clone();

		out.set(0, get(1) - get(0));

		for (int i = 1; i < size() - 1; i++)
			out.set(i, (get(i + 1) - get(i - 1)) / 2);

		out.set(out.size() - 1, get(out.size() - 1) - get(out.size() - 2));

		return out;
	}

	public FloatVector diffLeft() {
		FloatVector out = new FloatVector(size());

		for (int i = 1; i < size(); i++)
			out.add(get(i) - get(i - 1));

		out.set(0, 0.0f);

		return out;
	}

	public FloatVector diffCenter() {
		int newsize = this.size() - 1;
		FloatVector out = new FloatVector(newsize);

		for (int i = 0; i < newsize; i++)
			out.add(get(i + 1) - get(i));

		return out;
	}

	public FloatVector diffRight() {
		int newsize = this.size() - 1;
		FloatVector out = new FloatVector(newsize);

		for (int i = 0; i < newsize; i++)
			out.add(get(i + 1) - get(i));

		out.set(size() - 1, 0.0f);

		return out;
	}

	public FloatVector shift(int dist) {
		FloatVector out = new FloatVector(size());

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
			out.add(!(Float.isNaN(get(i)) || Float.isInfinite(get(i))));

		return out;
	}

	public FloatVector sort() {
		FloatVector out;

		if (!this.hasElementNames()) {
			float[] mydata = this.subVector(0, size).data;
			Arrays.sort(mydata);
			out = new FloatVector(mydata);
		} else {
			out = new FloatVector(this.size());
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

	public void set(BooleanVector bv, float val) {
		for (int i = 0; i < size(); i++)
			if (bv.get(i))
				set(i, val);
	}

	public void set(IntVector indexes, FloatVector vals) {
		vals = vals.clone();

		for (int i = 0; i < indexes.size(); i++)
			this.set(indexes.get(i), vals.get(i));
	}

	/**
	 * Replaces all entries of a value in the vector with a new value.
	 */
	public void replace(float oldval, float newval) {
		if (Float.isNaN(oldval)) {
			for (int i = 0; i < size(); i++)
				if (Float.isNaN(get(i)))
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
	public FloatVector permutation() {
		FloatVector perm = this.clone();

		java.util.Random r = new java.util.Random();

		for (int i = 0; i < perm.size(); i++) {
			int other = r.nextInt(perm.size());

			float temp = data[i];
			this.set(i, this.get(other));
			this.set(other, temp);
		}

		return perm;
	}

	public FloatVector cumAvg() {
		if (this.size() == 0)
			return new FloatVector(0);

		FloatVector out = new FloatVector(this.size());
		float sum = 0.0f;
		for (int i = 0; i < this.size(); i++) {
			sum += data[i];
			out.add((sum / (float) (i + 1)));
		}

		return out;
	}

	public FloatVector cumSum() {
		if (this.size() == 0)
			return new FloatVector(0);

		FloatVector out = new FloatVector(this.size());

		out.add(this.get(0));

		for (int i = 1; i < this.size(); i++)
			out.add(data[i] + out.get(i - 1));

		return out;
	}

	public FloatVector cumSum(ArrayIntList order) {
		if (this.size() == 0)
			return new FloatVector(0);

		FloatVector out = new FloatVector(this.size(), 0);

		float sum = 0;

		for (int i = 0; i < order.size(); i++) {
			sum += this.get(order.get(i));
			out.set(order.get(i), sum);
		}

		return out;
	}

	public static FloatVector rankProductPvalue(FloatVector dv1,
			FloatVector dv2, int numPerms) {
		if (dv1.size() != dv2.size()) {
			System.out
					.println("Error floatVector.rankProductPvalue(floatVector, floatVector, int): The two vectors must be the same size.");
			System.exit(0);
		}

		FloatVector perms = new FloatVector(numPerms * dv1.size());

		for (int i = 0; i < numPerms; i++)
			perms.addAll(rankProduct(dv1.permutation(), dv2));

		perms = perms.sort();

		FloatVector pvals = new FloatVector(dv1.size());
		for (int i = 0; i < dv1.size(); i++)
			pvals.add(perms.getEmpiricalValueFromSortedDist((float) Math
					.sqrt(dv1.get(i) * dv2.get(i))));

		return pvals;
	}

	public static FloatVector rankProduct(FloatVector dv1, FloatVector dv2) {
		FloatVector rp = new FloatVector(dv1.size());

		for (int i = 0; i < dv1.size(); i++)
			rp.add(Math.sqrt(dv1.get(i) * dv2.get(i)));

		return rp;
	}

	/**
	 * Returns the sum-squared error of this vector from its mean. SST =
	 * sum((y-mean(y))^2)
	 */
	public float SST() {
		float sst = 0;
		float m = this.mean();

		for (int i = 0; i < this.size(); i++) {
			float diff = data[i] - m;
			sst += diff * diff;
		}

		return sst;
	}

	public FloatVector squared() {
		FloatVector out = new FloatVector(this.size());

		for (int i = 0; i < this.size(); i++)
			out.add(data[i] * data[i]);

		return out;
	}

	public FloatVector round() {
		FloatVector out = new FloatVector(this.size());

		for (int i = 0; i < this.size(); i++)
			out.add(Math.round(data[i]));

		return out;
	}

	public boolean isAnyNaN() {
		for (int i = 0; i < this.size(); i++)
			if (Float.isNaN(data[i]))
				return true;

		return false;
	}

	public FloatVector sqrt() {
		FloatVector out = new FloatVector(this.size());

		for (int i = 0; i < this.size(); i++)
			out.add(Math.sqrt(data[i]));

		return out;
	}

	public static FloatVector loadRow(String file, int row) {
		int l = 0;
		for (String line : new utilities.files.FileIterator(file)) {
			if (l == row) {
				String[] cols = line.split("\t");
				return (new FloatVector(cols));
			}

			l++;
		}

		System.out.println("Error floatVector.loadRow: Line does not exist.");
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

	public boolean anyLessThan(float val) {
		for (int i = 0; i < size(); i++)
			if (get(i) < val)
				return true;

		return false;
	}

	public boolean anyLessThan(double val) {
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

	public BooleanVector lessThanOrEqual(double val) {
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

	public BooleanVector lessThanOrEqual(float val) {
		BooleanVector out = new BooleanVector(size());

		for (int i = 0; i < size(); i++)
			out.add(get(i) <= val);

		return out;
	}

	public BooleanVector greaterThan(float val) {
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

	public BooleanVector greaterThan(double val) {
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

	public BooleanVector greaterThanOrEqual(float val) {
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

	public BooleanVector greaterThanOrEqual(double val) {
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

	public FloatVector get(IntVector iv) {
		FloatVector out = new FloatVector(iv.size());

		for (int i = 0; i < iv.size(); i++)
			out.add(this.get(iv.get(i)));

		return out;
	}

	public static double euclideanDistance(FloatVector dv1, FloatVector dv2) {
		if (dv1.size() != dv2.size())
			throw new IllegalArgumentException("Vector sizes do not match: "
					+ dv1.size() + ", " + dv2.size());

		float out = 0;
		for (int i = 0; i < dv1.size(); i++) {
			float diff = dv1.get(i) - dv2.get(i);
			out += diff * diff;
		}

		return Math.sqrt(out);
	}

	public static float[] copy(float[] vec) {
		float[] out = new float[vec.length];

		for (int i = 0; i < vec.length; i++)
			out[i] = vec[i];

		return out;
	}
}
