package cytoscape.cruft.obo;

import java.io.IOException;
import java.io.Reader;

/**
 * The purpose of this class is to convert a 'gene_ontology.obo' file
 * to a crufty old 'go.onto' file.
 * The parameter passed to the constructor is the 'gene_ontology.obo' file and
 * the content read from this reader is the crufty old 'go.onto' file.
 */
public final class OboOntologyReader extends Reader
{

  public OboOntologyReader(final Reader oboFile)
  {
  }

  public final int read(final char[] cbuf,
                        final int off,
                        final int len) throws IOException
  {
    return -1;
  }

  public final void close() throws IOException
  {
  }

}
