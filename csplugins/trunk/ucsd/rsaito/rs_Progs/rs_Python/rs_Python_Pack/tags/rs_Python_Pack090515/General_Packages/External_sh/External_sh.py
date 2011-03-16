#!/usr/bin/env python

import tempfile
import string
import os

class External_sh:
    def __init__(self, scriptfile = None):

        self.given_scriptfile = scriptfile
        if self.given_scriptfile:
            self.scriptfile = scriptfile
        else:
            self.scriptfile = tempfile.mktemp()
            self.sh_fileobj = open(self.scriptfile, "w")
            self.sh_fileobj.write(self.script.__doc__)
            self.sh_fileobj.flush()
            self.sh_fileobj.close()
            os.chmod(self.scriptfile, 0700)

        self.param = None

    def set_param(self, *param):
        self.param = param

    def __del__(self):
        if not self.given_scriptfile:
            os.remove(self.scriptfile)

    def output(self):

        if self.param:
            opencommand = self.scriptfile + " " +string.join(self.param, " ")
        else:
            opencommand = self.scriptfile
            
        fh = os.popen(opencommand, "r")
        res = []
        for line in fh:
            res.append(string.rstrip(line))
        fh.close()
        return res

    def script(self):
        r"""#!/usr/bin/perl -w 

use strict;

my $a = shift @ARGV;
my $b = shift @ARGV;
my $x1 = shift @ARGV;
my $x2 = shift @ARGV;
my $step = shift @ARGV;

for(my $x = $x1;$x <= $x2;$x += $step){

   print join("\t", $x, $a*$x + $b), "\n";

}

"""

if __name__ == "__main__":
    ext_scr1 = External_sh()
    ext_scr1.set_param("2", "1", "-10", "+10", "0.5")
    print ext_scr1.result()

    ext_scr2 = External_sh("./test1.sh")
    print ext_scr2.output()
    
