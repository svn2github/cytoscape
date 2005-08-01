package utils;

import java.io.*;
import java.net.*;
import java.util.*;
import corejava.*;

/**
 * Class <code>MyUtils</code>
 * 
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9978 (Fri Nov 07 05:56:26 PST 2003)
 */
public class MyUtils {
	protected static boolean noExit = false;

	protected static MyUtils myUtils = new MyUtils();

	public static void SetNoExit(boolean noe) {
		noExit = noe;
	}

	public static void Exit(int signal) {
		if (noExit)
			return;
		try {
			System.exit(signal);
		} catch (Exception e) {
		}
		;
	}

	public static boolean IsURL(String file) {
		String flc = file.trim().toLowerCase();
		return flc.startsWith("http://") || flc.startsWith("ftp://")
				|| flc.startsWith("file://") || flc.startsWith("jar://");
	}

	public static InputStream OpenFile(String file) throws Exception {
		if (IsURL(file))
			return OpenURL(file);

		InputStream dis = null;
		try {
			try {
				dis = OpenFileFromJar(file);
				// if ( dis != null ) return dis;
			} catch (Exception e) {
				try {
					URL getURL = new URL(file);
					URLConnection urlCon = getURL.openConnection();
					dis = urlCon.getInputStream();
				} catch (Exception ee) {
					dis = new java.io.FileInputStream(file);
				}
			}
		} catch (Exception e) {
			dis = null;
			throw (e);
		}
		if (file.endsWith(".gz") || file.endsWith(".GZ"))
			dis = new java.util.zip.GZIPInputStream(dis);
		// else if ( file.endsWith( ".bz2" ) || file.endsWith( ".BZ2" ) )
		// dis = new org.apache.excalibur.bzip2.CBZip2InputStream( dis );
		return dis;
	}

	public static InputStream OpenURL(String file) throws Exception {
		InputStream dis = null;
		String flc = file.trim().toLowerCase();
		try {
			if (flc.startsWith("jar://")) {
				file = file.substring("jar://".length());
				dis = OpenFileFromJar(file);
			} else if (flc.startsWith("http://") || flc.startsWith("file://")) {
				file = file.substring("http://".length());
				URL getURL = new URL(file);
				URLConnection urlCon = getURL.openConnection();
				dis = urlCon.getInputStream();
			} else if (flc.startsWith("ftp://")) {
				file = file.substring("ftp://".length());
				URL getURL = new URL(file);
				URLConnection urlCon = getURL.openConnection();
				dis = urlCon.getInputStream();
			} else {
				dis = new java.io.FileInputStream(file);
			}
		} catch (Exception e) {
			dis = null;
			throw (e);
		}
		if (file.endsWith(".gz") || file.endsWith(".GZ"))
			dis = new java.util.zip.GZIPInputStream(dis);
		// else if ( file.endsWith( ".bz2" ) || file.endsWith( ".BZ2" ) )
		// dis = new org.apache.excalibur.bzip2.CBZip2InputStream( dis );
		return dis;
	}

	public static InputStream OpenFileFromJar(String fname) throws Exception {
		InputStream is = myUtils.getClass().getResourceAsStream("/" + fname);
		BufferedInputStream bis = new BufferedInputStream(is);
		if (bis.available() <= 0)
			return null;
		return bis;
	}

	public static Vector ReadFileLines(String fname, boolean skipBlank)
			throws Exception {
		return ReadLines(OpenFile(fname), skipBlank);
	}

	public static Vector ReadFileLines(String fname) throws Exception {
		return ReadFileLines(fname, false);
	}

	public static Vector ReadLines(InputStream is) throws Exception {
		return ReadLines(is, false);
	}

	public static Vector ReadLines(InputStream is, boolean skipBlank)
			throws Exception {
		DataInputStream dis = new DataInputStream(is);
		if (dis == null)
			return null;
		String str = null;
		Vector out = new Vector();
		while ((str = dis.readLine()) != null) {
			if ("".equals(str) || str.startsWith("#") || str.startsWith("//"))
				continue;
			out.addElement(str);
		}
		return out;
	}

	public static String ReadFile(String fname) throws Exception {
		DataInputStream dis = new DataInputStream(OpenFile(fname));
		StringBuffer out = new StringBuffer();
		if (dis == null)
			return null;
		String str = null;
		while ((str = dis.readLine()) != null)
			out.append(str + "\n");
		return out.toString();
	}

	public static Vector ReadFileTokens(String fname, String toks)
			throws Exception {
		return ReadTokens(OpenFile(fname), toks);
	}

	public static Vector ReadTokens(InputStream is, String toks)
			throws Exception {
		Enumeration e = ReadLines(is).elements();
		Vector v = new Vector();
		while (e.hasMoreElements()) {
			String s[] = Tokenize((String) e.nextElement(), toks);
			for (int i = 0, size = s.length; i < size; i++)
				v.addElement(s[i]);
		}
		return v;
	}

	public static String[] Tokenize(String str, String tok) {
		StringTokenizer t = new StringTokenizer(str, tok);
		String[] out = new String[t.countTokens()];
		int i = 0;
		while (t.hasMoreTokens())
			out[i++] = t.nextToken();
		return out;
	}

	public static String Join(Object obj[], String tok) {
		return Join(obj, tok, 0, obj.length - 1);
	}

	public static String Join(Object obj[], String tok, int from, int to) {
		String out = "";
		for (int i = from, s = to; i < s; i++)
			out += obj[i] + tok;
		return out + obj[obj.length - 1];
	}

	public static Vector ArrayToVector(Object arr[]) {
		Vector out = new Vector(arr.length);
		for (int i = 0, size = arr.length; i < size; i++)
			out.addElement(arr[i]);
		return out;
	}

	public static OutputStream OpenOutputFile(String fname) throws IOException {
		return OpenOutputFile(fname, false);
	}

	public static OutputStream OpenOutputFile(String fname, boolean append)
			throws IOException {
		try {
			File f = new File(fname);
			if (!f.exists())
				(new File(f.getParent())).mkdirs();
		} catch (Exception e) {
		}
		;
		OutputStream os = new FileOutputStream(fname, append);
		if (fname.endsWith(".gz") || fname.endsWith(".GZ"))
			os = new java.util.zip.GZIPOutputStream(os);
		// else if ( fname.endsWith( ".bz2" ) || fname.endsWith( ".BZ2" ) )
		// os = new org.apache.excalibur.bzip2.CBZip2OutputStream( os );
		return new BufferedOutputStream(os);
	}

	public synchronized static void SaveObject(Object obj, String fname) {
		try {
			/*
			 * if ( fname.indexOf( ".xml" ) > 0 || fname.indexOf( ".XML" ) > 0 ) {
			 * SaveObjectXML( obj, fname ); return; }
			 */
			ObjectOutputStream out = new ObjectOutputStream(
					OpenOutputFile(fname));
			out.writeObject(obj);
			out.flush();
			out.close();
		} catch (Exception e) {
		}
		;
	}

	public static byte[] SaveObject(Object obj, boolean gzip) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			java.util.zip.GZIPOutputStream gos = gzip ? new java.util.zip.GZIPOutputStream(
					bos)
					: null;
			ObjectOutputStream out = gzip ? new ObjectOutputStream(gos)
					: new ObjectOutputStream(bos);
			out.writeObject(obj);
			bos.flush();
			if (gos != null) {
				gos.finish();
				gos.flush();
			}
			out.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	public static File SaveBytes(byte bytes[], String fname) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(
					OpenOutputFile(fname));
			os.write(bytes);
			os.flush();
			return new File(fname);
		} catch (Exception e) {
			;
		}
		return null;
	}

	public synchronized static void SaveObjectAsString(Object obj, String fname) {
		try {
			PrintStream out = new PrintStream(OpenOutputFile(fname));
			out.print(obj.toString());
			out.flush();
		} catch (Exception e) {
		}
		;
	}

	public static int GetSerializedSize(Object obj) {
		return GetSerializedSize(obj, false);
	}

	public static int GetSerializedSize(Object obj, boolean gzip) {
		byte arr[] = SaveObject(obj, gzip);
		return arr != null ? arr.length : 0;
	}

	public static Object ReadObject(byte bytes[]) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new java.util.zip.GZIPInputStream(new BufferedInputStream(
							new ByteArrayInputStream(bytes))));
			return in.readObject();
		} catch (Exception e1) {
			/*
			 * try { ObjectInputStream in = new ObjectInputStream( new
			 * org.apache.excalibur.bzip2.CBZip2InputStream( new
			 * ByteArrayInputStream( bytes ) ) ); Object out = in.readObject();
			 * return out; } catch( Exception e ) {
			 */
			e1.printStackTrace();
			try {
				ObjectInputStream in = new ObjectInputStream(
						new BufferedInputStream(new ByteArrayInputStream(bytes)));
				return in.readObject();
			} catch (Exception ee) {
				ee.printStackTrace();
				return null;
			}
			// }
		}
	}

	public static Object ReadObject(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(OpenFile(fileName));
			Object out = in.readObject();
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object DeepCopy(Object o) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(
					new BufferedOutputStream(b));
			out.writeObject(o);
			out.flush();
			byte bytes[] = b.toByteArray();
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream in = new ObjectInputStream(
					new BufferedInputStream(bi));
			return in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void PrintStackTrace(PrintStream out) {
		try {
			throw new Exception("Stack Trace:");
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}

	public static void PrintStackTrace() {
		PrintStackTrace(System.out);
	}

	public static String ReplaceSubstring(String input, String toReplace,
			String replaceWith) {
		int ind = input.indexOf(toReplace), len = toReplace.length(), last = 0;
		if (ind < 0)
			return input;
		StringBuffer out = new StringBuffer();
		while (ind >= 0) {
			out.append(input.substring(last, ind)).append(replaceWith);
			last = ind + len;
			ind = input.indexOf(toReplace, last);
		}
		out.append(input.substring(last));
		return out.toString();
	}

	public static String GetPropsString(ResourceBundle props, String key) {
		String out = null;
		try {
			out = props.getString(key);
		} catch (Exception e) {
			out = null;
		}
		return out;
	}

	public static boolean IsNullString(String s) {
		return s == null || "".equals(s) || "''".equals(s);
	}

	public static String GetLocalHostName() {
		String localHostName = "Unknown";
		try {
			InetAddress lhost = java.net.InetAddress.getLocalHost();
			localHostName = lhost.getHostName();
			if ("localhost".equals(localHostName))
				localHostName = lhost.getHostAddress();
		} catch (Exception e) {
			localHostName = "Unknown";
		}
		return localHostName;
	}

	public static String Spaces(int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++)
			sb.append(" ");
		return sb.toString();
	}

	public static void Printf(String fmt, String in) {
		Format.print(System.out, fmt, in);
	}

	public static String SPrintf(String fmt, String in) {
		return (new Format(fmt)).form(in);
	}

	public static void Printf(String fmt, String in[]) {
		for (int i = 0; i < in.length; i++)
			Format.print(System.out, fmt, in[i]);
	}

	public static void Print(String in[]) {
		for (int i = 0; i < in.length; i++)
			System.out.print(in[i] + " ");
		System.out.println();
	}

	public static String ReadKeyboardInput() {
		try {
			String line;
			BufferedReader stdin = new BufferedReader(new InputStreamReader(
					System.in));
			while ((line = stdin.readLine()) == null) {
			}
			;
			return line;
		} catch (Exception e) {
			return null;
		}
	}

	public static char ReadKey() {
		char key = 0;
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(
					System.in));
			if (System.in.available() > 0)
				key = (char) System.in.read();
		} catch (Exception e) {
			key = 0;
		}
		return key;
	}

	public static String GetTempFolder() {
		// return flybase.Native.tempFolder(); // This is what the readseq
		// library uses.
		String osname = System.getProperty("os.name").toLowerCase();
		File ff = null;
		if (osname.startsWith("windows")
				|| System.getProperty("file.separator").equals("\\")
				|| osname.startsWith("os/2")) {
			ff = new File("\\tmp", "");
			if (ff == null || !ff.exists())
				ff = new File("\\temp", "");
			if (ff == null || !ff.exists())
				ff = new File("c:\\temp", "");
		} else
			ff = new File("/tmp", ""); // assume UNIX
		String fold = null;
		if (ff != null && ff.exists() && ff.isDirectory())
			fold = ff.getPath();
		if (fold == null || fold.length() == 0)
			fold = System.getProperty("user.dir", "") + "/";
		return fold;
	}

	public static void DeleteTempFiles(String prefix, String suffix) {
		String tempFolder = GetTempFolder();
		String glob = prefix + "*" + suffix;
		String[] listing = (new File(tempFolder)).list();
		for (int i = 0, s = listing.length; i < s; i++) {
			if (ViolinStrings.Strings.isLike(listing[i], glob)) {
				(new File(tempFolder, listing[i])).deleteOnExit();
			}
		}
	}

	public static void RemoveDirRecursive(String dirName) {
		File f = new File(dirName);
		f.deleteOnExit();
		if (!f.exists() || !f.isDirectory())
			return;
		String[] listing = (new File(dirName)).list();
		for (int i = 0, s = listing.length; i < s; i++)
			(new File(dirName, listing[i])).deleteOnExit();
	}

	public static void Sleep(int secs) {
		try {
			Thread.sleep(1000 * secs);
		} catch (Exception e) {
		}
		;
	}

	public static void JoinMaps(Map into, Map other) {
		if (other == null)
			return;
		Iterator it = other.keySet().iterator();
		while (it.hasNext()) {
			Object prot = it.next();
			into.put(prot, other.get(prot));
		}
	}

	public static Properties readProperties(String properties) {
		if (properties == null)
			return null;
		try {
			InputStream dis = OpenFile(properties);
			if (dis == null || dis.available() <= 0)
				throw new Exception("");
			Properties props = new Properties();
			props.load(dis);
			return props;
		} catch (Exception e) {
			System.err.println(e + ": Could not load properties file "
					+ properties);
		}
		return null;
	}
}
