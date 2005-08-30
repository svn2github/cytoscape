import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.bitvector.BitVector;
import cern.jet.stat.Descriptive;

import java.text.DecimalFormat;

import java.util.List;
import java.util.ArrayList;

public class QtClust
{
    private static DecimalFormat dformat = new DecimalFormat("0.###");
    
    private double var(DoubleArrayList a)
    {
        double sum = Descriptive.sum(a);
        double ss = Descriptive.sumOfSquares(a);

        return Descriptive.variance(a.size(), sum, ss);
    }

    double jackknife(DoubleArrayList a1, DoubleArrayList a2)
    {
        int N = a1.size();
        
        double sum1 = Descriptive.sum(a1);
        double sum2 = Descriptive.sum(a2);

        double ss1 = Descriptive.sumOfSquares(a1);
        double ss2 = Descriptive.sumOfSquares(a2);

        double sigma1 =
            Descriptive.standardDeviation(Descriptive.variance(N, sum1, ss1));

        double sigma2 =
            Descriptive.standardDeviation(Descriptive.variance(N, sum2, ss2));

        double u1 = sum1/N;
        double u2 = sum2/N;

        double sumOfProd = 0;
        for(int x=0; x < N; x++)
        {
            sumOfProd += a1.getQuick(x) * a2.getQuick(x);
        }
        
        double[] jn = new double[N];

        double v1, v2;
        double e1, e2;
        double tmpU1, tmpU2;
        int s = N - 1;
        double cov;
        
        for(int x=0; x < N; x++)
        {
            e1 = a1.getQuick(x);
            e2 = a2.getQuick(x);
            tmpU1 = (sum1-e1)/s;
            tmpU2 = (sum2-e2)/s;
            v1 = Descriptive.standardDeviation((ss1-(e1*e1))/s - (tmpU1*tmpU1));
            v2 = Descriptive.standardDeviation((ss2-(e2*e2))/s - (tmpU2*tmpU2));

            cov = ((sumOfProd - (e1*e2))/s) - (tmpU1*tmpU2);

            jn[x] = cov/(v1*v2);
        }

        double min = ((sumOfProd/N) - u1*u2)/(sigma1 * sigma2);

        //        System.out.println("  jn: " + min);
        for(int x=0; x < N; x++)
        {
            //System.out.println("  jn" + x + ": " + jn[x]);
            if(jn[x] < min)
            {
                min = jn[x];
            }
        }
        //System.out.println("  min: " + min);
        
        return min;
    }

    public double[][] computeDistances(DoubleArrayList[] ratios)
    {
        int N = ratios.length;
        double[][] d = new double[N][N];
        
        for(int i=0; i < N; i++)
        {
            for(int j=0; j < N; j++)
            {
                d[i][j] = jackknife(ratios[i], ratios[j]);
            }
            
            if(i % 10 == 0)
            {
                System.out.print(".");
            }
        }
        System.out.print("\n");
        return d;
    }

    
    private int whichMax(double[][] dist, int row, BitVector okCols)
    {
        double max = Double.NEGATIVE_INFINITY;
        int maxInd = -1;
        for(int i=(row+1); i < dist[row].length; i++)
        {
            /*
            System.out.println("  which " + okCols.get(i)
                               + " m=" + max
                               + " " + dist[row][i]);
            */
            if(okCols.get(i) && (dist[row][i] > max))
            {
                //System.out.println("  new max " + dist[row][i]);
                max = dist[row][i];
                maxInd = i;
            }
        }
        return maxInd;
    }


    private int whichMax(int[] data)
    {
        int max = Integer.MIN_VALUE;
        int maxInd = -1;
        for(int i=0; i < data.length; i++)
        {
            if(data[i] > max)
            {
                max = data[i];
                maxInd = i;
            }
        }
        return maxInd;
    }
    

    private String toString(double[] d)
    {

        StringBuffer b = new StringBuffer("[");
        for(int x=0; x < d.length; x++)
        {
            b.append(dformat.format(d[x]));
            if(x != d.length - 1) { b.append(", "); }
        }
        b.append("]");

        return b.toString();
    }
    
    IntArrayList clustIter(BitVector orfs,
                           double maxDiameter,
                           double[][] dist)
    {
        int N = orfs.size();
        IntArrayList[] clusters = new IntArrayList[N];
        int[] sizes = new int[N];

        if(orfs.cardinality() == 1)
        {
            sizes[0] = 1;
            clusters[0] = new IntArrayList(1);
            clusters[0].add(orfs.indexOfFromTo(0, N-1, true));
        }
        else
        {
            boolean flag = true;
            int cnt = 0;
            for(int i=0, M=N-1; i < M; i++)
            {
                if(!orfs.get(i)) { continue;}
                
                clusters[i] = new IntArrayList();
                clusters[i].add(i);
                flag = true;
                cnt = N - i;
                BitVector ok = orfs.copy();
                while(flag && (cnt > 0))
                {
                    int j = whichMax(dist, i, ok);

                    /*
                    System.out.println("i=" + i + ", j=" + j);
                    System.out.println(toString(dist[i]));
                    System.out.println(ok);
                    */
                    
                    if( (j < 0) || (1 - dist[i][j]) > maxDiameter)
                    {
                        /*
                        if(j > 0)
                        {
                            System.out.println("  ### i=" + i
                                               + ", j=" + j
                                               + ", d=" + dist[i][j]
                                               + ", ok=" + ok.get(j));
                        }
                        else
                        {
                            System.out.println("  ### i=" + i + ", j=" + j);
                            }*/
                        
                        flag = false;
                    }
                    else
                    {
                        /*
                        System.out.println("   i=" + i
                                           + ", j=" + j
                                           + ", d=" + dist[i][j]
                                           + ", ok=" + ok.get(j));
                        */
                        clusters[i].add(j);
                        ok.clear(j);
                    }
                    cnt--;
                }
                sizes[i] = clusters[i].size();
            }
        }

        int biggest = whichMax(sizes);

        IntArrayList c = clusters[biggest];
        for(int x=0; x < c.size(); x++)
        {
            orfs.clear(c.get(x));
        }

        return c;
    }

    public List cluster(double maxDiameter, double[][] dist)
    {
        BitVector orfs = new BitVector(dist.length);
        for(int x=0; x < dist.length; x++)
        {
            orfs.set(x);
        }
        
        List clusters = new ArrayList();

        while(orfs.cardinality() > 0)
        {
            IntArrayList c = clustIter(orfs, maxDiameter, dist);

            clusters.add(c);

            System.out.println(orfs.cardinality() + " remaining");
        }
        return clusters;
    }
}
