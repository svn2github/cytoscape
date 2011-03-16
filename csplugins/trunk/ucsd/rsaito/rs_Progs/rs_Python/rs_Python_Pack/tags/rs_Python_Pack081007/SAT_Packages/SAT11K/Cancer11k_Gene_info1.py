#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
from Data_Struct.NonRedSet1 import NonRedSetDict

class Oncogene_ID_ERROR:
    pass

class Cancer11k_Gene_info:
    def __init__(self,
                 cancer_gene_info_file,
                 category_info_file):

        self.accession_to_annot = Hash("S")
        self.accession_to_annot.read_file_hd(
            cancer_gene_info_file,
            Key_cols_hd = [ "GENBANK" ],
            Val_cols_hd = [ "GENE NAME" ])

        category_info_tmp = Hash("S")
        category_info_tmp.read_file_hd(cancer_gene_info_file,
                                       Key_cols_hd = [ "GENBANK" ],
                                       Val_cols_hd = [ "CLASSIFICATIONS" ])

        accession_to_categ = NonRedSetDict()
        categ_to_accession = NonRedSetDict()

        for accession in category_info_tmp.keys():

            categories_tmp = category_info_tmp.val(accession).split(" ")
            categories = []

            for category in categories_tmp:
                if category:
                    categories.append(category)
                    categories.append(category[0:2] + "00")

            for category in categories:
                accession_to_categ.append_Dict(accession, category)
                categ_to_accession.append_Dict(category, accession)

        self.accession_to_categ = accession_to_categ
        self.categ_to_accession = categ_to_accession

        self.category_info = Hash("S")
        self.category_info.read_file_hd(
            category_info_file,
            Key_cols_hd = [ "Category ID" ],
            Val_cols_hd = [ "FUNCTIONAL CLASSIFICATION" ])

    def get_accession_from_onc_id(self, onc_id):
        if self.judge_onc_id(onc_id):
            return onc_id[4:]
        else:
            return None

    def get_onc_id_from_accession(self, accession):
        return "ONC-" + accession

    def judge_onc_id(self, id):
        return id[:4] == "ONC-"

    def get_onc_id_from_categ(self, categ):

        accessions = self.categ_to_accession.ret_set_Dict(categ)
        return map(lambda str:
                   self.get_onc_id_from_accession(str),
                   accessions)

    def get_categ_from_onc_id(self, onc_id):
        if not self.judge_onc_id(onc_id):
            Oncogene_ID_ERROR.id = onc_id
            raise Oncogene_ID_ERROR

        if self.accession_to_categ.has_key(
            self.get_accession_from_onc_id(onc_id)):
            return self.accession_to_categ.ret_set_Dict(
                self.get_accession_from_onc_id(onc_id))
        else:
            return []

    def get_categ_descr_from_onc_id(self, onc_id):
        ret = {}
        categs = self.get_categ_from_onc_id(onc_id)
        for categ in categs:
            descr = self.get_category_descr(categ)
            if descr:
                ret[ descr ] = ""
        return ret.keys()

    def get_major_categ_descr(self, onc_id):

        ret = {}
        categ = self.get_categ_from_onc_id(onc_id)
        for elem in categ:
            elem_major = elem[:2] + "00"
            descr = self.get_category_descr(elem_major)
            if descr:
                ret[ descr ] = ""

        ret = ret.keys()
        ret.sort()
        return ret

    def get_categories(self):
        return self.categ_to_accession.keys()

    def get_category_descr(self, categ):
        return self.category_info.val_force(categ)

    def get_annotation(self, onc_id):
        if not self.judge_onc_id(onc_id):
            raise "Oncogene ID ERROR..."
        return self.accession_to_annot.val_force(
            self.get_accession_from_onc_id(onc_id))


class Cancer11k_Gene_info_OncTS(Cancer11k_Gene_info):
    def __init__(self,
                 cancer_gene_info_file,
                 category_info_file):
        Cancer11k_Gene_info.__init__(self,
                                     cancer_gene_info_file,
                                     category_info_file)
        self.accession_to_OncTS = Hash("S")
        self.accession_to_OncTS.read_file_hd(
            cancer_gene_info_file,
            Key_cols_hd = [ "GENBANK" ],
            Val_cols_hd = [ "Oncogenes / Tumor Supressors" ])

    def get_OncTS_from_onc_id(self, onc_id):
        if not self.judge_onc_id(onc_id):
            Oncogene_ID_ERROR.id = onc_id
            raise Oncogene_ID_ERROR
        accession = self.get_accession_from_onc_id(onc_id)
        return self.accession_to_OncTS.val_force(accession)


if __name__ == "__main__":
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")

    cancer_gene_info = Cancer11k_Gene_info_OncTS(
        rsc.Human11k_Cancer_gene_info,
        rsc.Human11k_Cancer_category_info_func)

    print cancer_gene_info.get_onc_id_from_categ("FG00")
    print cancer_gene_info.get_category_descr("FG00")

    print cancer_gene_info.get_categories()

    for category in cancer_gene_info.get_categories():
        print category, cancer_gene_info.get_category_descr(category)

    print cancer_gene_info.get_annotation("ONC-D50310")
    print cancer_gene_info.get_categ_from_onc_id("ONC-D50310")
    print cancer_gene_info.get_categ_descr_from_onc_id("ONC-D50310")
    print cancer_gene_info.get_major_categ_descr("ONC-M15024")

    print cancer_gene_info.get_accession_from_onc_id("ONC-M15024")
    print cancer_gene_info.get_accession_from_onc_id("XONC-M15024")
    print cancer_gene_info.get_onc_id_from_accession("M15024")
    print cancer_gene_info.judge_onc_id("ONC-M15024")
    print cancer_gene_info.judge_onc_id("M15024")
    print cancer_gene_info.get_OncTS_from_onc_id("ONC-X51630")
    print cancer_gene_info.get_OncTS_from_onc_id("ONC-D50310")

