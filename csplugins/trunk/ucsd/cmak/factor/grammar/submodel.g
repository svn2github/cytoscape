header {
package fgraph.test;
}

{
import fgraph.Submodel;

import java.util.List;
import java.util.ArrayList;
}
class ModelParser extends Parser;
options {
//  k = 2;
}

parseSubmodels returns [List l]
{
    l = new ArrayList();
    Submodel m;
}
    : (m=submodel {l.add(m);} )+
    ;

submodel returns [Submodel m]
{
    m = new Submodel();
}
    : LCURLY 
      MODEL_IDENTIFIER i:ID iv:ID
      LBRACK ID ( node:ID 
                 {
                    m.addVar(Integer.parseInt(node.getText()));
                 } 
              )*
      RBRACK 
      RCURLY
      {
            // The independent var is the first var in the list.
            m.setIndependentVar(Integer.parseInt(iv.getText()));

            /*
            System.out.println("parsed submodel " + i.getText() 
                                + " indep var=" + m.getIndependentVar()
                                + " var0=" + m.getVars().get(0)
                                + " size=" + m.size());
            */
      }
    ;



class ModelLexer extends Lexer;
options {
  k = 3;
  testLiterals = true;
}

LBRACK: '[';
RBRACK: ']';

LPAREN: '(';
RPAREN: ')';


LCURLY: '{';
RCURLY: '}';

MODEL_IDENTIFIER: "decomposedmodel";

ID: ('0'..'9')+;    


WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        |   ','
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
