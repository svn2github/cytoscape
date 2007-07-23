/* -*-Java-*-
********************************************************************************
*
* File:         AttributeTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/AttributeTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:  
* Author:       Michael L. Creech
* Created:      Wed Sep 21 09:15:03 2005
* Modified:     Thu Sep 22 05:42:16 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:      
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/


package cytoscape.hyperedge.unittest;

import cytoscape.hyperedge.impl.utils.HEUtils;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Test the various HyperEdge Attribute operations.
 * NOTE: This is currently removed.
 * @author Michael L. Creech
 * @version 1.0
 */
public class AttributeTest extends TestBase
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static Test suite ()
    {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(AttributeTest.class);
    }

    public static void main (String[] args)
    {
        junit.textui.TestRunner.run (suite ());
    }

    public void testHyperEdgeAttributes ()
    {
        setUp1 (true);
        HEUtils.log ("THIS TEST IS NOT CURRENTLY ACTIVE.");
        //        CytoscapeData hed = manager.getHyperEdgeData ();
        //        Assert.assertNull (hed.setAttributeValue (he1.getIdentifier (),
        //                                                  "attribute1", new Integer(3)));
        //        Assert.assertTrue (hed.getAttributeValue (he1.getIdentifier (),
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
        // TODO: Add test of deletion of HyperEdge leading to removal
        // of corresponding attributes:
    }
}
