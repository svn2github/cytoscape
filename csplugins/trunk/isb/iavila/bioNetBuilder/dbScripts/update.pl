#!/usr/bin/perl

###########################################################################################
# Authors: Iliana Avila-Campillo, adapted from Junghwan Park
# Last date modified: July 17, 2006 by Iliana
# Calls individual scripts for updating each db
###########################################################################################
use DBI();

my $testing = 0; # set to 0 when NOT testing!

print "---------------------- update.pl -------------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE update.pl -u=db user -p=db password  [synonyms=synonyms db name] [prolinks=prolinks db name] [kegg=kegg db name] [bind=bind db name] [dip=dip db name] [hprd=hprd db name] [go=go db name]\n".
	      "Examples:\n".
	      "Update all dbs: perl update.pl rootuser rootpassword synonyms=synonyms0 prolinks=prolinks0 kegg=kegg0 bind=bind0 dip=dip0 hprd=hprd0 go=go0)\n".
	      "Update only synonyms and prolinks: perl update.pl rootuser rootpassword synonyms=synonyms0 prolinks=prolinks0\n";
	die;
}

$dbuser="";
$dbpass="";
$synonyms="";
$prolinks="";
$kegg="";
$bind="";
$dip="";
$go="";
$hprd="";
foreach $entry (@ARGV){
	@values=split(/=/, $entry);
	if($entry =~ /^-u/){
		$dbuser=$values[1];
		print "DB user = $dbuser\n";
	
	}elsif($entry =~ /^-p/){
		$dbpass=$values[1];
		print "DB password = $dbpass\n";
	
	}elsif($entry =~ /^synonyms/){
		$synonyms=$values[1];
		print "synonyms = $synonyms\n";
	
	}elsif($entry =~ /^prolinks/){
		$prolinks=$values[1];
		print "prolinks = $prolinks\n";
	
	}elsif($entry =~ /^kegg/){
		$kegg=$values[1];
		print "kegg = $kegg\n";
	
	}elsif($entry =~ /^bind/){
		$bind=$values[1];
		print "bind = $bind\n";
	
	}elsif($entry =~ /^dip/){
		$dip=$values[1];
		print "dip = $dip\n";
	
	}elsif($entry =~ /^go/){
		$go=$values[1];
		print "go = $go\n";
	
	}elsif($entry =~ /^hprd/){
		$hprd = $values[1];
		print "hprd = $hprd\n";
	
	}
}

if($dbuser eq "" or $dbpass eq ""){
	print "No database user or database password given as arguments.\n";
	die;
}


$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpass) or die "Can't make database connect: $DBI::errstr\n";

print "Create bionetbuilder_info db if not there...";
$dbh->do("CREATE DATABASE IF NOT EXISTS bionetbuilder_info") or die "Could not create db: $dbh->errstr\n";
$dbh->disconnect();
$dbh = DBI->connect("dbi:mysql:database=bionetbuilder_info:host=localhost", $dbuser, $dbpwd)  or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE TABLE IF NOT EXISTS when_updated (db VARCHAR(30) KEY, timestamp TIMESTAMP)") or die "Could not create when_updated: $dbh->errstr\n";
$dbh->do("CREATE TABLE IF NOT EXISTS db_name (db VARCHAR(30) KEY, dbname VARCHAR(30))") or die "Could not create db_name: $dbh->errstr\n";
print "done\n";

if($synonyms ne ""){
	$cmd = "./update_synonyms.pl ${dbuser} ${dbpass} ${synonyms}";
	print "$cmd\n";
	system($cmd);
	update_dbinfo($dbh, "synonyms", $synonyms);
}

if($prolinks ne ""){
	$cmd ="./update_prolinks.pl ${dbuser} ${dbpass} ${prolinks}";
	system($cmd);
	update_dbinfo($dbh, "prolinks", ${prolinks});
}

if($kegg ne ""){
	$cmd = "./update_kegg.pl ${dbuser} ${dbpass} ${kegg}";
	print "$cmd\n";
	system($cmd);
	update_dbinfo($dbh, "kegg", ${kegg});
}

if($bind ne ""){
	$cmd = "./update_bind.pl ${dbuser} ${dbpass} ${bind}";
	print "$cmd\n";
	system($cmd);
	update_dbinfo($dbh, "bind", ${bind});
}

if($dip ne ""){
	$cmd = "./update_dip.pl ${dbuser} ${dbpass} ${dip}";
	print "$cmd\n";
	system($cmd);
	update_dbinfo($dbh, "dip", ${dip});
}

if($hprd ne ""){
	$cmd = "./update_hprd.pl ${dbuser} ${dbpass} ${hprd}";
	print "$cmd\n";
	system($cmd);
	update_dbinfo($dbh, "hprd", ${hprd});
}

if($go ne ""){
	$cmd = "./update_go.pl ${dbuser} ${dbpass} ${go}";
	print "$cmd\n";
	system($cmd);
	update_dbinfo($dbh,"go",${go});
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

#################################################
# Updates bionetbuilder_info
#################################################
sub update_dbinfo {
	my ($dbh, $dbkind, $newdbname);
	$dbh = shift;
	$dbkind = shift;
	$newdbname = shift;
	print "Updating db information for $dbkind in bionetbuilder_info tables...";
	$dbh->do("INSERT INTO when_updated VALUES (?, CURRENT_TIMESTAMP())", undef, $dbkind) or die "Error: $dbh->errstr";
	$dbh->do("DELETE FROM db_name WHERE db=?", undef, $dbkind) or die "Error: $dbh->errstr";
	$dbh->do("INSERT INTO db_name VALUES (?, ?)", undef, $dbkind, $newdbname) or die "Error: $dbh->errstr";
	print "done\n";
}
