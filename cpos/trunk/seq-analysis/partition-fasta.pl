#!/usr/bin/perl -w

use Funspec;
use Fasta;

use Getopt::Long;

my ($outdir);
GetOptions('o|output:s' => \$outdir);

die "$0: [-o <outdir>] <orflist> <fasta>\n" if (scalar(@ARGV) != 2);

my ($orflistFile, $fasta) = @ARGV;

my ($cat2orf, $orf2cat) = readORFlist($orflistFile);

my ($seqMeta, $seqData) = readInMemory($fasta);

printf "### Categories [%d]\n", scalar(keys %{$cat2orf});
#map { printf "  %s\n", $_; } keys %{$cat2orf};

printf("### Sequences [meta=%d] [data=%d]\n",
       scalar(keys %{$seqMeta}), 
       scalar(keys %{$seqData}));
#map { printf "  %s\n", $_; } keys %{$seqMeta};

foreach my $cat (keys %{$cat2orf})
{
    my $outfile = $cat . ".fasta";
    if(defined($outdir))
    {
	$outfile = $outdir . "/" . $outfile;
    }
    open OUT, ">$outfile" || die "Can't open $outfile: $!\n";
    my @orfs = @{$cat2orf->{$cat}};
    foreach my $orf (@orfs)
    {
	if(exists($seqMeta->{$orf}) && exists($seqData->{$orf}))
	{
	    printf OUT ">%s %s\n", $orf, $seqMeta->{$orf};
	    printf OUT "%s\n", $seqData->{$orf};
	}
	else
	{
	    print STDERR "### missing sequence data for $orf\n";
	}
    }
    close OUT;
}
