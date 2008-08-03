#!/usr/bin/perl -w
use strict;
use warnings;

use lib '.';

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


# if 1, debug 
if (0) {
	param('search_query', 'gcn* gal4 GO:0003677 "DNA binding"');
	#param('publication', '35');
	#param('species', 'all');
	#param('search_condition', 'default');
	
	#print header();
	#print start_html('Passed parameter from browser');
	#print h1('Passed parameter from browser\n');
	#print end_html,"\n";
	#exit;
}
print "Content-type: text/html\n\n";

if(param("search_query"))
{
	my @pubs;
	my @orgs;
	my $sort_method;
	my $pval_thresh;

    my $query = param("search_query");
# if 1, debug 
if (0) {

    $query =  "gcn* gal4 GO:0003677 \"DNA binding\""; # param("search_query");
#print $query,"<br>\n";
	my @qs = ('search_query=gcn%2A%20gal4%20GO%3A0003677%20%22DNA%20binding%22','search_query_button=Search','species=Saccharomyces%20cerevisiae
species=Homo%20sapiens','species=Caenorhabditis%20elegans','species=Drosophila%20melanogaster','sort_method=optionA_by_number_of_query_terms_matching_model','pval_thresh=0.0001','publication=35','publication=36','results_page=1');
	$sort_method = "optionA_by_number_of_query_terms_matching_model";
	$pval_thresh = "0.0001";
	@pubs = ("1"); #,"36");
	@orgs = ("Saccharomyces cerevisiae","Homo sapiens","Caenorhabditis elegans","Drosophila melanogaster");
}
	if (param('search_condition') && param('search_condition') eq 'default') {
		# called from index.html page
		@pubs         =  (35); #pub_id = -1, will search all pubs, ('Begley2002_MCR'); 
		@orgs         = ('Saccharomyces cerevisiae');
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
	
    #my @qs = split(/;/, query_string()); #query_string is a CGI.pm method
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
