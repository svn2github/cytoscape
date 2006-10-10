package CCDB::Synonyms;

require Exporter;

our @ISA       = qw(Exporter);
our @EXPORT    = qw(
		    $synonyms
		    $gene_ids
		    $symbols
		    ); #symbols to export by default
our @EXPORT_OK = qw(); #symbols to export on request
our $VERSION   = 1.00;

use File::Basename;
use CGI qw(:standard);
use CCDB::Constants qw($SYNONYM_FILE);

our ($synonyms,
     $gene_ids,
     $symbols
     ) = load_synonyms_hash_from_file($SYNONYM_FILE);

sub load_synonyms_hash_from_file
{
    my ($file) = @_;

    my $synonyms = {};
    my $gene_ids = {};
    my $symbols  = {};

    open(FILE, "$file") or die "Cannot open $file: $!\n";
    while(<FILE>) {
	next if($.==1); #skip header
	chomp;
	my @l = split(/\t/);
	die "ERROR: line $. of $file has < 5 items.\n" if(scalar(@l) < 5);

	my ($species_id, $genus, $species, $gene_id, $gene_symbol, @synonyms) = @l;

	$gene_ids->{$species_id}{$gene_symbol} = $gene_id;
	$symbols->{$gene_id} = $gene_symbol;
	if(scalar(@synonyms) > 0)
	{
	    $synonyms->{$gene_id} = join("\t", $gene_symbol, 
					 join(" ", @synonyms));
	}
	else {
	    $synonyms->{$gene_id} = $gene_symbol;
	}
    }
    close(FILE);

    return ($synonyms,$gene_ids,$symbols);
}
