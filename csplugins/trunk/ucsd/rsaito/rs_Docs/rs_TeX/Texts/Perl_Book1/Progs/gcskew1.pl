#!/usr/bin/perl -w

use strict;

sub get_sequence {

  my($fh) = $_[0]; #ファイルハンドル
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
      # $seq = $seq . $seq_fragだとかなり遅くなるので注意
    }
  }

  return $seq;

}

sub calc_gc {
  my $seq_frag = $_[0];
  return $seq_frag =~ tr/cg/cg/;
  
}

local *FH;
my $gbk_file = shift @ARGV;
open(FH, $gbk_file) || die "Cannot open \"$gbk_file\": $!";
my $seq = "";
while(<FH>){
  chomp;
  if(/^ORIGIN/){
    $seq = get_sequence(*FH); 
  }
}
close FH;

my $win_size = 1000;
my $step = 500;
for(my $pos = 0;$pos + $win_size <= length($seq); $pos += $step){
  my $seq_frag = substr($seq, $pos, $win_size);
  my $gc_skew = calc_gc($seq_frag);
  print "$pos\t$gc_skew\n";
}

