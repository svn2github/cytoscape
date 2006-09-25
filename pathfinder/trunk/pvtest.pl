#!/usr/bin/perl -w

use PVLR;
use TimeData;

my $data = TimeData->new("timeseries.lrpv");

printf "COLS: %s\n", join(",", @{$data->columnNames()});

$data->getTimeOfMaxSigExpression("YKL096W", 0.05);
$data->getTimeOfMaxSigExpression("YDR405W", 1);
$data->getTimeOfMaxSigExpression("YCR047C", 0.1);

my @colNames = @{$data->columnNames()};
my @ids = $data->idsInOrder();
my $THR = 0.05;
my $time;
foreach my $id (@ids)
{
    $time = $data->getTimeOfMaxSigExpression($id, 0.05);
    if($time >= 0)
    {
	printf "%s %s %s\n", $id, $time, $colNames[$time];
    }
}


