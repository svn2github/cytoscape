package org.cytoscape.phylotree.parser;
import org.cytoscape.phylotree.parser.PhylipTreeImpl;
import java.util.*;

public class PhylipTester{
 
  public static void main(String [] args)
  {
    PhylipTreeImpl phyl = new PhylipTreeImpl("((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700,seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201,weasel:18.87953):2.09460):3.87382,dog:25.46154);");
    
    int counter = 0;
    LinkedList<PhylotreeNode> a = phyl.getNodeList();
    
    for(Iterator<PhylotreeNode> i = a.iterator(); i.hasNext();)
    {
    PhylotreeNode f = i.next();
    System.out.println(f.getName());
      counter++;
    }
    
    
  }
}