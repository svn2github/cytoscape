#!/usr/bin/env python
import sys
import re
import os
import ucsc
import cPickle

#fineSeq_re = re.compile(r'[atgc]*',re.IGNORECASE)
fineSeq_re = re.compile(r'[atgc]+',re.IGNORECASE)
whites_re = re.compile(r'\W*')


def base_lookup(cls, name, show=False):
    if show:
        print 'cls', repr(cls.__bases__)
        print 'name', repr(name)

##     if str(name) in [str(x) for x in cls.__bases__]:
    if name in cls.__bases__:
        return name
    for base in cls.__bases__:
        try:
            return base_lookup(base, name)
        except AttributeError:
            pass
    raise AttributeError, name


def lookup_file(file):
    for p in sys.path:
        newf = os.path.join(p, file)
        try: 
            if open( newf, 'r' ): return newf
        except IOError: pass
    raise IOError, 'Cannot find %s in sys.path.' % file



def dump(obj, fname, mode=0):
    if fname and obj: 
        cPickle.dump(obj, open(fname,'w'), mode)


def load(fname):
    if fname: return cPickle.load( open(fname,'r') )
        


class SameKeyDifferentData(Exception):
    '''
    '''
##     def __init__(self, key, prev_val, new_val):
##         print >> sys.stderr, '==========================================='
##         com = "Error: Duplicated key '%s' with defferent data can not be added"\
##               % key
##         print >> sys.stderr, com
##         print >> sys.stderr, " New data     : \"%s\"" % new_val
##         print >> sys.stderr, " Previous data: \"%s\"" % prev_val
##         print >> sys.stderr, '==========================================='

class ExistSameDictKey(Exception): pass

class MyError(Exception):pass

class StrError(Exception):
     def __init__(self, v):
          print >> sys.stderr, 'strand must be +/-/plus/minus/(None) but',
          print >> sys.stderr, "\'%s\' was given\n" % v


def cmint(v):
    try: return int(v)
    except ValueError:
        rc = re.compile(r',')
        v = rc.sub('',v)

        return int(v)

def complement(seq):
     '''complement(str) -> str
   This method returns complement sequence of input kstr.
   String will be reversed, and bases, [atcg], will be 
    replaced to complement ones. Any other character will not be replaced.
    (ex. "ncgat" -> "atcgn")'''

     if type(seq) != str: 
         raise TypeError("complement() takes string as argument")

     i = 1
     result = ""
     while(i <= len(seq)):
         c = seq[-i]
         cc = ""
         if c == "a": cc = "t"
         elif c == "t": cc = "a"
         elif c == "g": cc = "c"            
         elif c == "c": cc = "g"                        
         else: cc = c
         result = "%s%s" % (result, cc)
         i+=1
     return result


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




class safedict(dict):
     '''Raise error if dictionary has an identical key with different object.
     Duplications among inputs will be eliminated.'''

     def safe_setitem(self, k, v):
          try:
               if self[k] == v: return 0
               else:
                    prev_val = str(self[k])
                    new_val = str(v)
                    c1 = "Duplicated key '%s' with defferent data:" % k
                    c2 = ' (New) "%s", ' % new_val
                    c3 = ' (Previous) "%s"' % prev_val
                    raise SameKeyDifferentData, '%s%s%s' % (c1, c2, c3)
          except KeyError:
               dict.__setitem__(self, k, v)
               return 1          

     def __setitem__(self, k_inp, v):
         try:
             self.safe_setitem(k_inp, v)
         except TypeError:
             k = k_inp.__str__()
             self.safe_setitem(k, v)




     def __getitem__(self, k):
          try: return dict.__getitem__(self, k )
          except KeyError:
               try: return dict.__getitem__(self, self.strkey(k) )
          ##except TypeError: return dict.__getitem__(self, self.strkey(k) )
               except KeyError: raise KeyError('Invalid key: %s, Keys: %s' % 
                                               ( k, str(self.keys()) ) )
               
     def strkey(self, k_inp):
          k = str(k_inp)
          if not k: raise Exception('Error: no key was passed (safedict).')
          else: return k

class verysafedict(safedict):
     def safe_setitem(self, k, v):
         if k in self: raise ExistSameDictKey('key: %s' % k)
         else: safedict.safe_setitem(self, k, v)


class chkdupld(verysafedict):
    def __init__(self,arg={}):
#        self.checkd = verysafedict()
        self.duplications = {}
        dict.__init__(self, arg)

    def __setitem__(self, k, v):
         try: verysafedict.__setitem__(self, k, v)
         except ExistSameDictKey:
             self.duplications.setdefault( k, [ self[k] ] ).append( v )

    def get_duplicated_keys(self): return self.duplications.iterkeys()
    def get_unique_keys(self):
        return [k for k in self.iterkeys() if not k in self.get_duplicated_keys() ]



class idict(safedict):
    def __setitem__(self, i, y):
         safedict.__setitem__(self, i, int(y) )


class fdict(safedict):
    def __setitem__(self, i, y):
         safedict.__setitem__(self, i, float(y) )


class clist(list):
    def __init__(self, v=[], casttype = []):
        self.casttype = []
        list.__init__(self, v)
        if casttype: self.cast(casttype)



    def cast(self, csttype):
        func = self.get_func_dict()

        self.type_check(csttype)
        for i, v in enumerate(self):
            try:
                k = self.casttype[i]
            except IndexError:
                print len(self), len(self.casttype)
            if type(k) is str: 
                try:
                    newval = func[ k ](v)
                except KeyError:
                    raise KeyError, 'keys: [%s]. input: "%s"' % \
                          ( ','.join(func.keys()), k )
            else: newval = k(v)
            self.__setitem__(i, newval)
            
    def get_func_dict(self):
        return { 'int' : int, 'float': float, 'str': str, 'bool': bool }

    def type_check(self, csttype):
        if type(csttype) is str \
           or csttype in self.get_func_dict().values():
            self.casttype = [csttype]*len(self)

        elif type(csttype) == list:
            if len(self) == len(csttype): self.casttype = csttype
            else:
                raise Exception, \
                      'Different length of casttype(%s) from this list(%s).' % \
                      ( len(csttype), len(self) )
        else:
            raise Exception, 'Second arg must be list. input type: %s' % \
                  type(csttype)            


class ilist(list):
    def __init__(self, v=[], dlm=','):
        if v == []: list.__init__([])
        try: v = v.split(dlm)
        except AttributeError: pass
        try:
            try: self.__init__( int(v) )
            except TypeError: pass
            try:
                for s in v: self.append( int(s) )
            except TypeError: pass
        except ValueError:
            com='Input has no-int value while ilist can only take integers.\n'
            raise ValueError(com)

    def __setitem__(self, i, y):
         list.__setitem__(self, i, int(y) )

    def __getslice__(self, i, j):
         return ilist( list.__getslice__(self, i,j) )



    def append(self, y):
         list.append(self, int(y) )

    def tds(self):
         ret = ''
         for v in self[:-1]:
              ret = '%s%s\t' % (ret, v)
         return '%s%s\n' % (ret, self[-1])


class flist(ilist):
    def __init__(self, v=[], dlm=','):
        if v == []: list.__init__([])
        try: v = v.split(dlm)
        except AttributeError: pass
        try:
            try: self.__init__( float(v) )
            except TypeError: pass
            try:
                for s in v: self.append( float(s) )
            except TypeError: pass
        except ValueError:
            com  ='Input was not convertable to float value'
            com2 = ' while flist can only take float.\n'
                 
            raise ValueError(com+com2)

    def __setitem__(self, i, y):
         list.__setitem__(self, i, float(y) )

    def __getslice__(self, i, j):
         return flist( list.__getslice__(self, i,j) )


    def append(self, y):
         list.append(self, float(y) )


## class nrlist(list):
##      '''Add val to list only when there is no identical value in this list'''
##      def __setitem__(self, i, y):
##           if not y in self: list.__setitem__(self, i, y)

##      def append(self, y):
##           if not y in self: list.append(self, y )


def add_safely( add_to_this, key, obj ):
     '''  Usage: add_safely( dictionary, key, object )
   Raise error if dictionary has an identical key with different object'''

     if type(add_to_this) is not dict:
         err = 'add_safely() expects dict as the first arg.'
         err = "%s (%s was given instead)" % (err, str(type(add_to_this)) )
         raise TypeError(err)
     if add_to_this.has_key(key):
         if add_to_this[key] == obj:
##               print "prev", add_to_this[key]
##               print "new", obj
              return 0
         else:
              prev_val = str(add_to_this[key])
              new_val = str(obj)
              c1 = "Duplicated key '%s' with defferent data:" % key
              c2 = ' (New) "%s", ' % new_val
              c3 = ' (Previous) "%s"' % prev_val
              raise SameKeyDifferentData, '%s%s%s' % (c1, c2, c3)

     else: 
         add_to_this[key] = obj
         return 1

def nrappend(list, val):
     '''  Usage: nrappend(list, val)
   Add val to list only when there is no identical value in list'''
     if not val in list: list.append(val)




def upstream_and_downstream_of_start(region_class_iterator, up=6000, 
                                      dn=2450, outf=""):
     for p in region_class_iterator:
         strand = p.get_strand() 
         if strand == '+':
             s = p.get_start() - up
             e = p.get_start() + dn
         elif strand == '-':
             s = p.get_end() - dn
             e = p.get_end() + up
         else: raise Exception(strand)
         p.set_start(s)
         p.set_end(e)




class VeryBasic:
    def __init__(self,id=''):
        self.ID = id
        self.parent = None

##     def __str__(self):
##         return self.__repr__()

    def __eq__(self, other):
         if self.__repr__() == other.__repr__(): return True
         else: return False
            
    def __ne__(self, other):
        if self == other: return False
        else: return True

    def get_ID(self): return self.ID
    def get_parent(self): return self.parent

    def set_ID(self, v): self.ID = v
    def set_parent(self,v):
        if not self.parent: self.parent = v
        else: raise Exception('self.parent is already set as: %s' % (self.parent) )
    def base_lookup(self, name, show=False):
        try: return basic.base_lookup(self, name)
        except AttributeError: return False


class simpled(dict):
    def __init__(self, f, k_col=0, v_col=1, dlm = '\t'):
        dict.__init__(self)
##         self.dlm = dlm
##         self.k_col = k_col
##         self.v_col = v_col

        n = 0
        for l in open(f,'r'):#.xreadlines():
            if l.startswith('#'): continue

            n+=1
            l = l.rstrip('\n\r')
            arr = l.split(dlm)            
            if v_col < 0: self.__setitem__( arr[k_col], arr )
            else: self.__setitem__( arr[k_col], arr[v_col] )
        if len(self) != n: 
            print 'Duplicated keys may exist (overwritten)'

    def in_format(self):
        s = ''
        for k, v in self.itervalues(): s = '%s%s\t%s\n' % (s,k,v)
        return s
            


                
class multid(simpled):
    def __init__(self, f, k_col=0, v_col=1, dlm = '\t'):
        dict.__init__(self)

        for l in open(f,'r').xreadlines():
            l = l.rstrip('\n')
            arr = l.split(dlm)            
            val = arr[v_col]
            key = arr[k_col]
            self.setdefault( key, [val] ).append(val)


class SimpleD:
     def __init__(self,f, kf=0, vf=1, dlm = "\t"):
         if type(f) is str:
               print >> sys.stderr, "dlm: \'%s\'\n" % dlm
               self.d = {}
               fh = open(f,'r')
               for l in fh.xreadlines():
                    arr = l.rstrip("\n").split(dlm)
                    self.d[ arr[kf] ] = arr[vf]
         elif type(f) is dict:
               self.d = f
         else: raise Exception
     def get(self, k): return self.d[ k ]
     def dict(self): return self.d
     def iterkeys(self): return self.d.iterkeys()
     def itervalues(self): return self.d.itervalues()


class MultiD(SimpleD):
     def __init__(self,f, kf=0, vf=1, dlm = "\t"):
         if type(f) is str:
               print >> sys.stderr, "dlm: \'%s\'\n" % dlm
               self.d = {}
               fh = open(f,'r')
               for l in fh.xreadlines():
                    arr = l.rstrip("\n").split(dlm)
                    self.d.setdefault( arr[kf], [ arr[vf] ] ).append(arr[vf])
         elif type(f) is dict:
               self.d = f
         else: raise Exception



class BaseContainer(VeryBasic):
     def __init__(self, fn = '', sp=''):
          VeryBasic.__init__(self, fn)
          self.file_name = fn
          #self.set_ID(fn)
          self.species = sp

          self.ID_data = safedict()
          self.all_data = []
          self.header_str = ''

          if not self.species:
               try: 
                    spe = self.suspect_species()
                    self.set_species(spe)
               except: pass
     
          if self.file_name: self.load()
              

     def __str__(self):
          return "%s with %d data" % ( self.__class__, self.get_num() )

     def suspect_species(self):
          file = os.basename( self.get_file_name() )
          spekw = file[0:2]
          kw2sp = { 'mm': 'mouse', 'hg': 'human', 'M': 'mouse', 'H': 'human' }
          try:
               print >> sys.stderr, '# (BaseContainer) Suspected species:', kw2sp[ spekw ]
               return kw2sp[ spekw ]

          except KeyError:
               raise Exception('Can not suspect species from filename.\n')


     def load(self):
         fname = self.get_file_name()
         fh = open( fname, 'r')
         sys.stderr.write("# loading: %s\n" % fname )
         self.header_check(fh)
         for l in fh.xreadlines():
             if l.startswith('#') : continue
             self.add( self.get_entry(l.rstrip('\n')) )
         fh.close()
         sys.stderr.write("# loaded: %s\n" % fname )


     def get_entry(self, l):
         arr = l.split("\t")
         return BaseRegion(arr)

     def header_skip(self): return 0

     def header_check(self,fh):
         i = 0
         while( i < self.header_skip() ):
             self.header_str += fh.readline()
             i+=1

     def add(self, v):
##         v.set_parent(self)

         # All data as listed
         self.all_data.append(v)

         # ID -> data
         id = v.get_ID()
         if not id: id = "key%d"  % ( len(self.ID_data)+1 )
         self.ID_data[ id ] = v

     def set_species(self, v): self.species = v
     def get_species(self): return self.species
     def set_file_name(self, v): self.file_name = v
     def get_file_name(self): return self.file_name     
     def get_region_IDs(self): return self.ID_data.keys()
     def get_num(self): return len(self.all_data)

     def get_region(self, v=None):
         if not v: return self.get_regions()
         return self.ID_data[v]

     def get_regions(self, opt=0): 
          if opt: return self.ID_data.values()
          else: return self.ID_data.itervalues()

#          try: return self.ID_data.itervalues()
#          except: 
#               print >> sys.stderr, \
#                     "You may use MyContainer class successfully\n"
#               sys.exit(1)

               ##return self.ID_data.itervalues()

     def ID_search(self, targetds):
          match = []
          unmatch = []
          for p in self.get_regions():
               id = p.get_ID()
               try: 
                    if type(targetds) is dict: m = targetds[ id ]
                    else: m = targetds.get_region( id )
                    match.append( p )
               except KeyError:
                    unmatch.append( p )

          return (match, unmatch)



class MyContainer(BaseContainer):

     def add(self, v):
          k = ''
          try:
               k = v.get_ID()
          except AttributeError:
               k = v
               v = 1
          try: add_safely( self.ID_data, k, v )
          except SameKeyDifferentData:
               dict_val = self.ID_data[ k ]
               try: dict_val.append( v )
               except AttributeError:
                    tmp = dict_val
                    dict_val = []
                    dict_val.append(tmp)
                    dict_val.append(v)
                    self.ID_data[k] = dict_val

     def get_regions(self): 
          test = self.ID_data.itervalues().next()
          if  test == 1:
               for k in  self.ID_data.iterkeys():
                    yield k
          else:
               for can_be_array in self.ID_data.itervalues():
                    try:
                         for v in can_be_array: yield v
                    except: 
                         yield can_be_array
                         



class BaseDataSet(BaseContainer):
     def __init__(self, fn = '', sp = ''):
          self.chromosome = safedict()
          self.non_redandant_sequences = safedict()
          BaseContainer.__init__(self, fn, sp)


     def out(self, fh = sys.stdout):
         for i in self.get_regions(): print >> fh, i
             

     def add(self, v):
          # Check
          self.check_input(v)
          BaseContainer.add(self,v)

          # By chromosome
          chrv = v.get_chromosome()
          self.chromosome.setdefault( chrv, Chromosome(chrv, self) ).add(v)

     def check_input(self, v): pass

     def get_chromosomes(self): return self.chromosome.itervalues()
     def get_chromosome(self, chr):
         try:
             return self.chromosome[chr]
         except KeyError:
              err = sys.stderr, 'Invalid key: [%s]. Valid keys are: %s\n'\
                    % ( chr, self.chromosome.keys() )
              raise KeyError(err)

     def get_chr_ID(self): #return self.chromosome.keys()
         arr = []
         arrs = []
         for v in self.chromosome.keys():
             try:
                 arr.append( int(v[3:]) )
             except:
                 arrs.append(v)

         arr.sort()
         for i, v in enumerate(arr):
                 arr[i] = "chr%d" % v
         arrs.sort()
         arr += arrs
         return arr


     def get_regions(self):
         for k in self.get_chr_ID():
             c = self.chromosome[k]
             for p in c.generator():
                 yield p

     def add_sequences(self, seq, region):
         #print "add_sequences"
         try: 
             before = self.non_redandant_sequences[ seq ]
             region.set_duplseq_status( True )

             if before: before.set_duplseq_status( True )
             else: self.non_redandant_sequences[ seq ] = ''
             
         except KeyError:
             self.non_redandant_sequences[ seq ] = region
             region.set_duplseq_status( False )


     def set_sequences(self, directory_path):
         for chr in self.get_chromosomes(): chr.set_sequences(directory_path)

     def get_duplicated_sequences(self):
         dd = chkdupld()
         for r in self.get_regions():
             dd[ r.get_sequence() ] = 1
         return dd.get_duplicated_keys()

#     def get_region(self, v):
#          if self.ID_num[ v ] == 1:
#               return self.ID_data[ v ]
#          else:
#               ret = []
#               for chr in self.get_chromosomes():
#                    region = chr.get_region(v)
#                    if region: ret.append(region)
#               return ret
#     def get_num(self):
#          total = 0
#          for chr in self.chromosome.itervalues():
#               total += chr.get_num()
#          return total


class Chromosome(BaseContainer):
     def __init__(self, id, ds=''):
         BaseContainer.__init__(self)

         self.set_ID(id)

         self.no_redandant_pos = verysafedict()
         self.duplicated_pos = safedict()
         self.ID_data = safedict()
         self.dataset = ds

     def __repr__(self):
         return '%s, with %d sequence data.\n' % \
                (self.ID, len(self.ID_data))

     def set_sequences(self, ucscpath):
         file = os.path.join(ucscpath, '%s.fa' % self.get_ID())
         if not os.path.isfile(file): raise Exception('Cannot open file%s' % file)
         title, seq = ucsc.load_UCSC_file( file )
         
         for region in self.get_regions():
             region_seq = seq[ region.get_start()-1 : region.get_end() ]
             if   region.get_strand() == '+': pass
             elif region.get_strand() == '-': region_seq = complement( region_seq )
             else: raise StrError
             region.set_sequence(region_seq)
##             self.dataset.add_sequences(region_seq, region)
                 


     def add(self, seq_data):
          BaseContainer.add(self, seq_data)
          
          # Focus on the physical loacation of regions
          #  taking it as ID of the data-storing-dict of this class
          physical_pos = seq_data.get_loc_string('with_strand')
          try:
              first = self.no_redandant_pos[ physical_pos ]
          except KeyError:
              self.no_redandant_pos[ physical_pos ] = seq_data
          else:
              self.duplicated_pos.setdefault( physical_pos, [ first ] ).append(seq_data)

     def generator(self): return self.get_regions()

     def get_no_redandant(self, opt=0):
         if opt: return self.no_redandant_pos.itervalues()
         else: return self.no_redandant_pos.keys()
     def get_no_redandant_num(self): 
          return len(self.no_redandant_pos.values())
     def get_duplications(self, val=0):
         if val: return self.duplicated_pos.keys()
         else: return self.duplicated_pos.itervalues()
     def get_duplicated_num(self): return len(self.duplicated_pos)



class Strand:
    def __init__(self, v):
        rplus = re.compile(r'plus|p|\+|forward|f', re.IGNORECASE)
        rminus = re.compile(r'minus|m|\-|reverse|r', re.IGNORECASE)

        if v == '': self.strand = v
        elif rplus.match(v): self.strand = '+'
        elif rminus.match(v): self.strand = '-'
        else: raise StrError(v)

    def __repr__(self):
        return self.strand
    def __str__(self):
        return self.strand
    def __eq__(self, v):
        if self.strand == '':
            return v == self.strand
        elif self.strand == '+':
            if v == 'plus' or v =='+' or v == 'Plus': return True
            elif v == 'minus' or v == '-' or v == 'Minus': return False
            else: raise StrError(v)
        elif self.strand == '-':
            if v == 'minus' or v == '-' or v == 'Minus': return True
            elif v == 'plus' or v =='+' or v == 'Plus': return False
            else: raise StrError(v)
        else: raise StrError(v)

    def reverse(self):
         if self.strand == '+': return Strand('-')
         else: return Strand('+')

    def __ne__(self, v):
        return not self.__eq__(v)




class BaseRegion(VeryBasic):
     def __init__(self, arg=''):
         VeryBasic.__init__(self)
         self.chromosome = ''
         self.start = None
         self.end = None
         self.strand = ''
         self.ID = ''
         self.sequence = ''
         self.seq_duplication_status = None

#         self.simple_init()

         if type(arg) == str: self.set_ID(arg)
         elif type(arg) == list: self.take_list_arg(arg)
         elif arg.__class__ is self.__class__: 
              self.cpy(arg)
              print "%s" % ( self.__class__), "==", arg.__class__
##              print str(v) == 'basic.BaseRegion'
         else:
              try:
                  if base_lookup(arg.__class__, self.__class__): self.cpy(arg)
## arg.has_this_as_base(): self.cpy(arg)
              except:
                  raise TypeError(arg.__class__, self.__class__)
                   


     def in_format(self):
         return "%s\t%s\t%d\t%d\t%s" % ( self.get_ID(), self.get_chromosome(),
                                         self.get_start(), self.get_end(),
                                         self.get_strand())

##      def __repr__(self):
##          ret = "%s\t%s\t%s\t%s\t%s" % ( self.get_ID(), self.get_chromosome(),
##                                         self.get_start(), self.get_end(),
##                                         self.get_strand())
          ##print "repr of baseregion:", ret
         return ret
                                 

     def take_list_arg(self,arg):
         try: 
              self.set_chromosome( arg.pop(0) )
              self.set_start( arg.pop(0) )
              self.set_end( arg.pop(0) )
              self.set_strand( arg.pop(0) )
              self.set_ID( arg.pop(0) )
         except IndexError: pass

     def has_this_as_base(self, v):
          for v in arg.__class__.__bases__:
               if v is self.__class__: return True
          return False

     def cpy(self, arg):
          self.set_ID( arg.get_ID() )        
          self.set_chromosome( arg.get_chromosome() )
          self.set_start( arg.get_start() )
          self.set_end( arg.get_end() )
          ###print arg.get_strand() ####
          self.set_strand( arg.get_strand() )

     def simple_init(self):
         self.chromosome = ''
         self.start = ''
         self.end = ''
         self.strand = ''
         self.ID = ''

     def set_sequence(self, v): 
        if not v:
            self.sequence = ''
            return 0
            
        m = fineSeq_re.match( v )
        if m:
            if m.group() == v:
                self.sequence = v.upper()
                return 0

        raise MyError("Invalid char for seq was found in %s: \'%s\'" % 
                      (self.get_ID(), v) )

                            
     def get_sequence(self):return self.sequence

     def set_duplseq_status(self, v): self.seq_duplication_status = v
     def is_duplicated_seq(self): return self.seq_duplication_status

         

##      def set_ID(self, v): self.ID = v
     def set_chromosome(self, v): 
        try: self.chromosome = int(v)
        except ValueError:
            if v[0:3] == 'chr' or v[0:3] == 'Chr':
                try:
                    self.chromosome = int(v[3:])
                except:
                    self.chromosome = v[3]
     def set_start(self, v): 
          #print 'set_start', v, 
          self.start = cmint(v)
          #print '->', self.start

     def set_end(self, v): self.end = cmint(v)
     def set_strand(self, v): 
          if v.__class__ is Strand:
               self.strand = v
          else:
               self.strand = Strand(v)


##      def get_ID(self): return self.ID
     def get_chromosome(self): 
        try: return "chr%d" % self.chromosome
        except: return "chr%s" % self.chromosome
     def get_chr_num(self): return self.chromosome
     def get_start(self): return self.start
     def get_end(self): return self.end
     def get_strand(self): return self.strand
     def get_upper_pos(self):
          if self.get_start() < self.get_end(): return self.get_start()
          else: return self.get_end()
     def get_downner_pos(self):
          if self.get_start() > self.get_end(): return self.get_start()
          else: return self.get_end()          
     def include(self, p):
##           if self.get_ID() == 'Not1_4995':
##                print self
##                print self.get_start() , p.get_start() , p.get_end() , 
##                print self.get_end()
##                print (self.get_upper_pos() <= p.get_upper_pos() \
##                       <= p.get_downner_pos() <= self.get_downner_pos())

          if self.get_upper_pos() <= p.get_upper_pos() \
                 <= p.get_downner_pos() <= self.get_downner_pos():
               return True
          else: return False

     def get_loc_string(self, with_strand_info = False): 
         if with_strand_info:
             return "%s:%s-%s:%s" % (self.get_chromosome(), self.get_start(), self.get_end(), str(self.get_strand()) )
         else: return "%s:%s-%s" % (self.get_chromosome(), self.get_start(), self.get_end() )


class ZeroStart:
    def set_start(self, v): self.start = int(v)+1
    def set_end(self, v): self.end = int(v)+1

##     def in_format(self):
##         return "%s\t%s\t%d\t%d\t%s" % ( self.get_ID(), self.get_chromosome(),
##                                         self.get_start()-1, self.get_end()-1,
##                                         self.get_strand())

class ZeroRegion(ZeroStart, BaseRegion): pass
    


class IDdict(dict):
    def __init__(self, v={}, idhead=None):
        dict.__init__(self, v)
        self.header_of_ID = idhead
    def set_ID_header(self, v): self.header_of_ID = v
    def get_ID(self): return self[self.header_of_ID]



if __name__ == "__main__":
    print "This is basic.__main__"
    ds = BaseDataSet()
    ds.add(BaseRegion(['chr1',100,200,'-']))
    ds.add(BaseRegion(['chr1',300,350,'-']))
    ds.add(BaseRegion(['chr2',400,5000,'-']))
    ds.add(BaseRegion(['chr1',100,200,'-']))

    print ds



