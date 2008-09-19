package org.cytoscape.model;

import org.cytoscape.model.internal.CyNetworkImpl;
import com.clarkware.junitperf.TimedTest;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestResult;


/**
 * Created by IntelliJ IDEA. User: skillcoy Date: Sep 19, 2008 Time: 3:04:03 PM To change this template use File |
 * Settings | File Templates.
 */
public class TimedAddNodeTest extends TestCase
  {
  private CyNetwork net;
  private int totalNodes = 100000;

  public static Test suite()
    {
    long maxTimeInMillis = 225;
    Test test = new TimedAddNodeTest("testLoadNetwork");
    Test timedTest = new TimedTest(test, maxTimeInMillis, false);
    return timedTest;
    }

  public static void main(String args[]) throws Exception
    {
    TestResult result = junit.textui.TestRunner.run(suite());
    System.out.println("Failures: " + result.failureCount());
    }

  public TimedAddNodeTest(String name)
    {
    super(name);
    }

  public void setUp()
    {
    net = new CyNetworkImpl(new DummyCyEventHelper());
    }

  public void testLoadNetwork()
    {
    for (int i = 0; i < totalNodes; i++)
      net.addNode();
    assertEquals(net.getNodeCount(), totalNodes);
    }


  }
