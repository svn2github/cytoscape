#!/usr/bin/perl

open(FILE, "textfile");

while(<FILE>){
   $lines[$i] = $_;
   $i = $i + 1;
}

for($j = 0;$j < $i;$j ++){

   print $lines[ $i - $j ];

}

