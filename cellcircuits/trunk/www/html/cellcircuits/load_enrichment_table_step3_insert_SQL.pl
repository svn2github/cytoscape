#! /usr/bin/perl

use File::Basename;

my $usage=<<USG;
 usage: $0 <pub_id>

USG
die $usage if(@ARGV != 1);

my $pub_id = shift @ARGV;

#use lib '/cellar/users/pwang/cc_cgi'; # for DB connection
use lib '../../../cgi-bin/cellcircuits'; # for DB connection

use CCDB::DB;
my $dbh = CCDB::DB::getDB();

print "pub_id = $pub_id\n";

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

my $find_cmd = `find ./_tmpGL_results -name "*.enrichment"\n`;

my @files = split(/\n/, $find_cmd);


foreach my $file (@files)
{
    #print  "file = $file\n";
    $file =~ s/^\.\///;
    my @a = split(/\//, $file);
    my $filename = shift @a;
	$filename = shift @a;
	
	my @b = split(/\./, $filename);
	my $basename = shift @b;
	
    my $model_id = get_model_id($pub_id, $dbh, $basename); # basename actually is the 'id' in table 'network_files'
    
    open(FILE, "< $file") or die "Cannot open $file: $!\n";
	
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
			if(($genus=~/homo/i) && ($species=~/sapiens/i))
			{
				$gene .= "_HUMAN";
			}
	
			my $gene_id = "";
			$gene_id = get_gene_id($species_id, $gene);
	
			#print "gene=$gene \t $gene_id = $gene_id\n";
	
			if($gene_id eq "")
			{
				print STDERR "ERROR: no id returned for $gene";
				exit;
			}
	
			push @gene_ids, $gene_id;
		}
	
		$gene_ids = join " ", @gene_ids;
		$gene_ids = "'$gene_ids'";
	
		my $term_id = get_term_id($term_acc);
	
		my @data = ();
		push @data, $model_id, $species_id, $term_id, 
					$n_genes_in_model_with_term, 
					$n_genes_in_model, $n_genes_with_term, 
					$n_genes_in_GO, $pval, $gene_ids;
		
		my $dbQuery = sprintf "INSERT INTO enrichment (%s) VALUES (%s)", 
		join(",", @{$enrichment_fields}), join(",", @data);
		
		#print $dbQuery, "\n";
		
		my $sth = $dbh->prepare($dbQuery);
    	$sth->execute();
    }
    close(FILE);
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
    my($pub_id, $dbh, $network_file_id) = @_;

	my $dbQuery = "select file_name from network_files where id = $network_file_id";
	
	my $sth = $dbh->prepare($dbQuery);
    $sth->execute();

    my $row = $sth->fetchrow_arrayref();
    if(!defined($row))
    {
        print  "ERROR:: network_file_id could not be retrieved "
	     . "for $network_file_id\n";
		exit;
    }
    my $file_name = $row->[0];
	#print "file_name = $file_name\n";
	#my @b = split(/\./, $file_name);
	#my $model_name = shift @b;
	my $model_name = substr($file_name,0, length($file_name)-4); # remove the .sif extension
	#print "model_name = $model_name\n";
	
	my $dbQuery = "select id from model where pub=$pub_id and name = '$model_name'";
	
	my $sth = $dbh->prepare($dbQuery);
    $sth->execute();
	
    $row = $sth->fetchrow_arrayref();
    if(!defined($row))
    {
        print  "ERROR:: model_id could not be retrieved "
	     . "for $model_name\n";
		exit;
    }
    my $model_id = $row->[0];
	
    return $model_id;
}
