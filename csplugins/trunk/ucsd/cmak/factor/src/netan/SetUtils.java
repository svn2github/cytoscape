package netan;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class SetUtils
{
    /**
     * Put all elements of set A and set B into the "result" set
     *
     * @param
     * @return
     * @throws
     */
    public static void union(Set a, Set b, Set result)
    {
        result.addAll(a);
        result.addAll(b);
    }

    /**
     * Create the union of sets A and B by adding all elements of B
     * to A.
     *
     */
    public static void union(Set a, Set b)
    {
        a.addAll(b);
    }

    /**
     * Find the intersection of two sets.
     * Does not modify set A or set B.
     *
     * @param
     * @return the intersection of sets a and b
     */
    public static Set intersect(Set a, Set b)
    {
        Set inter = new HashSet();
        
        for(Iterator it = a.iterator(); it.hasNext();)
        {
            Object o = it.next();
            if(b.contains(o))
            {
                inter.add(o);
            }
        }

        return inter;
    }
    
}
