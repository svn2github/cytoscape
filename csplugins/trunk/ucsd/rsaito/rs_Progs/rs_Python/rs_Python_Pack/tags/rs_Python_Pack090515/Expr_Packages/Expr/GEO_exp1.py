#!/usr/bin/env python

import string

from Data_Struct.Hash import Hash
from Data_Struct.ListList import ListList
from Usefuls.ListProc1 import NonRedList

from GEO_probe1 import Probe_Info

import Calc_Packages.Math.Math1 as Math

class Hash_GEOexp(Hash):

    def read_file(self, filename, Key_cols = [0], Val_cols = [1]):

        fh = open(filename, 'r')
        count = 0
        for line in fh.readlines():
            if line[0] == "#" or line[0] == "*": continue
            linec = string.rstrip(line)
            r = linec.split("\t")

            # Adds extra empty columns
            max_col = max(Key_cols + Val_cols)
            if len(r) - 1 < max_col:
                r += ("", ) * (max_col - len(r) + 1)

            if self.filt_line(r): continue
            if self.verbose: print "Reading line #" + `count`, linec
            keys = []

            flag = False
            for col in Key_cols:
                keys.append(r[col])
                if r[col]: flag = True
            if not flag: continue
            key = string.join(keys, "\t")

            vals = []
            for col in Val_cols: vals.append(r[col])
            val = string.join(vals, "\t")
            if self.get_val_type() == "S": # Scalar
                self._Hash__set_data(key, val)
            elif self.get_val_type() == "A": # Array
                self._Hash__push_data(key, val)
            elif self.get_val_type() == "L": # Scalar, row as value
                self._Hash__set_data(key, string.join(r[Val_cols[0]:], "\t"))
            elif self.get_val_type() == "N": # Scalar, null
                self._Hash__set_data(key, "")
            else:
                raise "Illegal option", val_type
            count = count + 1

        fh.close()

class GEOexp:

    def __init__(self, filename):

        self.probeID_to_val = Hash_GEOexp("S")
        self.probeID_to_val.read_file(filename,
                                      Key_cols = [0],
                                      Val_cols = [1])

        self.probeID_to_call = Hash_GEOexp("S")
        self.probeID_to_call.read_file(filename,
                                       Key_cols = [0],
                                       Val_cols = [2])

        self.probeID_to_pval = Hash_GEOexp("S")
        self.probeID_to_pval.read_file(filename,
                                       Key_cols = [0],
                                       Val_cols = [3])

    def get_val(self, probeID):
        if self.probeID_to_val.val_force(probeID):
            return string.atof(self.probeID_to_val.val_force(probeID))
        else:
            return None

    def get_call(self, probeID):
        return self.probeID_to_call.val_force(probeID)

class GEOexp_set:
    def __init__(self):
        self.exp_file = []
        self.exp_label = []
        self.exp = []

    def set_exp_file(self, filename, exp_label):
        self.exp_file.append(filename)
        self.exp_label.append(exp_label)
        self.exp.append(GEOexp(filename))

    def expression_pat(self, probeid):
        e_pat = []
        for exp_obj in self.exp:
            e_pat.append(exp_obj.get_val(probeid))
        return e_pat

    def genes(self):
        ret = []
        for eobj in self.exp:
            ret += eobj.probeID_to_val.keys()
        return NonRedList(ret)

    def row_num(self):
        return len(self.genes())

    def conditions(self):
        return self.exp_label

    def corr(self, gene1, gene2):
	exp1 = self.expression_pat(gene1)
        if not exp1: return False

	exp2 = self.expression_pat(gene2)
        if not exp2: return False

	exp1_f, exp2_f = Math.filter_floats2(exp1, exp2)
        if len(exp1_f) < 2: return False

	return Math.corr(exp1_f, exp2_f)


class GEOexp_set_geneid:
    def __init__(self, probe_info, geoexp_set):
        # probe_info belongs to class Probe_Info
        if not isinstance(probe_info, Probe_Info):
            raise "Instance type mismatch: Probe_Info expected."
        self.probe_info = probe_info

        # geoexp_set belongs to class GEOexp_set
        if not isinstance(geoexp_set, GEOexp_set):
            raise "Instance type mismatch: GEOexp_set expected."
        self.geoexp_set = geoexp_set

    def expression_pat(self, geneid):

        exp_lists = ListList()
        probeids = self.probe_info.conv_GeneID_to_probeIDs(geneid)
        for probeid in probeids:
            exp_lists.add_list(self.geoexp_set.expression_pat(probeid))

        return exp_lists.to_single_list()

    def genes(self):
        return self.probe_info.get_genes()

    def row_num(self):
        return len(self.genes())

    def conditions(self):
        return self.geoexp_set.exp_label

    def corr(self, gene1, gene2):
	exp1 = self.expression_pat(gene1)
        if not exp1: return False

	exp2 = self.expression_pat(gene2)
        if not exp2: return False

	exp1_f, exp2_f = Math.filter_floats2(exp1, exp2)
        if len(exp1_f) < 2: return False

	return Math.corr(exp1_f, exp2_f)


if __name__ == "__main__":

    import Usefuls.rsConfig
    testfiles = Usefuls.rsConfig.RSC_II("GEO_test")

    probe_info = Probe_Info(testfiles.GPL96_14367)

    geo_exp_set = GEOexp_set()
    geo_exp_set.set_exp_file(testfiles.GSM28995, "EXP0")
    geo_exp_set.set_exp_file(testfiles.GSM28998, "EXP1")
    geo_exp_set.set_exp_file(testfiles.GSM29001, "EXP2")
    geo_exp_set.set_exp_file(testfiles.GSM29004, "EXP3")

    geoexp_set_geneid = GEOexp_set_geneid(probe_info, geo_exp_set)

    header = [ "Gene", "Probe IDs" ] + geoexp_set_geneid.conditions()
    print string.join(header, "\t")

    for gene in geoexp_set_geneid.genes():
        probes = string.join(probe_info.conv_GeneID_to_probeIDs(gene), ";")
        expr = []
        for e_float in geoexp_set_geneid.expression_pat(gene):
            if e_float:
                expr.append(`e_float`)
            else:
                expr.append("")

        print string.join([gene, probes ] + expr, "\t")

    # print geoexp_set_geneid.genes()
    # print geoexp_set_geneid.expression_pat("29103")
    # print geoexp_set_geneid.expression_pat("29103")
