import java.io.*;

import antlr.CommonAST;

class Main {
    public static void main(String[] args) {
        try {
            L lexer = new L(new DataInputStream(System.in));
            P parser = new P(lexer);
            T walker = new T();
            parser.startRule();

            CommonAST tree = (CommonAST) parser.getAST();
            System.out.println("### tree");
            System.out.println(tree);
            System.out.println("###");

            walker.walk(tree);
        } catch(Exception e) {
            System.err.println("exception: "+e);
        }
    }
}

