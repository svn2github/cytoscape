package fgraph;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.logging.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cytoscape.data.mRNAMeasurement;

import antlr.ANTLRException;

/**
 * A class that parses edge attributes with java.lang.Double values
 *
 * 
 * Requirement: only one edge is allowed between every pair of
 * nodes.  If multiple edges are present in the input file, then
 * the last edge in the file will be used.
 */
public class EdgeExpressionData implements ExpressionDataIF
{
    private static Logger logger = Logger.getLogger(EdgeExpressionData.class.getName());
    private Map _data;
    private Set _sourceNodes;

    private double THRESH = 1;

    public static EdgeExpressionData load(String filename, double thresh)
        throws FileNotFoundException, BadInputException
    {
        EdgeExpressionData e = new EdgeExpressionData(filename);
        e.setPvalueThreshold(thresh);

        return e;
    }
    
    public EdgeExpressionData(String filename)
        throws FileNotFoundException, BadInputException
    {
        _data = new HashMap();
        _sourceNodes = new HashSet();

        EdgeAttrLexer lexer = new EdgeAttrLexer(new FileInputStream(filename));
        EdgeAttrParser parser = new EdgeAttrParser(lexer);

        try
        {
            // Expect a list of 4 element String arrays, a
            // a[0]: source node
            // a[1]: edge type
            // a[2]: target node
            // a[3]: double pvalue
            // a[4]: double logratio
            List d = parser.parse();
            
            logger.info("Parsed " + d.size() + " data records from "
                        + filename);
                        
            for(int x=0; x < d.size(); x++)
            {
                String[] t = (String[]) d.get(x);
                _data.put(t[0] + t[2], new mRNAMeasurement(t[4], t[3]));
                
                _sourceNodes.add(t[0]);
            }
        }
        catch(ANTLRException e)
        {
            throw new BadInputException("Exception parsing file: " + filename, e);
        }
    }
    
    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public boolean isPvalueBelowThreshold(String source, String target)
    {
        String key = source + target;
        if(_data.containsKey(key))
        {
            if(((mRNAMeasurement) _data.get(key)).getSignificance() <= THRESH)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the double value associated an edge
     * @throws NoSuchElementException if the edge does not exist
     */
    public double getPvalue(String koNode, String targetNode)
    {
        String key = koNode + targetNode;
        if(_data.containsKey(key))
        {
            return ((mRNAMeasurement) _data.get(key)).getSignificance();
        }

        return -1;
    }


    public mRNAMeasurement getExpression(String koNode, String targetNode)
    {
        String key = koNode + targetNode;
        if(_data.containsKey(key))
        {
            return (mRNAMeasurement) _data.get(key);
        }

        return null;
    }
    
    public int getNumEdges()
    {
        return _data.size();
    }
    
    public String[] getConditionNames()
    {
        return (String[]) _sourceNodes.toArray(new String[1]);
    }

    public String[] getGeneNames()
    {
        throw new RuntimeException("Not yet implemented");
    }
    
    
    /**
     * Set the threshold used by expressionChanges to determine whether
     * a knockout causes a target gene's expression to change.
     */
    public void setPvalueThreshold(double d)
    {
        THRESH = d;
    }

    public double getPvalueThreshold()
    {
        return THRESH;
    }
}
