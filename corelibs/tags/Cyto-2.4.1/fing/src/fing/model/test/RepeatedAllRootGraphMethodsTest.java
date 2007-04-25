package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.RootGraph;

public final class RepeatedAllRootGraphMethodsTest
{

  // No constructor.
  private RepeatedAllRootGraphMethodsTest() { }

  public static final void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int numIterations = 100000;
    for (int i = 0; i < numIterations; i++) {
      if (i % 100 == 0)
        System.out.println("Now running iteration " + (i + 1) +
                           " of " + numIterations);
      AllRootGraphMethodsTest.runTest(root); }
  }

}
