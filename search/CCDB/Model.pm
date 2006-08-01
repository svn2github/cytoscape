package CCDB::Model;

our $VERSION = '1.0';

use strict;
use warnings;
use DBI qw(:sql_types);

#use lib '/opt/www/cgi-bin/search/v1.6';
use CCDB::DB;
use CCDB::Cache;

our @ISA = qw( CCDB::DB );

#################################################
# new() - Constructor
# Example: CCDB::Model->new();
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
    $self->pub(undef);
    $self->name(undef);
    $self->sif(undef);
    $self->thm_img(undef);
    $self->lrg_img(undef);
    $self->legend(undef);
}

my @fields = qw(
		id
		pub
		name
		sif
		thm_img
		lrg_img
		legend
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

    $mo->id    ($Ref->{mid});
    $mo->pub   ($Ref->{mpub});
    $mo->name  ($Ref->{mname});

    my $sif = join "/", $Ref->{mpub}, 'sif',     $Ref->{mname};
    my $thm = join "/", $Ref->{mpub}, 'thm_img', $Ref->{mname};
    my $lrg = join "/", $Ref->{mpub}, 'lrg_img', $Ref->{mname};
    my $leg = join "/", $Ref->{mpub}, 'legend/legend_FAQ.html';

    $mo->sif    ($sif);
    $mo->thm_img($thm);
    $mo->lrg_img($lrg);
    $mo->legend ($leg);

    return $mo;
}

1;
