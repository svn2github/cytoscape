package fgraph;

import junit.framework.*;
import java.util.*;

import cytoscape.data.ExpressionData;
//import cytoscape.data.CachedExpressionData;
import cytoscape.data.mRNAMeasurement;

public class ExpressionTest extends TestCase
{
    private String file = "/cellar/users/cmak/data/data_expts1-300_geneerr.pvals";
    
    protected void setUp()
    {

    }

    
    public void testLoad() throws Exception
    {
        long start = System.currentTimeMillis();
        ExpressionData ed = new ExpressionData(file);

        System.out.println("load done at: " + 
                           (System.currentTimeMillis() - start) + " ms");


        System.out.println(ed.getDescription());
 
        mRNAMeasurement m = ed.getMeasurement("YHR007C", "ade16");

        assertEquals("gene=YHR007C, cond=ade16: ratio", 0.03, m.getRatio(), 0.01);
        assertEquals("gene=YHR007C, cond=ade16: ratio", 0.793, m.getSignificance(), 0.001);
    }

    public void testEdgeExpression() throws Exception
    {
        EdgeExpressionData ed = EdgeExpressionData.load("yeang-ko.eda", 0.02);
        
        String n1 = "YBR245C";
        String n2 = "YHR139C";
        String n3 = "YIL015C-A";
        
        assertEquals("num edges", ed.getNumEdges(), 24457);

        assertEquals(n1 + " " + n2 + " pval", 0.006120, 
                     ed.getPvalue(n1, n2), 0.0000001);
        
        assertEquals(n1 + " " + n3 + " pval", 0.003130,
                     ed.getPvalue(n1, n3),  0.0000001);
        
        assertTrue(n1 + " " + n2 + " below 0.02",
                   ed.isPvalueBelowThreshold(n1, n2));

        assertTrue(n1 + " " + n3 + " below 0.02",
                   ed.isPvalueBelowThreshold(n1, n3));

        assertEquals("bad nodes", -1, ed.getPvalue(n1, n3+"xxx"), 0.01);
            

        mRNAMeasurement m12 = ed.getExpression(n1, n2);
        assertEquals(n1 + " " + n2 + " ratio", 0.337000, m12.getRatio(), 0.0001);
        assertEquals(n1 + " " + n2 + " pval", 0.006120, m12.getSignificance(), 0.0001);
    }
    
    /*
    public void testCached() throws Exception
    {
        //loadCached(1000);
        //loadCached(5000);
        //loadCached(10000);
        //loadCached(15000);
        //loadCached(20000);
        //loadCached(25000);
    }
    */

    /*
    private void loadCached(int sz) throws Exception
    {
        //CachedExpressionData.CACHE_SIZE = sz;
        long start = System.currentTimeMillis();
        ExpressionData ed = new CachedExpressionData(file, sz);
        
        System.out.println("Cache size: " + sz + ".  Load done in: " + 
                           (System.currentTimeMillis() - start) + " ms");
        
        mRNAMeasurement m = ed.getMeasurement("YHR007C", "ade16");
        
        assertEquals("gene=YHR007C, cond=ade16: ratio", 0.03, m.getRatio(), 0.01);
        assertEquals("gene=YHR007C, cond=ade16: ratio", 0.793, m.getSignificance(), 0.001);
    }
    */
    /*
    public void testCache()
    {
        final int MAX_CACHE_ENTRIES = 5;
        Map cache = new LinkedHashMap(MAX_CACHE_ENTRIES+1, 1F, true) {
                // This method is called just after a new entry has been added
                public boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > MAX_CACHE_ENTRIES;
                }
              };

        
        cache.put("1.0", new Double(1));
        cache.put("2.0", new Double(2));
        cache.put("3.0", new Double(3));
        cache.put("4.0", new Double(4));
        cache.put("5.0", new Double(5));
        cache.put("6.0", new Double(6));

        check(cache, "1.0");
        check(cache, "2.0");
        check(cache, "3.0");
        check(cache, "4.0");
        check(cache, "5.0");
        check(cache, "6.0");
        check(cache, "6.0");
        check(cache, "1.0");
    }

    private void check(Map cache, String key)
    {
        if(cache.containsKey(key))
        {
            System.out.println(key + " hit");
        }
        else
        {
            System.out.println(key + " miss [" + key.hashCode() + "]");
        }
    }
    */
}
