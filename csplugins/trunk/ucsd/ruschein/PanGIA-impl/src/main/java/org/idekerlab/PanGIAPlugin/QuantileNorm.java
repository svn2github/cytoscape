package org.idekerlab.PanGIAPlugin;


/**
 * A self-contained module for performing quantile normalization.
 * Handles ties by assigning each the average value.
 * @author ghannum 5/4/10
 */
class QuantileNorm
{
	public static float[] quantileNorm(float[] v)
	{
		float[] rank = rankWTies(v);
		
		return divideBy(subtract(rank, .5f),v.length);
	}
	
	private static int[] sort_I(float[] v)
	{
		return Sort_I(copy(v));
	}
	
	private static float[] rankWTies(float[] v)
	{
		int[] order = sort_I(v);
		
		float[] out = new float[order.length];
		
		for (int i=0;i<order.length;i++)
			{
				int j;
				for (j=i+1;j<order.length && v[order[i]]==v[order[j]];j++);
				j--;
			
				if (i==j)
					out[order[i]] = i+1;
				else
					{
						long rsum = 0;
						for (int k=i;k<=j;k++)
							rsum += k+1;
				
						float r = rsum / (float) (j-i+1);
				
						for (int k=i;k<=j;k++)
							out[order[k]] = r;
					}
				
				i = j;
			}
		
		return out;
	}
	
	private static float[] divideBy(float[] v, int val)
	{
		float[] out = new float[v.length];
		
		for (int i=0;i<v.length;i++)
			out[i] = v[i]/val;
		
		return out;
	}
	
	private static float[] subtract(float[] v, float val)
	{
		float[] out = new float[v.length];
		
		for (int i=0;i<v.length;i++)
			out[i] = v[i]-val;
		
		return out;
	}
	
	public static float[] copy(float[] vec)
	{
		float[] out = new float[vec.length];
		
		for (int i=0;i<vec.length;i++)
			out[i] = vec[i];
		
		return out;
	}
	
	public static int[] Sort_I(float[] dv)
	{
		int[] index = new int[dv.length];
		for (int i=0;i<dv.length;i++)
			index[i]=i;
		
		sortwI(dv,0,dv.length,index);
				
		return index;
	}
	
	/**
	 * Sorts the specified sub-array of floats into ascending order.
	 * Adapted from Array.sort() source. 
	 */
	private static void sortwI(float x[], int off, int len, int[] index) {
           	
		// Insertion sort on smallest arrays
		if (len < 7) {
			for (int i=off; i<len+off; i++)
				for (int j=i; j>off && x[j-1]>x[j]; j--)
					swap(x, j, j-1, index);
			return;
		}

		// Choose a partition element, v
		int m = off + (len >> 1);       // Small arrays, middle element
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		float v = x[m];

		// Establish Invariant: v* (<v)* (>v)* v*
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			while (b <= c && x[b] <= v) {
				if (x[b]==v)
					swap(x, a++, b, index);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c]==v)
					swap(x, c, d--, index);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--, index);
		}

		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  vecswap(x, off, b-s, s, index);
		s = Math.min(d-c,   n-d-1);  vecswap(x, b,   n-s, s, index);

		// Recursively sort non-partition-elements
		if ((s = b-a) > 1)
			sortwI(x, off, s,index);
		if ((s = d-c) > 1)
			sortwI(x, n-s, s,index);
	}
    
	/**
	 * Swaps x[a] with x[b].
	 */
	private static void swap(float x[], int a, int b, int[] index) {
		float t = x[a];
		x[a] = x[b];
		x[b] = t;
        
		int ti = index[a];
		index[a] = index[b];
		index[b] = ti;
	}
    
	/**
	 * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
	 */
	private static void vecswap(float x[], int a, int b, int n, int[] index) {
		for (int i=0; i<n; i++, a++, b++)
			swap(x, a, b, index);
	}
    
	/**
	 * Returns the index of the median of the three indexed floats.
	 */
	private static int med3(float x[], int a, int b, int c) {
		return (x[a] < x[b] ?
			(x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
			(x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}
}
