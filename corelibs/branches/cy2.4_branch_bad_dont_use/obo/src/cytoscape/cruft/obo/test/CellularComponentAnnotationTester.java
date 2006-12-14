package cytoscape.cruft.obo.test;

import cytoscape.cruft.obo.CellularComponentAnnotationReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CellularComponentAnnotationTester
{

  public static void main(String[] args) throws Exception
  {
    final BufferedReader assocReader = new BufferedReader
      (new CellularComponentAnnotationReader
       (args[0], new InputStreamReader(System.in)));
    while (true) {
      String line = assocReader.readLine();
      if (line == null) break;
      System.out.println(line); }
  }

}
