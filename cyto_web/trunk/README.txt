README.txt
----------------

This directory contains all files for cytoscape.org.

I.  Deploying to cytoscape.org
------------------------------

To deploy web site changes to cytoscape.org:

1.  You need the cytoscape.org password.  If you don't have it, please  talk to Trey or Ethan.

2.  Type:  deploy.sh
 
    This does two things:
    a.  runs ant config_prod, so that everything is set up for production.
    b.  runs rsync to incrementally and securely update cytoscape.org
