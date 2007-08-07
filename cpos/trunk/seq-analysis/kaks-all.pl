#!/usr/bin/perl -w

use Funspec;
use Getopt::Long;

my ($outdir);
GetOptions('o|output:s' => \$outdir);

die "$0: [-o <outdir>] <orflist> <tuple>\n" if (scalar(@ARGV) != 2);

my ($orflist, $tupleFile) = @ARGV;

my ($cat2orf, $orf2cat) = readORFlist($orflist);
my @tuples = readTuple($tupleFile);

my $KAKS_SCRIPT = "kaks.sh";

my $rFile = $outdir . "/kaks.R";
my $scriptFile = $outdir . "/run.sh";

open R, ">$rFile" || die "Can't open $rFile: $!\n";
open SCRIPT, ">$scriptFile" || die "Can't open $scriptFile\n";

print R qq(source("../plot.kaks.R")) . "\n";

print R qq(pdf(file="kaks.pdf")) . "\n";

print R qq(w.ave <- c()) . "\n";
print R qq(names <- c()) . "\n";


foreach my $tuple (@tuples)
{
    next if (scalar(@{$tuple}) < 2);

    map { next if(scalar(@{$cat2orf->{$_}}) < 2)} @{$tuple};

    foreach my $x (@{$tuple})
    {
	printf SCRIPT "%s %s.fasta\n", $KAKS_SCRIPT, $x;
    }

    my @tmp = split(/_/, $tuple->[0]);
    my $name = shift @tmp;
    next if ($name eq "2");
    printf R ("data <- plot.kaks(files=c(%s), names=c(%s), group.name=%s)\n", 
	      join(", ", map { qt($_ . ".fasta.kaks")} @{$tuple}),
	      qq("Chrom", "Subt"),
	      qt($name));

    print R qq(w.ave <- rbind(w.ave, lapply(data\$w, median))) . "\n";
    printf R "names <- c(names, %s)\n", qt($name);

}
printf R "plot.summary(w.ave, names, col.names=c(%s))\n", qq("Chrom", "Subt");
print R qq(dev.off()) . "\n";
close R;

close SCRIPT;

sub qt
{
    my ($s) = @_;
    return "\"" . $s . "\"";
}

sub readTuple
{
    my ($file) = @_;
    open(IN, $file) || die "Can't open $file: $!\n";

    my @tuples;
    while(<IN>)
    {
	chomp;
	my @F = split(/\s+/);
	push @tuples, \@F;
    }
    return @tuples;
}
