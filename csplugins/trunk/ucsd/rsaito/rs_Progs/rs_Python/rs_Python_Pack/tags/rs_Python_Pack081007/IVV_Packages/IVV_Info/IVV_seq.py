#!/usr/bin/env python

import IVV_Packages.IVV_Info as IVV_Info

class IVV_seq:
    def __init__(self, seqid, ivv_info, idtype = "Prey"):

        if idtype != "Prey" and idtype != "Bait":
            raise "IVV Sequence type Error: " + idtype
        
        self.seqid = seqid
        self.idtype = idtype
        self.ivv_info = ivv_info

    def get_seqid(self):
        return self.seqid

    def get_idtype(self):
        return self.idtype

    def get_bait_type(self):
        if self.seqid == "Initial_Initial":
            return "Initial"
        elif self.seqid == "Mock_Mock":
            return "Mock"
        else:
            return "Bait"

    def geneid(self):
        if self.idtype == "Prey":
            info = self.ivv_info.Prey_info()
        else:
            info = self.ivv_info.Bait_info()
        return info.geneid(self.seqid)

    def hit_refseq(self):
        if self.idtype == "Prey":
            hit_id = self.ivv_info.Prey_info().get_qual_noerror(self.seqid,
                                                                "hit_refseqid")
        else:
            hit_id = False

        return hit_id

    def hit_refseq_region(self):
        if self.idtype == "Prey":
            info = self.ivv_info.Prey_info()
            hit_region = info.get_qual_noerror(self.seqid,
                                               "hit_ref_position")
            if hit_region == "": return False
            
            start_s, end_s = hit_region.split("..")
            hit_region_ret = (int(start_s), int(end_s))

        else:
            hit_region_ret = (-10000000, 10000000)

        return hit_region_ret


if __name__ == "__main__":
    ivv_info_file = "../IVV/ivv_human7.3_info"
    ivv_info = IVV_info.IVV_info(ivv_info_file, "JUN", "FOS")

    prey1 = IVV_seq("T050726_MJun4_2_D17.seq", ivv_info)
    print prey1.get_seqid()
    print prey1.hit_refseq()
    print prey1.hit_refseq_region()

    bait1 = IVV_seq("3725_502..957", ivv_info, idtype = "Bait")
    print bait1.get_seqid()
    print bait1.hit_refseq()
    print bait1.hit_refseq_region()

    prey2 = IVV_seq("T060117_D1_M08.seq", ivv_info)
    print prey2.get_seqid()
    print prey2.hit_refseq()
    print prey2.hit_refseq_region()
