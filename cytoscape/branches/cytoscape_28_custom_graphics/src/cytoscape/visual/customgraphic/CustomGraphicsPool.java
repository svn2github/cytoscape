package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.visual.SubjectBase;

public class CustomGraphicsPool extends SubjectBase implements PropertyChangeListener {

	private final ExecutorService imageLoaderService;

	private final Map<String, CyCustomGraphics<?>> graphicsMap = new ConcurrentHashMap<String, CyCustomGraphics<?>>();

	// Null Object
	private static final CyCustomGraphics<?> NULL = new NullCustomGraphics();

	private static final String METADATA_FILE = "image_metadata.txt";

	private File imageHomeDirectory;

	public CustomGraphicsPool() {
		// For loading images in parallel.
		this.imageLoaderService = Executors.newCachedThreadPool();

		graphicsMap.put(NULL.getDisplayName(), NULL);

		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(
				Cytoscape.CYTOSCAPE_EXIT, this);

		restoreImages();
	}

	private void restoreImages() {
		final CompletionService<BufferedImage> cs = new ExecutorCompletionService<BufferedImage>(
				imageLoaderService);

		// User config directory
		this.imageHomeDirectory = new File(CytoscapeInit.getConfigDirectory(),
				"images");

		imageHomeDirectory.mkdir();

		if (this.imageHomeDirectory != null && imageHomeDirectory.isDirectory()) {
			final File[] imageFiles = imageHomeDirectory.listFiles();

			try {
				for (File file : imageFiles) {
					cs.submit(new LoadImageTask(file.toURI().toURL()));
					

				}

				for (File file : imageFiles) {
					final Future<BufferedImage> f = cs.take();
					final BufferedImage image = f.get();
					
					graphicsMap.put(file.toURI().toURL().toString(),
							new URLImageCustomGraphics(file.toURI().toURL().toString(), image));
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void addGraphics(String id, CyCustomGraphics<?> graphics) {
		graphicsMap.put(id, graphics);
		this.fireStateChanged();
	}

	public void removeGraphics(String id) {
		graphicsMap.remove(id);
	}

	public CyCustomGraphics<?> get(String id) {
		return graphicsMap.get(id);
	}

	public Collection<CyCustomGraphics<?>> getAll() {
		return graphicsMap.values();
	}

	public Collection<String> getNames() {
		return graphicsMap.keySet();
	}

	public CyCustomGraphics<?> getNullGraphics() {
		return NULL;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Persist images
		System.out.println("Saving images...");
		
		for (CyCustomGraphics<?> cg : this.getAll()) {
			if (cg == NULL || cg instanceof URLImageCustomGraphics == false)
				continue;

			final Image img = cg.getImage();
			if (img != null) {
				final int hash = cg.hashCode();
				String newFileName = Integer.toString(hash);
				try {
					imageLoaderService.submit(new SaveImageTask(imageHomeDirectory, newFileName, ImageUtil.toBufferedImage(img)));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		
		System.out.println("Saving finihsed");
	}

}
