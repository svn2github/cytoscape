#!/usr/bin/env python

from basic import *
import os
import sys
import cPickle
#import Table
import copy

import ucsc


#from sets import Set

chrNum = {}
chrNum['H'] = range(1,23) + [ 'X' , 'Y' ]
chrNum['M'] = range(1,20) + [ 'X' , 'Y' ]

strandDirections = ('plus', 'minus')
reverse = { 'plus': 'minus', 'minus':'plus', 0:1, 1:0 }


# watson old
#allPairsFile = { 'H' : '/pub/Data/AS/temp/clusterH/ALLPAIRS.nst', 
#                 'M' : '/pub/Data/AS/temp/clusterM/ALLPAIRS.nst' }

#gene
allPairsFile = {
    'H' : '/home/osada/WS/OligoDesign/ExonTiling/MakePickles/AllPairsH.nst',
    'M' : '/home/osada/WS/OligoDesign/ExonTiling/MakePickles/AllPairsM.nst' }

genome_version = { 'H' : 'hg17',  'M' : 'mm6' }


#def sum(input):
#    """input list and get the sum of all elements"""
#    if type(input) is not list:
#        raise TypeError( str(type(input)) + ' was given instead of list' )
#    result = 0.0
#    for val in input:
#        result += val
#    return result

def is_overlapping(regionA, regionB):
    '''  Usage: is_overlapping([int1, int2], [int3, int4])
  Return: True when region [int1, int2] and [int3, int4] are overlapping'''

    for region in (regionA, regionB):
        for elem in region:
            if type(elem) is not int: raise TypeError
    START = 0
    END = 1
    if regionA[START] <= regionB[END] and regionB[START] <= regionA[END]:
        #print regionB, '--', regionA
        return True
    else: return False


def add_safely( add_to_this, key, obj ):
    '''  Usage: add_safely( dictionary, key, object )
  Raise error if dictionary has an identical key with different object'''

    if type(add_to_this) is not dict:
        err = 'add_safely() expects dict as the first arg.'
        err = "%s (%s was given instead)" % (err, str(type(add_to_this)) )
        raise TypeError(err)
    if add_to_this.has_key(key):
        if not add_to_this[key] == obj:
            err = "\n  identical key: %s\n  in   dict: %s\n  new input: %s" % \
                  ( key, str(add_to_this[key]), str(obj) )
            raise SameKeyDifferentData(err)
        else: return 0
    else: 
        add_to_this[key] = obj
        return 1

def nrappend(list, val):
    '''  Usage: nrappend(list, val)
  Add val to list only when there is no identical value in list'''
    if not val in list: list.append(val)


#class SafeDict(dict):
#    def __init__(self, *args, **kw):
#        dict.__init__(self, *args, **kw)
#
#    def __setitem__(self, key, value):
#        

class UnDefError(Exception): pass

class NoClusterFound(Exception):
    pass

class SameStrandPair(Exception):
    pass

class ExistSameDictKey(Exception):
    pass

class SameKeyDifferentData(Exception):
    pass


#
#class VeryBasic:
#    def __init__(self,id):
#        self.ID = id
#
#    def __str__(self):
#        return self.ID
#
#    def __eq__(self, other):
#        if str(self) == str(other): return True
#        else: return False
#            
#    def __ne__(self, other):
#        if self == other: return False
#        else: return True
#
#    def get_ID(self):
#        return self.ID


class AddressBook(BaseDataSet):
    '''  Get information using following functions!
   - ID: get_ID()
   - Version of Genome: get_genome_version()
   - Original nstore(perl) file: get_originated_file()
   - Species: get_species()
   - Sense-antisense pair of ID 'id': get_SAT('id')
   - Cluster of ID 'id': get_cluster('id')
   - EST or alternative isoform of ID 'id': get_isoform('id')
  And access all chromosomes of the species with: get_all_chromosomes()'''

    def __init__(self, id, spe='unknown species', ver='unknown version',
                 file='unknown file' ):
        self.ID = id
        self.species = spe
        self.genome_version = ver
        self.originated_file = file

        self.chromosome = {}
        self.SAT_pair = {}
        self.cluster = {}
        self.isoform = {}

    def __repr__(self):
        ret = \
            (self.ID, self.species, self.genome_version, self.originated_file)
        return str(ret)
    def __str__(self):
        ret = \
            (self.ID, self.species, self.genome_version, self.originated_file)
        return str(ret)

    def set_chromosomes(self, chromosome_dict):
        if type(chromosome_dict) is not dict: raise TypeError

        for chr_instance in chromosome_dict.values():
            if chr_instance.__class__ is not Chromosome: raise TypeError
                
        self.chromosome = chromosome_dict


    def make_address_book(self):
        for thisChr in self.chromosome.values():
            self.SAT_pair.update( thisChr._get_SAT_dict() )
            self.cluster.update( thisChr._get_clusters_dict() )

        for thisCluster in self.cluster.values():
            self.isoform.update( thisCluster.get_isoforms_dict() )


    def set_coding_potential(self, file=''):
        jfile = { 'H' : 'judge_hg17.txt', 'M' : 'judge_mm6.txt' }
        if not file: file = jfile[ self.species ]

        file = lookup_file(file)
        d = simpled(file)

        for c in self.get_all_clusters():
            repid = c.get_rep_ID()
            c.set_coding_potential( d[ repid ] )
##         print >> sys.stderr, 'Coding potentail info was added to clsuters.'



#    def set_SAT(self, pairs_dict):
#        self.SAT_pair = pairs_dict

#    def set_clusters(self, cluster_dict):
#        self.cluster = cluster_dict

#    def set_isoforms(self, isoform_dict):
#        self.isoform = isoform_dict

    def get_genome_version(self):
        return self.genome_version
    def get_species(self):
        return self.species
    def get_originated_file(self):
        return self.originated_file

    def get_all_chromosomes(self):
        return self.chromosome.itervalues()
    def get_all_SAT(self):
        return self.SAT_pair.itervalues()
    def get_all_clusters(self):
        return self.cluster.itervalues()
    def get_all_isoforms(self):
        return self.isoform.itervalues()

    def get_all_chromosome_ID(self):
        return self.chromosome.iterkeys()
    def get_all_SAT_ID(self):
        return self.SAT_pair.iterkeys()
    def get_all_cluster_ID(self):
        return self.cluster.iterkeys()
    def get_all_isoform_ID(self):
        return self.isoform.iterkeys()

    def get_chromosome(self, id):
        return self.chromosome[id]
    def get_SAT(self, id):
        return self.SAT_pair[id]
    def get_cluster(self, id):
        return self.cluster[id]
    def get_isoform(self, id):
        return self.isoform[id]



class Chromosome(VeryBasic):
    '''  Get information using following functions!
   - ID: get_ID()
   - Cluster of ID 'id': get_cluster('id')
  And access all sense-antisene pairs of chromosome with: get_all_SAT()'''

    def __init__(self, id):
        self.ID = id
        self.SAT_pair = {} # change this to dictionary
        self.clusters_by_strand = {}
        self.clusters_by_strand['plus'] = {}
        self.clusters_by_strand['minus'] = {}

    def __repr__(self):
        ret = (self.ID, self.SAT_pair.keys(), 
               self.clusters_by_strand['plus'], 
               self.clusters_by_strand['minus']   )
        return str(ret)
    def __str__(self):
        ret = (self.ID, self.SAT_pair.keys() )
        return str(ret)


    def _get_clusters_dict(self):
        tmpDic = {}
        tmpDic.update(self.clusters_by_strand['plus'])
        tmpDic.update(self.clusters_by_strand['minus'])
        return tmpDic

    def _get_SAT_dict(self):
        return self.SAT_pair

    def get_all_SAT(self):
        return self.SAT_pair.itervalues()

    def get_all_clusters(self):
        for d in self.clusters_by_strand.itervalues():
            for c in d.itervalues():
                yield c


    def get_cluster(self, strand, ID):
        if self.clusters_by_strand[strand].has_key(ID):
            return self.clusters_by_strand[strand][ID]
        else:
            raise KeyError

    def add_SAT(self, pair):
        if pair.__class__ is not SAT: 
            raise TypeError( "%s was given in place of SAT" % 
                             str(pair.__class__) )
        if self.ID != pair.get_chromosome() : 
            raise Exception('Do not try to add SAT on different chromosome')

        for up_or_dn in (SAT.UP, SAT.DN):

            new_cluster = pair.get_cluster(up_or_dn)
            ID = new_cluster.get_ID()
            strand = new_cluster.get_strand()

            strand_clusters = self.clusters_by_strand[ strand ]

            if strand_clusters.has_key( ID ):
                strand_clusters[ ID ].inherit_AS_info( new_cluster )
                pair.set_cluster( strand_clusters[ ID ], up_or_dn )
                anti_sense = pair.get_cluster( reverse[ up_or_dn ] )
                anti_sense.add_antisense( strand_clusters[ ID ] )
            else:
                strand_clusters[ ID ] = new_cluster


        self.SAT_pair[ pair.get_ID() ] = pair


#            for direction in strandDirections:
#                thisStrandCluster = self.strandCusters[ direction ]
#                new_cluster = pair.get_cluster(direction)
#                ID = new_cluster.get_ID()
#                if thisStrandCluster.has_key( ID ):
#                    thisStrandCluster[ ID ].inherit_AS_info( new_cluster )
#                    pair.set_cluster( thisStrandCluster[ ID ], direction )
#                else:
#                    thisStrandCluster[ ID ] = new_cluster



class SAT(BaseRegion):
    '''  Get information using following functions!
   - ID of SAT pair: get_ID()
   - Chromosome on which this SAT pairs exists: get_chromosome()
   - Cluster instance matching the 'criteria': get_cluster('criteria')
          criteria: ['upstream' | 'downstream' | 'plus' | 'minus']
   - Start and end positions of overlapping region: get_overlapping_region()
   - The clusters are exon overlapping?: is_exon_overlapping()
   - Matrix of all segments in overlapping region, all isoform-combinations
     and states of overlapping
       (if it is exon in both isoforms of different clusters->1, else->0)'''

    UP = 0
    DN = 1
    ST = 0
    ED = 1

    def __init__( self, id, clust, clust2, ovlplen=None, ovlpInfo=None ):
        BaseRegion.__init__(self)
        self.set_ID(id)
        
        self.clusters = {}

        self.clusters_by_strand = {}
        self.overlapping_length = None
        self.overlapping_info = None

        if ovlplen: self.set_overlapping_length( ovlplen )
        if ovlpInfo: self.set_overlapping_info( ovlpInfo )


        if clust.__class__ is not Cluster or clust2.__class__ is not Cluster:
            raise TypeError("%s was given in place of Cluster" % 
                            clust.__class__)

        if clust.get_chromosome() != clust2.get_chromosome() :
            raise Exception('Clusters on different chromosomes are not SAT')

        # Strand check. Two should be on different strand.
        if clust.get_strand() == clust2.get_strand():
            err = "%s,%s" % ( str(clust), str(clust2) )
            raise SameStrandPair(err)

        if self.clusters_by_strand.has_key( clust.get_strand() ) \
                 or self.clusters_by_strand.has_key( clust2.get_strand() ):
            raise ExistSameDictKey

        self.chromosome = clust.get_chromosome()

        clust.add_SAT_info(self)
        clust2.add_SAT_info(self)

        clust.add_antisense(clust2)
        clust2.add_antisense(clust)

        # Downstream Cluster 
        if clust.get_start() < clust2.get_start():
            self.clusters[SAT.UP] = clust
            self.clusters[SAT.DN] = clust2
        else:
            self.clusters[SAT.UP] = clust2
            self.clusters[SAT.DN] = clust
            
        self.clusters_by_strand[ clust.get_strand() ]  = clust
        self.clusters_by_strand[ clust2.get_strand() ] = clust2

    def __repr__(self):
        ret = (self.ID, self.chromosome, self.clusters)
        return str(ret)

    def __str__(self):
        ret = (self.ID, self.chromosome, self.clusters[SAT.UP], 
               self.clusters[SAT.DN] )
        return str(ret)


    def get_start(self): return self.clusters[SAT.UP].get_start()
    def get_end(self): return self.clusters[SAT.DN].get_end()

    def set_overlapping_length(self, v): self.overlapping_length = int(v)
    def set_overlapping_info(self, v) : self.set_overlapping_info = v

    def get_overlapping_length(self): return self.overlapping_length
    def get_overlapping_info(self) : return self.set_overlapping_info


    def getPairs(self):
        return (self.clusters[SAT.UP], self.clusters[SAT.DN])

    def get_chromosome(self):
        return self.chromosome

    def set_cluster(self, clust, key):
        if key == 'upstream': key = SAT.UP
        elif key == 'downstream': key = SAT.DN

        if key == SAT.UP or key == SAT.DN:
            self.clusters[key] = clust
            self.clusters_by_strand[ clust.get_strand() ] = clust
        else: raise NoMatch

    def get_cluster(self, key):
        if key == SAT.UP or key == 'upstream': return self.clusters[SAT.UP]
        elif key == SAT.DN or key == 'downstream': return self.clusters[SAT.DN]
        elif key == 'plus' or key == 'minus' : 
            return self.clusters_by_strand[key]
        else: raise NoClusterFound

    def get_overlapping_region(self):
        if self.clusters[SAT.UP].get_end() < self.clusters[SAT.DN].get_end():
            upstream_end = self.clusters[SAT.UP].get_end()
        else:
            upstream_end = self.clusters[SAT.DN].get_end()
        return (self.clusters[SAT.DN].get_start(), upstream_end)

    def is_exon_overlapping(self):
        ov_region = self.get_overlapping_region()
        for isoform in self.clusters[SAT.DN].get_all_isoforms():
            for senseExonRegion in isoform.get_exon_regions():
                if ov_region[SAT.ED] < senseExonRegion[SAT.ST]: break
                for as_isoform in self.clusters[SAT.UP].get_all_isoforms():
                    if as_isoform.any_exon_exists(senseExonRegion): return True
        return False


    def _get_region_edges(self):
        st = self.clusters[SAT.UP].get_starts()
        st.extend(self.clusters[SAT.DN].get_starts())

        ed = self.clusters[SAT.UP].get_ends()
        ed.extend(self.clusters[SAT.DN].get_ends())
        ret = [v+1 for v in ed ]
        st.extend(ret)
        ret = list( set(st) )
        ret.sort()
        return ret




    #def get_matrix_overlapping(self):
    def get_matrix(self):        
        OVR= self.get_overlapping_region()
        START = 0
        END = 1

        result_matrix = {}
        combinations = []
        edges = self.clusters[SAT.DN]._get_region_edges()
        
        # Matrix for overlapping region
        for i, edge in enumerate(edges):
            if OVR[END] < edge: 
                del edges[i+1:]
                break
            score_by_combination = {}
            for isofA in self.clusters[SAT.UP].get_all_isoforms():
                for isofB in self.clusters[SAT.DN].get_all_isoforms():
                    combination = "%s-%s" % ( isofA.get_ID(), isofB.get_ID() )
                    combinations.append(combination)
                    if isofA.any_exon_exists(edge) and \
                           isofB.any_exon_exists(edge):
                        score_by_combination[combination] = 1
                    else: score_by_combination[combination] = 0
            result_matrix[edge] = score_by_combination

        id = "%s_overlapping" % self.ID
        etm = ExonTilingMatrix( id, result_matrix, edges, combinations, self)
        return etm



class Cluster(BaseRegion):
    '''  Get information using following functions!
   - ID: get_ID()
   - Chromosome on which this Cluster exists: get_chromosome()
   - Strand on which this Cluster exists: get_strand()
   - Start pos of cluster region: get_start()
   - End pos of cluster region: get_end()
   - ID of representative isoform of the cluster: get_rep_ID()
   - All Isoforms of this cluster: get_all_isoforms()
   - All antisense Clusters: get_antisense()
   - Isoform with ID 'ID': get_isoform('ID')
   - All associated SAT pairs: get_SAT()
   - Matrix of all segments in the cluster region, all isoform-combinations
     and states of non-overlapping 
       (if it is exon exclusively in the isoform of this cluster->1, else->0)'''

#    def __init__(self, id):
#        self.id = id
    def __init__(self, id, chr = 'no chromosome', str = 'no strand', \
                 start = -9999, end = -9999):

        # When cluster1 == cluster2 ...
        # these must be identical
        self.ID = id
        self.chromosome = chr
        self.strand = str
        self.start = int(start)
        self.end = int(end)
        self.isoforms = {}
        self.rep = ''

        # but these can be copied
        self.antisenses = {}
        self.associated_SAT = {}


    def __repr__(self):
        ret = (self.ID, self.chromosome, self.strand, self.start, 
               self.end, self.isoforms.keys(), 
               self.associated_SAT.keys(), self.antisenses.keys() )
        return str(ret)

    def __str__(self):
        ret = (self.ID, self.chromosome, self.strand, self.start, 
               self.end, self.isoforms.keys())
        return str(ret)


    def set_coding_potential(self, v):
        if v in (0,1,True,False): self.protain_coding = v
        elif v in ('0','1'): self.protain_coding = int(v)
        else: raise ValueError, 'invalid input as coding potential: %s' % v


    def is_coding(self):
	if self.protain_coding:
		return True
	else:
		return False

    def is_noncoding(self):
	if self.protain_coding:
		return False
	else:
		return True

    def set_data(self, chr, str, start, end ):
        self.chromosome = chr
        self.strand = str
        self.start = int(start)
        self.end = int(end)
        #, self.strand, self.start, self.end = array

    def set_rep(self, rep):
        self.rep = rep

    def add_isoform(self, new_isoform):
##         if new_isoform.__class__ is not Isoform: #Changed
        if not isinstance(new_isoform, Isoform):
            err = "%s was given in place of Isoform" % \
                  str(new_isoform.__class__) 
            raise TypeError( err )
        add_safely( self.isoforms, new_isoform.get_ID(), new_isoform )
        new_isoform.set_cluster(self) #Changed
                       

    def add_antisense(self, as):
        if as.__class__ is not Cluster:
            err = "%s was given in place of Cluster" % str(as.__class__)
            raise TypeError( err )
        self.antisenses[ as.get_ID() ] = as
        #add_safely( self.antisenses, as.get_ID(), as )


    def add_SAT_info(self, new_sas):
        if new_sas.__class__ is not SAT:
            err = "%s was given in place of SAT" % str(new_sas.__class__)
            raise TypeError( err )
        add_safely( self.associated_SAT, new_sas.get_ID(), new_sas)


    def inherit_AS_info(self, other):
#B#        if self.get_ID == other.get_ID() and self.get_chromosome() == other.get_chromosome()\
#        and self.get_strand() == other.get_strand() and self.get_start() == other.get_strand()\
#        and self.get_end() == other.get_end()\
#        and self.get_all_isoform_ID() == other.get_all_isoform_ID():
#A#        if other == self:
        if self.get_ID() == other.get_ID():
            for others_as in other.get_antisense():
                self.add_antisense( others_as )
            for others_sat in other.get_SAT():
                self.add_SAT_info( others_sat )
        else:
            raise SameKeyDifferentData(self.get_ID(), other.get_ID(), 
                                       'self:',str(self), 'other:',str(other))
        
    def get_rep_ID(self):
        return self.rep

    def get_all_antisense_ID(self):
        return self.antisenses.keys()
    def get_all_isoform_ID(self):
        return self.isoforms.keys()
    def get_all_SAT_ID(self):
        return self.associated_SAT.keys()

    def get_isoform(self, id):
        return self.isoforms[id]
    
    def get_isoforms_dict(self):
        return self.isoforms

    def get_all_isoforms(self):
        return self.isoforms.values()

    def get_antisense(self):
        return self.antisenses.values()

    def get_SAT(self): return self.associated_SAT.values()
    def get_all_SAT(self): return self.associated_SAT.values()
    
    def get_chromosome(self):
        return self.chromosome
    def get_strand(self):
        return self.strand
    def get_start(self):
        return self.start
    def get_end(self):
        return self.end


    def get_starts(self):
        l = []
        for i in self.get_all_isoforms():
            l.extend( i.get_exon_starts() )
##            print 'In Cluster %s.get_starts(), starts of an isoform:' % self.get_ID(), i.get_exon_starts()
##        print ' returns:', list(set(l))
        return list(set(l))

    def get_ends(self): 
        l = []
        for i in self.get_all_isoforms():
            l.extend( i.get_exon_ends() )
##            print 'In Cluster %s.get_ends, isoform ends:' % self.get_ID(), i.get_exon_ends()
##        print ' returns:', list(set(l))
        return list(set(l))

    def _get_region_edges(self):
        tmp = []
        for sat in self.get_all_SAT(): 
            tmp.extend(sat._get_region_edges())
        tmp = list(set(tmp))
        ret = [ v for v in tmp if self.get_start() <= v <= (self.get_end()+1) ]
        ret.sort()
        return ret

    def get_matrix(self, opt='onlythis'):
        '''Matrix: is exon \"only in this cluster\"'''
        START = 0
        END = 1
        
        result_matrix = {}
        edges = self._get_region_edges()

        id = "%s_exclusive_exon" % self.ID
        combinations = []
        etm = ExonTilingMatrix(id, result_matrix, edges, combinations, self)

        for start_pos in edges[:-1]:        
            score_by_comb = {}
            for isofA in self.isoforms.values():
                ida = isofA.get_ID()
                for anti in self.antisenses.values():
                    for isofB in anti.get_all_isoforms():
                        combination = "%s-%s" % ( ida, isofB.get_ID() )
                        combinations.append(combination)

                        if opt == 'onlythis':
                            score_by_comb[combination] = self.me_exon_as_not(
                                isofA, isofB, start_pos)
                        elif opt == 'ds':
                            score_by_comb[combination] = self.me_exon_as_exon(
                                isofA, isofB, start_pos)

            result_matrix[start_pos] = score_by_comb
        return etm


    def me_exon_as_not(self, me, anti, start_pos):
        if me.any_exon_exists(start_pos) and \
               not anti.any_exon_exists(start_pos): 
            return 1
        else: return 0

    def me_exon_as_exon(self, me, anti, start_pos):
        if me.any_exon_exists(start_pos) and anti.any_exon_exists(start_pos): 
            return 1
        else: return 0


    def get_overlapping_status(self):
##         '''Return Edges with score set 1 if at least a pair is overlapping,
##         and 0 if no pair is overlapping'''
##        print 'S', self
#        for i in self.get_all_isoforms():
#            print >> sys.stderr, i

##        print 'region edges:', self._get_region_edges()

##        for anti in self.get_antisense(): print 'AS', anti ## test
#        for i in anti.get_all_isoforms():
#            print >> sys.stderr, i
        
        edges = Edges( self._get_region_edges() )

        n = 0
        for r in edges.get_regions():
            n+=1
            pos = r.get_start()
            r.set_ID( '%s_%s' % ( self.get_ID(), n ) )
            r.set_cluster(self)
            r.set_strand( self.get_strand() )
            r.set_chromosome( self.get_chromosome() )
            for anti in self.get_antisense(): 
                if self.exon_exists_at(pos) and anti.exon_exists_at(pos):
                    try:
                        r.set_score( 1 )
                        break
                    except:
                        raise Exception('Failed to set score to %s\nS%s\nAS%s\n' 
                                        % (r, self, anti) )
            if not r.get_score(): r.set_score( 0 )


#        print >> sys.stderr, 'Scored edges:'
#        print >> sys.stderr, edges

        return edges


    def exon_exists_at(self, v):
        '''If there is one or more isoform whose exon covers input position,
        this will return True'''
        for i in self.isoforms.values():
            if i.any_exon_exists( v ): return True

        return False
                    
                
        

class Isoform(VeryBasic):
    '''  Get information using following functions!
   - ID: get_ID()
   - Exon regions: get_exon_regions()
   - File of mapping results: get_originated_file()
   - Alignment result for mapping: get_alignment_result()
   - Whether the region [int1, int2] overlap any exon of this isoform: 
       any_exon_exists([int1, int2])'''

    def __init__( self, id, regions=None, file='no file', \
                  aln='no alignment', clstr='no cluster'):
        self.ID = id
        self.exon_regions = regions        # [ [start, end], [start, end] ]
        self.exon_starts = ilist()
        self.exon_ends = ilist()
        self.originated_file = file
        self.alignment_result = aln
        self.associated_cluster = clstr

        if regions:
            for (s,e) in regions:
                self.exon_starts.append(s)
                self.exon_ends.append(e)

    def __repr__(self):
        ret = ( self.ID, self.exon_regions, self.originated_file )
        return str(ret)

    def __str__(self):
        return str( (self.ID, self.exon_regions) )

    def __eq__(self, other):
        if repr(self) == repr(other): return True
        else: return False
            
    def diff(self, other):
        if self.ID != other.get_ID(): return (self.ID, other.get_ID())
        elif self.exon_regions != other.get_exon_regions() :
            return (self.exon_regions, other.get_exon_regions() )
        elif self.originated_file != other.get_originated_file() :
            return ( self.originated_file,  other.get_originated_file() )
        elif self.alignment_result != other.get_alignment_result() :
            return ( self.alignment_result, other.get_alignment_result() )
    
    def get_start(self): return self.get_exon_starts()[0]
    def get_end(self): return self.get_exon_ends()[-1]

    def get_exon_starts(self): return self.exon_starts
    def get_exon_ends(self): return self.exon_ends
    def get_exon_regions(self): return self.exon_regions
    def get_originated_file(self): return self.originated_file
    def get_alignment_result(self): return self.alignment_result
    def get_cluster(self): return self.associated_cluster

    def set_cluster(self, v): self.associated_cluster = v
        
    def is_exon(self, pos):
        for region in self.exon_regions:
            if is_overlapping(region, [pos,pos]) : return True
        return False

    def any_exon_exists(self, region):
        if type(region) is int: region = [region, region]
        elif len(region) != 2: 
            raise TypeError('any_exon_exists() takes 1X2 int array')
        for exon_region in self.exon_regions:
            if is_overlapping(exon_region, region): return True
        #print 'no exon is in region', region, '(exon:',self.exon_regions,')'
        return False


class Region(BaseRegion):
    def __init__(self,s = None, e = None, scr = None):
        BaseRegion.__init__(self)
        self.score =  None
        self.cluster = None
        self.sequence = None


        if type(s) is list:
            self.set_start( s.pop(0) )
            self.set_end( s.pop(0) )        
            self.set_score( s.pop(0) )
        elif type(s) is int or type(s) is str:
            try:
                self.set_start( s )
                self.set_end( e )                
                self.set_score( scr )
            except TypeError: pass

            
    def set_score(self,v): self.score = float(v)
    def set_cluster(self,v):
        if v.__class__ is Cluster: self.cluster = v
        else: raise ValueError('invalid literal for Region.set_cluster(): %s' % v.__class__ )

    def get_score(self): return self.score
    def get_cluster(self): return self.cluster
    def get_length(self): return self.end - self.start +1
    def get_next(self): return self.end+1

    def set_sequence(self, v):
        try:
            BaseRegion.set_sequence(self, v)
        except MyError:
            print >> sys.stderr, \
                  "Invalid char for seq was found in %s: %s" % \
                  (self.get_ID(), v) ,

            srch = fineSeq_re.search( v )
            if srch: 
                tmpseq = srch.group()
                print >> sys.stderr, "--- and set:", tmpseq
                BaseRegion.set_sequence(self, tmpseq)
            else: 
                print >> sys.stderr, "--- no sequence set."
                BaseRegion.set_sequence(self, '')


class Edges(list):
    def __init__(self, inputL=[], head = ['Start', 'End', 'Score'] ):
        list.__init__(self, inputL)
        self.header = head
        self.regions = safedict()

        s = self[0]
        for e in self[1:]:
            self.regions[ s ] = Region(s,e-1)
            s = e

    def __repr__(self): 
        ret = '%s\n' % '\t'.join(self.header)
        for r in self.get_regions():
            ret = "%s%s\t%s\t%s\n" % ( ret, r.get_start(), r.get_end(), 
                                       r.get_score() )
        return ret


    def get_starts(self): return self[:-1]

    def get_regions(self):
        if self == []: raise Exception('Can\'t calc pos without region edges')

        for s in self.get_starts():
            yield self.regions[ s ]
            





class ExonTilingMatrix(Edges):
    '''
   - Associated Class of this Matrix: get_parent()
   - All combinations of isoforms: get_combinations()
   - Matchs in all segments and combinations (X/Y) : get_share_of_match()
   - And the percent of that: get_match_percent()
   - Start and end positions of all segments: get_regions()
   - Lengths of all segments: get_region_lengths() '''
   
    def __init__(self, id, mat, inputLorE=[], comb=[], p_ref='not refferred'):
        Edges.__init__(self, inputLorE)
        self.ID = id
        self.parent = 'no parent'
        self.combinations = comb
        self.matrix = {}
        self.rows = {}

        if type(mat) is dict: self.matrix = mat
        else: raise TypeError
        if p_ref != 'not refferred': self.set_parent(p_ref)

        


    def __repr__(self):
        ret = (self.ID, self.edges, self.get_all_frac_of_match(), self.matrix)
        return str(ret)
    def __str__(self):
        ret = (self.ID, self.edges, self.get_all_frac_of_match(), self.matrix)
        return str(ret)


    def get_segments(self):

        strand = []
        parentID = self.parent.get_ID()
        chromosome = self.parent.get_chromosome()
        try:
            strand.append( self.parent.get_strand() )
        except AttributeError:
            strand = ['plus','minus']
            
        count = 0
        for region in self.get_regions():
            for s in strand:
                uniq = "%s:%s:%s-%s" % ( chromosome, s, str(region[0]),
                                         str(region[1]) )
                id = "%s_%s" % (parentID, count)
                count += 1

                try:
                    matchL = self.matrix[region[0]].values()
                except:
                    com = 'Error: cannot retrieve value from dict: %s, paretnt: %s, key: %s'\
                     % (self.matrix, self.parent, region[0])
                    raise Exception(com)
                    
                yield Segment(chromosome, s, str(region[0]),
                              str(region[1]), id, sum(matchL),
                              len( self.get_combinations() ),uniq)




    def printout(self, tb, criteria = []):
        parentID = self.parent.get_ID()
        chromosome = self.parent.get_chromosome()
        strand = ''
        try:
            strand = self.parent.get_strand()
        except AttributeError:
            strand = 'both'
            
        num = 0
        for region in self.get_regions():
            #yield self.get_ID()
           
            reg = ( chromosome, strand, str(region[0]), str(region[1]) )
            tb.append( 'region', "%s:%s:%s-%s" % reg )

            id = "%s_%s" % (parentID, num)
            tb.append( 'ID', id )
            tb.append( 'chromosome', chromosome )
            tb.append( 'strand', strand )
            tb.append( 'start', str(region[0]) )
            tb.append( 'end', str(region[1]) )
            tb.append( 'length', str(region[1] - region[0] +1) )
            tb.append( 'fraction', self.get_frac_of_match(region[0]) )
            tb.append( 'percent', str(self.get_match_percent(region[0])) )
            #for i, combination in enumerate(self.combinations):
            #    name = "comb_%s" % i
            #    tb.append( name, str(self.matrix[region[0]][combination]) )

            tb.output(',')
        return 



    def set_parent(self, parent_ref):
        if parent_ref.__class__ is SAT: pass
        elif parent_ref.__class__ is Cluster: pass
#        else: raise (TypeError, parent_ref)
        self.parent = parent_ref

    def get_parent(self):
        return self.parent

    def get_combinations(self):
        return self.matrix[self.edges[0]].keys()

    def get_all_frac_of_match(self):
        ret = {}
        for start_pos, comb in self.matrix.iteritems():
            matchL = comb.values()
            ret[start_pos] = "%s/%s" % ( str( sum(matchL) ), 
                                         str( len(matchL) ) )
        return ret
    def get_frac_of_match(self, start_pos):
        matchL = self.matrix[start_pos].values()
        return "%s/%s" % ( str( sum(matchL) ), str( len(matchL) ) )

    def get_all_match_percent(self):
        ret = {}
        for start_pos, comb in self.matrix.iteritems():
            matchL = comb.values()
            if matchL: ret[start_pos] = float( sum(matchL) ) / len(matchL)
            else: ret[start_pos] = 0
        return ret
    def get_match_percent(self, start_pos):
        matchL = self.matrix[start_pos].values()
        if matchL: return float( sum(matchL) ) / len(matchL)
        else: return 0

    def print_in_format(self, d = '\t'):
        infoL = self.get_matrix_in_format2()
        hline = '=' * ( len(self.ID) + 4 + 8*4 )
        print hline
        print infoL[0].replace(',','\t')
        print hline
        for line in infoL[1:]:
            print line.replace(',','\t')
        print hline


    def generate_all_info(self):
        parentID = self.parent.get_ID()
        chromosome = self.parent.get_chromosome()
        fields = ['parent ID', 'chromosome', 'strand', 'start', 'length', 
                  'percent', 'fraction']

        yield len(fields) + len(self.combinations)
        yield len(self.edges) - 1

        strand = ''
        try:
            strand = self.parent.get_strand()
        except AttributeError:
            strand = 'both'

        header = []
        header.extend(fields)
        header.extend(self.combinations)
        for h in header:
            yield h
        for segment in self.edges[:-1]:
            #yield self.get_ID()
            yield parentID
            yield chromosome
            yield strand
            yield str(segment)
            yield str(self.lengths[segment])
            yield str(self.get_match_percent(segment))
            yield self.get_frac_of_match(segment)
            for combination in self.combinations:
                yield str(self.matrix[segment][combination])
        return

    def print_all_info(self, d = '\t'):
        parentID = self.parent.get_ID()
        chromosome = self.parent.get_chromosome()
        fields = ['parent ID', 'chromosome', 'strand', 'start', 'length', 
                  'percent', 'fraction']
        strand = ''
        try:
            strand = self.parent.get_strand()
        except AttributeError:
            strand = 'both'
        header = []
        header.extend(fields)
        header.extend(self.combinations)
        print d.join(header)
        for segment in self.edges[:-1]:
            #print self.get_ID()
            print d.join( [parentID, chromosome, strand, str(segment),
                          str(self.lengths[segment]),
                          str(self.get_match_percent(segment)),
                          self.get_frac_of_match(segment) ] ) \
                          ,
            for combination in self.combinations:
                print "%s%s" % ( d, str(self.matrix[segment][combination]) ) ,
            print '\n'

    # ID, start, length, match
    def get_matrix_in_format1(self, d = ','):
        frac = self.get_all_frac_of_match()
        length = self.get_region_lengths()

        res = []
        sp = ' ' * ( len(self.ID) - 4 )
        header = [''.join( ['= ID', sp] ), 'start', 'length', 'match' ]
        res.append( d.join(header) )
        for st in self.edges[:-1]:
            infoL = [ self.ID, str(st), str(length[st]), str(frac[st]) ]
            res.append( d.join(infoL) )
        return res

    # ID, start, length, match, combination1, ... , combinationN
    def get_matrix_in_format2(self, d = ','):
        res = []
        format1 = self.get_matrix_in_format1()

        combinations = self.get_combinations()

        header = format1[0].split(',')
        header.extend(combinations)
        res.append( d.join(header) )

        for infoL in format1[1:]:
            elements = infoL.split(',')
            for comb in combinations:
                val = self.matrix[ int(elements[1]) ][ comb ]
                elements.append(str(val))
            res.append( d.join(elements) )
        return res


    def keys(self):
        return self.matrix.keys()

    def values(self):
        return self.matrix.values()

    #def get_matrix(self):
    #return self.matrix



    def get_rows(self):
       
        strand = []

        parentID = self.parent.get_ID()
        chromosome = self.parent.get_chromosome()
        strand = ''
        try:
            strand.append( self.parent.get_strand() )
        except AttributeError:
            strand = ['plus','minus']
            
        num = 0
        for region in self.get_regions():

            for s in strand:
                ret = {}
                ret['chromosome'] = chromosome
                ret['strand'] =  s
                ret['start'] =  str(region[0])
                ret['end'] = str(region[1])
            
                reg = ( chromosome, s, str(region[0]), str(region[1]) )
                ret['region'] = "%s:%s:%s-%s" % reg

                id = "%s_%s" % (parentID, num)
                ret['ID'] = id
            
                ret['length'] = str(region[1] - region[0] +1)
                ret['fraction'] = self.get_frac_of_match(region[0])
                ret['percent'] = str(self.get_match_percent(region[0]))

                yield ret

        return 


class Segment:
    def __init__(self, chr, stra, st, ed, id, num_mch, num_cmb, unq):
        self.h = {}
        self.h['chromosome'] = chr
        self.h['strand'] = stra
        self.h['start'] = st
        self.h['end'] = ed
        self.h['ID'] = id
        self.h['length'] = int(ed) - int(st) +1
        self.h['n_match'] = num_mch
        self.h['n_combination'] = num_cmb
        self.h['fraction'] = "%s/%s" % ( num_mch, num_cmb )
        self.h['percent'] = 1.0 * num_mch / num_cmb
        self.h['segment'] = unq

        self.fields = [ 'segment','ID', 'chromosome', 'strand', 'start', 'end',
                        'length', 'fraction', 'percent' ]

    def __str__(self):
        f = ('segment','ID','length','fraction','percent')
        return '\t'.join(list( self.get_all_data(f) ) )

    def get_data(self,inp): return self.h[inp]

    def get_length(self): return self.h['length']
    def get_percent(self): return self.h['percent']

    def get_all_data(self, fields = []):
        if not fields:
            fields = self.fields
        for f in fields:
            yield str(self.h[f])
        return

    def get_fields(self):
        return self.fields




def getSAT(get, chrD):

    for line in get:
         chr, SATID, ovlplen, ovlpInfo, \
              clidA, idA, strA, startA, endA, \
              clidB, idB, strB, startB, endB = line.rstrip().split()
         ## Temporary. 
         ## Because Numata-hash seem to have pos counted not from +1 butfrom +2
         ##    as the first position of genome.
         cl1 = Cluster(clidA, chr, strA, int(startA)-1, int(endA)-1)
         cl2 = Cluster(clidB, chr, strB, int(startB)-1, int(endB)-1)

##        cl1 = Cluster(clidA, chr, strA, int(startA), int(endA))
         cl1.set_rep(idA)
##        cl2 = Cluster(clidB, chr, strB, int(startB), int(endB))
         cl2.set_rep(idB)

         pair = SAT(SATID, cl1, cl2, ovlplen, ovlpInfo)

        ##print cl1
        ##print cl2

         if not chrD.has_key(chr):
             chrD[chr] = Chromosome(chr)
#             print "add to:", chr, ":", Chromosome(chr)

#         print >> sys.stderr, "chrD[chr]:", chrD[chr]
         chrD[chr].add_SAT(pair)



def getIsoforms(get, chrD, test=False):
    for line in get:
        chr, clusterID, estID, strand, starts, ends, file, alignment  \
             = line.rstrip().split('\t')
        #print chr, clusterID, estID, starts, ends

        sarr = starts.split(',')
        earr = ends.split(',')
        exonRegions = []
        while sarr:
            ## Temporary. 
            ## Because Numata-hash seem to have pos counted not from +1
            ## but from +2
            ##    as the first position of genome.
            #tempArray = [ int(sarr.pop(0)), int(earr.pop(0)) ]
            tempArray = [ int(sarr.pop(0)) -1, int(earr.pop(0)) -1 ]
            exonRegions.append(tempArray)


        thisIsoform = Isoform(estID, exonRegions, file, alignment)

        try:
            chrD[chr].get_cluster(strand, clusterID).add_isoform(thisIsoform)
        except KeyError:
            if test: pass
            else:
                print >> sys.stderr, "Valid keys are:", chrD.keys()
                raise


def load_nst(inpf, exStatus = ''):
    chrD = {}

    inpf = lookup_file(inpf)
    perl4sat = lookup_file('getSATpairs.pl')
    perl4isf = lookup_file('getAltIsoforms.pl')
        
    if not exStatus.endswith(' '): exStatus +=' '
    print >> sys.stderr, "Loading SAT info..."
    put, get = os.popen4(perl4sat + ' ' + exStatus + inpf)
    getSAT(get, chrD)
#    print >> sys.stderr, "Keys of chrD:", chrD.keys()
    print >> sys.stderr, "Loading isoforms..."
    put, get = os.popen4(perl4isf + ' ' + exStatus + inpf)
    getIsoforms(get, chrD, exStatus)
    print >> sys.stderr, "(Loaded)"

    spe = ''
    for s in ('H','M'):
        if s in os.path.basename(inpf).split('.'): spe = s
    if not spe: raise Exception('No species keyword')
        
    # Make address book
    bookID = 'SAT_' + genome_version[spe]
    addressBook = AddressBook( bookID, spe, genome_version[spe],
                                   allPairsFile[spe] ) 
    addressBook.set_chromosomes(chrD)
    addressBook.make_address_book()

    # Set coding potential
    try:
        addressBook.set_coding_potential()
        print >> sys.stderr, 'Coding potential was added to %s' % bookID
    except IOError:
        print >> sys.stderr, 'Failed to add coding potential to %s' % bookID
        pass

    return addressBook





if __name__ == '__main__':

    inpf = sys.argv.pop()
    print >> sys.stderr, 'Input File:', inpf
    
    noextfn = os.path.basename(inpf).split('.')
    noextfn.pop()
    
#    logfh2 = open('.'.join( noextfn + ['2', 'log'] ),'w')
#    logfh3 = open('.'.join( noextfn + ['3', 'log'] ),'w')
    
    outfh = sys.stdout
    try:
        pos = sys.argv.index('-o')
        outf = sys.argv[ pos+1 ]
        if sys.argv[pos+1].startswith('-'): raise UnDefError
        outfh = open(outf,'w')
        print >> sys.stderr, 'Output File:', outf
    except ValueError:
        print >> sys.stderr, 'Output: stdout'
    except (IndexError, UnDefError):
        outf = '.'.join( noextfn + ['txt'] )
        print >> sys.stderr, 'Output File:', outf
        outfh = open( outf, 'w')

    if '-log' in sys.argv or '-test' in sys.argv:        
        logf = outf[:outf.rfind('.')] + '.log'
        print >> sys.stderr, 'Log File:', logf
        logfh = open(logf,'w')




## ----------------------------------------
##     Load Data
## ----------------------------------------
    print >> sys.stderr, '\nLoad data:', inpf
    addressBook = ''
    if inpf.endswith('.pkl'): addressBook = cPickle.load(open(inpf, 'r'))
    elif inpf.endswith('.nst'): 
        if '-test' in sys.argv: addressBook = load_nst(inpf, 'test ')
        else: addressBook = load_nst(inpf)        
    else: raise Exception('Invalid file: %s\nNeither *.nst nor *.pkl\n' % inpf)
    print >> sys.stderr, 'Data loaded.'



    if '-test' in sys.argv:
        print addressBook

    ## ------------------------------------------
    ##  Filetering
    ## ------------------------------------------
    print >> sys.stderr, 'Filtering...\n   by length >= 60 and if-within-exon'
    ds = BaseDataSet()    
    for cluster in addressBook.get_all_clusters():
        exon_overlapping = False

        ols = cluster.get_overlapping_status()

        for region in ols.get_regions():
            len = region.get_length()
            if len < 60:
                try:
                    print >> logfh, 'Shorter than 60:', len, 
                    print >> logfh, cluster.get_all_SAT_ID(), region.get_ID(),
                    print >> logfh, region.get_loc_string(), ':',
                    print >> logfh, region.get_strand()
                except NameError: pass
            elif not cluster.exon_exists_at( region.get_start() ):
                try:
                    print >> logfh, 'Not within exon:', 
                    print >> logfh, cluster.get_all_SAT_ID(), region.get_ID(),
                    print >> logfh, region.get_loc_string(), ':', 
                    print >> logfh, region.get_strand()
                except NameError: pass
            else: ds.add(region)

    ## ---------------------------------------------------------
    ##     AFAS
    ## ---------------------------------------------------------
    print >> sys.stderr, 'Adding AFAS...'
    afas = []
    for r in ds.get_regions():
        if not r.get_score(): # If not have exon on opposite strand,
            af = copy.copy(r)
            af.set_ID( "AFAS:%s" % af.get_ID() )
            af.set_strand( af.get_strand().reverse() )
            afas.append(af)
    for af in afas: ds.add(af)

        
    

    spe = addressBook.get_species()
    ucscpath = os.path.join('/', 'pub', 'dnadb', 'ucsc', genome_version[spe], 'bigZips')
    
    ## --------------------------------------
    ##   Set sequence
    ## --------------------------------------
    print >> sys.stderr, 'Set sequence.'
    ds.set_sequences(ucscpath)
    print >> sys.stderr, '(done) set sequence'


    ## --------------------------------------
    ##   Get duplication info
    ## --------------------------------------
##     print >> sys.stderr, 'Checking duplicated sequences...'
##     duplications = ds.get_duplicated_sequences()
##     print >> sys.stderr, 'Filtering...\n',
##     print >> sys.stderr, '   by the uniquencess of nucleotide sequence'

    ## --------------------------------------
    ##   Output
    ## --------------------------------------
    for r in ds.get_regions():
##         if r.get_sequence() in duplications:
##             try:
##                 print >> logfh, 'Nonspecific sequence:', r.get_ID(), 
##                 print >> logfh, region.get_loc_string(), r.get_sequence()
##             except NameError: pass    
##         else:
##             try:
##                 print >> logfh, 'Specific sequence:', r.get_ID(),
##                 print >> logfh, region.get_loc_string(), r.get_sequence()
##             except NameError: pass

            allsat = r.get_cluster().get_all_SAT_ID()

            print >> outfh, '>lcl|%s|%s|ol:%d|%s' % \
            ( r.get_ID(), '='.join(allsat), int( r.get_score() ),
              r.get_loc_string(str) )

            print >> outfh, r.get_sequence()

    outfh.close()
    logfh.close()
    


    # Dump
    if '-dump' in sys.argv:
        fname = 'Sequences.' + spe + '.pkl'
        print >> sys.stderr, "Dump sequenes...", fname
        FILE = open(fname, 'w')
        cPickle.dump(ds, FILE)


    if '-dumpl' in sys.argv:
        fname = 'SATpairs.' + spe + '.pkl'
        print >> sys.stderr, "Dump this Addressbook to fname...", 
        FILE = open(fname, 'w')
        cPickle.dump(addressBook, FILE)
        #cPickle.dump(chrD, FILE)
