#!/usr/bin/perl -w
use strict;
use warnings;

use lib '.';
use CCDB::DB;
use CCDB::QueryInput;
use CCDB::Driver;
use CCDB::Query;
use CCDB::Model;
use CCDB::HtmlRoutines;

use CGI::Carp qw(fatalsToBrowser); 
use CGI qw(:standard);

my $publications = {};
my $species      = {};
my $sort_method  = '';
my $page         = 1; 	 
my $pval_thresh  = 1e-4; 
my $error_msg    = {};

print "Content-type: text/html\n\n";

if(param("search_query"))
{
	my @pubs;
	my @orgs;
	my $sort_method;
	my $pval_thresh;

	if ((param('search_condition') && param('search_condition') eq 'default')
	  || (param("search_query") =~ m/^MODELS_LIKE/i)) {
		# called from index.html page
		# i.e. search all the publications and all the species
		
		my $dbh = CCDB::DB::getDB();
		my $pubs_ref = getAllPublications($dbh);
		@pubs = @{$pubs_ref};

		my $orgs_ref = getAllSpecies($dbh);
		@orgs = @{$orgs_ref};
								
		$sort_method  = 'optionA_by_number_of_query_terms_matching_model'; # transfered from old index.html
		$pval_thresh  = '0.0001'; # transfered from old index.html
	}
	else {
		# called from advanced_search.php page
		@pubs         = param("publication");
		@orgs         = param("species");
		$sort_method  = param("sort_method");
		$pval_thresh  = param("pval_thresh");
	}

    my $query = param("search_query");
	
    my @qs = split(/;/, query_string()); #query_string is a CGI.pm method
    #printf "query_string: %s", join "<br>", @qs,"<br>\n";
	
	
    for my $sp  (@orgs) {
		$species->{$sp}++; #print "species->{$sp}<br>\n";
    }
    for my $pub (@pubs) {
		$publications->{$pub}++; #print "publications->{$pub}<br>\n";
    }
	
	#print "sort_method = $sort_method<br>\n";
	#print "pval_thresh = $pval_thresh<br>\n";
	
    my $queryInput = CCDB::Driver::process_query($query);
	
    appendHumanGeneIdentifiers($queryInput, $species);

    if(!defined($queryInput)) {
		$error_msg->{'uneven-number-of-double-quotes'}++;
		CCDB::HtmlRoutines::outputErrorPage($query, 
					    $publications,
					    $species,
					    $sort_method,
					    $pval_thresh,
					    $error_msg);

    }

    if(param("search_query_button"))
    {
		$page = 1;
    }
    elsif(param("page") && param("page") =~ /^\d+$/)
    {
		$page = param("page");
    }
    else
    {
		$page = 1;
    }
	
    if($pval_thresh > 1)     { $pval_thresh = 1; }
    elsif($pval_thresh <= 0) { $pval_thresh = 1; }

    CCDB::Driver::search($queryInput,
			 $publications,
			 $species,
			 $sort_method,
			 $pval_thresh,
			 $page);

}
else {
    $error_msg->{'no-query-notice'}++;
    CCDB::HtmlRoutines::outputErrorPage("", 
					$publications,
					$species,
					$sort_method,
					$pval_thresh,
					$error_msg);
}



# Workaround for the issue that names of human genes 
# in the GO database are identified with the suffix
# "_HUMAN"
#
# If "Homo sapiens" is selected as a species, then
# append _HUMAN to the end of all gene names.
sub appendHumanGeneIdentifiers
{
    my ($queryInput, $speciesSelected) = @_;

    if(exists($speciesSelected->{"Homo sapiens"}))
    {
		foreach my $gene (keys %{$queryInput->gene()})
		{
			my $human = $gene . "_HUMAN";
			if(!exists($queryInput->gene()->{$human}))
			{
				$queryInput->gene->{$human}++;
			}
		}
    }
}


sub getAllSpecies {
    my ($dbh) = @_;

	my $sth = $dbh->prepare("SELECT distinct species from network_file_info ");
	$sth->execute();

	my %distinct_species = ();
	
	my @all_orgs = ();
		
	while (my $ref = $sth->fetchrow_hashref()) {	
		my $species = $ref->{'species'}; 
		#$species = "saccharomyces cerevisiae,homo sapiens,homo sapiens";
		#print "species = $species<br>";
		my @species_items = split(/,/, $species);
		for my $item (@species_items) {
			$distinct_species{ucfirst($item)} = "A";
		}
	}

	while (my ($key, $value) = each(%distinct_species)) {
		push (@all_orgs, $key);
	}

	return \@all_orgs;
}


sub getAllPublications {
    my ($dbh) = @_;

	my $get_pubInfo_sth = $dbh->prepare("select publication_auto_id from publications where is_published = true");
	$get_pubInfo_sth->execute();
	
	my @all_pub_ids = ();
	while (my $ref = $get_pubInfo_sth->fetchrow_hashref()) {	
		my $pub_id = $ref->{'publication_auto_id'}; 
		#print "getAllPublications: pub_id = $pub_id<br>";
		push (@all_pub_ids, $pub_id);
	}

	return \@all_pub_ids;
}