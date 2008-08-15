/**
 * 
 */
package cytoscape.plugin;


/**
 * @author skillcoy
 *
 */
public class InvalidDownloadable extends PluginException
	{
	private final static long serialVersionUID = 1202339870229337L;

	/**
	 * @param arg0
	 */
	public InvalidDownloadable(String arg0)
		{
		super(arg0);
		}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidDownloadable(String arg0, Throwable arg1)
		{
		super(arg0, arg1);
		}

	}
