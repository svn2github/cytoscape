package cytoscape.cruft.obo.test;

import cytoscape.cruft.obo.OboOntologyReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OboOntologyTester
{

  public static void main(String[] args) throws Exception
  {
    final BufferedReader oboReader = new BufferedReader
      (new OboOntologyReader(new InputStreamReader(System.in)));
    while (true) {
      String line = oboReader.readLine();
      if (line == null) break;
      System.out.println(line); }
  }

}
