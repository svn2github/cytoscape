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
    data = new String[5];
}
    : source:WORD type:WORD target:WORD EQUAL pval:VALUE logratio:VALUE
      {
            data[0] = source.getText();
            data[1] = type.getText();
            data[2] = target.getText();
            data[3] = pval.getText();
            data[4] = logratio.getText();
      }
    ;



class EdgeAttrLexer extends Lexer;
options {
  k = 2;
  testLiterals = true;
}

WORD: ( 'a'..'z' | 'A'..'Z') ( 'a'..'z' | 'A'..'Z' | '0'..'9' | '-')+;

//VALUE: '0' '.' ('0'..'9')+;    

VALUE: ('-')* ('0'..'9')  '.' ('0'..'9')+;    

EQUAL: '=';

WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
