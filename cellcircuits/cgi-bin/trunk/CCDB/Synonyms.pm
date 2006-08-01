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

my $DIR = "/opt/www/cgi-bin/search/v0.8/CCDB";
#if(exists($ENV{DOCUMENT_ROOT}))
#{
#    print STDERR "CCDB::Synonym. SCRIPT_FILENAME = $ENV{SCRIPT_FILENAME}\n";
#    my $script_path = dirname($ENV{SCRIPT_FILENAME});
#    $DIR = join("/", $script_path, "CCDB");
#}
#else
#{
#    $DIR = `pwd`;
#    chomp $DIR;
#    print STDERR "DocRoot is NOT set using $DIR\n";
#}

our ($synonyms,
     $gene_ids,
     $symbols
     ) = load_synonyms_hash_from_file($DIR . "/synonyms.06182006.tab");

sub load_synonyms_hash_from_file
{
    my ($file) = @_;

    my $synonyms = {};
    my $gene_ids = {};
    my $symbols  = {};
    open(FILE, "< $file") or die "Cannot open $file: $!\n";
    while(<FILE>) {
	next if($.==1); #skip header
	chomp;
	my @l = split(/\t/);
	die "ERROR: line $. of $file has < 5 items.\n" if(scalar(@l) < 5);
	my $species_id    = $l[0];
	my $genus         = $l[1];
	my $species       = $l[2];
	my $gene_id       = $l[3];
	my $gene_symbol   = $l[4];

	$gene_ids->{$species_id}{$gene_symbol} = $gene_id;
	$symbols->{$gene_id} = $gene_symbol;
	if(scalar(@l) > 5){
	    my $gene_synonyms = join " ", @l[5..$#l];
	    $synonyms->{$gene_id} = join "\t", $gene_symbol, $gene_synonyms;
	}
	elsif(scalar(@l) == 5){
	    $synonyms->{$gene_id} = $gene_symbol;
	}
    }
    close(FILE);

    return ($synonyms,$gene_ids,$symbols);
}
