mysql -u mdaly --password=mdalysql cc \
    < dump/dump-human-genes.sql \
    > dump/human-genes-in-GO-DB.txt

mysql -u mdaly --password=mdalysql cc \
    < dump/dump-yeast-genes.sql \
    > dump/yeast-genes-in-GO-DB.txt
