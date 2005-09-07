package cytoscape.cruft.obo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * The purpose of this class is to convert a 'gene_association.*' file
 * (for example 'gene_association.sgd') into a crufty old 'molfunc.anno' file.
 * The parameter passed to the constructor is the 'gene_association.*' file
 * and the content read from this reader is the crufty old 'molfunc.anno'
 * file.<p>
 * Please note that this code was not written with performance in mind.
 * Therefore, evaluating this code will not give a good indication of the
 * author's programming ability.
 */
public final class MolecularFunctionAnnotationReader extends Reader
{

  private final String NL = System.getProperty("line.separator");
  private BufferedReader m_file;
  private String m_readString;
  private int m_readInx; // Index into a m_readString character.

  public MolecularFunctionAnnotationReader(final String speciesName,
                                           final Reader geneAssociationFile)
  {
    if (geneAssociationFile == null)
      throw new NullPointerException("geneAssociationFile is null");
    m_file = new BufferedReader(geneAssociationFile);
    m_readString = "(species=" + speciesName +
      ") (type=Molecular Function) (curator=??)" + NL;
    m_readInx = 0;
  }

  public final int read(final char[] cbuf,
                        final int off,
                        final int len) throws IOException
  {
    if (m_file == null) throw new IOException("this stream is closed");
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
    if (m_readInx == m_readString.length()) readMore();
    return returnThis;
  }

  private final void readMore() throws IOException
  {
  }

  /**
   * Closes the underlying gene_association file stream as well.
   */
  public final void close() throws IOException
  {
    try {
      m_file.close(); }
    finally {
      m_file = null; }
  }

}
