#!/usr/bin/perl -w

# almost works
# lex-like parsing
# problem: doesn't match overlapping words correctly 
# -- need a new approach
sub check_highlight_lex
{
    my ($s, $query_words) = @_;

    print "checking: $s\n";

    # check for exact matches
    foreach my $q (keys %{$query_words})
    {
	return highlight($s) if(uc($s) eq uc($q));
    }

    # check for partial matches
    my @positions;

    my $t = "";
    $_ = $s;
  LOOP:
    {
	foreach my $q (keys %{$query_words})
	{
	    print "  *$q*";
	    if(/\G($q)(\b[,.;]?\s*)/igc)
	    {
		$t .= highlight($1) . $2;
		my $end = pos();
		my $start = $end - length($1) - length($2);
		print(" found at " . $start . "-" . $end . "\n");
		redo LOOP;
	    }
	    else
	    {
		print "\n";
	    }
	}
	
	if(/\G(\S+\b[,.;]?\s*)/gc)
	{
	    print(" junk ($1)\n");
	    $t .= $1;
	    redo LOOP unless(pos() == length($_));
	}
	else
	{
	    print "done\n";
	}
    }

    return($t);
}

sub highlight
{
    my ($s) = @_;
    return "[" . $s . "]";
}


sub check_highlight
{
    my ($input, $query_words) = @_;

    #print "checking: $s\n";

    #
    # check for exact matches
    #
    foreach my $q (keys %{$query_words})
    {
	if(uc($input) eq uc($q))
	{
	    return start_highlight() . $input . end_highlight();
	}
    }

    #
    # check for partial matches
    #
    # Algorithm:
    # 1. get positions of all matches of all query terms in the input
    # 2. loop through all positions and mark characters that are matched
    #    [ marks stored in the @match array ]
    # 3. use the @match array to insert start_highlight and 
    #    end_highlight tags into the original string
    my @positions;
    foreach my $q (keys %{$query_words})
    {
	#print "  *$q*";
	$_ = $input;
	while(/($q)/g)
	{
	    my $end = pos() - 1;
	    my $start = $end - (length($1)-1);
	    #print(" MATCH from " . $start . " to " . $end);
	    
	    push @positions, [$start, $end];
	}

	#print "\n";
    }

    # setup the match array and mark matched characters
    my @match;
    my @input_array = split(//, $input);
    for my $i (0..(length($input)-1))
    {
	$match[$i] = 0;
    }
    foreach $p (@positions)
    {
	#print "setting $p->[0] to $p->[1] to match\n";
	for my $i ($p->[0]..$p->[1])
	{
	    $match[$i] = 1;
	}
    }
    # DEBUG: uncomment to see which chars are matched
    #printf "match = %s\n", join("", @match);

    # use the match array to insert highlighting
    my $out = "";
    my ($c, $x);
    for my $i (0..$#match)
    {
	my $c = $match[$i];
	my $x = $i == 0 ? 0 : $match[$i-1];
	if($c == 1 && $x == 0)
	{
	    $out .= start_highlight();
	}
	elsif($c == 0 && $x == 1)
	{
	    $out .= end_highlight();
	}

	#print "$i: out = $out\n";
	$out .= $input_array[$i];
    }
    
    # special case: if the last char is highlighted
    # then we need to add an end_highlight tag
    if($match[$#match] == 1)
    {
	$out .= end_highlight();
    }

    return($out);
}

sub start_highlight
{
    return qq([);
}


sub end_highlight
{
    return qq(]);
}

my $words =  { 
    DNA=>1, 
    binding=>1, 
    gcn4=>1,
    "HOG kinase"=>1,
    "carb HOG"=>1
    };


print check_highlight2("a HOG kinase cascade", $words) . "\n";
print check_highlight2("DNA", $words) . "\n";
print check_highlight2("gcn4", $words) . "\n";
print check_highlight2("DNA binding", $words) . "\n";
print check_highlight2("DNA binding stress", $words) . "\n";
print check_highlight2("stress binding a", $words) . "\n";
print check_highlight2("DNA carb HOG kinase", $words) . "\n";
