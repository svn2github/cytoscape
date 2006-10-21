#!/usr/bin/perl -w

use strict;
use DBI;

my $ccdev = 'mysql -u mdaly --password=mdalysql cellcircuits_dev';

my $usage=<<USG;
 usage: $0 <db-name>

    e.g. $0 cellcircuits_dev

USG

die $usage if(@ARGV != 1);


my $server   = 'localhost';
my $db       = shift @ARGV;
my $username = 'mdaly';
my $password = 'mdalysql';

my $dbh = DBI->connect("dbi:mysql:$db", $username, $password);

#######################################################################
# INDEXES - (table optimizations)
#
# 1.  create unique index gene_product_idx 
#      on gene_product ( id, symbol, species_id );
#
# 2.  create unique index species_idx on species ( id, genus, species );
# 
# 3.  create unique index gene_product_synonym_idx 
#      on gene_product_synonym ( gene_product_id, product_synonym );
#######################################################################

my $get_gene_product_id_from_gene_synonym = qq{
SELECT
 gene_product.id
FROM
 gene_product, gene_product_synonym, species
WHERE
 gene_product.id = gene_product_synonym.gene_product_id AND
 gene_product.species_id              = species.id AND
 species.genus                        = ? AND
 species.species                      = ? AND
 gene_product_synonym.product_synonym = ?
};
my $get_gene_product_id_from_gene_synonym_STH =
    $dbh->prepare($get_gene_product_id_from_gene_synonym);

my $get_gene_product_id_from_gene_symbol = qq{
SELECT
 gene_product.id
FROM
 gene_product, species
WHERE
 gene_product.species_id = species.id AND
 species.genus           = ? AND
 species.species         = ? AND
 gene_product.symbol     = ?
};
my $get_gene_product_id_from_gene_symbol_STH =
    $dbh->prepare($get_gene_product_id_from_gene_symbol);


my $get_model_id = qq{
SELECT model.id
FROM   model
WHERE  model.pub = ? AND model.name = ?
};
my $get_model_id_STH = 
    $dbh->prepare($get_model_id);

#########

my $get_species_id = qq{
SELECT id
FROM species
WHERE species.genus = ? AND species.species = ?
};
my $get_species_id_STH = $dbh->prepare($get_species_id);

#########

my $get_term_id_from_term_name = qq{
SELECT id FROM term WHERE name = ?
};
my $get_term_id_from_term_name_STH = 
    $dbh->prepare($get_term_id_from_term_name);

my @unknown_term_names = (
			  'biological process unknown',
			  'molecular function unknown',
			  'cellular component unknown'
			  );

my $term_name_by_term_id = {};
for my $utn (@unknown_term_names)
{
    $get_term_id_from_term_name_STH->bind_param(1,$utn,{TYPE=>12});
    $get_term_id_from_term_name_STH->execute();
    my $tid;
    my $row = $get_term_id_from_term_name_STH->fetchrow_arrayref();
    if(scalar(@$row) > 1){ 
	print STDERR "ERROR: more than term.id corresponds "
	    . "to term.name = $utn\n";  exit;
    }
    elsif(scalar(@$row) == 0){ 
	print STDERR "ERROR: no term.id corresponds "
	    . "to term.name = $utn\n"; exit;
    }
    else{ 
	$tid = $row->[0]; 
	$term_name_by_term_id->{$tid}++;
    }
}


#########

my $get_last_index_of_gene_product_table = qq{
SELECT COUNT(*) FROM gene_product
};
my $get_last_index_of_gene_product_table_STH =
    $dbh->prepare($get_last_index_of_gene_product_table);
$get_last_index_of_gene_product_table_STH->execute();
my $gp_row = $get_last_index_of_gene_product_table_STH->fetchrow_arrayref();
my $last_gene_product_idx = $gp_row->[0];
undef($gp_row);

#########
##
##  select MAX(dbxref)... is really BAD!!!!!  not flexible!!!!!
##
#########
my $get_last_index_of_dbxref_table = qq{
SELECT MAX(dbxref_id) FROM gene_product
};
my $get_last_index_of_dbxref_table_STH =
    $dbh->prepare($get_last_index_of_dbxref_table);
$get_last_index_of_dbxref_table_STH->execute();
my $db_row = $get_last_index_of_dbxref_table_STH->fetchrow_arrayref();
my $last_dbxref_idx = $db_row->[0];
undef($db_row);

#########

my %imgFormat = 
    (
     Begley2002_MCR             => "jpg",
     Bernard2005_PSB            => "jpg",
     de_Lichtenberg2005_Science => "jpg",
     Gandhi2006_NG              => "jpg",
     Hartemink2002_PSB          => "jpg",
     Haugen2004_GB              => "jpg",
     Ideker2002_BINF            => "jpg",
     Kelley2005_NBT             => "png",
     Sharan2005_PNAS            => "png",
     Suthram2005_Nature         => "jpg",
     Yeang2005_GB               => "gif",
     );

my @pubs = qw(
	      BandyopadhyayGersten2007
	      );
	      #Begley2002_MCR
	      #Bernard2005_PSB
	      #de_Lichtenberg2005_Science
	      #Gandhi2006_NG
	      #Hartemink2002_PSB
	      #Haugen2004_GB
	      #Ideker2002_BINF
	      #Kelley2005_NBT
	      #Sharan2005_PNAS
	      #Suthram2005_Nature
	      #Yeang2005_GB
	      #);


print STDERR "Here...1\n";

#my $errorlog = "insert_into_gene_model.missing_genes.log";
#open(ERROR_LOG, "> $errorlog") or die "Cannot open $errorlog: $!\n";


my $insert_sql_FILE = "insert_into_gene_model." . $$ . ".sql";
open INSERT, "> $insert_sql_FILE" or die "Cannot open $insert_sql_FILE: $!\n";

my $gene_model_sql = 'INSERT INTO gene_model (model_id, gene_product_id)';



my $sifList_path = "/cellar/users/mdaly/cellcircuits/trunk/data";

my $c = 0;

my $missing = {};

###########################################################
## 1st pass
## 
## collect missing genes into %{ $missing }
##
##
foreach my $pub (@pubs){

    my $sifList = "$sifList_path/$pub/sifList";

    print STDERR "sifList = $sifList\n";

    open(LIST_FILE, "< $sifList") or die "Cannot open $sifList: $!\n";
    while(<LIST_FILE>)
    {
	$c++;

	chomp; s/^[.]\/sif\///;
	
	my @line = split(/\t/);

	my $name     = $line[0]; $name =~ s/[.]sif$//;

	my $organism = $line[1];

	my $og = {}; # og->{organism}{gene}

	my $sif = $sifList_path . "/$pub/sif/$name.sif";

	parse_sif($sif,$organism, $og);

	my $model_id = get_model_id($pub,$name);
	
	foreach my $org (keys %{ $og })
	{

	    foreach my $gene (keys %{ $og->{$org} })
	    {

		my $gene_product_id = get_gene_product_ids_from_gene_name($gene,$org,$pub,$name); 

		if($gene_product_id == -1)
		{
		    
		    #print ERROR_LOG "\t$pub ${name}.sif $gene not in database ($org)\n"; 

		    # _HUMAN is added here, so later we find it on the 2nd pass 
		    #   (further down in this script in a different loop labeled
		    #    2nd pass in the comment above it).
		    # the _HUMAN suffix is a GO convention.
		    if($org =~ /homo sapiens/i)
		    {

			$gene .= '_HUMAN';

		    }

		    $missing->{$org}{$gene}++;

		    next;

		}
	    }
	}
    }

    close LIST_FILE;
    
}


print "Here...Finished collecting missing genes\n";


my $tmp_gp_insert = "tmp.GP.$$";
open GP, "> $tmp_gp_insert" or die "Cannot open $tmp_gp_insert: $!\n";

my $insert_into_gene_product_sql = "INSERT INTO gene_product (symbol,dbxref_id,species_id) VALUES ";

foreach my $org (sort keys %{ $missing } )
{
    my ($genus,$species) = split(/\s/,$org);

    my $species_id = get_species_id($genus,$species);

    foreach my $gene (sort keys %{ $missing->{$org} } )
    {

	$last_dbxref_idx++;

	print GP $insert_into_gene_product_sql, "(\"$gene\",$last_dbxref_idx,$species_id);\n";

    }

}

close GP;

#print STDERR "cat $tmp_gp_insert > tmp.gp\n";
#print        `cat $tmp_gp_insert > tmp.gp\n`;


#print "cat $tmp_gp_insert\n";
#print `cat $tmp_gp_insert\n`;
#exit;

print STDERR "cat $tmp_gp_insert | $ccdev\n";
print        `cat $tmp_gp_insert | $ccdev\n`;

print STDERR "rm $tmp_gp_insert\n";
print        `rm $tmp_gp_insert\n`;


my $tmp_assoc_insert = "tmp.ASSOC.$$";
open ASSOC, "> $tmp_assoc_insert" or die "Cannot open $tmp_assoc_insert: $!\n";

my $insert_into_association_sql = "INSERT INTO association (term_id, gene_product_id) VALUES ";

foreach my $org (sort keys %{ $missing } )
{

    my ($genus,$species) = split(/\s/,$org);

    my $species_id = get_species_id($genus,$species);

    foreach my $gene (sort keys %{ $missing->{$org} } )
    {

	my $gid = get_gene_product_ids_from_gene_name($gene, $org, '','');

	foreach my $term_id (keys %{ $term_name_by_term_id })
	{

	    print ASSOC $insert_into_association_sql, "($term_id,$gid);\n";

	}

    }

}

print STDERR "\n\n";

#print STDERR "cat $tmp_assoc_insert > tmp.assoc\n";
#print        `cat $tmp_assoc_insert > tmp.assoc\n`;

print STDERR "cat $tmp_assoc_insert | $ccdev\n";
print        `cat $tmp_assoc_insert | $ccdev\n`;

print STDERR "rm $tmp_assoc_insert\n";
print        `rm $tmp_assoc_insert\n`;


print STDERR "\n\n2nd pass -- inserting into gene_model\n\n";


###########################################################
## 2nd pass
##
## there should now be no missing genes,
##  so now we can generate insert_into_gene_model.sql
##  and populate the gene_model table.
##
##
foreach my $pub (@pubs){

    my $sifList = "$sifList_path/$pub/sifList";

    open(LIST_FILE, "< $sifList") or die "Cannot open $sifList: $!\n";
    while(<LIST_FILE>){
	$c++;

	chomp; s/^[.]\/sif\///;
	
	my @line = split(/\t/);

	my $name     = $line[0]; $name =~ s/[.]sif$//;

	my $organism = $line[1];

	my $og = {}; # og->{organism}{gene}

	my $sif = $sifList_path . "/$pub/sif/$name.sif";

	parse_sif($sif,$organism, $og);

	my $model_id = get_model_id($pub,$name);
	
	foreach my $org (keys %{ $og })
	{
	    foreach my $gene (keys %{ $og->{$org} })
	    {

		my $gene_product_id = get_gene_product_ids_from_gene_name($gene,$org,$pub,$name); 

		if($gene_product_id == -1)
		{
		    
		    print STDERR "\t$pub ${name}.sif $gene not in database ($org)\n"; exit;

		}
		else
		{

		    print INSERT $gene_model_sql, " VALUES ($model_id, $gene_product_id);\n";
		    
		}
	    }
	}
    }

    close LIST_FILE;
    
}

close INSERT;


my $create_sql_FILE = "create_gene_model.sql";

print STDERR "cat ./sql/$create_sql_FILE | $ccdev\n";
print        `cat ./sql/$create_sql_FILE | $ccdev\n`;

print        `cat $insert_sql_FILE | $ccdev\n`;
print STDERR "cat $insert_sql_FILE | $ccdev\n";

print `mv $insert_sql_FILE ./sql/insert_into_gene_model.sql\n`;
print `chmod 666 ./sql/insert_into_gene_model.sql\n`;




#######################################




sub parse_sif
{
    my ($sif,$organism, $og) = @_;

    open(SIF, "< $sif") or die "Cannot open $sif: $!\n";
    while(<SIF>)
    {
	chomp;
	my @l = split(/\t/);
	
	if(scalar(@l) == 3){
	    my ($gene1,$gene2) = ($l[0],$l[2]);
	    if($organism =~ /\|/) {
		my @orgs   = split(/\|/,$organism);
		my @genes1 = split(/\|/,$gene1);
		my @genes2 = split(/\|/,$gene2);
		for my $i (0..$#genes1){
		    $og->{$orgs[$i]}{$genes1[$i]}++; 
		    $og->{$orgs[$i]}{$genes2[$i]}++; 
		}
	    }
	    else { 
		$og->{$organism}{$gene1}++;
		$og->{$organism}{$gene2}++;
	    }
	}
	elsif(scalar(@l) == 1){
	    my $gene = $l[0];
	    if($organism =~ /\|/) {
		my @orgs  = split(/\|/, $organism);
		my @genes = split(/\|/,$gene);
		for my $i (0..$#genes){ $og->{$orgs[$i]}{$genes[$i]}++; }
	    }
	    else { $og->{$organism}{$gene}++; }
	}
	else{
	    die "scalar(@l) != 3 or 1...  check $sif @ line $.\n";
	}
    }
    close(SIF);

    return;
}

sub get_gene_product_ids_from_gene_name
{
    my ($gene,$organism,$publication,$model_name) = @_;

    chomp $organism;
    my @org     = split(/\s/,$organism);
    my $genus   = $org[0];
    my $species = $org[1];

    if($organism =~ /homo sapiens/i){$gene .= "_HUMAN";}

    $get_gene_product_id_from_gene_symbol_STH->bind_param(1,$genus,  {TYPE=>12});
    $get_gene_product_id_from_gene_symbol_STH->bind_param(2,$species,{TYPE=>12});
    $get_gene_product_id_from_gene_symbol_STH->bind_param(3,$gene,   {TYPE=>12});
    $get_gene_product_id_from_gene_symbol_STH->execute();

    my $row = $get_gene_product_id_from_gene_symbol_STH->fetchrow_arrayref;
    if(!defined $row){
	$get_gene_product_id_from_gene_synonym_STH->bind_param(1,$genus,  {TYPE=>12});
	$get_gene_product_id_from_gene_synonym_STH->bind_param(2,$species,{TYPE=>12});
	$get_gene_product_id_from_gene_synonym_STH->bind_param(3,$gene,   {TYPE=>12});
	$get_gene_product_id_from_gene_synonym_STH->execute();
	$row = $get_gene_product_id_from_gene_synonym_STH->fetchrow_arrayref;
	if(!defined $row){
	    #print STDERR "ERROR: no models returned for "
	#	. "publication=$publication\tmodel_name=$model_name "
	#	. "genus=$genus\tspecies=$species\tgene=$gene\n"; 
	    return -1;# exit;
	}
	elsif(scalar(@$row) > 1) { 
	    print STDERR "ERROR: more than one gene_product_id returned for "
		. "publication=$publication\tmodel_name=$model_name "
		. "genus=$genus\tspecies=$species\tgene=$gene\n"; exit;
	}
    }
    elsif(scalar(@$row) > 1) { 
	print STDERR "ERROR: more than one gene_product_id returned for "
	    . "publication=$publication\tmodel_name=$model_name "
	    . "genus=$genus\tspecies=$species\tgene=$gene\n"; exit;
    }

    my $gene_product_id = $row->[0];

    return $gene_product_id;
}

sub get_model_id
{
    my ($publication,$model_name) = @_;
    my $model_id;

    $get_model_id_STH->bind_param(1, $publication, {TYPE=>12});
    $get_model_id_STH->bind_param(2, $model_name,  {TYPE=>12});
    $get_model_id_STH->execute();

    my $row = $get_model_id_STH->fetchrow_arrayref;
    if(scalar(@$row) > 1) { 
	print STDERR "ERROR: more than one model_id returned for "
	    . "publication=$publication model_name=$model_name\n"; exit;
    }
    elsif(scalar(@$row) == 0){
	print STDERR "ERROR: no models returned for "
	    . "publication=$publication model_name=$model_name\n"; exit;
    }
    else{ $model_id = $row->[0]; }

    return $model_id;
}

sub get_species_id
{
    my ($genus,$species) = @_;

    $get_species_id_STH->bind_param(1, $genus,   {TYPE=>12});
    $get_species_id_STH->bind_param(2, $species, {TYPE=>12});
    $get_species_id_STH->execute();

    my $species_id;
    my $row = $get_species_id_STH->fetchrow_arrayref();
    if(scalar(@$row) > 1){ 
	print STDERR "ERROR: more than one species id corresponds "
	    . "to genus=$genus species=$species\n";  exit;
    }
    elsif(scalar(@$row) == 0){ 
	print STDERR "ERROR: no species id corresponds "
	    . "to genus=$genus species=$species\n"; exit;
    }
    else{ 
	$species_id = $row->[0]; 
    }

    return $species_id;
}

$get_gene_product_id_from_gene_symbol_STH->finish();
$get_gene_product_id_from_gene_synonym_STH->finish();
$get_model_id_STH->finish();
$get_species_id_STH->finish();
$get_term_id_from_term_name_STH->finish();
$get_last_index_of_gene_product_table_STH->finish();
$get_last_index_of_dbxref_table_STH->finish();

$dbh->disconnect();

