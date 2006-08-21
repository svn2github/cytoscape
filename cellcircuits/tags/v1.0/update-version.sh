#!/bin/bash
#
# This script updates the version of the CellCircuits
# cgi-bin directory
#
# Last modified: 17 aug 2006

DIR=/cgi-bin/search
FILES="about_cell_circuits.html advanced_search.html index.html"
BACKUP_SUFFIX=.backup

##
## Validate command line input
##
OLD_VERSION=$1
NEW_VERSION=$2

test $OLD_VERSION || { echo "Usage: $0: <old version> <new version>"; exit; }
test $NEW_VERSION || { echo "Usage: $0: <old version> <new version>"; exit; }

##
## Confirm change with user
##
echo "You are about to change all occurances of [$DIR/$OLD_VERSION] to [$DIR/$NEW_VERSION] in:"

for file in $FILES
do
    echo "  $file"
done

read -p "Do you want to continue? [y/n] " ANSWER

test  $ANSWER == "y" || exit;

echo ""
##
## Make changes
##
echo "Editing..."

for file in $FILES
do
    echo "  $file"

    sed --in-place=$BACKUP_SUFFIX \
        -e "s%$DIR/$OLD_VERSION%$DIR/$NEW_VERSION%g" $file
done

echo "Backup files are called [$BACKUP_SUFFIX]"
