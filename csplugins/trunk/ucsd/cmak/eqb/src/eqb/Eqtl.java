package eqb;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Eqtl
{
    private String _gene;
    private Map _loci = new HashMap(); 

    public void setGene(String s)
    {
        _gene = s;
    }

    public void addLocus(String s, Double val)
    {
        _loci.put(s, val);
    }

    public String getGene()
    {
        return _gene;
    }

    public boolean isLocus(String s)
    {
        return _loci.containsKey(s);
    }

    public Double get(String s)
    {
        Object o = _loci.get(s);

        if(o != null)
        {
            return (Double) o;
        }

        return null;
    }

    
    public String toString()
    {

        StringBuffer b = new StringBuffer();
        b.append(_gene);
        b.append(" ");

        if(_loci.size() > 0)
        {
            for(Iterator i = _loci.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry e = (Map.Entry) i.next();
                String node = (String) e.getKey();
                Double val = (Double) e.getValue();
                b.append("[");
                b.append(node);
                b.append(",");
                b.append(val);
                b.append("]");                                
                if(i.hasNext()) { b.append(", "); }
            }
        }
        return b.toString();
    }
}
