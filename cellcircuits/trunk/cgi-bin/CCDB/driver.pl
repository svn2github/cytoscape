#! /usr/bin/perl
use strict;
use warnings;

#printf STDERR "Before uses: %s\n", localtime(time);

#
# Set PERL5LIB environment variable instead of 
# hardcoding this path.
#
# use lib '/var/www/cgi-bin/search/v1.1';
#
# In Apache httpd.conf:
# SetEnvIf Request_URI "search" PERL5LIB /var/www/cgi-bin/search/v1.1
#

use CCDB::Driver;
use CCDB::Query;
use CCDB::Model;
use CCDB::HtmlRoutines;
use CGI qw(:standard);
#printf STDERR "After uses: %s\n", localtime(time);

print STDERR "### Calling process_command_line\n";
my ($query, $test_case_file, $test_case_num, $DEBUG) = process_command_line();

my $gq           = {};
my $tnq          = {};
my $taq          = {};
my $modelIdQuery = {};
my $modelLikeQuery = {};
my $publications = {};
my $species      = {};
my $sort_method  = 'optionA_by_number_of_query_terms_matching_model';
my $page         = 1;
my $pval_thresh  = 1e-4;
my $error_msg    = {};

if($test_case_file) {
    ($query,  #str
     $species #ref-to-hash
     ) = read_test_case_file($test_case_file,$test_case_num,$DEBUG);
}

print STDERR "### Calling Driver::process_query\n";

my @retval = CCDB::Driver::process_query($query);
if(scalar(@retval) == 0) {
    $error_msg->{'uneven-number-of-double-quotes'}++;
    CCDB::HtmlRoutines::outputErrorPage($query, 
    					$publications,
    					$species,
    					$sort_method,
    					$pval_thresh,
    					$error_msg);
    exit;
}
($gq, $tnq, $taq, $modelIdQuery, $modelLikeQuery) = @retval;

print STDERR "### Calling Driver::search\n";
CCDB::Driver::search($query, $gq, $tnq, $taq, $modelIdQuery, $modelLikeQuery,
		     $publications,
		     $species,
		     $sort_method,
		     $pval_thresh,
		     $page);

print STDERR "### Done\n";

sub process_command_line
{
    my $usg=<<USG;
  usage: $0 <-q 'query' | -t test-case-file case-num>

example: $0 -q 'GCN4 rad* GO:0357891 "DNA binding"' -debug
example: $0 -t test-cases-for-driver.tab 1 -debug

USG

    die $usg if(@ARGV < 2);
    
    my $DEBUG = 0;
    my $opt = '';
    my $query = '';
    my $test_case_file = '';
    my $test_case_num = '';
    while(@ARGV)
    {
	my $arg = shift @ARGV;
	if   ($opt eq 'query') { $query = $arg; $opt = ''; next; }
	elsif($opt eq 'tfile') { 
	    $test_case_file = $arg; 
	    $test_case_num  = shift;
	    $opt = ''; next;
	}
	if($arg eq '-q')       { $opt = 'query'; }
	elsif($arg eq '-t')    { $opt = 'tfile'; }
	elsif($arg eq '-debug'){ $DEBUG = 1; }
    }
    
    print STDERR "query          = $query\n" if($query);
    print STDERR "test_case_file = $test_case_file\n" if($test_case_file);
    print STDERR "test_case_num  = $test_case_num\n" if($test_case_num);
    #print "debug          = $DEBUG\n" if($DEBUG);

    return ($query,
	    $test_case_file,
	    $test_case_num,
	    $DEBUG);
}

sub read_test_case_file
{
    my ($file,$num,$DEBUG) = @_;

    my $test_case = "";
    my $original_query = "";
    my $species = {};
    open(FILE, "< $file") or die "Cannot open $file: $!\n";
    while(<FILE>){
	chomp;
	next if(/^\#\#/);
	#print "test_case = $test_case: $_\n";
	if(/^\#(\d+)/){
	    if($1 eq $num){ $test_case = $num; next; }
	}
	unless($test_case eq ""){
	    last if(/^\#\d+/);
	    print "$_\n" if($DEBUG == 1);
	    my @l = split(/\t/);
	    if($l[0] =~ /^QUER/){ $original_query = $l[1]; }
	    if($l[0] =~ /^SPEC/){
		foreach my $t (@l[1..$#l]){
		    $t =~ s/^\'//;
		    $t =~ s/\'$//;
		    $species->{$t}++;
		}
	    }
	}
    }
    close(FILE);
    
    #my @species_list = sort keys %{ $species };

    return ($original_query,$species);
}
