#!/usr/bin/env python

from featext import file44k
from featext import resfile
from featext import probes











class Dataset:
    def __init__(self, dataset_cond_kw = ['Mouse']
                 dataset_set_prb_kw = ['SAT']):

        self.norm_base_keywords = norm_base_keywords     # 1 dim list

        self.cond_keywords_list = [ dataset_cond_kw ]    # 2 dim list
        self.prb_keywords_list = [ dataset_set_prb_kw ]

        
    def filter_probes():

        # Get all Probes
        spe2probes = {}
        for spe in species:
            spe2probes[ spe ] = probes.read(files.kwget( spe ))







        
                 

def read(norm_base_keywords = ['Brain','mouse','dT']):
    f = file44k.Files()
    
    def timestump():
        print >> sys.stderr, '\n',time.ctime(), '\n'
    
    timestump()


    # Load file info
    files = file44k.Files()

    species = ('Human', 'Mouse')

    dump_set_key_word_list = [ ['Mouse'],
                               ['Human', 'Tissue'],
                               ['Human', 'Cancer', 'Hepatic'],
                               ['Human', 'Cancer', 'Colon', 'dT'],
                               ['Human', 'Cancer', 'Colon', 'Random']
                               ]


    
>


    # Print info
    print >> sys.stderr, 'Dump set:'
    for k in dump_set_key_word_list:
        print >> sys.stderr, ' ', '_'.join(k), '(%s)' % len(files.kwget(k))
        for i in files.kwget(k):
            print >> sys.stderr, '   ', i
        

    # Normalize base file
    normbase = files.kwget(norm_base_keywords)[0]
    print >> sys.stderr, 'Normalize base:'
    print >> sys.stderr, ' ', normbase




    for dump_set_kw in dump_set_key_word_list:

        timestump()

        outf = 'NormExp.%s.pkl' % '_'.join(dump_set_kw)
        print >> sys.stderr, 'Output:'
        print >> sys.stderr, ' ', outf

        files = files.kwget(dump_set_kw)

        exp = expression.read(files, probes[ dump_set_kw[0].title() ])
        
        timestump()
        
        normbase = exp.normalize_by(normbase)

       
        print >> sys.stderr, 'Normalize done.'

        timestump()

        print >> sys.stderr, 'Pickling...'

        fh = open(outf, 'w')
        cPickle.dump(exp, fh)
        fh.close()
        del exp

        print >> sys.stderr, 'Done.'


    normf = 'normbase_%s.pkl' % '_'.join(norm_base_keywords)
    print >> sys.stderr, 'Dump Normalize base to: normf'
    cPickle.dump(normbase, open(normf,'w'))
    




if __name__ == '__main__':

    species = ('Mouse', 'Human')

    # Get all Probes
    spe2probes = {}
    for spe in species:
        spe2probes[ spe ] = probes.read(files.kwget( spe ))




    
