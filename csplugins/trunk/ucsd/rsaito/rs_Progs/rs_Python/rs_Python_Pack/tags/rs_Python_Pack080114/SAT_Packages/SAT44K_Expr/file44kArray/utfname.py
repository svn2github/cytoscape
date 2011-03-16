#!/usr/bin/env python
# -*- coding: utf-8 -*-

import re
import sys
import os
import glob
import codecs

class BaseFname:
    
    rp = re.compile(r'(dT|Random)')
    primingtype = {'Random': 'Random primer', 'dT': 'Oligo dT primer'}

    def __init__(self, fname):
        self.file_name = fname
        self.priming = None
        self.sample = None


    def get_fnpart(self):
        head, tail = os.path.split(self.file_name)
        sample, priming =  os.path.split(head)
        sample = os.path.split(sample)[-1]
        return sample, priming

    def get_sample(self): return self.sample
    def get_file_name(self): return self.file_name
    def get_priming(self): return BaseFname.primingtype[ self.priming ]


class TissueFname(BaseFname):

    key = [ x.rstrip('\n\r') for x in file('/home/osada/PyMod/featext/file44kArray/exchange_ascii') ]
    val = [ re.compile( unicode(x.rstrip('\n\r'), 'utf-8') ) 
            for x in file('/home/osada/PyMod/featext/file44kArray/jplist') ]
    re_dict = dict( zip(key, val) )
    re_dict[ r'ES-Neuron day \1' ] = re.compile(r'D(\d+)')
    re_dict[''] = re.compile('Human ')

    re_day = re.compile(r'(\d+[\.[\d]+]?)day', re.I)
    re_info = re.compile(r'\((.*?)\)')

    re_subcate = re.compile(r'(fraction|day)\s*\d+\.?\d*', re.I)

    def __init__(self, fname):
        BaseFname.__init__(self, fname)
        self.info = ''
        self.subcate = ''


        sample, priming = self.get_fnpart()
        # Priming
        self.priming = BaseFname.rp.search(priming).group()
        # Sample
        self.sample = sample

        tmp_sample = unicode( self.sample, 'utf-8' )
        for  correct, r in TissueFname.re_dict.iteritems():
            tmp_sample = r.sub( correct, tmp_sample )


        tmp_sample = TissueFname.re_day.sub( r'day \1', tmp_sample )


        try:
            self.subcate = TissueFname.re_subcate.search(tmp_sample).group()\
                           .title()
        except AttributeError: pass
#        print '   subcate', self.subcate

        tmp_sample = TissueFname.re_subcate.sub('', tmp_sample)



        try:
            self.info = TissueFname.re_info.findall(tmp_sample)
        except AttributeError: pass
#        print '   info',self.info

        
        self.sample = tmp_sample.split('(').pop(0).rstrip()
        self.sample = self.sample.title()


    def get_sub_category(self): return self.subcate
    def get_info(self): return self.info
    


    def __str__(self):
        return '\t'.join([self.get_sample(), self.get_sub_category(), 
                          self.get_priming(), 
                          ])


class CancerFname(BaseFname):

    r = re.compile(r'(HeCa|CoCa)\s*([T|N|C])([1-9]+)')
    r2 = re.compile(r'(HeCa|CoCa)\s*([1-9]+)([T|N|C])')

    
    cancertype = {'HeCa': 'Hepatic cancer', 'CoCa': 'Colon cancer'}
    tissuetype = {'N': 'Normal', 'C': 'Diseased', 'T':'Diseased'}


    def __init__(self, fname):
        self.cancer_type = None
        self.tissue_type = None
        self.patient_ID_number = None
        
        BaseFname.__init__(self, fname)


        sample, priming = self.get_fnpart()
        # Priming
        self.priming = BaseFname.rp.search(priming).group()


        # Other info
        try:
            if CancerFname.r.search(priming):
                (self.cancer_type, self.tissue_type, self.patient_ID_number) \
                                   = CancerFname.r.search(priming).groups()
            elif CancerFname.r2.search(priming):
                (self.cancer_type, self.patient_ID_number, self.tissue_type) \
                                   = CancerFname.r2.search(priming).groups()
            else:
                (self.cancer_type, self.tissue_type, self.patient_ID_number) \
                                   = CancerFname.r.search(sample).groups()
        except:
            print self.file_name
            raise 

    def __repr__(self): return self.file_name()

    def __str__(self):
        return '\t'.join([self.get_cancer_type(), self.get_patient_ID_number(),
                          self.get_tissue_type(), self.get_priming()])

    def get_cancer_type(self): return CancerFname.cancertype[self.cancer_type]
    def get_tissue_type(self):
        return '%s tissue' % CancerFname.tissuetype[self.tissue_type]
    def get_patient_ID_number(self): 
        return 'Patient %s' % self.patient_ID_number
        
    
def tmp(spe):

    cancer1, tissue1 = get_file_path(spe)
    
##     print 'Cancer:' 
##     for f in cancer1: print f
##     print
##     print 'Tissue:'
##     for f in tissue1: print f

    ret = {'cancer':[], 'tissue':[]}

    for a in cancer1:
        ret['cancer'].append( CancerFname(a) )
    for a in tissue1:
        ret['tissue'].append( TissueFname(a) )


    return ret

def get_files(spe):
    spedir = '*%s*' % spe

    cancer1 = glob.glob('/home/numassan/FE-44k-Cancer/%s/*/*/*/*.txt' % spedir)
    cancer2 = glob.glob('/home/numassan/FE-44k-Cancer/%s/*/*/*.txt' % spedir)
    cancer1.extend(cancer2)

    tissue1 = glob.glob('/home/numassan/FE-44k-Tissues/%s/*/*/*.txt' % spedir)
    tissue2 = glob.glob('/home/numassan/FE-44k-Tissues/%s/*/*/*/*.txt' % spedir)
    tissue1.extend(tissue2)
    
    return cancer1, tissue1


if __name__ == '__main__':
    sys.argv.pop(0)

    species = ('Human', 'Mouse')
    if '-human' in sys.argv: species = ['Human']
    elif '-mouse' in sys.argv: species = ['Mouse']


    for s in species:
        d = tmp(s)
        for k, v in d.iteritems():
            for a in v: 

                if '-f' in sys.argv: 
                    print '%s\t' % (a.get_file_name())
                    print '%s' % a

                else: print a


    




##     for a in arr: 
##         try:
##             print TissueFname(a)
##         except UnicodeEncodeError: print a


##     try:
##         if len(sys.argv) == 0: raise IOError
##         while sys.argv:
##             file = sys.argv.pop(0)
##             print '%s\t%s' % (CancerFname(file), file)


##     except IOError:
##         print >> sys.stderr, 'Usage:\n python fname.py filename'
##         print >> sys.stderr, 'Example:\n python fname.py /home/numassan/FE-44k-Cancer/*/*/*/*.txt'
