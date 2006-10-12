#!/usr/bin/perl -w

use MultiOrganismSIF;
use EdgeMapper;
use Publication;
use GeneNameMapper;


my $pub = Publication->new("Suthram2005_Nature", 
			  "/opt/www/htdocs/search/data",
			  GeneNameMapper->new(),
			  EdgeMapper->new());

print $pub->print();
