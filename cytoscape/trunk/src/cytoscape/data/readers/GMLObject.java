// GMLObject.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//-------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

//-----------------------------------------------------------------
public class GMLObject {

    protected int LineNumber;
    protected int scope;
    protected GMLPair rootPair;

    //-----------------------------------------------------------------
    /**
     * Constructor for the default GMLObject.
     */
    public GMLObject() {
	//GMLObject("gml"); //why can't we just do this???
	GMLToken rootToken = new GMLToken("gml", "ROOT", 0);
	rootPair = new GMLPair(rootToken, 0);
	LineNumber = 0;
	scope = 0;
    }
    /**
     * Constructor for a named GMLObject.
     */
    public GMLObject(String name) {	
	GMLToken rootToken = new GMLToken(name, "ROOT", 0);
	rootPair = new GMLPair(rootToken, 0);
	LineNumber = 0;
	scope = 0;
    }
    //-----------------------------------------------------------------
    /**
     * Get the string representation of the GMLObject.
     *
     * @return A formatted String copy of the GMLObject.
     */
    public String toString() {
	StringBuffer buf = new StringBuffer();
	if(rootPair != null)
	    rootPair.loadString(buf);
	return buf.toString();
    }
    //-----------------------------------------------------------------
     /**
     * Get all concatonated string keys and their object values.
     * Each key will contain keys from all parent levels of the GML tree.
     * @param keys Vector of strings to be loaded
     * @param vals Vector of corresponding objects {Integer, Double, String}
     * to be loaded
     */
    public void flatPairs(Vector keys, Vector vals) {
	String pre = "";
	if(rootPair != null)
	    rootPair.flattenPairs(pre, keys, vals);
    }
    //-----------------------------------------------------------------
   /**
    * Number of GMLPairs in the GMLObject
    * @return values.size()
    */
    public int size() {
	return rootPair.values.size();
    }
    //-----------------------------------------------------------------
    /**
     * Builds the GMLObject rooted at rootPair.
     *
     * @param fileName the file name (including path) of the GML file to be loaded
     * @return called for side effects only
     */
    public void read(String fileName) {
	LinkedList tokenList = new LinkedList();
	//System.out.println("You have choosen to open this file: " + fileName);

	rootPair.clear();
	// read the text file 
	TextFileReader reader = new TextFileReader(fileName);
	reader.read();

	// handle the lines -> build GMLToken list
	StringTokenizer lines = new StringTokenizer(reader.getText(), "\n");

	//System.out.println("lines list done");
	// slow from here ----->
	while (lines.hasMoreTokens()) {
	    StringTokenizer tokens = new StringTokenizer(lines.nextToken());
	    LineNumber++;
	    while (tokens.hasMoreTokens())
		tokenList.add(getGMLToken(tokens, lines));
	}
	tokenList.add(new GMLToken(0, "EOF", LineNumber));
	// <----- to here
	//System.out.println("token list done");

	// handle GMLToken list -> build GML
	scope = 0;
	getGMLPairs(tokenList, rootPair);
    }
    //-----------------------------------------------------------------
    /**
     * Writes the GMLObject to a text file.
     *
     * @param fileName the full file name of the GML file to be written
     * @see toString
     */
    public void write(String fileName){
	System.out.println("writing " + fileName);
	String gmlStr = toString();

	//networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	if ( !fileName.endsWith(".gml") || !fileName.endsWith(".GML") ) 
	    fileName = fileName + ".gml";
	try {
	    FileWriter fileWriter = new FileWriter(fileName);
	    fileWriter.write(gmlStr);
	    fileWriter.close();
	} catch (IOException ioe) {
	    System.err.println("Error while writing " + fileName);
	    ioe.printStackTrace();
        } 

    }
    //-----------------------------------------------------------------
    /**
     * Builds and returns a subset of root items
     *
     * @param key String for a key found at the GML root level
     * @return GMLObject containing the key values
     */
    public GMLObject getGML(String key) {
	GMLPair p;
	GMLObject items = new GMLObject();

	Iterator ki = rootPair.values.iterator();
	while(ki.hasNext()) {
	    p = (GMLPair) ki.next();
	    if(p.key.GMLstring.equals(key))
		items.rootPair.values.add(p);
	}
	return items;
    }
    //-----------------------------------------------------------------
    /**
     * Builds and returns a subset of root items and subsets of their values
     *
     * @param key1 String for a key found at the GML root level
     * @param key2 String for a key found at the GML root +1 level
     * @return GMLObject containing a [key1 [key2]+]+ tree
     */
    public GMLObject getGML(String key1, String key2) {
	GMLPair rp, p;
	GMLObject items = new GMLObject();
	Vector rps = new Vector();

	//System.out.println("Object has " + rootPair.values.size() + " items");
	Iterator ri = rootPair.values.iterator();
	while(ri.hasNext()) {
	    rp = (GMLPair) ri.next();
	    if(rp.key.GMLstring.equals(key1)) {
		Iterator ki = rp.values.iterator();
		while(ki.hasNext()) {
		    p = (GMLPair) ki.next();
		    if(p.key.GMLstring.equals(key2))
			items.rootPair.values.add(p);
		}
	    }
	}
	return items;
    }
    /**
     * Get the values {Integer, Double, String} associated with the string "key"
     * @return Vector of values for "key". Will be empty if "key" does not exist 
     * at the root level
     */
    public Vector getVector(String key) {
	GMLObject items = getGML(key) ;
	Vector v = new Vector();
	Iterator ri = items.rootPair.values.iterator();
	while(ri.hasNext()) {
	    GMLPair p  = (GMLPair) ri.next();
	    GMLPair vp = (GMLPair) p.values.firstElement();
	    //if(vp.key.GMLtype == "integer")     v.add(new Integer(vp.key.GMLinteger));
	    //else if(vp.key.GMLtype == "double") v.add(new Double(vp.key.GMLdouble));
	    if(vp.key.GMLtype == "integer")     v.add(vp.key.GMLinteger);
	    else if(vp.key.GMLtype == "double") v.add(vp.key.GMLdouble);

	    else                                v.add(vp.key.GMLstring);
	}
	return v;
    }
    /**
     * Get the values {Integer, Double, String} associated with the string "key1" and "key2"
     * @return Vector of values for "key". Will be empty if "key" does not exist 
     * at the root level
     */
    public Vector getVector(String key1, String key2) {
	GMLObject items = getGML(key1, key2) ;
	Vector v = new Vector();
	Iterator ri = items.rootPair.values.iterator();
	while(ri.hasNext()) {
	    GMLPair p  = (GMLPair) ri.next();
	    GMLPair vp = (GMLPair) p.values.firstElement();

	    //if(vp.key.GMLtype == "integer")     v.add(new Integer(vp.key.GMLinteger));
	    //else if(vp.key.GMLtype == "double") v.add(new Double(vp.key.GMLdouble));
	    if(vp.key.GMLtype == "integer")     v.add(vp.key.GMLinteger);
	    else if(vp.key.GMLtype == "double") v.add(vp.key.GMLdouble);
	    else                                v.add(vp.key.GMLstring);
	}
	return v;
    }

    //------------------------------------------------------------
    // polymorphic token class
    //------------------------------------------------------------
    protected class GMLToken {
	protected Integer GMLinteger;
	protected Double  GMLdouble;
	protected String  GMLstring;

	//{"double", "integer", "key", "string", "EOF", "comment"}
	protected String GMLtype;

	// inorder to report problems with the input file
	protected int    LineFound;

	public GMLToken() {
	    GMLinteger = new Integer(0);
	    GMLdouble  = new Double(0.0);
	    GMLstring  = new String();
	    GMLtype = "empty";
	    LineFound = 0;
	}
	public GMLToken(int i, String type, int line) {
	    GMLinteger = new Integer(i);
	    GMLdouble  = new Double(0.0);
	    GMLstring  = new String();
	    GMLtype = type;
	    LineFound = line;
	}
	public GMLToken(double i, String type, int line) {
	    GMLinteger = new Integer(0);
	    GMLdouble  = new Double(i);
	    GMLstring  = new String();
	    GMLtype = type;
	    LineFound = line;
	}
	public GMLToken(String i, String type, int line) {
	    GMLinteger = new Integer(0);
	    GMLdouble = new Double(0.0);
	    GMLstring = new String(i);
	    GMLtype = type;
	    LineFound = line;
	}
        public String getGMLType() {
	    return GMLtype;
	}
	public String toString() {
	    return( this.value().toString() );
	}
	public Object value() {
	    if(GMLtype == "integer")
		return(GMLinteger);
	    else if(GMLtype == "double")
		return(GMLdouble);
	    else 
		return(GMLstring);
	}
    }

    //------------------------------
    // holds a "key" GMLToken and a 
    // vector of GMLToken "values"
    protected class GMLPair {
	protected int pairScope;
	protected GMLToken key;
	protected Vector values;
	public GMLPair() {
	    pairScope = 0;
	    key    = new GMLToken();
	    values = new Vector();
	}
	public GMLPair(GMLToken pk, int ps) {
	    pairScope = ps;
	    key    = pk;
	    values = new Vector();
	}
	public void clear() {
	    values.clear();
	}
	public void print() {
	    GMLPair pairValue;
	    StringBuffer buf = new StringBuffer();
	    for(int i=0; i<pairScope; i++)
		buf.append("\t");
	    if ( (key.GMLtype == "key") ||
		 (key.GMLtype == "GML_R_BRACKET") )
		System.out.print(buf.toString() + key.toString());
	    else if (key.GMLtype != "ROOT") 
		System.out.print(" " + key.toString());	    
	    if(values.size()>0) {
		Iterator vi = values.iterator();
		while(vi.hasNext()) {
		    pairValue = (GMLPair) vi.next();
		    pairValue.print();
		}
	    }
	    else
		System.out.println("");
	}
	//--------------------------------------------
	protected void loadString(StringBuffer buf) {
	    GMLPair pairValue;
	    StringBuffer pre = new StringBuffer();

	    for(int i=0; i<pairScope; i++)
		pre.append("\t");

	    if ( (key.GMLtype == "key") ||
		 (key.GMLtype == "GML_R_BRACKET") )
		buf.append( pre.toString() + key.toString() );
	    else if (key.GMLtype != "ROOT") 
		buf.append(" " + key.toString());

	    if(values.size()>0) {
		Iterator vi = values.iterator();
		while(vi.hasNext()) {
		    pairValue = (GMLPair) vi.next();
		    pairValue.loadString(buf);
		}
	    }
	    else
		buf.append("\n");
	}

 	//--------------------------------------------
	protected void flattenPairs(String pre, Vector keys, Vector vals) {
	    GMLPair pairValue;
	    StringBuffer newpre = new StringBuffer(pre);

	    // if a the key GMLtoken is actually a value 
	    if ( (key.GMLtype == "integer") ||
		 (key.GMLtype == "double") || 
		 (key.GMLtype == "string") 
		 ) {
		//System.out.println("VALUES SIZE " + values.size());
		keys.add(pre);
		vals.add(key.value());
	    }
            else {
		if( (key.GMLtype == "key") || (key.GMLtype =="ROOT") )
		    if(pre.length()>0) 
			newpre.append("." + key.toString());
		    else
			newpre.append(key.toString());
	    }
	    if(values.size()>0) {
		Iterator vi = values.iterator();
		while(vi.hasNext()) {
		    pairValue = (GMLPair) vi.next();
		    pairValue.flattenPairs(newpre.toString(), keys, vals);
		}
	    }
	}
    }

    //-----------------------------------------------------------------
    protected void getGMLPairs(LinkedList tokens, GMLPair thisPair) {
	GMLToken keyToken, valueToken;
	GMLPair valuePair, newPair;
	boolean done = false;

	while((tokens.size()>0) && !done) {
	    
	    newPair = getGMLPair(tokens);

	    if(newPair.key.getGMLType() == "EOF") { //System.out.println("\t\t\tEOF");
		if(scope>0) { // THROW EXCEPTION here (file ended unexpectedly)
		    System.out.println("SCOPE AT EOF = " + 
				       scope + " " + newPair.key.LineFound);
		}
		done = true;
	    } 
	    else {
		if(newPair.key.getGMLType() == "GML_R_BRACKET") {
		    scope--;
		    done = true;
		    newPair.pairScope = scope; // reset the pair's scope
		    thisPair.values.add(newPair);
		}
		else {
		    thisPair.values.add(newPair);
		    valuePair = (GMLPair)  newPair.values.firstElement();
		    
		    if( valuePair.key.getGMLType() == "GML_L_BRACKET") {
			scope++;
			getGMLPairs(tokens, newPair);
		    }
		}
	    }
	}
    }
    
    //-----------------------------------------------------------------
    protected GMLPair getGMLPair(LinkedList tokens){
	GMLToken keyToken;
	GMLToken valueToken;
	GMLPair newPair, valuePair;

	do { //get next valid GMLToken
	    keyToken = (GMLToken) tokens.removeFirst();
	} while((tokens.size()>0) && keyToken.getGMLType() == "comment");

	if( (keyToken.getGMLType() != "key") && 
	    (keyToken.getGMLType() != "EOF") &&
	    (keyToken.getGMLType() != "GML_R_BRACKET") ) {
	    // THROW EXCEPTION here (malformed input file)
	    System.out.println("ERROR: must have a key before a value " + 
			       keyToken.toString() + " on line " + keyToken.LineFound);
	}
	
	newPair = new GMLPair(keyToken, scope);

	if( (keyToken.getGMLType() != "EOF") && 
	    (keyToken.getGMLType() != "GML_R_BRACKET") ) {
	    do { // get next valid GMLToken
		valueToken = (GMLToken) tokens.removeFirst();
	    } while((tokens.size()>0) && valueToken.getGMLType() == "comment");

	    valuePair = new GMLPair(valueToken, scope);
	    newPair.values.add(valuePair);
	}

	return(newPair);	
    }

    //-----------------------------------------------------------------
    protected GMLToken getGMLToken(StringTokenizer tokens, StringTokenizer lines) {
	double  doubleValue = 0.0;
	int     intValue    = 0; 
	int     initLine    = LineNumber;

	String more, clipped;
	//String gmlError = "";
	StringBuffer buffer = new StringBuffer();
	GMLToken ret = null;

	buffer.append(tokens.nextToken());
	int strLen = buffer.length() - 1;
	
	if ( buffer.charAt(0) == '\"' ) {
	    while( buffer.charAt(strLen) != '\"' ) {
		//if EOL and string not terminated
		if( !tokens.hasMoreTokens() && buffer.charAt(strLen) != '\"' ) {
		    if( lines.hasMoreTokens() ) {
			tokens = new StringTokenizer(lines.nextToken());
			LineNumber++;
		    } else { // THROW EXCEPTION here.
			System.out.println("ERROR: Un-terminated string value starting on line " + initLine);
		    }
		}
		more = tokens.nextToken();
		if( more.startsWith("\"") && more.endsWith("\"") ) { // THROW EXCEPTION here.
		    System.out.println("ERROR: string value within a string starting on line " + initLine);
		}
		buffer.append(" " + more);
		strLen = buffer.length() - 1;
	    }
	    clipped = buffer.substring(1, buffer.length()-1);
	    ret = new GMLToken(clipped, "string", LineNumber);
	}
	else {
	    if (buffer.charAt(0) == '#') { 
		while(tokens.hasMoreTokens()) {
		    //more = tokens.nextToken();
		    buffer.append(" " + tokens.nextToken());
		}
		ret = new GMLToken(buffer.toString(), "comment", LineNumber);
		//System.out.println(buffer.toString());
	    }
	    else {
		if (buffer.charAt(0) == '[') {
		    ret = new GMLToken(buffer.toString(), "GML_L_BRACKET", LineNumber);
		}
		else {
		    if (buffer.charAt(0) == ']') {
			ret = new GMLToken(buffer.toString(), "GML_R_BRACKET", LineNumber);
		    }
		    else {
			try {
			    intValue = Integer.parseInt(buffer.toString());
			    ret = new GMLToken(intValue, "integer", LineNumber);
			}
			catch(NumberFormatException e) {
			    try {
				doubleValue = Double.parseDouble(buffer.toString());
				ret = new GMLToken(doubleValue, "double", LineNumber);
			    }
			    catch(NumberFormatException e2) {
				ret = new GMLToken(buffer.toString(), "key", LineNumber);
			    }
			}
		    }
		}
 	    }
	}
	return (ret);
    }
}
