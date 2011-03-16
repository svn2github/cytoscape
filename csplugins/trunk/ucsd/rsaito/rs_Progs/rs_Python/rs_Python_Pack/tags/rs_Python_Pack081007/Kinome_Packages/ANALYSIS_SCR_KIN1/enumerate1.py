#!/usr/bin/env python

from Kinome_Packages.Data_Info.Modified_Seq1 import Modified_Seq_Set
from General_Packages.Usefuls.Table_maker import Table_row

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsKinome_Config")

sfs = Modified_Seq_Set()
sfs.read_kinome_file1(rsc.Plasmo_rat_eryth, rsc.IPI_MOUSE_RAT_DB)

tb = Table_row()

tb.append("Detected Seq #", "")
tb.append("Detected Seq.",  "")
tb.append("Modification Position (Frag.)", "")
tb.append("Modification Type", "")
tb.append("Hit Entry", "")

count = 0
for mod_seq_inst in sfs.get_all_mod_seq_inst():

    hit_entries = mod_seq_inst.get_hit_entries()
    hit_seqs = mod_seq_inst.get_hit_entry_seqs()
    hit_mod_seq_instss = mod_seq_inst.map_mod_seq_to_hit_entries()

    tb.append("Detected Seq #", `count`)
    tb.append("Detected Seq.",  mod_seq_inst.get_mod_seq().get_seq())

    for i in range(len(hit_entries)):
        hit_entry = hit_entries[i]
        hit_seq   = hit_seqs[i]
        hit_mod_seq_insts = hit_mod_seq_instss[i]

        tb.append("Hit Entry", hit_entry)

        hit_pos_count = 0
        for hit_mod_seq_inst in hit_mod_seq_insts:

            for j in range(len(hit_mod_seq_inst.get_modifications())):
                modif = hit_mod_seq_inst.get_modifications()[j]
                modif_org = mod_seq_inst.get_modifications()[j]

                tb.append("Modification Position (Frag.)",
                          `modif_org.get_site()`)

                tb.append("Hit Position Count", `hit_pos_count`)
                tb.append("Modification Type", modif.get_mtype())
                tb.append("Modification Position", `modif.get_site()`)

                upseq = hit_mod_seq_inst.get_mod_seq().get_seq_frag(
                    modif.get_site() - 10,
                    modif.get_site() - 1)

                mdseq = hit_mod_seq_inst.get_mod_seq()[ modif.get_site() ]

                dnseq = hit_mod_seq_inst.get_mod_seq().get_seq_frag(
                    modif.get_site() + 1,
                    modif.get_site() + 10)

                tb.append("Upstream Seq.", upseq)
                tb.append("Modified Residue", mdseq)
                tb.append("Downstream Seq.", dnseq)
                tb.output("\t")

            hit_pos_count += 1

    count += 1
