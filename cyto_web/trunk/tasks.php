<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape Commons: Task Framework</title> 
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css" />
	<link rel="shortcut icon" href="images/cyto.ico" />
</head>
<body bgcolor="#FFFFFF">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tr>
		<td width="10">
			&nbsp; 
		</td>
		<td valign="center">
			<h1>Cytoscape Commons: Task Framework</h1> 
		</td>
	</tr>
</table>
<? include "nav.php"; ?>
<div id="indent">
<h3>About the Task Framework:</h3>
<P>
    The task framework is a convenient framework for building, running
    and visually monitoring long-term tasks within Cytoscape.
    <P>
    <IMG SRC="images/customized_jtask.png"/>
    <BR>
    The main goal of the task package is to improve the perceived 
	performance of Cytoscape:
    <BLOCKQUOTE>
    Often the subjective speed of your application has little to do 
	with how quickly it actually executes its code. To the user, an
    application that starts up rapidly, repaints quickly, and provides
    continuous feedback feels "snappier" than an application that just
    "hangs up" while it churns through its work. [<A HREF="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/vbcon98/html/vbconoptimizingperceivedspeed.asp">1</A>].
    </BLOCKQUOTE>
    <P>
    By using the task framework, core developers and plugin developers
    can provide continuous feedback to the end-user, and we can all
    make Cytoscape a "snappier" application.

<h3>Status:  Open for Public Review</h3>
A first draft of the Task Framework is now complete and available for public review.  Once we receive feedback, we will migrate the task framework to the Cytoscape core.

<UL>
<LI><A HREF="http://cbio.mskcc.org/cytoscape/commons/task">Review Javadocs</A>:  Includes an overview page with sample code for
creating and running tasks.
<LI>Try demos:
<UL>
<LI>Check out code from cvs:  csplugins/common/task/.
<LI>ant sample0:  Illustrates bare bones JTask dialog box.  
<LI>ant sample1:  Illustrates customized JTask dialog box with popup delay.
<LI>ant sample2:  Illustrates exception handling.
</UL>
</UL>
Please direct all feedback to the cytostaff mailing list.
<P/>&nbsp;<P>
</div>
</div>
<? include "footer.php"; ?>
</body>
</html>
