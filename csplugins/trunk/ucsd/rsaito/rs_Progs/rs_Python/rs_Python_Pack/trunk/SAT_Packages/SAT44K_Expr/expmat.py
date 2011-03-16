#!/usr/bin/env python

import sys
import time
import cPickle

#from array import array as parray
from numpy import array
from numpy import zeros
from numpy import arange
from numpy import average

import file44k
import resfile
import probes


class Mat:
    def __init__(self, array_files='', data_info = 'gProcessedSignal',
                 probeset = ''):
        self.keyword =  ''

        self.data_info = None
        self.condition2idx = {}  # Key: file44k.*Fname instance; Val = index
        self.probeID2idx = {}    # Key: probe ID; Val = index
        self.probeID2probes = {} # Key: probe ID; Val = Probe instance

        self.values = None


        self.set_data_info(data_info)

        if probeset:
            self.set_probes(probeset)

        if array_files:
            self.condition2idx = dict(zip( array_files, 
                                           arange(len(array_files)) ))
            self.load()

    def want(self): 
        return [ 'gProcessedSignal', 'gIsPosAndSignif', 'gIsWellAboveBG' ]

    def key(self): 
        return 'ProbeName'
    
    def set_keyword(self, kw):
        try:
            self.keyword = '_'.join(kw)
        except ValueError: # Temporally fixed.
            self.keyword = str(kw)

    def set_data_info(self, dati):
        try:
            self.data_info = self.want()[dati]
        except KeyError:
            if dati in self.want(): self.data_info = dati
            else: raise ValueError( 'Choose from %s' % self.want() )

    def set_probes(self, file):
        if isinstance(file, file44k.BaseFname):  
            allprb = probes.read(file)
            print >> sys.stderr, 'Newly load probes.'

        elif isinstance(file, dict) \
                 and isinstance(file.itervalues().next(), probes.Probe): 
            allprb = file
            print >> sys.stderr, 'Use given probeset.'

        else:
            raise Exception('Invalid input to set_probes: %s' % file)


        # ID -> idx
        no_control = [ p for p in allprb.iterkeys() 
                       if allprb[p].is_not_control() ] #Delete control probes
        self.probeID2idx = dict(zip( no_control, 
                                     arange(len(no_control)) ))

        # ID -> Probe instance
        for id, p in allprb.iteritems():
            if p.is_not_control():
                self.probeID2probes[id] = p


    def get_probe(self, v=''): 
        if v: return self.probeID2probes[ v ]
        else: return self.probeID2probes.values()

    def get_probes(self, v = 0):
        """ m.get_probes( ) -> list( probe_ID )
        m.get_probes(1) -> list( probe_instances )
        """
        if v:
            return self.probeID2probes.values()
        else:
            return self.probeID2probes.keys()
        

    def get_expression_of_probe(self, prb):
        """ m.get_expression_of_probe( probe_ID or probe_instance )
        -> array( values )
        """
        try:
            idx = self.probeID2idx[prb]
        except KeyError:
            idx = self.probeID2idx[ prb.get_ID() ]

        return self.values[:, idx]


    def get_expression_of_condition(self, cond):
        idx = self.condition2idx[ cond ]
        return self.values[idx]


    def get_conditions(self):
        f = file44k.Files(None)
        for cond in self.condition2idx.keys():
            f.add(cond)
        return f

    def normalize_by(self, standard):

        if isinstance(standard, float) or isinstance(standard, int):
            standard_ave = standard

        elif isinstance(standard, file44k.BaseFname):
            idx = self.condition2idx[ standard ]
            standard_ave = average( self.values[idx] )

        elif not standard:
            standard_ave = average( self.values[0] )

        else: 
            com = 'Input must be float, int or file44k.Fname instance'
            raise ValueError(com)


        # Calculate and replace value to standardized version
        for cond, idx in self.condition2idx.iteritems():
            cur = self.values[idx]
            cur = cur * standard_ave / average( cur )
            self.values[idx] = cur
            

        # Inform parameter for standardization
        print >> sys.stderr,\
            'Value were standardized based on:'
        print >> sys.stderr,\
              '  %s (by average:%s)' % (standard, standard_ave)
            
        self.norm_std_ave = standard_ave

        return standard_ave



    def load(self):
        skipprb = {}
        class Cntr:
            def __init__(self): self.cnt = 0
            def up(self): self.cnt+=1
            def dn(self): self.cnt-=1
            def __repr__(self): return '%s' % self.cnt

        def read_one_file(cond):
            fpart = resfile.WholeFile( cond.get_file_name() ).\
                    get_file_part('FEATURES')

            #print fpart.header
            #print fpart.casttype

            kw = [self.key(), self.data_info]
            #print 'datainfo:',self.data_info

            ret = zeros(len(self.probeID2idx))
            for prb, value in fpart.get_entries(kw):
                try:
                    pidx = self.probeID2idx[ prb ]
                    ret[pidx] = value
                except KeyError:
                    count = skipprb.setdefault(prb, Cntr()).up()

            return ret


        # Set probes
        if not self.probeID2idx:
            self.set_probes(self.condition2idx.iterkeys().next())

        # Make matrix
        nprb = len(self.probeID2idx)
        ncnd = len(self.condition2idx)
        self.values = zeros( ncnd * nprb ).reshape( ncnd, nprb )
        print >> sys.stderr, 'Matrix (%s x %s)' % (ncnd, nprb)

        # Load expressions
        print >> sys.stderr, 'Loading values...'
        for cond in self.condition2idx:
            print >> sys.stderr, '  %s' % cond
            cidx = self.condition2idx[ cond ]
            self.values[ cidx ] = read_one_file(cond)
            
        # Log( skipped probes. maybe controls )
        fh = open('Skipped_probes.log', 'w')
        for id, count in skipprb.iteritems():
            print >> fh, '\t'.join((id,str(count)))
        print >> sys.stderr, 'Skipped probes info -> Skipped_probes.log'




def read(kw = ['Mouse','Fraction','dT'], column = 0, prb = ''):

    f = file44k.Files()
    files = f.kwget(kw)

    # Probes
    if not prb: prb = probes.read(files[0])
    
    mat = Mat(files, column, prb)
    mat.set_keyword(kw)
    return mat



if __name__ == '__main__':
    
    mat = read()
    print mat.values

