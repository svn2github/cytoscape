#!/usr/bin/perl

# This script reads interactions from the Vidal dataset (PMID:16189514, interactions are in supplemental excell file)
# Vidal interactions  tab separated file has this header:
# EntrezIDA       GeneSymbolA     EntrezGeneIDB   GeneSymbolB     Y2H     LCI
# IDs in this dataset are NCBI GeneIDs. This script uses the synonyms database for BioNetBuilder
# to translate these IDs to protein GIs and then writes them to a tab separated file so that it can be
# writen to a SQL table

use DBI();
use Cwd;
use warnings;

if(scalar(@ARGV) < 4){
	print "USAGE: perl vidal_interactions.pl <synonyms db name> <synonyms db user> <synonyms db password> <tab delimited Vidal interactions>\n";
	exit;
}

$dbName = $ARGV[0];
$dbUser = $ARGV[1];
$dbPassword = $ARGV[2];
$vidalFile = $ARGV[3];

open (IN, $vidalFile) or die "Could not open file $vidalFile\n";

# connect to the synonyms db
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbUser, $dbPassword) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("USE $dbName") or die "Error: $dbh->errstr";

# prepare the ID mapping statement
$mapStatement = $dbh->prepare("SELECT protgi FROM refseq_geneid WHERE geneid=?") or die "Error: $dbh->errstr";
$mapStatement2 = $dbh->prepare("SELECT accession FROM refseq_accession WHERE protgi=?") or die "Error: $dbh->errstr";

my %idMap; # from geneID to GI
my %idMap2; # from GI to RefSeq

$numInteractions = 0;
$line = <IN>; # skip header
while($line = <IN>){
	@cols = split(/\s+/,$line);
	$geneID1 = $cols[0];
	$geneID2 = $cols[2];
	# the vidal file contains new interactions, and existent HPRD interactions that were validated by the study
	# if the "Y2H" column has a "+" then this is a new interaction
	$y2h = $cols[4];
	if($y2h eq "+"){
		
		$protGi1 = "";
		$protGi2 = "";
		
		if(exists $idMap{$geneID1}){
			$protGi1 = $idMap{$geneID1};
		}
		
		if(exists $idMap{$geneID2}){
			$protGi2 = $idMap{$geneID2};
		}
		
		if($protGi1 eq ""){
			$mapStatement->execute($geneID1) or die "Could not execute statement: "."$mapStatement.errstr";
			@row = $mapStatement->fetchrow_array();
			if( scalar(@row) > 0 ){
				#match
				$protGi1 = $row[0];
				$idMap{geneID1} = $protGi1;
			}#if match
		}
		
		if($protGi2 eq ""){
			$mapStatement->execute($geneID2) or die "Could not execute statement: "."$mapStatement.errstr";
			@row = $mapStatement->fetchrow_array();
			if( scalar(@row) > 0 ){
				#match
				$protGi2 = $row[0];
				$idMap{geneID2} = $protGi2;
			}#if match
		}
		
		$protRefSeq1 = "";
		$protRefSeq2 = "";
		
		if($protGi1 ne ""){
			if(exists $idMap2{$protGi1}){
				$protRefSeq1 = $idMap2{$protGi1};
			}else{
				$mapStatement2->execute($protGi1) or die "Could not execute statement: "."$mapStatement.errstr";
				@row = $mapStatement2->fetchrow_array();
				if(scalar(@row) > 0){
					$protRefSeq1 = $row[0];
					$idMap2{$protGi1} = $protRefSeq1;
				}
			}
		}
		
		if($protGi2 ne ""){
			if(exists $idMap2{$protGi2}){
				$protRefSeq2 = $idMap2{$protGi2};
			}else{
				$mapStatement2->execute($protGi2) or die "Could not execute statement: "."$mapStatement.errstr";
				@row = $mapStatement2->fetchrow_array();
				if(scalar(@row) > 0){
					$protRefSeq2 = $row[0];
					$idMap2{$protGi2} = $protRefSeq2;
				}
			}
		}
		
		# test
		#print "protGi1 = $protGi1\n";
		#print "protGi2 = $protGi2\n";
		
		$geneID1 = "GeneID:".$geneID1;
		$geneID2 = "GeneID:".$geneID2;
		
		$interactor1 = ($protRefSeq1 ne '' ? ${protRefSeq1} : $protGi1 ne '' ? "GI:$protGi1" : "$geneID1");
		$interactor2 = ($protRefSeq2 ne '' ? ${protRefSeq2} : $protGi2 ne '' ? "GI:$protGi2" : "$geneID2");
		
		# This prints: <interaction id> <interactor 1> pp <interactor 2> 9696 9606 <Vidal PubMedID> two hybrid
		# For bioNetBuilder's interactions mySQL database
		
		$numInteractions++;
		print "VIDAL_$numInteractions\t".$interactor1."\tpp\t".$interactor2."\t9606\t9606\t16189514\ttwo hybrid\n";
		
	}#if new interaction
	
}#while


