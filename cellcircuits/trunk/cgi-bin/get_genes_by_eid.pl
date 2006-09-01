#!/usr/bin/perl
use strict;
use warnings;

#
# Set PERL5LIB environment variable instead of 
# hardcoding this path.
#
# In Apache httpd.conf:
# SetEnvIf Request_URI "search" PERL5LIB /var/www/cgi-bin/search/v1.1
# NOT USING SetEnvIf (encountered some apache config problems)
#
use lib '/var/www/cgi-bin/search/v1.1';


use CCDB::Query;
use CCDB::Enrichment;
use CCDB::Synonyms;
use CCDB::HtmlRoutines qw(highlight gen_go_url);

use CCDB::Constants qw($search_url
		       $pubCitation 
		       $pubName 
		       $db_link_by_species 
		       );

use CGI::Carp qw(fatalsToBrowser); 
use CGI qw(:standard);

print "Content-type: text/html\n\n";

sub gen_header
{
    my ($title) = @_;
    
    my $html = <<HEADER;    
<!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
<head>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
  <meta name="author"      content="Mike Daly, Craig Mak" />
  <meta name="keywords"    content="cell circuits, biological networks, network models, systems biology, pathway hypotheses" />
  <meta name="description" content="Cell Circuits" />
  <meta name="robots"      content="all" />
  <title>$title</title>
  <link type=text/css rel=stylesheet href="$search_url/master.css" />
</head>
<body>

<a href="$search_url/index.html">
<img src="$search_url/CC-logo-small.jpg" 
     border="0" 
     alt="Cell Circuits" 
     title="Click to go to the Cell Circuits Home Page"/>
</a>
HEADER

   return $html;
}


my $FOOT .= <<FOOT;
  <center>
    <a class="white-bg-link" href="$search_url/advanced_search.html">Advanced Search</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class="white-bg-link" href="$search_url/about_cell_circuits.html">About CellCircuits</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class="white-bg-link" href="http://chianti.ucsd.edu/idekerlab/index.html">Ideker Lab</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class="white-bg-link" href="http://www-bioeng.ucsd.edu/">UCSD</a>
    <p style="font-style:italic;font-size: 0.8em">Funding provided by the National Science Foundation (NSF 0425926).</p>
  </center>
FOOT

if(param("eid"))
{
    my $eid = param("eid");
    my $thm_img = param("thm");
    my @hilite_array = param("h");
    my $eo = CCDB::Query::get_enrichment_object_by_eid($eid);

    my $lrg_img = $thm_img;
    $lrg_img =~s/thm_img/lrg_img/; 

    my $n_genes_in_model_with_term = $eo->n_genes_in_model_with_term(); 
    my $n_genes_in_model           = $eo->n_genes_in_model();           
    my $n_genes_with_term          = $eo->n_genes_with_term();          
    my $n_genes_in_GO              = $eo->n_genes_in_GO();              
    my $pval                       = $eo->pval();                       
    my $gene_ids                   = $eo->gene_ids();                   
    my $mid                        = $eo->mid();                        
    my $mpub                       = $eo->mpub();                       
    my $mname                      = $eo->mname();                      
    my $sid                        = $eo->sid();                       
    my $genus                      = $eo->genus();                     
    my $species                    = $eo->species();                   
    my $tid                        = $eo->tid();                        
    my $tacc                       = $eo->tacc();                       
    my $tname                      = $eo->tname();                      
    my $ttype                      = $eo->ttype();                      

    my $org = join " ", $genus, $species;

    my %hilite_hash;
    map { $hilite_hash{$_}++ } @hilite_array;

    my $gene_html = "<table cellpadding=4 cellspacing=0 border=0><tr>";

    my @gene_ids = split(/\s+/,$gene_ids);

    for my $i (0..$#gene_ids)
    {
	my $gid = $gene_ids[$i];
	my @syns = split(/\s+/, $synonyms->{$gid});
	my $gene = $syns[0];
	
	my $syn_str = '';
	if(scalar(@syns) > 1)
	{
	    $syn_str = "Synonyms of $gene: @syns[1..$#syns]";
	}
	else
	{ 
	    $syn_str = "No synonyms of $gene registered in the GO database";
	}

	
	my $href  = '';
	my $class = '';
	if(! exists $db_link_by_species->{$org})
	{
	    if(exists $hilite_hash{$gene})
	    {
		$class = 'highlighted';
	    }
	    else
	    {
		$class = '';
	    }
	    #print "db_link_by_species->{$org} doesnt exists!";
	}
	else
	{
	    if(exists $hilite_hash{$gene})
	    {
		$class = 'highlighted-link';
	    }
	    else
	    {
		$class = 'white-bg-link';
	    }

	    $href = $db_link_by_species->{$org} . $gene;
	}

	$gene_html .= "<td><a class='$class' href='$href' title='$syn_str'>$gene</td>";
	
	if( $i > 0 && ($i % 5)==0 )
	{
	    $gene_html .= "</tr>"; 
	    if($i != $#gene_ids)
	    {
		$gene_html .= "<tr>";
	    }
	}

    }

    $gene_html .= "</tr></table>";

    my $go_url = gen_go_url($tacc);
    
    my $html = gen_header("Genes in Model (Annotated with $tname)");

    $html .= <<HTML;    
  <table cellpadding=2 cellspacing=2 border=0>

  <tr> <td colspan=2> <hr> </td></tr>

  <tr>
  <td valign="top">
      <h2>Model information</h2>
      <table>
      
         <tr><td class="bold">Publication:</td><td>$pubName->{$mpub}<br><a class='white-bg-link' href="$pubCitation->{$mpub}">[PubMed]</a></td></tr>
	 <tr><td class="bold">Model Name:</td><td>$mname</td></tr>
	 <tr><td class="bold">Organism:</td><td><i>$genus&nbsp;$species</i></td></tr>
	 <tr><td class="bold">Enriched GO Annotation:</td><td>$tname<br>
	 <a class="white-bg-link" href="$go_url">($tacc)</a><br>$ttype</td>
	 </tr>
	 
       </table>
  </td>

  <td valign="top">
     <h2>Hypergeometric statistics</h2>
     <table cellpadding=0 cellspacing=0>
  
         <tr><td colspan=2><img src="$search_url/hypergeometric-cartoon.jpg"></td></tr>
         <tr><td class="bold">K (overlap):</td><td>$n_genes_in_model_with_term</td></tr>     
         <tr><td class="bold">M (in model):</td><td>$n_genes_in_model</td></tr>
	 <tr><td class="bold">A (with annotation):</td><td>$n_genes_with_term</td></tr>
	 <tr><td class="bold">T (total):</td><td>$n_genes_in_GO</td></tr>
	 <tr><td class="bold">P-value (uncorrected):</td><td>$pval</td></tr>

     </table>
  </td>
  </tr>

  <tr> <td colspan=2> <hr> </td></tr>

  <tr>
  <td valign="top">
  <h2>Genes in model annotated with<br><i>"$tname"</i></h2>
  $gene_html
  <br>
  <a class="highlighted">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> = genes in query
  </td>
  <td valign="top"><br>
  <div style="background-color:white;">
  <a href="$lrg_img" bgcolor='#FFFFFF'><img src="$thm_img" border=0></a></div></td>
  </td>
  </tr>

  <tr> <td colspan=2> <hr> </td></tr>
  <tr> <td colspan=2> $FOOT </td></tr>
  </table>

  </body>
  </html>
HTML

print $html;
}
else
{
    my $url = url(-query=>1);


    my $html = gen_header("Error Finding Genes in Model");
    $html .= <<HTML;    
  <hr>
  
  Bad input parameters
  <br>
 URL: $url
   
  <hr>
  $FOOT

  </body>
  </html>
HTML

print $html;
}
