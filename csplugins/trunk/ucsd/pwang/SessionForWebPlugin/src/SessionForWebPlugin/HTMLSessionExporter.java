package SessionForWebPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.awt.image.BufferedImage;
import javax.swing.tree.TreeModel;
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
import cytoscape.data.CyAttributes;
import cytoscape.data.writers.InteractionWriter;
import cytoscape.data.writers.CytoscapeSessionWriter;

public class HTMLSessionExporter
{
	public void export(final SessionExporterSettings settings, final Bundle bundle)
	{
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
					if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS) {
						File destDir = settings.destinationDir;

						String prefixDir = destDir.getName();
						int idx = prefixDir.lastIndexOf(".");
						if (idx > 0){
							prefixDir = prefixDir.substring(0, idx);
						}
						
						if (prefixDir.length()>0){
							prefixDir += "/";
						}
						
						generateNetworkFilesForCellCircuits(prefixDir, networkIDs);	
						addPubData2Zip(prefixDir);
					}
					else {
						writeSession();
						generateNetworkFiles(networkIDs);
						generateIndexHTML(networkIDs);	
					}
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

			/**
			 * Takes a list of network IDs and breaks them up into pages,
			 * where a page is a list of network IDs.
			 * If the settings say not to break up into pages,
			 * this will return a single page with all the network IDs.
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

			/**
			 * Takes a page and breaks it into groups.
			 * One group has one visual style. If the settings say
			 * not to sort by visual style, this will return
			 * a single group with the network IDs from the page
			 */
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

			/**
			 * Takes a group and breaks it into rows.
			 */
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

			/**
			 * Groups a list of network IDs together based on visual style.
			 * @return A map where visual styles map to network IDs
			 */
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

			/**
			 * @return a list of network IDs that can be unsorted, sorted by
			 * visual style, or sorted alphabetically, depending on the settings.
			 */
			private List<String> networkIDs()
			{
				Map<String,String> networkTitleToIDMap = SessionForWebPlugin.networkTitleToIDMap();
				List<String> networkIDs = new ArrayList<String>();

				if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_AS_IS)
				{
					TreeModel model = Cytoscape.getDesktop().getNetworkPanel().getTreeTable().getTree().getModel();
					searchTreeModel(networkIDs, networkTitleToIDMap, model, model.getRoot());
				}
				else if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_ALPHABETICALLY)
				{
					Set<String> networkTitles = new TreeSet<String>(networkTitleToIDMap.keySet());
					for (String networkTitle : networkTitles)
					{
						String networkID = networkTitleToIDMap.get(networkTitle);
						if (settings.networks.contains(networkID))
							networkIDs.add(networkID);
					}
				}
				else if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_BY_VISUAL_STYLE)
				{
					networkIDs.addAll(networkTitleToIDMap.values());
					Map<VisualStyle,List<String>> vizStyleToIDsMap = vizStyleToIDs(networkIDs);
					networkIDs.clear();
					for (VisualStyle vizStyle : vizStyleToIDsMap.keySet())
						for (String networkID : vizStyleToIDsMap.get(vizStyle))
							if (settings.networks.contains(networkID))
								networkIDs.add(networkID);
				}
				return networkIDs;
			}

			/**
			 * Goes through a TreeModel and adds network IDs it finds to the networkIDs list.
			 */
			private void searchTreeModel(List<String> networkIDs, Map<String,String> networkTitleToIDMap, TreeModel model, Object node)
			{
				if (node != model.getRoot())
				{
					String networkID = networkTitleToIDMap.get(node.toString());
					if (settings.networks.contains(networkID))
						networkIDs.add(networkID);
				}
				for (int i = 0; i < model.getChildCount(node); i++)
					searchTreeModel(networkIDs, networkTitleToIDMap, model, model.getChild(node, i));
			}

			/**
			 * Writes a Cytoscape session file to the bundle.
			 * <i>This is currently a hack:</i> CytoscapeSessionWriter
			 * cannot take an arbitrary InputStream or Writer and write
			 * the contents of the session file to it. Therefore, this method
			 * creates a temporary file that CytoscapeSessionWriter can write to.
			 * It then reads the temporary file byte by byte, and writes it
			 * to the bundle.
			 */
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

			private String getSpeciesSubDirectory(String speciesStr){
				// Turn the string "Saccharomyces cerevisia,Homo sapiens" into "Saccharomyces_cerevisia_Homo_sapiens"
				if (speciesStr == null || speciesStr.trim().equals("")) {
					return "unknown_species";
				}
				String retValue = "";
				String[] items = speciesStr.split(",");
				for (int i=0; i< items.length; i++){
					String oneSpecies = items[i];
					String[] components = oneSpecies.split(" ");
					for (int j=0; j<components.length; j++ ) {
						if (!retValue.equals("")) {
							retValue += "_";
						}
						retValue += components[j];
					}
				}
				return retValue;
			}
			
			private void generateNetworkFiles(List<String> networkIDs) throws Exception
			{
				Map viewMap = Cytoscape.getNetworkViewMap();
				Map<VisualStyle,BufferedImage> vizStyles = new HashMap<VisualStyle,BufferedImage>();
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
					if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS) {
						/*
						ZipBundle2 zipBundle2 = (ZipBundle2) bundle;
						
						CyAttributes cyAttrs = Cytoscape.getNetworkAttributes();					  	
						String species = cyAttrs.getStringAttribute(networkID, "species");
						String speciesSubDir = getSpeciesSubDirectory(species);
						
						zipBundle2.openEntry("sif/"+speciesSubDir, Bundle.sifFile(networkTitle));
						
						CyNetwork network = Cytoscape.getNetwork(networkID);
						InteractionWriter.writeInteractions(network, zipBundle2.entryWriter(), null);	
						*/					
					}
					else {
						bundle.openEntry(Bundle.sifFile(networkTitle));
						CyNetwork network = Cytoscape.getNetwork(networkID);
						InteractionWriter.writeInteractions(network, bundle.entryWriter(), null);
					}
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
						if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS) {
							/*
							ZipBundle2 zipBundle2 = (ZipBundle2) bundle;
							zipBundle2.openEntry("img", zipBundle2.imageFile(networkTitle, settings.imageFormat));
							ImageIO.write(image, settings.imageFormat, bundle.entryOutputStream());
							*/
						}
						else {
							bundle.openEntry(Bundle.imageFile(networkTitle, settings.imageFormat));
							ImageIO.write(image, settings.imageFormat, bundle.entryOutputStream());
						}
						bundle.closeEntry();
					}

					if (needToHalt)
						return;

					// generate thumbnail
					setStatus("Generating thumbnail for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 40 / networkCount);
					if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS) {
						/*
						ZipBundle2 zipBundle2 = (ZipBundle2) bundle;
						zipBundle2.openEntry("thm_img", ZipBundle2.thumbnailFile(networkTitle, settings.imageFormat));
						BufferedImage thumbnail = Thumbnails.createThumbnail(image, settings);
						ImageIO.write(thumbnail, settings.imageFormat, zipBundle2.entryOutputStream());	
						*/
					}
					else {
						bundle.openEntry(Bundle.thumbnailFile(networkTitle, settings.imageFormat));
						BufferedImage thumbnail = Thumbnails.createThumbnail(image, settings);
						ImageIO.write(thumbnail, settings.imageFormat, bundle.entryOutputStream());	
					}
					bundle.closeEntry();
					
					if (needToHalt)
						return;

					if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS) {
						continue;
					}
					
					// render legend
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 60 / networkCount);
					VisualStyle visualStyle = Cytoscape.getNetworkView(networkID).getVisualStyle();

					BufferedImage legend = vizStyles.get(visualStyle); 
					if ( legend == null ) 
					{
						setStatus("Rendering legend for: " + networkTitle);
						legend = VisualStyleToImage.convert(visualStyle);
						vizStyles.put(visualStyle,legend);
					}

					if (legend != null)
					{
						bundle.openEntry(Bundle.legendFile(networkTitle, settings.imageFormat));
						ImageIO.write(legend, settings.imageFormat, bundle.entryOutputStream());
						bundle.closeEntry();
					}

					if (bundle.hasEntry(Bundle.imageFile(networkTitle, settings.imageFormat)))
					{
						bundle.openEntry(Bundle.networkHTMLFile(networkTitle));
						PrintWriter writer = new PrintWriter(bundle.entryWriter());
						writer.println("<html>");
						writer.println("<body>");
						writer.println("<p><img src=\"" + Bundle.imageFile(networkTitle, settings.imageFormat) + "\"></p>");
						if (bundle.hasEntry(Bundle.legendFile(networkTitle, settings.imageFormat)))
						{
							writer.println("<hr>");
							writer.println("<p align=center>Legend:</p>");
							writer.println("<p align=center><img src=\"" + Bundle.legendFile(networkTitle, settings.imageFormat) + "\"></p>");
						}
						writer.println("</body>");
						writer.println("</html>");
						bundle.closeEntry();
					}

					currentNetwork++;

				} // end for
				
			} // end generateImages()

			
			private void generateNetworkFilesForCellCircuits(String prefixDir, List<String> networkIDs) throws Exception
			{
				Map viewMap = Cytoscape.getNetworkViewMap();
				Map<VisualStyle,BufferedImage> vizStyles = new HashMap<VisualStyle,BufferedImage>();
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
					ZipBundle2 zipBundle2 = (ZipBundle2) bundle;
						
					CyAttributes cyAttrs = Cytoscape.getNetworkAttributes();					  	
					String species = cyAttrs.getStringAttribute(networkID, "species");
					String speciesSubDir = getSpeciesSubDirectory(species);
						
					zipBundle2.openEntry(prefixDir+"sif/"+speciesSubDir, Bundle.sifFile(networkTitle));
						
					CyNetwork network = Cytoscape.getNetwork(networkID);
					InteractionWriter.writeInteractions(network, zipBundle2.entryWriter(), null);						
					
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
						//ZipBundle2 zipBundle2 = (ZipBundle2) bundle;
						zipBundle2.openEntry(prefixDir+"img", zipBundle2.imageFile(networkTitle, settings.imageFormat));
						ImageIO.write(image, settings.imageFormat, bundle.entryOutputStream());
						bundle.closeEntry();
					}

					if (needToHalt)
						return;

					// generate thumbnail
					setStatus("Generating thumbnail for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 40 / networkCount);
					//ZipBundle2 zipBundle2 = (ZipBundle2) bundle;
					zipBundle2.openEntry(prefixDir+"thm_img", ZipBundle2.thumbnailFile(networkTitle, settings.imageFormat));
					BufferedImage thumbnail = Thumbnails.createThumbnail(image, settings);
					ImageIO.write(thumbnail, settings.imageFormat, zipBundle2.entryOutputStream());	
					bundle.closeEntry();
					
					if (needToHalt)
						return;

				} // end for
				
			} // end generateImages()

			private void addPubData2Zip(String prefixDir)throws Exception {
				// export publication info
				setStatus("Exporting Publication info ... ");
				setPercentCompleted(90);
				ZipBundle2 zipBundle2 = (ZipBundle2) bundle;

				if (settings.SupplementURL != null){
					zipBundle2.putEntry(settings.SupplementURL, prefixDir+"supplement_url.txt");					
				}
				if (settings.SupplementMaterial != null){
					zipBundle2.putEntry(settings.SupplementMaterial, prefixDir+"supplement_material.doc");					
				}
				if (settings.PublicationURL != null){
					zipBundle2.putEntry(settings.PublicationURL, prefixDir+"publication_url.txt");					
				}
				setPercentCompleted(95);
				if (settings.CoverImageFile != null){
					String ext = Utils.getExtension(settings.CoverImageFile);
					zipBundle2.putEntry(settings.CoverImageFile, prefixDir+"coverImage."+ext);					
				}
				if (settings.PDFFile != null){
					zipBundle2.putEntry(settings.PDFFile, prefixDir+"myPaper.pdf");					
				}

				setPercentCompleted(98);
				
				if (settings.LegendsDir != null){
					File[] files = settings.LegendsDir.listFiles();
										
					for (int i=0; i<files.length; i++){
						// assume there is no nested directory in the legend directory
						if (files[i].isDirectory()){
							continue;
						}
						zipBundle2.putEntry(files[i], prefixDir+"legend/"+ files[i].getName());
					}	
				}
			}
			
			/*
			private List<File> getFilesFromDir(File pDir, List<File> fileList){
				for (File file: pDir.listFiles()){
					if (file.isDirectory()){
						getFilesFromDir(file, fileList);
					}
					else {
						fileList.add(file);
					}
				}
				return fileList;
			}
			*/
			
			private void generateIndexHTML(List<String> networkIDs) throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing HTML pages");
				setPercentCompleted(90);
				
				List<List<String>> pages = breakIntoPages(networkIDs);
				for (List<String> page : pages)
				{
					int pageIndex = pages.indexOf(page);
					bundle.openEntry(Bundle.indexHTMLFile(pageIndex));
					PrintWriter writer = new PrintWriter(bundle.entryWriter());
					writer.println("<html>");
					writer.println("<body>");

					StringBuffer navLinks = new StringBuffer();
					if (pages.size() != 1)
					{
						boolean isFirstPage = (pageIndex == 0);
						boolean isLastPage = (pageIndex == pages.size() - 1);

						navLinks.append("<p align=right>");
						if (!isFirstPage)
							navLinks.append("<a href=\"" + Bundle.indexHTMLFile(pageIndex - 1) + "\">");
						navLinks.append("&lt; Previous Page");
						if (!isFirstPage)
							navLinks.append("</a>");
						navLinks.append(" | ");
						if (!isLastPage)
							navLinks.append("<a href=\"" + Bundle.indexHTMLFile(pageIndex + 1) + "\">");
						navLinks.append("Next Page &gt;");
						if (!isLastPage)
							navLinks.append("</a>");
						navLinks.append("</p>");
					}
					writer.println("<table width=\"100%\"><tr>");
					writer.println("<td><a href=\"" + Bundle.sessionFile() + "\">Cytoscape Session File</a></td>");
					if (pages.size() != 1)
						writer.println("<td align=center>Page " + (pageIndex + 1) + " of " + pages.size() + "</td>");
					writer.println("<td>" + navLinks.toString() + "</td>");
					writer.println("</tr></table><hr>");

					List<List<String>> groups = breakPageIntoGroups(page);
					for (List<String> group : groups)
					{
						if (settings.sortImages == SessionExporterSettings.SORT_IMAGES_BY_VISUAL_STYLE)
						{
							if (group.size() != 0)
							{
								if (groups.indexOf(group) != 0)
									writer.println("<hr>");
								writer.print("<p align=center><b>Visual style:</b> ");
								VisualStyle vizStyle = Cytoscape.getNetworkView(group.get(0)).getVisualStyle();
								if (vizStyle == null)
									writer.print("none");
								else
									writer.print(vizStyle.getName());
								writer.println("</p>");
							}
						}

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
									writer.print("<a href=\"" + Bundle.networkHTMLFile(networkTitle) + "\">");
								writer.print("<img border=0 src=\"" + Bundle.thumbnailFile(networkTitle, settings.imageFormat) + "\">");
								if (hasImageFile)
									writer.print("</a>");

								writer.print("<font size=-1>");
								writer.print("<br>" + networkTitle + " <br/>(");
								writer.print("<a href=\"" + Bundle.sifFile(networkTitle) + "\">sif</a>");
								
								if (hasImageFile)
									writer.print(" | <a href=\"" + Bundle.networkHTMLFile(networkTitle) + "\">image</a>");
								if (hasLegendFile)
									writer.print(" | <a href=\"" + Bundle.legendFile(networkTitle, settings.imageFormat) + "\">legend</a>");
					
								writer.print(")</font>");
								writer.println("</td>");
							} // end for (String networkID : row)
							writer.println("</tr>");
						} // end for (List<String> row : rows)
						writer.println("</table>");
					} // for (List<String> group : groups)
					
					writer.println("<hr>");
					writer.println(navLinks.toString());
					writer.println("</body>");
					writer.println("</html>");
					bundle.closeEntry();
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
