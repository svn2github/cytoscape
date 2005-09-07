package cytoscape.cruft.obo.test;

import cytoscape.cruft.obo.MolecularFunctionAnnotationReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MolecularFunctionAnnotationTester
{

  public static void main(String[] args) throws Exception
  {
    final BufferedReader assocReader = new BufferedReader
      (new MolecularFunctionAnnotationReader
       (args[0], new InputStreamReader(System.in)));
    while (true) {
      String line = assocReader.readLine();
      if (line == null) break;
      System.out.println(line); }
  }

}
