import java.io.File;
import java.io.FilenameFilter;

/* Automatically generates Cytoscape manifest file (writes it to System.out).
 * Manifest File is automatically based on all JAR Files in lib directory.
 * Lib directory name is defined in first parameter passwd to main() method. */
public class ManifestCreator
{

  public static void main(String[] args)
  {
    if (!(args.length > 0 && args.length <= 2))
      throw new RuntimeException("must pass 1 or 2 parameters to program");
    String libDirName = args[0];
    String manifestJarRelativePath = args.length > 1 ? args[1] : "lib/";
    // We use print() instead of println() because we want newlines to be same
    // regardless of which system the manifest (and jar file) are built on.
    System.out.print("Main-Class: cytoscape.CyMain\n");
    System.out.print("Class-Path: ");
    //  Get All JAR Files in lib directory.
    File libDir = new File(libDirName);
    FilenameFilter jarFilter =
      new FilenameFilter() {
        /* Accepts Files Ending in .jar extension.
         * @param dir Directory.
         * @param name File Name.
         * @return Accept or Reject Flag
         */
        public boolean accept (File dir, String name)
        {
          if (name.endsWith(".jar")) { return true; }
          else { return false; }
        } };
    File jars[] = libDir.listFiles(jarFilter);
    for (int i = 0; i < jars.length; i++) {
      String name = jars[i].getName();
      System.out.print(manifestJarRelativePath + name + " "); }
    System.out.print("\n");
  }

}
