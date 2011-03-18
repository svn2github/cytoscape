#!/usr/bin/perl -w

use strict;

# ここにget_sequence, save_sequenceなどの関数を置く

sub get_sequence {

   my($fh) = @_; #ファイルハンドル
   my $seq = "";
   my $seq_frag;

   while(<$fh>){
      if($_ =~ /^\/\//){ # //を見つけたら、そこで終了
         last;
      }
      else {
         $seq_frag = $_; #読みこんだ行の塩基配列を$seq_fragに格納する。
         $seq_frag =~ s/[^a-z]//g;  # 数字、空白などは削除。
         $seq .= $seq_frag; # $seqに配列全体を格納
      }
   }
   return $seq;
}

sub save_sequence {

    my($filename, $fh) = @_;
    my($seq_frag);
    local(*SEQFILE);

    open(SEQFILE, "> $filename");
       # $filenameという名前のファイルを書きこみ用にオープンする

    while(<$fh>){
        if($_ =~ /^\/\//){
            last;
        }
        else {
            $seq_frag = $_;
            $seq_frag =~ s/[^a-z]//g;
            print SEQFILE $seq_frag;
        }
    }

    close SEQFILE;
}


my(@cds_start_set, $cds_start); # @cds_start_setに開始位置を記録していく
my($cds_count, $atg_count); # CDSの数，atgで始まるCDSの数をカウント
open(FILE, $ARGV[0]) || die "Cannot open \"$ARGV[0]\": $!\n";

$cds_count = 0;
$atg_count = 0;

while(<FILE>){ # [[1]]
   chomp; # 行末端の改行記号を消す
   if($_ =~ /^LOCUS/){
      # [[2]]
      @cds_start_set = ();
   }
   elsif($_ =~ /^     CDS             ([0-9]+)\.\.([0-9]+)/){
      # [[3]]
      $cds_start = $1;
      # $cds_end   = $2;
      push(@cds_start_set, $cds_start);
      $cds_count ++;
   }
   elsif($_ =~ /^ORIGIN/){
      # [[4,5]]
      my $seq = &get_sequence(*FILE);
      for $cds_start (@cds_start_set){
         if(substr($seq, $cds_start - 1, 3) eq "atg"){
            $atg_count ++;
         }
      }
   }
}

close FILE;

print "$atg_count / $cds_count = ", 1.0*$atg_count / $cds_count, "\n";
