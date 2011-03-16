#!/usr/bin/env python

import Expr_Packages.Expr.Express1 as Express
import Data_Struct.Data_Sheet2
import Obj_Oriented.Singleton

class Protein:
    def __init__(self, protein_name):
        self.protein_name = protein_name
        self.data = {}
        self.iexp = None

    def get_protein_name(self):
        return self.protein_name

    def set_expression(self, iexp):
        self.iexp = iexp

    def get_expression(self):
        return self.iexp

    def set_value(self, tag, val):
        self.data[tag] = val

    def get_value(self, tag):
        return self.data.get(tag)


class Protein_Factory(Obj_Oriented.Singleton.Singleton3):
    def __init__(self):
        if 'name2obj' not in vars(self):
            self.name2obj = {}

    def make(self, iname):
        if iname not in self.name2obj:
            self.name2obj[ iname ] = Protein(iname)
            # print iname, "newly produced as", self.name2obj[iname]
        return self.name2obj[ iname ]


class Protein_Set:
    def __init__(self):
        self.protein_set = {}
        self.protein_name_set = {}
        self.protein_factory = Protein_Factory()

    def add_protein_by_name(self, prot_name):
        protein = self.protein_factory.make(prot_name)
        self.protein_set[ protein ] = prot_name
        self.protein_name_set[ prot_name ] = protein
        return protein

    def add_protein(self, iprotein):
        prot_name = iprotein.get_protein_name()
        return self.add_protein_by_name(prot_name)

    def delete_protein(self, protein):
        if not self.has_protein(protein):
            raise "Protein " + protein + " not in the set."
        del self.protein_set[ protein ]
        del self.protein_name_set[ protein.get_protein_name() ]

    def delete_protein_by_name(self, protein_name):
        protein = self.get_protein_by_name(protein_name)
        self.delete_protein(protein)

    def has_protein(self, protein):
        return protein in self.protein_set

    def protein_same_name(self, protein):
        prot_name = protein.get_protein_name()
        if prot_name in self.protein_name_set:
            return self.protein_name_set[ prot_name ]
        else:
            return False

    def get_protein_by_name(self, protein_name):

        if protein_name in self.protein_name_set:
            return self.protein_name_set[ protein_name ]
        else:
            return False

    def get_proteins(self):
        return self.protein_set.keys()

    def get_protein_names(self):
        return self.protein_name_set.keys()

    def display_protein_set(self):
        for protein in self.protein_set:
            print protein, protein.get_protein_name()

    def set_expression(self, expr):
        """ expr is expression instance """
        for id in expr.genes():
            protein = self.add_protein_by_name(id)
            protein.set_expression(expr.expression_pat(id))

    def set_values(self, tag, datasheet):
        if not isinstance(datasheet, Data_Struct.Data_Sheet2.Data_Sheet):
            raise "Instance type mismatch."

        for id in datasheet.row_labels():
            protein = self.add_protein_by_name(id)
            protein.set_value(tag, datasheet.get_data(id))

    def __iter__(self):
        return self.protein_set.keys().__iter__()


if __name__ == "__main__":
    import Usefuls.TmpFile

    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

-         C1  C2  C3
Rintaro -2.3 1.5 2.1
Saito   -1.3 1.9 1.1
XYZ      1.7 0.3
WWW      9.5 7.2 1.3

    """)

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

-         C1  C2  C3
Rintaro -2.1 1.0 2.1
Saito   -1.0 1.9 1.3
XYZ      1.0 0.3
CCC     -0.1 1.2 1.3

    """)

    prot_factory  = Protein_Factory()
    prot_factory2 = Protein_Factory()
    protein1 = prot_factory.make("AAA")
    protein2 = prot_factory.make("AAA")
    protein3 = prot_factory2.make("AAA")
    protein4 = prot_factory.make("AAB")

    print protein1.get_protein_name(), id(protein1)
    print protein2.get_protein_name(), id(protein2)
    print protein3.get_protein_name(), id(protein3)
    print protein4.get_protein_name(), id(protein4)

    pset = Protein_Set()
    protein1 = pset.add_protein(protein1)
    print protein1.get_protein_name(), id(protein1)

    expr = Express.Express(tmp_obj.filename())
    nums = Data_Struct.Data_Sheet2.Data_Sheet(tmp_obj2.filename())
    nums.numerize()

    protein = Protein("Rintaro")
    print protein.get_protein_name()
    protein2 = Protein("Saito")
    protein3 = Protein("Saito")
    protein4 = Protein("XYZ")

    protein_set = Protein_Set()
    print protein_set.add_protein(protein)
    print protein_set.add_protein(protein2)
    print protein_set.add_protein(protein4)
    protein_set.set_expression(expr)

    wwwprotein = protein_set.get_protein_by_name("WWW")
    print wwwprotein
    print wwwprotein.get_expression()

    protein_set.set_values("TEST1", nums)
    print protein2.get_expression()
    print protein4.get_expression()
    protein_set.display_protein_set()

    print "CCC"
    cccprotein = protein_set.get_protein_by_name("CCC")
    print cccprotein.get_value("TEST1")

    print protein_set.get_proteins()
    print protein_set.get_protein_names()

    print protein_set.add_protein(protein3)
    print protein_set.add_protein(protein)
    print protein_set.has_protein(protein2)
    print protein_set.has_protein(protein3)
    print protein_set.protein_same_name(protein3)
    print protein_set.get_protein_by_name("Saito")

    protein_set.display_protein_set()

    protein4.set_value("Rin", ["A", "B", "C"])
    print protein4.get_value("Rin")
    print protein4.get_value("TEST1")

    protein_set2 = Protein_Set()
    protein_set2.add_protein_by_name("Rintaro")
    protein_set2.add_protein_by_name("Saito")
    protein_set2.add_protein_by_name("Chishima")
    protein_set2.add_protein_by_name("Saito")
    protein_set2.display_protein_set()

    print
    Saito = protein_set2.get_protein_by_name("Saito")
    protein_set2.delete_protein(Saito)
    protein_set2.delete_protein_by_name("Rintaro")
    protein_set2.display_protein_set()


    protein_set3 = Protein_Set()
    protein_set3.add_protein_by_name("Rintaro")
    protein_set3.add_protein_by_name("Saito")

    for protein in protein_set3:
        print protein.get_protein_name()
