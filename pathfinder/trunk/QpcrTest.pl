#!/usr/bin/perl -w

use QpcrCopyNumberTimeData;

die "$0: <.mrna file> <thresh>\n" if scalar(@ARGV) != 2;

my ($file, $thresh) = @ARGV;

my $qdata = QpcrCopyNumberTimeData->new($file);

my @ids = @{$qdata->ids()};

my @cols = @{$qdata->columnNames()};

printf "cols [%s]\n", join(", ", @cols);
printf "ids [%s]\n", join(", ", @ids);

foreach my $id (@ids[0..5])
{

    print "$id\n";
    my @vals = @{$qdata->getGeneData($id)};
    printf "   data: [%s]\n", join(", ", @vals);

    foreach my $c (@cols)
    {
	printf "   $c: %s\n", $qdata->getValue($id, $c);
    }

    printf "   TME = %s\n", $qdata->index2colName($qdata->getTimeOfMaxSigExpression($id, $thresh));
    printf "   TFE = %s\n", $qdata->index2colName($qdata->getTimeOfFirstSigExpression($id, $thresh));
}

