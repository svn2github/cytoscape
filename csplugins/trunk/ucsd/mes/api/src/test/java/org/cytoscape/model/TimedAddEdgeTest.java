package org.cytoscape.model;

import com.clarkware.junitperf.TimedTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Created by IntelliJ IDEA. User: skillcoy Date: Sep 29, 2008 Time: 1:40:43 PM To change this template use File |
 * Settings | File Templates.
 */
/*
public class TimedAddEdgeTest extends TestCase
  {
  private CyNetwork net;
  private static final int totalEdges = 100000;
  private static final long maxTimeInMillis = 350;
  */
/**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
/*
  public static Test suite()
    {
    Test test = new TimedAddEdgeTest("testLoadNetwork");
    Test timedTest = new TimedTest(test, maxTimeInMillis, false);
    return timedTest;
    }

  */
/**
   * DOCUMENT ME!
   *
   * @param args DOCUMENT ME!
   * @throws Exception DOCUMENT ME!
   */
/*
  public static void main(String[] args) throws Exception
    {
    TestResult result = junit.textui.TestRunner.run(suite());
    }

  public TimedAddEdgeTest(String name)
    {
    super(name);
    }

  */
/** DOCUMENT ME! */
/*
  public void setUp()
    {
    net = CyNetworkFactory.getInstance();
    }

  */
/** DOCUMENT ME! */
/*
  public void testLoadNetwork()
    {
    int lastNodeIndex = -1; // ??? Can node indicies be negative?  If so we need a better test
    for (int i = 0; i < totalEdges; i++)
      {
      CyNode source = net.addNode();
      CyNode target;
      if (lastNodeIndex >= 0)
        target = net.getNode(lastNodeIndex);
      else
        target = net.addNode();

      lastNodeIndex = target.getIndex();
      net.addEdge(source, target, false);
      }

    assertEquals(net.getEdgeCount(), totalEdges);
    assertEquals(net.getNodeCount(), totalEdges + 1);
    }


  }
*/
