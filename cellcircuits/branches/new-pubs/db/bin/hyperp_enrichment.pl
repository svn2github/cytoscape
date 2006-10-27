#! /usr/bin/perl

$hyper_p = "/cellar/users/cworkman/src/hypergeometric_Pvalue/hyper_p";

$usg=<<USG;
usage: $0 <verbose-enrichment-file>+

  example: $0 verbose.variant8.gl.enrichment

USG
die $usg if(@ARGV < 1);

foreach $file (@ARGV){
    @t = split(/[.]/, $file); shift @t;
    $out = join ".", "results", @t;

    open(OUT, "> $out") or die "Cannot open $out: $!\n";

    my $count = 0;
    my @header = ();
    open(FILE, "< $file") or die "Cannot open $file: $!\n";
    while(<FILE>){
	next if(/\#/);
	chomp;
	if($count == 0){
	    @header = split(/\t/, $_); $count++; next;
	}
	if($count == 1){
	    printf OUT "pval\t%s\n", join("\t", @header); $count++;
	}
	my @data = split(/\t/, $_);
	my $hyperp_input = join " ", @data[0..3];
	my $pval = `$hyper_p $hyperp_input\n`;
	chomp $pval;

	printf STDERR "%0.6e\t%s\n", $pval, join("\t", @data);
	printf OUT    "%0.6e\t%s\n", $pval, join("\t", @data);
    }
    close(FILE);

    close(OUT);
}

