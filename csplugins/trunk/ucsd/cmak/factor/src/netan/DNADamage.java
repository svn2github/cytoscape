package netan;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import fgraph.CytoscapeExpressionData;
import fgraph.ExpressionDataIF;

public class DNADamage
{
    static String BUF_DIR = "/cellar/users/cmak/data/buffering/";
    static String FISHER_COMBINED = BUF_DIR + "FisherCombinedPvalues_adjusted.logratios.pvalues";

    static String BUF_DATA = BUF_DIR + "TF_KOs_orf.logratios.pscores4.tab2";
    
    static double EXP_VAL = 0.0001;
    static double BUF_VAL = 0.78;
    
    public DNADamage()
    {
    }

    /**
     * Read Wild-type expression changes in response to DNA damage
     *
     * @return a set of gene names
     */
    public Set readWTExpression()
    {
        ExpressionDataIF wtExpression =
            CytoscapeExpressionData.load(FISHER_COMBINED, EXP_VAL);
       
        String[] genes = wtExpression.getGeneNames();
        String wt = "WT";
        
        Set diffExp = new HashSet();
        for(int x=0; x < genes.length; x++)
        {
            if(wtExpression.isPvalueBelowThreshold(wt, genes[x]))
            {
                diffExp.add(genes[x]);
            }
        }


        return diffExp;
    }

    public Map readBuffering()
    {
        ExpressionDataIF buf =
            CytoscapeExpressionData.load(BUF_DATA, BUF_VAL);
       
        String[] genes = buf.getGeneNames();
        String[] kos = buf.getConditionNames();
        

        Map buffered = new HashMap();

        for(int k=0; k < kos.length; k++)
        {
            if(!kos[k].equals("WT_MAY2004"))
            {
                Set targets = new HashSet();
                buffered.put(kos[k], targets);
                
                for(int x=0; x < genes.length; x++)
                {
                    if(buf.isPvalueBelowThreshold(kos[k], genes[x]))
                    {
                        targets.add(genes[x]);
                    }
                }
            }
        }

        return buffered;
    }
    
    
    public static void main(String[] args)
    {
        DNADamage dd = new DNADamage();

        Set diffExpressed = dd.readWTExpression();
        Map buffered = dd.readBuffering();

        System.out.println("Diff Exp: " + diffExpressed.size()
                           + " genes at p < " + EXP_VAL);


        System.out.println("Buffering value = " + BUF_VAL);
        Set union = new HashSet();
        for(Iterator kos=buffered.entrySet().iterator(); kos.hasNext();)
        {
            Map.Entry e = (Map.Entry) kos.next();
            String ko = (String) e.getKey();

            Set targets = (Set) e.getValue();

            union.addAll(targets);
            
            System.out.println("Buffering " + GeneNameMap.getName(ko) +
                               " numTargets= " + targets.size());
        }
        
        System.out.println("Total buffered genes: " + union.size());

    }
}
