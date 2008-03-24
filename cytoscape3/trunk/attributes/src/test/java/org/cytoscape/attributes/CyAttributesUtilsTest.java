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
package org.cytoscape.attributes;


import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class CyAttributesUtilsTest extends TestCase {
	private String testNode1;
	private String testNode2;
	private String testEdge;

	// track complex p-values:
	private List<Double> PValues = new ArrayList<Double>();
	private int numTSIs;

	// track complex TextSourceInfo values:
	private List<String> TSIValues = new ArrayList<String>();
	private int numPValues;
	private boolean initialized = false;


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
		testNode1 = "testNode1";
		testNode2 = "testNode2";
		testEdge = "testNode1 (pp) testNode2";
		addAttributes(testNode1,CyAttributesFactory.getCyAttributes("node"));
		addAttributes(testEdge,CyAttributesFactory.getCyAttributes("edge"));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testToString() {
		CyAttributes attrs = CyAttributesFactory.getCyAttributes("node");
		assertTrue("BOOLEAN".equals(CyAttributesUtils.toString(attrs.getType("BooleanTest"))));
		assertTrue("STRING".equals(CyAttributesUtils.toString(attrs.getType("StringTest"))));
		assertTrue("SIMPLE_MAP".equals(CyAttributesUtils.toString(attrs.getType("MapTest"))));
		assertTrue("SIMPLE_LIST".equals(CyAttributesUtils.toString(attrs.getType("ListTest"))));
		assertTrue("COMPLEX".equals(CyAttributesUtils.toString(attrs.getType("TextSourceInfo"))));
		assertTrue("UNDEFINED".equals(CyAttributesUtils.toString(attrs.getType("foo"))));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testTraverseAttributeValues() {
		// traverse a simple attribute:
		CyAttributesUtils.traverseAttributeValues(testNode1, "BooleanTest",
		                                          CyAttributesFactory.getCyAttributes("node"),
		                                          new AttributeValueVisitor() {
				public void visitingAttributeValue(String objTraversedID, String attrName,
				                                   CyAttributes attrs, Object[] keySpace,
				                                   Object visitedValue) {
					assertTrue(Boolean.TRUE.equals(visitedValue));
				}
			});
		// traverse a complex attribute TSIs:
		numTSIs = 0;
		CyAttributesUtils.traverseAttributeValues(testNode1, "TextSourceInfo",
		                                          CyAttributesFactory.getCyAttributes("node"),
		                                          new AttributeValueVisitor() {
				public void visitingAttributeValue(String objTraversedID, String attrName,
				                                   CyAttributes attrs, Object[] keySpace,
				                                   Object visitedValue) {
					numTSIs++;
					assertTrue(TSIValues.contains(visitedValue));
				}
			});
		assertTrue(numTSIs == TSIValues.size());
		// traverse a complex attribute TSIs:
		numPValues = 0;
		CyAttributesUtils.traverseAttributeValues(testNode1, "p-valuesTest",
		                                          CyAttributesFactory.getCyAttributes("node"),
		                                          new AttributeValueVisitor() {
				public void visitingAttributeValue(String objTraversedID, String attrName,
				                                   CyAttributes attrs, Object[] keySpace,
				                                   Object visitedValue) {
					numPValues++;
					assertTrue(PValues.contains(visitedValue));
				}
			});
		assertTrue(numPValues == PValues.size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCopyAttributes() {
		String copyTestNode1 = "copyTestNode1";
		CyAttributes attrs = CyAttributesFactory.getCyAttributes("node");
		// copy all attributes to copyTestNode1:
		CyAttributesUtils.copyAttributes(testNode1, copyTestNode1,
		                                 attrs, false);
		testAttributes(copyTestNode1,attrs);

		// only copy IntegerTest using restrictive AttributeFilter:
		CyAttributesUtils.copyAttributes(testNode1, "copyAnotherTestNode1", attrs,
		                                 new AttributeFilter() {
				public boolean includeAttribute(CyAttributes attrs, String objID, String attrName) {
					return "IntegerTest".equals(attrName);
				}
			}, false);

		assertTrue(new Integer(6).equals(attrs.getIntegerAttribute("copyAnotherTestNode1",
		                                                                  "IntegerTest")));

		List<String> attrNames = CyAttributesUtils.getAttributeNamesForObj("copyAnotherTestNode1",
		                                                                   attrs);
		assertTrue(attrNames.size() == 1);
	}

	/**
	 *  DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked") // stupid attrs
	public void testCopyAttribute() {
		String testNode3 = "testNode3";
		String testEdgeCopy = "testNode1 (pp) testNode3";
		CyAttributes attrs = CyAttributesFactory.getCyAttributes("edge");
		// only copy ListTest attribute:
		CyAttributesUtils.copyAttribute(testEdge, testEdgeCopy, "ListTest", attrs, false);

		List<String> listVal = attrs.getListAttribute(testEdgeCopy, "ListTest");
		assertTrue((listVal.size() == 2));
		assertTrue(listVal.contains("list test value1"));
        assertTrue( listVal.contains("list test value2"));

		List<String> attrNames = CyAttributesUtils.getAttributeNamesForObj(testEdgeCopy, attrs);
		assertTrue(attrNames.size() == 1); // only ListTest has been copied 
	}

	private void addAttributes(String goID,CyAttributes attrs) {

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
		addComplexAttributes(goID, attrs);
	}

	private void addComplexAttributes(String goID, CyAttributes attrs) {
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

	@SuppressWarnings("unchecked") // stupid attrs
	private void testAttributes(String goID, CyAttributes attrs) {
		assertTrue(Boolean.TRUE.equals(attrs.getBooleanAttribute(goID, "BooleanTest")));

		assertTrue("string test value".equals(attrs.getStringAttribute(goID, "StringTest")));
		assertTrue(new Integer(6).equals(attrs.getIntegerAttribute(goID, "IntegerTest")));
		assertTrue(new Double(5.0).equals(attrs.getDoubleAttribute(goID, "DoubleTest")));

		List<String> listVal = attrs.getListAttribute(goID, "ListTest");
		assertTrue((listVal.size() == 2) && listVal.contains("list test value1")
		                  && listVal.contains("list test value2"));

		Map<String, String> mapVal = attrs.getMapAttribute(goID, "MapTest");
		assertTrue((mapVal.size() == 2) && "map key1 value".equals(mapVal.get("map key1"))
		                  && "map key2 value".equals(mapVal.get("map key2")));
		testComplexAttributes(goID, attrs);
	}

	private void testComplexAttributes(String goID, CyAttributes attrs) {
		MultiHashMap mmap = attrs.getMultiHashMap();
		assertTrue(new Double(0.5).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Jojo", new Integer(0)
		                                                                })));
		assertTrue(new Double(0.6).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Jojo", new Integer(1)
		                                                                })));
		assertTrue(new Double(0.6).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Jojo", new Integer(2)
		                                                                })));
		assertTrue(new Double(0.7).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Harry", new Integer(0)
		                                                                })));
		assertTrue(new Double(0.6).equals(mmap.getAttributeValue(goID, "p-valuesTest",
		                                                                new Object[] {
		                                                                    "Harry", new Integer(1)
		                                                                })));

		assertTrue("url1: sentence1".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url1", new Integer(0),
		                                                                      new Integer(0)
		                                                                  })));
		assertTrue("url1: sentence2".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url1", new Integer(0),
		                                                                      new Integer(1)
		                                                                  })));
		assertTrue("url1: sentence3".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url1", new Integer(0),
		                                                                      new Integer(10)
		                                                                  })));

		assertTrue("url1: publication 1".equals(mmap.getAttributeValue(goID,
		                                                                      "TextSourceInfo",
		                                                                      new Object[] {
		                                                                          "url1",
		                                                                          new Integer(1),
		                                                                          new Integer(0)
		                                                                      })));

		assertTrue("url2: sentence1".equals(mmap.getAttributeValue(goID, "TextSourceInfo",
		                                                                  new Object[] {
		                                                                      "url2", new Integer(0),
		                                                                      new Integer(6)
		                                                                  })));
		assertTrue("url2: publication 1".equals(mmap.getAttributeValue(goID,
		                                                                      "TextSourceInfo",
		                                                                      new Object[] {
		                                                                          "url2",
		                                                                          new Integer(1),
		                                                                          new Integer(0)
		                                                                      })));
	}
}
