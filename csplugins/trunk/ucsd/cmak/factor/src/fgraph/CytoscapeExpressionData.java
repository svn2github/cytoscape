package fgraph;

import cytoscape.data.ExpressionData;
import cytoscape.data.mRNAMeasurement;


public class CytoscapeExpressionData implements ExpressionDataIF
{
    private ExpressionData _data;
    private double PVAL_THRESH = 1;

    public static CytoscapeExpressionData load(String file, double pvalue)
    {
        CytoscapeExpressionData d = new CytoscapeExpressionData(file);
        d.setPvalueThreshold(pvalue);
        
        return d;
    }
    
    public CytoscapeExpressionData(String filename)
    {
        _data = new ExpressionData(filename);
    }
    
    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public boolean isPvalueBelowThreshold(String koNode, String targetNode)
    {           
        mRNAMeasurement m = _data.getMeasurement(targetNode, 
                                                 koNode);
        if(m != null)
        {
            return m.getSignificance() <= PVAL_THRESH;
        }
        
        return false;
    }

    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public double getPvalue(String koNode, String targetNode)
    {
        mRNAMeasurement m = _data.getMeasurement(targetNode, 
                                                 koNode);
        if(m != null)
        {
            return m.getSignificance();
        }
        
        return -1;
    }


    public String[] getGeneNames()
    {
        return _data.getGeneNames();
    }
    
    public String[] getConditionNames()
    {
        return _data.getConditionNames();
    }

    
    /**
     * Set the threshold used by expressionChanges to determine whether
     * a knockout causes a target gene's expression to change.
     */
    public void setPvalueThreshold(double d)
    {
        PVAL_THRESH = d;
    }

    /**
     * Set the threshold used by expressionChanges to determine whether
     * a knockout causes a target gene's expression to change.
     */
    public double getPvalueThreshold()
    {
        return PVAL_THRESH;
    }

    
    public mRNAMeasurement getExpression(String koNode, String targetNode)
    {
        mRNAMeasurement m = _data.getMeasurement(targetNode, 
                                                 koNode);
        
        return m;
    }
}
