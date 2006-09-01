#!/bin/bash
#
# This script updates the version of the CellCircuits
# cgi-bin directory
#
# Last modified: 17 aug 2006

DIR=/cgi-bin/search
HTML_FILES="about_cell_circuits.html advanced_search.html index.html"
CGI_FILES="CCDB/Constants.pm search.pl get_genes_by_eid.pl"
BACKUP_SUFFIX=.backup

##
## Validate command line input
##

HTML_DIR=$1
CGI_DIR=$2

OLD_VERSION=$3
NEW_VERSION=$4

USAGE="Usage: $0: <html dir> <cgi dir> <old version> <new version>"

test $DIR || { echo $USAGE; exit; }
test $OLD_VERSION || { echo $USAGE; exit; }
test $NEW_VERSION || { echo $USAGE; exit; }

##
## Confirm change with user
##
echo "You are about to change all occurances of [$DIR/$OLD_VERSION] to [$DIR/$NEW_VERSION] in:"

for file in $HTML_FILES
do
    echo "  $HTML_DIR/$file"
done

echo "You are about to change all occurances of [$OLD_VERSION] to [$NEW_VERSION] in:"

for file in $CGI_FILES
do
    echo "  $CGI_DIR/$file"
done

read -p "Do you want to continue? [y/n] " ANSWER

test  $ANSWER == "y" || exit;

echo ""
##
## Make changes
##
echo "Editing..."

for file in $HTML_FILES
do
    echo "  $HTML_DIR/$file"

    sed --in-place=$BACKUP_SUFFIX \
        -e "s%$DIR/$OLD_VERSION%$DIR/$NEW_VERSION%g" $HTML_DIR/$file
done



for file in $CGI_FILES
do
    echo "  $CGI_DIR/$file"

    sed --in-place=$BACKUP_SUFFIX \
        -e "s%$OLD_VERSION%$NEW_VERSION%g" $CGI_DIR/$file
done


echo "Backup files are called [$BACKUP_SUFFIX]"
