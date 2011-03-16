#!/usr/bin/env python

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

import Usefuls.Synonyms

cc_syno = Usefuls.Synonyms.Synonyms(rsc.Cell_cyc_Syno,
                                    0,
                                    [],
                                    False)

fh = open(rsc.Botstein_expr, "r")
header = fh.readline().rstrip()
print header
for rline in fh:
    sline = rline.rstrip()
    r = sline.split("\t")
    if cc_syno.to_main(r[1]) is not None:
        print "\t".join([r[0]] +
                        [cc_syno.to_main(r[1])] +
                        r[2:])

