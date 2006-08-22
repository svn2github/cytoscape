// $ANTLR 2.7.4: "eqtl.g" -> "EqtlParser.java"$

package eqb;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

import eqb.Eqtl;

import java.util.Map;
import java.util.HashMap;

public class EqtlParser extends antlr.LLkParser       implements EqtlParserTokenTypes
 {

protected EqtlParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public EqtlParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected EqtlParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public EqtlParser(TokenStream lexer) {
  this(lexer,1);
}

public EqtlParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final Map  parseEqtl() throws RecognitionException, TokenStreamException {
		Map l;
		
		
		l = new HashMap();
		Eqtl m;
		
		
		try {      // for error handling
			{
			int _cnt3=0;
			_loop3:
			do {
				if ((LA(1)==NODE)) {
					m=eqtl();
					l.put(m.getGene(), m);
				}
				else {
					if ( _cnt3>=1 ) { break _loop3; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_0);
		}
		return l;
	}
	
	public final Eqtl  eqtl() throws RecognitionException, TokenStreamException {
		Eqtl m;
		
		Token  gene = null;
		Token  locus = null;
		Token  value = null;
		
		m = new Eqtl();
		
		
		try {      // for error handling
			gene = LT(1);
			match(NODE);
			{
			_loop6:
			do {
				if ((LA(1)==LPAREN)) {
					match(LPAREN);
					locus = LT(1);
					match(NODE);
					match(COMMA);
					value = LT(1);
					match(NODE);
					match(RPAREN);
					
					m.addLocus(locus.getText(),
					Double.valueOf(value.getText()));
					
				}
				else {
					break _loop6;
				}
				
			} while (true);
			}
			
			m.setGene(gene.getText());
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return m;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"NODE",
		"LPAREN",
		"COMMA",
		"RPAREN",
		"LBRACK",
		"RBRACK",
		"LCURLY",
		"RCURLY",
		"WS"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 18L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
