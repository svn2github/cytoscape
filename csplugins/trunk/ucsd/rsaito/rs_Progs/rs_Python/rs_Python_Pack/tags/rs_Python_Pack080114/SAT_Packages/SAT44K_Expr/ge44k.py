#!/usr/bin/env python

import sys
import cPickle
import time

from featext import expression
from featext import file44k

from featext import expmat


def dump(kws_set = [['Mouse','Fraction','dT']] ):

    print >> sys.stderr,'Keywords:'
    print >> sys.stderr, '\n  '.join( [ '\t'.join(x) for x in kws_set] )


    # Probes
    prbd = {}
    usespe = [ x for x in ('Human','Mouse') if x in str(kws_set) ]
    print 'species:',usespe


    for spe in usespe:
        fl = file44k.Files(spe).get()[0]
        prbd[spe] = probes.read(fl)
    
        
    # Value matrix
    f = file44k.Files()
    for keywords in kws_set:

        print 'keywords:', keywords
        files = f.kwget(keywords)


        spe = files[0].get_species()


        mat = expmat.Mat(files, 0, prbd[spe])
        
        outf = 'NormExp.%s.pkl' % '_'.join(keywords)
        basic.dump(mat, outf)

        


def test():
    print >> sys.stderr, 'Test exec'


    f = file44k.Files()

    species = ('Human', 'Mouse')

#     for spe in species:
#         files = f.kwget(spe)
#         exp_ds = expression.read(files)

    flst1 = f.kwget(['Mouse','Testis', 'Fraction', 'dT'])
    flst2 = f.kwget(['Brain','Human','Oligo dT primer'])

    for f in flst1: print >> sys.stderr, f
    for f in flst2: print >> sys.stderr, f

    print >> sys.stderr, 'load human'
    exp = expression.read(flst2)    
    print >> sys.stderr, 'done'
    
    print >> sys.stderr, 'load mouse'
    exp2 = expression.read(flst1)
    print >> sys.stderr, 'done'

    print >> sys.stderr, 'normalize human'
    norm_base_ave = exp.normalize_by(flst2[0])
    print >> sys.stderr, 'done'

    print >> sys.stderr, 'normalize mouse'
    exp2.normalize_by( norm_base_ave )
    print >> sys.stderr, 'done'


    for f in  flst1: print >> sys.stderr, f
    i = 0
#     for pat in exp2.get_signal_patterns(): 
#         print pat.get_probe().get_ID(), pat.get_exp_levels()
#         print pat.get_probe().get_ID(), pat.get_signal_levels()

#         i+=1
#         if i == 10: break

    for f in  flst2: print >> sys.stderr, f
    i = 0
#     for pat in exp.get_signal_patterns():
#         print pat
#         print pat.get_probe().get_ID(), pat.get_exp_levels()
#         print pat.get_probe().get_ID(), pat.get_signal_levels()

#         i+=1
#         if i == 10: break

    print exp2



if __name__ == '__main__':
    import sys
    
    if '-dump' in sys.argv:
        dump()
    else: 
        test()
