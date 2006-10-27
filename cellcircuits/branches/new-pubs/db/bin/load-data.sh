./score-all-models.pl > scores.txt 
mysql -u mdaly --password=mdalysql cc < ../db/sql/create_model_similarity.sql
mysql -u mdaly --password=mdalysql cc < scores.txt
