/*
 File: XGMMLWriterTest.java

 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.writers;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;

import cytoscape.data.readers.XGMMLReader;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import junit.framework.TestCase;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.net.URISyntaxException;


/**
 * Tests the XGMMLWriter Class.
 */
public class XGMMLWriterTest extends TestCase {
    public void testXGMMLWriterRoundTrip1() throws IOException, URISyntaxException {
        // No characters that are affecting by full encoding, so only one test.
        compareRoundTrip("testData/XGMMLWriterTestFile01a.xgmml", "testData/XGMMLWriterTestFile01b.xgmml");
    }

    public void testXGMMLWriterRoundTrip2aFullEncodingDefault() throws IOException, URISyntaxException {
        System.clearProperty(XGMMLWriter.ENCODE_PROPERTY);
        compareRoundTrip("testData/XGMMLWriterTestFile02a.xgmml", "testData/XGMMLWriterTestFile02a.xgmml");
    }

    public void testXGMMLWriterRoundTrip2aFullEncodingOn() throws IOException, URISyntaxException {
        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "true");
        compareRoundTrip("testData/XGMMLWriterTestFile02a.xgmml", "testData/XGMMLWriterTestFile02a.xgmml");
    }

    public void testXGMMLWriterRoundTrip2aFullEncodingOff() throws IOException, URISyntaxException {
        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "false");
        compareRoundTrip("testData/XGMMLWriterTestFile02a.xgmml", "testData/XGMMLWriterTestFile02a.xgmml");
    }

    public void testXGMMLWriterRoundTrip2bFullEncodingDefault() throws IOException, URISyntaxException {
        System.clearProperty(XGMMLWriter.ENCODE_PROPERTY);
        compareRoundTrip("testData/XGMMLWriterTestFile02b.xgmml", "testData/XGMMLWriterTestFile02a.xgmml");
    }

    public void testXGMMLWriterRoundTrip2bFullEncodingOn() throws IOException, URISyntaxException {
        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "true");
        compareRoundTrip("testData/XGMMLWriterTestFile02b.xgmml", "testData/XGMMLWriterTestFile02a.xgmml");
    }

    public void testXGMMLWriterRoundTrip2bFullEncodingOff() throws IOException, URISyntaxException {
        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "false");
        compareRoundTrip("testData/XGMMLWriterTestFile02b.xgmml", "testData/XGMMLWriterTestFile02a.xgmml");
    }

    public void testXGMMLWriterRoundTrip2cFullEncodingDefault() throws IOException, URISyntaxException {
        System.clearProperty(XGMMLWriter.ENCODE_PROPERTY);
        compareRoundTrip("testData/XGMMLWriterTestFile02c.xgmml", "testData/XGMMLWriterTestFile02d.xgmml");
    }

    public void testXGMMLWriterRoundTrip2cFullEncodingOn() throws IOException, URISyntaxException {
        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "true");
        compareRoundTrip("testData/XGMMLWriterTestFile02c.xgmml", "testData/XGMMLWriterTestFile02d.xgmml");
    }

    public void testXGMMLWriterRoundTrip2cFullEncodingOff() throws IOException, URISyntaxException {
        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "false");
        compareRoundTrip("testData/XGMMLWriterTestFile02c.xgmml", "testData/XGMMLWriterTestFile02c.xgmml");
    }

    public void testXGMMLWriterBug0001938() throws IOException, URISyntaxException {
        XGMMLReader r;
        CyNetwork cn;
        StringWriter sw;
        XGMMLWriter w;
        String xgmmls;
        ByteArrayInputStream bais;

        System.setProperty(XGMMLWriter.ENCODE_PROPERTY, "true");
        cn = Cytoscape.createNetwork("<\"aw&ward\name\tstring>\"");

        sw = new StringWriter();
        w = new XGMMLWriter(cn, null);
        w.write(sw);
        sw.close();

        xgmmls = sw.toString();
        System.out.println(xgmmls);
        bais = new ByteArrayInputStream(xgmmls.getBytes("US-ASCII"));
        r = new XGMMLReader(bais);
        cn = Cytoscape.createNetwork(r, false, null);
    }

    private void compareRoundTrip(String fileIn, String fileToCompare) throws IOException, URISyntaxException {
        XGMMLReader r;
        CyNetwork cn;
        StringWriter sw;
        XGMMLWriter w;
		String output;

        r = new XGMMLReader(fileIn);
        cn = Cytoscape.createNetwork(r, false, null);

        sw = new StringWriter();
        w = new XGMMLWriter(cn, null);
        w.write(sw);
        sw.close();

		output = sw.toString();

        compareFilesByLine(fileToCompare, output);
    }

    private void compareFilesByLine(String fileToCompare, String output) throws IOException {
        String[] linesGot;
        StringBuilder sb;
        FileInputStream fis;
        InputStreamReader isr;
        int c;
        String content;
        String[] linesExptd;

		linesGot = output.split("\n");

		for (String line : linesGot) {
			System.out.println(line);
		}

        sb = new StringBuilder();
        fis = new FileInputStream(fileToCompare);
        isr = new InputStreamReader(fis, XGMMLWriter.ENCODING);
        try {
            c = isr.read();
            while (c != -1)
            {
                sb.append((char)c);
                c = isr.read();
            }
        }
        finally {
            if (isr != null) {
                isr.close();
            }
        }
        content = sb.toString();
        System.out.println("Read " + content.getBytes(XGMMLWriter.ENCODING).length + " bytes");
		linesExptd = content.split("\n");
        assertTrue("XGMMLWriter: No. of lines, expect " + linesExptd.length + ", got" + linesGot.length, linesExptd.length == linesGot.length);

        for (int i = 0; i < linesExptd.length; i++) {
            String exptd;
            String got;

            exptd = linesExptd[i];
            got = linesGot[i];
            // <dc:date> value will be different so skip this line
            if (exptd.contains("<dc:date>")) {
                continue;
            }
            if (exptd.contains("<node")) {
                continue;
            }
            if (exptd.contains("<edge")) {
                continue;
            }
            System.out.println("Exp (" + exptd.length() + ")>" + exptd);
            System.out.println("Got (" + got.length() + ")>" + got);
            assertEquals("Line " + i + " {" + got + "} {" + exptd + "}", exptd, got);
        }
    }

	/**
	 * Runs just this one unit test.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(XGMMLWriterTest.class);
	}
}
