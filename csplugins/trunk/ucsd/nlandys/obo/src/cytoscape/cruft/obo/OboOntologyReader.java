package cytoscape.cruft.obo;

import java.io.IOException;
import java.io.Reader;

/**
 * The purpose of this class is to convert a 'gene_ontology.obo' file
 * to a crufty old 'go.onto' file.
 * The parameter passed to the constructor is the 'gene_ontology.obo' file and
 * the content read from this reader is the crufty old 'go.onto' file.<p>
 * Please note that this code was not written with performance in mind.
 * Therefore, evaluating this code will not give a good indication of the
 * programming ability of the author of this code, Nerius Landys.
 */
public final class OboOntologyReader extends Reader
{

  private Reader m_obo;
  private String m_readString;
  private int m_readInx; // Index into a m_readString character.

  public OboOntologyReader(final Reader oboFile)
  {
    if (oboFile == null) throw new NullPointerException("oboFile is null");
    m_obo = oboFile;
    m_readString = null;
  }

  public final int read(final char[] cbuf,
                        final int off,
                        final int len) throws IOException
  {
    if (m_obo == null) throw new IOException("this stream is closed");
    if (m_readString == null) readMore();
    if (m_readString == null) return -1;
    final int returnThis;
    if (m_readString.length() - m_readInx >= len) {
      m_readString.getChars(m_readInx, m_readInx + len,
                            cbuf, off);
      returnThis = len;
      m_readInx += len; }
    else { // len is greater than the number of chars left in m_readString.
      m_readString.getChars(m_readInx, m_readString.length(),
                            cbuf, off);
      returnThis = m_readString.length() - m_readInx;
      m_readInx = m_readString.length(); }
    if (m_readInx == m_readString.length()) m_readString = null;
    return returnThis;
  }

  private final void readMore()
  {
  }

  /**
   * Closes the underlying obo file stream as well.
   */
  public final void close() throws IOException
  {
    try {
      m_obo.close(); }
    finally {
      m_obo = null; }
  }

}
