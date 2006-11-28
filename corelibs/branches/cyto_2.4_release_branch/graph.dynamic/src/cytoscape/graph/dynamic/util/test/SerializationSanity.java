package cytoscape.graph.dynamic.util.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class SerializationSanity implements Serializable {
    private static final long serialVersionUID = 796950138L;

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws IllegalStateException DOCUMENT ME!
     * @throws NullPointerException DOCUMENT ME!
     */
    public static void main(String[] args) throws Exception {
        final int loopSize = Integer.parseInt(args[0]);
        final SerializationSanity ss = new SerializationSanity();

        {
            final Node nodeLoop = new Node();
            Node currNode = nodeLoop;

            for (int i = 1; i < loopSize; i++) {
                currNode.m_next = new Node();
                currNode = currNode.m_next;
            }

            currNode.m_next = nodeLoop;
            currNode = nodeLoop;

            while (true) {
                currNode.m_next.m_prev = currNode;

                if (currNode.m_next == nodeLoop) {
                    break;
                }

                currNode = currNode.m_next;
            }

            ss.m_node = nodeLoop;
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(ss);
        objOut.writeObject(null);
        objOut.flush();
        objOut.close();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        SerializationSanity ss2 = (SerializationSanity) objIn.readObject();
        Object nullObj = objIn.readObject();
        objIn.close();

        if (nullObj != null) {
            throw new IllegalStateException("expected a null object");
        }

        final Node[] nodes = new Node[loopSize];
        Node currNode = ss2.m_node;

        while (true) {
            if (currNode == null) {
                throw new NullPointerException();
            }

            boolean timeToBreak = false;

            for (int i = 0;; i++) {
                if (nodes[i] == currNode) {
                    timeToBreak = true;

                    break;
                }

                if (nodes[i] == null) {
                    nodes[i] = currNode;

                    break;
                }
            }

            if (timeToBreak) {
                break;
            }

            currNode = currNode.m_next;
        }

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) {
                throw new IllegalStateException("bad juju in next list");
            }

            nodes[i] = null;
        }

        currNode = ss2.m_node;

        while (true) {
            if (currNode == null) {
                throw new NullPointerException();
            }

            boolean timeToBreak = false;

            for (int i = 0;; i++) {
                if (nodes[i] == currNode) {
                    timeToBreak = true;

                    break;
                }

                if (nodes[i] == null) {
                    nodes[i] = currNode;

                    break;
                }
            }

            if (timeToBreak) {
                break;
            }

            currNode = currNode.m_prev;
        }

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) {
                throw new IllegalStateException("bad juju in prev list");
            }
        }
    }

    private Node m_node;

    private SerializationSanity() {
    }

    private static class Node implements Serializable {
        private static final long serialVersionUID = 629716647L;
        private Node m_next;
        private Node m_prev;

        private Node() {
        }
    }
}
