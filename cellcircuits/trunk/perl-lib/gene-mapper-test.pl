#!/usr/bin/perl -w

use GOGeneMap;
use YeastHumanGeneMapper;

my $map = GOGeneMap->new("../db/dump/human-genes-in-GO-DB.txt");

testXref($map, "Q9HDC5");
testSymbol($map, "LCK_HUMAN");

my $ymap = GOGeneMap->new("../db/dump/yeast-genes-in-GO-DB.txt");

testXref($ymap, "YKL106W"); # AAT1, 1867976
testSymbol($ymap, "AAR2"); #1867975

my $yhmap = YeastHumanGeneMapper->new();

testHY($yhmap, "saccharomyces cerevisiae", "YBR182C");
testHY($yhmap, "homo sapiens", "Q9HDC5");

sub testHY
{
    my ($map, $org, $gene) = @_;

    printf "%s:%s = %s\n", $org, $gene, $map->mapName($gene, $org);
}

sub testXref
{
    my ($map, $xref) = @_;

    printf "%s\texistsXref:%s\n", $xref, $map->existsXref($xref);
    printf "%s\tgo:%s\n", $xref, $map->xref2go($xref);
    printf "%s\tsymbol:%s\n", $xref, $map->xref2symbol($xref);
}

sub testSymbol
{
    my ($map, $symbol) = @_;

    printf "%s\texistsSymbol:%s\n", $symbol, $map->existsSymbol($symbol);
    printf "%s\tgo:%s\n", $symbol, $map->symbol2go($symbol);

}
