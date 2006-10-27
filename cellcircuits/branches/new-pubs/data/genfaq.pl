#!/usr/bin/perl -w
#
# Script to generate the FAQ html files
#

sub generate_HTML
{
    my ($datFile, $outDir) = @_;

    open(DAT, $datFile) || die "Can't open $datFile\n";
    ##
    ## Parse the .dat file
    ##
    
    my %SECTIONS;
    
    my $section = "";
    my $text = "";
    while(<DAT>)
    {
	if(/^==(\S+)/)
	{
	    my $name = $1;

	    if($section ne "")
	    {
		chomp $text;
		if($section eq "ORGANISM")
		{
		    $text =~ s/\n/<br>/g;
		}
		
		$text =~ s/<a/<a class="white-bg-link"/g;

		$SECTIONS{$section} = $text;
	    }
	    $section = $name;
	    $text = "";
	}
	elsif(/\S+/)
	{
	    $text .= $_;
	}    
    }
    
    $SECTIONS{$section} = $text;
     
    ##
    ## Generate HTML
    ##

    #foreach $s (keys %SECTIONS)
    #{
	#printf STDERR ("%s\t%s\n", $s, $SECTIONS{$s});
    #}
    
    
    my $legendHTML = "";
    if($SECTIONS{LEGEND} ne "")
    {
	$legendHTML .= qq(<img src="$SECTIONS{LEGEND}"\>);
    }

    my $figHTML = "";

    foreach $key (sort keys %SECTIONS)
    {
	if($SECTIONS{$key} ne "" && $key =~ /^FIGURE/)
	{
	    my ($a, $large, $thm) = split(/:/, $key);

	    $figHTML .= qq(<a href="$large"><img src="$thm" align="left" hspace="10">);
	    $figHTML .= $SECTIONS{$key};
	    $figHTML .= "<br clear=left><br>";
	}
    }

    if($figHTML ne "")
    {
	$figHTML = "<h2>Original figures from this paper</h2>" . $figHTML;
    }


    my $HTML = <<HTML;
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
 <head>
  <title>Cell Circuit Search : $SECTIONS{CITATION}</title>
  <link rel="stylesheet" type="text/css" href="/master.css" />
 </head>
<body>

$legendHTML

<h2>FAQ: $SECTIONS{CITATION}</h2> 

"$SECTIONS{TITLE}"
<a class="white-bg-link" href="$SECTIONS{FULLTEXT}">[fulltext]</a>
   
$figHTML

<h2>Q&A</h2>
<ol>
<li><b>What is this paper about?</b>
<p>
<table width="80%">
<tr><td>
<b>Abstract:</b> $SECTIONS{ABSTRACT}
</td>
</tr>
</table>
<br>
<li><a name="organisms"><b>What organism is being modeled?</b></a>
<p>
$SECTIONS{ORGANISM}
<p>
</ol>
</body>
</html>
  
HTML

      open(OUT, ">$outDir/legend_FAQ.html") || die "Can't open $outDir/legend_FAQ.html\n";

      print OUT $HTML;
}

foreach $arg (@ARGV)
{
    
    if(-r "$arg/legend/faq.dat")
    {
	print "$arg\n";
	generate_HTML("$arg/legend/faq.dat", "$arg/legend");
    }
}
