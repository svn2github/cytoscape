import java.util.ArrayList;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChiSquaredDistribution
{
    private double[] x;
    private double[] cdf; // 1 - CDF

    private int insertionPointBound;

    private static String table = "/cellar/users/cmak/code/factor/chi2cdf1table_rev.txt";

    private static ChiSquaredDistribution __singleton = null;

    static
    {
        if(__singleton == null)
        {
            try
            {
                __singleton = new ChiSquaredDistribution(table);
            }
            catch(Exception e)
            {
                System.err.println("Error initializing ChiSquaredDistribution");
            }
        }
    }

    private ChiSquaredDistribution(String table) 
        throws FileNotFoundException, IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(table));

        String line;

        ArrayList lines = new ArrayList();

        while((line = in.readLine()) != null)
        {
            if(line.matches("^\\d.*"))
            {
                lines.add(line);
            }
        }

        x = new double[lines.size()];
        cdf = new double[lines.size()];
        insertionPointBound = -1 * (cdf.length);
        
        for(int y=0; y < lines.size(); y++)
        {
            String[] vals = ((String) lines.get(y)).split("\\s+");
            if(vals.length == 3)
            {
                x[y] = Double.parseDouble(vals[0]);
                cdf[y] = Double.parseDouble(vals[2]);

                //System.out.println("chi[" + y + "] " + x[y] + " " + cdf[y]);
            }
            else
            {
                System.err.println("Error reading chi-squared table: "
                                   + lines.get(y));
            }
        }

        in.close();
    }
    
    public static double inverseCDFMinus1(double pval)
    {
        return __singleton._inverseCDFMinus1(pval);
    }

    /**
     * @return The inverse of the CDF of the chi-squared distribution for
     * (1-pval)
     */
    private double _inverseCDFMinus1(double pval)
    {
        if(pval <= 0)
        {
            return x[0];
        }
        else if(pval >=1)
        {
            return 0;
        }

        int index = Arrays.binarySearch(cdf, pval);
        
        if(index >= 0)
        {
            return x[index];
        }
        else if (index <= insertionPointBound)
        {
            return 0f;
        }
        else
        {
            /* If pval is not exactly one of the computed CDF values
             * then binarySearch returns (-(insertionPoint) - 1)
             */
            int i = (index + 1) * -1; // insertionPoint

            /**
             * Interpolate to estimate x.
             */
            return x[i-1] + 
                ((pval - cdf[i-1])*(x[i] - x[i-1])/(cdf[i] - cdf[i-1]));
        }
    }
}
