package fgraph;

import cytoscape.data.mRNAMeasurement;

public interface ExpressionDataIF
{
    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public boolean isPvalueBelowThreshold(String koNode, String targetNode);

    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public double getPvalue(String koNode, String targetNode);
    
    public String[] getConditionNames();
    
    /**
     * Set the threshold used by expressionChanges to determine whether
     * a knockout causes a target gene's expression to change.
     */
    public void setPvalueThreshold(double d);

    public double getPvalueThreshold();

    public mRNAMeasurement getExpression(String koNode, String targetNode);
}
