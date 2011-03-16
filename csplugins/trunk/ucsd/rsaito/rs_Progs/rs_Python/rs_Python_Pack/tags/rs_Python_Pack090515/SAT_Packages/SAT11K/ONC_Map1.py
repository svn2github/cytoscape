#!/usr/bin/env python

import Seq_Packages.Map.Map_Info1 as Map
import Seq_Packages.Homology.Homology_descr4 as Homol

from SAT_Packages.Integration.SAT_Global_Center import strand_s, strand_o

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

map_infos = Map.Map_Infos()
map_errors = map_infos.read_spaln(rsc.Human11k_Cancer_ONC_Map_hg17_spaln)

homol_probe_seq = Homol.HomologyDescr(rsc.Human11k_Cancer_ONC_AFAS_probes_homol)
homol_probe_seq.enable_reverse()

for id in map_infos:
    map_info = map_infos[id]
    for map in map_info.get_map():
        if map.get_strand():
            strand = map.get_strand()
        else:
            strand = ""
        gap = abs(map.s_end() - map.s_start()) - abs(map.q_end() - map.q_start())
        if gap == 0:
            gap = ""
        print map.get_id(), map.get_chr(), strand, map.q_start(), map.q_end(), map.s_start(), map.s_end(), gap
    probe_map = {}
    for probe_id in homol_probe_seq.reverse_query_ID(map.get_id()):
        # print probe_id, homol_probe_seq.subject_start(probe_id, map.get_id()), homol_probe_seq.subject_end(probe_id, map.get_id())
        probe_map[ probe_id ] = (homol_probe_seq.subject_start(probe_id, map.get_id()),
                                 homol_probe_seq.subject_end(probe_id, map.get_id()))
    for probe_id in map_info.map_q_segms_to_subj(probe_map):
        if map.get_strand() is None:
            probe_strand = ""
        elif probe_id.startswith("ONC-"):
            probe_strand = strand_s[map.get_strand()]
        elif probe_id.startswith("AFAS-"):
            probe_strand = strand_o[map.get_strand()]
            
        for segm in map_info.map_q_segms_to_subj(probe_map)[ probe_id ]:
            print probe_id, probe_strand, segm[0], segm[1]

# print map_errors