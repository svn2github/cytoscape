
Allowed versions of Cytoscape is controlled by the column 'cy_version' in table 'plugin_version',
To make the change, login to chianti and run mysql as follows,

%mysql -u root -p
MYSQL> use cyplugindb;
MYSQL> describe plugin_version;

Then do something like this,

MYSQL> alter table plugin_version modify column cy_version set('2.0','2.1','2.2','2.3','2.4','2.5', '2.5.1', '2.5.2', '2.6', '2.6.1', '2.6.3','2.6.4','2.7','2.7.1','2.7.2','2.7.3','2.7.4','2.8','2.8.1','2.8.2','2.8.3','2.9','2.9.1','2.9.2');
