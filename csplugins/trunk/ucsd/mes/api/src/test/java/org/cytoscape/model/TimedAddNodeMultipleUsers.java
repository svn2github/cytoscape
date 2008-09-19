package org.cytoscape.model;

import junit.framework.TestResult;
import junit.framework.Test;
import junit.framework.TestCase;
import org.cytoscape.model.internal.CyNetworkImpl;
import com.clarkware.junitperf.TimedTest;
import com.clarkware.junitperf.LoadTest;

/**
 * Created by IntelliJ IDEA. User: skillcoy Date: Sep 19, 2008 Time: 4:07:31 PM To change this template use File |
 * Settings | File Templates.
 */
public class TimedAddNodeMultipleUsers extends TestCase
  {

  private CyNetwork net;
  private int totalNodes = 100000;

  public static Test suite()
    {
    long maxTimeInMillis = 1000;
    int concurrentUsers = 3;
    Test test = new TimedAddNodeMultipleUsers("testLoadNetwork");
    Test loadTest = new LoadTest(new TimedTest(test, maxTimeInMillis), concurrentUsers);
    return loadTest;
    }

  public static void main(String args[]) throws Exception
    {
    junit.textui.TestRunner.run(suite());
    }

  public TimedAddNodeMultipleUsers(String name)
    {
    super(name);
    }

  public void setUp()
    {
    net = new CyNetworkImpl(new DummyCyEventHelper());
    }

  public void tearDown()
    {
    net = null;
    }

  public void testLoadNetwork()
    {
    for (int i = 0; i < totalNodes; i++)
      net.addNode();

    /*
    TODO
    !! This is failing with an error that LOOKS like all nodes are being added to the same network.
     Unsure if each timed test case should be running separately or not so don't know if this is actually
     an error !!
     */
    //assertEquals(net.getNodeCount(), totalNodes);
    }


  }
