header {
package fgraph;
}

{
import fgraph.Submodel;

import java.util.List;
import java.util.ArrayList;
}
class ExpressionParser extends Parser;
options {
//  k = 2;
}

parse returns [List l]
{
    l = new ArrayList();
}
    : (m=kodata {l.add(m);} )+
    ;

kodata returns [String[] data]
{
    data = new String[3];
}
    : ko:ORF target:ORF p:PVALUE
      {
            data[0] = ko.getText();
            data[1] = target.getText();
            data[2] = p.getText();
      }
    ;



class ExpressionLexer extends Lexer;
options {
  k = 3;
  testLiterals = true;
}

ORF: ;
PVALUE: ('0'..'9')+;    


WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
