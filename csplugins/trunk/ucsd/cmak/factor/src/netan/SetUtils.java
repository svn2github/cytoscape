package netan;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class SetUtils
{

    public static void union(Set a, Set b, Set result)
    {
        result.addAll(a);
        result.addAll(b);
    }

    public static void union(Set a, Set b)
    {
        a.addAll(b);
    }

}
