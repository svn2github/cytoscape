import java.util.List;
import java.util.ArrayList;

//import cern.colt.bitvector.BitVector;

public class CachedPathFactorNode extends PathFactorNode
{
    private static int MAX_PATH_LEN = 5;
    
    private List _signCache;
    private List _signCachePLUS;
    private List _signCacheMINUS;
    
    private static PathFactorNode __singleton = new CachedPathFactorNode();
    
    public static PathFactorNode getInstance()
    {
        return __singleton;
    }

    protected CachedPathFactorNode()
    {
        super();
        
        _signCache = new ArrayList(MAX_PATH_LEN);;
        _signCachePLUS = new ArrayList(MAX_PATH_LEN);
        _signCacheMINUS = new ArrayList(MAX_PATH_LEN);

        for(int x=0; x <= MAX_PATH_LEN; x++)
        {
            _signCache.add(super.enumerate(x));
            _signCachePLUS.add(super.enumerate(x, State.PLUS));
            _signCacheMINUS.add(super.enumerate(x, State.MINUS));
        }
            
    }

    protected short[][] enumerate(int numSigns)
    {
        if(numSigns <= MAX_PATH_LEN)
        {
            return (short[][]) _signCache.get(numSigns);
        }

        return super.enumerate(numSigns);
        
    }

    protected short[] enumerate(int numSigns, State pORm)
    {
        if(numSigns <= MAX_PATH_LEN)
        {
            if(pORm == State.PLUS)
            {
                return (short[]) _signCachePLUS.get(numSigns);
            }
            else if(pORm == State.MINUS)
            {
                return (short[]) _signCacheMINUS.get(numSigns);
            }
        }
        return super.enumerate(numSigns, pORm);
    }

}
