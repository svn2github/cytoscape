#!/usr/bin/env python

import sys
import cPickle
import Expr_Packages.Expr_II.Probe1
import resfile

class ControlWarning(Warning):
    '''Warning that Probe is Control'''

class Probe(Expr_Packages.Expr_II.Probe1.Probe):
    want = ( 'ProbeName', 'Sequence',
             'ControlType', 'Description', 'GeneName','SystematicName' )

    def __init__(self, arg):

        Expr_Packages.Expr_II.Probe1.Probe.__init__(self, '')
        self.control_type = None
        self.description = None
        self.gene_name = None
        self.systematic_name = None

#        self.get_ID = self.get_probeid


        if isinstance(arg, str):
            self.set_ID( arg )

        elif isinstance(arg, Expr_Packages.Expr_II.Probe1.Probe):
            self.set_ID( arg.get_probeid() )
            self.set_transcript( arg.get_transcript() )
            self.set_sequence( arg.get_sequence() )

            if isinstance(arg, Probe):
                self.set_description( arg.get_description() )
                self.set_gene_name( arg.get_gene_name() )
                self.set_systematic_name( arg.get_systematic_name() )

        elif isinstance(arg, list):
            while arg:
                self.set_ID(arg.pop(0))
                self.set_sequence(arg.pop(0))
                self.set_control_type(arg.pop(0))
                self.set_description(arg.pop(0))
                self.set_gene_name(arg.pop(0))
                self.set_systematic_name(arg.pop(0))

        else:
            com = 'Argument must be str or list or Probe. Input: %s'
            raise ValueError, com % arg.__class__
        


    def set_ID(self, v): self.probeid = v
    def set_control_type(self, v): self.control_type = bool(v)
    def set_description(self, v): self.description = v
    def set_gene_name(self, v): self.gene_name = v
    def set_systematic_name(self, v): self.systematic_name = v

    def get_ID(self): return self.probeid
    def get_control_type(self): return self.control_type
    def get_description(self): return self.description            
    def get_gene_name(self): return self.gene_name
    def get_systematic_name(self): return self.systematic_name

    def get_transcript(self):
        gn = self.get_gene_name()
        if gn != 'unknown':
            return gn
        else:
            return self.get_systematic_name()

    def is_not_control(self): 
        if self.control_type == 0: return True
        else: return False

    def is_control(self):
        if self.control_type == 0: return False
        else: return True

    def control_check(self):
        if self.is_control(): 
            raise ControlWarning

    def __str__(self):
        return 'Probe: %s' % '\t'.join( [self.get_ID(), self.get_sequence(), 
                                         self.get_transcript(), 
                                         self.get_description()]
                                        )


def read_all(fnames, dumpfile = False):
    ''' probes.read( files_iterable[, dumpfile] ) -> dict_of_probes '''

    print >> sys.stderr, 'Loading probe info...'

    for fname in fnames:

        try: fname = fname.get_file_name()
        except AttributeError: pass

        fpart = resfile.WholeFile(fname).get_file_part('FEATURES')

        # Get all Probe instances
        probes = {}
        for lst in fpart.get_entries( Probe.want ):
            if not lst[0] in probes:
                cur = Probe( lst )
                probes[ cur.get_ID() ] = cur


    print >> sys.stderr, 'Done.'

    if dumpfile: cPickle.dump(probes, open(dumpfile,'w'))

    return probes


def read(fname, dumpfile = False):
    ''' probes.read( file[, dumpfile] ) -> dict_of_probes '''

    print >> sys.stderr, 'Load probe info from:\n  %s' % fname

    try: fname = fname.get_file_name()
    except AttributeError: pass

    fpart = resfile.WholeFile(fname).get_file_part('FEATURES')

    # Get all Probe instances
    probes = {}
    for lst in fpart.get_entries( Probe.want ):
        if not lst[0] in probes:
            cur = Probe( lst )
            probes[ cur.get_ID() ] = cur


    print >> sys.stderr, 'Done.'

    if dumpfile: cPickle.dump(probes, open(dumpfile,'w'))

    return probes




#     # Set Probes onto the spots of Array 
#     array = ArrayGrid()

#     counter = 0
#     for lst in fpart.get_entries( Feature.want ):

#         pid = lst.pop()
#         lst.append( probes[pid] )
#         cur = Feature( lst )
        
# #         counter+=1
# #         if counter == 15: sys.exit()
# #         print counter,'::', cur

#         array.add( cur )
#     print >> sys.stderr, 'Array grid instance was made.'

# #        features[ cur.get_ID() ] = cur
        
#     return array




if __name__ == '__main__':
    
    import sys
    import file44k

    f = file44k.Files()

    species = ('Human', 'Mouse')
    for spe in species:

        files = f.kwget(spe)
        read(files, 'Probes.%s.pkl' % spe )
            

    
