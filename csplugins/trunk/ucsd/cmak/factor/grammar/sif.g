header {
package netan.parse;
}

{
import java.util.List;
import java.util.ArrayList;
}
class SifParser extends Parser;
options {
//  k = 2;
}

parse returns [List l]
{
    l = new ArrayList();
    String[] m;
}
    : (m=edge {l.add(m); } )+
    ;

edge returns [String[] data]
{
    data = new String[3];
}
    : source:WORD type:WORD target:WORD
      {
            data[0] = source.getText();
            data[1] = type.getText();
            data[2] = target.getText();
      }
    ;



class SifLexer extends Lexer;
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
