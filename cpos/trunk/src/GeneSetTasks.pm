package GeneSetTasks;

use GeneSet;

require Exporter;
our @ISA = qw(Exporter);

#symbols to export by default
our @EXPORT = qw(
		 filterSetsByMTD
		 );

#symbols to export on request
our @EXPORT_OK = qw(); 

sub filterSetsByMTD
{
    my ($sets, $mtdCutoff) = @_;

    my %orflist;

    foreach my $gs (@{$sets})
    {
	my @a = $gs->getGenesByMTD($mtdCutoff);
	$orflist{$gs->name()} = \@a;
    }
    return \%orflist;
}



1;
