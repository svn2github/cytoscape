#!/usr/bin/env python

""" This system is a part of PUNI, Potentially Universal and Narcissistic Infrastructure. """

from os import system, path
import pickle
from datetime import datetime

from Data_Struct.Hash2 import Hash
from random import randrange
from Usefuls.rsConfig import RSC_II
from Usefuls.TmpFile import TmpFile_II
from Usefuls.Replace_by_dict import replace_by_dict
from WEB.CGI_BasicI import cgi_html_out


def upper_first_char(s):
    return s[0].upper() + s[1:]

rsc = RSC_II("rsLec_MCB_Sys_Config")

hDB = Hash("S")
hDB.set_miscolumn_permit()
hDB.read_file_hd(filename = rsc.BioBasic3_DB,
                 Key_cols_hd = ["Student ID"],
                 Val_cols_hd = ["Department",
                                "Grade",
                                "Account",
                                "E-Mail",
                                "Family Name",
                                "Given Name",
                                "Score A",
                                "Score B",
                                "Score A Rank",
                                "Score B Rank",
                                "DS A",
                                "DS B",
                                "DS A Mod",
                                "DS B Mod",
                                "Validity A",
                                "Validity B",
                                "Presentation Score A",
                                "Presentation Score B",
                                "Presentation Days A",
                                "Presentation Days B",
                                ])

ACCESS_LIMIT = 5

m_replace_SID   = "[[SID]]"
m_replace_NAME  = "[[Name]]"
m_replace_Mail  = "[[Mail]]"
m_replace_NACC  = "[[NACC]]"
m_replace_RNDM  = "[[RNDM]]"
m_replace_A     = "[[A]]"
m_replace_ARNK  = "[[ARNK]]"
m_replace_ADS   = "[[ADS]]"
m_replace_ADSM  = "[[ADSM]]"
m_replace_B     = "[[B]]"
m_replace_BRNK  = "[[BRNK]]"
m_replace_BDS   = "[[BDS]]"
m_replace_BDSM  = "[[BDSM]]"

m_replace_GNAM  = "[[GName]]"
m_replace_VLDA  = "[[VLDA]]"
m_replace_VLDB  = "[[VLDB]]"
m_replace_PRSTA = "[[PRSTA]]"
m_replace_PRSTB = "[[PRSTB]]"
m_replace_DSTOT = "[[DSTOT]]"

rd_messages = open(rsc.BioBasic3_rnd_m, "r").readlines()

MAIL_Command = "mail"
MAIL_Subject = "MCB3-2010: Result of your exam (Ver. 20101020)"
MAIL_From    = "rsaito@sfc.keio.ac.jp"

def set_MAIL_Command(mail_command):
    global MAIL_Command
    MAIL_Command = mail_command


def auto_email(input_ID, send_email = True):

    name = "(Unknown)"
    access_time = datetime.now().strftime("%Y/%m/%d %H:%M:%S")

    alog = {}
    if path.isfile(rsc.BioBasic3_Log):
        alog = pickle.load(open(rsc.BioBasic3_Log, "r"))
        
    if alog.get(input_ID, 0) >= ACCESS_LIMIT:

        print cgi_html_out(open(rsc.BioBasic3_html_ACLM, "r").read())

    elif hDB.has_key(input_ID):
        name     = upper_first_char(hDB.val_accord_hd(input_ID, "Given Name")) + ' ' + upper_first_char(hDB.val_accord_hd(input_ID, "Family Name"))
        email    = hDB.val_accord_hd(input_ID, "E-Mail")

        if hDB.val_accord_hd(input_ID, "Score A"):
            test_a    = "%2d"  % int(hDB.val_accord_hd(input_ID, "Score A"))
            test_arnk = hDB.val_accord_hd(input_ID, "Score A Rank")
            test_ads  = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS A"))
            # test1adsm = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 1A Mod"))
            test_adsm = hDB.val_accord_hd(input_ID, "DS A Mod")
        else:
            test_a    = ""
            test_arnk = ""
            test_ads  = ""
            test_adsm = ""
            
        if hDB.val_accord_hd(input_ID, "Score B"):
            test_b    = "%2d"  % int(hDB.val_accord_hd(input_ID, "Score B"))
            test_brnk = hDB.val_accord_hd(input_ID, "Score B Rank")
            test_bds  = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS B"))
            # test1bdsm = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 1B Mod"))
            test_bdsm = hDB.val_accord_hd(input_ID, "DS B Mod")
        else:
            test_b    = ""
            test_brnk = ""
            test_bds  = "" 
            test_bdsm = ""

        if hDB.val_accord_hd(input_ID, "Validity A") == "Invalid":
            vld_A = "Invalid"
        elif test_adsm == "":
            vld_A = ""
        else:
            vld_A = "OK"
        if hDB.val_accord_hd(input_ID, "Validity B") == "Invalid":
            vld_B = "Invalid"
        elif test_bdsm == "":
            vld_B = ""
        else:
            vld_B = "OK"            

        if hDB.val_accord_hd(input_ID, "Presentation Days A"):
            presA = hDB.val_accord_hd(input_ID, "Presentation Days A")
        else:
            presA = "None"

        if hDB.val_accord_hd(input_ID, "Presentation Days B"):
            presB = hDB.val_accord_hd(input_ID, "Presentation Days B")
        else:
            presB = "None"


        m_replace = {
                     m_replace_SID   : input_ID, 
                     m_replace_NAME  : name,
                     m_replace_GNAM  : upper_first_char(hDB.val_accord_hd(input_ID, "Given Name")),
                     m_replace_Mail  : email,
                     m_replace_NACC  : " ".join(['*'] * (alog.get(input_ID, 0) + 1)),
                     m_replace_RNDM  : rd_messages[ randrange(0, len(rd_messages)) ].rstrip(),
                     m_replace_A     : test_a,
                     m_replace_ARNK  : test_arnk,
                     m_replace_ADS   : test_ads,
                     m_replace_ADSM  : test_adsm,
                     m_replace_B     : test_b,
                     m_replace_BRNK  : test_brnk,
                     m_replace_BDS   : test_bds,
                     m_replace_BDSM  : test_bdsm,

                     m_replace_VLDA  : vld_A,
                     m_replace_VLDB  : vld_B,

                     m_replace_PRSTA : presA,                   
                     m_replace_PRSTB : presB,

                    }
        
        message = open(rsc.BioBasic3_mail_IDOK_e1).read()
        message = replace_by_dict(message, m_replace)
        
        tmp_obj = TmpFile_II(message)
        if send_email:
            system("%s -s '%s' %s -f %s < %s" %
                   (MAIL_Command,
                    MAIL_Subject,
                    email,
                    MAIL_From,
                    tmp_obj.filename()))
        else:
            system("cat " + tmp_obj.filename())
        html_out = open(rsc.BioBasic3_html_IDOK, "r").read()
        html_out = replace_by_dict(html_out, m_replace)
        print cgi_html_out(html_out)

    else:
        m_replace = {
             m_replace_SID  : input_ID 
        }
        html_out = open(rsc.BioBasic3_html_IDNG, "r").read()
        html_out = replace_by_dict(html_out, m_replace)
        print cgi_html_out(html_out)

    alog[input_ID] = alog.get(input_ID, 0) + 1
    pickle.dump(alog, open(rsc.BioBasic3_Log, "w"))
    open(rsc.BioBasic3_LogTime, "a").write("\t".join((access_time, input_ID, name)) + '\n')

if __name__ == "__main__":
    
    auto_email(input_ID = "79151932", send_email = False)
    

