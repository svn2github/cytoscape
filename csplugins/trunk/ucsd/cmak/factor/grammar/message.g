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
    import java.util.LinkedHashMap;

    import cern.colt.map.OpenIntObjectHashMap;
}
class P extends Parser;
options {
  k = 8;
  buildAST = true;
}

nodes returns [String[] n]
{n = new String[2]; }
    : LPAREN from:NODE to:NODE RPAREN
	{ 
	    n[0] = from.getText();
	    n[1] = to.getText();
	}
    ;

fhead returns [String[] n]
{
    n = null;
}
    : F2V n=nodes;

vhead returns [String[] n]
{
    n = null;
}
    : V2F n=nodes;

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


parseMessages returns [List l]
{
    l = new ArrayList();
    LinkedHashMap[] m;
}
    : (m=messageBlock {l.add(m);} )+
    ;


/**
* @returns an array of 2 LinkedHashMaps.
* The first map contains the variable2factor messages in the same order
* that they appear in the input file.  The map key is the index of the 
* message and the value of a TestMessage object.
*
* The second map contains the factor2variable messages in the se same order
* that they appear in the input file.  The map is a String of the "from"
* node concatenated with the "to" node.  This allows you to look up the
* factor2var TestMessage associated with a given var2factor TestMessage.
*/
messageBlock returns [LinkedHashMap[] messages]
{
    messages = new LinkedHashMap[2];
    messages[0] = new LinkedHashMap();
    messages[1] = new LinkedHashMap();
    TestMessage f2v;
    TestMessage v2f;
}
    : n:NODE (NODE_TEXT)+ LCURLY (v2f=v2fMessage 
            {
                messages[0].put(v2f.getFrom() + v2f.getTo(), v2f);
            }
        | f2v=f2vMessage 
            {
                messages[1].put(f2v.getFrom() + f2v.getTo(), f2v);
            } 
      )+ RCURLY
	{
	    System.out.println("parsed node: " + n.getText());
	}
    ;


f2vMessage returns [TestMessage tm]
{
    tm = null;
    ProbTable pt;
    double[] v;
    String[] n;
}
    : (fhead ID TYPE2) => (n=fhead! i:ID t:TYPE2^ v=table2
            {

                String type = t.getText();
                //System.out.println("msg ("+i.getText() + ", " + t.getText() + ")");
                if(type.equals("tx"))
                {
                    pt = NodeFactory.createEdge(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.EDGE);
		    tm.setFrom(n[0]);
		    tm.setTo(n[1]);
                }
                else if(type.equals("ts"))
                {
                    pt = NodeFactory.createSign(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.SIGN);
		    tm.setFrom(n[0]);
		    tm.setTo(n[1]);
                }
                else if(type.equals("tp"))
                {
                    pt = NodeFactory.createPathActive(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.PATH_ACTIVE);
		    tm.setFrom(n[0]);
		    tm.setTo(n[1]);
                }
                else if(type.equals("td"))
                {
                    pt = NodeFactory.createDir(v[0], v[1]);
                    tm = new TestMessage(Integer.parseInt(i.getText()), pt,
                                         NodeType.DIR);
		    tm.setFrom(n[0]);
		    tm.setTo(n[1]);
                }
            }
        )
    | (fhead ID TK) => (n=fhead! i2:ID t2:TK^ v=table3
        {
            pt = NodeFactory.createKO(v[0], v[1], v[2]);
            tm = new TestMessage(Integer.parseInt(i2.getText()), pt,
                                 NodeType.KO);
		tm.setFrom(n[0]);
		tm.setTo(n[1]);
        }
       )
    ;


v2fMessage returns [ TestMessage tm]
{
    ProbTable pt = null;
    tm = null;
    double[] v;
    State pm = null;
    String[] n = null;
    
}
    : (vhead ID MSG) => (n=vhead! i:ID t:MSG^ v=table2
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

		tm.setFrom(n[0]);
		tm.setTo(n[1]);
            }
            )
   
    | (vhead ID D) => (n=vhead! i2:ID t2:D^ v=table2 pm=plus_minus
            {
                //System.out.println("dir ("+i2.getText() + ", " + t2.getText() + ")");
                pt = NodeFactory.createDir(v[0], v[1]);
                tm = new TestMessage(Integer.parseInt(i2.getText()), pt,
                                     NodeType.DIR, pm);
		tm.setFrom(n[0]);
		tm.setTo(n[1]);
            }
            )
    | (vhead ID K) => (n=vhead! i3:ID t3:K^ v=table3
            {
                //System.out.println("k ("+i3.getText() + ", " + t3.getText() + ")");
                pt = NodeFactory.createKO(v[0], v[1], v[2]);
                tm = new TestMessage(Integer.parseInt(i3.getText()), pt,
                                     NodeType.KO);

		tm.setFrom(n[0]);
		tm.setTo(n[1]);
            }
            )

    ;


class L extends Lexer;
options {
  k = 2;
  testLiterals = true;
}

F2V: "f2v";
V2F:  "v2f";

LBRACK: '[';
RBRACK: ']';

LPAREN: '(';
RPAREN: ')';


LCURLY: '{';
RCURLY: '}';

NODE_TEXT: ("path_factor" | "Factor");

NODE: '-' ID;

ID: ('0'..'9')+;    

PROB: ('0'|'1') '.' ID
    ;

PLUS: "+";

MINUS: "-";

T: 't';
PSI: "psi";
OR: "or";

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
