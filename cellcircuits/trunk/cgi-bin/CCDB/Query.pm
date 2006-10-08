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

our $VERSION = '1.0';
our @ISA     = qw( CCDB::DB );

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

sub getMatchingModels
{
    my ($query,        #str
	$gq,           #ref-to-hash
	$tnq,          #ref-to-hash
	$taq,          #ref-to-hash
	$modelIdQuery,   #ref-to-hash
	$modelLikeQuery, #ref-to-hash
	$publications, #ref-to-hash
	$species,      #ref-to-hash
	$pval_thresh,   #num
	$enrichment_limit #num
	) = @_;

    my $hash               = {};
    my $e_objects          = {};
    my $error_msg          = {};
    my $expanded_query     = {}; # same as original query, except regex's are expanded
    my $gene_ids           = {}; # gene_ids->{gene_product_id}++
    my $gene_symbols       = {}; # gene_symbols->{gene_product_id} = symbol
    my $gid_by_gene_symbol = {}; # gid_by_gene_symbol->{ $gene_query } = $Ref->{gid};

    #### TERM ACC QUERY ####
    name_acc_query($taq, $get_from_term_accession_sth, $hash, $e_objects, $expanded_query,$pval_thresh, $species, $publications, $error_msg);

    #### TERM NAME QUERY ####
    name_acc_query($tnq, $get_from_fulltext_term_name_sth, $hash, $e_objects, $expanded_query, $pval_thresh, $species, $publications, $error_msg);

    my $tmp_error = {};
    my $tmp_error_msg = {};
    for my $gene_query (keys %{ $gq })
    {

	my $match = 0;

	if($gene_query =~ /\*/)
	{

	    my $sym_sth = $get_gene_product_id_from_re_symbol_sth;
	    my $syn_sth = $get_gene_product_id_from_re_synonym_sth;

	    $match += get_gene_ids($sym_sth, $gene_query,
				   $species, $gene_ids, $gene_symbols, 
				   $tmp_error, $tmp_error_msg, 
				   'regex', 'symbol'
				   );
	    
	    $match += get_gene_ids($syn_sth, $gene_query,
				   $species, $gene_ids, $gene_symbols, 
				   $tmp_error, $tmp_error_msg, 
				   'regex', 'synonym'
				   );
	    
	}
	else
	{

	    my $sym_sth = $get_gene_product_id_from_em_symbol_sth;
	    my $syn_sth = $get_gene_product_id_from_em_synonym_sth;

	    $match += get_gene_ids($sym_sth, $gene_query,
				   $species, $gene_ids, $gene_symbols,
				   $tmp_error, $tmp_error_msg, 
				   'exactmatch', 'symbol'
				   );
	    
	    $match += get_gene_ids($syn_sth, $gene_query,
				   $species, $gene_ids, $gene_symbols,
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

    model_id_query($modelIdQuery, 
		   $enrichment_limit,
		   $e_objects,
		   $hash, 
		   $error_msg);
    

    model_like_query($modelLikeQuery, 
		     $enrichment_limit,
		     $e_objects,
		     $hash, 
		     $error_msg);

    gene_query($gene_ids,
	       $expanded_query,
	       $gene_symbols,
	       $get_from_gene_product_id_sth,
	       $pval_thresh,
	       $publications,
	       $species,
	       $gid_by_gene_symbol,
	       $e_objects,
	       $hash,
	       $tmp_error_msg,
	       $error_msg);
    
    #CCDB::Driver::inspect_results($hash,'');
    #exit;

    #$dbh->disconnect();
    
    return ($hash, scalar(keys %{ $hash }), $expanded_query, $error_msg, $gid_by_gene_symbol );
}

sub model_like_query
{
  my ($queryHash, 
      $enrichment_limit,
      $e_objects,
      $hash, 
      $error_msg) = @_;

  my $sth = $get_model_like_sth;
  foreach my $queryMid (keys %{ $queryHash })
  {
      print STDERR "$queryMid: models like\n";
      #print STDERR "$get_model_like\n";

      $sth->bind_param(1,$queryMid); # TYPE=SQL_INTEGER
      $sth->bind_param(2,$queryMid); # TYPE=SQL_INTEGER
      $sth->execute();
      
      #printf STDERR "$gid: %d rows\n", $sth->rows;
      #printf STDERR "$gid: SQL\n"; 
      #printf STDERR "   " . $dbh->{Statement} . "\n";
      my %matched;
      my ($id_a, $id_b);
      while(my $Ref = $sth->fetchrow_hashref())
	{
	    $id_a = $Ref->{model_id_a};
	    $id_b = $Ref->{model_id_b};
	    
	    #printf STDERR ("MODELS_LIKE: %s %s %d\n", $id_a, $id_b, $Ref->{gene_score});
	    $matched{$id_a} = $Ref->{gene_score} if($id_a != $queryMid);
	    $matched{$id_b} = $Ref->{gene_score} if($id_b != $queryMid);
	}
      
      #### ERROR HANDLING ####
      if(scalar(keys %matched) == 0)
      {
	  $error_msg->{"no-model-like-notice"}{$queryMid}++;
      }
      else
      {
	  $matched{$queryMid}++;
	  model_id_query(\%matched,
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
  my ($queryHash, 
      $enrichment_limit,
      $e_objects,
      $hash, 
      $error_msg) = @_;

  my $sth = $get_from_model_id_sth;
  foreach my $mid (keys %{ $queryHash })
  {
      print STDERR "$mid: querying\n";
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
	$gene_ids,
	$gene_symbols,
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

	### filter by species constraint
	my $genus_species_str = join " ", $Ref->{genus}, $Ref->{species};
	next unless(exists $species->{$genus_species_str});

	my $gid = $Ref->{gid};

	$match++;
	$gene_ids->{ $gid }++;
	$gene_symbols->{ $gid } = $Ref->{symbol};

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
    my ($gene_ids,
	$expanded_query,
	$gene_symbols,
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
    
    foreach my $gid (keys %{ $gene_ids })
    {
	#print STDERR "$gid: querying\n";
	#print STDERR "$gid: pval=$pval_thresh\n";

	$expanded_query->{$gene_symbols->{$gid}}++;

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
	    my $genus_species_str = join " ", $Ref->{genus}, $Ref->{species};
	    next unless(exists $species->{$genus_species_str});
	    next unless(exists $publications->{$Ref->{mpub}});
	    

	    $matched++;

	    my $mid = $Ref->{mid};
	    my $sid = $Ref->{sid};
	    my $tid = $Ref->{tid};

	    $gid_by_gene_symbol->{ $mid }{ $genus_species_str }{ $tid }{ $gene_symbols->{$gid} } = $gid;

	    unless( exists $e_objects->{$mid}{"enrichment"}{$sid}{$tid} )
	    {

		### dont make enrichment object if the gene ($gid) is not 
		### annotated to term ($tid) in species ($sid)
		next if($Ref->{e_gids} !~ /$gid/);

		$e_objects->{ $Ref->{mid} }{'enrichment'}{ $Ref->{sid} }{ $Ref->{tid} }++;

		push @{ $hash->{ $mid }{'enrichment'} }, CCDB::Enrichment::populate_enrichment($Ref);

	    }

	    ### if we didnt make an enrichment object for this gene, 
	    ### we certainly wont make a model object for it
	    next unless(exists $hash->{ $Ref->{mid} }{'enrichment'});

	    unless(exists $hash->{ $Ref->{mid} }{'model'})
	    {
		
		$hash->{ $Ref->{mid} }{'model'} = CCDB::Model::populate_model($Ref);

	    }
	}

	#### ERROR HANDLING ####
	if($matched == 0)
	{
	    $error_msg->{"no-match-notice"}{$gene_symbols->{$gid}}++;
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
    my ($taq_or_tnq, 
	$sth, 
	$hash, 
	$e_objects, 
	$expanded_query, 
	$pval_thresh,
	$species,
	$publications,
	$error_msg) = @_;

    foreach my $q (keys %{ $taq_or_tnq })
    {

	$expanded_query->{$q}++;
	$sth->bind_param(1,$pval_thresh);
	$sth->bind_param(2,$q);
	$sth->execute();

	my $found = {};

	while(my $Ref = $sth->fetchrow_hashref())
	{

	    ### filter by contraints (pval, species, publications) ###
	    next if($Ref->{e_pval} > $pval_thresh);
	    my $genus_species_str = join " ", $Ref->{genus}, $Ref->{species};
	    next unless(exists $species->{$genus_species_str});
	    next unless(exists $publications->{$Ref->{mpub}});

	    $found->{$q}++;

	    my $mid = $Ref->{mid};
	    my $sid = $Ref->{sid};
	    my $tid = $Ref->{tid};

	    ### if we havent yet made this model ($mid) containing the matched term 
	    ### name or accession, we make it
	    unless( exists $hash->{ $Ref->{mid} }{'model'} )
	    {

		$hash->{$mid}{'model'} = CCDB::Model::populate_model($Ref);

	    }

	    ### if we havent yet made an enrichment object for this term ($tid) in 
	    ### the model($mid) for this species ($sid), then we make it
	    unless( exists $e_objects->{$mid}{'enrichment'}{$sid}{$tid} )
	    {

		$e_objects->{ $Ref->{mid} }{'enrichment'}{ $Ref->{sid} }{ $Ref->{tid} }++;

		push @{ $hash->{ $Ref->{mid} }{'enrichment'} }, CCDB::Enrichment::populate_enrichment($Ref);

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
