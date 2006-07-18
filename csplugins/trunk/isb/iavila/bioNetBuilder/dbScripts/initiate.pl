#!/usr/bin/perl

###########################################################################################
# Authors: Iliana Avila-Campillo, adapted from Junghwan Park
# Last date modified: July 17, 2006 by Iliana
# Calls update.pl with "all" argument, so that all dbs are updated.
# Requires initiate.props in same level directory.
###########################################################################################
use DBI();

print "---------------------- initiate.pl -------------------------\n";

open(PROPS, "initiate.props") or die "Could not find file \"./initiate.props\"\n";

my $dbuser, $dbpwd;
my  %propsHash;

print "Reading  initiate.props file...\n";

while (<PROPS>) {
	chomp;
	
	if($_ =~ /^#/){
		next; #ignore comments
	}
	
	($leftSide,$rightSide) = split(/=/,$_);
	
	if($leftSide =~ /^dbuser/){
		$dbuser = $rightSide;
		print "\tdbuser = $dbuser\n";
		next;
	}
	
	if($leftSide =~ /^dbpwd/){
		$dbpwd=$rightSide;
		print "\tdbpwd = $dbpwd\n";
		next;
	}
	
	if($leftSide =~ /^dbnames/){
		@targetdbs = split(/,/, $rightSide);
		print "\tdbs:\t";
		for $t (@targetdbs){
			print "$t\t";
		}
		print "\n";
		next;
	}
	
	($db, $propname) = split(/:/,$leftSide);
	print "\tProperty: $db $propname $rightSide\n";
	$propsHash{$db}{$propname} = $rightSide;
}
print "done.\n";

print "Creating bionetbuilder_info database...";
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd)  or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("DROP DATABASE IF EXISTS bionetbuilder_info") or die "Could not drop bionetbuilder_info db: $dbh->errstr\n";
$dbh->do("CREATE DATABASE IF NOT EXISTS bionetbuilder_info") or die "Could not create bionetbuilder_info db: $dbh->errstr\n";;
$dbh->disconnect();
print "done\n";

print "Populating bionetbuilder_info database...";
$dbh = DBI->connect("dbi:mysql:database=bionetbuilder_info:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE TABLE IF NOT EXISTS when_updated (db VARCHAR(30) KEY, timestamp TIMESTAMP)") or die "Could not create when_updated: $dbh->errstr\n";
$dbh->do("CREATE TABLE IF NOT EXISTS db_name (db VARCHAR(30) KEY, dbname VARCHAR(30))") or die "Could not create db_name: $dbh->errstr\n";

for $t (@targetdbs){
	$dbh->do("INSERT INTO db_name VALUES (?, ?)", undef, $t, $t) or die "Could not insert into db_name: $dbh->errstr\n";
}

$dbh->disconnect();
print "done\n";

# Update all the databases
print "Calling update.pl for all dbs...\n";
system("./update.pl $dbuser $dbpwd all");

print "\n---------------------- Leaving initiate.pl -------------------------\n";




