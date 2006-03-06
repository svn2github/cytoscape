
/*
  File: CyProjectTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import cytoscape.CyProject;
//-----------------------------------------------------------------------------------------
public class CyProjectTest extends TestCase {
//------------------------------------------------------------------------------
public CyProjectTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testAll() throws Exception {
    String filename = "src/cytoscape/unitTests/sampleProject.pro";
    File projectFile = new File(filename);
    File directory = projectFile.getAbsoluteFile().getParentFile();
    CyProject project = new CyProject(filename);
    
    assertTrue( project.getProjectFilename().equals(filename) );
    String intName = (new File(directory, "network.sif")).getPath();
    assertTrue( project.getInteractionsFilename().equals(intName) );
    String geomName = (new File(directory, "network.gml")).getPath();
    assertTrue( project.getGeometryFilename().equals(geomName) );
    String exprName = (new File(directory, "yabba.mrna")).getPath();
    assertTrue( project.getExpressionFilename().equals(exprName) );
    assertTrue( project.getNumberOfNodeAttributeFiles() == 4 );
    String noaName = (new File(directory, "age.noa")).getPath();
    assertTrue( project.getNodeAttributeFilenames()[0].equals(noaName) );
    assertTrue( project.getNodeAttributeFilenames()[1].endsWith("commonName.noa") );
    assertTrue( project.getNodeAttributeFilenames()[2].endsWith("frillycurtainlength.noa") );
    assertTrue( project.getNodeAttributeFilenames()[3].endsWith("species.noa") );
    assertTrue( project.getNumberOfEdgeAttributeFiles() == 3 );
    String edaName = (new File(directory, "edgeancillaryname.eda")).getPath();
    assertTrue( project.getEdgeAttributeFilenames()[0].equals(edaName) );
    assertTrue( project.getEdgeAttributeFilenames()[1].endsWith("edgeweight.eda") );
    assertTrue( project.getEdgeAttributeFilenames()[2].endsWith("interaction.eda") );
    assertTrue( project.getBioDataDirectory().equals("jar://privateServer") );
    assertTrue( project.getDefaultSpeciesName().equals("Saccharomyces cerevisiae") );
    assertTrue( project.getDefaultLayoutStrategy().equals("frisbee") );
    assertTrue( project.getProjectPropsFileName().equals("special.props") );
    assertTrue( project.getProjectVizmapPropsFileName().equals("vspecial.props") );
    assertTrue( project.getCanonicalize() == false );
    assertTrue( project.getOtherArgs().length == 2 );
    assertTrue( project.getOtherArgs()[0].equals("more") );
    assertTrue( project.getOtherArgs()[1].equals("args") );
}
//-------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyProjectTest.class));
}
//-------------------------------------------------------------------------
}

