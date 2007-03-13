#!/usr/bin/perl

@s0 = readSif($ARGV[0]);
@s1 = readSif($ARGV[1]);

#if ( $#s0 != $#s1 ) {
#	print "$ARGV[0] size: $#s0\n$ARGV[1] size: $#s1\n";
#	exit;
#}

for ( $i = 0; $i <= $#s0; $i++ ) {
	
	if ( $s0[$i] ne $s1[$i] ) {
		print "Mismatch ($i)!\n$s0[$i] ($ARGV[0])\n$s1[$i] ($ARGV[1])\n\n";
		#exit;
	} 
}

print "Files match!\n";


sub readSif {
	my $f = shift;
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
			push @results, $res;
		}
	}

	my @r = sort @results;
	return @r; 
}

