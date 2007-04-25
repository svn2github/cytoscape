package cytoscape.cruft.obo.test;

import cytoscape.cruft.obo.BiologicalProcessAnnotationReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BiologicalProcessAnnotationTester
{

  public static void main(String[] args) throws Exception
  {
    final BufferedReader assocReader = new BufferedReader
      (new BiologicalProcessAnnotationReader
       (args[0], new InputStreamReader(System.in)));
    while (true) {
      String line = assocReader.readLine();
      if (line == null) break;
      System.out.println(line); }
  }

}
