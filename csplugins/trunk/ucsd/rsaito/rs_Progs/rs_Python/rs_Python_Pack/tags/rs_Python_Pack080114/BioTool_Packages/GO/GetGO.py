#!/usr/bin/env python

import GO2Class
import Dag

dag = Dag.Dag()

class GetGO(GO2Class.GO2Class):
    def info_type( self ):
	return "GetGO"

    def set_database(self, database):
	self.set_class( database )

    def get_id2dag(self, id):
        return self.dag.input_id( id )

    def get_name2dag(self, id):
        return self.dag.input_name( id )

    def get_namespace2dag(self, id):
        return self.dag.input_namespace( id )

    def get_isa2dag(self, id):
        return self.dag.input_isa( id )

    def get_upperisa2dag(self, id):
        return self.dag.input_upper_isa( id )

    def get_partof2dag( self, id ):
        return self.dag.input_partof( id )

    def get_upperpartof2dag( self, id ):
        return self.dag.input_upper_partof( id )

    def get_tree_isa2dag( self, ids ):
	return self.dag.tree_isa( ids )
    
    def get_tree_upperisa2dag( self, ids ):
	return self.dag.tree_upper_isa( ids )

    def get_match_go( self, keywords ):
	return self.dag.match_go( keywords )

    def get_goid2tree( self, keywords ):
	querys = self.get_match_go( keywords )
	if querys != False:
	    print "KEYWORD\t\t", keywords
	    print "ID\t\t", self.get_id2dag( querys )
	    print "IS_A\t\t", self.get_isa2dag( querys )
	    print "TIS_A\t\t", self.get_tree_isa2dag( querys )
	    print "UIS_A\t\t", self.get_upperisa2dag( querys )
	    print "TUIS_A\t\t", self.get_tree_upperisa2dag( querys )
	    print "PART_OF\t\t", self.get_partof2dag( querys )
	    print "UPART_OF\t", self.get_upperpartof2dag( querys )
	else:
	    print keywords, "not exist!"

# check_ids_exist performs only to search IDs from Keywords.
    def check_ids_exist( self, keywords ):
	querys = self.get_match_go( keywords )
	if querys != False:
	    print self.get_id2dag( querys )
	else:
	    print keywords, "not exist!"

if __name__ == "__main__":
    result = GetGO()

# "gene_ontology.obo" is the latest database in GO
# "gene_ontology_sample" is shorter database edited(20+ entrys), old version in GO

    result.set_database( "gene_ontology_sample" )

    result.check_ids_exist( ["membrane"] )
    result.check_ids_exist( ["lactase"] )
"""
    result.get_goid2tree( ["lactase"] )
    result.get_goid2tree( ["MacOSX Intel"] )
    print "*******************"
    
    print "ID\t\t", result.get_id2dag( ["GO:0007017"] )
    print "IS_A\t\t", result.get_isa2dag( ["GO:0007017"] )
    print "TIS_A\t\t", result.get_tree_isa2dag( ["GO:0007017"] )
    print "UIS_A\t\t", result.get_upperisa2dag( ["GO:0007017"] )
    print "UTIS_A\t\t", result.get_tree_upperisa2dag( ["GO:0007017"] )
    print "PART_OF\t\t", result.get_partof2dag( ["GO:0007017"] )
    print "UPART_OF\t", result.get_upperpartof2dag( ["GO:0007017"] )
    print

    print "NameSpace\t\t", result.get_namespace2dag( ["GO:0000001"] )
"""
