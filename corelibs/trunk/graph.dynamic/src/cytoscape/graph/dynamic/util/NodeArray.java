package cytoscape.graph.dynamic.util;


// Package visible.
// Valid indices: [0, Integer.MAX_VALUE - 1].
final class NodeArray {
    private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

    /* private */ Node[] m_nodeArr; // Not private for serialization.

    NodeArray() {
        m_nodeArr = new Node[INITIAL_CAPACITY];
    }

    // Understand that this method will not increase the size of the underlying
    // array, no matter what.
    // Throws ArrayIndexOutOfBoundsException if index is negative.
    // The package-level agreement for this class is that Integer.MAX_VALUE
    // will never be passed to this method.
    final Node getNodeAtIndex(final int index) {
        if (index >= m_nodeArr.length) {
            return null;
        }

        return m_nodeArr[index];
    }

    // Understand that this method will potentially increase the size of the
    // underlying array, but only if two conditions hold:
    //   1. node is not null and
    //   2. index is greater than or equal to the length of the array.
    // Throws ArrayIndexOutOfBoundsException if index is negative.
    // The package-level agreement for this class is that Integer.MAX_VALUE
    // will never be passed to this method.
    final void setNodeAtIndex(final Node node, final int index) {
        if ((index >= m_nodeArr.length) && (node == null)) {
            return;
        }

        try {
            m_nodeArr[index] = node;
        } catch (ArrayIndexOutOfBoundsException e) {
            if (index < 0) {
                throw e;
            }

            final int newArrSize = (int) Math.min((long) Integer.MAX_VALUE,
                    Math.max((((long) m_nodeArr.length) * 2L) + 1L,
                        ((long) index) + 1L + (long) INITIAL_CAPACITY));
            final Node[] newArr = new Node[newArrSize];
            System.arraycopy(m_nodeArr, 0, newArr, 0, m_nodeArr.length);
            m_nodeArr = newArr;
            m_nodeArr[index] = node;
        }
    }
}
