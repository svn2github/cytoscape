README.txt
----------------

This directory contains all files for cytoscape.org.

I.  Deploying to cytoscape.org
------------------------------

To deploy web site changes to cytoscape.org:

1.  You need the cytoscape.org password.  If you don't have it, please  talk to Trey or Ethan.

2A.  On Mac OS X or Linux, type:  deploy.sh
 
    This does two things:
    a.  runs ant config_prod, so that everything is set up for production.
    b.  runs rsync to incrementally and securely update cytoscape.org

    rsync is much more efficent and secure than FTP.  So this is the preferred
    way to do deployments.

2B.  On Windows, type:

    ant deploy -Dpassword=[value]

    This does two things:
    a.  runs ant config_prod, so that everything is set up for production.
    b.  copies files over via the ant FTP task.  
