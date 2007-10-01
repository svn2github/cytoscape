/*
  File: CyAttributesUtilsTest.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.AttributeFilter;
import cytoscape.data.AttributeValueVisitor;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;

import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import giny.model.GraphObject;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// package cytoscape.data;
/**
 *
 */
public class CyAttributesUtilsTest extends TestCase {
	private CyNode testNode1;
	private CyNode testNode2;
	private CyEdge testEdge;

	// track complex p-values:
	private List<Double> PValues = new ArrayList<Double>();
	private int numTSIs;

	// track complex TextSourceInfo values:
	private List<String> TSIValues = new ArrayList<String>();
	private int numPValues;
	private boolean initialized = false;

	//    public static Test suite() {
	//        // Will dynamically add all methods as tests that begin with 'test'
	//        // and have no arguments:
	//        return new TestSuite(CyAttributesUtilsTest.class);
	//    }

	/**
	 * Runs just this one unit test.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(CyAttributesUtilsTest.class);
	}

	protected void setUp() {
		// we only need to do setup once:
		if (initialized) {
			return;
		}

		initialized = true;
		testNode1 = Cytoscape.getCyNode("testNode1", true);
		testNode2 = Cytoscape.getCyNode("testNode2", true);
		testEdge = Cytoscape.getCyEdge(testNode1.getIdentifier(), "Interaction Value",
		                               testNode2.getIdentifier(), Semantics.INTERACTION);
		addAttributes(testNode1);
		addAttributes(testEdge);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testToString() {
		CyAttributes attrs = Cytoscape.getNodeAttributes();
		Assert.assertTrue("BOOLEAN".equals(CyAttributesUtils.toString(attrs.getType("BooleanTest"))));
		Assert.assertTrue("STRING".equals(CyAttributesUtils.toString(attrs.getType("StringTest"))));
		Assert.assertTrue("SIMPLE_MAP".equals(CyAttributesUtils.toString(attrs.getType("MapTest"))));
		Assert.assertTrue("SIMPLE_LIST".equals(CyAttributesUtils.toString(attrs.getType("ListTest"))));
		Assert.assertTrue("COMPLEX".equals(CyAttributesUtils.toString(attrs.getType("TextSourceInfo"))));
		Assert.assertTrue("UNDEFINED".equals(CyAttributesUtils.toString(attrs.getType("foo"))));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testTraverseAttributeValues() {
		// traverse a simple attribute:
		CyAttributesUtils.traverseAttributeValues(testNode1.getIdentifier(), "BooleanTest",
		                                          Cytoscape.getNodeAttributes(),
		                                          new AttributeValueVisitor() {
				public void visitingAttributeValue(String objTraversedID, String attrName,
				                                   CyAttributes attrs, Object[] keySpace,
				                                   Object visitedValue) {
					Assert.assertTrue(Boolean.TRUE.equals(visitedValue));
				}
			});
		// traverse a complex attribute TSIs:
		numTSIs = 0;
		CyAttributesUtils.traverseAttributeValues(testNode1.getIdentifier(), "TextSourceInfo",
		                                          Cytoscape.getNodeAttributes(),
		                                          new AttributeValueVisitor() {
				public void visitingAttributeValue(String objTraversedID, String attrName,
				                                   CyAttributes attrs, Object[] keySpace,
				                                   Object visitedValue) {
					numTSIs++;
					Assert.assertTrue(TSIValues.contains(visitedValue));
				}
			});
		Assert.assertTrue(numTSIs == TSIValues.size());
		// traverse a complex attribute TSIs:
		numPValues = 0;
		CyAttributesUtils.traverseAttributeValues(testNode1.getIdentifier(), "p-valuesTest",
		                                          Cytoscape.getNodeAttributes(),
		                                          new AttributeValueVisitor() {
				public void visitingAttributeValue(String objTraversedID, String attrName,
				                                   CyAttributes attrs, Object[] keySpace,
				                                   Object visitedValue) {
					numPValues++;
					Assert.assertTrue(PValues.contains(visitedValue));
				}
			});
		Assert.assertTrue(numPValues == PValues.size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCopyAttributes() {
		CyNode copyTestNode1 = Cytoscape.getCyNode("copyTestNode1", true);
		CyAttributes attrs = Cytoscape.getNodeAttributes();
		// copy all attributes to copyTestNode1:
		CyAttributesUtils.copyAttributes(testNode1.getIdentifier(), copyTestNode1.getIdentifier(),
		                                 attrs, false);
		testAttributes(copyTestNode1);

		// only copy IntegerTest using restrictive AttributeFilter:
		CyAttributesUtils.copyAttributes(testNode1.getIdentifier(), "copyAnotherTestNode1", attrs,
		                                 new AttributeFilter() {
				public boolean includeAttribute(CyAttributes attrs, String objID, String attrName) {
					return "IntegerTest".equals(attrName);
				}
			}, false);

		Assert.assertTrue(new Integer(6).equals(attrs.getIntegerAttribute("copyAnotherTestNode1",
		                                                                  "IntegerTest")));

		List<String> attrNames = CyAttributesUtils.getAttributeNamesForObj("copyAnotherTestNode1",
		                                                                   attrs);
		Assert.assertTrue(attrNames.size() == 1);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCopyAttribute() {
		CyNode testNode3 = Cytoscape.getCyNode("testNode3", true);
		CyEdge testEdgeCopy = Cytoscape.getCyEdge(testNode1.getIdentifier(), "Interaction Value",
		                                          testNode3.getIdentifier(), Semantics.INTERACTION);
		CyAttributes attrs = Cytoscape.getEdgeAttributes();
		// only copy ListTest attribute:
		CyAttributesUtils.copyAttribute(testEdge.getIdentifier(), testEdgeCopy.getIdentifier(),
		                                "ListTest", attrs, false);

		List<String> listVal = attrs.getListAttribute(testEdgeCopy.getIdentifier(), "ListTest");
		Assert.assertTrue((listVal.size() == 2) && listVal.contains("list test value1")
		                  && listVal.contains("list test value2"));

		List<String> attrNames = CyAttributesUtils.getAttributeNamesForObj(testEdgeCopy
		                                                                                                                                                                                                        .getIdentifier(),
		                                                                   attrs);
		// should have 3 attributes: ListTest and Semantics.INTERACTION, and Semantics.CANONICAL_NAME:                                                                  
		Assert.assertTrue(attrNames.size() == 3);
	}

	private void addAttributes(GraphObject go) {
		String goID = go.getIdentifier();
		CyAttributes attrs = null;

		if (go instanceof CyNode) {
			attrs = Cytoscape.getNodeAttributes();
		} else if (go instanceof CyEdge) {
			attrs = Cytoscape.getEdgeAttributes();
		}

		attrs.setAttribute(goID, "BooleanTest", new Boolean(true));
		attrs.setAttribute(goID, "StringTest", "string test value");
		attrs.setAttribute(goID, "IntegerTest", new Integer(6));
		attrs.setAttribute(goID, "DoubleTest", new Double(5.0));

		List<String> listTestValue = new ArrayList<String>();
		listTestValue.add("list test value1");
		listTestValue.add("list test value2");
		attrs.setListAttribute(goID, "ListTest", listTestValue);

		Map<String, String> mapTestValue = new HashMap<String, String>();
		mapTestValue.put("map key1", "map key1 value");
		mapTestValue.put("map key2", "map key2 value");
		attrs.setMapAttribute(goID, "MapTest", mapTestValue);

		// Now add a complex value to test:
		addComplexAttributes(go, attrs);
	}

	private void addComplexAttributes(GraphObject go, CyAttributes attrs) {
		String goID = go.getIdentifier();
		MultiHashMap mmap = attrs.getMultiHashMap();
		MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();

		if (mmapDef.getAttributeValueType("p-valuesTest") < 0) {
			mmapDef.defineAttribute("p-valuesTest", // most specific values:
			                        MultiHashMapDefinition.TYPE_FLOATING_POINT,
			                        new byte[] {
			                            MultiHashMapDefinition.TYPE_STRING,
			                            MultiHashMapDefinition.TYPE_INTEGER
			                        });
		}

		if (mmapDef.getAttributeValueType("TextSourceInfo") < 0) {
			mmapDef.defineAttribute("TextSourceInfo", // most specific values:
			                        MultiHashMapDefinition.TYPE_STRING,
			                        new byte[] {
			                            MultiHashMapDefinition.TYPE_STRING,
			                            MultiHashMapDefinition.TYPE_INTEGER,
			                            MultiHashMapDefinition.TYPE_INTEGER
			                        });
		}

		PValues.clear();
		mmap.setAttributeValue(goID, "p-valuesTest", new Double(0.5),
		                       new Object[] { "Jojo", new Integer(0) });
		PValues.add(new Double(0.5));
		mmap.setAttributeValue(goID, "p-valuesTest", new Double(0.6),
		                       new Object[] { "Jojo", new Integer(1) });
		PValues.add(new Double(0.6));
		mmap.setAttributeValue(goID, "p-valuesTest", new Double(0.6),
		                       new Object[] { "Jojo", new Integer(2) });
		PValues.add(new Double(0.6));
		mmap.setAttributeValue(goID, "p-valuesTest", new Double(0.7),
		                       new Object[] { "Harry", new Integer(0) });
		PValues.add(new Double(0.7));
		mmap.setAttributeValue(goID, "p-valuesTest", new Double(0.6),
		                       new Object[] { "Harry", new Integer(1) });
		PValues.add(new Double(0.6));

		TSIValues.clear();
		mmap.setAttributeValue(goID, "TextSourceInfo", "url1: sentence1",
		                       new Object[] { "url1", new Integer(0), new Integer(0) });
		TSIValues.add("url1: sentence1");
		mmap.setAttributeValue(goID, "TextSourceInfo", "url1: sentence2",
		                       new Object[] { "url1", new Integer(0), new Integer(1) });
		TSIValues.add("url1: sentence2");
		mmap.setAttributeValue(goID, "TextSourceInfo", "url1: sentence3",
		                       new Object[] { "url1", new Integer(0), new Integer(10) });
		TSIValues.add("url1: sentence3");
		mmap.setAttributeValue(goID, "TextSourceInfo", "url1: publication 1",
		                       new Object[] { "url1", new Integer(1), new Integer(0) });
		TSIValues.add("url1: publication 1");
		mmap.setAttributeValue(goID, "TextSourceInfo", "url2: sentence1",
		                       new Object[] { "url2", new Integer(0), new Integer(6) });
		TSIValues.add("url2: sentence1");
		mmap.setAttributeValue(goID, "TextSourceInfo", "url2: publication 1",
		                       new Object[] { "url2", new Integer(1), new Integer(0) });
		TSIValues.add("url2: publication 1");
	}

	private void testAttributes(GraphObject go) {
		String goID = go.getIdentifier();
		CyAttributes attrs = null;

		if (go instanceof CyNode) {
			attrs = Cytoscape.getNodeAttributes();
		} else if (go instanceof CyEdge) {
			attrs = Cytoscape.getEdgeAttributes();
		}

		Assert.assertTrue(Boolean.TRUE.equals(attrs.getBooleanAttribute(goID, "BooleanTest")));

		Assert.assertTrue("string test value".equals(attrs.getStringAttribute(goID, "StringTest")));
		Assert.assertTrue(new Integer(6).equals(attrs.getIntegerAttribute(goID, "IntegerTest")));
		Assert.assertTrue(new Double(5.0).equals(attrs.getDoubleAttribute(goID, "DoubleTest")));

		List<String> listVal = attrs.getListAttribute(goID, "ListTest");
		Assert.assertTrue((listVal.size() == 2) && listVal.contains("list test value1")
		                  && listVal.contains("list test value2"));

		Map<String, String> mapVal = attrs.getMapAttribute(goID, "MapTest");
		Assert.assertTrue((mapVal.size() == 2) && "map key1 value".equals(mapVal.get("map key1"))
		                  && "map key2 value".equals(mapVal.get("map key2")));
		testComplexAttributes(goID, attrs);
	}

	private void testComplexAttributes(String goID, CyAttributes attrs) {
		MultiHashMap mmap = attrs.getMultiHashMap();
		Assert.assertTrue(new Double(0.5).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Jojo", new Integer(0)
		                                                                })));
		Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Jojo", new Integer(1)
		                                                                })));
		Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Jojo", new Integer(2)
		                                                                })));
		Assert.assertTrue(new Double(0.7).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Harry", new Integer(0)
		                                                                })));
		Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Harry", new Integer(1)
		                                                                })));

		Assert.assertTrue("url1: sentence1".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url1", new Integer(0),
		                                                                      new Integer(0)
		                                                                  })));
		Assert.assertTrue("url1: sentence2".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url1", new Integer(0),
		                                                                      new Integer(1)
		                                                                  })));
		Assert.assertTrue("url1: sentence3".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url1", new Integer(0),
		                                                                      new Integer(10)
		                                                                  })));

		Assert.assertTrue("url1: publication 1".equals(mmap.getAttributeValue(goID,
		                                                                      "TextSourceInfo",
		                                                                      new Object[] {
		                                                                          "url1",
		                                                                          new Integer(1),
		                                                                          new Integer(0)
		                                                                      })));

		Assert.assertTrue("url2: sentence1".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url2", new Integer(0),
		                                                                      new Integer(6)
		                                                                  })));
		Assert.assertTrue("url2: publication 1".equals(mmap.getAttributeValue(goID,
		                                                                      "TextSourceInfo",
		                                                                      new Object[] {
		                                                                          "url2",
		                                                                          new Integer(1),
		                                                                          new Integer(0)
		                                                                      })));
	}
}
