#!/usr/bin/env python

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

colon_cancer_keys = [ "Colon_C1",
                      "Colon_C12",
                      "Colon_C13",
                      "Colon_C15",
                      "Colon_C2",
                      "Colon_C7" ] 
""" Colon_C8 eliminated because marker gene expression is abnormal. """

colon_normal_keys = [ "Colon_N1",
                      "Colon_N12",
                      "Colon_N13",
                      "Colon_N15",
                      "Colon_N2",
                      "Colon_N7" ]

""" Colon_N8 eliminated because marker gene expression is abnormal. """

    
hepatic_cancer_keys = [ "Hepatic_C12",
                        "Hepatic_C16",
                        "Hepatic_C20",
                        "Hepatic_C5",
                        "Hepatic_C6" ]

hepatic_normal_keys = [ "Hepatic_N12",
                        "Hepatic_N16",
                        "Hepatic_N20",
                        "Hepatic_N5",
                        "Hepatic_N6" ]

okay_marker_afas_colon = [
    "AFAS-Onc-Anti-L31951-03",
    "AFAS-Onc-Anti-L34058-01",
    "AFAS-Onc-Anti-M14505-02",
    "AFAS-Onc-Anti-M25753-01",
    "AFAS-Onc-Anti-U01038-02",
    "AFAS-Onc-Anti-U29343-04",
    "AFAS-Onc-Anti-U37139-02",
    "AFAS-Onc-Anti-U43746-02",
    "AFAS-Onc-Anti-U43746-04",
    "AFAS-Onc-Anti-U58334-02",
    "AFAS-Onc-Anti-X52022-10",
    "AFAS-Onc-Anti-X57766-04",
    "AFAS-Onc-Anti-X63629-03",
    "AFAS-Onc-Anti-X63629-04",
    "AFAS-Onc-Anti-X63629-05" 
    ]

okay_marker_afas_hepatic = [
    "AFAS-Onc-Anti-L03840-02",
    "AFAS-Onc-Anti-L12350-04",
    "AFAS-Onc-Anti-L34058-01",
    "AFAS-Onc-Anti-L34058-02",
    "AFAS-Onc-Anti-L34058-03",
    "AFAS-Onc-Anti-M15796-02",
    "AFAS-Onc-Anti-M21616-03",
    "AFAS-Onc-Anti-M25753-02",
    "AFAS-Onc-Anti-M31899-03",
    "AFAS-Onc-Anti-M81104-03",
    "AFAS-Onc-Anti-U25278-02",
    "AFAS-Onc-Anti-X53586-04"
    ]

# Patient 8 is eliminated because of abnormal expression.

colon_dT = {
    "Colon_N1"  : rsc.FE_Human11k_Colon_dT_N1,
    "Colon_N12" : rsc.FE_Human11k_Colon_dT_N12,   
    "Colon_N13" : rsc.FE_Human11k_Colon_dT_N13,  
    "Colon_N15" : rsc.FE_Human11k_Colon_dT_N15,   
    "Colon_N2"  : rsc.FE_Human11k_Colon_dT_N2,    
    "Colon_N7"  : rsc.FE_Human11k_Colon_dT_N7,    
    # "Colon_N8"  : rsc.FE_Human11k_Colon_dT_N8, 

    "Colon_C1"  : rsc.FE_Human11k_Colon_dT_C1,
    "Colon_C12" : rsc.FE_Human11k_Colon_dT_C12,   
    "Colon_C13" : rsc.FE_Human11k_Colon_dT_C13,  
    "Colon_C15" : rsc.FE_Human11k_Colon_dT_C15,   
    "Colon_C2"  : rsc.FE_Human11k_Colon_dT_C2,    
    "Colon_C7"  : rsc.FE_Human11k_Colon_dT_C7    
    # "Colon_C8"  : rsc.FE_Human11k_Colon_dT_C8
    }

# Patient 8 is eliminated because of abnormal expression.

colon_random = {
    "Colon_N1"  : rsc.FE_Human11k_Colon_random_N1,
    "Colon_N12" : rsc.FE_Human11k_Colon_random_N12,   
    "Colon_N13" : rsc.FE_Human11k_Colon_random_N13,  
    "Colon_N15" : rsc.FE_Human11k_Colon_random_N15,   
    "Colon_N2"  : rsc.FE_Human11k_Colon_random_N2,    
    "Colon_N7"  : rsc.FE_Human11k_Colon_random_N7,    
    # "Colon_N8"  : rsc.FE_Human11k_Colon_random_N8,

    "Colon_C1"  : rsc.FE_Human11k_Colon_random_C1,
    "Colon_C12" : rsc.FE_Human11k_Colon_random_C12,   
    "Colon_C13" : rsc.FE_Human11k_Colon_random_C13,  
    "Colon_C15" : rsc.FE_Human11k_Colon_random_C15,   
    "Colon_C2"  : rsc.FE_Human11k_Colon_random_C2,    
    "Colon_C7"  : rsc.FE_Human11k_Colon_random_C7    
    # "Colon_C8"  : rsc.FE_Human11k_Colon_random_C8
    }

hepatic_dT = {
    "Hepatic_N12" : rsc.FE_Human11k_Hepatic_dT_N12,   
    "Hepatic_N16" : rsc.FE_Human11k_Hepatic_dT_N16,  
    "Hepatic_N20" : rsc.FE_Human11k_Hepatic_dT_N20,   
    "Hepatic_N5"  : rsc.FE_Human11k_Hepatic_dT_N5,    
    "Hepatic_N6"  : rsc.FE_Human11k_Hepatic_dT_N6,

    "Hepatic_C12" : rsc.FE_Human11k_Hepatic_dT_C12,   
    "Hepatic_C16" : rsc.FE_Human11k_Hepatic_dT_C16,  
    "Hepatic_C20" : rsc.FE_Human11k_Hepatic_dT_C20,   
    "Hepatic_C5"  : rsc.FE_Human11k_Hepatic_dT_C5,    
    "Hepatic_C6"  : rsc.FE_Human11k_Hepatic_dT_C6 }

hepatic_random = {
    "Hepatic_N12" : rsc.FE_Human11k_Hepatic_random_N12,   
    "Hepatic_N16" : rsc.FE_Human11k_Hepatic_random_N16,  
    "Hepatic_N20" : rsc.FE_Human11k_Hepatic_random_N20,   
    "Hepatic_N5"  : rsc.FE_Human11k_Hepatic_random_N5,    
    "Hepatic_N6"  : rsc.FE_Human11k_Hepatic_random_N6,

    "Hepatic_C12" : rsc.FE_Human11k_Hepatic_random_C12,   
    "Hepatic_C16" : rsc.FE_Human11k_Hepatic_random_C16,  
    "Hepatic_C20" : rsc.FE_Human11k_Hepatic_random_C20,   
    "Hepatic_C5"  : rsc.FE_Human11k_Hepatic_random_C5,    
    "Hepatic_C6"  : rsc.FE_Human11k_Hepatic_random_C6 }

human11k_dT = {
    "Brain"      : rsc.FE_Human11k_Brain_dT,
    "Fibroblast" : rsc.FE_Human11k_Fibroblast_dT,
    "Heart"      : rsc.FE_Human11k_Heart_dT,         
    "Liver"      : rsc.FE_Human11k_Liver_dT,         
    "Testis"     : rsc.FE_Human11k_Testis_dT }       

human11k_random = {
    "Brain"      : rsc.FE_Human11k_Brain_random,
    "Fibroblast" : rsc.FE_Human11k_Fibroblast_random,
    "Heart"      : rsc.FE_Human11k_Heart_random,         
    "Liver"      : rsc.FE_Human11k_Liver_random,         
    "Testis"     : rsc.FE_Human11k_Testis_random }       

