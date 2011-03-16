#!/usr/bin/env python

import sys
sys.path.append("../")
import Expr.Express1
import Usefuls.Data_Sheet
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
        self.name2objID = {}

    def make(self, iname):
        if iname not in self.name2objID:
            self.name2objID[ iname ] = Protein(iname)
        return self.name2objID[ iname ]


class Protein_Set:
    def __init__(self):
        self.protein_set = {}
        self.protein_name_set = {}

    def add_protein(self, protein):
        prot_name = protein.get_protein_name()
        if prot_name in self.protein_name_set:
            if self.protein_name_set[ prot_name ] is protein:
                # sys.stderr.write("Same object for " +
                # protein.get_protein_name() +
                # " already registered.\n")
                return protein
            else:
                # sys.stderr.write('Protein "' + prot_name +
                # ' already registered as ' +
                # 'different object.\n')
                return self.protein_name_set[ prot_name ]

        else:
            self.protein_set[ protein ] = prot_name
            self.protein_name_set[ prot_name ] = protein
            # print "Newly registering protein ..."
            return protein

    def add_protein_by_name(self, prot_name):
        if prot_name in self.protein_name_set:
            # print "Already registered name:", prot_name
            return self.protein_name_set[ prot_name ]
        else:
            protein = Protein(prot_name)
            self.add_protein(protein)
            # print "Newly registering", prot_name
            return protein

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
        if not isinstance(datasheet, Usefuls.Data_Sheet.Data_Sheet):
            raise "Instance type mismatch."

        for id in datasheet.row_labels():
            protein = self.add_protein_by_name(id)
            protein.set_value(tag, datasheet.get_data(id))


if __name__ == "__main__":
    import Usefuls.TmpFile

    prot_factory  = Protein_Factory()
    prot_factory2 = Protein_Factory()
    protein1 = prot_factory.make("AAA")
    protein2 = prot_factory2.make("AAA")
    protein3 = prot_factory.make("AAB")
    print protein1.get_protein_name(), id(protein1)
    print protein2.get_protein_name(), id(protein2)
    print protein3.get_protein_name(), id(protein3)


    tmp_obj = Usefuls.TmpFile.TmpFile3("""

-         C1  C2  C3
Rintaro -2.3 1.5 2.1
Saito   -1.3 1.9 1.1
XYZ      1.7 0.3
WWW      9.5 7.2 1.3

    """)

    tmp_obj2 = Usefuls.TmpFile.TmpFile3("""

-         C1  C2  C3
Rintaro -2.1 1.0 2.1
Saito   -1.0 1.9 1.3
XYZ      1.0 0.3
CCC     -0.1 1.2 1.3

    """)

    expr = Expr.Express1.Express1(tmp_obj.filename())
    nums = Usefuls.Data_Sheet.Data_Sheet()
    nums.read_sheet_file(tmp_obj2.filename(), "\t")
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


