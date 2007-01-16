package cytoscape.cruft.obo.test;

import cytoscape.cruft.obo.SynonymReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SynonymTester
{

  public static void main(String[] args) throws Exception
  {
    final BufferedReader assocReader = new BufferedReader
      (new SynonymReader
       (args[0], new InputStreamReader(System.in)));
    while (true) {
      String line = assocReader.readLine();
      if (line == null) break;
      System.out.println(line); }
  }

}
