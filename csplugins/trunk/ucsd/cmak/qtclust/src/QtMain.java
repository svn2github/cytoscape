import cern.jet.random.engine.MersenneTwister64;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.bitvector.BitVector;

import java.util.List;
import java.util.ArrayList;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class QtMain
{
    private static DoubleArrayList[] readInput(String file) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(file));

        ArrayList lines = new ArrayList();
        String l;
        while( (l = in.readLine()) != null)
        {
            lines.add(l.split("\\s+"));
        }

        String[] header = (String[]) lines.remove(0);

        System.err.println(header.length + " cols: " + header[0]);
        
        DoubleArrayList[] data = new DoubleArrayList[lines.size()];
        int M;
        for(int i=0; i < lines.size(); i++)
        {
            String[] str = (String[]) lines.get(i);
            M = str.length;
            DoubleArrayList d = new DoubleArrayList(M);
            d.setSize(M);
            data[i] = d;
            for(int j=0; j < M; j++)
            {
                d.set(j, Double.parseDouble(str[j]));
            }
        }

        return data;
    }
    
    public static void main(String[] argv) throws Exception
    {
        if(argv.length == 0)
        {
            System.err.println("Usage: <logratio file> [outputfile]");
            System.exit(1);
        }

        String input = argv[0];
        
        QtClust qt = new QtClust();
        
        DoubleArrayList[] data = readInput(input);
        
        System.out.println("Computing distances");
        double[][] d = qt.computeDistances(data);
        System.out.println("done");

        System.out.println("Clustering");
        List clusters = qt.cluster(0.3, d); 
        System.out.println("done");
        
        PrintStream out = System.out;
        if(argv.length > 1)
        {
            out = new PrintStream(new FileOutputStream(argv[1]));
        }

        writeClusters(out, clusters);
    }

    public static void writeClusters(PrintStream out, List clusters)
    {
        for(int x=0; x < clusters.size(); x++)
        {
            IntArrayList c = (IntArrayList) clusters.get(x);
            StringBuffer b = new StringBuffer();
            for(int i=0; i < c.size(); i++)
            {
                b.append(c.get(i));
                if(i != (c.size() - 1))
                {
                    b.append(" ");
                }
            }
            out.println(b.toString());
        }
    }
    
    public static void test(QtClust qt, int N, int M)
    {
        DoubleArrayList[] data = new DoubleArrayList[N];
        MersenneTwister64 mt = new MersenneTwister64();
        
        System.out.println("Initializing array...");
        
        for(int i=0; i < N; i++)
        {
            data[i] = new DoubleArrayList(M);
            data[i].setSize(M);
            for(int j=0; j < M; j++)
            {
                data[i].setQuick(j, mt.raw());
            }
        }

        System.out.println("Computing correlations array");

        double[][] d = qt.computeDistances(data);
        
        System.out.println("done");

        /*
        BitVector orfs = new BitVector(N);
        for(int x=0; x < N; x++)
        {
            orfs.set(x);
        }
        
        IntArrayList c = qt.clustIter(orfs, 0.3, d); 
        System.out.println("cluster: " + c);
        */

        
        List clusters = qt.cluster(0.3, d); 
        for(int x=0; x < clusters.size(); x++)
        {
            System.out.println(x + ": " + clusters.get(x));
        }
    }
}
