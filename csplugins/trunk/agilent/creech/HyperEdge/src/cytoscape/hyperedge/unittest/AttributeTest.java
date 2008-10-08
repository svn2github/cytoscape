
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
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



package cytoscape.hyperedge.unittest;

import junit.framework.Test;
import junit.framework.TestSuite;
import cytoscape.hyperedge.impl.utils.HEUtils;


/**
 * Test the various HyperEdge Attribute operations.
 * NOTE: This is currently removed.
 * @author Michael L. Creech
 * @version 1.0
 */
public final class AttributeTest extends TestBase {
    // forced CheckStyle:
    /** 
     * Bonehead Checkstyle requires contructor and javadoc.
     */
    public AttributeTest () { super();}
    //~ Methods ////////////////////////////////////////////////////////////////
    /**
     * JUnit method for running tests for this class.
     * @return the Test to peform.
     */
    public static Test suite () {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(AttributeTest.class);
    }
    /**
     * Main for test.
     * @param args standard args to main program
     */
    public static void main (final String[] args) {
        junit.textui.TestRunner.run (suite ());
    }
    /**
     * Test HypeEdge attributes. Not currently used, since HyperEdges don't have their own attributes any more.
     */
    public void testHyperEdgeAttributes () {
        setUp1 (true);
        HEUtils.log ("THIS TEST IS NOT CURRENTLY ACTIVE.");
        //        CyAttributes cAtt = Cytoscape.getNodeAttributes ();
        //       Assert.assertNull (cAtt.setAttributeValue (he1.getIdentifier (),
        //                                                  "attribute1", new Integer(3)));
        //        Assert.assertTrue (cAtt.getAttributeValue (he1.getIdentifier (),
        //                                                  "attribute1").equals (new Integer(3)));
        // Assert.assertTrue (0 == rd.addAttributeListValue (he1.getIdentifier (),
        //                                                          "attribute2",
        //                                                          "first value"));
        //        Assert.assertTrue (1 == hed.addAttributeListValue (he1.getIdentifier (),
        //                                                          "attribute2",
        //                                                          "second value"));
        //
        //        hed.putAttributeKeyValue (he1.getIdentifier (), "attribute3", "first",
        //                                 "first map value");
        //        hed.putAttributeKeyValue (he1.getIdentifier (), "attribute3", "second",
        //                                 "second map value");
        //        Assert.assertTrue (hed.getAttributeValue (he1.getIdentifier (),
        //                                                 "attribute1").equals (new Integer(3)));
        //        List att_list = hed.getAttributeValueList (he1.getIdentifier (),
        //                                                  "attribute2");
        //        Assert.assertTrue ((att_list != null) &&
        //                           ("first value".equals ((String) att_list.get (0))) &&
        //                           ("second value".equals ((String) att_list.get (1))));
        //        Map att_map = hed.getAttributeValuesMap (he1.getIdentifier (),
        //                                                "attribute3");
        //        Assert.assertTrue ((att_map != null) &&
        //                           ("first map value".equals ((String) att_map.get ("first"))) &&
        //                           ("second map value".equals ((String) att_map.get ("second"))));
        //        Iterator it = hed.getObjectKeys ("attribute1");
        //        while (it.hasNext ())
        //        {
        //            HEUtils.log ("key = " + it.next ());
        //        }
        // TODO Add test of deletion of HyperEdge leading to removal
        // of corresponding attributes:
    }
}
