package CCDB::Query;

## Purpose: 
# build the $hash data structure from a web query, where
# hash->{model_id}{"model"}      = Model object
# hash->{model_id}{"enrichment"} = Array of Enrichment objects

use strict;
use warnings;
#use DBI;
use DBI qw(:sql_types);

use CCDB::Driver;
use CCDB::DB;
use CCDB::Enrichment;
use CCDB::Sql;
use CCDB::QueryInput;


our $VERSION = '1.0';
our @ISA     = qw( CCDB::DB Exporter);

our @EXPORT = qw();
our @EXPORT_OK = qw(&get_species_string);

my $dbh = CCDB::DB::getDB();

my $get_gene_product_id_from_em_symbol_sth  = $dbh->prepare($get_gene_product_id_from_em_symbol);
my $get_gene_product_id_from_re_symbol_sth  = $dbh->prepare($get_gene_product_id_from_re_symbol);
my $get_gene_product_id_from_em_synonym_sth = $dbh->prepare($get_gene_product_id_from_em_synonym);
my $get_gene_product_id_from_re_synonym_sth = $dbh->prepare($get_gene_product_id_from_re_synonym);
my $get_from_fulltext_term_name_sth         = $dbh->prepare($get_from_fulltext_term_name);
my $get_from_term_accession_sth             = $dbh->prepare($get_from_term_accession);
my $get_from_gene_product_id_sth            = $dbh->prepare($get_from_gene_product_id);
my $get_from_eid_sth                        = $dbh->prepare($get_from_eid);

my $get_from_model_id_sth = $dbh->prepare($get_from_model_id);

my $get_model_like_sth = $dbh->prepare($get_model_like);

my $get_species_sth = $dbh->prepare($get_species);

# SPECIES_CACHE is hash-ref mapping sid to the string 'genus species'
my $SPECIES_CACHE = populate_species_cache(); 

sub get_species_string
{
    my ($sid) = @_;

    return "" if(!defined($sid));

    if(exists($SPECIES_CACHE->{$sid}))
    {
	return $SPECIES_CACHE->{$sid};
    }

    printf STDERR "Cache miss for species: $sid\n";
    
    $get_species_sth->bind_param(1, $sid);
    $get_species_sth->execute();

    if(my $Ref = $get_species_sth->fetchrow_hashref())
    {
	my ($sp, $genus) = ("", "");
	$sp = $Ref->{species} if(defined($Ref->{species}));
	$genus = $Ref->{genus} if(defined($Ref->{genus}));
	$SPECIES_CACHE->{$Ref->{id}} = join(" ", $genus, $sp);
	return join(" ", $genus, $sp);
    }

    return "Invalid Species ID: $sid";
}

sub populate_species_cache
{
    my $species_cache_sth = $dbh->prepare($get_species_cache);
    $species_cache_sth->execute();
    my %cache;

    while(my $Ref = $species_cache_sth->fetchrow_hashref())
    {
	$cache{$Ref->{id}} = join(" ", $Ref->{genus}, $Ref->{species});
    }
    
    return \%cache;
}

sub getMatchingModels
{
    my ($queryInput,        #object
	$publications, #ref-to-hash
	$species,      #ref-to-hash
	$pval_thresh,   #num
	$enrichment_limit #num
	) = @_;

    print STDERR "getMatchineModels:\n" . $queryInput->print() . "\n";;

    my $hash               = {};
    my $e_objects          = {};
    my $error_msg          = {};
    #my $gene_symbols       = {}; # gene_symbols->{gene_product_id} = symbol
    my $gid_by_gene_symbol = {}; # gid_by_gene_symbol->{ $gene_query } = $Ref->{gid};

    #### TERM ACC QUERY ####
    name_acc_query($queryInput, $queryInput->termAccession(), $get_from_term_accession_sth, $hash, $e_objects, $pval_thresh, $species, $publications, $error_msg);

    #### TERM NAME QUERY ####
    name_acc_query($queryInput, $queryInput->termName(), $get_from_fulltext_term_name_sth, $hash, $e_objects, $pval_thresh, $species, $publications, $error_msg);

    my $tmp_error = {};
    my $tmp_error_msg = {};
    for my $gene_query (keys %{ $queryInput->gene() })
    {
	my $match = 0;

	if($gene_query =~ /\*/)
	{
	    my $sym_sth = $get_gene_product_id_from_re_symbol_sth;
	    my $syn_sth = $get_gene_product_id_from_re_synonym_sth;

	    $match += get_gene_ids($sym_sth, $gene_query,
				   $species, $queryInput, 
				   $tmp_error, $tmp_error_msg, 
				   'regex', 'symbol'
				   );
	    
	    $match += get_gene_ids($syn_sth, $gene_query,
				   $species, $queryInput,
				   $tmp_error, $tmp_error_msg, 
				   'regex', 'synonym'
				   );
	    
	}
	else
	{
	    my $sym_sth = $get_gene_product_id_from_em_symbol_sth;
	    my $syn_sth = $get_gene_product_id_from_em_synonym_sth;

	    $match += get_gene_ids($sym_sth, $gene_query,
				   $species, $queryInput,
				   $tmp_error, $tmp_error_msg, 
				   'exactmatch', 'symbol'
				   );
	    
	    $match += get_gene_ids($syn_sth, $gene_query,
				   $species, $queryInput,
				   $tmp_error, $tmp_error_msg, 
				   'exactmatch', 'synonym'
				   );
	    
	}

	## e.g. query : 'crap' doesnt match any gids
	if($match == 0)
	{
	    $error_msg->{"no-match-notice"}{$gene_query}++;
	}

    }
#    return ($hash, scalar(keys %{ $hash }), $expanded_query, $error_msg, $gid_by_gene_symbol );

    model_id_query($queryInput, 
		   $enrichment_limit,
		   $e_objects,
		   $hash, 
		   $error_msg);
    
    model_like_query($queryInput, 
		     $enrichment_limit,
		     $e_objects,
		     $hash, 
		     $error_msg);
    
    gene_query($queryInput,
	       $get_from_gene_product_id_sth,
	       $pval_thresh,
	       $publications,
	       $species,
	       $gid_by_gene_symbol,
	       $e_objects,
	       $hash,
	       $tmp_error_msg,
	       $error_msg);
    
    CCDB::Driver::inspect_results($hash,'');

    #$dbh->disconnect();
    
    return ($hash, scalar(keys %{ $hash }), $error_msg, $gid_by_gene_symbol );
}

sub model_like_query
{
  my ($queryInput, 
      $enrichment_limit,
      $e_objects,
      $hash, 
      $error_msg) = @_;

  my $sth = $get_model_like_sth;
  foreach my $queryMid (keys %{ $queryInput->modelLike() })
  {
      print STDERR "$queryMid: models_like\n";
      #print STDERR "$get_model_like\n";

      $sth->bind_param(1,$queryMid); # TYPE=SQL_INTEGER
      $sth->bind_param(2,$queryMid); # TYPE=SQL_INTEGER
      $sth->execute();
      
      #printf STDERR "$gid: %d rows\n", $sth->rows;
      #printf STDERR "$gid: SQL\n"; 
      #printf STDERR "   " . $dbh->{Statement} . "\n";

      my $count = 0;
      my ($id_a, $id_b);
      while(my $Ref = $sth->fetchrow_hashref())
	{
	    $id_a = $Ref->{model_id_a};
	    $id_b = $Ref->{model_id_b};
	    
	    #printf STDERR ("MODELS_LIKE: %s %s %d\n", $id_a, $id_b, $Ref->{gene_score});
	    $queryInput->modelId()->{$id_a} = $Ref->{gene_score} if($id_a != $queryMid);
	    $queryInput->modelId()->{$id_b} = $Ref->{gene_score} if($id_b != $queryMid);
	    $count++;
	}
      
      #### ERROR HANDLING ####
      if($count == 0)
      {
	  $error_msg->{"no-model-like-notice"}{$queryMid}++;
      }
      else
      {
	  $queryInput->modelId()->{$queryMid}++; ## Add the queryied model to the list of matches
	  model_id_query($queryInput,
			 $enrichment_limit,
			 $e_objects,
			 $hash,
			 $error_msg);
      }
  }
    
  return;
}

sub model_id_query
{
  my ($queryInput, 
      $enrichment_limit,
      $e_objects,
      $hash, 
      $error_msg) = @_;

  print STDERR "model_id:\n" . $queryInput->print() . "\n";

  my $sth = $get_from_model_id_sth;
  foreach my $mid (keys %{ $queryInput->modelId() })
  {
      print STDERR "$mid: model_id\n";
      print STDERR "$mid: limit=$enrichment_limit\n";
      
      $sth->bind_param(1,$mid); # TYPE=SQL_INTEGER
      $sth->bind_param(2,$enrichment_limit); # TYPE = SQL_INTEGER
      $sth->execute();
      
      my $matched = 0;
      while(my $Ref = $sth->fetchrow_hashref())
	{
	  $matched++;
	  
	  my $sid = $Ref->{sid}; # species id
	  my $tid = $Ref->{tid}; # term id
	  
	  unless( exists $e_objects->{$mid}{"enrichment"}{$sid}{$tid} )
	  {
	      $e_objects->{ $mid }{'enrichment'}{ $sid }{ $tid }++;
	      push @{ $hash->{ $mid }{'enrichment'} }, CCDB::Enrichment::populate_enrichment($Ref);
	  }
	  
	  if(! exists($hash->{$mid}{'enrichment'}))
	  {
	      $hash->{$mid}{'enrichment'} = [];
	  }
	  unless(exists $hash->{ $mid }{'model'})
	  {
	      $hash->{ $mid }{'model'} = CCDB::Model::populate_model($Ref);
	  }
	}
      
	#### ERROR HANDLING ####
	if($matched == 0)
	{
	    $error_msg->{"no-model-match-notice"}{$mid}++;
	}
    }
    
    return;
}

#
# return: The number of gene_ids matched
#
sub get_gene_ids
{
    my ($sth,
	$gene_query,
	$species, 
	$queryInput,
	$tmp_error,
	$tmp_error_msg,
	$opt1, $opt2) = @_;

    my $match = 0;
    my $original_gene_query = $gene_query;
    if($opt1 eq 'regex')
    {
	$gene_query =~ s/\*/.*/g;
    }

    $sth->bind_param(1,$gene_query);
    $sth->execute();

    while(my $Ref = $sth->fetchrow_hashref())
    {
	my $genus_species_str = get_species_string($Ref->{sid});

	#printf STDERR ("### get_gene_ids: sid=%s gid=%s symbol=%s ss=%s\n", 
	#	       $Ref->{sid},
	#	       $Ref->{gid},
	#	       $Ref->{symbol},
	#	       $genus_species_str);
	
	### filter by species constraint

	next unless(exists $species->{$genus_species_str});

	my $gid = $Ref->{gid};

	$match++;
	$queryInput->geneId2Symbol()->{$gid} = $Ref->{symbol};
	$queryInput->geneSymbol2Id()->{$Ref->{symbol}} = $gid;
	
	if($opt2 eq 'synonym')
	{
	    unless( uc($Ref->{symbol}) eq uc($Ref->{synonym}) )
	    {
		if($opt1 eq 'exactmatch')
		{
		    next if(exists $tmp_error->{$gid});
		    $tmp_error->{$gid}++;
			
		    my $h = {
			query   => $gene_query,
			org     => $genus_species_str,
			symbol  => $Ref->{symbol},
			synonym => $Ref->{synonym}
		    };
		    push @{ $tmp_error_msg->{"exactmatch-synonym-notice"}{$gid} }, $h;
		}
		else
		{
		    if(exists $tmp_error->{$gid}){
			#print "tmp_error->{$gid} already exists<br>";
			$tmp_error->{$gid}++; next;
		    }
		    $tmp_error->{$gid}++;
		    my $h = {
			query   => $original_gene_query,
			org     => $genus_species_str,
			symbol  => $Ref->{symbol},
			synonym => $Ref->{synonym}
		    };
		    #print "gid: $gid<br>";
		    #print "original_gene_query : $original_gene_query<br>";
		    #print "org : $genus_species_str<br>";
		    #print "sym: $Ref->{symbol}<br><br>";
		    #print "tmp_error_msg->{'regex-synonym-notice'}{$gid}<br>";
		    push @{ $tmp_error_msg->{"regex-synonym-notice"}{$gid} }, $h;
		}
	    }
	}
    }

    if(0){
    foreach my $k (keys %{ $tmp_error_msg }){
	foreach my $k2 (keys %{ $tmp_error_msg->{$k} }){
	    for my $i (0..$#{ $tmp_error_msg->{$k}{$k2} }){
		my $h = ${ $tmp_error_msg->{$k}{$k2} }[$i];
		my $query = $h->{query};
		my $org   = $h->{org};
		my $sym   = $h->{symbol};
		my $syn   = $h->{synonym};
		print "query : $query<br>";
		print "org   : $org<br>";
		print "sym   : $sym<br>";
		print "syn   : $sym<br><br>";
	    }
	}
    }}


    return $match;
}


sub gene_query
{
    my ($queryInput,
	$sth,
	$pval_thresh,
	$publications,
	$species,
	$gid_by_gene_symbol,
	$e_objects,
	$hash,
	$tmp_error_msg,
	$error_msg
	) = @_;

    printf STDERR "gene_query:\n" . $queryInput->print() . "\n";
    
    foreach my $gid (keys %{ $queryInput->geneId2Symbol() })
    {
	print STDERR "$gid: query by gene id\n";
	print STDERR "$gid: pval=$pval_thresh\n";

	$queryInput->expandedQuery()->{$queryInput->geneId2Symbol()->{$gid}}++;

	printf STDERR "gene_query2:\n" . $queryInput->print() . "\n";

	$sth->bind_param(1,$pval_thresh); # TYPE = SQL_VARCHAR
	$sth->bind_param(2,$gid); # TYPE=SQL_INTEGER
	$sth->execute();

	#print STDERR "$gid: execute done\n";
	#if(defined($sth->errstr))
	#{
	#    print STDERR "ERROR: " . $sth->errstr;
	#}
	    

	#printf STDERR "$gid: %d rows\n", $sth->rows;
	#printf STDERR "$gid: SQL\n"; 
	#printf STDERR "   " . $dbh->{Statement} . "\n";
	my $matched = 0;
	while(my $Ref = $sth->fetchrow_hashref())
	{
	    #print STDERR "$gid: found row $matched\n";

	    ### filter by contraints (pval, species, publications) ###
	    next if($Ref->{e_pval} > $pval_thresh);

	    my $sid = $Ref->{sid};

	    my $genus_species_str = get_species_string($sid);
	    next unless(exists $species->{$genus_species_str});
	    next unless(exists $publications->{$Ref->{mpub}});
	    
	    $matched++;

	    my $mid = $Ref->{mid};
	    my $tid = $Ref->{tid};

	    $gid_by_gene_symbol->{ $mid }{ $genus_species_str }{ $tid }{ $queryInput->geneId2Symbol()->{$gid} } = $gid;

	    unless( exists $e_objects->{$mid}{"enrichment"}{$sid}{$tid} )
	    {

		### dont make enrichment object if the gene ($gid) is not 
		### annotated to term ($tid) in species ($sid)
		next if($Ref->{e_gids} !~ /$gid/);

		$e_objects->{ $mid }{'enrichment'}{ $sid }{ $tid }++;

		push @{ $hash->{ $mid }{'enrichment'} }, CCDB::Enrichment::populate_enrichment($Ref);

	    }

	    ### if we didnt make an enrichment object for this gene, 
	    ### we certainly wont make a model object for it
	    next unless(exists $hash->{ $mid }{'enrichment'});

	    unless(exists $hash->{ $mid }{'model'})
	    {
		$hash->{ $mid }{'model'} = CCDB::Model::populate_model($Ref);

	    }
	}

	#### ERROR HANDLING ####
	if($matched == 0)
	{
	    $error_msg->{"no-match-notice"}{$queryInput->geneId2Symbol()->{$gid}}++;
	}
	else
	{
	    if( exists $tmp_error_msg->{"exactmatch-synonym-notice"}{$gid} )
	    {
		for my $i (0..$#{ $tmp_error_msg->{"exactmatch-synonym-notice"}{$gid} })
		{
		    my $h = ${ $tmp_error_msg->{"exactmatch-synonym-notice"}{$gid} }[$i];
		    push @{ $error_msg->{"exactmatch-synonym-notice"}{$gid} }, $h;
		}
	    }
	    if( exists $tmp_error_msg->{"regex-synonym-notice"}{$gid} )
	    {

		for my $i (0..$#{ $tmp_error_msg->{"regex-synonym-notice"}{$gid} })
		{
		    my $h = ${ $tmp_error_msg->{"regex-synonym-notice"}{$gid} }[$i];
		    push @{ $error_msg->{"regex-synonym-notice"}{$gid} }, $h;
		}

	    }
	    
	}
    }
    
    return;
}

sub name_acc_query
{
    my ($queryInput, 
	$queryTermHash,
	$sth, 
	$hash, 
	$e_objects, 
	$pval_thresh,
	$species,
	$publications,
	$error_msg) = @_;

    foreach my $q (keys %{$queryTermHash})
    {

	$queryInput->expandedQuery()->{$q}++;
	$sth->bind_param(1,$pval_thresh);
	$sth->bind_param(2,$q);
	$sth->execute();

	my $found = {};

	while(my $Ref = $sth->fetchrow_hashref())
	{

	    ### filter by contraints (pval, species, publications) ###
	    next if($Ref->{e_pval} > $pval_thresh);

	    my $sid = $Ref->{sid};

	    my $genus_species_str = get_species_string($sid);

	    next unless(exists $species->{$genus_species_str});
	    next unless(exists $publications->{$Ref->{mpub}});

	    $found->{$q}++;

	    my $mid = $Ref->{mid};
	    my $tid = $Ref->{tid};

	    ### if we havent yet made this model ($mid) containing the matched term 
	    ### name or accession, we make it
	    unless( exists $hash->{ $mid }{'model'} )
	    {

		$hash->{$mid}{'model'} = CCDB::Model::populate_model($Ref);

	    }

	    ### if we havent yet made an enrichment object for this term ($tid) in 
	    ### the model($mid) for this species ($sid), then we make it
	    unless( exists $e_objects->{$mid}{'enrichment'}{$sid}{$tid} )
	    {

		$e_objects->{ $mid }{'enrichment'}{ $sid }{ $tid }++;

		push @{ $hash->{ $mid }{'enrichment'} }, CCDB::Enrichment::populate_enrichment($Ref);

	    }

	}
	if(scalar(keys %{ $found }) == 0)
	{
	    
	    $error_msg->{'no-match-notice'}{$q}++;
	    
	}
    }

    return;
}


sub get_enrichment_object_by_eid
{
    my ($eid) = @_;
    
    my $sth = $get_from_eid_sth;

    $sth->bind_param(1, $eid);
    $sth->execute();	

    my $Ref = $sth->fetchrow_hashref();
    my $eo = CCDB::Enrichment::populate_enrichment($Ref);
    
    return $eo;
}

1;
