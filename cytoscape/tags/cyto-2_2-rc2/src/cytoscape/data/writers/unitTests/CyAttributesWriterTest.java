package cytoscape.data.writers.unitTests;

import junit.framework.TestCase;
import cytoscape.data.writers.CyAttributesWriter;
import cytoscape.data.CyAttributesImpl;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.data.readers.unitTests.CyAttributesReaderTest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests the CyAttributesWriter Class.
 *
 */
public class CyAttributesWriterTest extends TestCase {

    /**
     * Tests Writing out of Scalar Values.
     * @throws IOException IO Error.
     */
    public void testWriterScalars() throws IOException {
        CyAttributes cyAttributes = new CyAttributesImpl();
        File file =  new File ("testData/galFiltered.nodeAttrs1");
        FileReader reader = new FileReader(file);
        CyAttributesReader.loadAttributes(cyAttributes, reader);
        StringWriter writer = new StringWriter();
        CyAttributesWriter.writeAttributes(cyAttributes, "TestNodeAttribute1",
                writer);
        String output = writer.toString();
        String lines[] = output.split(System.getProperty("line.separator"));
        assertEquals ("TestNodeAttribute1 (class=java.lang.Integer)", lines[0]);
        assertEquals ("YDR309C=1", lines[1]);
        assertEquals ("YML024W=2", lines[lines.length-1]);
    }

    /**
     * Tests Writing out of Lists.
     * @throws IOException IO Error.
     */
    public void testWriteSimpleLists() throws IOException {
        CyAttributes cyAttributes = new CyAttributesImpl();
        File file =  new File ("testData/implicitStringArray.attribute");
        FileReader reader = new FileReader(file);
        CyAttributesReader.loadAttributes(cyAttributes, reader);

        //  Add a new item
        List list = new ArrayList();
        list.add(new String ("Apple"));
        list.add(new String ("Orange"));
        list.add(new String ("Banana"));

        cyAttributes.setAttributeList("ABC_123",
                "GO_molecular_function_level_4", list);
        StringWriter writer = new StringWriter();
        CyAttributesWriter.writeAttributes(cyAttributes,
                "GO_molecular_function_level_4", writer);
        String output = writer.toString();
        String lines[] = output.split(System.getProperty("line.separator"));
        assertEquals ("GO_molecular_function_level_4 (class=java.lang.String)",
                lines[0]);
        assertEquals ("HSD17B2=(membrane::intracellular)", lines[1]);
        assertEquals ("E2F4=(DNA binding)", lines[2]);
        assertEquals ("AP1G1=(intracellular::clathrin adaptor::intracellular "
            + "transporter)", lines[3]);
        assertEquals("ABC_123=(Apple::Orange::Banana)", lines[4]);
        assertEquals ("CDH3=(cell adhesion molecule)", lines[5]);
    }

    /**
     * Runs just this one unit test.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CyAttributesWriterTest.class);
    }
}
