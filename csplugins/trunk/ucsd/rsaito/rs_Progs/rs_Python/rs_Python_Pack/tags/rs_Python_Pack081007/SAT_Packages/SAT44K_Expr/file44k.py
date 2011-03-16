#!/usr/bin/env python
# -*- coding: utf-8 -*-

import re
import sys
import os
import glob
import codecs


def convert_list_to_relist(arr):
    criteria_re = []
    for v in arr:
        if v.islower():
            criteria_re.append( re.compile(v, re.IGNORECASE) )
        else:
            criteria_re.append( re.compile(v) )
    return criteria_re
        

class BaseFname:
    
    rp = re.compile(r'(dT|Random)')
    primingtype = {'Random': 'Random primer', 'dT': 'Oligo dT primer', '':''}

    def __init__(self, fname=''):
        self.file_name = fname
        self.priming = ''
        self.sample = ''
        self.species = ''
        self.valid = True

        if fname and not os.access( fname, os.R_OK ):
            raise IOError( "Can't open file: %s" % fname )

    def __str__(self):
        return '\t'.join([self.get_species(), self.get_sample(),
                          self.get_priming()])

#     def meet(self, instance):
#         p = instance.get_priming()
#         if p:
#             self.get_priming() != p: return False
#         smp = instance.get_sample()
#         if smp: 
#             self.get_sample() != smp: return False
#         spe = instance.get_species()            
#         if spe:
#             self.get_species() != spe: return False
#         return True
            

    def get_fnpart(self):
        head, tail = os.path.split(self.file_name)
        sample, priming =  os.path.split(head)
        sample = os.path.split(sample)[-1]
        return sample, priming

    def get_species(self): return self.species
    def get_sample(self):
        return self.sample
    def get_file_name(self): return self.file_name
    def get_priming(self):
        try:
            return BaseFname.primingtype[ self.priming ]
        except KeyError:
            raise KeyError('Priming type must be "dT" or "Random"')

    def get_category(self): return 'NoCategory'

    def set_species(self, v): 
        species = ('Human', 'Mouse')
        if not v:
            pass
        elif v in species:
            self.species = v
        else:
            raise Exception('Species must be in: %s' % str(species) ) 

    def set_sample(self, v): self.sample = v
    def set_file_name(self, v): self.file_name = v
    def set_priming(self, v): self.priming = BaseFname.rp.match(v).group()
    def set_as_valid(self,v): self.valid = bool(v)


    def match_AND(self, arr):
        cond_terms = str(self).split('\t')
        cond_terms = list(set(cond_terms))

        if not arr: return True

        # Make list of regular expression to match
        for a in arr:
            if not isinstance(a, type(re.compile(''))):
                criteria_re = convert_list_to_relist(arr)
                
        else: criteria_re = arr
            
                    
        # Check whether all the criteria meet to this instance
        for criterion in criteria_re:
            for cond_term in cond_terms:
                if 0 <= criterion.search(cond_term): break
            else:
                return False

        return True



class TissueFname(BaseFname):

    key = [ x.rstrip('\n\r') for x 
            in file('/home/osada/PyMod/featext/file44kArray/exchange_ascii') ]
    val = [ re.compile( unicode(x.rstrip('\n\r'), 'utf-8') ) for x 
            in file('/home/osada/PyMod/featext/file44kArray/jplist') ]
    re_dict = dict( zip(key, val) )
    re_dict[ r'ES-Neuron day \1' ] = re.compile(r'D(\d+)')
    re_dict[''] = re.compile('Human ')

    re_day = re.compile(r'(\d+[\.[\d]+]?)day', re.I)
    re_info = re.compile(r'\((.*?)\)')

    re_subcate = re.compile(r'(fraction|day)\s*\d+\.?\d*', re.I)


    def __init__(self, fname=''):
        BaseFname.__init__(self, fname)
        self.info = ''
        self.subcate = ''

        if fname: self.analyze()


    def analyze(self):
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



    def __str__(self):
        return '\t'.join([self.get_species(), self.get_category(), 
                          self.get_sample(), self.get_sub_category(), 
                          self.get_priming()])


    def get_tissue(self): return self.get_sample()
    def get_sub_category(self): return self.subcate
    def get_info(self): return self.info
    def get_category(self): return 'Tissue'
    
    def set_tissue(self, v): self.set_sample(v)
    def set_sub_category(self, v): self.subcate = v
    def set_info(self, v): self.info = v 



class CancerFname(BaseFname):

    r = re.compile(r'(HeCa|CoCa)\s*([T|N|C])([1-9]+)')
    r2 = re.compile(r'(HeCa|CoCa)\s*([1-9]+)([T|N|C])')
    cancertype = {'HeCa': 'Hepatic cancer', 'CoCa': 'Colon cancer', '':''}
    tissuetype = {'N': 'Normal', 'C': 'Diseased', 'T':'Diseased', '':''}
    metastasis_colon2hepa = ['21' ,'22', '23', '24','25']
    invalid_colon_array = ['8', '28', '29', '32']


        

    def __init__(self, fname=''):
        self.cancer_type = ''
        self.tissue_type = ''
        self.patient_ID_number = ''

        BaseFname.__init__(self, fname)
        if fname: self.analyze()

    def analyze(self):
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
            print >> sys.stderr, self.file_name
            raise 


    def __str__(self):
        return '\t'.join([self.get_species(), self.get_category(),
                          self.get_cancer_type(), self.get_patient(),
                          self.get_tissue_type(), self.get_priming()])

    def get_cancer_type(self): return CancerFname.cancertype[self.cancer_type]

    def get_sample(self):
        if self.is_metastasis() and self.get_tissue_type() == 'Normal tissue':
            return 'Hepatic'
        else:
            return self.get_cancer_type().split()[0]

    def is_metastasis(self):
        if self.get_cancer_type() == 'Colon cancer' and \
               self.patient_ID_number in CancerFname.metastasis_colon2hepa:
            return True
        else:
            return False



    def is_invalid(self, invalid_patient=''):
        """ c.is_invalid( [invalid_patient_num_array] ) -> Bool
        when invalid_patient_num_array was omitted,
        CancerFname.invalid_colon_array is used."""

        if not invalid_patient:
            invalid_patient = CancerFname.invalid_colon_array


        invalid_patient = [ str(x) for x in invalid_patient ]

        if self.get_cancer_type() == 'Colon cancer' and \
               self.patient_ID_number in invalid_patient:
            return True
        else:
            return False


    def get_tissue_type(self):
        try:
            return '%s tissue' % CancerFname.tissuetype[self.tissue_type]
        except KeyError:
            return self.tissue_type
    def get_patient(self): 
        if self.patient_ID_number:
            return 'Patient %s' % self.patient_ID_number
        return ''
        
    def get_category(self): return 'Cancer'
    
    def set_cancer_type(self, v): 
        if v in CancerFname.cancertype.keys():
            self.cancer_type = v
        else: raise ValueError('Input must be: %s' % 
                               str(CancerFname.cancertype.keys()) )
    def set_tissue_type(self, v): self.tissue_type = v
    def set_patient_ID_number(self, v): self.patient_ID_number = v




 
class Files(list):
    def __init__(self, spe=''):
        self.species = spe
        self.files = {}
        self.tissue_files = {}
        self.cancer_files = {}
        self.kw = ''

        if spe != None:
            self.load()


    def __str__(self):
        ret = 'Files instance of: %s (species)\n' % '_'.join(self.kw)
        ret += '\n'.join( [str(x) for x in self] )
        return ret

    def get_file_path(self, spe):
        spedir = '*%s*' % spe

        cancer1 = glob.glob('/home/numassan/FE-44k-Cancer/%s/*/*/*/*.txt' 
                            % spedir)
        cancer2 = glob.glob('/home/numassan/FE-44k-Cancer/%s/*/*/*.txt'
                            % spedir)
        cancer1.extend(cancer2)

        tissue1 = glob.glob('/home/numassan/FE-44k-Tissues/%s/*/*/*.txt'
                            % spedir)
        tissue2 = glob.glob('/home/numassan/FE-44k-Tissues/%s/*/*/*/*.txt'
                            % spedir)
        tissue1.extend(tissue2)

        return cancer1, tissue1


    def load(self):
        species = ('Human', 'Mouse')
        if self.species in species:
            species = [ self.species ]
        elif not self.species:
            pass
        else:
            raise Exception('Invalid species: %s' % self.species) 



        for spe in species:

            cancer, tissue = self.get_file_path(spe)
            for a in cancer:
                tmp = CancerFname(a)
                tmp.set_species(spe)
                self.add( tmp )
            for a in tissue:
                tmp = TissueFname(a)
                tmp.set_species(spe)
                self.add( tmp )


    def append(self, v):
        cate = v.get_category()
        if  cate == 'Tissue' or cate == 'Cancer': pass
        else: raise ValueError( 'Cannot categoryze Tissue nor Cancer.')
        list.append(self,v)
        


    def add(self, v): self.append(v)
##         if v.get_category() == 'Tissue': self.tissue_files[str(v)] = v
##         elif v.get_category() == 'Cancer': self.cancer_files[str(v)] = v
##         self.files[str(v)] = v        




#     def get(self, v=''):
#         if v:
#             return self.files[ str(v) ]
#         else:
#             return self.files

    def get(self, instance=''):
        result = Files(None)
        result.kw = instance
        for f in self.files.itervalues():
            if str(instance) == str(f): result.add(f)
        return result
        
##         qlst = str(instance).split('\t')
##         qlst = list(set(qlst))
##         try: qlst.remove('')
##         except ValueError: pass
##         if len(qlst) == 0: return self.files.values()

##         files = self.files
##         if 'Tissue' in qlst:
##             files = self.tissue_files
##         elif 'Cancer' in qlst: 
##             files = self.cancer_files

##         lines = files.keys()
##         matchkeys = []
##         for keystr in lines:
##             keystrarr = keystr.split('\t')
##             keystrarr = list(set(keystrarr))
##             try: keystrarr.remove('')
##             except ValueError: pass

##             for kw in qlst:
##                 if not kw in keystrarr: break
##             else:
##                 matchkeys.append( keystr )

##         result = []
##         for m in matchkeys:
##             result.append( self.files[ m ] )

##         return result


    def get_tissue_files(self):
        for f in self:
            if f.get_category() == 'Tissue': yield f


    def get_cancer_files(self):
        for f in self:
            if f.get_category() == 'Cancer': yield f


    def kwget(self, inparr):
        try: inparr.__iter__()
        except AttributeError: inparr = [inparr]

        result = Files(None)
        result.kw = inparr

        arr = []
        arr.extend(inparr)

        if not isinstance(arr, list): arr = [arr]
        
        if 'Tissue'  in arr:
            files = self.get_tissue_files()
            arr.remove('Tissue')
        elif 'Cancer' in arr: 
            files = self.get_cancer_files()
            arr.remove('Cancer')
        else:
            files = self

        # Convert into regular expression
        criteria = convert_list_to_relist(arr)

        # Add compatible files to 'result'
        for file in files:
            if file.match_AND( criteria ): result.add( file )

        # Return
        return result



def get_cancer_file_criteria():
    criteria = [ ['Cancer', 'Hepatic', 'dT'],
                 ['Cancer', 'Hepatic', 'random'],
                 ['Cancer', 'Colon', 'dT'],
                 ['Cancer', 'Colon', 'random']
                 ]
    return criteria



if __name__ == '__main__':
    sys.argv.pop(0)

#     species = ('Human', 'Mouse')
#     if '-human' in sys.argv: species = ['Human']
#     elif '-mouse' in sys.argv: species = ['Mouse']

    files = Files()
    print files


    query = TissueFname()
    query.set_species('Human')
    query.set_tissue('Brain')
    query.set_priming('dT')

    print '-----------------------------------'
    print '   Select Human Brain dT'
    print '-----------------------------------'
    for f in files.get(query):
        print f


    print '-----------------------------------'
    print '   Select Colon cancer'
    print '-----------------------------------'

    query = CancerFname()
    query.set_cancer_type('CoCa')
    for f in files.get(query):
        print f

    print '-----------------------------------'

