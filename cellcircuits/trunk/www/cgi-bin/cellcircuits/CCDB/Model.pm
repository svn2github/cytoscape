package CCDB::Model;

our $VERSION = '1.0';

use strict;
use warnings;

use CCDB::Cache;


#################################################
# new() - Constructor
# Example: CCDB::Model->new();
# Returns: blessed hash
sub new
{
    my $invocant = shift;
    my $self     = bless({}, ref($invocant) || $invocant);

    $self->id(undef);
    $self->pub(undef);
    $self->name(undef);
    $self->sif(undef);
    $self->thm_img(undef);
    $self->lrg_img(undef);
    $self->legend(undef);
    $self->wordsMatched({}); # hash of (regex expanded) query words matched.
    $self->eid2genes({});    # hash of enrichment ids in this model mapped 
                             # to the genes in the query that are in that enrichment.
                             
    $self->term2org({});     # GO term ids mapped to a hash of species that the id
                             # was found in.
    $self->score(0);         # The model score.
    $self->isQueryModel(0);  # Set to 1 if this model is the 
                             # query in a MODELS_LIKE or MODEL_ID query.
    $self->size(0);          # enrichment.n_genes_in_model
	$self->pvalue(0); 		 # enrichment.pval as e_pval
    return $self;
}


my @fields = qw(
		id
		pub
		name
		sif
		thm_img
		lrg_img
		legend
		wordsMatched
		eid2genes
		term2org
		score
		isQueryModel
		size
		pvalue
		);

# p 338 of "Programming Perl" - Generating Accessors with Closures
for my $field (@fields)
{
    my $slot = __PACKAGE__ . "::$field";
    no strict "refs";         # So symbolic red to typeglob works.
    *$field = sub {
	my $self = shift;
	$self->{$slot} = shift if @_; # assign a value if one is given 
                                      #(acts like set method)...
	                              # if no value is given, then acts 
                                      #like get method...
	return $self->{$slot};
    };
}

########################################################
# populate_model
# 
# Purpose: Return a Model object populated from a 
# result set
#
# Parameters: Data from a DBI::fetchrow_hashref
# method call - a ref-to-a-hash of the result row
# we will create an object for and return
#
# Returns: A Model object
sub populate_model 
{
    my ($Ref) = @_;

    my $mo = CCDB::Model->new();

    my $mpub = $Ref->{mpub};
    my $mname = $Ref->{mname};
    my $msize = $Ref->{e_k};
    my $mpvalue = $Ref->{e_pval};

    $mo->id    ($Ref->{mid});
    $mo->pub   ($mpub);
    $mo->name  ($mname);

    my $sif = join "/", $mpub, 'sif',     $mname;
    my $thm = join "/", $mpub, 'thm_img', $mname;
    my $lrg = join "/", $mpub, 'lrg_img', $mname;
    my $leg = join "/", $mpub, 'legend/legend_FAQ.html';

    $mo->sif    ($sif);
    $mo->thm_img($thm);
    $mo->lrg_img($lrg);
    $mo->legend ($leg);
	
    $mo->size ($msize);
	$mo->pvalue($mpvalue);
    return $mo;
}


sub score_model
{
    my ($self, $queryInput, $enrichment_objects) = @_;

    my @query_terms = keys %{$queryInput->expandedQuery()};

    # 2. a ref-to-hash of all query terms matched [used for match column]
    # 3. a ref-to-hash that maps enrichment object ids to the genes in the query that are
    #    annotated with that enrichment
    # 4. a ref-to-hash of the term types matched 
    #      e.g. if model matches mf, bp in S cer and bp, cc in D mel and mf in H sap then
    #           termtype->{'molecular_function'} = 2
    #           termtype->{'biological_process'} = 2
    #           termtype->{'cellular_component'} = 1
    my $words_matched = $self->wordsMatched();
    my $eid_to_genes = $self->eid2genes();
    my $term2org = $self->term2org();

    for my $eo (@{$enrichment_objects})
    {
	my $eid                        = $eo->id();                         
	my $gene_ids                   = $eo->gene_ids();                   
	my $sid                        = $eo->sid();                        
	my $tacc                       = $eo->tacc();                       
	my $tname                      = $eo->tname();                      
	my $ttype                      = $eo->ttype();                      
	
	$term2org->{$ttype}{$sid}++;

	my %gids;
	map {$gids{$_}++} split(/\s+/, $gene_ids);

	foreach my $query (@query_terms)
	{
	    # query is a GO term
	    if(($query =~ /GO:\d{7}/i) && (uc($tacc) eq uc($query)))
	    {
		#print "score_model: found term query = $query\n";
		$words_matched->{$query}++;
	    }
	    # query is part of a GO term name
	    elsif(($query =~ /^\"(.*)\"$/) && ($tname =~ /$1/i)) 
	    {
		#print "score_model: found query = $query, tname = $tname\n";
		$words_matched->{$query}++;
	    }
	    # query is a gene in the model and in this enrichment object 
	    elsif(exists $queryInput->geneSymbol2Id()->{$query})
	    {
		my $gid = $queryInput->geneSymbol2Id()->{$query};

		# need to do this because $gid_by_gene_symbol appears to be incorrect
		if(exists($gids{$gid}))
		{
		    #print "score_model: found gene query = $query\n";
		    push @{$eid_to_genes->{$eid}}, $query;
		    #$query =~ s/_HUMAN//;
		    $words_matched->{$query}++;
		}
	    }
	}
    }
    
    $self->score($self->score() + scalar(keys %{$words_matched}));
}

1;
