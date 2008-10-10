package CCDB::Error;

use strict;
use warnings;

use CCDB::Constants qw($colors $pubInfo);

my $VERSION = '1.0';

sub formatErrorMessages
{
    my ($error_msg, $pval_thresh, $publications) = @_;

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
	$msg .= print_params( $pval_thresh, $publications);
    }
    else
    {
		my $SUMMARY_INFO =<<SUMMARY_INFO;	
			<div class="Summary_header" id="summary_header">
		        <a id="summary_link" class="summary-info-header" name="summary_link" href="#" 
		           title="Toggle visibility of Summary information"
		           onClick="CategoryVisibility_Toggle('info', 'summary'); return false;">
		           +&nbsp;&nbsp;Search Term Summary</a>&nbsp;
			</div>
		<div name="info" class='summary' id='summary' style="display:none">
		
SUMMARY_INFO
		
		$msg .= $SUMMARY_INFO;
		if(exists $error_msg->{'regex-synonym-notice'})
		{
			##$msg .= regex_syn($error_msg);
			$msg .= regex_syn($error_msg->{'regex-synonym-notice'});
		}
		
		$msg .= printMatchStatistics($error_msg);
		
		# The following is replaced with the above Matching statistics table
		#if(exists $error_msg->{'exactmatch-synonym-notice'})
		#{
		#	#$msg .= exactmatch_syn($error_msg->{'exactmatch-synonym-notice'});
		#}
		#if(exists $error_msg->{'no-match-notice'})
		#{
		#	#$msg .= no_match($error_msg->{'no-match-notice'});
		#	##$msg .= print_params( $pval_thresh, $publications);
		#}
		
		$msg .= '</div>';
		
    }

    return $msg;
}


sub printMatchStatistics {
    my ($error_msg) = @_;
	
	my $retStr = "";
	
	my $speciesWordsHash = $error_msg->{'matchingStat'};
	
	#printHash($speciesWordsHash);

	my @speciesKeys = keys %{$speciesWordsHash};

	my %wordsHash = ();	
	foreach my $oneSpecies (@speciesKeys) {
		my $theWordsHash = $speciesWordsHash->{$oneSpecies};
		foreach my $word (keys %{ $theWordsHash }) {		
			if (!exists($wordsHash{$word})) {
				$wordsHash{$word} = 1;
			}
		}
	}
	#my $firstSpecies = $speciesKeys[0];
	#my $wordsHash = $speciesWordsHash->{$firstSpecies};

	my @words = keys (%wordsHash);
	my $wordCount = @words;
	
	# Table title
	$retStr .= '<br>Matching statistics<br>';
	$retStr .= "<table  border=\"1\">";

	#print table header 
	$retStr .= "<tr>";
	$retStr .= "<th>Search word</th>";
	#foreach my $word (@words) {
	#		$retStr .= "<th>$word</th>";
	#}
	foreach my $species (@speciesKeys) {
			$retStr .= "<th>$species</th>";
	}

	$retStr .= "</tr>";
	
	#print table rows	
	@words = sort(@words);
	foreach my $word (@words) {
		$retStr .= "<tr>";
		$retStr .="<td>$word</td>";

		foreach my $species (@speciesKeys) {
				
			my $value = 0;
			if (exists($speciesWordsHash->{$species}->{$word})) {
				$value = $speciesWordsHash->{$species}->{$word};
			}
			if ($value == 0) {
				$retStr .= "<td><div align=\"center\" class=\"style2\">";			
			}
			else {
				$retStr .= "<td><div align=\"center\" >";
			}
			
			$retStr .= "$value";	
			$retStr .= "</div></td>";		
		}
		$retStr .= "</tr>";
	}		

	$retStr .= "</table>";

	return "$retStr";
}


sub print_params
{
    my ($pval_thresh, $publications) = @_;

    my $h = "";
    if(defined($pval_thresh))
    {
	$h = sprintf("<font style=\"color:%s; \">P-value threshold = %s</font><br><br>", 
		     $colors->{error_message},
		     $pval_thresh);
    }
    
    if(defined($publications))
    {
	$h .= sprintf("<font style=\"color:%s;\"> Publications searched:<ul>", $colors->{error_message});
	
	foreach my $pub (sort keys %{$publications})
	{
	    $h .= sprintf("<li>%s</li>", pretty_print_pub($pub));
	}
	$h .= "</ul> </font>";
    }

    $h .= '<p>Try another search OR Adjust search parameters using <a class="color-bg-link" href="/advanced_search.html">Advanced Search</a></p>';

    return $h;
}

sub pretty_print_pub
{
    my ($pub) = @_;

    if(exists($pubInfo->{$pub})) # =~ /(\D+)(\d+)_(\w+)/)
    {
	return $pubInfo->{$pub}->{name};
	#return sprintf("%s (%d) <i>%s</i>", $1, $2, $3);
    }

    return $pub;
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
    return sprintf("<b style=\"color:%s; \">No results</b><br>", $colors->{error_message});
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
		#$msg .= sprintf("<b style='color:%s;'>[%s] matches:</b><br>", $colors->{error_message}, $query);
		$msg .= sprintf("<b>[%s] matches:</b><br>", $query);
	    }
	    $msg .= $spacer
		#. sprintf("<b style='color:%s;'>[%s], a synonym of %s in <em>%s</em>.  Using %s in results.</b><br>", 
		. sprintf("<b >[%s], a synonym of %s in <em>%s</em>.  Using %s in results.</b><br>", 
			  #$colors->{error_message}, 
			  $syn, $sym, $org, $sym
			  );
#	    $msg .= sprintf("<b style='color:%s;'>[%s] matches [%s], a synonym of %s in <em>%s</em>. Only showing %s.</b><br>", 
#			    $colors->{error_message}, 
#			    $query, $syn, $sym, $org, $sym
#			    );
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
