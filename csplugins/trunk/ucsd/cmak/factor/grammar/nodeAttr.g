header {
package netan.parse;
}

{
import java.util.List;
import java.util.ArrayList;
}
class NodeAttrParser extends Parser;
options {
//  k = 2;
}

parse returns [List l]
{
    l = new ArrayList();
    String[] m;
}
    : WORD ( LPAREN WORD EQUAL WORD RPAREN)? (m=nodeAttr {l.add(m); } )+
    ;

nodeAttr returns [String[] data]
{
    data = new String[2];
}
    : node:WORD EQUAL val:WORD
      {
            data[0] = node.getText();
            data[1] = val.getText();
      }
    ;



class NodeAttrLexer extends Lexer;
options {
  k = 2;
  testLiterals = true;
}

LPAREN: '(';
RPAREN: ')';

WORD: ( 'a'..'z' | 'A'..'Z') ( 'a'..'z' | 'A'..'Z' | '0'..'9' | '-' | '.' | '_' | ',' | '\'')+;

//NUMBER: ('-')* ('0'..'9')  '.' ('0'..'9')+;    

EQUAL: '=';


WS  :   ( ' ' 
        | '\t' 
        | '\r' '\n' { newline(); }
        | '\n' { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
