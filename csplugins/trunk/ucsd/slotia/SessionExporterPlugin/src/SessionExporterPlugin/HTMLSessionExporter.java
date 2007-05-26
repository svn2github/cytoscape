package SessionExporterPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import cytoscape.visual.VisualStyle;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.data.writers.InteractionWriter;
import cytoscape.data.writers.CytoscapeSessionWriter;

public class HTMLSessionExporter
{
	public void export(final SessionExporterSettings settings)
	{
		final Bundle bundle = BundleChooser.chooseBundle(settings);
		if (bundle == null)
			return;
		
		Task task = new Task()
		{
			TaskMonitor monitor = null;
			boolean needToHalt = false;

			public String getTitle()
			{
				return "Session for Web";
			}

			public void setTaskMonitor(TaskMonitor monitor)
			{
				this.monitor = monitor;
			}

			public void halt()
			{
				needToHalt = true;
			}

			private void setStatus(String status)
			{
				if (monitor != null)
					monitor.setStatus(status);
			}

			private void setPercentCompleted(int percent)
			{
				if (monitor != null)
					monitor.setPercentCompleted(percent);
			}

			public void run()
			{
				try
				{
					List<String> networkIDs = networkIDs();
					//crap(networkIDs);
					writeSession();
					generateNetworkFiles(networkIDs);
					generateIndexHTML(networkIDs);
					bundle.close();
				}
				catch (Exception e)
				{
					if (monitor != null)
						monitor.setException(e, "Could not complete session export");
					else
						e.printStackTrace();
				}
			}

			/*
			private void crap(List<String> networkIDs)
			{
				System.out.println("Networks:");
				for (String networkID : networkIDs)
				{
					String title = Cytoscape.getNetwork(networkID).getTitle();
					System.out.println('\t' + title);
				}

				System.out.println("Pages:");
				List<List<String>> pages = breakIntoPages(networkIDs);
				for (List<String> page : pages)
				{
					System.out.println("\tGroups:");
					List<List<String>> groups = breakPageIntoGroups(page);
					for (List<String> group : groups)
					{
						System.out.println("\t\tRows:");
						List<List<String>> rows = breakGroupIntoRows(group);
						for (List<String> row : rows)
						{
							for (String networkID : row)
							{
								String title = Cytoscape.getNetwork(networkID).getTitle();
								System.out.println("\t\t" + title);
							}
							System.out.println("\t\t-- end row");
						}
						System.out.println("\t-- end group");
					}
					System.out.println("-- end page");
				}
			}
			*/

			private List<List<String>> breakIntoPages(List<String> networkIDs)
			{
				List<List<String>> pages = new ArrayList<List<String>>();
				if (!settings.doSeparateIntoPages)
					pages.add(networkIDs);
				else
				{
					List<String> page = new ArrayList<String>();
					for (String networkID : networkIDs)
					{
						page.add(networkID);
						if (page.size() == settings.numNetworksPerPage)
						{
							pages.add(page);
							page = new ArrayList<String>();
						}
					}
					if (page.size() != 0)
						pages.add(page);
				}

				return pages;
			}

			private List<List<String>> breakPageIntoGroups(List<String> page)
			{
				List<List<String>> groups = new ArrayList<List<String>>();
				if (settings.sortImages != SessionExporterSettings.SORT_IMAGES_BY_VISUAL_STYLE)
					groups.add(page);
				else
				{
					Map<VisualStyle,List<String>> vizStyleToIDsMap = vizStyleToIDs(page);
					groups.addAll(vizStyleToIDsMap.values());
				}
				return groups;
			}

			private List<List<String>> breakGroupIntoRows(List<String> group)
			{
				List<List<String>> rows = new ArrayList<List<String>>();
				List<String> currentRow = new ArrayList<String>();
				for (String networkID : group)
				{
					currentRow.add(networkID);
					if (currentRow.size() == settings.numNetworksPerRow)
					{
						rows.add(currentRow);
						currentRow = new ArrayList<String>();
					}
				}
				if (currentRow.size() != 0)
					rows.add(currentRow);

				return rows;
			}

			private Map<String,String> networkTitleToIDMap()
			{
				Map<String,String> map = new HashMap<String,String>();
				Iterator iterator = Cytoscape.getNetworkSet().iterator();
				while (iterator.hasNext())
				{
					CyNetwork network = (CyNetwork) iterator.next();
					String networkID = network.getIdentifier();
					String networkTitle = network.getTitle();
					map.put(networkTitle, networkID);
				}
				return map;
			}

			private Map<VisualStyle,List<String>> vizStyleToIDs(List<String> networkIDs)
			{
				Map<VisualStyle,List<String>> vizStyleToIDsMap = new HashMap<VisualStyle,List<String>>();
				for (String networkID : networkIDs)
				{
					CyNetworkView view = Cytoscape.getNetworkView(networkID);
					VisualStyle vizStyle = view.getVisualStyle();
					if (!vizStyleToIDsMap.containsKey(vizStyle))
						vizStyleToIDsMap.put(vizStyle, new ArrayList<String>());
					vizStyleToIDsMap.get(vizStyle).add(networkID);
				}
				return vizStyleToIDsMap;
			}

			private List<String> networkIDs()
			{
				Map<String,String> networkTitleToIDMap = networkTitleToIDMap();
				List<String> networkIDs = new ArrayList<String>();

				if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_AS_IS)
					networkIDs.addAll(networkTitleToIDMap.values());
				else if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_ALPHABETICALLY)
				{
					Set<String> networkTitles = new TreeSet<String>(networkTitleToIDMap.keySet());
					for (String networkTitle : networkTitles)
						networkIDs.add(networkTitleToIDMap.get(networkTitle));
				}
				else if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_BY_VISUAL_STYLE)
				{
					networkIDs.addAll(networkTitleToIDMap.values());
					Map<VisualStyle,List<String>> vizStyleToIDsMap = vizStyleToIDs(networkIDs);
					networkIDs.clear();
					for (VisualStyle vizStyle : vizStyleToIDsMap.keySet())
						for (String networkID : vizStyleToIDsMap.get(vizStyle))
							networkIDs.add(networkID);
				}
				return networkIDs;
			}

			private void writeSession() throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing Cytoscape session file...");
				setPercentCompleted(1);

				// Write session file to a temporary location
				File tempFile = File.createTempFile("session", "cys");
				CytoscapeSessionWriter sessionWriter = new CytoscapeSessionWriter(tempFile.toString());
				sessionWriter.writeSessionToDisk();

				// Copy the temp file to the bundle
				FileInputStream tempStream = new FileInputStream(tempFile);
				bundle.openEntry(Bundle.sessionFile());
				OutputStream bundleStream = bundle.entryOutputStream();
				byte[] buffer = new byte[1024*1024]; // 1MB buffer
				while (true)
				{
					int bytesRead = tempStream.read(buffer);
					if (bytesRead == -1)
						break;
					bundleStream.write(buffer, 0, bytesRead);
					bundleStream.flush();
					
				}
				bundle.closeEntry();
				tempStream.close();
			} // end writeSession()

			private void generateNetworkFiles(List<String> networkIDs) throws Exception
			{
				Map viewMap = Cytoscape.getNetworkViewMap();
				int currentNetwork = 0;
				int networkCount = networkIDs.size();
				for (String networkID : networkIDs)
				{
					String networkTitle = Cytoscape.getNetwork(networkID).getTitle();

					if (needToHalt)
						return;

					// export the sif file
					setStatus("Exporting SIF for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount);
					bundle.openEntry(Bundle.sifFile(networkTitle));
					CyNetwork network = Cytoscape.getNetwork(networkID);
					InteractionWriter.writeInteractions(network, bundle.entryWriter(), null);
					bundle.closeEntry();

					if (needToHalt)
						return;

					// render network image
					DGraphView view = (DGraphView) viewMap.get(networkID);
					setStatus("Rendering image for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 20 / networkCount);
					BufferedImage image = GraphViewToImage.convert(view, settings);
					if (image != null)
					{
						bundle.openEntry(Bundle.imageFile(networkTitle, settings.imageFormat));
						ImageIO.write(image, settings.imageFormat, bundle.entryOutputStream());
						bundle.closeEntry();
					}

					if (needToHalt)
						return;

					// generate thumbnail
					setStatus("Generating thumbnail for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 40 / networkCount);
					bundle.openEntry(Bundle.thumbnailFile(networkTitle, settings.imageFormat));
					BufferedImage thumbnail = Thumbnails.createThumbnail(image, settings);
					ImageIO.write(thumbnail, settings.imageFormat, bundle.entryOutputStream());
					bundle.closeEntry();
					
					if (needToHalt)
						return;

					// render legend
					setStatus("Rendering legend for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 60 / networkCount);
					VisualStyle visualStyle = Cytoscape.getNetworkView(networkID).getVisualStyle();
					BufferedImage legend = VisualStyleToImage.convert(visualStyle);
					if (legend != null)
					{
						bundle.openEntry(Bundle.legendFile(networkTitle, settings.imageFormat));
						ImageIO.write(legend, settings.imageFormat, bundle.entryOutputStream());
						bundle.closeEntry();
					}

					currentNetwork++;

				} // end for
				
			} // end generateImages()

			private void generateIndexHTML(List<String> networkIDs) throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing HTML pages");
				setPercentCompleted(90);
				
				int pageCount = 0;
				List<List<String>> pages = breakIntoPages(networkIDs);
				for (List<String> page : pages)
				{
					bundle.openEntry(Bundle.indexHTMLFile(pageCount));
					PrintWriter writer = new PrintWriter(bundle.entryWriter());
					writer.println("<html>");
					writer.println("<body>");

					StringBuffer navLinks = new StringBuffer();
					if (pages.size() != 1)
					{
						boolean isFirstPage = (pageCount == 0);
						boolean isLastPage = (pageCount == pages.size() - 1);

						navLinks.append("<font size=-1 align=right>");
						if (!isFirstPage)
							navLinks.append("<a href=\"" + Bundle.indexHTMLFile(pageCount - 1) + "\">");
						navLinks.append("&lt; Previous Page");
						if (!isFirstPage)
							navLinks.append("</a>");
						navLinks.append(" | ");
						if (!isLastPage)
							navLinks.append("<a href=\"" + Bundle.indexHTMLFile(pageCount + 1) + "\">");
						navLinks.append("&gt; Next Page");
						if (!isLastPage)
							navLinks.append("</a>");
						navLinks.append("</font>");
					}
					writer.println(navLinks.toString());

					List<List<String>> groups = breakPageIntoGroups(page);
					for (List<String> group : groups)
					{
						writer.println("<table border=\"0\" cellspacing=\"10\" cellpadding=\"0\">");

						List<List<String>> rows = breakGroupIntoRows(group);
						for (List<String> row : rows)
						{
							writer.println("<tr valign=bottom>");

							for (String networkID : row)
							{
								String networkTitle = Cytoscape.getNetwork(networkID).getTitle();
								boolean hasImageFile = bundle.hasEntry(Bundle.imageFile(networkTitle, settings.imageFormat));
								boolean hasLegendFile = bundle.hasEntry(Bundle.legendFile(networkTitle, settings.imageFormat));
								writer.println("<td align=center valign=bottom>");
								if (hasImageFile)
									writer.print("<a href=\"" + Bundle.imageFile(networkTitle, settings.imageFormat) + "\">");
								writer.print("<img border=0 src=\"" + Bundle.thumbnailFile(networkTitle, settings.imageFormat) + "\">");
								if (hasImageFile)
									writer.print("</a>");

								writer.print("<font size=-1>");
								writer.print("<br>" + networkTitle + " <br/>(");
								writer.print("<a href=\"" + Bundle.sifFile(networkTitle) + "\">sif</a>");
								
								if (hasImageFile)
									writer.print(" | <a href=\"" + Bundle.imageFile(networkTitle, settings.imageFormat) + "\">image</a>");
								if (hasLegendFile)
									writer.print(" | <a href=\"" + Bundle.legendFile(networkTitle, settings.imageFormat) + "\">legend</a>");
					
								writer.print(")</font>");
								writer.println("</td>");
							}
							
							writer.println("</tr>");
						}
						writer.println("</table>");
						if (groups.size() != 1)
							writer.println("<ul>");
					}
					
					writer.println("<br>");
					writer.println(navLinks.toString());
					writer.println("</body>");
					writer.println("</html>");
					bundle.closeEntry();
					pageCount++;
					
				}
			} // end generateHTML
		}; // end new JTask

		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(task, jTaskConfig);
	}
}
