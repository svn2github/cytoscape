package cytoscape.util.intr.test;

import cytoscape.util.intr.IntBTree;

import java.io.IOException;
import java.io.InputStream;

public class IntBTreeConstructionTuner
{

  public static void main(String[] args) throws IOException
  {
    int branches = Integer.parseInt(args[0]);
    int N = Integer.parseInt(args[1]);
    int[] elements = new int[N];
    InputStream in = System.in;
    byte[] buff = new byte[4];
    int inx = 0; int off = 0; int read;
    while (inx < N && (read = in.read(buff, off, buff.length - off)) > 0) {
      off += read;
      if (off < buff.length) continue;
      else off = 0;
      elements[inx++] = (0x7fffffff & assembleInt(buff)) % N; }
    if (inx < N) throw new IOException("premature end of input");

    IntBTree tree = new IntBTree(branches);
    long timeBegin = System.currentTimeMillis();
    tree.insert(elements[0]);
    for (int i = 1; i < elements.length; i++) {
      tree.delete(elements[i - 1]);
      tree.insert(elements[i]);
      tree.insert(elements[i - 1]);
      tree.insert(elements[i]);
      tree.delete(elements[i]); }
    if (tree.size() != elements.length) throw new IllegalStateException();
    tree.delete(elements[0]);
    for (int i = 1; i < elements.length; i++) {
      tree.delete(elements[i]);
      tree.insert(elements[i - 1]);
      tree.insert(elements[i]);
      tree.delete(elements[i - 1]);
      tree.delete(elements[i]); }
    if (tree.size() != 0) throw new IllegalStateException();
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
