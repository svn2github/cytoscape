
class VarScanReader(object):

    def __init__(self, filename, filetype):
        self.fh = file(filename,'r')


class VarScanVar(object):
    
    def __init__(self, row):
        self.chrom = row[0]
	self.position = row[1]
	self.ref = row[2]
	self.var = row[3]
	self.normal_reads1 = row[4]
	self.normal_reads2 = row[5]
	self.normal_var_freq = row[6]
	self.normal_gt = row[7]
	self.tumor_reads1 = row[8]
	self.tumor_reads2 = row[9]
	self.tumor_var_freq = row[10]
	self.tumor_gt = row[11]
	self.somatic_status = row[12]
	self.variant_p_value = row[13]
	self.somatic_p_value = row[14]
	self.tumor_reads1_plus = row[15]
	self.tumor_reads1_minus = row[16]
	self.tumor_reads2_plus = row[17]
	self.tumor_reads2_minus = row[18]

class VarScanCopy(object):
    
    def __init__(self, row):
        self.chrom = row[0]
        self.chr_start = row[1]
        self.chr_stop = row[2]
        self.num_positions = row[3]
        self.normal_depth = row[4]
        self.tumor_depth = row[5]
        self.log2_ratio = row[6]
        self.gc_content = row[7]
