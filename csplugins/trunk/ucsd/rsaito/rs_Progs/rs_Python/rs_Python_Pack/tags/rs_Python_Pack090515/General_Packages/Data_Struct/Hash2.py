#!/usr/bin/python

import sys
from random import randint

class Hash:
    """ Reads tab-delimited file and make dictionary using items
    specified columns as key and another specified column as value.
    Basically, keys and values are tab-delimited and they are the main
    inputs and outputs to the member functions respectively.
    """

    def __init__(self, val_type):
        self._set_data("", "", initialize = True)
        self.val_type = val_type
        self.verbose = False
        self.filts = []
        self.filts_OR_dict = {}
        self.fh = None
        self.miscolumn_permit = False
        self.auto_fill = False

    # Only the following 3 functions performs the direct manipulation of "h".
    def _set_data(self, key, val, initialize = False):
        if initialize:
            self.__h = {}
        else:
            self.__h[ key ] = val

    def _push_data(self, key, val): # Redundancies of "val"s are allowed.
        if self.has_key(key):
            self.__h[ key ].append(val)
        else:
            self.__h[ key ] = [ val ]
            
    def __getattr__(self, attrname):
        return getattr(self.__h, attrname)

    ### Only the following 8 functions look "h" directly. ###
    def val(self, *keylist):
        """ Each element can be tab-delimited before
        calling this function, i.e., self.val("a", "b") and
        self.val("a\tb") produce the same outputs """
        key = "\t".join(keylist)
        return self.__h[ key ]

    def val_force(self, *keylist):
        """ Each element can be tab-delimited before
        calling this function, i.e., self.val("a", "b") and
        self.val("a\tb") produce the same outputs """
        key = "\t".join(keylist)
        return self.__h.get(key, "")

    def keys(self):
        return self.__h.keys()

    def has_key(self, *keylist):
        """ Each element can be tab-delimited before
        calling this function, i.e., self.val("a", "b") and
        self.val("a\tb") produce the same outputs """
        key = "\t".join(keylist)
        return self.__h.has_key(key)

    def set_miscolumn_permit(self):
        self.miscolumn_permit = True

    def set_auto_fill(self, cols):
        self.auto_fill = cols

    def all_data(self):
        return self.__h

    def val_raw(self, key):
        return self.__h[ key ]

    def val_raw_force(self, key):
        return self.__h.get(key, "")

    def has_key_raw(self, key):
        return self.__h.has_key(key)

#########################################################

    def __getitem__(self, key):
        return self.val(key)

    def __iter__(self):
        return self.keys().__iter__()

    def __len__(self):
        return len(self.keys())

    def val_list(self, *key_list):
        key = "\t".join(key_list)
        return self.val(key).split("\t")

    def vals(self, *keys):
        values = []
        for k in keys:
            values.append(self.val(k))
        return values
    
    def get_all_vals(self):
        ret = []
        for k in self.keys():
            ret.append(self.val_force(k))
        return ret

    def has_pair(self, key1, key2):
        if self.has_key(key1, key2):
            return key1 + "\t" + key2
        elif self.has_key(key2, key1):
            return key2 + "\t" + key1
        else:
            return False

    def pair_val(self, key1, key2):
        if self.has_key(key1, key2):
            return self.val(key1, key2)
        elif self.has_key(key2, key1):
            return self.val(key2, key1)
        else:
            return False

    def filt_key(self, *filters):
        """ Gets keys having certain key patterns """
        filt_keys = []
        for key in self.keys():
            hit = 1
            for filter in filters:
                [ col, pat ] = filter
                r = key.split("\t")
                if col >= len(r) or r[col] <> pat:
                    hit = 0
                    break
            if hit == 1:
                filt_keys.append(key)
        return filt_keys

    def get_val_type(self):
        return self.val_type

    def verbose_mode(self):
        self.verbose = True

    def pre_read_file(self):
        pass

    def recorder(self, Key_cols, Val_cols):
        for line in self.fh.readlines():
            if line[0] == "#": continue
            linec = line[:len(line)-1]
            """ string.rstrip(line) may also cut last tabs """

            r = linec.split("\t")

            if self.filt_line(r): continue
            # print linec, r

            self.record(r, Key_cols, Val_cols)


    def record(self, r, Key_cols, Val_cols):

        if self.verbose: print "Reading line ---", r
        
        if self.auto_fill and '_record_prev_r' in vars(self):
            tmp_r = []
            for i in range(max(len(r), len(self._record_prev_r))):
                if ((i >= len(r) or r[i].isspace() or r[i] == "")
                    and i in self.auto_fill):
                    tmp_r.append(self._record_prev_r[i])
                elif i >= len(r):
                    tmp_r.append("")
                else:
                    tmp_r.append(r[i])
            r = tmp_r
        self._record_prev_r = r           
        
        keys = []
        for col in Key_cols:
            if col < len(r):
                keys.append(self.conv_key(r[col]))
            elif self.miscolumn_permit:
                keys.append(self.conv_key(""))
            else:
                raise "Key column missing ..."
                       
        key = "\t".join(keys)    
        
        vals = []
        for col in Val_cols:
            if col < len(r):
                vals.append(self.conv_val(r[col]))
            elif self.miscolumn_permit:
                vals.append(self.conv_val(""))
            else:
                raise "Val column missing ..."                
          
        val = "\t".join(vals)  
           
        if self.get_val_type() == "S": # Scalar
            self._set_data(key, val)
        elif self.get_val_type() == "A": # Array
            self._push_data(key, val)
        elif self.get_val_type() == "L": # Scalar, row as value
            self._set_data(key, "\t".join(r[Val_cols[0]:]))
        elif self.get_val_type() == "N": # Scalar, null
            self._set_data(key, "")
        else:
            raise "Illegal option", self.get_val_type()


    def conv_key(self, k):
        return k
    
    def conv_val(self, v):
        return v

    def read_file(self, filename, Key_cols = [0], Val_cols = [1],
                  Fil_cols = None):

        self.fh = open(filename, 'r')

        if Fil_cols is not None:
            self.set_auto_fill(Fil_cols)

        self.pre_read_file()
        self.recorder(Key_cols, Val_cols)

        self.fh.close()
        self.fh = None

    def add_hash(self, hash):
        if self.get_val_type() <> hash.get_val_type():
            raise "Type mismatch", (self.get_val_type(),
                                    hash.get_val_type())
        count = 0
        for key in hash.keys():
            self._set_data(key, hash.val(key))
            if self.verbose:
                print "Reading key #" + `count`, key, hash.val(key)
            count = count + 1

    def reverse_Hash(self, hash):

        count = 0
        for key in hash.keys():
            val = hash[key]
            if self.verbose:
                print "Reading key #" + `count`, key, hash.val(key)
            if self.get_val_type() == "S": # Scalar
                self._set_data(val, key)
            elif self.get_val_type() == "A": # Array
                self._push_data(val, key)
            elif self.get_val_type() == "N": # Scalar, null
                self._set_data(val, "")
            else:
                raise "Illegal option " + self.get_val_type()
            count = count + 1

    def ret_reversed_Hash(self, ret_val_type):
        """ Only value types "S" and "A" are supported. """

        ret_hash = Hash(ret_val_type)
        for k in self.keys():
            v = self.val(k)
            if self.get_val_type() == "S":
                if ret_val_type == "S":
                    ret_hash._set_data(v, k)
                elif ret_val_type == "A":
                    ret_hash._push_data(v, k)
                else:
                    raise "Return value type error..."
                
            elif self.get_val_type() == "A":
                for ev in v:
                    if ret_val_type == "S":
                        ret_hash._set_data(ev, k)
                    elif ret_val_type == "A":
                        ret_hash._push_data(ev, k)
                    else:
                        raise "Return value type error..."
            else:
                raise "Source value type error..."

        return ret_hash


    #
    # From here, methods for headers are defined.
    #

    def header_key(self):
        return self.hd_key

    def header_val(self):
        return self.hd_val

    def val_accord_hd(self, key, val_term):
        """ self.header_val() = [ "Sho", "Chu", "Dai" ],
        Small --> "Medium\tLarge"
        Few   --> "Medium\tMany"
        Never --> "Often\tAlways"

        self.val_accord_hd("Few", "Dai") --> "Many"
        """

        col_val_hd = self.header_val().index(val_term)
        val = self.val_force(key)
        if val:
            if self.get_val_type() == "S":
                return val.split("\t")[ col_val_hd ]
            elif self.get_val_type() == "A":
                ret = []
                for v in val:
                    ret.append(v.split("\t")[ col_val_hd ])
                return ret
            else:
                raise "Illegal type:", self.get_val_type()
        else:
            return None
        
    def get_all_vals_accord_hd(self, val_term):
        ret = []
        for k in self.keys():
            ret.append(self.val_accord_hd(k, val_term))
        return ret

    def read_file_hd(self, filename, Key_cols_hd, Val_cols_hd,
                     Fil_cols_hd = None):

        self.hd_key = Key_cols_hd
        self.hd_val = Val_cols_hd

        self.fh = open(filename, 'r')
        self.pre_read_file()

        header_line = self.fh.readline()
        header_c = header_line.rstrip()
        header = header_c.split("\t");

        Key_cols = []
        for Kch in Key_cols_hd:
            Key_cols.append(header.index(Kch))
        Val_cols = []
        for Vch in Val_cols_hd:
            Val_cols.append(header.index(Vch))
            
        Fil_cols = []
        if Fil_cols_hd is not None:
            for Fch in Fil_cols_hd:
                Fil_cols.append(header.index(Fch))
            self.set_auto_fill(Fil_cols)
                
        self.recorder(Key_cols, Val_cols)

        self.fh.close()
        self.fh = None

    #
    # From here, methods for filters are defined.
    #

    def set_filt(self, *filts):
        self.filts = filts

    def set_filt_OR(self, *filts):
        for filt in filts:
            [ col, filt_key ] = filt
            if not (col in self.filts_OR_dict):
                self.filts_OR_dict[ col ] = {}
            self.filts_OR_dict[ col ][ filt_key ] = True


    def filt_line(self, r):

        filter = False
        for filt in self.filts:
            [ col, filt_key ] = filt
            if col < len(r) and r[col] <> filt_key:
                """ If any one of the condition is NOT met,
                filter is activated. """
                filter = True
                break

        if not filter and self.filts_OR_dict != {}:
            filter = True
            for col in range(0,len(r)):
                val = r[col]
                if (col in self.filts_OR_dict and
                    val in self.filts_OR_dict[col]):
                    """ If any one of the condition is MET,
                    filter is inactivated. """
                    filter = False
                    break

        return filter

    def shuffle(self, iteration_scale = 10):
        h = {}
        klist = self.keys()
        for k in self:
            v = self[k]
            h[k] = v
        if self.verbose:
            print len(self) * iteration_scale, "times shuffling ..."
        for i in range(len(self) * iteration_scale):
            k1 = klist[randint(0, len(self)-1)]
            k2 = klist[randint(0, len(self)-1)]
            v1 = h[k1]
            v2 = h[k2]
            (h[k1], h[k2]) = (h[k2], h[k1])
        self._set_data(None, None, initialize = True)
        for k in h:
            self._set_data(k, h[k])
            

if __name__ == "__main__":
    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

Rintaro   Saito      SFC   male
Takayuki  Saito      SFC   male
Yuji      Kagayama   Law   male
Hiroshi   Mochizuki  Mer   male
Masaru    Mitsuhashi Law   male
Yoshihisa Wada       Eco   male
Sotaro    Yamazaki   Eco   male
Nobuo     Saito      SFC   male
Mayumi    Hasegawa   SFC   female
Miki      Matsumura  SFC   female
Ryosuke   Suzuki     SFC   male
Takashi   Suzuki     SFC   male
Dosan     Saito      His   male

""")

    print "Temporary file:"
    print tmp_obj.filename()

    print
    print "*** Hash Class ***"

    h_test = Hash("S")
#    h_test.verbose_mode()
    h_test.read_file(filename = tmp_obj.filename(),
                     Key_cols = [0,1],
                     Val_cols = [2])
    print "All data:"
    print h_test.all_data()
    print "All vals:", h_test.get_all_vals()
    print "Keys:"
    print h_test.keys()
    print h_test.has_key("Rintaro", "Saito")
    print h_test.has_pair("Saito", "Rintaro")
    print h_test.pair_val("Rintaro", "Saito")
    print h_test.filt_key([1, "Saito"])
    
    print "For loop:"
    for k in h_test:
        print k, h_test[k]
    
    print

    print "All data reversed."
    h_test_reverse = h_test.ret_reversed_Hash("S")
    print h_test_reverse.all_data()
    h_test_reverse = h_test.ret_reversed_Hash("A")
    print h_test_reverse.all_data()

    print "Stage 2:"
    h_test = Hash("A")
    h_test.read_file(filename = tmp_obj.filename(),
                     Key_cols = [1],
                     Val_cols = [2])
    print h_test.all_data()
    print "All data reversed."
    h_test_reverse = h_test.ret_reversed_Hash("S")
    print h_test_reverse.all_data()
    h_test_reverse = h_test.ret_reversed_Hash("A")
    print h_test_reverse.all_data()    

    print
    print "*** Hash_filt Class ***"

    h_test2 = Hash("S")
    h_test2.set_filt([2, "SFC"])
    h_test2.set_filt_OR([1, "Saito"], [1, "Suzuki"])
    h_test2.verbose_mode()
    h_test2.read_file(filename = tmp_obj.filename(),
                      Key_cols = [0],
                      Val_cols = [1])
    print h_test2.all_data()

    print
    print "*** Hash_headf Class ***"

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

Given     Family     Faculty Gender

Rintaro   Saito      SFC      male
Takayuki  Saito      SFC      male
Yuji      Kagayama   Law      male
Hiroshi   Mochizuki  Mer      male
Masaru    Mitsuhashi Law      male
Yoshihisa Wada       Eco      male
Sotaro    Yamazaki   Eco      male
Nobuo     Saito      SFC      male
Mayumi    Hasegawa   SFC      female
Miki      Matsumura  SFC      female
Ryosuke   Suzuki     SFC      male
Takashi   Suzuki     SFC      male
Dosan     Saito      His      male

""")

    hhf = Hash("A")
    hhf.verbose_mode()
    hhf.read_file_hd(filename = tmp_obj2.filename(),
                     Key_cols_hd = ["Faculty"],
                     Val_cols_hd = ["Given", "Family"])
    print hhf.keys()
    print hhf.val_accord_hd("Law", "Given")
    print hhf.val_accord_hd("Law", "Family")
    print "All vals:", hhf.get_all_vals_accord_hd("Family")
    print

    disney_resort = Usefuls.TmpFile.TmpFile_III("""
Given     Family     Place   Date
Satomi    Nakazawa   Land    1995.3
Tomoko    Yamagishi  Land    1998.4
Reiko     Tomita     Sea     2006.9
Akiko     Sato       Land    2007.12
""")
    disney_with = Hash("A")
    disney_with.read_file_hd(filename = disney_resort.filename(),
                             Key_cols_hd = [ "Place" ],
                             Val_cols_hd = [ "Given" ])
    print disney_with.val_accord_hd("Land", "Given")
    print


    niagara_curry = Usefuls.TmpFile.TmpFile_III("""
Given     Family     Date
Tomoko    Yamagishi  1998.8
Akiko     Sato       2006.1
Akiko     Shiozawa   2006.3
Ayako     Kazamaki   2008.1
""")
    niagara_with = Hash("A")
    niagara_with.read_file_hd(filename = niagara_curry.filename(),
                              Key_cols_hd = [ "Family" ],
                              Val_cols_hd = [ "Given" ])
    print niagara_with.val_accord_hd("Kazamaki", "Given")
    print

    class Hash_skip(Hash):
        def pre_read_file(self):
            while True:
                line = self.fh.readline()
                if line[0:4] != "Skip":
                    break
                print "Skipping ..."
            self.fh.seek(-len(line), 1)

    print "*** Hash_skip Class ***"

    tmp_obj3 = Usefuls.TmpFile.TmpFile_III("""

Skip #1
Skip #2
Skip #3
Given     Family     Faculty Gender

Rintaro   Saito      SFC      male
Takayuki  Saito      SFC      male
Yuji      Kagayama   Law      male
Hiroshi   Mochizuki  Mer      male
Masaru    Mitsuhashi Law      male
Yoshihisa Wada       Eco      male
Sotaro    Yamazaki   Eco      male
Nobuo     Saito      SFC      male
Mayumi    Hasegawa   SFC      female
Miki      Matsumura  SFC      female
Ryosuke   Suzuki     SFC      male
Takashi   Suzuki     SFC      male
Dosan     Saito      His      male

""")

    hhf = Hash_skip("A")
    hhf.verbose_mode()
    hhf.read_file_hd(filename = tmp_obj3.filename(),
                     Key_cols_hd = ["Faculty"],
                     Val_cols_hd = ["Given", "Family"])
    print hhf.keys()
    print hhf.val_accord_hd("Law", "Given")
    print hhf.val_accord_hd("Law", "Family")

    tmp_obj4 = Usefuls.TmpFile.TmpFile_III("""
    
Apple   Ringo
Bell    Suzu
Cherry  Sakurambo
Dinner  Yushoku
East    Higashi

""")
    
    h_simple = Hash("S")
    h_simple.read_file(filename = tmp_obj4.filename(),
                       Key_cols = [0],
                       Val_cols = [1])
    h_simple.verbose_mode()
    h_simple.shuffle()
    print h_simple.all_data()
    print
    
    tmp_obj5 = Usefuls.TmpFile.TmpFile_III("""
    
A   apple
A   air
A   arc
B   banana
B   basket
C   candy
C   Canada
C   climate
D   dry
D   dinner
D   dream
E   eagle
""")
    
    h_simple = Hash("A")
    h_simple.read_file(filename = tmp_obj5.filename(),
                       Key_cols = [0],
                       Val_cols = [1])
    h_simple.verbose_mode()
    h_simple.shuffle()
    print h_simple.all_data()
    print
    h_simple_rev = h_simple.ret_reversed_Hash("S")
    print h_simple_rev.all_data()
    
    tmp_obj6 = Usefuls.TmpFile.TmpFile_III("""

No Word      Head
1  apple     
2  air
3  arc
4  banana    B
5  basket
6  candy     C
7  Canada
8  climate
9  dry       D
10 dinner
11 dream
12 eagle     E
""")
    h_simple = Hash("A")
    h_simple.set_miscolumn_permit()
    h_simple.read_file_hd(filename = tmp_obj6.filename(),
                          Key_cols_hd = [ "Head" ],
                          Val_cols_hd = [ "Word" ],
                          Fil_cols_hd = [ "Head" ])
    print h_simple.all_data()
    
    print "......"
    
    tmp_obj7 = Usefuls.TmpFile.TmpFile_II("""
No\tWord\tHead
1\tapple
\tair
\tarc\tA
4\tbanana\tB
5\tbasket\t
6\tcandy\tC
\tCanada\t""", trim_f_line_flag = True)
    h_simple = Hash("A")
    h_simple.set_miscolumn_permit()
    h_simple.read_file_hd(filename = tmp_obj7.filename(),
                          Key_cols_hd = [ "No" ],
                          Val_cols_hd = [ "Word", "Head" ],
                          Fil_cols_hd = [ "No" ])
    #for k in h_simple:
    #    print k, h_simple[k]
    print h_simple.all_data()




    