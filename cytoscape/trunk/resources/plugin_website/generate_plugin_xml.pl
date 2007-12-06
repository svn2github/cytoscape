# This script takes the information in the cyplugindb database and creates the plugins.xml file
# in a web-accessible location for the Cytoscape Plugin Manager to read.
# Call generate_plugin_xml.pl -help for information on usage. 

use strict;
use warnings;

use Data::Dumper;
use XML::DOM;
use Getopt::Long;
use DBI;

# constants
use constant CYTOSCAPE_URL => "http://cytoscape.org";
use constant DATABASE_NAME => "cyplugindb";
use constant PLUGIN_DL_SCRIPT => "pluginjardownload.php";

#Globals
my $dbh;
my ($BaseUrl, $XmlFileLoc, $HttpLoc, $Host, $UserName, $PassWord, $Help);

GetOptions ("loc=s" => \$XmlFileLoc,
						"url=s" => \$BaseUrl,
						"host=s" => \$Host, 
						"user=s" => \$UserName, 
						"passwd=s" => \$PassWord, 
						"help" => \$Help);


usage("Help:") if ($Help);
usage("ERROR: Missing required parameter:") if (!$BaseUrl || !$XmlFileLoc || !$UserName || !$PassWord);

$Host = 'localhost' if (!$Host);
my $XmlFile = $XmlFileLoc . "/plugins.xml";

dbConnect();

# Create the xml document
my $Doc = XML::DOM::Document->new();
my $XMLPi = $Doc->setXMLDecl($Doc->createXMLDecl ('1.0', 'UTF-16'));
my $Root = $Doc->createElement("project");
$Doc->appendChild($Root);
addProjectInfo();

my $PluginList = $Doc->createElement("pluginlist");
$Root->appendChild($PluginList);

my $AllPlugins = $dbh->selectall_hashref
	("SELECT * FROM plugin_list", 'plugin_auto_id');
foreach my $id (keys %$AllPlugins)
	{ addPlugin(%{$AllPlugins->{$id}}); }

#my $ThemeList = $Doc->createElement("themes");
#$Root->appendChild($ThemeList);
#my $AllThemes = $dbh->selectall_hashref
#	("SELECT * FROM theme_list", 'theme_auto_id');
#foreach my $id (keys %$AllThemes)
#	{ addTheme(%{$AllThemes->{$id}}); }



# format the xml in some basic manner, doesn't seem to be a nice lib for pretty formatting of xml
#my $Xml = $Doc->toString();
my $Xml = $Root->toString();
$Xml =~  s/></>\n</g;
$Xml =~ s/^\s+//;

open(XMLOUT, ">$XmlFile") || die "Failed to open $XmlFile: $!";
print XMLOUT $Xml;
close(XMLOUT);


# ------------ FUNCTIONS --------------- #
sub createBasicElement
	{
	my %args = @_;
		
	my $Element = $Doc->createElement($args->{"elementName"});

	# create a unique id for the theme, can't change from one version to another
	my $Id = $Doc->createElement("uniqueID");
	$Id->appendChild($Doc->createTextNode($args{'unique_id'}));
	$Element->appendChild($Id);

	my $Name = $Doc->createElement("name");
	$Name->appendChild($Doc->createTextNode($args{'name'})); 
	$Element->appendChild($PluginName);
	
	my $Desc = $Doc->createElement("description");
	$args{'description'} = stripBadHex($args{'description'});
	$Desc->appendChild($Doc->createCDATASection($args{'description'}));
	$Element->appendChild($PluginDesc);
	
	return $Element;
	}

sub addTheme
	{
	my %args = @_;
	$args{"elementName"} = "theme";

	my $ThemeElement = createBasicElement(%args)
	
	# add versions
	}
	
# Create the xml for a single plugin
sub addPlugin
	{
	my %args = @_;

	my $PluginElement = $Doc->createElement("plugin");

	# create a unique id, this can't change from one version of a plugin to another! 
	# Name and auto_id should work
	my $Id = $Doc->createElement("uniqueID");
	$Id->appendChild($Doc->createTextNode($args{'unique_id'}));
	$PluginElement->appendChild($Id);

	my $PluginName = $Doc->createElement("name");
	$PluginName->appendChild($Doc->createTextNode($args{'name'})); 
	$PluginElement->appendChild($PluginName);
	
	my $PluginDesc = $Doc->createElement("description");
	$args{'description'} = stripBadHex($args{'description'});
	$PluginDesc->appendChild($Doc->createCDATASection($args{'description'}));
	$PluginElement->appendChild($PluginDesc);

	# add the license if requried	
	if ($args{'license_required'} =~ /yes/i)
		{
		my $License = $Doc->createElement("license");
		my $LicenseText = $Doc->createElement("text");

		# these are bad hex chars, illegal for xml
		$args{'license'} = stripBadHex($args{'license'});

		$LicenseText->appendChild($Doc->createCDATASection($args{'license'}));
		$License->appendChild($LicenseText);
		$PluginElement->appendChild($License);
		}

	# add a category
	my ($CategoryName) = $dbh->selectrow_array("SELECT name FROM categories WHERE category_id = ".$args{'category_id'});
	my $Category = $Doc->createElement("category");
	$Category->appendChild($Doc->createTextNode($CategoryName));
	$PluginElement->appendChild($Category);

	addVersions($args{'plugin_auto_id'}, $PluginElement);
	}

# create the <plugin> element for each version of the plugin
sub addVersions
	{
	my $PluginId = shift || die "Missing plugin id";	
	my $VersionPlugin = shift || die "Missing required plugin element";

	my $VersionOk = 0;
	
	# get all possible versions
	# Note: only pull out those versions whose status is "published", exclude those with status "new", i.e. pending to be curated by CytoStaff
	my $Versions = $dbh->selectall_hashref
		("SELECT cy_version, version, plugin_file_id, version_auto_id  
			FROM plugin_version 
			WHERE plugin_id = $PluginId and status = 'published'", 'version_auto_id');

	my $CytoVersion = $Doc->createElement("cytoscapeVersions");
	foreach my $vid (keys %$Versions)
		{
		foreach my $cyVers (split /,/, $Versions->{$vid}->{'cy_version'}) 
			{
			my $Version = $Doc->createElement("version");	
			$Version->appendChild($Doc->createTextNode($cyVers));
		
			$CytoVersion->appendChild($Version);
			}
		$VersionPlugin->appendChild($CytoVersion);

		
		my $PluginVersionNum = $Doc->createElement("pluginVersion");
		$PluginVersionNum->appendChild($Doc->createTextNode($Versions->{$vid}->{'version'}));
		$VersionPlugin->appendChild($PluginVersionNum);		

		# add all authors for this version	
		if (addAuthors($vid, $VersionPlugin) == 1) { $VersionOk = 1; }
		else 
			{ 
			$VersionOk = 0;
			warn "Failed to add authors to version $vid\n";
			} 
		
		if (addFileInfo($Versions->{$vid}->{'plugin_file_id'}, $VersionPlugin) == 1)
			{ $VersionOk = 1; }
		else 
			{ 
			$VersionOk = 0; 
			warn "Failed to add file information to version $vid\n";
			}
		
		$PluginList->appendChild($VersionPlugin) if ($VersionOk);
		}
	}

# add the <filetype> and <url> elements to each <plugin>
sub addFileInfo
	{
	my $PluginFileId = shift || return 0;
	my $PluginElement = shift || die "Missing required plugin element";

	my ($FileType, $FileName) = $dbh->selectrow_array
		("SELECT file_type, file_name 
			FROM plugin_files 
			WHERE plugin_file_auto_id = $PluginFileId");

	if (!$FileName)
		{
		warn "Missing a plugin name for plugin_file_auto_id $PluginFileId";
		return 0;
		}
		
	my $PluginFileType = $Doc->createElement("filetype");
	$FileName =~ s/\s+$//; # in case they get inserted in the db with spaces at the end
	if ($FileName =~ /.*\.(jar|zip)$/i)
		{ $FileType = $1; }
	else
		{
		if (isJar($FileType))
			{ $FileType = "jar"; }
		elsif (isZip($FileType))
			{ $FileType = "zip"; }
		}
	 #print "$PluginFileId: $FileType : $FileName\n";

		
	$PluginFileType->appendChild($Doc->createTextNode($FileType));
	$PluginElement->appendChild($PluginFileType);
	
	my $FileUrl = $BaseUrl . "/" . PLUGIN_DL_SCRIPT . "?id=$PluginFileId"; 

	my $PluginUrl = $Doc->createElement("url");
	$PluginUrl->appendChild($Doc->createTextNode($FileUrl));
	$PluginElement->appendChild($PluginUrl);
	
	return 1;
	}

# add the <authorlist> element and get each author to add to the <plugin>
sub addAuthors
	{
	my $PluginVersionId = shift || return 0;
	my $PluginElement = shift || die "Missing required plugin element";
	
	my $AuthorList = $Doc->createElement('authorlist');
	$PluginElement->appendChild($AuthorList);
	
	my $sql = "SELECT names, affiliation FROM plugin_author 
							INNER JOIN authors
								ON author_auto_id = author_id
							WHERE plugin_version_id = $PluginVersionId
							ORDER BY authorship_seq";
	my $Authors = $dbh->selectall_arrayref($sql);
	
	return 0 if (@$Authors == 0);
	
	foreach my $author (@$Authors)
		{
		my $AuthorElement = $Doc->createElement('author');
		my $Name = $Doc->createElement('name');
		$Name->appendChild($Doc->createTextNode($author->[0]));
		$AuthorElement->appendChild($Name);
		
		my $Inst = $Doc->createElement('institution');
		$Inst->appendChild($Doc->createTextNode($author->[1]));
		$AuthorElement->appendChild($Inst);
		
		$AuthorList->appendChild($AuthorElement);
		}
	return 1;
	}

# set up the <project> element and the global info for the xml doc
sub addProjectInfo
	{
	my $Name = $Doc->createElement("name");
	$Name->appendChild($Doc->createTextNode("Cytoscape Plugins"));
	
	my $Desc = $Doc->createElement("description");
	$Desc->appendChild($Doc->createTextNode("These plugins have been submitted to Cytoscape by their authors, 
		they may have their own licensing requirements.  Please contact the plugin authors with any questions."));
	
	my $ProjUrl = $Doc->createElement("url");
	$ProjUrl->appendChild($Doc->createTextNode(CYTOSCAPE_URL));
	
	$Root->appendChild($Name);
	$Root->appendChild($Desc);
	$Root->appendChild($ProjUrl);
	}


sub isZip
	{
	# application/x-zip-compressed
	my $Type = shift || warn "No string passed to isZip method";
	$Type =~ /^application\/x-zip-compressed$/ ? return 1: return 0;
	}

sub isJar
	{
	# application/x-jar  application/x-java-archive
	my $Type = shift || warn "No string passed to isJar method";
	$Type =~ /^application\/x-[jar|java\-archive]/ ? return 1: return 0;
	}

# these are illegal for xml
sub stripBadHex
	{
	my $str = shift || warn "No string passed to strip hex from";
	$str =~ s/\x0b|\x0c|\x0d|\x0e|\x0f//g;
	$str =~ s/([\x00-\x7f])[\x80-\xbf]/$1/g; 
	return $str;
	}

# connect to database
sub dbConnect
	{
	my $DSN = "DBI:mysql:host=$Host;database=".DATABASE_NAME;
	my %attr;
	$dbh = DBI->connect($DSN, $UserName, $PassWord, \%attr);
	}


sub usage
	{
	my $Msg = shift;
	print qq($Msg
Usage: $0 -loc <some dir> -host <db host> -user <db user name> -passwd <db password>

  Parameter   Description                          Optional/Required
  -loc 	:     Location to write resulting XML file to.  REQUIRED
  -url	:     Base url for the pluginjardownload.php    REQUIRED 
              used for the plugin download url.
  -user :     Database user name.                       REQUIRED
  -passwd :   Database password.                        REQUIRED
  -host :     Database host name,                       OPTIONAL
              defaults to localhost                       

  [-help : See this message]
);
	exit(-1);
	}
	



