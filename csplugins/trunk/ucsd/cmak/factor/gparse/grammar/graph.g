class GP extends Parser;
options {
 k=4;
}
{
    String lastSource="";
    String lastTarget="";
    
    boolean matchesLastSource(Token t)
    {
//        System.out.println("matching token text: " + t.getText() + ".");
        return lastSource.equals(t.getText());
    }

    boolean matchesLastTarget(Token t)
    {
//        System.out.println("matching token text: " + t.getText() + ".");
        return lastTarget.equals(t.getText());
    }


}


startRule
    : ( continuedEdge | directedEdge ) *
    ;

continuedEdge
    : {matchesLastTarget(LT(3))}? OPEN e:EDGE n1:NODE n2:NODE CLOSE
        { 
            lastSource = n1.getText();
            lastTarget = n2.getText();
            System.out.println("continued Edge: " + e.getText() + ", "
                + n1.getText() + "-" + n2.getText());
        }
    ;

directedEdge
    : OPEN e:EDGE n1:NODE n2:NODE CLOSE
        { 
            lastSource = n1.getText();
            lastTarget = n2.getText();
            System.out.println("directed Edge: " + e.getText() + ", "
                       + n1.getText() + "-" + n2.getText());
        }
    ;

class GL extends Lexer;
options {
   k=2;
}

OPEN: '('
    ;

CLOSE: ')'
    ;

// one-or-more letters followed by a newline
NODE:   ( 'A'..'Z'|'0'..'9' )+ 
    ;

EDGE: ( "pp" | "pd" | "ypd" | "mms" )+ 
    ;


WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
