import junit.framework.*;
import java.util.*;

public class ChiTest extends TestCase
{
    protected void setUp() throws Exception
    {
    }

    public void testChi()
    {
        assertEquals("p=1", 0, ChiSquaredDistribution.inverseCDFMinus1(1d), .001);
        assertEquals("p=2.653433e-14", 57.98, ChiSquaredDistribution.inverseCDFMinus1(2.653433e-14d), .01);
        assertEquals("p=.9203433", 0.01, ChiSquaredDistribution.inverseCDFMinus1(.9203443d), .01);

        System.out.println(ChiSquaredDistribution.inverseCDFMinus1(0.00523d));
    }
}
