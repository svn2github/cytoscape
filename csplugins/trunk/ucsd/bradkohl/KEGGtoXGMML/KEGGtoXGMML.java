package cytoscape.data.readers;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class KEGGtoXGMML{
	
	public static String transform(String fileName) {
		try {
			String styleSheet = "xml_to_xgmml.xsl";
			File outFile = File.createTempFile("kegg", "xml");
			StreamSource in;
			StreamSource ss;
			StreamResult out;
			in = new StreamSource(new File(fileName));
			ss = new StreamSource(new File(styleSheet));
			out = new StreamResult(outFile);
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer(ss);
			tf.transform(in, out);
			return outFile.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}	
}