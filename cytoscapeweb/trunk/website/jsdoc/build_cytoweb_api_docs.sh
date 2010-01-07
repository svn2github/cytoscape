#/bin/bash

# Config
####################################################################################################

METADATA_DIR="metadata"
VERSION_FILE="$METADATA_DIR/version.txt"
RELEASE_FILE="$METADATA_DIR/release.txt"
VERSION="$1"
SOURCE_FILE="cytoscapeweb.js"
JSDOC_PHP_DIR="out/jsdoc/symbols"
API_OUTPUT_PHP_DIR="../src/php/content/documentation/api/$VERSION"
PHP_EXTENSION="php"
DATE=`date +%Y-%m-%d`

function copy_group {
    # Setting the shell's Internal Field Separator to null
    OLD_IFS=$IFS
    IFS=''

    group_name=$1
   
    local array_string="$2[*]"
    local loc_array=(${!array_string})
    IFS=$OLD_IFS
    
    mkdir $API_OUTPUT_PHP_DIR/$group_name
    for file_name in ${loc_array[@]}
    do
        cp $JSDOC_PHP_DIR/$file_name.$PHP_EXTENSION $API_OUTPUT_PHP_DIR/$group_name
        if [[ $3 == "real_class" ]]
        then
            touch $API_OUTPUT_PHP_DIR/$group_name/.real_class
        fi
    done
    
}

# Make API from jsdoc
####################################################################################################

if [ -z $VERSION ]
then
    echo "FAIL: Inform the version number! For example:
    $0 0.3.1"
    exit
fi

echo Clearing existing APIs with same version
rm -rf $JSDOC_PHP_DIR

echo Running jsdoc
java -jar jsrun.jar app/run.js -a -t=templates/jsdoc $SOURCE_FILE
rm -rf $API_OUTPUT_PHP_DIR
mkdir $API_OUTPUT_PHP_DIR

echo Copying metadata
echo $DATE > $API_OUTPUT_PHP_DIR/.date
cat $RELEASE_FILE > $API_OUTPUT_PHP_DIR/.release_info

echo Copying class categories

SHAPES=( "org.cytoscapeweb.ArrowShape" "org.cytoscapeweb.NodeShape" )
copy_group "shapes" SHAPES

MAPPERS=( "org.cytoscapeweb.ContinuousMapper" "org.cytoscapeweb.CustomMapper" "org.cytoscapeweb.DiscreteMapper" "org.cytoscapeweb.PassthroughMapper" )
copy_group "mappers" MAPPERS

GRAPH_OBJECTS=( "org.cytoscapeweb.Node" "org.cytoscapeweb.Edge" "org.cytoscapeweb.Group" )
copy_group "elements" GRAPH_OBJECTS

EVENTS=( "org.cytoscapeweb.Event" "org.cytoscapeweb.EventType" "org.cytoscapeweb.Error" )
copy_group "events" EVENTS

VISUAL_STYLE=( "org.cytoscapeweb.VisualStyle" )
copy_group "visual_style" VISUAL_STYLE

LAYOUT=( "org.cytoscapeweb.Layout" )
copy_group "layout" LAYOUT

# a real_class must be on its own in the array
VISUALIZATION=( "org.cytoscapeweb.Visualization" )
copy_group "cytoscape_web" VISUALIZATION "real_class"

echo SUCCESS