#! /usr/bin/perl

my $usage=<<USG;
 usage: $0 <database instance> <output dir> <pub>+

 e.g. $0 cellcircuits_dev output Yeang2005_GB

USG

die $usage if(@ARGV < 3);

use DBI;
use File::Spec;


my ($db, $OUTPUT_DIR, @PUBS) = @ARGV;

my $server   = 'localhost';
my $username = 'mdaly';
my $password = 'mdalysql';

my $dbh = DBI->connect("dbi:mysql:$db", $username, $password);

my $get_gene_id_from_symbol = qq{
SELECT gene_product.id
FROM   gene_product, species
WHERE
 gene_product.species_id = species.id AND
 species.id = ? AND
 gene_product.symbol = ?
};
$get_gene_id_from_symbol_STH = $dbh->prepare($get_gene_id_from_symbol);

my $get_gene_id_from_synonym = qq{
SELECT gene_product.id
FROM   gene_product, gene_product_synonym, species
WHERE
 gene_product.id = gene_product_synonym.gene_product_id AND
 gene_product.species_id = species.id AND
 species.id = ? AND
 gene_product_synonym.product_synonym = ?
};
$get_gene_id_from_synonym_STH = $dbh->prepare($get_gene_id_from_synonym);

my $get_model_id = qq{
SELECT id
FROM   model
WHERE  name = ? AND pub = ?
};
$get_model_id_STH = $dbh->prepare($get_model_id);

my $get_term_id = qq{
SELECT term.id
FROM   term
WHERE  term.acc = ?
};
$get_term_id_STH = $dbh->prepare($get_term_id);

my $enrichment_fields = [
			 'model_id',
			 'species_id',
			 'term_id',
			 'n_genes_in_model_with_term',
			 'n_genes_in_model',
			 'n_genes_with_term',
			 'n_genes_in_GO',
			 'pval',
			 'gene_ids'
			 ];

foreach my $pub (@PUBS)
{
    my $glDir = "$OUTPUT_DIR/gl/$pub";
    my $sqlDir = "$OUTPUT_DIR/sql";
    if(-d $glDir && -d $sqlDir)
    {
	printf STDERR "Generating enrichment SQL for $glDir\n";
	processPub($glDir, $sqlDir, $pub);
    }

}

sub processPub
{
    my ($glDir, $sqlDir, $pub) = @_;

    printf STDERR "Reading .gl.enrichment files in $glDir\n";

    my @files = glob("$glDir/*.gl.enrichment"); 

    my $output_file = "$sqlDir/$pub.insert-ENRICHMENT.sql";
    printf STDERR "Writing SQL files to: $output_file\n";
    open(OUTPUT, "> $output_file" ) || die "Cannot open $output_file\n";
    
    foreach my $file (@files)
    {
	print STDERR "file = $file\n";
	
### all this is for "nested models"  
### We will address this problem later.

 #   my $pub = 'BandyopadhyayGersten2007';

 #   $file =~ s/^\.\///;

#    $file = join "/", $pub, $file;

#    my @a = split(/\//, $file);
    
#    my $pub = shift @a;
    
#    $pub = shift @a;

#    my $tmp_file = join '/', @a[0..$#a];

#    my @fp = split(/[.]/, $tmp_file);

#    my $model_name = $fp[0];

	my ($volume, $dirs, $name) = File::Spec->splitpath( $file );
	if ($name !~ /(.*)\.gl\.enrichment/)
	{
	    printf STDERR "### WARNING expected .gl.enrichment file.  Found $name.\n";
	    next;
	}
	
	my $model_name = $1;
	
	my $model_id = get_model_id($get_model_id_STH, $model_name, $pub);
	print STDERR "mid = $model_id\n";    
	open(FILE, "< $file") or die "Cannot open $file: $!\n";
	
	printf OUTPUT ("INSERT INTO enrichment (%s) VALUES\n", 
		       join(",", @{$enrichment_fields}));
	
	my @values;
	while(<FILE>)
	{
	    next if(/^\#/);
	    next if(/^pval/);
	    chomp;
	    
	    my @l = split(/\t/, $_);
	    my $pval                       = $l[0];
	    my $n_genes_in_model_with_term = $l[1];
	    my $n_genes_in_model           = $l[2];
	    my $n_genes_with_term          = $l[3];
	    my $n_genes_in_GO              = $l[4];
	    my $species_id                 = $l[5];
	    my $genus                      = $l[6];
	    my $species                    = $l[7];
	    my $term_acc                   = $l[8];
	    my $term_type                  = $l[9];
	    my $term_name                  = $l[10];
	    
	    my @genes = @l[11..$#l];
	    
	    my @gene_ids   = ();
	    for my $gene (@genes)
	    {
		
#### !!!! CLOOGE: add _HUMAN as suffix for all human genes... !!!! ####
		if(($genus=~/homo/i) && ($species=~/sapiens/i) && 
		   ($gene !~ /_HUMAN$/i))
		{
		    $gene .= "_HUMAN";
		}
		
		my $gene_id = "";
		$gene_id = get_gene_id($species_id, $gene);
		
		if($gene_id eq "")
		{
		    print STDERR "### ERROR: no id returned for $gene";
		    exit;
		}
		
		push @gene_ids, $gene_id;
	    }
	    
	    $gene_ids = sprintf("'%s'", join " ", @gene_ids);
	    
	    my $term_id = get_term_id($term_acc);
	    
	    push @values, sprintf("  (%s)", join(",", 
						 $model_id, 
						 $species_id, 
						 $term_id, 
						 $n_genes_in_model_with_term, 
						 $n_genes_in_model, 
						 $n_genes_with_term, 
						 $n_genes_in_GO, 
						 $pval, 
						 $gene_ids));
	}
	close(FILE);
	
	printf OUTPUT ("%s;\n", join(",\n", @values));
    }
    
    close OUTPUT;
}

sub get_term_id
{
    my($term_acc) = @_;

    $get_term_id_STH->bind_param(1, $term_acc, {TYPE=>12});
    $get_term_id_STH->execute();

    my $row = $get_term_id_STH->fetchrow_arrayref();
    if(!defined($row))
    {
        print STDERR "ERROR:: term_id could not be retrieved "
	     . "for $term_acc\n";
	exit;
    }
    my $term_id = $row->[0];

    return $term_id;
}


sub get_gene_id
{
    my ($species_id,$gene_name) = @_;

    #print "in get_gene_id(): species_id = $species_id\tgene_name=$gene_name\n";

    $get_gene_id_from_symbol_STH->bind_param(1, $species_id, {TYPE=>4});
    $get_gene_id_from_symbol_STH->bind_param(2, $gene_name,  {TYPE=>12});
    $get_gene_id_from_symbol_STH->execute();

    my $gene_id = "";
    my $row = $get_gene_id_from_symbol_STH->fetchrow_arrayref();
    #print "row->[0] = $row->[0]\n";
    unless(defined $row){
	$get_gene_id_from_synonym_STH->bind_param(1, $species_id, {TYPE=>4});
	$get_gene_id_from_synonym_STH->bind_param(2, $gene_name,  {TYPE=>12});
	$get_gene_id_from_synonym_STH->execute();
	$row = $get_gene_id_from_synonym_STH->fetchrow_arrayref();
	unless(defined $row){
	    print STDERR "ERROR: id not found for gene=$gene_name "
		. "for species_id=$species_id\n";
	    exit;
	}
    }
    $gene_id = $row->[0];

    return $gene_id;
}


sub get_model_id
{
    my($sth, $model_name, $pub) = @_;

    $sth->bind_param(1, $model_name);
    $sth->bind_param(2, $pub);
    $sth->execute();

    my $row = $sth->fetchrow_arrayref();
    if(!defined($row))
    {
        print STDERR "ERROR:: model_id could not be retrieved "
	     . "for $model_name\n";
	exit;
    }
    my $model_id = $row->[0];

    return $model_id;
}
