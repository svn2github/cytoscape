#!/usr/bin/perl

###########################################################################################
# Authors: Iliana Avila-Campillo, adapted from Junghwan Park
# Last date modified: December 7, 2005 by Iliana
# Calls individual scripts for updating each db
###########################################################################################
use DBI();

my $testing = 0; # set to 0 when NOT testing!

print "---------------------- update.pl -------------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE update.pl <db user> <db password>  <:prolinks:kegg:bind:dip:go:synonyms:all>\n";
	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];

$fulllist = ":prolinks:kegg:bind:dip:go:synonyms:";
if ($ARGV[0] =~ /all/) {
	$updatee = $fulllist;
}else{
	$updatee = $ARGV[2];
}


print "Database(s) to update: $updatee\n";

$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

print "Create metainfo db if not there...";
$dbh->do("CREATE DATABASE IF NOT EXISTS metainfo") or die "Could not create metainfo db: $dbh->errstr\n";
$dbh->disconnect();
$dbh = DBI->connect("dbi:mysql:database=metainfo:host=localhost", $dbuser, $dbpwd)  or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE TABLE IF NOT EXISTS when_updated (db VARCHAR(30) KEY, timestamp TIMESTAMP)") or die "Could not create when_updated: $dbh->errstr\n";
$dbh->do("CREATE TABLE IF NOT EXISTS db_name (db VARCHAR(30) KEY, dbname VARCHAR(30))") or die "Could not create db_name: $dbh->errstr\n";
print "done\n";

my @dbkinds = ('prolinks', 'kegg', 'go','bind','synonyms');

@updatees = split(/:/, $updatee);

print "Calling udpate script for each db...\n";
foreach $curupdatee (@updatees) { 
	foreach $dbkind (@dbkinds) {
		if ($curupdatee eq $dbkind) {
			my ($dbname, $sth);
			$dbname = get_db_name($dbh, $dbkind);
			$newdbname = get_new_db_name($dbname, $dbkind);
			
			print "dbname = $dbname, newdbname = $newdbname, script= update_$dbkind.pl\n";			

			# if a db is being updated, we create a new name for it
	        # so that if the other one is currently being used, we don't crash or mix up data
			print "Calling command: ./update_${dbkind}.pl ${dbuser} ${dbpwd} ${newdbname}\n";
			system("./update_${dbkind}.pl ${dbuser} ${dbpwd} ${newdbname}");
			
			# done updating, update metainfo
			update_dbinfo($dbh, $dbkind, $dbname, $newdbname);
		}
	}
}
$dbh->disconnect();
print "\n---------------------- Leaving update.pl -------------------------\n";

########################################## 
# Get the current name for the database
##########################################
sub get_db_name {
	my ($dbh, $dbkind, $dbname);
	
	$dbh = shift;
	$dbkind = shift;
		
	my $sth = $dbh->prepare_cached("SELECT dbname FROM db_name WHERE db=?") or die "Error: $dbh->errstr";
	$sth->execute($dbkind) or die "Error: $dbh->errstr";
	
	while ($row = $sth->fetchrow_hashref()) {
		$dbname = $row->{'dbname'};
	}
	return $dbname;
}

############################################################# 
# Get new name for database: for example, 
# for prolinks, it returns prolinks0 or prolinks1
# So if the current prolinks db is contained in prolinks0, then a new
# prolinks will be created in prolinks1, and prolinks0 will be droped
# once prolinks1 is fully populated. metainfo.db_name table specifies
# the current fully popoulated prolinks db that should be used.
# If testing, returns normal db name with a "_test" ending
#############################################################
sub get_new_db_name {
	my ($dbname, $newdbname);

	$dbname = shift;
	$dbkind = shift;
	
	if($dbname =~ /$0/){
		#switch to 1
		if($testing == 0){
			return $dbname."1";
		}else{
			return $dbname."1_test";
		}
	}
	
	if($dbname =~ /$1/){
		# switch to 0
		if($testing == 0){
			return $dbname."0";
		}else{
			return $dbname."0_test";
		}
	}
	
	# this is the first version of the db to be created:
	if($testing == 0){
		return $dbname."0";
    }else{
    		return $dbname."0_test";
    }
}

#################################################
# Updates metainfo.db_info table
#################################################
sub update_dbinfo {
	print "Updating db information for $dbname in metainfo tables...";
	my ($dbh, $dbkind, $dbname, $newdbname);
	$dbh = shift;
	$dbkind = shift;
	$dbname = shift; #old name
	$newdbname = shift;
	
	$dbh->do("INSERT INTO when_updated VALUES (?, CURRENT_TIMESTAMP())", undef, $dbkind) or die "Error: $dbh->errstr";
	$dbh->do("DELETE FROM db_name WHERE db=?", undef, $dbkind) or die "Error: $dbh->errstr";
	$dbh->do("INSERT INTO db_name VALUES (?, ?)", undef, $dbkind, $newdbname) or die "Error: $dbh->errstr";
	print "done\n";
}
