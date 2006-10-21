#! /usr/bin/perl -w

use strict;

my $ccdev = 'mysql -u mdaly --password=mdalysql cellcircuits_dev';

my $usage=<<USG;
 usage: $0 <db-name>

    e.g. $0 cellcircuits_dev

USG

die $usage if(@ARGV != 1);

my $dbname = shift @ARGV;


## this block is specific to updating the database with a new GO version

my $gwt_file = "genes_with_term.tab";
my $n_genes_beneath_file = "n_genes_beneath_BY_GO_term_accession.tab";

if(0){    
#print STDERR "\n\nNOTE: Loading the enrichment table takes 5-6 hours!!\n\n";
#print STDERR "./precompute_genes_with_term.pl > genes_with_term.tab\n";
#print        `./precompute_genes_with_term.pl > genes_with_term.tab\n`;
    
    my $tmp_out = "n_genes_beneath_tmp";
    open TMP_OUT, "> $tmp_out" or die "Cannot open $tmp_out: $!\n";
    
    open F, "< $gwt_file" or die "Cannot open $gwt_file: $!\n";
    while(<F>)
    {
	next if($. == 1);
	chomp;
	my @l = split(/\t/);
	my $term_acc        = $l[5];
	my $n_genes_beneath = $l[7];
	my $genus           = $l[1];
	my $species         = $l[2];
	
	my $organism = join " ", $genus, $species;
	
	printf TMP_OUT "%s\n", join "\t", $term_acc, $n_genes_beneath, $organism;
	
    }
    close F;

    print STDERR "sort $tmp_out > $n_genes_beneath_file\n";
    print        `sort $tmp_out > $n_genes_beneath_file\n`;

    print STDERR "rm $tmp_out\n";
    print        `rm $tmp_out\n`;

#exit;
}

#my @pubs = qw(
#	      Begley2002_MCR
#	      Bernard2005_PSB
#	      de_Lichtenberg2005_Science
#	      Gandhi2006_NG
#	      Hartemink2002_PSB
#	      Haugen2004_GB
#	      Ideker2002_BINF
#	      Kelley2005_NBT/between_pathway/full
#	      Kelley2005_NBT/between_pathway/restricted
#	      Kelley2005_NBT/within_pathway
#	      Sharan2005_PNAS/pairwise-cd
#	      Sharan2005_PNAS/pairwise-yc
#	      Sharan2005_PNAS/pairwise-yd
#	      Sharan2005_PNAS/three-way
#	      Suthram2005_Nature
#	      Yeang2005_GB
#	      );

my @pubs = qw( BandyopadhyayGersten2007 );


my $pubs_list = join " ", @pubs;

#print STDERR "./compute_enrichment.pl cellcircuits_dev $n_genes_beneath_file $pubs_list\n";
#print        `./compute_enrichment.pl cellcircuits_dev $n_genes_beneath_file $pubs_list\n`;


my $dir         = '/cellar/users/mdaly/cellcircuits/trunk/db';
my $results_dir = join "/", $dir, 'results';
my $sql_dir     = join "/", $dir, 'sql';


my $insert_into_enrichment_sql_FILE = 'insert_into_enrichment.sql.Bandyo';

chdir "$results_dir" or die "Cannot cd to $results_dir: $!\n";

print STDERR "./generate_enrichment_insert_SQL.pl cellcircuits_dev > $insert_into_enrichment_sql_FILE\n";
print        `./generate_enrichment_insert_SQL.pl cellcircuits_dev > $insert_into_enrichment_sql_FILE\n`;

print STDERR "mv $insert_into_enrichment_sql_FILE ../sql\n";
print        `mv $insert_into_enrichment_sql_FILE ../sql\n`;



chdir "$sql_dir" or die "Cannot cd to $sql_dir: $!\n";

#print STDERR "cat create_enrichment.sql | $ccdev\n";
#print        `cat create_enrichment.sql | $ccdev\n`;

print STDERR "cat $insert_into_enrichment_sql_FILE | $ccdev\n";
print        `cat $insert_into_enrichment_sql_FILE | $ccdev\n`;
