package cytoscape.visual.customgraphic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

public class SaveImageTask implements Callable<String>{
	private final File imageHome;
	private String fileName;
	private final BufferedImage image;

	public SaveImageTask(final File imageHomeDirectory, String fileName, BufferedImage image) {
		this.imageHome = imageHomeDirectory;
		this.fileName = fileName;
		this.image = image;
	}

	@Override
	public String call() throws Exception {
		
		if (!fileName.endsWith(".png"))
			fileName += ".png";
		File file = new File(imageHome, fileName);

		try {
			file.createNewFile();
			ImageIO.write(image, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done: " + file.toString());
		return file.toString();
	}
}
