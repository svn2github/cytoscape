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

sub complemental($){ 
  my $seq = $_[0]; 
  my $complement; 
  $complement = reverse($seq);
  $complement =~ tr/ACGTacgt/TGCAtgca/;
  return $complement; 
}

my @cds_start_set; # @cds_start_setにCDS開始位置を記録していく
my @cds_end_set;   # @cds_end_setにCDS終了位置を記録していく
my @complement;   # 相補的配列なら1,そうでなければ0を記録していく
my $cds_count = 0; # CDSの数を記録
my $seq; # 塩基配列

open(FILE, $ARGV[0]) || die "Cannot open \"$ARGV[0]\": $!\n";

while(<FILE>){ # [[1]]
  chomp; # 行末端の改行記号を消す
  if($_ =~ /^     CDS             ([0-9]+)\.\.([0-9]+)/){
    # [[3]]
    push(@cds_start_set, $1);
    push(@cds_end_set,   $2);
    push(@complement, 0);
    $cds_count ++;
  }
  elsif($_ =~ /^     CDS             complement\(([0-9]+)\.\.([0-9]+)\)/){
    # [[3]]
    push(@cds_start_set, $1);
    push(@cds_end_set,   $2);
    push(@complement, 1);
    $cds_count ++;
  }
  elsif($_ =~ /^ORIGIN/){
    # [[4,5]]
    $seq = &get_sequence(*FILE);
  }
}

close FILE;

for my $ncds (0..$cds_count - 1){
  my $cds_seq;
  if($complement[$ncds] == 0){
    $cds_seq = substr($seq,
                      $cds_start_set[ $ncds ] - 1,
                      $cds_end_set[ $ncds ] - $cds_start_set[ $ncds ] + 1);
  }
  else {
    $cds_seq = complemental(
      substr($seq,
             $cds_start_set[ $ncds ] - 1,
             $cds_end_set[ $ncds ] - $cds_start_set[ $ncds ] + 1));
  }
  print "$ncds\t$complement[$ncds]\t$cds_seq\n";
  
}

