#!/usr/bin/env python

import sys


import Expr2.Expression1

from featext import resfile
#import array
from featext import probes
from featext import file44k
from basic import clist


class SingleGreenSignal(Expr2.Expression1.Single_Expression):
    want = [ 'gProcessedSignal', 'gIsPosAndSignif', 'gIsWellAboveBG', 
             'ProbeName' ]

#              'Col', 'Row' ]

    def __init__(self, arg=None, exp=None):

        Expr2.Expression1.Single_Expression.__init__(self, None, None)

        self.probe = None
        self.processed_signal = None
        self.if_is_pos_and_signif = None
        self.if_is_well_above_BG = None


        # Alias
    ##############################################
    #    self.set_condition = self.set_cond
    #    self.get_condition = self.get_cond
    ##############################################
#        self.exp_level = self.normalized_exp

        if not arg: 
            pass
#         elif isinstance(arg, array.Feature):
#             self.set_feature(arg)
#             self.set_processed_signal( exp )
        elif isinstance(arg, list):
            self.set_processed_signal( arg.pop(0) )
            self.set_is_pos_and_signif( arg.pop(0) )
            self.set_is_well_above_BG( arg.pop(0) )
        else:
            com = 'Argument must be void or list %s or Feature and expression.'
            com = '%s args: %s, %s' % com
            argl = '[processed_signal, pos_sign, above_BG]'
            raise ValueError, com % ( argl, arg.__class__, exp )
        

    def set_condition(self,v): self.set_cond(v)
    def get_condition(self): return self.get_cond()

    def __str__(self):
        return 'SingleGreenSignal %s(norm:%s) at %s condition %s' \
               % ( self.get_processed_signal(),
                   self.get_normalized_expression(),
                   self.get_probe(), self.get_condition() )

    def set_ID(self, v): 
        raise AttributeError, \
              'Cannot set ID to this class. ID -> "Condition\tFeature_ID"'

    def set_probe(self, v): self.probe = v
    def set_processed_signal(self, v): self.processed_signal = float(v)
    def set_is_pos_and_signif(self, v): self.if_ispos_and_signif = bool(v)
    def set_is_well_above_BG(self, v): self.if_iswell_above_BG = bool(v)

    def set_normalized_expression(self, v): self.exp_level = v
    def get_normalized_expression(self): return self.exp_level

    def get_probe(self): return self.probe
    def get_processed_signal(self): return self.processed_signal
    def is_pos_and_signif(self): return self.if_is_pos_and_signif
    def is_well_above_BG(self): return self.if_is_well_above_BG





class ExpressionPattern(Expr2.Expression1.Expression_Pat):
    def __init__(self, probe):
        self.probe = None
        Expr2.Expression1.Expression_Pat.__init__(self, '')

        self.set_probe(probe)
        self.cond2signal = {}

        # Alias
        self.signal_pat = self.exp_pat


    def __str__(self):
        return 'ExpressionPattern of %s' % (self.get_probe())


#     def set_feature(self, v): 
# ##         if not isinstance(v, array.Feature): 
# ##             raise 'Argument must be Feature. Input: %s' % v.__class__
#         self.feature = v

    def set_probe(self, v):
        self.probe = v

    def get_signal_levels(self):
        res = []
        for exp in self.exp_pat:
            res.append( exp.get_processed_signal() )
        return res


    def get_probe(self): return self.probe
#    def get_probe(self): return self.get_feature().get_probe()

    def add_single_green_signal(self, new_signal):

#         if new_signal.get_probe() != self.get_probe():
        if new_signal.get_probe().get_ID() != self.get_probe().get_ID():
            com = 'Probe mismatch. This: %s, Input: %s' 
            raise com % (self.get_probe(), new_signal.get_probe() )

        
        self.signal_pat.append(new_signal)
        self.cond2signal[ new_signal.get_condition() ] = new_signal

    def get_signal_of_condition(self, v): return self.cond2signal[ v ]



class ArrayExpressions:

    def __init__(self, cond = ''):
        self.condition = None
        self.signals = []
        self.probe2signal = {}

        if cond: self.set_condition(cond)
        

    def set_condition(self, v): 
#         if not isinstance(v, str): 
#             raise ValueError, 'Input must be str. (Input: %s)' % v.__class__

        if self.condition == v: pass
        elif self.condition:
            com = 'Condition was already set %s. %s was not set.'
            raise Exception, com % (self.condition, v)

        self.condition = v
        

    def add(self, new_signal):
##         # Check input type
##         if isinstance(new_signal, SingleGreenSignal):
##             com = 'Input must be SingleGreenSignal. (Input: %s)'
##             raise ValueError, com % new_signal.__class__

        # Check input condition
        if self.signals == []: 
            self.probe2signal = {}
            if not self.get_condition():
                self.set_condition( new_signal.get_condition() )
        elif new_signal.get_condition() != self.get_condition():
            com = 'Condition mismatch. This: %s, Input: %s'
            raise Exception, com % (self.get_condition(), 
                                    new_signal.get_condition() )

        # Assignment
        self.probe2signal[new_signal.get_probe()] = new_signal        
        self.signals.append(new_signal)
        

    def get_probes(self):
        ret = []
        for s in self.signals: ret.append( s.get_probe() )
        ret.sort()
        return ret

    def get_condition(self): return self.condition
    def get_signals(self): return self.signals
    def num_of_signals(self): return len(self.signals)

    def get_signal(self, v=''): 
        if v: return self.probe2signal[ v ]
        else: return self.get_signals()
        
    def get_signal_levels(self, With_Control = False):
        return [ sge.get_processed_signal() for sge in self.get_signals() ]

#         if With_Control:
#             return [ sge.get_processed_signal() for sge in self.get_signals() ]
#         else:
#             return [sge.get_processed_signal() for sge in self.get_signals() 
#                     if sge.get_probe().get_probe().is_not_control()]

                     

    def _get_signal_average(self, With_Control = False):
        processed_signals = self.get_signal_levels( With_Control )
        return sum(processed_signals) / len(processed_signals)


    def _normalize_with_standard_average(self, v, With_Control = False):
        ratio  = v / self._get_signal_average(With_Control)
        for signal in self.get_signals():
            signal.set_normalized_expression( signal.get_processed_signal()
                                              * ratio )
            
##     def get_exp_levels(self): 
##         return [ sge.get_expressions

##         raise 'Under construction'




class ExpressionData(Expr2.Expression1.Expression_Data):

    def __init__(self):
        Expr2.Expression1.Expression_Data.__init__(self)

        self.arrays = {}
        self.norm_std_ave = None
        

        # Overwrite
        #self.add_exp_pat = self.add_signal_pat

        #wrong
        #self.all_signal_patterns = self.expr_pat_probe_based_set 
        #self.get_features = self.probes


    def get_probes(self): 
        return self.arrays.itervalues().next().get_probes()

#     def get_all_signal_patterns(self): 
#         return self.expr_pat_probe_based_set

    def get_signal_pattern(self, probeid): 
        return self.expr_pat_probe_based_set[ probeid ]


    def get_signal_patterns(self): 
        return self.expr_pat_probe_based_set.values()
##        self.get_all_signal_patterns().values()
##        return self.expr_pat_probe_based_set.itervalues()


    def get_signals_of_condition(self, v):
        return [ pattern.get_signal_of_condition(v)
                 for pattern in e.get_signal_patterns() ]

    def add_array(self, v):
        if self.arrays == {}: pass
        elif self.get_probes() != v.get_probes():
            com = "Probes mismatch.\n   This:[0:5] %s\n   New pattern:[0:5] %s"
            raise com % (self.get_probes()[0:5], v.get_probes()[0:5])
        
        self.arrays[ v.get_condition() ] = v


    def get_arrays(self): return self.arrays.values()
    def iter_arrays(self): return self.arrays.itervalues()

    def get_array(self, v=''):
        if v: return self.arrays[v]
        else: return self.get_arrays()


    def add_exp_pat(self, new_pattern):

        probe = new_pattern.get_probe()

        if self.expr_pat_probe_based_set == {}:
            self.expr_pat_probe_based_set[ probe ] = new_pattern
                                   
        elif self.conditions() == new_pattern.get_conditions():
            self.expr_pat_probe_based_set[ probe ] = new_pattern
                                   
        else:
            com = "Condition mismatch.\n   This:%s\n   New pattern:%s"
            raise com % (self.conditions(), new_pattern.get_conditions())
    



    def normalize_by(self, standard, With_Control=False):

        if isinstance(standard, float) or isinstance(standard, int):
            standard_ave = standard

        else:
            if isinstance(standard, ArrayExpressions):
                standard_condition = standard.get_condition()

            standard_condition = standard
            standard_ave = self.get_array(standard_condition).\
                           _get_signal_average(With_Control)


        for curarr in self.get_arrays():
            curarr._normalize_with_standard_average( standard_ave, 
                                                     With_Control )

        # Inform parameter for standardization
        if With_Control: cntrlstat = 'with control probes'
        else: cntrlstat = 'without control probes'
        print >> sys.stderr,\
            'gProcessedSignal was standardized based on:'
        print >> sys.stderr,\
            '  %s %s (by average:%s)' % (standard, cntrlstat, standard_ave)
            
        self.norm_std_ave = standard_ave

        return standard_ave
        

    def __str__(self):
        header = ['']
        header.extend( [ str(x).replace('\t','_') for x in self.conditions() ] )
        header = '\t'.join(header)

        row = []
        for prb in self.get_probes():

            cl = clist(self.expression_pat(prb))
            cl.insert(0, str(prb).replace('\t','_'))
            cl.cast(str)
            row.append( '\t'.join(cl) )

        data = '\n'.join(row)
        ret = '%s\n%s\n' %(header, data)

        return ret
        

def read(files, probe_dict='', fastmode=True):
    

                      
    if len(files) < 1: 
        raise ValueError, 'No file set'

    if isinstance(files, str): files = [files]
        
    # Get Probes. 
    # Without prb_set input, expressions for all probes are loaded.
    if not isinstance(probe_dict, dict):
        pritn >> sys.stderr, 'Probes (unlimited).'
        if fastmode:
            print >> sys.stderr, 'Probe info are based on:'
            print >> sys.stderr, '  %s' % files.__iter__().next()
            probe_dict = probes.read([ files.__iter__().next() ])
        else:
            probe_dict = probes.read(files)
        # array_grid = array.read(files.__iter__().next().get_file_name())


    # Expression Datasets (by Probe, by Array)
    allsignaldat = ExpressionData()

    print >> sys.stderr, 'Load expression data:'
    signal_pat_d = {}
    for fn in files:
        cond = fn
        try:
            fname = fn.get_file_name()
        except AttributeError:
            #print >> sys.stderr, 'Attribute'
            fname = fn

        print >> sys.stderr, '  Condition:', cond
        
        # Expression Datasets by File (Array)
        current_array = ArrayExpressions(cond)

        fpart = resfile.WholeFile(fname).get_file_part('FEATURES')
        for lst in fpart.get_entries( SingleGreenSignal.want ):

#             row = lst.pop()
#             col = lst.pop()
#             feature = array_grid.get( row, col )
            pid = lst.pop()

            try:
                probe = probe_dict[pid]
                if fastmode: probe.control_check()
            except KeyError:
                if fastmode: pass
                else: raise
            except probes.ControlWarning:
#                 print >> sys.stderr, 'skip:', probe
                pass
            else:
                cur = SingleGreenSignal( lst )

#            cur.set_feature( feature )
                cur.set_probe(probe)
                cur.set_cond( cond )

                # Add expression info to datasets (by probe, by array)
                signal_pat_d.setdefault( probe,
                                         ExpressionPattern(probe) 
                                         ).add_single_green_signal( cur )

                current_array.add( cur )

        allsignaldat.add_array( current_array )
        


    # Expression Datasets by Probe
    for pat in signal_pat_d.itervalues():
        try:
            allsignaldat.add_exp_pat( pat )
        except:
            print >> sys.stderr, allsignaldat.conditions()
            print >> sys.stderr, pat.get_conditions()
            raise


    return allsignaldat






if __name__ == '__main__':

    from expression import *

    fparams = [x for x in sys.argv[1:] if x.startswith('-') ]
    fnames = [x for x in sys.argv[1:] if not x.startswith('-') ]


    # Read
    allsignaldat = read(fnames)



    # Standardize
    for param in fparams:
        if param.startswith('-standard='):
            standard_condition = param.split('=').pop()
            break
    else:
        standard_condition = allsignaldat.iter_arrays().next().get_condition()
        
    if '-nocontrol' in fparams: WithControl = True
    else: WithControl = False

    allsignaldat.normalize_by(standard_condition, WithControl)



    # Out
    print allsignaldat



