import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class GenerateBuildFile
{

  private final static String SRC_DIR = "src.dir";
  private final static String BUILD_DIR = "build.dir";
  private final static String TEMP_SRC_DIR = "temp.src.dir";
  private final static String CLASSES_DIR = "classes.dir";
  private final static String LITTLE_JARS_DIR = "little.jars.dir";

  public static void main(String[] args) throws IOException
  {
    PrintStream out = System.out;
    File sourceDir = new File(args[0]);
    File tempSourceDir = new File(args[1]);
    File classesDir = new File(args[2]);
    File littleJarsDir = new File(args[3]);
    String description = args[4];

    out.println("<?xml version=\"1.0\"?>");
    out.println();
    out.println("<!-- ==========================================================================");
    out.println();
    out.println("   Build file for " + description + ".");
    out.println("   This file is auto generated.  Do not modify this file directly.  Modify the");
    out.println("   code that generates this file instead.");
    out.println();
    out.println("=========================================================================== -->");
    out.println();
    out.println("<project name=\"" + description + "\" default=\"littlejars\">");
    out.println();

    String[] packageDirs = getAllDirsWithJavaFiles(sourceDir);
    writeProperties(out, sourceDir, tempSourceDir, classesDir, littleJarsDir);
    writePatternsets(out, packageDirs);
    writeJarTarget(out, packageDirs);
    writeLittleJarsTarget(out, packageDirs);
    Hashtable deps = new Hashtable();
    for (int i = 0; i < packageDirs.length; i++)
      deps.put(packageDirs[i],
               getPackageDeps(new File(sourceDir, packageDirs[i])));
    writeJarTargets(out, packageDirs, deps);
    out.println("</project>");
  }

  private static void writeProperties(PrintStream printStream,
                                      File sourceDir,
                                      File tempSourceDir,
                                      File classesDir,
                                      File littleJarsDir)
  {
    printStream.println
      ("  <property name=\"" + SRC_DIR + "\" value=\"" +
       sourceDir.getAbsolutePath() + "\"/>");
    printStream.println
      ("  <property name=\"" + TEMP_SRC_DIR + "\" value=\"" +
       tempSourceDir.getAbsolutePath() + "\"/>");
    printStream.println
      ("  <property name=\"" + CLASSES_DIR + "\" value=\"" +
       classesDir.getAbsolutePath() + "\"/>");
    printStream.println
      ("  <property name=\"" + LITTLE_JARS_DIR + "\" value=\"" +
       littleJarsDir.getAbsolutePath() + "\"/>");
    printStream.println();
  }

  private static void writePatternsets(PrintStream printStream,
                                       String[] packageDirs)
  {
    for (int i = 0; i < packageDirs.length; i++)
    {
      printStream.println("  <patternset id=\"source__" +
                          packageDirs[i].replace('/', '_') +
                          "\">");
      printStream.println("    <include name=\"" +
                          packageDirs[i] + "/*.java\"/>");
      printStream.println("  </patternset>");
      printStream.println();
    }
  }

  private static void writeLittleJarsTarget(PrintStream printStream,
                                            String[] packageDirs)
  {
    printStream.println("  <target name=\"littlejars\"");
    printStream.print("          depends=\"");
    for (int i = 0; i < packageDirs.length; i++)
    {
      if (i != 0)
        printStream.print("                   ");
      printStream.print(packageDirs[i].replace('/', '_') + ".zip");
      if (i != packageDirs.length - 1)
        printStream.println(",");
      
    }
    printStream.println("\"/>");
    printStream.println();
  }

  private static void writeJarTarget(PrintStream printStream,
                                     String[] packageDirs)
  {
    printStream.println("  <target name=\"nwo.zip\"");
    printStream.println("          depends=\"littlejars\">");
    printStream.println("    <mkdir dir=\"${" + LITTLE_JARS_DIR +
                        "}/all_classes\"/>");
    for (int i = 0; i < packageDirs.length; i++)
    {
      printStream.println("    <unzip src=\"${" + LITTLE_JARS_DIR +
                          "}/" + packageDirs[i].replace('/', '_') +
                          ".zip\"");
      printStream.println("           dest=\"${" + LITTLE_JARS_DIR +
                          "}/all_classes\"/>");
    }
    printStream.println("    <zip zipfile=\"${" + BUILD_DIR +
                        "}/nwo.zip\"");
    printStream.println("         basedir=\"${" + LITTLE_JARS_DIR +
                        "}/all_classes\"");
    printStream.println("         filesonly=\"true\"/>");
    printStream.println("    <delete dir=\"${" + LITTLE_JARS_DIR +
                        "}/all_classes\"/>");
    printStream.println("  </target>");
    printStream.println();
  }

  private static void writeJarTargets(PrintStream printStream,
                                      String[] packageDirs,
                                      Hashtable deps)
  {
    for (int i = 0; i < packageDirs.length; i++)
    {
      String[] packageDeps = (String[]) deps.get(packageDirs[i]);
      String pkgUnderscores = packageDirs[i].replace('/', '_');
      printStream.println("  <target name=\"" + pkgUnderscores + ".zip\"" +
                          ((packageDeps.length == 0) ? ">" : ""));
      if (packageDeps.length != 0)
      {
        printStream.print("          depends=\"");
        for (int j = 0; j < packageDeps.length; j++)
        {
          if (j != 0)
            printStream.print("                   ");
          printStream.print(packageDeps[j].replace('/', '_') + ".zip");
          if (j != packageDeps.length - 1)
            printStream.println(",");
        }
        printStream.println("\">");
      }
      printStream.println("    <copy todir=\"${" + TEMP_SRC_DIR +
                          "}/" + pkgUnderscores + "\">");
      printStream.println("      <fileset dir=\"${" + SRC_DIR + "}\">");
      printStream.println("        <patternset refid=\"source__" +
                          pkgUnderscores + "\"/>");
      printStream.println("      </fileset>");
      printStream.println("    </copy>");
      printStream.println("    <mkdir dir=\"${" + CLASSES_DIR +
                          "}/" + pkgUnderscores + "\"/>");
      printStream.println("    <javac destdir=\"${" + CLASSES_DIR +
                          "}/" + pkgUnderscores + "\"");
      printStream.println("           srcdir=\"${" + TEMP_SRC_DIR +
                          "}/" + pkgUnderscores + "\"");
      printStream.println("           includeAntRuntime=\"no\">");
      String[] allDeps = getAllPackageDependencies(packageDirs[i], deps);
      for (int j = 0; j < allDeps.length; j++)
        printStream.println("      <classpath path=\"${" + LITTLE_JARS_DIR +
                            "}/" + allDeps[j].replace('/', '_') +
                            ".zip\"/>");
      printStream.println("    </javac>");
      printStream.println("    <mkdir dir=\"${" + LITTLE_JARS_DIR + "}\"/>");
      printStream.println("    <zip zipfile=\"${" + LITTLE_JARS_DIR + "}/" +
                          pkgUnderscores + ".zip\"");
      printStream.println("         basedir=\"${" + CLASSES_DIR + "}/" +
                          pkgUnderscores + "\"");
      printStream.println("         filesonly=\"true\"/>");
      printStream.println("    <delete dir=\"${" + TEMP_SRC_DIR +
                          "}/" + pkgUnderscores + "\"/>");
      printStream.println("    <delete dir=\"${" + CLASSES_DIR +
                          "}/" + pkgUnderscores + "\"/>");
      printStream.println("  </target>");
      printStream.println();
    }
  }

  private static String[] getPackageDeps(File packageDir) throws IOException
  {
    File depsFile = new File(packageDir, "DEPENDENCIES");
    if (depsFile.exists())
    {
      Vector vec = new Vector();
      String depsFileContents = new String(readFileContents(depsFile), 0);
      StringTokenizer tokens = new StringTokenizer(depsFileContents, "\n\r");
      while (tokens.hasMoreTokens())
        vec.addElement(tokens.nextToken().trim().replace('.', '/'));
      String[] returnThis = new String[vec.size()];
      vec.copyInto(returnThis);
      return returnThis;
    }
    else
    {
      return new String[0];
    }
  }

  private final static byte[] s_buff = new byte[1024];

  private static byte[] readFileContents(File f) throws IOException
  {
    FileInputStream fin = null;
    try {
      fin = new FileInputStream(f);
      int length = 0;
      ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
      while ((length = fin.read(s_buff)) >= 0)
        outBuff.write(s_buff, 0, length);
      return outBuff.toByteArray(); }
    finally {
      try { fin.close(); }
      catch (Exception e) {} }
  }

  private static String[] getAllDirsWithJavaFiles(File parentDir)
  {
    Vector vec = new Vector();
    getAllDirsWithJavaFiles_helper(parentDir, "", vec);
    String[] returnThis = new String[vec.size()];
    vec.copyInto(returnThis);
    return returnThis;
  }

  private static void getAllDirsWithJavaFiles_helper(File parentDir,
                                                     String prefix,
                                                     Vector vec)
  {
    String[] fileNames = parentDir.list();
    for (int i = 0; i < fileNames.length; i++)
    {
      if (!fileNames[i].equals("CVS"))
      {
        File file = new File(parentDir, fileNames[i]);
//      if (!file.isDirectory() && fileNames[i].endsWith(".java")) {
        if (!file.isDirectory() && fileNames[i].equals("DEPENDENCIES")) {
          if (prefix.equals(""))
            throw new RuntimeException
              ("java source code not allowed to be in empty package - " +
               "source code found in directory " +
               parentDir.getAbsolutePath());
          vec.addElement(prefix.substring(0, prefix.length() - 1));
          break; }
      }
    }
    for (int i = 0; i < fileNames.length; i++)
    {
      if (!fileNames[i].equals("CVS"))
      {
        File file = new File(parentDir, fileNames[i]);
        if (file.isDirectory())
          getAllDirsWithJavaFiles_helper
            (file, prefix + fileNames[i] + "/", vec);
      }
    }
  }

  private static String[] getAllPackageDependencies(String packageDir,
                                                    Hashtable deps)
  {
    Hashtable allDeps = new Hashtable();
    Vector allDepsList = new Vector();
    String[] packageDeps = (String[]) deps.get(packageDir);
    for (int i = 0; i < packageDeps.length; i++)
      getAllPackageDependencies_helper(packageDeps[i], deps, allDeps,
                                       allDepsList);
    String[] returnThis = new String[allDepsList.size()];
    allDepsList.copyInto(returnThis);
    return returnThis;
  }

  private static void getAllPackageDependencies_helper(String packageDep,
                                                       Hashtable deps,
                                                       Hashtable allDeps,
                                                       Vector allDepsList)
  {
    if (allDeps.get(packageDep) == null)
    {
      allDeps.put(packageDep, packageDep);
      allDepsList.addElement(packageDep);
      String[] packageSubDeps = (String[]) deps.get(packageDep);
      if (packageSubDeps != null)
        for (int i = 0; i < packageSubDeps.length; i++)
          getAllPackageDependencies_helper(packageSubDeps[i], deps, allDeps,
                                           allDepsList);
    }
  }

}
