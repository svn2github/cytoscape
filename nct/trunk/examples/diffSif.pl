#!/usr/bin/perl

%inter = ();
@s0 = readSif($ARGV[0],1);
@s1 = readSif($ARGV[1],2);


for $i (keys %inter) {
	$x = $inter{$i};
	if ( $x == 3 )	{
		# hooray
	} elsif ( $x == 1 ) {
		print "missing in $ARGV[1]:  $i\n";
	} elsif ( $x == 2 ) {
		print "missing in $ARGV[0]:  $i\n";
	} else {
		print "WTF?  $i -> $x\n";
	}
}


sub readSif {
	my $f = shift;
	my $bits = shift;
	my @results = ();

	open F, "$f" || die "couldn't open $f\n";
	while (<F>) {
		if ( $_ =~ /^(\S+)\s+(\S+)\s+(\S+)\s*$/ ) {
			my $n1 = $1;
			my $e = $2;
			my $n2 = $3;
			my $res = "";
			if ( $n1 le $n2 ) {
				$res = $n1 . " " . $e . " " . $n2;
			} else {
				$res = $n2 . " " . $e . " " . $n1;
			}
			$inter{$res} += $bits;
			push @results, $res;
		}
	}

	my @r = sort @results;
	return @r; 
}

