header {
package eqb;
}

{
import eqb.Eqtl;

import java.util.Map;
import java.util.HashMap;
}
class EqtlParser extends Parser;
options {
//  k = 2;
}

parseEqtl returns [Map l]
{
    l = new HashMap();
    Eqtl m;
}
    : (m=eqtl {l.put(m.getGene(), m);} )+
    ;

eqtl returns [Eqtl m]
{
    m = new Eqtl();
}
    : gene:NODE
      ( LPAREN locus:NODE COMMA value:NODE RPAREN
            {
                m.addLocus(locus.getText(),
                           Double.valueOf(value.getText()));
            } 
        )*
        {
            m.setGene(gene.getText());
        }
    ;



class EqtlLexer extends Lexer;
options {
  testLiterals = true;
}

LBRACK: '[';
RBRACK: ']';

LPAREN: '(';
RPAREN: ')';

LCURLY: '{';
RCURLY: '}';

NODE: ( 'a'..'z'|'A'..'Z'|'0'..'9'|'.' )+;

COMMA: ',';

WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
