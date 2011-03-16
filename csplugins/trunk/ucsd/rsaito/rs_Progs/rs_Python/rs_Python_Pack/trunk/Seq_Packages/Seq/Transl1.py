#!/usr/bin/env python

codon_table_std = { 'ttt': 'F', 'tct': 'S', 'tat': 'Y', 'tgt': 'C',
                    'ttc': 'F', 'tcc': 'S', 'tac': 'Y', 'tgc': 'C',
                    'tta': 'L', 'tca': 'S', 'taa': '*', 'tga': '*',
                    'ttg': 'L', 'tcg': 'S', 'tag': '*', 'tgg': 'W',
                    'ctt': 'L', 'cct': 'P', 'cat': 'H', 'cgt': 'R',
                    'ctc': 'L', 'ccc': 'P', 'cac': 'H', 'cgc': 'R',
                    'cta': 'L', 'cca': 'P', 'caa': 'Q', 'cga': 'R',
                    'ctg': 'L', 'ccg': 'P', 'cag': 'Q', 'cgg': 'R',
                    'att': 'I', 'act': 'T', 'aat': 'N', 'agt': 'S',
                    'atc': 'I', 'acc': 'T', 'aac': 'N', 'agc': 'S',
                    'ata': 'I', 'aca': 'T', 'aaa': 'K', 'aga': 'R',
                    'atg': 'M', 'acg': 'T', 'aag': 'K', 'agg': 'R',
                    'gtt': 'V', 'gct': 'A', 'gat': 'D', 'ggt': 'G',
                    'gtc': 'V', 'gcc': 'A', 'gac': 'D', 'ggc': 'G',
                    'gta': 'V', 'gca': 'A', 'gaa': 'E', 'gga': 'G',
                    'gtg': 'V', 'gcg': 'A', 'gag': 'E', 'ggg': 'G'
}

if __name__ == "__main__":
    print codon_table_std["atg"]
