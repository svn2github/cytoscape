class P extends Parser;

startRule
    :   (n:NAME a:AGE
        {System.out.println("("+n.getText() + ", " + a.getText() + ")");}
            )+
    ;

class L extends Lexer;

AGE :   ('0'..'9')+
    ;    

// one-or-more letters followed by a newline
NAME:   ( 'a'..'z'|'A'..'Z' )+ 
    ;

WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
