package bool;

import junit.framework.*;
import java.util.*;

public class FuncTest extends TestCase
{
    FunctionTable t;
    FunctionTable t2;
    Message m0;
    Message m1;
    Message mS;
    Message mR, mW;


    protected void setUp()
    {
        t = new FunctionTable(2, State.BOOLEAN_SET);
        t.setId(5);
        t.setNode2Var(0, 0);
        t.setNode2Var(1, 1);
        t.setProb(0, .3);
        t.setProb(1, .2);
        t.setProb(2, .4);
        t.setProb(3, .1);

        t.print();

        t2 = new FunctionTable(3, State.BOOLEAN_SET);
        t2.setId(4);
        t2.setNode2Var(0, 0);
        t2.setNode2Var(1, 1);
        t2.setNode2Var(2, 2);
        t2.setProb(0, .2475);
        t2.setProb(1, .0025);
        t2.setProb(2, .225);
        t2.setProb(3, .025);
        t2.setProb(4, .225);
        t2.setProb(5, .025);
        t2.setProb(6, 0);
        t2.setProb(7, .25);

        t2.print();

        m0 = new Message(0);
        m0.setProb(Message.T, .4);
        m0.setProb(Message.F, .6);

        m1 = new Message(1);
        m1.setProb(Message.T, .7);
        m1.setProb(Message.F, .3);

        
        mS = new Message(0);
        mS.setProb(Message.T, .26);
        mS.setProb(Message.F, .74);

        mR = new Message(1);
        mR.setProb(Message.T, .5);
        mR.setProb(Message.F, .5);

        mW = new Message(2);
        mW.setProb(Message.T, .8);
        mW.setProb(Message.F, .2);

    }

    public void testSumProduct1()
    {
        System.out.println("sumProductTest1");

        List msgs = new ArrayList();
        msgs.add(m0);        

        Message result = t.sumProduct(msgs, 1);

        assertEquals(result.getSourceId(), t.getId());
        assertEquals(.72, result.getProb(BooleanAttributes.T), .001);
        assertEquals(.28, result.getProb(BooleanAttributes.F), .001);
    }


    public void testSumProduct2()
    {
        System.out.println("sumProductTest2");

        List msgs = new ArrayList();
        msgs.add(m1);

        Message result = t.sumProduct(msgs, 0);

        assertEquals(result.getSourceId(), t.getId());
        assertEquals(.27/.58, result.getProb(BooleanAttributes.T), .0001);
        assertEquals(.31/.58, result.getProb(BooleanAttributes.F), .0001);
    }



    public void testSumProduct3()
    {
        System.out.println("sumProductTest3");

        List msgs = new ArrayList();
        msgs.add(mS);
        msgs.add(mR);

        Message result = t2.sumProduct(msgs, 2);

        assertEquals(result.getSourceId(), t2.getId());
        assertEquals(.5787, result.getProb(BooleanAttributes.T), .0001);
        assertEquals(.4213, result.getProb(BooleanAttributes.F), .0001);
    }


    public void testSumProduct4()
    {
        System.out.println("sumProductTest4");

        List msgs = new ArrayList();
        msgs.add(mS);
        msgs.add(mW);

        Message result = t2.sumProduct(msgs, 1);

        assertEquals(result.getSourceId(), t2.getId());
        assertEquals(.18851/.27361, result.getProb(BooleanAttributes.T), .00001);
        assertEquals(.0851/.27361, result.getProb(BooleanAttributes.F), .00001);
    }



    public static Test suite() {
        return new TestSuite(FuncTest.class);
    }

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }
}
