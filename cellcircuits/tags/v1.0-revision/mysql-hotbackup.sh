DB=cc
DIR=$1

test $DIR || { echo "Usage: $0: <destination dir>"; exit; }

echo "Hot-copying $DB to $DIR"

/usr/bin/mysqlhotcopy --user=mdaly --password=mdalysql \
$DB \
$DIR
