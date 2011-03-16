#!/usr/bin/env python

""" ex. 100 is the left-edge of the box #100 """

def scaler(map_start, segm_positions, map_end,
           scale_segm, scale_spacer):
    scaled_pos_s = {}
    scaled_pos_e = {}
    segm_positions_sorted = []
    for info in segm_positions:
        pos, boundary = info
        if boundary == "s":
            segm_positions_sorted.append((pos, boundary))
        elif boundary == "e":
            segm_positions_sorted.append((pos+1, boundary))
        else:
            raise "Boundary symbol error."
    map_end += 1
    segm_positions_sorted.sort()
    old_pos = map_start
    new_map_pos = 0
    scaled_pos_s[ map_start ] = new_map_pos
    start_count = 0
       
    for cur_pos_info in segm_positions_sorted:
        cur_pos, boundary = cur_pos_info
        # print cur_pos_info, start_count
        if cur_pos < old_pos:
            raise "Mapping position error ..."
        if start_count == 0:
            new_map_pos += (cur_pos - old_pos) * scale_spacer
        elif start_count > 0:
            new_map_pos += (cur_pos - old_pos) * scale_segm
        else:
            raise "Start - End relation error."
        if boundary == "s":
            scaled_pos_s[ cur_pos ] = new_map_pos
            start_count += 1
        elif boundary == "e":
            scaled_pos_e[ cur_pos - 1 ] = new_map_pos
            start_count -= 1
        old_pos = cur_pos
        
    if map_end < old_pos:
        raise "Mapping position error ..."
        
    if start_count == 0:
        new_map_pos += (map_end - old_pos) * scale_spacer
    elif start_count > 0:
        new_map_pos += (map_end - old_pos) * scale_segm
    scaled_pos_e[ map_end - 1 ] = new_map_pos
       
    return scaled_pos_s, scaled_pos_e    


class Genome_Segm_Struct:
    def __init__(self, focused_region_name):
        self.segm_fragments = []
        self.focused_region_name = focused_region_name

    def add_segm(self, start, end, segm_name):
        self.segm_fragments.append((start, end, segm_name))

    def get_segms(self):
        return self.segm_fragments

    def scale(self, scale_segm, scale_spacer,
              map_start, map_end):

        segm_positions = []
        for segm in self.get_segms():
            start, end, segm_name = segm
            segm_positions.append((start, "s"))
            segm_positions.append((end, "e"))
        scaled_pos_s, scaled_pos_e = scaler(map_start, segm_positions, map_end,
                                            scale_segm, scale_spacer)
        out = [(scaled_pos_s[map_start], scaled_pos_e[map_end], self.focused_region_name)]
        for segm in self.get_segms():
            start, end, segm_name = segm
            scaled_start = scaled_pos_s[start]
            scaled_end   = scaled_pos_e[end]
            out.append((scaled_start, scaled_end, segm_name))
        return out

    def scale_II(self, scale_segm, scale_spacer,
                 extra_region_left, extra_region_right):
        segm_fragments_start = map(lambda x: x[0], self.segm_fragments)
        segm_fragments_end   = map(lambda x: x[1], self.segm_fragments)
        map_start = min(segm_fragments_start) - extra_region_left
        map_end   = max(segm_fragments_end) + extra_region_right
        segm_positions = []
        for segm in self.get_segms():
            start, end, segm_name = segm
            segm_positions.append((start, "s"))
            segm_positions.append((end, "e"))
        scaled_pos_s, scaled_pos_e = scaler(map_start, segm_positions, map_end,
                                            scale_segm, scale_spacer)
        out = [(scaled_pos_s[map_start], scaled_pos_e[map_end], self.focused_region_name)]
        for segm in self.get_segms():
            start, end, segm_name = segm
            scaled_start = scaled_pos_s[start]
            scaled_end   = scaled_pos_e[end]
            out.append((scaled_start, scaled_end, segm_name))
        return out
    
        
if __name__ == "__main__":
    scl_s, scl_e = scaler(1,
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
    keys_sorted = scl_s.keys()
    keys_sorted.sort()
    print "s"
    for each in keys_sorted:
        print each, scl_s[each]
    keys_sorted = scl_e.keys()
    keys_sorted.sort()
    print "e"
    for each in keys_sorted:
        print each, scl_e[each]
        
    gss = Genome_Segm_Struct("Genome")
    gss.add_segm(1021, 1060, "Exon Sense")
    gss.add_segm(1041, 1080, "Exon Antisense")
    gss.add_segm(1101, 1190, "Exon Sense")
    print gss.scale(1.0, 0.5, 1001, 1200)
    print gss.scale_II(1.0, 0.5, 20, 10)
    