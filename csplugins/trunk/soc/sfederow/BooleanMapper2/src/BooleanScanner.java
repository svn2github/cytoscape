package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.List;

public class BooleanScanner {
	String criteria;
	String input;
	int charNr = 0;
	int charNumber = 0;
	HashMap<String, Integer> attributeTypeMap = new HashMap<String, Integer>();
	
	public BooleanScanner(){
		
	}
	
	
	public ArrayList<Token> parseT(String criteria) throws Exception{
		this.criteria = criteria;
		ArrayList<Token> tokenList = new ArrayList<Token>();
		Token token = null;
		System.out.println("PARSE T: "+criteria);
		// eat whitespace
		char ch = eatChar();

		while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')
		{
			ch = eatChar();
		}

		// read token
		switch (ch)
		{
		case '-':
		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
		case '.': {
			String value = "" + ch;
			ch = eatChar();
			while ((ch >= '0' && ch <= '9') || ch == '.')
			{
				value += ch;
				ch = eatChar();
			}
			putBack (ch);									
			tokenList.add(new Token(Token.TOKEN_NUMBER, Double.parseDouble(value))); }                            
		break;
		case '<':
			ch = eatChar();
			if (ch == '=')
				tokenList.add(new Token(Token.TOKEN_LE));	
			else
			{
				tokenList.add(new Token(Token.TOKEN_LT));
				putBack (ch);
			}
			break;
		case '>':
			System.out.println("Greate than >");
			ch = eatChar();
			if (ch == '=')
				tokenList.add(new Token(Token.TOKEN_GE));	
			else
			{
				tokenList.add(new Token(Token.TOKEN_GT));
				putBack (ch);
			}
			break;
		case '=': 
			tokenList.add(new Token(Token.TOKEN_EQ));
			break;
		case '(': 
			tokenList.add(new Token(Token.TOKEN_LPAREN));
			break;
		case ')': 
			tokenList.add(new Token(Token.TOKEN_RPAREN));
			break;
		case '[': {
			ch = eatChar();
			String value = "";
			while (ch != ']' && ch != '\0')
			{
				value += ch;
				ch = eatChar();
			}
			tokenList.add(new Token(Token.TOKEN_ID, value));                 
		} break;
		case 'A':	

			if (eatChar() == 'N' && eatChar() == 'D')
			{
				tokenList.add(new Token (Token.TOKEN_AND));
			}
			else
			{
				throw new Exception("Invalid character 'A' at position " + (charNr - 2) + 
				"\n- Expected start of 'AND'");
			}
			break;
		case 'O':
			ch = eatChar();
			if (ch == 'R')
			{
				tokenList.add(new Token(Token.TOKEN_OR));
			}
			else
			{
				throw new Exception("Invalid character 'O' at position " + (charNr - 1) + 
				"\n- Expected start of 'OR'");
			}
			break;
		case 'a': case 'b': case 'c': case 'd': case 'e':
		case 'f': case 'g': case 'h': case 'i': case 'j':
		{
			String value = "" + ch;
			System.out.println("Got TO VALUE: "+value);
			ch = eatChar();
			while (ch != ' ' && ch != '\0')
			{
				value += ch;
				ch = eatChar();
			}
			putBack (ch);
			
			//tokenList.add(new Token(Token.TOKEN_NUMBER, Double.parseDouble(value))); 
			
		}
		case '\0':
			tokenList.add(new Token(Token.TOKEN_END));
			break;
		default:
			throw new Exception("Unexpected end of expression at position " + charNr);
		}
		//~ System.out.print (token.type + ", ");
		
		return tokenList;
	}


	char eatChar()
	{
		if (input.length() == 0)
		{
			return '\0';
		}
		else
		{
			charNr++;
			char result = input.charAt(0);
			input = input.substring(1);
			return result;
		}
	}

	void putBack(char ch)
	{
		if (input.length() == 0 && ch == '\0')
		{
		}
		else
		{
			input = ch + input;
		}
	}

	
	
	public boolean parse(String a) throws IOException{
		criteria = a;
		StringReader reader = new StringReader(criteria);
		StreamTokenizer st = new StreamTokenizer(reader);
		int i;
		double tmp;
		String variable;
		System.out.println("SCANNER: "+ a);
		while((i = st.nextToken()) != StreamTokenizer.TT_EOF ){
			switch(i) {
			case 29:
				System.out.println("gooood "+st.sval);
				
			case 34: 
				System.out.println("woooord "+st.sval);
			
            case StreamTokenizer.TT_NUMBER: // Found a number, push value to stack
                //push(st.nval);
            	System.out.println(st.nval);
                break;
            
            case '<':
                // + operator is commutative.
                //push(pop( ) + pop( ));
            	System.out.println("<");
            	break;
            case '>':
                // - operator: order matters.
                //tmp = pop( );
                //push(pop( ) - tmp);
            	System.out.println(">");
                break;
            case '=':
                // Multiply is commutative
                //push(pop( ) * pop( ));
                break;
            case 'A':
            	System.out.println("dfa");
            	if(st.nextToken() == 'N' && st.nextToken() == 'D'){
            		//System.out.println("IT WORRKKS");
            	}
                // Handle division carefully: order matters!
                //tmp = pop( );
                //push(pop( ) / tmp);
                break;
            case 'O':
                //out.println(peek( ));
                break;
            case 'N':
                //out.println(peek( ));
                break;
            case StreamTokenizer.TT_WORD:
                // Found a variable, save its name. Not used here.
                variable = st.sval;
                if(variable.equals("AND")){
                	System.out.println("IT IS");
                }
                if(variable.equals("OR")){
                	System.out.println("IT wAS");
                }
                if(variable.equals("NOT")){
                	System.out.println("IT has");
                }
                System.out.println(variable);
                break;
            default:
                System.out.println("What's this? iType = " + i);
            }
        }
    
		
		return true;
	}
	
	
	char nextChar()
	{
		if (criteria.length() == 0)
		{
			return '\0';
		}
		else
		{
			charNumber++;
			char result = criteria.charAt(0);
			criteria = criteria.substring(1);
			return result;
		}
	}


	
	public boolean parse2(String criteria){
		ArrayList<String> valueChars = new ArrayList<String>();
		ArrayList<String> validAttributes = new ArrayList<String>();
		ArrayList<String> operations = new ArrayList<String>();
		String value = "";
		int valueCount = 2;
		System.out.println("CRITERIA:"+criteria);
		criteria = criteria + "  ";
		//System.out.println(criteria.length());
		for(int i=0;i<criteria.length();i++){
			String parse = "" + criteria.charAt(i);
			//System.out.println("parse: "+ parse);
			if(!parse.matches(" ")){
				//System.out.println("parse: "+ parse);
				valueChars.add(parse);
				
			}
			if(parse.matches(" ") && valueChars.size() != 0){
				for(int k=0;k<valueChars.size();k++){
					value = value + valueChars.get(k);
					
				}
				//System.out.println("value: "+value);
				valueChars.clear();
				if(valueCount%2 == 0){
					validAttributes.add(value);
					operations.add("");
				}else{
					validAttributes.add("");
					operations.add(value);
					
				}
				valueCount++;
				value = "";
			}
			
		}	
		
		for(int b=2;b<validAttributes.size()+2;b++){
			if(b%2 == 0){
				//System.out.println("b"+validAttributes.get(b-2));
			}
			if(b%2 != 0){
				//System.out.println("a"+operations.get(b-2));
			}
		}
		
		boolean outcome = checkCriteria(validAttributes, operations);
		
		//System.out.println(checkCriteria());
		return outcome;
	}
	
	
	
	
	public boolean checkCriteria(ArrayList<String> validAttributes, ArrayList<String> operations){
		boolean nFlag = false;
		boolean cFlag = true;
		int a = 0;
		int b = 0;
		int size = validAttributes.size();
		//System.out.println("size: "+size);
		if (size == 1 && !validAttributes.get(0).equals("")){ return true; }
		for(int i=0; i<size; i++){
			//System.out.println(i);
			/*
			if(!validAttributes.get(i).equals("")){
				if(validAttributes.get(i).matches("^[0-9\\.\\-]+")){
					if(validAttributes.get(i).contains(".")){
						a = 2;
					}else{
						a = 3;
					}
				}else{
					if(validAttributes.get(i).equals("true") || validAttributes.get(i).equals("false")){
						a = 1;
					}else{
						//System.out.println(validAttributes.get(i));
						//a = (Integer)attributeTypeMap.get(validAttributes.get(i));
					}
				}
			}
			if((i+2) < size && !validAttributes.get(i+2).equals("")){
				
				if(validAttributes.get(i+2).matches("^[0-9\\.\\-]+")){
					
					if(validAttributes.get(i+2).contains(".")){
						b = 2;
						//Sytem.out.println("to TWO");
					}else{
						
						b = 3;
					}
				}else{
					if(validAttributes.get(i+2).equals("true") || validAttributes.get(i+2).equals("false")){
						b = 1;
					}else{
						//b = (Integer)attributeTypeMap.get(validAttributes.get(i+2));
					}
				}
			}*/
			if(cFlag && (a == 2 || a == 3) && (i+1) < size && operations.get(i+1).matches("[<>=]+") && (b == 2 || b == 3)){
				nFlag = true;
				cFlag = false;
				i = i + 2;
				
			}else{
				if((a == 1) && (operations.get(i+1).matches("[ANDORT]+") || operations.get(i+1).equals("=")) && (b == 1)){
					nFlag = true;
					cFlag = false;
					i = i + 2;
				
				
				}else{
					if(nFlag && operations.get(i).matches("[ANDORT]+")){
						cFlag = true;
						nFlag = false;
					}else{
						return false;
			
					}
				}
			}
		}
		//masterList.add(validAttributes);
		//masterList.add(operations);
		//validAttributes.clear();
		//operations.clear();
		return true;
	}

}
	class Token {
		public int type;
		public static final int TOKEN_NONE = -2;
		public static final int TOKEN_END = -1;
		public static final int TOKEN_NUMBER = 0;
		public static final int TOKEN_ID = 1;
		public static final int TOKEN_EQ = 2;
		public static final int TOKEN_GT = 3;
		public static final int TOKEN_LT = 4;
		public static final int TOKEN_GE = 5;
		public static final int TOKEN_LE = 6;
		public static final int TOKEN_AND = 7;
		public static final int TOKEN_OR = 8;
		public static final int TOKEN_LPAREN = 9;
		public static final int TOKEN_RPAREN = 10;

		public double numberValue; // in case it is a number...
		public String symbolValue; // in case it is a symbol

		Token (int _type) { type = _type; numberValue = 0; symbolValue = ""; }
		Token (int _type, double _numberValue) { type = _type; numberValue = _numberValue; symbolValue = ""; }
		Token (int _type, String _symbolValue) { type = _type; numberValue = 0; symbolValue = _symbolValue; }

		
		
	}
	
	class CriteriaExpression {
		
		//boolean 
		
		public CriteriaExpression(Token[] tokens){
			setExpressionFlags(tokens);
		}
		
		public void setExpressionFlags(Token[] tokens){
			
		}
		public void evaluate(){
			
		}
	}
