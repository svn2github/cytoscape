import java.io.*;

class GMain {
    public static void main(String[] args) {
        try {
            GL lexer = new GL(new DataInputStream(System.in));
            GP parser = new GP(lexer);
            parser.startRule();
        } catch(Exception e) {
            System.err.println("exception: "+e);
        }
    }
}

