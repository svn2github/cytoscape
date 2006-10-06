#!/usr/bin/perl -w

use MultiOrganismSIF;
use EdgeMapper;
use Publication;


my $pub = Publication->new("Suthram2005_Nature", 
			  "/opt/www/htdocs/search/data",
			  sub{ return shift;},
			  EdgeMapper->new());

print $pub->print();
