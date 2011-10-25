package org.idekerlab.PanGIAPlugin.data;

import java.util.List;

public class Sorter {

	public static IntVector Sort_I(StringVector sv)
	{
		int[] index = new int[sv.size()];
    	for (int i=0;i<sv.size();i++)
    		index[i]=i;
		
		String[] mydata = sv.asStringArray();
		sortwI(mydata,0,sv.size(),index);
		sv.set(mydata);

		if (sv.hasElementNames())
		{
			List<String> temp = sv.getElementNames();
			for (int i=0;i<index.length;i++)
				sv.setElementName(i, temp.get(index[i]));
		}
		
		IntVector ail = new IntVector(index.length);
		for (int i=0;i<index.length;i++)
    		ail.add(index[i]);
		
		return ail;
	}
	
	public static IntVector Sort_I(IntVector iv)
	{
		int[] index = new int[iv.size()];
    	for (int i=0;i<iv.size();i++)
    		index[i]=i;
		
		int[] mydata = iv.asIntArray();
		sortwI(mydata,0,iv.size(),index);
		iv.set(mydata);

		if (iv.hasElementNames())
		{
			List<String> temp = iv.getElementNames();
			for (int i=0;i<index.length;i++)
				iv.setElementName(i, temp.get(index[i]));
		}
		
		IntVector ail = new IntVector(index.length);
		for (int i=0;i<index.length;i++)
    		ail.add(index[i]);
		
		return ail;
	}
	
	
	public static int[] Sort_I(double[] dv)
	{
		int[] index = new int[dv.length];
    	for (int i=0;i<dv.length;i++)
    		index[i]=i;
		
		sortwI(dv,0,dv.length,index);
				
		return index;
	}
	
	public static IntVector Sort_I(DoubleVector dv)
	{
		int[] index = new int[dv.size()];
    	for (int i=0;i<dv.size();i++)
    		index[i]=i;
		
		double[] mydata = dv.asDoubleArray();
		sortwI(mydata,0,dv.size(),index);
		dv = new DoubleVector(mydata);

		if (dv.hasElementNames())
		{
			List<String> temp = dv.getElementNames();
			for (int i=0;i<index.length;i++)
				dv.setElementName(i, temp.get(index[i]));
		}
		
		IntVector ail = new IntVector(index.length);
		for (int i=0;i<index.length;i++)
    		ail.add(index[i]);
		
		return ail;
	}
	
	public static IntVector Sort_I(FloatVector fv)
	{
		int[] index = new int[fv.size()];
    	for (int i=0;i<fv.size();i++)
    		index[i]=i;
		
		float[] mydata = fv.asFloatArray();
		sortwI(mydata,0,fv.size(),index);
		fv = new FloatVector(mydata);

		if (fv.hasElementNames())
		{
			List<String> temp = fv.getElementNames();
			for (int i=0;i<index.length;i++)
				fv.setElementName(i, temp.get(index[i]));
		}
		
		IntVector ail = new IntVector(index.length);
		for (int i=0;i<index.length;i++)
    		ail.add(index[i]);
		
		return ail;
	}
	
	public static IntVector Sort_I(ByteVector dv)
	{
		int[] index = new int[dv.size()];
    	for (int i=0;i<dv.size();i++)
    		index[i]=i;
		
		byte[] mydata = dv.asByteArray();
		sortwI(mydata,0,dv.size(),index);
		dv = new ByteVector(mydata);

		if (dv.hasElementNames())
		{
			List<String> temp = dv.getElementNames();
			for (int i=0;i<index.length;i++)
				dv.setElementName(i, temp.get(index[i]));
		}
		
		IntVector ail = new IntVector(index.length);
		for (int i=0;i<index.length;i++)
    		ail.add(index[i]);
		
		return ail;
	}
    
	
	
	/**
     * Sorts the specified sub-array of doubles into ascending order.
     * Adapted from Array.sort() source. 
     */
    private static void sortwI(double x[], int off, int len, int[] index) {
           	
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
        double v = x[m];

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
    private static void swap(double x[], int a, int b, int[] index) {
        double t = x[a];
        x[a] = x[b];
        x[b] = t;
        
        int ti = index[a];
        index[a] = index[b];
        index[b] = ti;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
     */
    private static void vecswap(double x[], int a, int b, int n, int[] index) {
        for (int i=0; i<n; i++, a++, b++)
            swap(x, a, b, index);
    }

    /**
     * Returns the index of the median of the three indexed doubles.
     */
    private static int med3(double x[], int a, int b, int c) {
        return (x[a] < x[b] ?
                (x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
                (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }
    
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
        double v = x[m];

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
     * Returns the index of the median of the three indexed doubles.
     */
    private static int med3(float x[], int a, int b, int c) {
        return (x[a] < x[b] ?
                (x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
                (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }

    private static void sortwI(int x[], int off, int len, int[] index) {
       	
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
        double v = x[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while(true) {
            while (b <= c && x[b] <= v) {
                if (x[b] == v)
                    swap(x, a++, b, index);
                b++;
            }
            while (c >= b && x[c] >= v) {
                if (x[c] == v)
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
    private static void swap(int x[], int a, int b, int[] index) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
        
        int ti = index[a];
        index[a] = index[b];
        index[b] = ti;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
     */
    private static void vecswap(int x[], int a, int b, int n, int[] index) {
        for (int i=0; i<n; i++, a++, b++)
            swap(x, a, b, index);
    }

    /**
     * Returns the index of the median of the three indexed doubles.
     */
    private static int med3(int x[], int a, int b, int c) {
        return (x[a] < x[b] ?
                (x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
                (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }
    
    private static void sortwI(String x[], int off, int len, int[] index) {
       	
    	// Insertion sort on smallest arrays
        if (len < 7) {
            for (int i=off; i<len+off; i++)
                for (int j=i; j>off && x[j-1].compareTo(x[j])>0; j--)
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
        String v = x[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while(true) {
            while (b <= c && x[b].compareTo(v) <=0) {
                if (x[b].equals(v))
                    swap(x, a++, b, index);
                b++;
            }
            while (c >= b && x[c].compareTo(v)>=0) {
                if (x[c].equals(v))
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
    private static void swap(String x[], int a, int b, int[] index) {
    	String t = x[a];
        x[a] = x[b];
        x[b] = t;
        
        int ti = index[a];
        index[a] = index[b];
        index[b] = ti;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
     */
    private static void vecswap(String x[], int a, int b, int n, int[] index) {
        for (int i=0; i<n; i++, a++, b++)
            swap(x, a, b, index);
    }

    /**
     * Returns the index of the median of the three indexed doubles.
     */
    private static int med3(String x[], int a, int b, int c) {
        return (x[a].compareTo(x[b])<0 ?
                (x[b].compareTo(x[c])<0 ? b : x[a].compareTo(x[c])<0 ? c : a) :
                (x[b].compareTo(x[c])>0 ? b : x[a].compareTo(x[c])>0 ? c : a));
    }

    /**
     * Sorts the specified sub-array of doubles into ascending order.
     * Adapted from Array.sort() source. 
     */
    private static void sortwI(byte x[], int off, int len, int[] index) {
           	
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
        double v = x[m];

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
    private static void swap(byte x[], int a, int b, int[] index) {
    	byte t = x[a];
        x[a] = x[b];
        x[b] = t;
        
        int ti = index[a];
        index[a] = index[b];
        index[b] = ti;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
     */
    private static void vecswap(byte x[], int a, int b, int n, int[] index) {
        for (int i=0; i<n; i++, a++, b++)
            swap(x, a, b, index);
    }

    /**
     * Returns the index of the median of the three indexed doubles.
     */
    private static int med3(byte x[], int a, int b, int c) {
        return (x[a] < x[b] ?
                (x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
                (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }
}
