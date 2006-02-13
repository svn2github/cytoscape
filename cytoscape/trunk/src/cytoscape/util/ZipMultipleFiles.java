package cytoscape.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip multiple files into one.
 * 
 * The created zip files can be decompressed by other utilities. 
 * 
 *  The original code was written by Mr. Masanori Kouno
 *  (http://www4.ocn.ne.jp/~mark44/TIPS/Java/ZipOutputStream/zip.htm)
 *
 * @author kono
 * 
 **/

public class ZipMultipleFiles {
	private String zipFileName;
	private String[] files;
	private int fileCount;
	private File tempDir;
	private String sessionDirName;

	private final String FS = System.getProperty("file.separator");

	public ZipMultipleFiles(String zipFile, String[] fileList) {
		this.zipFileName = zipFile;
		this.fileCount = fileList.length;
		this.files = new String[fileCount];
		System.arraycopy(fileList, 0, files, 0, fileCount);
	}

	public ZipMultipleFiles(String zipFile) {
		this.zipFileName = zipFile;
	}

	public ZipMultipleFiles(String zipFile, String[] fileList, File tempDir) {
		this.zipFileName = zipFile;
		this.fileCount = fileList.length;
		this.files = new String[fileCount];
		this.tempDir = tempDir;

		System.arraycopy(fileList, 0, files, 0, fileCount);
	}

	public ZipMultipleFiles(String zipFile, String[] fileList, String tempDir) {
		this.zipFileName = zipFile;
		this.fileCount = fileList.length;
		this.files = new String[fileCount];
		this.sessionDirName = tempDir;

		System.arraycopy(fileList, 0, files, 0, fileCount);
	}

	protected void clean() {

		for (int i = 0; i < fileCount; i++) {
			File tempFile = new File(files[i]);
			System.out.println("delete file is " + tempFile.delete());
		}

		// Delete the temp directory
	}

	// Compress the files into one zipped file.
	public void compress() {

		ByteArrayOutputStream baos = null;
		ZipOutputStream zos = null;
		ZipEntry zent = null;

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {

			baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(baos);

			ArrayList al = new ArrayList();

			for (int i = 0; i < fileCount; i++) {

				byte[] buf = getFileBytes(files[i]);
				zent = new ZipEntry(sessionDirName + FS + files[i]);
				al.add(zent);
				zos.putNextEntry(zent);

				zos.write(buf, 0, buf.length);
				zos.closeEntry();
			}

			try {
				zos.close();
			} catch (Exception e) {
			}

			try {
				baos.close();
			} catch (Exception e) {
			}

			baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(baos);

			for (int i = 0; i < fileCount; i++) {
				byte[] buf = getFileBytes(files[i]);

				zent = (ZipEntry) al.get(i);
				zos.putNextEntry(zent);
				zos.write(buf, 0, buf.length);
				zos.closeEntry();
			}

			zos.finish();

			byte[] bufResult = baos.toByteArray();

			fos = new FileOutputStream(zipFileName);
			bos = new BufferedOutputStream(fos);
			bos.write(bufResult, 0, bufResult.length);

		} catch (IOException e) {
			System.err.println(e);
		} finally {

			try {
				zos.close();
			} catch (Exception e) {
			}

			try {
				baos.close();
			} catch (Exception e) {
			}

			try {
				bos.close();
			} catch (Exception e) {
			}

			try {
				fos.close();
			} catch (Exception e) {
			}
		}

		clean();
	}

	private byte[] getFileBytes(String filename) {
		File file = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			file = new File(filename);

			int len = (int) file.length();
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis, len);
			byte buf[] = new byte[len];
			bis.read(buf, 0, len);
			return buf;
		} catch (IOException e) {

		} finally {

			try {
				if (bis != null) {
					bis.close();
				}
			} catch (Exception e) {
			}

			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
			}

		}

		return null;
	}
}