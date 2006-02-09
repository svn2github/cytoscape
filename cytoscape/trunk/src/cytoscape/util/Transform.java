package cytoscape.util;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Apply XSLT to an XML document
 * 
 * @author kono
 *
 */
public class Transform {
	
	
	private File input, xslt, output;
	
	public Transform( String document, String template, String output) {
		input = new File(document);
		xslt = new File(template);
		this.output = new File(output);
	}
	
	public void convert() throws TransformerException {
		StreamSource in = new StreamSource(input);
		StreamSource ss = new StreamSource(xslt);
		StreamResult out = new StreamResult(output);
		
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer(ss);
		tf.transform(in, out);
		
		System.out.println("File conversion done!: " + output.getName());
	}
	
	public static void main(String args[]) throws Exception {
		StreamSource in = new StreamSource(new File(args[0]));
		StreamSource ss = new StreamSource(new File(args[1]));
		StreamResult out = new StreamResult(new File(args[2]));

		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer(ss);
		tf.transform(in, out);
		System.out.println("Done: " + args[2]);
	}
}
