#!/usr/bin/env python

class Cluster_Eisen_frmt:
    def __init__(self, experiments, eweight = None, eorder = None):
        self.experiments = experiments
        if eweight:
            self.eweight = eweight
        else:
            self.eweight = [1] * len(experiments)
        if eorder:
            self.eorder = eorder
        else:
            self.eorder = map(lambda x:x + 1, range(len(experiments)))
        self.genes = []
        self.expression_pat_info = {}

            
    def set_expression(self, uniqid, expression_pat, annot = "", experiments = None, gweight = 1.0, gorder = None):
                    
        if uniqid in self.expression_pat_info:
            raise uniqid + " duplicated."
                       
        if experiments:
            if len(expression_pat) != len(experiments):
                raise "Number of experiments error ..."
            
            expression_pat_reorder = [ "" ] * len(self.experiments)
            for i in range(len(experiments)):
                exper = experiments[i]
                idx = list(self.experiments).index(exper)
                expression_pat_reorder[ idx ] = expression_pat[i]
        else:
            expression_pat_reorder = expression_pat
            
        if gorder == None:
            gorder = len(self.expression_pat_info) + 1
            
        self.expression_pat_info[uniqid] = (annot, gweight, gorder, expression_pat_reorder)
        self.genes.append(uniqid)
        
    def display(self):
        print "\t".join(["UNIQID", "NAME", "GWEIGHT", "GORDER"] + list(self.experiments))
        print "\t".join(["EWEIGHT", "",    "",        ""      ] + map(lambda x: `x`, self.eweight))
        print "\t".join(["EORDER",  "",    "",        ""      ] + map(lambda x: `x`, self.eorder))
        for uniqid in self.genes:
            annot, gweight, gorder, expression_pat = self.expression_pat_info[uniqid]
            expression_pat_str = []
            for exp in expression_pat:
                if type(exp) == str:
                    expression_pat_str.append(exp)
                else:
                    expression_pat_str.append("%.3f" % exp)
            print "\t".join([uniqid, annot, `gweight`, `gorder`] + expression_pat_str)
    

class Cluster_Eisen_frmt_II:
    def __init__(self, experiments, eweight = None, eorder = None):
        self.experiments = experiments
        if eweight:
            self.eweight = eweight
        else:
            self.eweight = [1] * len(experiments)
        if eorder:
            self.eorder = eorder
        else:
            self.eorder = map(lambda x:x + 1, range(len(experiments)))
        self.genes = []
        self.expression_pat_info = {}

            
    def set_expression(self, gid,
                       uniqid, expression_pat, annot = "", experiments = None, gweight = 1.0, gorder = None):
                    
        if uniqid in self.expression_pat_info:
            raise uniqid + " duplicated."
                       
        if experiments:
            if len(expression_pat) != len(experiments):
                raise "Number of experiments error ..."
            
            expression_pat_reorder = [ "" ] * len(self.experiments)
            for i in range(len(experiments)):
                exper = experiments[i]
                idx = list(self.experiments).index(exper)
                expression_pat_reorder[ idx ] = expression_pat[i]
        else:
            expression_pat_reorder = expression_pat
            
        if gorder == None:
            gorder = len(self.expression_pat_info) + 1
            
        self.expression_pat_info[uniqid] = (gid, annot, gweight, gorder, expression_pat_reorder)
        self.genes.append(uniqid)
        
    def display(self):
        print "\t".join(["GID",     "UNIQID", "NAME", "GWEIGHT", "GORDER"] + list(self.experiments))
        print "\t".join(["EWEIGHT", "",       "",     "",        ""      ] + map(lambda x: `x`, self.eweight))
        print "\t".join(["EORDER",  "",       "",     "",        ""      ] + map(lambda x: `x`, self.eorder))
        for uniqid in self.genes:
            gid, annot, gweight, gorder, expression_pat = self.expression_pat_info[uniqid]
            expression_pat_str = []
            for exp in expression_pat:
                if type(exp) == str:
                    expression_pat_str.append(exp)
                else:
                    expression_pat_str.append("%.3f" % exp)
            print "\t".join([gid, uniqid, annot, `gweight`, `gorder`] + expression_pat_str)

class Cluster_Eisen_frmt_II_simple:
    def __init__(self, experiments, eweight = None):
        self.experiments = experiments
        if eweight:
            self.eweight = eweight
        else:
            self.eweight = [1] * len(experiments)
        self.genes = []
        self.expression_pat_info = {}

            
    def set_expression(self, gid,
                       uniqid, expression_pat, annot = "", experiments = None, gweight = 1.0):
                    
        if uniqid in self.expression_pat_info:
            raise uniqid + " duplicated."
                       
        if experiments:
            if len(expression_pat) != len(experiments):
                raise "Number of experiments error ..."
            
            expression_pat_reorder = [ "" ] * len(self.experiments)
            for i in range(len(experiments)):
                exper = experiments[i]
                idx = list(self.experiments).index(exper)
                expression_pat_reorder[ idx ] = expression_pat[i]
        else:
            expression_pat_reorder = expression_pat
                        
        self.expression_pat_info[uniqid] = (gid, annot, gweight, expression_pat_reorder)
        self.genes.append(uniqid)
        
    def display(self):
        print "\t".join(["GID",     "UNIQID", "NAME", "GWEIGHT" ] + list(self.experiments))
        print "\t".join(["EWEIGHT", "",       "",     "",       ] + map(lambda x: `x`, self.eweight))
        for uniqid in self.genes:
            gid, annot, gweight, expression_pat = self.expression_pat_info[uniqid]
            expression_pat_str = []
            for exp in expression_pat:
                if type(exp) == str:
                    expression_pat_str.append(exp)
                else:
                    expression_pat_str.append("%.3f" % exp)
            print "\t".join([gid, uniqid, annot, `gweight`] + expression_pat_str)
             
if __name__ == "__main__":
    cef = Cluster_Eisen_frmt(("Cond #1", "Cond #2", "Cond #3"))
    cef.set_expression("Gene #1", [ 0.1, 4.3, 2.5 ], "This is a test gene #1")
    cef.set_expression("Gene #2", [ 7.1, 4.0, 2.5 ], "This is a test gene #2")
    cef.set_expression("Gene #3", [ 0.1, 4.0, 2.2 ], "This is a test gene #3")
    cef.set_expression("Gene #X", [ 1.5, 4.3, 2.1 ], "This is a test gene #X",
                       ["Cond #2", "Cond #1", "Cond #3"], 5.0)
    # cef.display()
    
    cef2 = Cluster_Eisen_frmt_II(("Cond #1", "Cond #2", "Cond #3"))
    cef2.set_expression("GENE1X", "Gene #1", [ 0.1, 4.3, 2.5 ], "This is a test gene #1")
    cef2.set_expression("GENE2X", "Gene #2", [ 7.1, 4.0, 2.5 ], "This is a test gene #2")
    cef2.set_expression("GENE3X", "Gene #3", [ 0.1, 4.0, 2.2 ], "This is a test gene #3")
    cef2.set_expression("GENE4X", "Gene #X", [ 1.5, 4.3, 2.1 ], "This is a test gene #X",
                       ["Cond #2", "Cond #1", "Cond #3"], 5.0)
    # cef2.display()
    
    cef2s = Cluster_Eisen_frmt_II_simple(("Cond #1", "Cond #2", "Cond #3"))
    cef2s.set_expression("GENE1X", "Gene #1", [ 0.1, 4.3, 2.5 ], "This is a test gene #1")
    cef2s.set_expression("GENE2X", "Gene #2", [ 7.1, 4.0, 2.5 ], "This is a test gene #2")
    cef2s.set_expression("GENE3X", "Gene #3", [ 0.1, 4.0, 2.2 ], "This is a test gene #3")
    cef2s.set_expression("GENE4X", "Gene #X", [ 1.5, 4.3, 2.1 ], "This is a test gene #X",
                       ["Cond #2", "Cond #1", "Cond #3"], 5.0)
    cef2s.display()
    