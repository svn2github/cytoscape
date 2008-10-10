package CCDB::QueryInput;

CCDB::QueryInput->_generateAccessors(qw(gene termName termAccession modelId modelLike 
					queryString
					expandedQuery
					geneId2Symbol
					geneSymbol2Id
					noMatchingQueryWords
					));

sub new
{
    my ($caller) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);

    $self->gene({});
    $self->termName({});
    $self->termAccession({});
    $self->modelId({});
    $self->modelLike({});
    $self->queryString("");
    $self->expandedQuery({});   # same as original query, except regex's are expanded
    $self->geneId2Symbol({});
    $self->geneSymbol2Id({});
	$self->noMatchingQueryWords({});

    return $self;
}

sub print
{
    my ($self) = @_;
    my $s = "";
    $s .= sprintf( "  qstr:\t[%s]\n", $self->queryString());
    $s .= sprintf( "  gq:\t[%s]\n", join ", ", keys %{ $self->gene() } );
    $s .= sprintf( "  taq:\t[%s]\n", join ", ", keys %{ $self->termAccession() } );
    $s .= sprintf( "  tnq:\t[%s]\n", join ", ", keys %{ $self->termName() } );
    $s .= sprintf( "  midq:\t[%s]\n", join ", ", keys %{ $self->modelId() } );
    $s .= sprintf( "  model_like:\t[%s]\n", join ", ", keys %{ $self->modelLike() } );
    $s .= sprintf( "  expanded:\t[%s]\n", join ", ", keys %{ $self->expandedQuery() } );
    $s .= sprintf( "  gid2symbol:\t[%s]\n", 
		   join(", ", map {join "=", $_, $self->geneId2Symbol()->{$_} } keys %{ $self->geneId2Symbol() } ));

    $s .= sprintf( "  symbol2gid:\t[%s]\n", 
		   join(", ", map {join "=", $_, $self->geneSymbol2Id()->{$_} } keys %{ $self->geneSymbol2Id() } ));

    $s .= sprintf( "  symbol2gid:\t[%s]\n", 
		   join(", ", map {join "=", $_, $self->noMatchingQueryWords()->{$_} } keys %{ $self->noMatchingQueryWords() } ));

    return $s;
}

sub _generateAccessors
{
    my ($caller, @fields) = @_;
    my $class = ref($caller) || $caller;

    # generate accessor methods
    for my $field (@fields)
    {
	my $slot = $field;
	my $field = $class . "::" . $field;
	no strict "refs";
	*$field = sub {
	    my $self = shift;
	    $self->{$slot} = shift if @_;
	    return $self->{$slot};
	}
    }
}

1;
