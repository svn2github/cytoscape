package cytoscape.util.intr.test;

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;

import java.io.IOException;
import java.io.InputStream;

public class IntBTreeQueryTuner
{

  public static void main(String[] args) throws IOException
  {
    int branches = Integer.parseInt(args[0]);
    int N = Integer.parseInt(args[1]);
    if (N < 1000) throw new IllegalStateException("N should be at least 500");
    int[] elements = new int[N];
    InputStream in = System.in;
    byte[] buff = new byte[4];
    int inx = 0; int off = 0; int read;
    while (inx < N && (read = in.read(buff, off, buff.length - off)) > 0) {
      off += read;
      if (off < buff.length) continue;
      else off = 0;
      elements[inx++] = Math.abs(assembleInt(buff)) % N; }
    if (inx < N) throw new IOException("premature end of input");

    IntBTree tree = new IntBTree(branches);
    for (int i = 0; i < elements.length; i++) tree.insert(elements[i]);
    int querySpan = N / 3;
    boolean reverseOrder = false;
    long timeBegin = System.currentTimeMillis();
    for (int i = querySpan; i < 100; i += 3) {
      IntEnumerator iter = tree.searchRange(i, i + querySpan, reverseOrder);
      while (iter.numRemaining() > 0) iter.nextInt();
      reverseOrder = !reverseOrder; }
    long timeEnd = System.currentTimeMillis();
    System.out.println((timeEnd - timeBegin) + " milliseconds");
  }

  private static int assembleInt(byte[] fourConsecutiveBytes)
  {
    int firstByte = (((int) fourConsecutiveBytes[0]) & 0x000000ff) << 24;
    int secondByte = (((int) fourConsecutiveBytes[1]) & 0x000000ff) << 16;
    int thirdByte = (((int) fourConsecutiveBytes[2]) & 0x000000ff) << 8;
    int fourthByte = (((int) fourConsecutiveBytes[3]) & 0x000000ff) << 0;
    return firstByte | secondByte | thirdByte | fourthByte;
  }

}
