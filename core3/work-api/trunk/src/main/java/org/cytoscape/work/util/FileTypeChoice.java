package org.cytoscape.work.util;

import java.util.List;
import org.cytoscape.io.CyFileFilter;

/**
 * Used to communicate the user's desired file type when selecting a file.
 * Example:
 * <p><pre>
 * class MyClass implements Task
 * {
 *   @Tunable(description = "File to load", fileTypeChoiceName="fileTypeChoice")
 *   public File file;
 *   public FileTypeChoice fileTypeChoice = new FileTypeChoice(...);
 *   ...
 *
 *   public void run(TaskMonitor monitor)
 *   {
 *     System.out.println("User has selected: " + fileTypeChoice.getSelectedFileFilter())
 *     ...
 *   }
 *   ...
 * }
 * </pre></p>
 *
 * @author Pasteur
 */
public class FileTypeChoice
{
	final List<CyFileFilter> choices;
	final boolean allowAcceptAll;
	CyFileFilter selected = null;
	
	/**
	 * Default constructor where the file chooser dialog allows an option for selecting any file.
	 * @param choices A list of CyFileFilters the user can choose from
	 */
	public FileTypeChoice(List<CyFileFilter> choices)
	{
		this(true, choices);
	}

	/**
	 * @param allowAcceptAll Specifies whether the file chooser dialog should allow an option for selecting any file
	 * @param choices A list of CyFileFilters the user can choose from
	 */
	public FileTypeChoice(boolean allowAcceptAll, List<CyFileFilter> choices)
	{
		if (choices == null)
			throw new IllegalArgumentException("choices == null");
		this.allowAcceptAll = allowAcceptAll;
		this.choices = choices;
	}

	public List<CyFileFilter> getChoices()
	{
		return choices;
	}

	/**
	 * @return the CyFileFilter selected by the user or null if the user chose the option for selecting any file.
	 */
	public CyFileFilter getSelectedFileFilter()
	{
		return selected;
	}

	/**
	 * No need to use this method--it is used by the Tunables framework
	 */
	public void setSelectedFileFilterType(CyFileFilter selected)
	{
		if (!choices.contains(selected))
			throw new IllegalArgumentException("!choices.contains(selected)");
		this.selected = selected;
	}

	public boolean allowAcceptAll()
	{
		return allowAcceptAll;
	}
}
