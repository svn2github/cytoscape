package cytoscape.util.intr.test;

import cytoscape.util.intr.IntStack;
import java.math.BigInteger;

public class PrimesGenerator
{

  public static void main(String[] args)
  {
    final IntStack stack = new IntStack();
    int currSeed = Integer.MAX_VALUE;
    while (currSeed > 10) {
      while (!((new BigInteger(String.valueOf(currSeed))).isProbablePrime(64)))
        currSeed--;
      stack.push(currSeed);
      currSeed = currSeed / 2; }
    while (stack.size() > 0)
      System.out.println(stack.pop());
  }

}
