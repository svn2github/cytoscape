package CCDB::HtmlRoutines;

require Exporter;

use strict;
use warnings;

use CCDB::Model;
use CCDB::Enrichment;
use CCDB::Synonyms;
use CCDB::Error;
use CGI qw(:standard unescape escape);

use CCDB::Constants qw($cgi_version
		       $search_url
		       $cgi_url
		       $data_url
		       $chianti_url
		       $pubCitation 
		       $pubName 
		       $db_link_by_species 
		       $colors 
		       );

our @ISA = qw(Exporter);
our @EXPORT = qw();
our @EXPORT_OK = qw(highlight gen_go_url);
our $VERSION = 1.0;

my $pubSupplementURL = {
    'de_Lichtenberg2005_Science' => "http://www.cbs.dtu.dk/cellcycle/yeast_complexes/complexes.php",
    'Kelley2005_NBT'             => "$chianti_url/Kelley2005",
    'Sharan2005_PNAS'            => "$chianti_url/Sharan2005",
    'Yeang2005_GB'               => "$chianti_url/Yeang2005"
};


my %img_format =
    (
     Begley2002_MCR             => "jpg",
     Bernard2005_PSB            => "jpg",
     de_Lichtenberg2005_Science => "jpg",
     Gandhi2006_NG              => "jpg",
     Hartemink2002_PSB          => "jpg",
     Haugen2004_GB              => "jpg",
     Ideker2002_BINF            => "jpg",
     Kelley2005_NBT             => "png",
     Sharan2005_PNAS            => "png",
     Suthram2005_Nature         => "jpg",
     Yeang2005_GB               => "gif",
     );

my $species_abbrev = {
    'Caenorhabditis elegans'   => 'C. ele',
    'Drosophila melanogaster'  => 'D. mel',
    'Homo sapiens'             => 'H. sap',
    'Plasmodium falciparum'    => 'P. fal',
    'Saccharomyces cerevisiae' => 'S. cer'
    };

my $species_by_id = {
    88307  => 'Caenorhabditis elegans',
    157890 => 'Drosophila melanogaster',
    92001  => 'Homo sapiens',
    7015   => 'Plasmodium falciparum',
    97048  => 'Saccharomyces cerevisiae'
    };

my $sortMethods = {
    "optionA_by_number_of_query_terms_matching_model" => "By Number of Query Terms Matching Model",
    "optionB_by_publication"                          => "By Publication",
    "optionC_by_functional_enrichment"                => "By Functional Enrichment",
    "optionD_by_db_compatibility_score"               => "By Database Compatibility Score"
    };

my $sortMethod_to_html = {
    "optionA_by_genes_in_model"         => "# Query Terms Matching the Model",
    "optionB_by_publication"            => "Publication",
    "optionC_by_functional_enrichment"  => "Functional Enrichment",
    "optionD_by_db_compatibility_score" => "Database Compatibility Score"
    };




sub outputResultsPage{
    my ($query,
	$publications,
	$species,
	$sort_method,
	$pval_thresh,
	$page,
	$hash,
	$n_matched_models,
	$error_msg,
	$expanded_query,
	$gid_by_gene_symbol
	) = @_;

    print_header($query);
    print_body($query,
	       $hash,	      
	       $page,
	       $expanded_query,
	       $publications, 
	       $species, 
	       $sort_method,
	       $pval_thresh, 
	       $error_msg, 
	       $gid_by_gene_symbol);
    print_trailer();

    return;
}


sub print_header{
    my ($original_query) = @_;
    my $unescaped_query = unescape($original_query);
    my $HEADER =<<HEADER;
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

  <script src="$search_url/javascript/advanced_search.js" type="text/javascript"></script>
  <script src="$search_url/javascript/prototype.js"       type="text/javascript"></script>
  <script src="$search_url/javascript/scriptaculous.js"   type="text/javascript"></script>

  <link type="text/css" rel="stylesheet" href="$search_url/master.css" />
  <title>Cell Circuits - Results: $unescaped_query</title>
</head>
HEADER

    print $HEADER;
    return;
}

sub start_body{
    my ($original_query, $publications, $species, $sort_method, $pval_thresh, $error_msg, $bp_count, $cc_count, $mf_count) = @_;

    my $pub_html         = get_publication_hidden_fields_html($publications);
    my $species_html     = get_species_hidden_fields_html($species);         
    my $sort_method_html = get_sort_method_hidden_fields_html($sort_method);
    my $pval_thresh_html = get_pval_thresh_hidden_fields_html($pval_thresh);
    my $error_html       = CCDB::Error::formatErrorMessages($error_msg, $pval_thresh, $publications);

    my $unescaped_query = '\'' . unescape($original_query) . '\'';
    #$unescaped_query =~ s/\"/\\\"/g;

    my $START_BODY =<<START_BODY;
<!-- <body id='body-element' onLoad="onload_action($bp_count, $cc_count, $mf_count);return false;"> -->
<body id='body-element'>

<form method='POST' name='search' action='$cgi_url/search.pl'>
 
  <table align='center' border='0' cellspacing=0 cellpadding=0 summary='search interface'>
    <tr>
      <td align='right' rowspan=2>
	<a href='$search_url/index.html'><img src='$search_url/CC-logo-small.jpg' border='0' alt="Cell Circuits" title="Click to go to the Cell Circuits Home Page"/></a>
      </td>
      <td align="center" valign="bottom">
        <input type="text" size="55" name="search_query" value=$unescaped_query title='For information on valid queries, click "About CellCircuits" link to the right'/>
      </td>
      <td align='left' valign='center' rowspan=2>
         &nbsp;<a class='color-bg-link' href='$search_url/index.html' title='Click to go to the Cell Circuits Home Page'>CellCircuits&nbsp;Home</a><br />
	 &nbsp;<a class='color-bg-link' href='$search_url/advanced_search.html'>Advanced&nbsp;Search</a><br />
	 &nbsp;<a class='color-bg-link' href='$search_url/about_cell_circuits.html'>About&nbsp;CellCircuits</a>
      </td>
    </tr>
    <tr>
      <td align='center' valign='top'>
        <input type="submit" name="search_query_button" value="Search" title='Click to find models matching your query'/><input type="submit" value="Load Example Query" title="requires javaScript" onClick="LoadExampleQuery('gcn* gal4 GO:0003677','DNA binding');return false;" />
      </td>
    </tr>
  </table>
<!--
    <tr>
      <td align='right'>
	<a href='$search_url/index.html'><img src='$search_url/CC-logo-small.jpg' border='0' alt="Cell Circuits" title="Click to go to the Cell Circuits Home Page"/></a>
      </td>
      <td align="center" valign="center">
         <table width="100%" border="0" cellspacing=0 cellpadding=0>
	   <tr>
             <td align=center>
	       <input type="text" size="55" name="search_query" value=$unescaped_query title='Enter a search query. Example: gcn* gal4 GO:0003677 "DNA binding"'/>
	       <br>
	       <input type="submit" name="search_query_button" value="Search" title='Click to find models matching your query'/><input type="submit" value="Load Example Query" title="requires javaScript" onClick="LoadExampleQuery('gcn* gal4 GO:0003677','DNA binding');return false;" />
-->
<!--
	       <a class="color-bg-link" href="#" title="requires javaScript" onClick="LoadExampleQuery('gcn* gal4 GO:0003677','DNA binding');return false;">Load Example Query</a>
	     </td>
	   </tr>
	 </table>
      </td>
      <td align='left' valign='center'>
         <a class='color-bg-link' href='$search_url/index.html' title='Click to go to the Cell Circuits Home Page'>CellCircuits&nbsp;Home</a><br />
	 <a class='color-bg-link' href='$search_url/advanced_search.html'>Advanced&nbsp;Search</a><br />
	 <a class='color-bg-link' href='$search_url/about_cell_circuits.html'>About&nbsp;CellCircuits</a>
      </td>
    </tr>
-->
  </table>
$pub_html
$species_html
$sort_method_html
$pval_thresh_html

<center>
<table><tr><td align="left">
$error_html
</td></tr></table>
</center>

START_BODY

   return $START_BODY;
}

sub outputErrorPage
{
    my ($original_query, # string
        $publications,   # ref-to-hash
	$species,        # ref-to-hash
	$sort_method,    # string
	$pval_thresh,
	$error_msg
        ) = @_;

    print_header($original_query);
    print start_body($original_query,
	       $publications, 
	       $species, 
	       $sort_method,
	       $pval_thresh, 
	       $error_msg,'','','');
    print "</body>";
    print_trailer();

    return;
}

sub formatPageNavigation_tr
{
    my ($N_PAGE_JUMP, $page, $total_pages, $lower_lim, $upper_lim, $n_matched_models) = @_;
    
    my $page_html = "";
    
    ## Add 'Previous' link if necessary
    if($page > 1){
	my $prev_page = $page - 1;
	$page_html .= tag("a", { class=>"color-bg-link",
				 onclick=>"document.search.page.value=$prev_page; document.search.submit();", 
				 href=>"#"},
			  "Previous");
	$page_html .= "&nbsp;&nbsp;\n";
    }

    ## Add 'Page' button and input box
    $page_html .= tag("input", 
		      { type=>"submit",
			name=>"page_jump_" . $N_PAGE_JUMP,
			onclick=>"document.search.page.value=document.search.jump_to_page_${N_PAGE_JUMP}.value;",
 			value=>"Page"});
    $page_html .= "&nbsp;\n";
    $page_html .= tag("input", { type=>"text",
				size=>3,
				name=>"jump_to_page_" . $N_PAGE_JUMP,
				value=>$page});
    $page_html .= "&nbsp;of $total_pages\n";

    ## Add 'Next' link if necessary
    if($page < $total_pages){
	my $next_page = $page + 1;
	$page_html .= tag("a", { class=>"color-bg-link", 
				 onclick=>"document.search.page.value=$next_page; document.search.submit();", 
				 href=>"#"},
			  "Next");
	$page_html .= "&nbsp;&nbsp;\n";
    }

    ## Print the page info and navigation HTML
    my $html = "<tr>";

    $html .= tag("td", { class=>"results-page-info",
			 align=>"left",
			 valign=>"top",
			 colspan=>"2"},
		 "Results $lower_lim to $upper_lim of $n_matched_models");

    $html .= tag("td", { align=>"right", colspan=>"2"}, $page_html);

    $html .= "</tr>";

    return($html);
}

# called by outputResultsHeader()
sub pageCalc
{
    my($n_matched_models,$page) = @_;

    my $total_pages = 1;
    if($n_matched_models > 20){
	$total_pages = ($n_matched_models/20);
	$total_pages =~ s/(\d+).(\d+)/$1/;
	my $remainder = ($n_matched_models % 20);
	if($remainder > 0){ $total_pages++; }
    }
    if($page > $total_pages){ $page = 1; }

    my $upper_lim = $page * 20;
    if($upper_lim > $n_matched_models){ $upper_lim = $n_matched_models; }
    my $lower_lim = (($page-1)*20) + 1;

    return ($total_pages, $upper_lim, $lower_lim);
}

# called by printResultsHeader()
sub get_publication_hidden_fields_html
{
    my ($publications) = @_;
    my $pub_html;

    if(scalar(keys %{ $publications }) > 0) {
        foreach my $pub (keys %{ $publications }) {
            $pub_html .= "  <input type=\"hidden\" name=\"publication\" "
                . "value=\"$pub\" checked=\"checked\">\n";
        }
    }
    else {
	foreach my $pub (keys %{ $pubName }) {
	    $pub_html .= "  <input type=\"hidden\" name=\"publication\" "
		. "value=\"$pub\" checked=\"checked\">\n";
	}
    }
    
    return $pub_html;
}

# called by printResultsHeader()
sub get_species_hidden_fields_html
{
    my ($species) = @_;
    my $species_html;

    if(scalar(keys %{ $species }) > 0) {
        foreach my $sp (keys %{ $species }) {
            $species_html .= "  <input type=\"hidden\" name=\"species\" "
                . "value=\"$sp\" checked=\"checked\">\n";
        }
    }
    else {
	foreach my $sp (keys %{ $species_abbrev }) {
	    $species_html .= "  <input type=\"hidden\" name=\"species\" "
		. "value=\"$sp\" checked=\"checked\" />";
	}
    }
    
    return $species_html;
}

# called by printResultsHeader()
sub get_sort_method_hidden_fields_html
{
    my ($sort_method) = @_;
    my $sort_method_html;

    if($sort_method eq "") {
	$sort_method_html = 
	    "  <input type=\"hidden\" name=\"sort_method\" "
	    . "value=\"optionA_by_number_of_query_terms_matching_model\" "
	    . "checked=\"checked\" />";
    }
    else {
	$sort_method_html = 
	    "  <input type=\"hidden\" name=\"sort_method\" "
	    . "value=\"$sort_method\" checked=\"checked\" />";
    }
    
    return $sort_method_html;
}

# called by printResultsHeader()
sub get_pval_thresh_hidden_fields_html
{
    my ($pval_thresh) = @_;
    my $pval_thresh_html;

    if($pval_thresh eq "") {
	$pval_thresh_html = 
	    "  <input type=\"hidden\" name=\"pval_thresh\" "
	    . "value=\"1e-02\" checked=\"checked\" />";
    }
    else {
	$pval_thresh_html = 
	    "  <input type=\"hidden\" name=\"pval_thresh\" "
	    . "value=\"$pval_thresh\" checked=\"checked\" />";
    }
    
    return $pval_thresh_html;
}

## outputResultsTable()
#    -> sort_model_and_get_html()
#    -> printFunctionViewResultsTableHeader()
#    -> sortModelsBy()
#
sub print_body
{
    my ($original_query,
	$hash,	      
	$page,
	$expanded_query,
	$publications, 
	$species, 
	$sort_method,
	$pval_thresh, 
	$error_msg, 
	$gid_by_gene_symbol
	) = @_;

    #my $html = {};
    my $counts = {
	menu_block_container => 0,
	menu_header => 0,
	molecular_function => 0,
	biological_process => 0,
	cellular_component => 0
	};

    my $eo_type_counts_by_model = {};
    my $types = {};
    
    my $html = '';

    ## $N_PAGE_JUMP keeps track of the number of page navigation boxes.
    ## Typically 2: one at the top of the table and one at the bottom.
    my $N_PAGE_JUMP = 1; 

    my $score = {};
    my $query_matched = {};
    my $eid_genes = {};
    my $termtypes = {};
    ## we score and get html for ALL models -- should we just score them all, then
    ## get the html only for the ones we're displaying?  that sounds better to me...
    ## 20 july -- this is now what we do ;-)
    foreach my $mid (keys %{ $hash })
    {
	my @enrichment_objects = @{ $hash->{$mid}{"enrichment"} };
	($score->{$mid}, 
	 $query_matched->{$mid}, 
	 $eid_genes->{$mid},
	 $termtypes->{$mid}) = score_model($expanded_query, \@enrichment_objects, $gid_by_gene_symbol);
    }

    #foreach my $m (keys %{ $termtypes }){
#	foreach my $t (keys %{ $termtypes->{$m} }){
#	    print "termtypes->{$m}{$t}<br>";
#	}
#    }

    my $sorted_mids = ();
    @{ $sorted_mids } = sort { $score->{$b} <=> $score->{$a} 
			       || min_pval( $hash->{$a}{"enrichment"} ) <=> min_pval( $hash->{$b}{"enrichment"} )
				   || $hash->{$a}{"model"}->pub() cmp $hash->{$b}{"model"}->pub()	
			       } keys %{ $score };
    
    my $n_matched_models = scalar(@{ $sorted_mids });
    my ($total_pages, $upper_lim,$lower_lim) = pageCalc($n_matched_models, $page);
    my $n_models_on_page = $upper_lim - $lower_lim + 1;
    
    my $bp_count = 0;
    my $cc_count = 0;
    my $mf_count = 0;
    for my $i (($lower_lim-1)..($upper_lim-1))
    {
	my $mid = $sorted_mids->[$i];
	if(exists $termtypes->{$mid}{'biological_process'}){ $bp_count += $termtypes->{$mid}{'biological_process'} };
	if(exists $termtypes->{$mid}{'cellular_component'}){ $cc_count += $termtypes->{$mid}{'cellular_component'} };
	if(exists $termtypes->{$mid}{'molecular_function'}){ $mf_count += $termtypes->{$mid}{'molecular_function'} };
    }
    my $body = start_body($original_query, $publications, $species, 
			  $sort_method, $pval_thresh, $error_msg, $bp_count, $cc_count, $mf_count);
    
    $body .= qq(<table id="results_table" align="center" 
		cellpadding="0" cellspacing="0" bgcolor="$colors->{page_background}" 
		summary="results" width="100%">
		);

    # The "page" hidden field keeps track of what page we are on
    $body .= qq( <input type="hidden" name="page" value="$page"/>);

    $body .= formatPageNavigation_tr($N_PAGE_JUMP++, $page, $total_pages, $lower_lim, $upper_lim, $n_matched_models);
    $body.=<<TBL_HDR;
   <tr class="group-toggle-link">
      <th class="result-header-left" title="# Distinct Matches" width=5%>Score</th>
      <th class="result-header">Model</th>
      <th class="result-header">Matches</th>
      <th class="result-header" colspan="1" valign='bottom'>
         <a class="color-bg-link-header" href="http://www.geneontology.org/" title="GO Home Page">Gene Ontology</a>&nbsp;&nbsp;
         <a class="color-bg-pval-link" href="$search_url/advanced_search.html" title="Click to go to the advanced search page to change this p-value threshold">
	    (P-value < $pval_thresh)
	 </a>
	 <br />
	 <table border=0 align='center' valign='bottom' cellpaddin=0 cellspacing=0>
	    <tr>
	       <td align='center' nowrap><a class="group-toggle-link" href="#" title='Toggle visibility of all Biological Process results on this page' onClick="CategoryVisibility_GroupToggle('bp','',$bp_count); return false;">&nbsp;+/-&nbsp;Biological&nbsp;Process&nbsp;</a></td>
	       <td align='center' nowrap><a class="group-toggle-link" href="#" title='Toggle visibility of all Cellular Component results on this page' onClick="CategoryVisibility_GroupToggle('cc','',$cc_count); return false;">&nbsp;+/-&nbsp;Cellular&nbsp;Component&nbsp;</a></td>
	       <td align='center' nowrap><a class="group-toggle-link" href="#" title='Toggle visibility of all Molecular Function results on this page' onClick="CategoryVisibility_GroupToggle('mf','',$mf_count); return false;">&nbsp;+/-&nbsp;Molecular&nbsp;Function&nbsp;</a></td>
	    </tr>
	 </table>
      </th>
   </tr>
TBL_HDR
    print $body;
    for my $i (($lower_lim-1)..($upper_lim-1))
    {
	my $mid = $sorted_mids->[$i];
	print_model($hash, $mid, $score->{$mid}, $query_matched->{$mid}, $eid_genes->{$mid}, $expanded_query, $counts, $pval_thresh);
    }
    print formatPageNavigation_tr($N_PAGE_JUMP++, $page, $total_pages, $lower_lim, $upper_lim, $n_matched_models);
    print "</table>\n";

    return;
}

sub min_pval
{
    my ($eo_list) = @_;
    my $min_pval = 1;

    for my $i (0..$#{ $eo_list })
    {
	my $pval = $eo_list->[$i]->pval();
	if($pval < $min_pval){ $min_pval = $pval; }
    }

    return $min_pval;
}

sub print_model
{
    my ( $hash, $mid, $score, $query_matched, $eid_genes, $expanded_query, $counts, $pval_thresh) = @_;

    my $mo = $hash->{$mid}{'model'};
    my $pub     = $mo->pub();
    my $sif     = $mo->sif();
    my $thm_img = $mo->thm_img();
    my $lrg_img = $mo->lrg_img();
    my $legend  = $mo->legend();
    
    my @enrichment_objects = @{ $hash->{$mid}{'enrichment'} };
    my $html = "<tr>";
    $html .= format_model_thm_td($score, $pub, $sif, $lrg_img, $thm_img, $legend);
    $html .= format_query_matched_td($query_matched);
    $html .= format_all_eo_td($expanded_query, $mo, \@enrichment_objects, $eid_genes, $counts, $pval_thresh);
    $html .= "   </tr>\n";

    print $html;
    return;
}

sub score_model
{
    my ($expanded_query, $sorted_objects, $gid_by_gene_symbol) = @_;

    my @query_terms = keys %{$expanded_query};

    my $score = 0;
    my %words_matched;
    my %eid_to_genes;
    
    my $org_termtype = {};
    my $termtype     = {};

    for my $eo (@{$sorted_objects})
    {
	my $eid                        = $eo->id();                         
	#my $n_genes_in_model_with_term = $eo->n_genes_in_model_with_term(); 
	#my $n_genes_in_model           = $eo->n_genes_in_model();           
	#my $n_genes_with_term          = $eo->n_genes_with_term();          
	#my $n_genes_in_GO              = $eo->n_genes_in_GO();              
	#my $pval                       = $eo->pval();                       
	my $gene_ids                   = $eo->gene_ids();                   
	my $mid                        = $eo->mid();                        
	#my $mpub                       = $eo->mpub();                       
	#my $mname                      = $eo->mname();                      
	#my $sid                        = $eo->sid();                        
	my $genus                      = $eo->genus();                      
	my $species                    = $eo->species();                    
	my $tid                        = $eo->tid();                        
	my $tacc                       = $eo->tacc();                       
	my $tname                      = $eo->tname();                      
	my $ttype                      = $eo->ttype();                      
	
	my $org = join(" ", $genus, $species);
	my $key = join " ", $org, $eo->ttype();
	unless(exists $org_termtype->{$key}){ $termtype->{$ttype}++; }
	$org_termtype->{$key}++;

	my %gids;
	map {$gids{$_}++} split(/\s+/, $gene_ids);


	foreach my $query (@query_terms)
	{
	    # query is a GO term
	    if(($query =~ /GO:\d{7}/i) && (uc($tacc) eq uc($query)))
	    {
		#print "score_model: found term query = $query\n";
		$words_matched{$query}++;
	    }
	    # query is part of a GO term name
	    elsif(($query =~ /^\"(.*)\"$/) && ($tname =~ /$1/i)) 
	    {
		#print "score_model: found query = $query, tname = $tname\n";
		$words_matched{$query}++;
	    }
	    # query is a gene in the model and in this enrichment object 
	    elsif(exists $gid_by_gene_symbol->{$mid}{$org}{$tid}{$query})
	    {
		my $gid = $gid_by_gene_symbol->{$mid}{$org}{$tid}{$query};

		# need to do this because $gid_by_gene_symbol appears to be incorrect
		if(exists($gids{$gid}))
		{
		    #print "score_model: found gene query = $query\n";
		    push @{$eid_to_genes{$eid}}, $query;
		    #$query =~ s/_HUMAN//;
		    $words_matched{$query}++;
		}
	    }
	}
    }

    # return 
    # 1. the score
    # 2. a ref-to-hash of all query terms matched [used for match column]
    # 3. a ref-to-hash that maps enrichment object ids to the genes in the query that are
    #    annotated with that enrichment
    # 4. a ref-to-hash of the term types matched 
    #      e.g. if model matches mf, bp in S cer and bp, cc in D mel and mf in H sap then
    #           termtype->{'molecular_function'} = 2
    #           termtype->{'biological_process'} = 2
    #           termtype->{'cellular_component'} = 1
    return (scalar(keys %words_matched), \%words_matched, \%eid_to_genes, $termtype);
}


##
## Utility method used to wrap text in an arbitrary 
## HTML tag with attributes
##
sub tag
{
    my ($tag, $attr_hash, $str) = @_;

    my $h = "<$tag";

    if(defined($attr_hash))
    {
	foreach my $key (keys %{$attr_hash})
	{
	    $h .= qq( $key="$attr_hash->{$key}");
	}
    }
    
    if(defined($str))
    {
	$h .= ">$str</$tag>";
    }
    else
    {
	$h .= "/>";
    }

    return $h;
}

sub format_all_eo_td
{
    my ($expanded_query, $model, $eo_list, $eid_genes, $counts, $pval_thresh) = @_;
    
    # index eo's by species and then by GO term type (eg MF, BP, or CC)
    my $eo_hash = {};
    foreach my $eo (@{$eo_list})
    {
	my $sp = $species_by_id->{$eo->sid()};
	push @{ $eo_hash->{$sp}{$eo->ttype()} }, $eo;
    }

    # now format the eo's for each species
    my $h = qq(    <td class="search-result-right" align="center" valign="top">\n);
    foreach my $sp (sort keys %{$eo_hash})
    {
	$h .= format_eo_for_species($expanded_query, $model, $sp, $eo_hash->{$sp}, $eid_genes, $counts, $pval_thresh);
    }
    $h .= "</td>\n";
 
    return ($h);
}

##
## These need to be coordinated with the Javascript code 
## that controls expand/collapse
##
my $EO_HTML_CONSTANTS = 
{
    molecular_function => { 
	CAT          => "mf",
	LABEL        => "Molecular Function",
	SHORT_LABEL  => "Function",
	ID           => "menu_block_mol_func",
	TITLE        => "Toggle visibility of (%d) GO Molecular Function",
	TITLE_PLURAL => "Toggle visibility of (%d) GO Molecular Functions"
	},
    biological_process => { 
	CAT          => "bp",
	LABEL        => "Biological Process",
	SHORT_LABEL  => "Process",
	ID           => "menu_block_bio_proc",
	TITLE        => "Toggle visibility of (%d) GO Biological Process",
	TITLE_PLURAL => "Toggle visibility of (%d) GO Biological Processes"
	},
    cellular_component => { 
	CAT          => "cc",
	LABEL        => "Cellular Component",
	SHORT_LABEL  => "Component",
	ID           => "menu_block_cel_comp",
	TITLE        => "Toggle visibility of (%d) GO Cellular Component",
	TITLE_PLURAL => "Toggle visibility of (%d) GO Cellular Components"
	}
};

##
## Generate HTML for all of the enrichment objects 
## for a species for a given model
##
sub format_eo_for_species
{
    my ($expanded_query, $model, $species, $eo_by_ttype, $eid_genes, $counts, $pval_thresh) = @_;

    my $html = "";
    $html .= qq(<table cellspacing=0 width=500><tr><td>\n);
    $html .= qq(<div class="menu">\n);
    $html .= qq(<div class="menu_options"><em>$species</em></div>\n);
    
    foreach my $t (sort keys %{$eo_by_ttype})
    {
	my @eos = sort { $a->pval() <=> $b->pval() } @{ $eo_by_ttype->{$t} };
	my $n_results = scalar( @eos );

	$counts->{menu_block_container}++; ## not sure what is the difference between these 2 counts?
	$counts->{menu_header}++;

	$counts->{$t}++;

	#print "counts->{$t} $counts->{$t}<br>";

	my $constants = $EO_HTML_CONSTANTS->{$t};
	my $cat = $constants->{CAT};
	my $label = $constants->{LABEL};
	my $short_label = $constants->{SHORT_LABEL};
	my $id = $constants->{ID} . $counts->{$t};
	#my $title = $constants->{TITLE} . "(P-value < $pval_thresh) in this model";
	
	my $title = '';
	if($n_results > 1)
	{
	    $title = sprintf("$constants->{TITLE_PLURAL} (P-value < %g) in this model", $n_results, $pval_thresh);
	}
	else
	{
	    $title = sprintf("$constants->{TITLE} (P-value < %g) in this model", $n_results, $pval_thresh);
	}
	    
	my $header_id = "menu_header" . $counts->{menu_header};
	my $container_id = "menu_block_container" . $counts->{menu_block_container};

	$html .= qq(<div class="menu_header" id="$header_id">
		        <a id="${id}_link" class="go-cat-results-header" name="${cat}_link" href="#" 
		           title="$title"
		           onClick="CategoryVisibility_Toggle('$cat', '$id'); return false;">
		           +&nbsp;&nbsp;$label
                        </a>&nbsp;
		    );
	if($n_results > 1)
	{
	    $html .= qq(<span style='font-size:0.8em;'>($n_results results)</span>);
	}
	else
	{
	    $html .= qq(<span style='font-size:0.8em;'>($n_results result)</span>);
	}
	$html .= qq(
		    </div>
		    <div class="menu_block_container" id="$container_id">
		         <div name="$cat" class='menu_block' id='$id' style="display:none">
		    
		         <table cellspacing=0 width=100%>
		         <tr class="even-row" width=100%>
		             <th width=40%>Genes In Model<br />(Annotated with $short_label)</th>
		             <th align='center' width=40%>GO $short_label</th>
		             <th align='center' width=20%>P-value</th>
		         </tr>
		    );

	for my $i (0..$#eos)
	{
	    if(($i % 2)==0) { 
		$html .= qq( <tr class="odd-row">\n); 
	    }
	    else { 
		$html .= qq( <tr class="even-row">\n); 
	    }
	    
	    $html .= format_eo($expanded_query, $model, $eos[$i], $eid_genes) . "\n";
	    $html .= "</tr>\n";
	}

	$html .= "<tr><td colspan=3></td></tr>\n";
	$html .= "</table>\n";
	$html .= "</div>\n";
    }

    $html .= "</div>\n";
    $html .= "</div>\n";
    $html .= "</td></tr></table>\n";
    
    return ($html);
}

# generate HTML for a single enrichment object
# return a <td> element
sub format_eo
{
    my ($expanded_query, $model, $eo, $eid_genes) = @_;

    my $pub = $model->pub();
    my $thm_img = $model->thm_img();

    my $eid                        = $eo->id();                         
    my $pval                       = $eo->pval();                       
    my $tacc                       = $eo->tacc();                       
    my $tname                      = $eo->tname();                      
    my $n_genes_in_model_with_term = $eo->n_genes_in_model_with_term(); 
    my $org = join(" ", $eo->genus(), $eo->species());

    ## Genes in model and enrichment
    my $h = qq( <td align="center">\n);

    my $url = sprintf("$cgi_url/get_genes_by_eid.pl?eid=%s&thm=%s",
		      $eid,
		      "$data_url/${thm_img}.$img_format{$pub}");
    
    foreach my $g (@{ $eid_genes->{ $eid } })
    {
	if(exists($db_link_by_species->{$org}))
	{
	    $h .= highlight($g, { href=>"$db_link_by_species->{$org}" . $g }) . " ";
	}
	else
	{
	    $h .= highlight($g) . " ";
	}
	$url .= "&h=" . $g;
    }


    $h .= tag("a", 
	      { class=>"color-bg-link", href=>"$url"},
	      join("&nbsp;", "[See", "all", $n_genes_in_model_with_term, "genes]"));

    $h .= "</td>\n";
    
    my @query_terms = keys %{$expanded_query};
    ## term name and accession
    $h .= tag("td", { align=>"left"}, 
	      sprintf("%s (%s)", 
		      check_highlight($tname, \@query_terms), 
		      # This doesn't work for highlighted GO terms.  Need to fix.  CM
		      check_highlight($tacc, \@query_terms, { class=>"color-bg-link", href=>gen_go_url($tacc) })));

    ## pvalue
    $h .= tag("td", { align=>"center", style=>"font-size:0.8em;"}, sprintf("%0.2e",$pval));

    return $h;
}


sub gen_go_url
{
    my ($tacc) = @_;

    my $url = sprintf("http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&query=%s&search_constraint=terms", escape($tacc));

    return $url;
}


# check input text against a list of query words to find parts of the
# input that match one or more query words
sub check_highlight
{
    my ($input, $query_words, $default_params) = @_;

    #printf "checking: $input. qw=[%s]\n<br>", join(",", @{$query_words});

    #
    # check for exact matches
    #
    foreach my $q (@{$query_words})
    {
	if(uc($input) eq uc($q))
	{
	    return highlight($input, $default_params);
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
    foreach my $q (@{$query_words})
    {
	#print "  *$q*";
	if($q =~ /^\"(.*)\"$/)
	{
	    $q = $1;
	}
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

    # If no positions match, just return the input string
    if(scalar(@positions) == 0)
    {
	return $input if(!defined($default_params));
	return tag("a", $default_params, $input);
    }

    # setup the match array and mark matched characters
    my @match;
    my @input_array = split(//, $input);
    for my $i (0..(length($input)-1))
    {
	$match[$i] = 0;
    }
    foreach my $p (@positions)
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
	my $x = ($i == 0) ? 0 : $match[$i-1];
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


sub highlight
{
    my ($str, $default_params) = @_;
    
#    return start_highlight() . $str . end_highlight();
#    if(!defined($params))
#    {
#	$params = {};
#    }
    if(exists($default_params->{href}))
    {
	$default_params->{class}='highlighted-link';
    }
    else
    {
	$default_params->{class}='highlighted';
    }
    return tag("a", $default_params, $str);
}


sub start_highlight
{
    #my ($title) = @_;
    return qq(<a class='highlighted'>);
    #return qq(<a class='highlighted' title='$title'>);
}
sub start_highlight_link
{
    #my ($title) = @_;
    return qq(<a class='highlighted-link'>);
    #return qq(<a class='highlighted-link' title='$title'>);
}


sub end_highlight
{
    return "</a>";
}


sub format_query_matched_td
{
    my ($words_matched) = @_;
    
    my $h = "";
    my $i = 0;

    my %unique_words;

    foreach my $w (keys %{ $words_matched }) 
    {
	if(!exists($unique_words{$w}))
	{
	    $h .= highlight($w);
	    $unique_words{uc($w)}++;
	}

	if((++$i % 3)==0){ $h .= "<br>"; }
	else { $h .= " "; }
    }

    return tag("td", 
	       { class=>"search-result", align=>"center", valign=>"top", bgcolor=>"#cccccc" },
	       $h);
}


sub format_model_thm_td
{
    my($score,$pub,$sif,$lrg_img,$thm_img,$legend) = @_;
    my $model_thm_html = "";
    $model_thm_html = <<MODEL_HTML;
      <td class='search-result' align='center' valign='top' >$score</td>
      <td class='search-result' align='center' valign='top' bgcolor='white'>
         <a href='$data_url/${lrg_img}.$img_format{$pub}'>
            <img src='$data_url/${thm_img}.$img_format{$pub}' border='0'>
         </a><br />
	 <b class='pub-citation'>$pubName->{$pub}</b><br><a class='white-bg-link' href='$pubCitation->{$pub}' title='PubMed abstract'>[PubMed]</a>
MODEL_HTML
      
    if(exists($pubSupplementURL->{$pub})) { 
        $model_thm_html .= "<a class='white-bg-link' href='$pubSupplementURL->{$pub}' title='Online publication supplement'>[supplement]</a>\n";
    }

#### WITHOUT POPUP ####
    $model_thm_html .= <<MODEL_HTML2;
         <a class="white-bg-link" href="$data_url/$legend" title='Legend and FAQ for [$pubName->{$pub}] and its models.'>[legend]</a>
         <a class="white-bg-link" href="$data_url/${sif}.sif" title='(s)imple (i)nteraction (f)ormat: a textual representation of this model.'>[sif]</a>
       </td>
MODEL_HTML2

####  WITH POPUP ####
#    $model_thm_html .= <<MODEL_HTML2;
#         <a class="white-bg-link" href="$data_url/$legend"
#            onClick="window.open('$data_url/$legend','PopupPage',
#	                         'height=700, width=530, toolbar=yes, menubar=yes, 
#                                  scrollbars=yes,resizable=yes'); return false;"
#	    target="_blank">
#            [Legend]
#         </a>
#         <a class="white-bg-link" href="$data_url/${sif}.sif">[sif]</a>
#       </td>
#MODEL_HTML2

    return $model_thm_html;
}

## called by outputResultsTable()
sub sortModelsBy
{
    my ($hash, # hash->{modelid}{"model"}      = Model object 
	       # hash->{modelid}{"enrichment"} = array of Enrichment objects
	$sort_method) = @_;

    my $ordered_modelids = ();
#    @{ $ordered_modelids } = 
#	sort {
#	    scalar(keys %{ $hash->{$b}{"gene"} })
#		<=>
#		scalar(keys %{ $hash->{$a}{"gene"} }) 
#	    } keys %{ $hash };

    return $ordered_modelids;
}

sub print_trailer
{
    print "<br /><br /><center>";
    print "<a class='color-bg-link' href='$search_url/index.html' title='Click to go to the CellCircuits Home Page'>CellCircuits&nbsp;Home</a>&nbsp;&nbsp;|&nbsp;&nbsp;";
    print "<a class='color-bg-link' href='$search_url/advanced_search.html'>Advanced Search</a>&nbsp;&nbsp;|&nbsp;&nbsp;";
    print "<a class='color-bg-link' href='$search_url/about_cell_circuits.html'>About CellCircuits</a>&nbsp;&nbsp;|&nbsp;&nbsp;";
    print "<a class='color-bg-link' href='http://chianti.ucsd.edu/idekerlab/index.html'>Ideker Lab</a>&nbsp;&nbsp;|&nbsp;&nbsp;";
    print "<a class='color-bg-link' href='http://ucsd.edu'>UCSD</a><br />";
    print "<p style='font-size: 0.8em; font-style:italic'>Funding provided by the National Science Foundation (NSF 0425926).</p>";
    #print "<hr>";
    #print "<p class=\"credits\">Questions? Comments? Suggestions? Please see the ";
    #print "<a href=\"http://groups.google.com/group/CellCircuits\">archives</a>";
    #print "or send email to <a href=\"mailto:CellCircuits@googlegroups.com?subject=[CellCircuits]\">";
    #print "CellCircuits@googlegroups.com</a></p>";
    print "</center>";
    print "</div>";
    print "</body></html>";
    return;
}

1;
