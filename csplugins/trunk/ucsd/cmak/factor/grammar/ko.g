header {
package fgraph;
}

{
import java.util.List;
import java.util.ArrayList;
}
class EdgeAttrParser extends Parser;
options {
//  k = 2;
}

parse returns [List l]
{
    l = new ArrayList();
    String[] m;
}
    : (m=edgeAttr {l.add(m);} )+
    ;

edgeAttr returns [String[] data]
{
    data = new String[4];
}
    : source:ORF type:EDGE_TYPE target:ORF EQUAL v:VALUE
      {
            data[0] = source.getText();
            data[1] = type.getText();
            data[2] = target.getText();
            data[3] = v.getText();
      }
    ;



class EdgeAttrLexer extends Lexer;
options {
  k = 2;
  testLiterals = true;
}

ORF: ( 'a'..'z' | 'A'..'Z' | '0'..'9')+ ;

EDGE_TYPE: ( 'a'..'z' | 'A'..'Z' )+ ;

VALUE: '0' '.' ('0'..'9')+;    

EQUAL: '=';

WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
