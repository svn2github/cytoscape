<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content=
    "text/html; charset=ISO-8859-1">
    <title>
      Cytoscape Online Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
    "css/cytoscape.css">
    <link rel="shortcut icon" href="images/cyto.ico">
  </head>
  <body bgcolor="#FFFFFF">
    <table id="feature" border="0" cellpadding="0" cellspacing=
    "0" summary="">
      <tr>
        <td width="10">
           
        </td>
        <td valign="center">
          <h1>
            Cytoscape Online Tutorial
          </h1>
        </td>
      </tr>
    </table>
    <? include "nav.php"; ?>
    <? include "nav_tut.php"; ?>
    <div id="indent">

<center>
<h2>Important security information about running
Cytoscape via Java Web Start</h2>
</center>

When you click on a Java Web Start link, you are downloading the program and
running it on your own machine. This is somewhat similar to a Java applet
that runs on your local machine, with some important differences.<P>

Most importantly, Cytoscape differs from most web-deployed applications in
that we configure the application to run with <strong>FULL
PERMISSIONS</strong>. This is primarily because Cytoscape was written to run
from the command line and thus expects access to the local machine, like the
ability to open and save network files. This means
that running Cytoscape via Java Web Start has the same security implications
as downloading, installing, and running the application on your local
computer. If security is a concern for you, you should consider whether this
is what you want to do. By running Cytoscape you agree to the terms of the
license agreement, including the standard disclaimer regarding limitation of
liability. You can view a copy of the license <a
href="license.txt">here</a>.<P>

When you first click on a Web Start link, Java Web Start will provide a
warning message informing you of the security risks before starting the
application.<P>

Click "Start" if you want to continue, or "Exit" to abort. If you run the
application by clicking "Start", Java Web Start will not present this dialog
on future requests to run the same application, unless you remove it from
your local archive (see below).<P>

Cytoscape allows externally developed plugin modules to be loaded by a user once
Cytoscape has started. These plugins inherit all of the permissions
granted to the core platform, including the ability to read and write from/to
the local disk. Each plugin module has its own license agreement. The
Cytoscape team cannot under any circumstances be responsible for or make any
guarantees regarding the safety or functionality of any plugin modules that
are not distributed with the core platform.<P>

Java Web Start will usually store the application on your local machine (in a
Web Start archive) so that it can be run later without having to download the
application again. You can manage your downloaded applications by running the
Java Web Start executable itself; this is usually called <code>javaws</code>
and should be in your Java or Java Web Start application folder. Java Web
Start will also automatically check for and download any updates to the
web-available Cytoscape application. Cytoscape Web Start applications may not
work properly if a connection to the internet is not available.<P>

In the course of a normal Cytoscape session, the application may save certain
files to your local disk for the purposes of saving configuration information.
In particular, Cytoscape may attempt to place a <code>vizmap.props</code> file
in your home directory to save visual mapping specifications. Such files may
be written without prompting the user. These files may be safely deleted
without affecting the ability of Cytoscape to function properly.<P>

    </div>
    <? include "footer.php"; ?>
  </body>
</html>
