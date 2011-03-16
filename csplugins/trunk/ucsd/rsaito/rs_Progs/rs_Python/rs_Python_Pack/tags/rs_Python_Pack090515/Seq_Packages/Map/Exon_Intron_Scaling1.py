#!/usr/bin/env python

""" ex. 100 is the left-edge of the box #100 """

def scaler(map_start, exon_positions, map_end,
           scale_exon, scale_intron):
    scaled_pos = {}
    exon_positions_sorted = list(exon_positions)
    exon_positions_sorted.sort()
    old_pos = map_start
    new_map_pos = map_start
    start_count = 0
    for cur_pos_info in exon_positions_sorted:
        cur_pos, boundary = cur_pos_info
        print cur_pos_info, start_count
        if start_count == 0:
            new_map_pos += (cur_pos - old_pos) * scale_intron
        elif start_count > 0:
            new_map_pos += (cur_pos - old_pos) * scale_exon
        scaled_pos[ cur_pos ] = new_map_pos
        if boundary == "s":
            start_count += 1
        elif boundary == "e":
            start_count -= 1
        old_pos = cur_pos
    if start_count == 0:
        new_map_pos += (map_end - old_pos) * scale_intron
    elif start_count > 0:
        new_map_pos += (map_end - old_pos) * scale_exon
    scaled_pos[ map_end ] = new_map_pos
       
    return scaled_pos    

if __name__ == "__main__":
    scl = scaler(1,
                 ((21, "s"),
                  (40, "e"),
                  (31, "s"),
                  (50, "e"),
                  (71, "s"),
                  (90, "e")    
                  ),    
                  100,    
                  1.0,    
                  0.5)
    keys_sorted = scl.keys()
    keys_sorted.sort()
    for each in keys_sorted:
        print each, scl[each]