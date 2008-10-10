package CCDB::HtmlRoutines;

require Exporter;

use strict;
use warnings;

use CCDB::DB;
use CCDB::Model;
use CCDB::Enrichment;
use CCDB::Error;
use CGI qw(:standard unescape escape);

use CCDB::Query qw(&get_species_string);

use CCDB::Constants qw($cgi_version
		       $search_url
		       $cgi_url
		       $data_url
		       $chianti_url
		       $db_link_by_species 
		       $colors 
		       $species_abbrev
		       $pubInfo
		       );

our @ISA = qw(Exporter);
our @EXPORT = qw();
our @EXPORT_OK = qw(highlight gen_go_url $TRAILER format_header query_form);
our $VERSION = 1.0;

our $dbh = CCDB::DB::getDB();

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


our $TRAILER =  qq (
    <br /><br /><center>
    <a class='white-bg-link' href='$search_url/index.html' title='Click to go to the CellCircuits Home Page'>CellCircuits&nbsp;Home</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class='white-bg-link' href='$search_url/advanced_search.php'>Advanced Search</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class='white-bg-link' href='$search_url/about_cell_circuits.html'>About CellCircuits</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class='white-bg-link' href='$search_url/tutorial/Tutorial-home.html'>Help</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class='white-bg-link' href='http://chianti.ucsd.edu/idekerlab/index.html'>Ideker Lab</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class='white-bg-link' href='http://ucsd.edu'>UCSD</a><br />
    <p style='font-size: 0.8em; font-style:italic'>Funding provided by the National Science Foundation (NSF 0425926).</p>
	
    </center>
);


sub outputResultsPage{
    my ($queryInput,
	$publications,
	$species,
	$sort_method,
	$pval_thresh,
	$page,
	$hash,
	$n_matched_models,
	$error_msg,
	$gid_by_gene_symbol
	) = @_;

	# populate the model->wordsMatched()	 			 
	foreach my $mid (keys %{ $hash })
    {
		my @enrichment_objects = @{ $hash->{$mid}{"enrichment"} };
		my $model = $hash->{$mid}{'model'};
		$model->score_model($queryInput, \@enrichment_objects);
    }

	# set the statistics data in error_msg
	my $speciesWordsHash = getMatchingStatistics($hash, $species, $queryInput);
	$error_msg->{'matchingStat'} = $speciesWordsHash;

    print format_header(format_query_as_title($queryInput->queryString()));
    print query_form($queryInput->queryString(), 
		     $publications, 
		     $species, 
		     $sort_method, 
		     $pval_thresh,
			 $page);
	
    print error_html($publications, $pval_thresh, $error_msg);
    
    print_results($queryInput,
		  $hash,	      
		  $page,
		  $publications, 
		  $species, 
		  $sort_method,
		  $pval_thresh, 
		  $error_msg, 
		  $gid_by_gene_symbol);

    print trailer();
    return;
}


# get the statistics from $hash and put it in $error_msg
sub getMatchingStatistics {
	my ($hash, $species,  $queryInput) = @_;
	
	my %speciesWordsHash	= ();
	my %speciesIdNameHash	= (); # species Id->Name
	
    foreach my $mid (keys %{ $hash })
    {
		# determine the species of this model
		my @enrichment_objects = @{ $hash->{$mid}{"enrichment"} };
		my %unique_speciesID;
		foreach my $eo (@enrichment_objects) {
			if(!exists($unique_speciesID{$eo->sid()}))
			{
				$unique_speciesID{$eo->sid()}=1;
				if (!exists($speciesIdNameHash{$eo->sid()})) {
					$speciesIdNameHash{$eo->sid()} = get_species_string($eo->sid());
				}
			}			
		}
		
		my $model = $hash->{$mid}{"model"};
		my $words_matched  = $model->wordsMatched();
		foreach my $w (keys %{ $words_matched }) 
		{
			foreach my $sid (keys %unique_speciesID) 
			{
				$speciesWordsHash{$speciesIdNameHash{$sid}}{$w}++;				
			}
		}
    }
	
	# Fill zeroes for those not matched in expandedQuery
	my @expandedQueryWords = (); #getQueryWords($queryInput);
    foreach my $q (keys %{$queryInput->expandedQuery()})
    {
		#print "expandedQuery =",$q,"<br>" ;
		push(@expandedQueryWords, $q);
	}	
	foreach my $oneSpecies (keys %speciesWordsHash) {
		foreach my $w (@expandedQueryWords)
		{
			if (!exists($speciesWordsHash{$oneSpecies}{$w})) {
				$speciesWordsHash{$oneSpecies}{$w} = 0;
			}
		}	
	}
	
	# Fill zeroes for those not matched
	my @noMatchingQueryWords = keys %{$queryInput->noMatchingQueryWords()};
	foreach my $oneSpecies (keys %speciesWordsHash) {
		foreach my $w (@noMatchingQueryWords)
		{
			if (!exists($speciesWordsHash{$oneSpecies}{$w})) {
				$speciesWordsHash{$oneSpecies}{$w} = 0;
			}
		}	
	}
	
	return \%speciesWordsHash;
}


sub outputErrorPage
{
    my ($queryInput, # object
        $publications,   # ref-to-hash
	$species,        # ref-to-hash
	$sort_method,    # string
	$pval_thresh,
	$error_msg
        ) = @_;

    print format_header(format_query_as_title($queryInput->queryString()));
    print query_form($queryInput->queryString(),
		     $publications, 
		     $species, 
		     $sort_method,
		     $pval_thresh);
    print error_html($publications, $pval_thresh, $error_msg);
    print trailer();
    return;
}


sub format_query_as_title
{
    my ($original_query) = @_;
    return "Cell Circuits - Results: " . unescape($original_query);
}

sub format_header
{
    my ($title) = @_;
    
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
  <title>$title</title>
</head>
<body id='body-element'>
HEADER
    
    return $HEADER;
}

sub query_form
{
    my ($original_query, $publications, $species, $sort_method, $pval_thresh, $page) = @_;

    my $pub_html         = get_publication_hidden_fields_html($publications);
    my $species_html     = get_species_hidden_fields_html($species);         
    my $sort_method_html = get_sort_method_hidden_fields_html($sort_method);
    my $pval_thresh_html = get_pval_thresh_hidden_fields_html($pval_thresh);
    my $pagination_field_html = "<input type=\"hidden\" name=\"page\" value=\"$page\" />";
    
    my $unescaped_query = '\'' . unescape($original_query) . '\'';

    my $QUERY_FORM =<<QUERY_FORM;

<form method='POST' name='search' action='$cgi_url/search.pl'>
 
  <table align='center' border='0' cellspacing=0 cellpadding=0 summary='search interface'>
    <tr>
      <td align='right' rowspan=2>
	<a href='$search_url/index.html'><img src='$search_url/CC-logo-small.jpg' border='0' alt="Cell Circuits" title="Click to go to the Cell Circuits Home Page"/></a>
      </td>
      <td align="center" valign="bottom">
        <input type="text" size="55" name="search_query" value=$unescaped_query title='For information on valid queries, click "About CellCircuits" or "Help" to the right'/>
      </td>
      <td align='left' valign='center' rowspan=2>
         &nbsp;<a class='white-bg-link' href='$search_url/index.html' title='Click to go to the Cell Circuits Home Page'>CellCircuits&nbsp;Home</a><br />
	 &nbsp;<a class='white-bg-link' href='$search_url/advanced_search.php'>Advanced&nbsp;Search</a><br />
	 &nbsp;<a class='white-bg-link' href='$search_url/about_cell_circuits.html'>About&nbsp;CellCircuits</a>&nbsp;|&nbsp;<a class='white-bg-link' href='$search_url/tutorial/Tutorial-results-1.html'>Help</a>
      </td>
    </tr>
    <tr>
      <td align='center' valign='top'>
        <input type="submit" name="search_query_button" value="Search" title='Click to find models matching your query'/><input type="submit" value="Load Example Query" title="requires javaScript" onClick="LoadExampleQuery('rad51 GO:0006950 ercc1', 'DNA repair');return false;" />
      </td>
    </tr>
  </table>

$pub_html
$species_html
$sort_method_html
$pval_thresh_html
$pagination_field_html
</form>

QUERY_FORM

   return $QUERY_FORM;
}

sub error_html
{
    my ($publications, $pval_thresh, $error_msg) = @_;
    my $error_html = CCDB::Error::formatErrorMessages($error_msg, 
						      $pval_thresh, 
						      $publications);
    #return qq(
	#      <center>
	#      <table><tr><td align="left">
	#      $error_html
	#      </td></tr></table>
	#      </center>
	#      );
    return qq(
	      <table><tr><td align="left" bgcolor="#99CCFF">
	      $error_html
	      </td></tr></table>
	      );

}

sub trailer
{
    return $TRAILER . "</body></html>\n";
    
}

sub formatPageNavigation_tr
{
    my ($N_PAGE_JUMP, $page, $total_pages, $lower_lim, $upper_lim, $n_matched_models) = @_;
    
    my $page_html = "";
    
	$page_html .="<table>";
	$page_html .="<tr>";

	$page_html .="<td scope='col'>";
	
    # Add 'Previous' link if necessary
    if($page > 1){
		my $prev_page = $page - 1;
		$page_html .= tag("a", { class=>"white-bg-link",
					 #onclick=>"document.search.page.value=$prev_page; document.search.submit();", 
					 onclick=>"document.search.page.value=$prev_page; document.search.submit();", 
					 href=>"#"},
				  "Previous");
		$page_html .= "&nbsp;&nbsp;\n";
    }
	
	$page_html .="</td>";
	
	$page_html .="<td scope='col'>";

    ## Add 'Page' button and input box
    $page_html .= tag("input", 
		      { type=>"submit",
				name=>"page_jump_" . $N_PAGE_JUMP,
				#onclick=>"document.search.page.value=document.jump_to_page_${N_PAGE_JUMP}.value;",
				onclick=>"document.search.page.value=document.forms[1].jump_to_page.value; document.search.submit();",
	
				value=>"Page"});
    $page_html .= "&nbsp;\n";
	$page_html .="</td>";

	$page_html .="<td scope='col'>";
	
	# put the button and textfield in a form
	$page_html .= "<FORM NAME='pagination_form1' method='post'>";
	
    $page_html .= tag("input", { type=>"text",
				size=>3,
				#name=>"jump_to_page_" . $N_PAGE_JUMP,
				name=>"jump_to_page",
				value=>$page});

	$page_html .= "</FORM>";
	$page_html .="</td>";
			
	$page_html .="<td scope='col'>";
				
    $page_html .= "&nbsp;of $total_pages\n";

	$page_html .="</td>";

	$page_html .="<td scope='col'>";

    ## Add 'Next' link if necessary
    if($page < $total_pages){
	
		my $next_page = $page + 1;
		$page_html .= tag("a", { class=>"white-bg-link", 
					 #onclick=>"document.search.page.value=$next_page; document.search.submit();", 
					 onclick=>"document.search.page.value=$next_page; document.search.submit();", 
					 href=>"#"},
				  "Next");
		$page_html .= "&nbsp;&nbsp;\n";
    }
	
	$page_html .="</td>";

	$page_html .="</tr>";
	$page_html .="</table>";



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
	foreach my $pub (keys %{ $pubInfo }) {
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

#
sub print_results
{
    my ($queryInput,
	$hash,	      
	$page,
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
    
    ## $N_PAGE_JUMP keeps track of the number of page navigation boxes.
    ## Typically 2: one at the top of the table and one at the bottom.
    my $N_PAGE_JUMP = 1; 

	# move the following to outputResultsPage()
    #foreach my $mid (keys %{ $hash })
    #{
	#my @enrichment_objects = @{ $hash->{$mid}{"enrichment"} };
	#my $model = $hash->{$mid}{'model'};
	#$model->score_model($queryInput, \@enrichment_objects);
    #}

    my $sorted_mids = ();
	
	# Sorting
	if ($sort_method eq "optionB_by_size_of_model") {
	    @{ $sorted_mids } = sort {  
			       $hash->{$b}{"model"}->size() <=> $hash->{$a}{"model"}->size()			       
				|| $hash->{$b}{'model'}->score() <=> $hash->{$a}{'model'}->score()
			    } keys %{ $hash };
	}
	elsif ($sort_method eq "optionC_by_publication") {
	    @{ $sorted_mids } = sort {  
			       $hash->{$b}{"model"}->pub() <=> $hash->{$a}{"model"}->pub()			       
				|| $hash->{$b}{'model'}->score() <=> $hash->{$a}{'model'}->score()
			    } keys %{ $hash };
	}
	elsif ($sort_method eq "optionD_by_most_enriched_for_a_GO_term") {
	    @{ $sorted_mids } = sort {  
			       $hash->{$a}{"model"}->pvalue() <=> $hash->{$b}{"model"}->pvalue()			       
				|| $hash->{$b}{'model'}->score() <=> $hash->{$a}{'model'}->score()
			    } keys %{ $hash };
	}		
	else { # default -- optionA_by_number_of_query_terms_matching_model
	    @{ $sorted_mids } = sort { $hash->{$b}{'model'}->score() <=> $hash->{$a}{'model'}->score() 
			       || min_pval( $hash->{$a}{"enrichment"} ) <=> min_pval( $hash->{$b}{"enrichment"} )
			       || $hash->{$a}{"model"}->pub() <=> $hash->{$b}{"model"}->pub()	
			       } keys %{ $hash };
	}
	
	#print "sort_method = ",$sort_method,"<br>";
	#for my $mid (@{ $sorted_mids }) {
	#	print $hash->{$mid}{"model"}->score(),"<br>";
	#}
    my $n_matched_models = scalar(@{ $sorted_mids });
    my ($total_pages, $upper_lim,$lower_lim) = pageCalc($n_matched_models, $page);
    my $n_models_on_page = $upper_lim - $lower_lim + 1;
    
    my $bp_count = 0;
    my $cc_count = 0;
    my $mf_count = 0;
    for my $i (($lower_lim-1)..($upper_lim-1))
    {
	my $term2org = $hash->{$sorted_mids->[$i]}{'model'}->term2org();
	if(exists $term2org->{'biological_process'})
	{ 
	    $bp_count += scalar(keys %{$term2org->{'biological_process'} });
	}
	if(exists $term2org->{'cellular_component'})
	{ 
	    $cc_count += scalar(keys %{$term2org->{'cellular_component'} });
	}
	if(exists $term2org->{'molecular_function'})
	{ 
	    $mf_count += scalar(keys %{$term2org->{'molecular_function'}});
	}
    }

    my $body .= qq(<br><table id="results_table" name="results_table" align="center" 
		   cellpadding="0" cellspacing="0" 
		   bgcolor="$colors->{page_background}" 
		   summary="results" width="100%">
		   <input type="hidden" name="page" value="$page"/>
		   );

    # The "page" hidden field keeps track of what page we are on

    $body .= formatPageNavigation_tr($N_PAGE_JUMP++, $page, $total_pages, $lower_lim, $upper_lim, $n_matched_models);

    $body.=<<TBL_HDR;

   <tr class="extra-header">
   <td align='center' colspan=2>
<a class="color-bg-link" href="http://www.geneontology.org/" title="Gene Ontology">GO</a> annotation 
<a class="color-bg-pval-link" href="$search_url/advanced_search.php" title="Click to change the p-value threshold on the Advanced Search page"> P-value < $pval_thresh</a>
   </td>
   <td align='right' colspan=2>Show/Hide:&nbsp;
   <a class="group-toggle-link" href="#" title='Show/Hide all Biological Process results on this page' onClick="CategoryVisibility_GroupToggle('bp','',$bp_count); return false;">&nbsp;Biological Process&nbsp;</a>&nbsp;&nbsp; 
   <a class="group-toggle-link" href="#" title='Show/Hide all Cellular Component results on this page' onClick="CategoryVisibility_GroupToggle('cc','',$cc_count); return false;">&nbsp;Cellular Component&nbsp;</a>&nbsp;&nbsp;
   <a class="group-toggle-link" href="#" title='Show/Hide all Molecular Function results on this page' onClick="CategoryVisibility_GroupToggle('mf','',$mf_count); return false;">&nbsp;Molecular Function&nbsp;</a>
   </td>
   </tr>

   <tr class="group-toggle-link">
     <th class="result-header" title="# Distinct Matches">Score</th>
     <th class="result-header">Model</th>
     <th class="result-header">Matches</th>
     <th class="result-header" colspan="1" valign='bottom'>
     Model annotation [<a class="color-bg-link" href="/cellcircuits/search/tutorial/Tutorial-results-2.html#row4_explanation" title="Go to tutorial">read more</a>]&nbsp;&nbsp;
     </th>
   </tr>
TBL_HDR

    print $body;
    for my $i (($lower_lim-1)..($upper_lim-1))
    {
	my $mid = $sorted_mids->[$i];
	print_model($hash, $mid, $queryInput->expandedQuery(), $counts, $pval_thresh);
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
    my ($hash, $mid, $expanded_query, $counts, $pval_thresh) = @_;

    my $mo = $hash->{$mid}{'model'};
    my $pub     = $mo->pub();
    my $sif     = $mo->sif();
    my $thm_img = $mo->thm_img();
    my $lrg_img = $mo->lrg_img();
    my $legend  = $mo->legend();
    
    my @enrichment_objects = @{ $hash->{$mid}{'enrichment'} };

    my $html = "<tr>";
    $html .= format_model_thm_td($mo, $pub, $sif, $lrg_img, $thm_img, $legend);
    $html .= format_query_matched_td($mo->wordsMatched());
    $html .= format_all_eo_td($expanded_query, $mo, \@enrichment_objects, $counts, $pval_thresh);
    $html .= "   </tr>\n";

    print $html;
    return;
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
    my ($expanded_query, $model, $eo_list, $counts, $pval_thresh) = @_;
    
    # index eo's by species and then by GO term type (eg MF, BP, or CC)
    my $eo_hash = {};
    foreach my $eo (@{$eo_list})
    {
	my $sp = get_species_string($eo->sid());
	push @{ $eo_hash->{$sp}{$eo->ttype()} }, $eo;
    }

    # now format the eo's for each species
    my $h = qq(    <td class="search-result-right" align="left" valign="top">\n);

    $h .= tag("a", 
	      {class=>"white-bg-link",
	       title=>"See models that contain genes from this model (currently Yeast only)",
	       href=>"$cgi_url/search.pl?search_query=MODELS_LIKE:" . $model->id()
	       }, 
	      "View similar models");
    $h .= "<br><hr>";

    foreach my $sp (sort keys %{$eo_hash})
    {
	$h .= format_eo_for_species($expanded_query, $model, $sp, $eo_hash->{$sp}, $counts, $pval_thresh);
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
    my ($expanded_query, $model, $species, $eo_by_ttype, $counts, $pval_thresh) = @_;

    my $html = "";
    $html .= qq(<table cellspacing=0 width=400><tr><td>\n);
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
	    
	    $html .= format_eo($expanded_query, $model, $eos[$i]) . "\n";
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
    my ($expanded_query, $model, $eo) = @_;

    my $pub = $model->pub();
    my $thm_img = $model->thm_img();

    my $eid                        = $eo->id();                         
    my $pval                       = $eo->pval();                       
    my $tacc                       = $eo->tacc();                       
    my $tname                      = $eo->tname();                      
    my $n_genes_in_model_with_term = $eo->n_genes_in_model_with_term(); 

    my $org = get_species_string($eo->sid()); #  join(" ", $eo->genus(), $eo->species());


	#my $thm_img_file_name = ${thm_img}.$pubInfo->{$pub}->{img_format};
	
	my ($lrg_img_file_id, $thm_img_file_id) = getImageFileIDs_enrichment(${thm_img},$pubInfo->{$pub}->{img_format});
	

    ## Genes in model and enrichment
    my $h = qq( <td align="center">\n);

    my $url = sprintf("$cgi_url/get_genes_by_eid.pl?eid=%s&lrg_image_file_id=%s&thm_image_file_id=%s",
		      $eid, $lrg_img_file_id, $thm_img_file_id);  #"$data_url/${thm_img}.$pubInfo->{$pub}->{img_format}");
    
    foreach my $g (@{ $model->eid2genes()->{ $eid } })
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

# This is a work-around
sub getImageFileIDs_enrichment {
	my ($image_info, $img_format) = @_;

	my @tmpArray = split('/', $image_info);
	
	my $pub = $tmpArray[0];
	my $image_file_name = $tmpArray[2].".".$img_format;
	
	### Get large image file id
	my $lrg_image_file_id = -1;

	my $dbQuery = "SELECT network_file_info.image_file_id as id ";
	$dbQuery .=   "FROM network_file_info, network_image_files ";
	$dbQuery .=   "WHERE network_file_info.publication_id = $pub AND network_image_files.file_name='$image_file_name' ";
	$dbQuery .=   "AND network_file_info.image_file_id = network_image_files.id";
	
	my $sth = $dbh->prepare($dbQuery);
	$sth->execute();
	
	# should be one record only
	while (my $ref = $sth->fetchrow_hashref()) {
		$lrg_image_file_id = $ref->{'id'}; 
	}

	### Get large image file id
	my $thum_image_file_id = -1;

	$dbQuery = "SELECT network_file_info.thum_image_file_id as id ";
	$dbQuery .=   "FROM network_file_info, network_thum_image_files ";
	$dbQuery .=   "WHERE network_file_info.publication_id = $pub AND network_thum_image_files.file_name='$image_file_name' ";
	$dbQuery .=   "AND network_file_info.thum_image_file_id = network_thum_image_files.id";
	
	$sth = $dbh->prepare($dbQuery);
	$sth->execute();
	
	# should be one record only
	while (my $ref = $sth->fetchrow_hashref()) {
		$thum_image_file_id = $ref->{'id'}; 
	}

	my @image_file_ids = ($lrg_image_file_id,$thum_image_file_id);

	return @image_file_ids;
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
	    $h .= $w . "<br/>";
	    $unique_words{uc($w)}++;
	}

	#if((++$i % 3)==0){ $h .= "<br>"; }
	#else { $h .= " "; }
    }

    return tag("td", 
	       { class=>"search-result", align=>"center", 
		 valign=>"top", bgcolor=>"$colors->{page_background}" },
	       tag("p",
		   {class=>"highlighted"}, $h)
	       );
}

# image_file_IDs for networkImage and thumImage
sub getImageFileIDs {
 my ($pubName, $modelName) = @_;

	my $img_format = $pubInfo->{$pubName}->{img_format};
	my $dbQuery = "select network_image_files.id as id from network_file_info, network_image_files where network_file_info.image_file_id = network_image_files.id AND network_file_info.publication_id = $pubName AND network_image_files.file_name = '$modelName.$img_format'";
	
	my $get_image_file_id_sth = $dbh->prepare($dbQuery);
	$get_image_file_id_sth->execute();
	
	my $image_file_id = -1;
	
	# should be one record only
	while (my $ref = $get_image_file_id_sth->fetchrow_hashref()) {
		$image_file_id = $ref->{'id'}; 
	}

	# thum image
	my $thum_image_file_id = -1;

	$dbQuery = "select network_thum_image_files.id as id from network_file_info, network_thum_image_files where network_file_info.thum_image_file_id = network_thum_image_files.id AND network_file_info.publication_id = $pubName AND network_thum_image_files.file_name = '$modelName.$img_format'";
	
	#print "dbQuery = <br>$dbQuery<br>";
	
	$get_image_file_id_sth = $dbh->prepare($dbQuery);
	$get_image_file_id_sth->execute();
		
	# should be one record only
	while (my $ref = $get_image_file_id_sth->fetchrow_hashref()) {
		$thum_image_file_id = $ref->{'id'}; 
	}

	my @image_file_ids = ($image_file_id,$thum_image_file_id);

	return @image_file_ids;
}


sub getSifFileID {
	my ($pubName, $modelName) = @_;
	
	my $dbQuery = "select network_files.id as id from network_file_info, network_files where network_file_info.network_file_id = network_files.id AND network_file_info.publication_id = $pubName AND network_files.file_name = '$modelName.sif'";
	
	my $get_sif_file_id_sth = $dbh->prepare($dbQuery);
	$get_sif_file_id_sth->execute();
	
	my $sif_file_id = -1;
	
	# should be one record only
	while (my $ref = $get_sif_file_id_sth->fetchrow_hashref()) {
		$sif_file_id = $ref->{'id'}; 
	}

	return $sif_file_id;
}


sub format_model_thm_td
{
    my($model, $pub,$sif,$lrg_img,$thm_img,$legend) = @_;
    
    my $score = "Query";
    if(!$model->isQueryModel())
    {
	$score = $model->score();
    }
    my $name = $model->name();

	my @tmpArray = split '/', $thm_img;
	my $pubName = $tmpArray[0];
	my $modelName = $tmpArray[2];
	
	#print "pubName = $pubName\n";
	#print "modelName = $modelName\n";

	# image_file_IDs for networkImage and thumImage
	my ($lrg_image_file_id, $thm_image_file_id) = getImageFileIDs($pubName, $modelName);
	
    my $model_thm_html = <<MODEL_HTML;
      <td class='search-result' align='center' valign='top' >$score</td>
      <td class='search-result' align='center' valign='top' bgcolor='white'>
	  
         <a href='$search_url/getNetworkImage.php?image_type=network_image&image_file_id=$lrg_image_file_id&return_type=html'>
            <img src='$search_url/getNetworkImage.php?image_type=network_thm_image&image_file_id=$thm_image_file_id' border='0'>
         </a><br />
		  
	 <b class='pub-citation'></b><br>
	 <b class='pub-citation'>$pubInfo->{$pub}->{name} [model:&nbsp;$name]</b><br>
	 <a class='white-bg-link' href='$pubInfo->{$pub}->{citation}' title='PubMed abstract'>[PubMed]</a>
MODEL_HTML
      
   if(exists($pubInfo->{$pub}->{supplement_URL}) &&
       $pubInfo->{$pub}->{supplement_URL} ne "") 
    { 
        $model_thm_html .= "<a class='white-bg-link' href='$pubInfo->{$pub}->{supplement_URL}' title='Online publication supplement'>[web site]</a>\n";
    }

	my $sif_file_id = getSifFileID($pubName, $modelName);
	my $legend_id = 0;
	
#### WITHOUT POPUP ####
    $model_thm_html .= <<MODEL_HTML2;
         <a class="white-bg-link" href="$search_url/get_legend.php?pub_id=$pubName" title='Legend and FAQ for [$pubInfo->{$pub}->{name}] and its models.'>[legend]</a>
         <a class="white-bg-link" href="$search_url/file_download.php?file_type=sif&file_id=$sif_file_id" title='(s)imple (i)nteraction (f)ormat: a textual representation of this model.'>[sif]</a>
	 <br>
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

1;
