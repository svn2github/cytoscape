package fgraph;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.util.Collections;
import java.util.List;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import antlr.ANTLRException;

/**
 * A class that parses edge attributes with java.lang.Double values
 *
 */
public class EdgeData
{
    private Map _data;
    private Set _sourceNodes;

    private double THRESH = 1;
    
    public EdgeData(String filename)
        throws FileNotFoundException, BadInputException
    {
        _data = new HashMap();
        _koOrfs = new HashSet();

        EdgeAttrLexer lexer = new EdgeAttrLexer(new FileInputStream(filename));
        EdgeAttrParser parser = new EdgeAttrParser(lexer);

        try
        {
            // Expect a list of 4 element String arrays, a
            // a[0]: source node
            // a[1]: edge type
            // a[2]: target node
            // a[3]: double value
            List d = parser.parse();
            
            System.out.println("Parsed " + d.size() + " data records");
                        
            for(int x=0; x < d.size(); x++)
            {
                String[] t = (String[]) d.get(x);
                _data.put(t[0] + t[2], new Double(t[3]));
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
    public boolean isValueBelowThreshold(String source, String target)
    {
        String key = source + target;
        if(_data.containsKey(key))
        {
            if(((Double) _data.get(key)).doubleValue() <= THRESH)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public double getValue(String source, String target)
    {
        String key = source + target;
        if(_data.containsKey(key))
        {
            return ((Double) _data.get(key)).doubleValue();
        }

        return 1;
    }

    
    public Set getSourceNames()
    {
        return Collections.unmodifiableSet(_sourceNodes);
    }

    
    /**
     * Set the threshold used by expressionChanges to determine whether
     * a knockout causes a target gene's expression to change.
     */
    public void setThreshold(double d)
    {
        THRESH = d;
    }
}
