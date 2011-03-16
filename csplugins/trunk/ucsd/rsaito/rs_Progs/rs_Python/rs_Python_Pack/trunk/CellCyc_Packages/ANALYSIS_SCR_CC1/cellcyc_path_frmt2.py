#!/usr/bin/env python

from Usefuls.Sheet_Analysis import Sheet_tab_header
import Usefuls.rsConfig

def expand(node_name):
    if node_name == "Chk1/2":
        node_names = [ "Chk1", "Chk2" ]
    elif node_name == "Cdk4/6":
        node_names = [ "Cdk4", "Cdk6" ]
    elif node_name == "p16/p15":
        node_names = [ "p16", "p15" ]
    elif node_name == "p21/p27":
        node_names = [ "p21", "p27" ]
    else:
        node_names = [ node_name ]
    return node_names

rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

ccpath = Sheet_tab_header(rsc.Cell_cyc_path_mammal_descr)
ct = {}

while True:
    if ccpath.readline():
        substrate = ccpath.get_items_accord_hd(
            "Substrate 1",
            "Substrate 2")
        product = ccpath.get_items_accord_hd(
            "Product 1",
            "Product 2",
            "Product 3")

        substrate_epd = []
        product_epd = []

        if substrate and product:
            for elem in substrate:
                if elem:
                    expanded = expand(elem)
                substrate_epd += expanded

            for elem in product:
                if elem:
                    expanded = expand(elem)
                product_epd += expanded

            print substrate_epd, product_epd
        
    else:
        break
    
