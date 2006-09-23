#!/usr/bin/perl -w

use PVLR;
use TimeData;

my $data = TimeData->new("timeseries.lrpv");

printf "COLS: %s\n", join(",", @{$data->columnNames()});

$data->getTimeOfMaxSigExpression("YKL096W");

my @ids = $data->idsInOrder();

print(join "\n", @ids[0..10]);
