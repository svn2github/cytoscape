#!/usr/bin/perl -w

use PVLR;

my $USAGE = <<USAGE;
      <.lrpv file> 
      <rp|rr|cr|cp>  (byRow|byCol) | (byPval|byRatio) 
      <function>     e.g. "< 0.05"
USAGE

if(scalar(@ARGV) < 3)
{
    die "$0\n$USAGE";
}

my $lrpv = PVLR->new(shift @ARGV);
my $type = shift @ARGV;
my $function = shift @ARGV;
my $validColumns = \@ARGV;

if($type eq "rp")
{
    print STDERR "By Row By Pval\n";
    writeORFlist($lrpv->makeORFlistByRowByPvalue(makeFunction($function), 
						 $validColumns));
}

if($type eq "rr")
{
    print STDERR "By Row By Ratio\n";
    writeORFlist($lrpv->makeORFlistByRowByRatio(makeFunction($function),
						$validColumns));
}

if($type eq "cr")
{
    print STDERR "By Col By Ratio\n";
    writeORFlist($lrpv->makeORFlistByColumnByRatio(makeFunction($function),
						   $validColumns));
}

if($type eq "cp")
{
    print STDERR "By Col By PV\n";
    writeORFlist($lrpv->makeORFlistByColumnByPvalue(makeFunction($function),
						    $validColumns));
}

sub makeFunction
{
    my ($functionString) = @_;

    my $str = 'sub { $_[0] ' . $functionString . '}';

    return(eval($str));
}

sub writeORFlist
{
    my ($orflist) = @_;

    foreach my $gene (sort keys %{$orflist})
    {
	printf "%s\t%s\n", $gene, join(" ", sort @{$orflist->{$gene}});
    }
    
}
