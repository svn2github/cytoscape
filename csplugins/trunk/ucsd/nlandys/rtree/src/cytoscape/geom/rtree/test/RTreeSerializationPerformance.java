package cytoscape.geom.rtree.test;

import cytoscape.geom.rtree.RTree;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RTreeSerializationPerformance
{

  public static void main(String[] args) throws Exception
  {
    RTree tree = new RTree();
    final int N = Integer.parseInt(args[0]);

    // Populate the tree with entries.
    {
      double sqrtN = Math.sqrt((double) N);
      InputStream in = System.in;
      byte[] buff = new byte[16];
      int inx = 0;
      int off = 0;
      int read;
      while (inx < N && (read = in.read(buff, off, buff.length - off)) > 0) {
        off += read;
        if (off < buff.length) continue;
        else off = 0;
        int nonnegative = 0x7fffffff & assembleInt(buff, 0);
        double centerX = ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & assembleInt(buff, 4);
        double centerY = ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & assembleInt(buff, 8);
        double width =
          (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
        nonnegative = 0x7fffffff & assembleInt(buff, 12);
        double height =
          (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
        tree.insert(inx,
                    (float) (centerX - (width / 2.0d)),
                    (float) (centerY - (height / 2.0d)),
                    (float) (centerX + (width / 2.0d)),
                    (float) (centerY + (height / 2.0d)));
        inx++; }
      if (inx < N) throw new IOException("premature end of input");
      for (inx = 0; inx < N; inx += 2) { // Delete half of the entries.
        tree.delete(inx); }
    }

    final byte[] serializedData;
    {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
      final long millisBegin = System.currentTimeMillis();
      objOut.writeObject(tree); objOut.flush(); objOut.close();
      final long millisEnd = System.currentTimeMillis();
      System.err.println("serialization took " + (millisEnd - millisBegin) +
                         " milliseconds");
      serializedData = byteOut.toByteArray();
    }

    System.err.println("serialized stream is " + serializedData.length +
                       " bytes long");

    {
      ByteArrayInputStream byteIn =
        new ByteArrayInputStream(serializedData);
      final long millisBegin = System.currentTimeMillis();
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      tree = (RTree) objIn.readObject();
      objIn.close();
      final long millisEnd = System.currentTimeMillis();
      System.err.println("deserialization took " + (millisEnd - millisBegin) +
                         " milliseconds");
    }
  }

  private static int assembleInt(byte[] bytes, int offset)
  {
    int firstByte = (((int) bytes[offset]) & 0x000000ff) << 24;
    int secondByte = (((int) bytes[offset + 1]) & 0x000000ff) << 16;
    int thirdByte = (((int) bytes[offset + 2]) & 0x000000ff) << 8;
    int fourthByte = (((int) bytes[offset + 3]) & 0x000000ff) << 0;
    return firstByte | secondByte | thirdByte | fourthByte;
  }

}
