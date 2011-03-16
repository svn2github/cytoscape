#!/usr/bin/env python

import sys
from SAT_Packages.SAT44K_Info.basic import clist
import time
    
class CastList(clist):
    def get_func_dict(self):
        return { 'integer' : int, 'float': float, 'text': str, 'boolean': bool }



class FilePart:
    def __init__(self, stfh, head=[], castt=[], skipcolnum=0):
        self.fh = stfh
        self.header = head
        self.casttype = castt
        self.start = stfh.tell()
        self.skip_column_num = skipcolnum
#        self.header_needed = []

        n = range(0, len(head))
        self.head2col = dict(zip(head,n))


    def get_col_num(self, arg):
#         print 'head2col:', self.head2col
#         print 'arg:', arg
#         time.sleep(10)

        if not ( isinstance(arg, list) or isinstance(arg, tuple) ):
            raise 'argument must be list'

        res = []
        for hd in arg:
            res.append( self.head2col[hd] )
        return res

    def set_want_header(self, arg):
        if not isinstance(arg, list): raise 'argument must be array'

        if isinstance(arg, str): arg = [arg]
        self.header_needed.extend(arg)
        self.header_needed = list(set(self.header_needed))
        

    def get_lines(self):
        '''
f.get_lines( [list_of_column_you_want] ) -> lists_of_columns.
f.get_lines() -> lines
'''
        self.fh.seek(self.start)

        l = 1
        while l:
            l = self.fh.readline()
            if l.startswith('*') or l.rstrip() == '':
                break
            else:
                yield l

    def get_entries(self, arg=[]):
        if not ( isinstance(arg, list)  or isinstance(arg, tuple) ):
            raise 'argument must be a list'

        col = self.get_col_num(arg)

        for l in self.get_lines():
            elem = CastList( l.rstrip().split('\t')[1:] )
            elem.cast(self.casttype)
            if arg:
                res=[]
#                 if elem[0] == 12:
#                     print 'col in resfile:', col
#                     print 'elem', elem
                for c in col: res.append(elem[c])
                yield res
            else: yield elem


    def get_entries_d(self, arg=[]):
        for l in self.get_entries(arg):
            yield dict(zip(arg,l))





class WholeFile:
    def __init__(self, fn=''):
        try:
            self.file_name = fn.get_file_name()
        except AttributeError:
            self.file_name = fn

        self.FEPARAMS = {}
        self.STATS = {}
        self.FEATURES = []

    def load(self, fh):
        self.FEPARAMS = self._get_file_part(fh,'FEPARAMS').next()
        self.STATS = self._get_file_part(fh,'STATS').next()

        for v in self._get_file_part(fh, 'FEATURES'):
            self.FEATURES.append(v)
            

    def get_features(self):
        for f in self.FEATURES:
            yield Feature(self.FEPARAMS, self.STATS, f)


    def get_file_part(self, stkw, edkw='//'):
        self.part_delm = edkw

        fh=(open(self.file_name,'r'))

        casttype = []
        header = []
        dat = []

        while fh:
            l = fh.readline()
            if l.startswith(self.part_delm): continue

            arg = l.rstrip().split('\t')
            col1 = arg.pop(0)
            if  col1 == 'TYPE': casttype = arg
            elif col1 == stkw: 
                header = arg
                if not casttype: raise 'No cast type'
                return FilePart(fh, header, casttype, 1)
        raise 'No %s found' % stkw




if __name__ == '__main__':
    import sys

    fn = sys.argv[1]
    fpart = WholeFile(fn).get_file_part('FEATURES')

    print fpart.header
    print fpart.casttype

    kw = ['ProbeName', 'gIsWellAboveBG']


    for arr in fpart.get_entries(kw):
        print arr

