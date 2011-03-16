#!/usr/bin/env python

class Dag:
    def __init__(self):
	print "Dag"
	self.idhash = {}
	self.namehash = {}
	self.namespacehash = {}
	self.isa = {}
	self.id2isaupper = {}
	self.partof = {}
	self.id2partofupper = {}
	self.checkresultarray = {}

# ID  ex) GO:0000001

    def set_id(self, nm):
	self.id = nm
	self.idhash[ nm ] = self.id
	self.isaarray = []
	self.partofarray = []

    def input_id(self, ids):
	resultidhash = []
	for id in ids:
	    if id in self.idhash.keys():
		resultidhash.append( self.idhash[ id ] )
	if len( resultidhash ) == 0:
	    resultidhash = False
	return resultidhash

    def id_keys(self):
	return self.idhash.keys()

# name  ex) mitochondrion inheritance

    def set_name(self, name):
	self.name = name
	id = self.id
	self.namehash[ id ] = name

    def input_name(self, ids):
	resultname = []
	for id in ids:
	    if id in self.namehash.keys():
		resultname.append( self.namehash[ id ] )
	if len( resultname ) == 0:
	    resultname = False
	return resultname

# namespace  ex) biological_process

    def set_namespace(self, namespace):
	self.namespace = namespace
	id = self.id
	self.namespacehash[ id ] = namespace

    def input_namespace(self, ids):
	resultns = []
	for id in ids:
	    if id in self.namespacehash.keys():
		resultns.append( self.namespacehash[ id ] )
	if len( resultns ) == 0:
	    resultns = False
	return resultns

# is_a  ex) is_a: GO:0048308 ! organelle inheritance

    def set_isa(self, isa):
	self.isaarray.append( isa )
	self.isa[ self.id ] = self.isaarray

    def input_isa(self, ids):
	resultisa = []
	for id in ids:
	    if id in self.isa.keys():
		for newarray in self.isa[ id ]:
		    resultisa.append( newarray )
	if len( resultisa ) == 0:
	    resultisa = False
	return resultisa

    def tree_isa( self, ids ):
	self.resultarray = []
	self.checkresultarray = {}
	while 1:
	    self.temp = []
	    for id in ids:
		if id in self.isa.keys():
		    for temp in self.isa[ id ]:
			if temp in self.checkresultarray.keys():
			    pass
			else:
			    self.checkresultarray[ temp ] = 1
			    self.resultarray.append( temp )
			    self.temp.append( temp )
		else:
		    pass
	    if len( self.temp ) != 0:
		ids = self.temp
		continue
	    else:
		break
	if len( self.resultarray ) == 0:
	    self.resultarray = False
	return self.resultarray

# upper_is_a  ex) GO:0000001 is_a: GO:0048308   =>  GO:0048308 -> GO:0000001

    def set_upper_isa(self, id, target):
	if self.regist_upper_isa( target ) == False:
	    self.isaupper = []
	    self.id2isaupper[ target ] = self.isaupper
	self.id2isaupper[ target ].append( id )
	return self.id2isaupper[ target ]

    def regist_upper_isa(self, check):
	if check in self.id2isaupper.keys():
	    return True
	else:
	    return False

    def input_upper_isa(self, targets):
	resultupperisa = []
	for target in targets:
	    if self.regist_upper_isa( target ) == True:
		for newarray in self.id2isaupper[ target ]:
		    resultupperisa.append( newarray )
	if len( resultupperisa ) == 0:
	    resultupperisa = False
	return resultupperisa

    def tree_upper_isa( self, ids ):
	self.resultarray = []
	self.checkresultarray = {}
	while 1:
	    self.temp = []
	    for id in ids:
		if id in self.id2isaupper.keys():
		    for temp in self.id2isaupper[ id ]:
			if temp in self.checkresultarray.keys():
			    pass
			else:
			    self.checkresultarray[ temp ] = 1
			    self.resultarray.append( temp )
			    self.temp.append( temp )
		else:
		    pass
	    if len( self.temp ) != 0:
		ids = self.temp
		continue
	    else:
		break
	if len( self.resultarray ) == 0:
	    self.resultarray = False
	return self.resultarray

		    
# part_of  ex) relationship: part_of GO:0006310 ! DNA recombination

    def set_partof(self, partof):
	self.partofarray.append( partof )
	self.partof[ self.id ] = self.partofarray

    def input_partof(self, ids):
	self.resultpartof = []
	for id in ids:
	    if id in self.partof.keys():
		self.resultpartof.append( self.partof[ id ] )
	if len( self.resultpartof ) == 0:
	    self.resultpartof = False
	else:
	    self.resultpartof = self.reconstract_partof( self.resultpartof )
	return self.resultpartof

    def reconstract_partof( self, arrays ):
	self.resultpartof = []
	for array in arrays:
	    for id in array:
		self.resultpartof.append( id )
	return self.resultpartof

# upper_part_of  ex) GO:0000018 part_of: GO:0006310  =>  GO:0006310 -> GO:0000018
    
    def set_upper_partof(self, id, target):
	if self.regist_upper_partof( target ) == False:
	    self.partofupper = []
	    self.id2partofupper[ target ] = self.partofupper
	self.id2partofupper[ target ].append( id )
	return self.id2partofupper[ target ]

    def regist_upper_partof(self, check):
	if check in self.id2partofupper.keys():
	    return True
	else:
	    return False

    def input_upper_partof(self, targets):
	resultupperpartof = []
	for target in targets:
	    if self.regist_upper_partof( target ) == True:
		for part in self.id2partofupper[ target ]:
		    resultupperpartof.append( part )
	if len( resultupperpartof ) == 0:
	    resultupperpartof = False
	return resultupperpartof
    
    def get_upper_partof(self):
	return self.id2partofupper.keys()

    def match_go(self, keywords):
	matchgoarray = []
	for keyword in keywords:
	    for id in self.namehash.keys():
		name = self.namehash[ id ]
		if keyword in name:
		    matchgoarray.append( id )
	if len( matchgoarray ) == 0:
	    matchgoarray = False
	return matchgoarray

if __name__ == "__main__":

    keio = Dag()
    keio.set_id( "Tokyo" )
    keio.set_name( "general" )
    keio.set_namespace( "bun hou keizai rikou i" ) 
    keio.set_isa( "hiyoshi" )
    keio.set_isa( "mita" )
    keio.set_isa( "shinano" )
    keio.set_upper_isa( "Tokyo", "all" )
    keio.set_partof( "yagami" )
    keio.set_upper_partof( "Tokyo", "yagami" )

    keio.set_id( "Kanagawa" )
    keio.set_name( "kankyo" )
    keio.set_namespace( "kankyo sougou kango" )
    keio.set_isa( "Tokyo" )
    keio.set_upper_isa( "Kanagawa", "Tokyo" )

    keio.set_id( "Yamagata" )
    keio.set_name( "bio" )
    keio.set_namespace( "labo metabo copia" )
    keio.set_isa( "Kanagawa" )
    keio.set_upper_isa( "Yamagata", "Kanagawa" )
    keio.set_partof( "center" )
    keio.set_upper_partof( "Yagami", "center" )

    keyarray = []
    keyarray.append( ["Tokyo"] )
    keyarray.append( ["Kanagawa"] )
    keyarray.append( ["Yamagata"] )
    keyarray.append( ["python"] )
    for key in keyarray:
	print "KeyWord\t", key
	print "ID\t\t", keio.input_id( key )
	print "NAME\t\t", keio.input_name( key )
	print "NAMESP\t\t", keio.input_namespace( key )
	print "IS_A\t\t", keio.input_isa( key )
	print "UIS_A\t\t", keio.input_upper_isa( key )
	print "TIS_A\t\t", keio.tree_isa( key )
	print "TUIS_A\t\t", keio.tree_upper_isa( key )
	print "PART\t\t", keio.input_partof( key )
	print "UPART\t\t", keio.input_partof( key )
	print 
