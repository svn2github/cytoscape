
package cytoscape.cytable;


import cytoscape.data.CyAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class CyTableReaderTest {

	@Test
	public void testGoodAttrs() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" + 
                      "a,homer,1,0.23,false\n" + 
                      "b,marge,4,.24,true\n";

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int",Integer.valueOf("1"));
		verify(attrs).setAttribute("b","marge_int",Integer.valueOf("4"));

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("0.23"));
		verify(attrs).setAttribute("b","bart_double",Double.valueOf(".24"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.valueOf("false"));
		verify(attrs).setAttribute("b","lisa_boolean",Boolean.valueOf("true"));
	}

	
	// The attributes with these names DO NOT already exist, so we
	// can guess that they're strings.
	@Test
	public void testMissingTypeRowNoPreExistingAttrs() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "a,homer,1,0.23,false\n" +
                      "b,marge,4,0.24,true\n";

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType(anyString())).thenReturn(CyAttributes.TYPE_UNDEFINED);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int","1");
		verify(attrs).setAttribute("b","marge_int","4");

		verify(attrs).setAttribute("a","bart_double","0.23");
		verify(attrs).setAttribute("b","bart_double","0.24");

		verify(attrs).setAttribute("a","lisa_boolean","false");
		verify(attrs).setAttribute("b","lisa_boolean","true");
	}

	// The attributes with these names DO already exist, so we
	// CANNOT guess their type, so we expect an exception. 
	@Test(expected=Exception.class)
	public void testMissingTypeRowWithPreExistingAttrs() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "a,homer,1,0.23,false\n" +
                      "b,marge,4,0.24,true\n";

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();
	}


	// the second row of attrs is missing trailing values, which should be OK
	@Test
	public void testMissingTrailingValuesRow() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" +
                      "a,homer,1,0.23,false\n" +
                      "b,marge,4,,\n";

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int",Integer.valueOf("1"));
		verify(attrs).setAttribute("b","marge_int",Integer.valueOf("4"));

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("0.23"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.valueOf("false"));
	}

	// the second row of attrs is missing values in the middle of the row, which should be OK
	@Test
	public void testMissingMiddleValuesRow() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" +
                      "a,homer,1,0.23,false\n" +
                      "b,marge,,,true\n";

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int",Integer.valueOf("1"));

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("0.23"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.valueOf("false"));
		verify(attrs).setAttribute("b","lisa_boolean",Boolean.valueOf("true"));
	}

	// the second row of attrs is missing trailing values with no commas, which should be OK
	@Test
	public void testMissingTrailingValuesWithNoCommasRow() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" +
                      "a,homer,1,0.23,false\n" +
                      "b,marge,4\n";

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int",Integer.valueOf("1"));
		verify(attrs).setAttribute("b","marge_int",Integer.valueOf("4"));

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("0.23"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.valueOf("false"));
	}

	// the type row is too short 
	@Test(expected=Exception.class)
	public void testTypeRowTooShort() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer\n" +
                      "a,homer,1,0.23,false\n" +
                      "b,marge,4,0.24,true\n" ;

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();
	}

	// Wrong int values for specified types
	@Test
	public void testWrongValuesForIntType() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" +
                      "a,homer,0.1,0.23,false\n" +
                      "b,marge,asdf,.24,true\n" ;

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("0.23"));
		verify(attrs).setAttribute("b","bart_double",Double.valueOf(".24"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.valueOf("false"));
		verify(attrs).setAttribute("b","lisa_boolean",Boolean.valueOf("true"));
	}

	// Wrong double values for specified types
	@Test
	public void testWrongValuesForDoubleType() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" +
                      "a,homer,1,23,false\n" +
                      "b,marge,4,asdf,true\n" ;

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int",Integer.valueOf("1"));
		verify(attrs).setAttribute("b","marge_int",Integer.valueOf("4"));

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("23"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.valueOf("false"));
		verify(attrs).setAttribute("b","lisa_boolean",Boolean.valueOf("true"));
	}

	// Wrong boolean values for specified types
	@Test
	public void testWrongValuesForBooleanType() throws Exception {

		String data = "ID,homer_string,marge_int,bart_double,lisa_boolean\n" +
                      "string,String,java.lang.Integer,Double,boolean\n" +
                      "a,homer,1,23,homer\n" +
                      "b,marge,4,1.32,marge\n" ;

		CyAttributes attrs = mock(CyAttributes.class);
		when(attrs.getType("ID")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("homer_string")).thenReturn(CyAttributes.TYPE_STRING);
		when(attrs.getType("marge_int")).thenReturn(CyAttributes.TYPE_INTEGER);
		when(attrs.getType("bart_double")).thenReturn(CyAttributes.TYPE_FLOATING);
		when(attrs.getType("lisa_boolean")).thenReturn(CyAttributes.TYPE_BOOLEAN);

		new CyTableReader(new StringReader(data),attrs).read();

		verify(attrs).setAttribute("a","homer_string","homer");
		verify(attrs).setAttribute("b","homer_string","marge");

		verify(attrs).setAttribute("a","marge_int",Integer.valueOf("1"));
		verify(attrs).setAttribute("b","marge_int",Integer.valueOf("4"));

		verify(attrs).setAttribute("a","bart_double",Double.valueOf("23"));
		verify(attrs).setAttribute("b","bart_double",Double.valueOf("1.32"));

		verify(attrs).setAttribute("a","lisa_boolean",Boolean.FALSE);
		verify(attrs).setAttribute("b","lisa_boolean",Boolean.FALSE);
	}
}
