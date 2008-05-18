DIR=$1
USAGE="Usage: $0: <dir>"
test $DIR ||{ echo $USAGE; exit;}

find $DIR  -print | \
egrep '/\.|*/classes/*|/bin/*|/build/' > Exclude.txt
tar cvf ./wikizip/$DIR.tar -X Exclude.txt $DIR
rm Exclude.txt