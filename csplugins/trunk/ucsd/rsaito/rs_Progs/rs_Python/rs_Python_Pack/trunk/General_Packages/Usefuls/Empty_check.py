#!/usr/bin/env python

def notEmpty_hash(hash, ikey):
    if ikey not in hash:
        return False
    if hash[ikey] == "":
        return False
    if hash[ikey].isspace():
        return False

    return True

def notEmpty_cgi_form(form, ikey):
    if ikey not in form:
        return False
    if form[ikey].value == "":
        return False
    if form[ikey].value.isspace():
        return False

    return True

if __name__ == "__main__":
    hash = { "A": "", "B": "  \t", "C": "XXX" }
    
    print notEmpty_hash(hash, "A")
    print notEmpty_hash(hash, "B")
    print notEmpty_hash(hash, "C")
    print notEmpty_hash(hash, "X")