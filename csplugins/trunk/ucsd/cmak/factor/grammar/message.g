class T extends TreeParser;

walk: (message)+;

message
    : #(TYPE2 ID PROB PROB) {System.out.println("type tree"); }
    | #(D ID PROB PROB (PLUS|MINUS)) {System.out.println("d tree"); }
    | #(K ID PROB PROB PROB) {System.out.println("k tree"); }
    | #(TK ID PROB PROB PROB) {System.out.println("tk tree"); }
    ;

{
    import java.util.List;
    import java.util.ArrayList;

    import cern.colt.map.OpenIntObjectHashMap;
}
class P extends Parser;
options {
  k = 8;
  buildAST = true;
}


head: HEADER LPAREN NODE NODE RPAREN;

plus_minus returns [State s]
{s = null;}
    : PLUS { s = State.PLUS; }
    | MINUS { s =  State.MINUS; }
    ;

table2 returns [ double[] vals]
{ vals = new double[2]; }
    : LBRACK! p1:PROB p2:PROB RBRACK!
        { 
            vals[0] = Double.parseDouble(p1.getText());
            vals[1] = Double.parseDouble(p2.getText());
        }
    ;

table3 returns [ double[] vals]
{ vals = new double[3]; }
    : LBRACK! p1:PROB p2:PROB p3:PROB RBRACK!
        { 
            vals[0] = Double.parseDouble(p1.getText());
            vals[1] = Double.parseDouble(p2.getText());
            vals[2] = Double.parseDouble(p3.getText());
        }
    ;


parseMessages returns [OpenIntObjectHashMap[] messages]
{
    messages = new OpenIntObjectHashMap[2];
    messages[0] = new OpenIntObjectHashMap();
    messages[1] = new OpenIntObjectHashMap();
    TestMessage m;
    TestMessage r;
}
    : (m=message 
            {
                messages[0].put(m.getIndex(), m);
            }
        | r=result 
            {
                messages[1].put(r.getIndex(), r);
            } 
      )+
    ;


result returns [TestMessage tm]
{
    tm = null;
    ProbTable pt;
    double[] v;
}
    : (head ID TYPE2) => (head! i:ID t:TYPE2^ v=table2
            {

                String type = t.getText();
                //System.out.println("msg ("+i.getText() + ", " + t.getText() + ")");
                if(type.equals("tx"))
                {
                    pt = NodeFactory.createEdge(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.EDGE);
                }
                else if(type.equals("ts"))
                {
                    pt = NodeFactory.createSign(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.SIGN);
                }
                else if(type.equals("tp"))
                {
                    pt = NodeFactory.createPathActive(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.PATH_ACTIVE);
                }
                else if(type.equals("td"))
                {
                    pt = NodeFactory.createDir(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.DIR);
                }
            }
        )
    | (head ID TK) => (head! i2:ID t2:TK^ v=table3
        {
            pt = NodeFactory.createKO(v[0], v[1], v[2]);
            tm = new TestMessage(Integer.parseInt(i2.getText()), pt,
                                 NodeType.KO);
        }
       )
    ;


message returns [ TestMessage tm]
{
    ProbTable pt = null;
    tm = null;
    double[] v;
    State pm = null;
    
}
    : (head ID MSG) => (head! i:ID t:MSG^ v=table2
            {
                String type = t.getText();
                //System.out.println("msg ("+i.getText() + ", " + t.getText() + ")");
                if(type.equals("x"))
                {
                    pt = NodeFactory.createEdge(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.EDGE);
                }
                else if(type.equals("s"))
                {
                    pt = NodeFactory.createSign(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.SIGN);
                }
                else if(type.equals("p"))
                {
                    pt = NodeFactory.createPathActive(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.PATH_ACTIVE);
                }
            }
            )
   
    | (head ID D) => (head! i2:ID t2:D^ v=table2 pm=plus_minus
            {
                //System.out.println("dir ("+i2.getText() + ", " + t2.getText() + ")");
                pt = NodeFactory.createDir(v[0], v[1]);
                tm = new TestMessage(Integer.parseInt(i2.getText()), pt,
                                     NodeType.DIR, pm);
            }
            )
    | (head ID K) => (head! i3:ID t3:K^ v=table3
            {
                //System.out.println("k ("+i3.getText() + ", " + t3.getText() + ")");
                pt = NodeFactory.createKO(v[0], v[1], v[2]);
                tm = new TestMessage(Integer.parseInt(i3.getText()), pt,
                                     NodeType.KO);
            }
            )

    ;


class L extends Lexer;
options {
  k = 2;
  testLiterals = true;
}

HEADER: ("f2v" | "v2f");

LBRACK: '[';
RBRACK: ']';

LPAREN: '(';
RPAREN: ')';

protected
NODE: '-' ID;

ID: ('0'..'9')+;    

PROB: ('0'|'1') '.' ID
    ;

PLUS: "+1";

protected
MINUS: "-1";

NODE_MINUS
    : ('-' '1') => MINUS {$setType(MINUS);}
    | ('-' ID) => NODE {$setType(NODE);}
    ;

protected
X: 'x';
protected
P: 'p';
protected
S: 's';

protected
TX: "tx";
protected
TD: "td";
protected
TS: "ts";
protected
TP: "tp";

D: 'd';
K: 'k';
TK: "tk";
MSG: (X|P|S);
TYPE2: (TX|TP|TS|TD);

WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;
