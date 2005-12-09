#!/usr/bin/perl

##########################################################################################################
# Authors: Iliana Avila-Campillo
# Last date modified: December 8, 2005 by Iliana
# Files are downloaded from ftp site, into ./go_sql, ready to load into db, creates additional tables
# TODO: Table gi2go
##########################################################################################################

print "------------------------- update_go.pl --------------------------\n";

use DBI();

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_go.pl <db user> <db password> <db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];
$dbkind = 'go';

print "Downloading GO sql files to go_sql directory... ";
system('rm -r ./go_sql');
system('mkdir go_sql');

# seqdb contains all the GO db information
system("wget ftp://ftp.geneontology.org/pub/go/godatabase/archive/latest/go_*-seqdb-tables.tar.gz --directory-prefix=./go_sql/") == 0 or die "\nError: $?\n";
print "done.\n";

# The following messy mess of LIST loops is my way of getting around the shell not expanding wild cards! -iliana (I tried EVERYTHING, wasted a day!)

system('cd go_sql;ls -1 > list.lst'); #print one entry per line of output
open (LIST, "go_sql/list.lst") or die "Could not open go_sql/list.lst\n";
while ($file = <LIST>) {
	chomp $file;
	if($file =~ /gz$/){
		$directory = "./go_sql/".substr($file,0,-7);
		system("cd go_sql; gunzip < ${file} | tar xvf -");		
	}
}
close(LIST);
print "\n$directory\n";

print "Creating database... ";
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("DROP DATABASE IF EXISTS $dbname");
$dbh->do("CREATE DATABASE $dbname") or die "Error: $dbh->errstr";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";
print "done.\n";

print "Creating tables...\n";
system("cd ${directory};ls -1 > list.lst"); #print one entry per line of output
open (LIST, "${directory}/list.lst") or die "Could not open go_sql/list.lst\n";
while ($file = <LIST>) {
	chomp $file;
	if($file =~ /sql$/){
		print "\tLoading ${directory}/${file}...";
		system("mysql $dbname < ${directory}/${file} -u $dbuser -p${dbpwd}") == 0 or die "\nError: $?\n";
		print "done.\n";
	}
}
print "done.\n";
close(LIST);

print "Populating tables...\n";
system("rm ${directory}/list.lst");
system("cd ${directory};ls -1 > list.lst"); #print one entry per line of output
open (LIST, "${directory}/list.lst") or die "Could not open go_sql/list.lst\n";
while ($file = <LIST>) {
	chomp $file;
	if($file =~ /txt$/){
		print "\tLoading ${directory}/${file}: ";
		system("mysqlimport -u $dbuser -p${dbpwd} -L $dbname ${directory}/${file}") == 0 or die "\nError: $?\n";
	}
}
close(LIST);
print "done.\n";

### messy code over ###


############ CREATE term_children TABLE ##########################

$sth = $dbh->prepare("SELECT id FROM term WHERE term_type = ?") or die "Error: $dbh->errstr";
$sth->execute("universal") or die "Error: $dbh->errstr";

while(@row = $sth->fetchrow_array()){
	$rootID = $row[0]; 
}

print "Root term id = $rootID\n";

# this table contains a term's immediate children
print "Creating term_children table... ";
$dbh->do("CREATE TABLE term_children ( term_id INT, children LONGTEXT)") or die "\nError: $dbh->errstr";
my %hash = ();
insertChildren($rootID,%hash);
print "done.\n";
$dbh->disconnect();

print "------------------- Leaving update_go.pl --------------------\n";

#################################################################
# Recursively inserts rows into table term_children, 
# starts with the given id and then inserts rows for the children
#################################################################
sub insertChildren {
 	
 	my ($rootID,%hash) = @_;
 	
 	if( exists $hash{$rootID} ){return;}
        
     my @children = getChildrenIDs($rootID);
     if(scalar(@children) == 0){ return;}
     $childrenList = $children[0];
     for($i = 1; $i < scalar(@children); $i++){
   		$childrenList = $childrenList.",".$children[$i];
     }
     my $sth = $dbh->prepare_cached( "INSERT INTO term_children VALUES (?,?)" ) or die "\nError: $dbh->errstr";
     $sth->execute($rootID,$childrenList) or die "\nError: $dbh->errstr";
     $hash{$rootID}=1;
     
     #recursive call
     foreach $childID (@children){
     	insertChildren($childID,%hash);
     }
     
}


###########################################################
# Returns the children IDs of the given term
###########################################################
sub getChildrenIDs {
        
       my ($termID) = @_;
        my $sth = $dbh->prepare_cached("SELECT child.id  FROM term AS parent, term2term, term AS child WHERE parent.id = term2term.term1_id AND parent.id = ? AND child.id  = term2term.term2_id AND parent.id != child.id AND child.is_obsolete = 0") or die "\nError: $dbh->errstr\n";
        $sth->execute($termID) or die "\nError: $dbh->errstr\n";
        
        my @children;
        while(@row = $sth->fetchrow_array){
        		push(@children, $row[0]);
        }
        	  
        	return @children;
}

############################ OLD CODE ###################################
############ CREATE ancestor and subterms TABLES ################

#open (REL, '>go_sql/relation.txt') or die "Could not create ./go_sql/relation.txt\n";

#$relsth = $dbh->prepare('SELECT term1_id, term2_id FROM term2term');
#$relsth->execute();

# term1_id is the parent of term2_id
#while ($relref = $relsth->fetchrow_hashref()) {
#	print REL $relref->{'term1_id'}."\t".$relref->{'term2_id'}."\n";
#}

#open (OBS, '>go_sql/obsolete.txt');

#$obssth = $dbh->prepare('SELECT id FROM term WHERE is_obsolete=1');
#$obssth->execute();

#while ($obsref = $obssth->fetchrow_hashref()) {
#	print OBS $obsref->{'id'}."\n";
#}

# where is this script????
#system('update_go_jokbo.pl go_sql/relation.txt go_sql/obsolete.txt');
#$dbh->do('CREATE TABLE ancestor (term_id INT, ancestors TEXT, INDEX (term_id))');
#$dbh->do('CREATE TABLE subterms (term_id INT, subterms LONGTEXT, INDEX (term_id))');

#$dbh->do('LOAD DATA INFILE \'/bsr1/bioinfo/public_html/pub/bin/go_sql/relation.txt.ancestors\' INTO TABLE ancestor');
#$dbh->do('LOAD DATA INFILE \'/bsr1/bioinfo/public_html/pub/bin/go_sql/relation.txt.subterms\' INTO TABLE subterms');