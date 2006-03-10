/*
 File: ZipMultipleFiles.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
 - Agilent Technologies
 
 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.
 
 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute 
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute 
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute 
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import cytoscape.CytoscapeInit;

/**
 * Zip multiple files into one.
 * 
 * The created zip files can be decompressed by other utilities.
 * 
 * Special Thanks for Mr. Masanori Kouno
 * The original code was written by him.
 * (http://www4.ocn.ne.jp/~mark44/TIPS/Java/ZipOutputStream/zip.htm)
 * 
 * @author kono
 * 
 */

public class ZipMultipleFiles {
	private String zipFileName;
	private String[] files;
	private int fileCount;
	private String sessionDirName;

	private Writer[] targets;

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

	public ZipMultipleFiles() {
	}

	public ZipMultipleFiles(String zipFile, String[] fileList, File tempDir) {
		this.zipFileName = zipFile;
		this.fileCount = fileList.length;
		this.files = new String[fileCount];

		System.arraycopy(fileList, 0, files, 0, fileCount);
	}

	public ZipMultipleFiles(String zipFile, String[] fileList, String tempDir) {
		this.zipFileName = zipFile;
		this.fileCount = fileList.length;
		this.files = new String[fileCount];
		this.sessionDirName = tempDir;

		System.arraycopy(fileList, 0, files, 0, fileCount);
	}

	//
	// Now acceptiong multiple streams instead of file names.
	//
	public ZipMultipleFiles(String zipFile, Writer[] entryList, String rootDir) {
		this.zipFileName = zipFile;
		this.fileCount = entryList.length;
		this.targets = new Writer[fileCount];
		this.files = new String[fileCount];
		this.sessionDirName = rootDir;

		System.arraycopy(entryList, 0, targets, 0, fileCount);
	}

	protected void clean() {

		for (int i = 0; i < fileCount; i++) {
			File tempFile = new File(files[i]);

			tempFile.delete();
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

	public void readVizmap() throws IOException {

		BufferedInputStream bis = null;
		ZipInputStream zis = null;

		FileOutputStream fos = null;

		File currentVizmap = new File(CytoscapeInit
				.getVizmapPropertiesLocation());

		FileInputStream fis = null;
		ZipEntry zent = null;

		try {
			fis = new FileInputStream(zipFileName);
			bis = new BufferedInputStream(fis);
			zis = new ZipInputStream(bis);

			byte[] buf = new byte[1024];
			int len;

			while ((zent = zis.getNextEntry()) != null) {
				if (zent.getName().endsWith("vizmap.props")) {
					fos = new FileOutputStream(currentVizmap);
					while (-1 != (len = zis.read(buf, 0, buf.length))) {
						fos.write(buf, 0, len);
					}
					fos.close();
				}
			}

		} catch (IOException e) {
			System.err.println(e);
		} finally {

			try {
				zis.close();
			} catch (Exception e) {
			}

			try {
				bis.close();
			} catch (Exception e) {
			}

		}
	}

}