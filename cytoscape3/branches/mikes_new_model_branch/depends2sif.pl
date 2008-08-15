#!/usr/bin/perl

$everything = 0;
if ( $ARGV[0] eq "-everything" ) {
	$everything = 1;
}

$currentModule = "";
$record = 0;

open COM, "mvn dependency:list -DexcludeTransitive|" or die "couldn't execute mvn dependency:list\n";
while (<COM>) {

	if ( $_ =~ /Building\s+(\S+)\s+/ ) {
		$currentModule = $1;
		$record = 0;
	} elsif ( $currentModule ne "" &&
	          $_ =~ /The following files have been resolved/ ) {
		$record = 1; 
    } elsif ( $record == 1 &&
	          $_ =~ /\s+(\S+)\:(\S+)\:\S+\:\S+\:(\S+)/ ) {
		$org = $1;
		$depModule = $2;
		$phase = $3;
		if ( $phase eq "compile" ) {
			if ( $everything == 1 ) {
				print "$currentModule	dependsOn	$depModule\n";
			} else {
				if ( $org =~ /cytoscape/ ) {
					print "$currentModule	dependsOn	$depModule\n";
				}
			}
		}
	}
}

close COM;
