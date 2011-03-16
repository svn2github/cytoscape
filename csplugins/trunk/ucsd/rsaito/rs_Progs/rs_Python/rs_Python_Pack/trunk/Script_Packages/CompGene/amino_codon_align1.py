#!/usr/bin/env python

from Seq_Packages.Homology.Pair_residue_to_residue1 import invoke_amino_codon_align1

info, align_prot_prot, align_nuc_prot1, align_nuc_prot2 \
    = invoke_amino_codon_align1(nucseq1 = """atg gcggccagca ggaggctgat gaaggagctt gaagaaatcc
gcaaatgtgg c gatgaaaaac ttccgtaaca tccaggttga tgaagctaat ttattgactt
ggcaagggct tattgttcct gacaaccctc catatgataa gggagccttc aatcgaaa
tcaactttcc agcagagtac""",
                                protseq1 = """
MAASRRLMKELEEIRKCGMKNFRNIQVDEANLLTWQGLIVPDNP
PYDKGAFRIEINFPAEYPFKPPKITFKTKIYHPNIDEKGQVCLPVISAENWKPATKTD
QVIQSLIALVNDPQPEHPLRADLAEEYSKDRKKFCKNAEEFTKKYGEKRPVD""",
                                nucseq2 = """atg gcggccagca ggaggctgat gaaggagctt gaagaaatcc
gcaaatgtgg c gatgaaaaac ttccgtaaca ac tccaggttga tgaagctaat ttattgactt
ggcaagggct tattgttcct gacaaccctc catatgataa gggagccttc aatcgaaa
tcaactttcc agcagagtac""",
                                protseq2 = """
MAASRRLMKELEEIRKCGMKNFRNIQVDEANLLTWQGLIVPDNP
PYDKGAFRIEINFPAEYPFKPPKKTKIYHPNIDEKGQVCLPVISAENWKPATKTD
QVIQSLIALVNDPQPEHPLRADLAEEYSKDRKKFCKNAEEFTKKYGEKRPVD""",
                                nucseq_ID1 = "NucSeq#1",
                                protseq_ID1 = "ProtSeq#1",
                                nucseq_ID2 = "NucSeq#2",
                                protseq_ID2 = "ProtSeq#2")

print "Alignment between prot 1 and prot 2:"
print "\n".join(align_prot_prot)
print
print "Alignment between nuc 1 and prot 1:"
print "\n".join(align_nuc_prot1)
print
print "Alignment between nuc 2 and prot 2:"
print "\n".join(align_nuc_prot2)
print

for residue_info in info:
    (amino_num1, amino_num2, 
     amino1, amino2, 
     codon1, codon2) = residue_info
    print "\t".join((`amino_num1`, `amino_num2`,
                     amino1, amino2,
                     codon1, codon2))        
