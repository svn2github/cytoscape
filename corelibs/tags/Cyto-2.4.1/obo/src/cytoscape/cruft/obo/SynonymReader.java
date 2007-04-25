package cytoscape.cruft.obo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

/**
 * The purpose of this class is to convert a 'gene_association.*' file
 * (for example 'gene_association.sgd') into a crufty old '*.syno' file that
 * Cytoscape's BioDataServer understands.
 * The parameter passed to the constructor is the 'gene_association.*' file
 * and the content read from this reader is the crufty old '*.syno' file.<p>
 * Please note that this code was not written with performance in mind.
 * Therefore, evaluating this code will not give a good indication of the
 * author's programming ability.
 */
public final class SynonymReader extends Reader
{

  private final String NL = System.getProperty("line.separator");
  private final HashMap m_dupsFilter = new HashMap();
  private BufferedReader m_file;
  private String m_readString;
  private int m_readInx; // Index into a m_readString character.

  public SynonymReader(final String speciesName,
                       final Reader geneAssociationFile)
  {
    if (geneAssociationFile == null)
      throw new NullPointerException("geneAssociationFile is null");
    m_file = new BufferedReader(geneAssociationFile);
    m_readString = speciesName + NL;
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
    while (true)
    {
      String line;
      while (true) // Read comments and possibly blank lines.
      {
        line = m_file.readLine();
        if (line == null) { // End of underlying stream.
          m_readString = null;
          return; }
        line = line.trim();
        if (line.length() > 0 && !line.startsWith("!")) {
          break; }
      }

      // Now line contains a line of data.
      int fromIndex = 0;
      for (int i = 0; i < 2; i++) {
        fromIndex = 1 + line.indexOf('\t', fromIndex); }
      final String canon =
        line.substring(fromIndex, line.indexOf('\t', fromIndex));
      if (m_dupsFilter.get(canon) == null) { // This synonym new.
        for (int i = 0; i < 8; i++) {
          fromIndex = 1 + line.indexOf('\t', fromIndex); }
        final String delimitedList =
          line.substring(fromIndex, line.indexOf('\t', fromIndex));
        if (delimitedList.length() > 0) {
          m_readString = canon + " " + delimitedList.replace('|', ' ') + NL;
          
          
          System.out.println("Syno: " + m_readString);
          
          
          m_readInx = 0;
          m_dupsFilter.put(canon, canon);
          break; } }
    }
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
