# This script takes the information in the cyplugindb database and creates the plugins.xml file
# in a web-accessible location for the Cytoscape Plugin Manager to read.
# Call generate_plugin_xml.pl -help for information on usage. 

use strict;
#use warnings;

use Data::Dumper;
use XML::DOM;
use Getopt::Long;
use DBI;

# constants
use constant CYTOSCAPE_URL => "http://cytoscape.org";
use constant DATABASE_NAME => "cyplugindb";
use constant PLUGIN_DL_SCRIPT => "pluginjardownload.php";


#Globals
my %xmlEl = (
	'id' => "uniqueID",
	'name' => "name",
	'desc' => "description",
	'license' => "license",
	'cy_vers' => "cytoscapeVersions",
	'version' => "version",
	'pl_vers' => "pluginVersion",
	'th_vers' => "themeVersion",
	'plugin_list' => "pluginlist",
	'theme_list' => "themes",
	'url' => "url"
);

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




# --- create the plugin list --- #

my $PluginListEl = $Doc->createElement($xmlEl{'plugin_list'});

my $Plugins = retrievePlugins();
foreach my $plugin (@$Plugins) 
	{
	my $Plugin = createPluginNode(@$plugin);
	$PluginListEl->appendChild($Plugin) if $Plugin;
	}
$Root->appendChild($PluginListEl);

# --- create the themes --- #
my $ThemeListEl = $Doc->createElement($xmlEl{'theme_list'});

my $themeSql = qq(
SELECT DISTINCT L.theme_auto_id, L.name, L.unique_id, L.description, 
	V.version_auto_id, V.cy_version, V.version, V.release_date
FROM theme_list L
  INNER JOIN theme_version V
    ON L.theme_auto_id = V.theme_id
);

my $Themes = $dbh->selectall_arrayref($themeSql);
foreach my $theme (@$Themes)
	{
	my $Theme = createThemeNode(@$theme);
	$ThemeListEl->appendChild($Theme) if $Theme;
	}
$Root->appendChild($ThemeListEl);

# format the xml in some basic manner, doesn't seem to be a nice lib for pretty formatting of xml
#my $Xml = $Doc->toString();
my $Xml = $Root->toString();
$Xml =~  s/></>\n</g;
$Xml =~ s/^\s+//;

open(XMLOUT, ">$XmlFile") || die "Failed to open $XmlFile: $!";
print XMLOUT $Xml;
close(XMLOUT);



# -------------------- SUBS --------------------- #

sub retrievePlugins
	{
	my %args = @_; 

	my $ThemeOnly = $args{'theme_only'};
	my $PluginVersionId = $args{'plugin_vers_id'};

	my $pluginSql = qq(
SELECT DISTINCT L.plugin_auto_id, L.name, L.unique_id, L.description, L.license, L.license_required,
C.name, V.version, V.release_date, V.cy_version, V.plugin_file_id, V.version_auto_id, V.theme_only
FROM plugin_list L
  INNER JOIN plugin_version V
    ON L.plugin_auto_id = V.plugin_id
  INNER JOIN categories C 
    ON L.category_id = C.category_id
WHERE V.status = 'published' 
); 

	$pluginSql .= "AND V.theme_only = 'no' " if (!$ThemeOnly || $ThemeOnly eq 'no');
	$pluginSql .= "AND V.version_auto_id = $PluginVersionId " if ($PluginVersionId);
	$pluginSql .= "ORDER BY L.name";

	return $dbh->selectall_arrayref($pluginSql);
	}

# set up the <project> element and the global info for the xml doc
sub addProjectInfo
	{
	my $Name = $Doc->createElement($xmlEl{'name'});
	$Name->appendChild($Doc->createTextNode("Cytoscape Plugins"));
	
	my $Desc = $Doc->createElement($xmlEl{'desc'});
	$Desc->appendChild($Doc->createTextNode("These plugins have been submitted to Cytoscape by their authors, 
		they may have their own licensing requirements.  Please contact the plugin authors with any questions."));
	
	my $ProjUrl = $Doc->createElement($xmlEl{'url'});
	$ProjUrl->appendChild($Doc->createTextNode(CYTOSCAPE_URL));
	
	$Root->appendChild($Name);
	$Root->appendChild($Desc);
	$Root->appendChild($ProjUrl);
	}

sub createIdNode
	{
	my $Id = shift;
	my $IdEl = $Doc->createElement($xmlEl{'id'});
	$IdEl->appendChild($Doc->createTextNode($Id));
	return $IdEl;
	}

sub createNameNode
	{
	my $Name = shift;
	my $NameEl = $Doc->createElement($xmlEl{'name'});
	$NameEl->appendChild($Doc->createTextNode($Name));
	return $NameEl;
	}

sub createDescNode
	{
	my $Desc = shift;
	my $DescEl = $Doc->createElement($xmlEl{'desc'});
	$DescEl->appendChild($Doc->createTextNode( stripBadHex($Desc) ));
	return $DescEl;
	}
	
sub createCyVersionNode
	{
	my $CyVersion = shift;
	my $CyVersionsEl = $Doc->createElement($xmlEl{'cy_vers'});
	my @cyVersions = split(/,/, $CyVersion);
	
	foreach my $v (@cyVersions)
		{ 
		my $VersionEl = $Doc->createElement($xmlEl{'version'});
		$VersionEl->appendChild($Doc->createTextNode($v));
		$CyVersionsEl->appendChild($VersionEl);
		}
		
	return $CyVersionsEl;
	}

sub createReleaseDateNode
	{
	my $ReleaseDate = shift;
	my $RelDateEl = $Doc->createElement('release_date');
	$RelDateEl->appendChild($Doc->createTextNode($ReleaseDate));
	return $RelDateEl;	
	}

sub createVersionNode
	{
	my $VersionElName = shift;
	my $Version = shift;
	
	my $VersEl = $Doc->createElement($VersionElName);
	$VersEl->appendChild($Doc->createTextNode($Version));
	return $VersEl;
	}

sub createThemeNode
	{
	my @theme = @_;
	
	my ($ThemeAutoId, $Name, $UniqueId, $Description, 
	    $ThemeVersionId, $CyVersion, $ThemeVersion, $ReleaseDate) = @theme;
	    
	my $ThemeEl = $Doc->createElement('theme');
	
	$ThemeEl->appendChild( createIdNode($UniqueId) );
	$ThemeEl->appendChild( createNameNode($Name) );
	$ThemeEl->appendChild( createDescNode($Description) );
	$ThemeEl->appendChild( createCyVersionNode($CyVersion) );
	
	my $ThemeVersEl = $Doc->createElement($xmlEl{'th_vers'});
	$ThemeVersEl->appendChild($Doc->createTextNode($ThemeVersion));
	$ThemeEl->appendChild($ThemeVersEl);
	
	# get plugin ids
	my $PluginIds = $dbh->selectcol_arrayref
		("SELECT plugin_version_id FROM theme_plugin 
			WHERE theme_version_id = $ThemeVersionId");

	my $PluginListEl = $Doc->createElement($xmlEl{'plugin_list'});

	# add plugins
	foreach my $pluginId (@$PluginIds)
		{
		my $Plugins = retrievePlugins('theme_only' => 1, 'plugin_vers_id' => $pluginId);
		foreach my $plugin (@$Plugins)
			{
			my $PluginEl = undef;

			if ($plugin->[12] eq 'yes')
				{
				$PluginEl = createPluginNode(@$Plugins);
				}
			else # create the short hand version
				{
	#			<plugin>
	#				<uniqueID>goodZIPPlugin777</uniqueID>
	#				<pluginVersion>0.45</pluginVersion>
	#			</plugin>
				$PluginEl = $Doc->createElement('plugin');
				$PluginEl->appendChild( createIdNode( $plugin->[2] ) );
				$PluginEl->appendChild( createVersionNode($xmlEl{'pl_vers'}, $plugin->[7]) );
				} 
			$PluginListEl->appendChild($PluginEl) if $PluginEl;		
			}
		}
	$ThemeEl->appendChild($PluginListEl);
	return $ThemeEl;
	}

sub createPluginNode
	{
	my @plugin = @_;
	my ($PluginAutoId, $PluginName, $UniqueId, $Description, 
			$License, $LicenseReq, $Category, $PluginVersion, 
			$ReleaseDate, $CyVersion, $PluginFileId, 
			$PluginVersionId, $ThemeOnly) = @plugin;

	my $PluginEl = $Doc->createElement('plugin');
	
	$PluginEl->appendChild( createIdNode($UniqueId) );
	$PluginEl->appendChild( createNameNode($PluginName) );
	$PluginEl->appendChild( createDescNode($Description) );

	my $CategoryEl = $Doc->createElement('category');
	$CategoryEl->appendChild($Doc->createTextNode($Category));
	$PluginEl->appendChild($CategoryEl);

	$PluginEl->appendChild( createVersionNode($xmlEl{'pl_vers'}, $PluginVersion) );
	$PluginEl->appendChild( createCyVersionNode($CyVersion) );
	
	if ($LicenseReq eq 'yes')
		{
		my $LicenseEl = $Doc->createElement($xmlEl{'license'});
		my $LicenseTextEl = $Doc->createElement('text') ;
		$LicenseEl->appendChild($LicenseTextEl);
		my $TextNode = $Doc->createTextNode( stripBadHex($License) );
		$LicenseTextEl->appendChild($TextNode);
		$PluginEl->appendChild($LicenseEl);
		}
	
	$PluginEl->appendChild( createReleaseDateNode($ReleaseDate) );

	# add authors
	my $Authors = addAuthors($PluginVersionId);
	$PluginEl->appendChild($Authors) if $Authors;
	
	# file type / download url
	my $FileUrl = $BaseUrl . "/" . PLUGIN_DL_SCRIPT . "?id=$PluginFileId"; 
	my $PluginUlrEl = $Doc->createElement('url');
	$PluginUlrEl->appendChild($Doc->createTextNode($FileUrl));
	$PluginEl->appendChild($PluginUlrEl);
	
	my $FileTag = addFileInfo($PluginFileId);

	if ($FileTag)
		{
		$PluginEl->appendChild(addFileInfo($PluginFileId));
		return $PluginEl;	
		}
	else
		{
		return undef;
		}
	}
	
	
sub addAuthors
	{
	my $PluginVersionId = shift || return 0;
	
	my $AuthorList = $Doc->createElement('authorlist');
	
	my $sql = "SELECT names, affiliation FROM plugin_author 
							INNER JOIN authors
								ON author_auto_id = author_id
							WHERE plugin_version_id = $PluginVersionId
							ORDER BY authorship_seq";
	my $Authors = $dbh->selectall_arrayref($sql);
	
	if (@$Authors == 0)
		{
		warn "No authors found for plugin_version_id $PluginVersionId";
		return 0;
		}
	
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
	return $AuthorList;
	}



# add the <filetype> and <url> elements to each <plugin>
sub addFileInfo
	{
	my $PluginFileId = shift || return 0;

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
	$PluginFileType->appendChild( $Doc->createTextNode($FileType) );
	return $PluginFileType;
	}
	

# these are illegal for xml
sub stripBadHex
	{
	my $str = shift;# || warn "No string passed to stripBadHex method";
	$str =~ s/\x0b|\x0c|\x0d|\x0e|\x0f//g;
	$str =~ s/([\x00-\x7f])[\x80-\xbf]/$1/g; 
	return $str;
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



# connect to database
sub dbConnect
	{
	my $DSN = "DBI:mysql:host=$Host;database=".DATABASE_NAME;
	my %attr;
	$dbh = DBI->connect($DSN, $UserName, $PassWord, \%attr);
	}

# check usage
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
	



	