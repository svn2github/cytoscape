package bool;

import junit.framework.*;
import java.util.*;
import luna.LunaRootGraph;
import giny.model.RootGraph;

public class FactorGraphTest extends TestCase
{
    FactorGraph graph;
    int fw = 0;
    int wet = 0;

    protected void setUp()
    {
        RootGraph f = new LunaRootGraph();
        
        graph = new FactorGraph(f);
            
        FunctionTable fst = new FunctionTable(2, State.BOOLEAN_SET);
        fst.setProb(0, .1);
        fst.setProb(1, .9);
        fst.setProb(2, .5);
        fst.setProb(3, .5);
        
        FunctionTable frt = new FunctionTable(2, State.BOOLEAN_SET);
        frt.setProb(0, .8);
        frt.setProb(1, .2);
        frt.setProb(2, .2);
        frt.setProb(3, .8);
        
        FunctionTable fwt = new FunctionTable(3, State.BOOLEAN_SET);
        fwt.setProb(0, .2475);
        fwt.setProb(1, .0025);
        fwt.setProb(2, .225);
        fwt.setProb(3, .025);
        fwt.setProb(4, .225);
        fwt.setProb(5, .025);
        fwt.setProb(6, 0);
        fwt.setProb(7, .25);
        
        
        int night = graph.createNode(new BooleanAttributes(.6, .4));
        int fs = graph.createNode(fst);
        int sprinkler = graph.createNode(new BooleanAttributes(.5, .5));
        
        int cloudy = graph.createNode(new BooleanAttributes(.5, .5));
        int fr = graph.createNode(frt);
        int rain = graph.createNode(new BooleanAttributes(.5, .5));
        
        fw = graph.createNode(fwt);
        wet = graph.createNode(new BooleanAttributes(.5, .5));
        
        graph.createDirectedEdges(night, fs);
        fst.setNode2Var(night, 0);
        
        graph.createDirectedEdges(fs, sprinkler);
        fst.setNode2Var(sprinkler, 1);
        
        graph.createDirectedEdges(cloudy, fr);
        frt.setNode2Var(cloudy, 0);
        
        graph.createDirectedEdges(fr, rain);
        frt.setNode2Var(rain, 1);
        
        graph.createDirectedEdges(sprinkler, fw);
        fwt.setNode2Var(sprinkler, 0);
        
        graph.createDirectedEdges(rain, fw);
        fwt.setNode2Var(rain, 1);
        
        graph.createDirectedEdges(fw, wet);
        fwt.setNode2Var(wet, 2);
    }
    
    public void testSumProduct() throws AlgorithmException
    {
        graph.sumProduct();

        Message m = graph.getMessage(fw, wet);
        assertNotNull("No message on last edge", m);
        assertEquals(.5787, m.getProb(Message.T), .00001);
        assertEquals(.4213, m.getProb(Message.F), .00001);
    }
}
