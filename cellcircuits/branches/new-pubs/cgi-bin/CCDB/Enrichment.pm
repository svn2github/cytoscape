package CCDB::Enrichment;

our $VERSION = '1.0';

use strict;
use warnings;
use DBI qw(:sql_types);

use CCDB::DB;
use CCDB::Cache;

our @ISA = qw( CCDB::DB );

#################################################
# new() - Constructor
# Example: CCDB::Gene->new();
# Returns: blessed hash
sub new
{
    my $invocant = shift;
    my $self     = bless({}, ref($invocant) || $invocant);
    $self->init();
    return $self;
}

sub init
{
    my $self = shift;
    $self->id(undef);
    $self->n_genes_in_model_with_term(undef);
    $self->n_genes_in_model(undef);
    $self->n_genes_with_term(undef);
    $self->n_genes_in_GO(undef);
    $self->pval(undef);
    $self->gene_ids(undef);
    $self->mid(undef);
    $self->mpub(undef);
    $self->mname(undef);
    $self->sid(undef);
    $self->tid(undef);
    $self->tacc(undef);
    $self->tname(undef);
    $self->ttype(undef);
}

my @fields = qw(
		id
		n_genes_in_model_with_term
		n_genes_in_model
		n_genes_with_term
		n_genes_in_GO
		pval
		gene_ids
		mid
		mpub
		mname
		sid
		tid
		tacc
		tname
		ttype
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
# populate_enrichment
# 
# Purpose: Return an Enrichment object populated from a 
# result set
#
# Parameters: Data from a DBI::fetchrow_hashref
# method call - a ref-to-a-hash of the result row
# we will create an object for and return
#
# Returns: An Enrichment object
sub populate_enrichment 
{
    my ($Ref     #ref-to-hash, keyed by aliases of a mysql db query
	) = @_;

    my $eo = CCDB::Enrichment->new();

    $eo->id                        ($Ref->{e_id});
    $eo->n_genes_in_model_with_term($Ref->{e_n});
    $eo->n_genes_in_model          ($Ref->{e_k});
    $eo->n_genes_with_term         ($Ref->{e_m});
    $eo->n_genes_in_GO             ($Ref->{e_N});
    $eo->pval                      ($Ref->{e_pval});
    $eo->gene_ids                  ($Ref->{e_gids});

    $eo->mid    ($Ref->{mid});
    $eo->mpub   ($Ref->{mpub});
    $eo->mname  ($Ref->{mname});
    
    $eo->sid    ($Ref->{sid});

    $eo->tid    ($Ref->{tid});
    $eo->tacc   ($Ref->{tacc});
    $eo->tname  ($Ref->{tname});
    $eo->ttype  ($Ref->{ttype});

    return $eo;
}

1;
