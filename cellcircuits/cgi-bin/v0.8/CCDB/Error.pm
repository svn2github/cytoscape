package CCDB::Error;

use strict;
use warnings;

use CCDB::HtmlRoutines qw($colors);

my $VERSION = '1.0';

my $colors = {
    page_background => '#cccccc',
    error_message => '#B22222',
    pvalue => '#B22222',
    error_background => '#B4C3CA',
    GO_table_dark  => '#446689',
    GO_table_medium => '#738DA7',
    GO_table_light => '#A2B3C5'
};

sub formatErrorMessages
{
    my ($error_msg) = @_;

    my $msg = "";

    if(exists $error_msg->{'no-query-notice'}) 
    { 
	return no_query();
    }

    if(exists $error_msg->{'uneven-number-of-double-quotes'})
    {
	$msg .= uneven_number_of_double_quotes();
    }

    if(exists $error_msg->{'no-results'})
    {
	$msg .= no_results();
    }
    else
    {
	if(exists $error_msg->{'regex-synonym-notice'})
	{
	    #$msg .= regex_syn($error_msg);
	    $msg .= regex_syn($error_msg->{'regex-synonym-notice'});
	}
	if(exists $error_msg->{'exactmatch-synonym-notice'})
	{
	    $msg .= exactmatch_syn($error_msg->{'exactmatch-synonym-notice'});
	}
	if(exists $error_msg->{'no-match-notice'})
	{
	    $msg .= no_match($error_msg->{'no-match-notice'});
	}
    }

    return $msg;
}

sub no_query
{
    return sprintf("<b style=\"color:%s; \"> No query was entered.  For example queries, see "
		   . "<a class=\"color-bg-link\" href=\"/about_cell_circuits.html\">About Cell Circuits</a> "
		   . "or click on the 'Load Example Query' link above.</b><br>",
			$colors->{error_message});
}

sub uneven_number_of_double_quotes
{
    return sprintf("<b style=\"color:%s; \"> Query contains an uneven number of double quotation marks [\"].</b><br>",
		   $colors->{error_message});
}

sub no_results
{
    return "<b style=\"color:%s; \">No results</b><br>";
}

sub regex_syn
{
    my ($error_msg) = @_;

    my $msg = '';
    my $spacer = "&nbsp;"x8;

    my $j = {};
    foreach my $gid (keys %{ $error_msg }){
	for my $i (0..$#{ $error_msg->{$gid} }){
	    my $h = ${ $error_msg->{$gid} }[$i];
	    my $query = $h->{query};
	    my $org   = $h->{org};
	    my $sym   = $h->{symbol};
	    my $syn   = $h->{synonym};
	    #print "query : $query<br>";
	    #print "org   : $org<br>";
	    #print "sym   : $sym<br>";
	    #print "syn   : $sym<br><br>";
	    unless(exists $j->{$query}){
		$j->{$query}++;
		$msg .= sprintf("<b style='color:%s;'>Query term [%s]:</b><br>", $colors->{error_message}, $query);
	    }
	    $msg .= $spacer
		. sprintf("<b style='color:%s;'>matched [%s], a synonym of %s in <em>%s</em>.  Using %s in results</b><br>", 
			  $colors->{error_message}, 
			  $syn, $sym, $org, $sym
			  );
	}
    }

    return $msg;
}
sub exactmatch_syn
{
    my ($error_msg) = @_;
    
    my $msg = '';
    my $j = {};
    foreach my $gid (keys %{ $error_msg }){
	for my $i (0..$#{ $error_msg->{$gid} }){
	    my $h = ${ $error_msg->{$gid} }[$i];
	    my $query = $h->{query};
	    my $org   = $h->{org};
	    my $sym   = $h->{symbol};
	    my $syn   = $h->{synonym};
	    #print "query : $query<br>";
	    #print "org   : $org<br>";
	    #print "sym   : $sym<br>";
	    #print "syn   : $sym<br><br>";
	    
	    unless(exists $j->{$query}){
		$j->{$query}++;
		$msg .= sprintf("<b style=\"color:%s; \"> Query term [%s] is a synonym of %s in <em>%s</em>.  Using %s in results.</b><br>",
				$colors->{error_message},
				$query, $sym, $org, $sym
				);
	    }
	}
    }

    return $msg;
}

sub no_match
{
    my ($error_msg) = @_;

    my $msg = '';
    foreach my $query ( keys %{ $error_msg } )
    {
	$msg .= sprintf("<b style=\"color:%s; \"> [%s] did not match any models</b><br>",
			$colors->{error_message}, $query
			);
    }

    return ($msg);
}

1;
