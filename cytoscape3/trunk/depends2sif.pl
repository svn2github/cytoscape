#!/usr/bin/perl


%lastModule = ();

open COM, "mvn dependency:tree -Dtokens=whitespace|" or die "couldn't execute mvn dependency:tree\n";
while (<COM>) {

	if ( $_ =~ /^\[INFO\] \[dependency\:tree\$]/ ) {
		$lastModule = ();	
    } elsif ( $_ =~ /^\[INFO\] (\S+)\:(\S+)\:\S+\:\S+$/ ) {
		$org = $1;
		$depModule = $2;
		$lastModule{0} = $org . "." . $depModule;
    } elsif ( $_ =~ /^\[INFO\] ( +)(\S+)\:(\S+)\:\S+\:\S+\:(\S+)$/ ) {
		$spaces = (length $1)/3;
		$org = $2;
		$depModule = $3;
		$phase = $4;
		$lastModule{$spaces} = $org . "." . $depModule;
		if ( $phase eq "compile" ) {
			print "$lastModule{$spaces-1}	dependsOn	$lastModule{$spaces}\n";
		}
	}
}

close COM;
