#!/usr/bin/perl -w

use QpcrCopyNumberMultiTimeData;
use MinProfileAnalyzer;

die "$0: <.mrna file>\n" if scalar(@ARGV) != 1;

my ($file) = @ARGV;

my $qdata = QpcrCopyNumberMultiTimeData->new($file);

my @ids = @{$qdata->ids()};

my @cols = @{$qdata->columnNames()};

printf "cols [%s]\n", join(", ", @cols);
printf "ids [%s]\n", join(", ", @ids);

my $profileAnalyzer = MinProfileAnalyzer->new();

foreach my $id (@ids[0..5])
{
    print "$id\n";
    my @vals = @{$qdata->getGeneData($id)};
    printf "   data: [%s]\n", join(", ", @vals);

    foreach my $c (@cols)
    {
	printf "   $c: %s\n", $qdata->getValue($id, $c);
    }

    my $tme = $qdata->getTimeOfMaxSigExpression($id, $profileAnalyzer);
    
    printf "   TME = %s\n", join(",", map { $qdata->index2colName($_) } @{$tme});
}

$qdata->getAllTME($profileAnalyzer, "test-tme.na", "test-ratio.na");
