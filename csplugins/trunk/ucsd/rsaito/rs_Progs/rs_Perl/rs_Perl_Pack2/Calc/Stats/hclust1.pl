#!/usr/bin/env perl

use strict;
use warnings;
use List::Util qw(sum);

# n次元データa, bの相関係数を計算する
sub corr_single($$){
    my $a = shift;
    my $b = shift;
    my $n = $#$a + 1;

    my $mean_a = sum(@$a) / ($#$a + 1);
    my $mean_b = sum(@$b) / ($#$b + 1);

    my $var_a = 0;
    my $var_b = 0;

    foreach(0..$n-1){ $var_a += ($a->[$_] - $mean_a)**2 / $n; }
    foreach(0..$n-1){ $var_b += ($b->[$_] - $mean_b)**2 / $n; }

    my $corr = 0;

    foreach(0..$n - 1){
	$corr += (($a->[$_] - $mean_a) / ($var_a ** 0.5))
	       * (($b->[$_] - $mean_b) / ($var_b ** 0.5)) / $n;
    }

    return $corr;
}

# クラスターa, bの要素間の相関を計算する
sub corr_clust($$$){
    my $clust_a = shift;
    my $clust_b = shift;
    my $d = shift;

    my $max = -1.0;
    for my $i (@$clust_a){
	for my $j (@$clust_b){
	    if($max < $d->[ $i ]->[ $j ]){
		$max = $d->[ $i ]->[ $j ];
	    }
	}
    }
    
    return $max;
}

# クラスターa, bを統合し、結果をクラスターnewに入れる
sub merge($$){
    my $cluster_a = shift;
    my $cluster_b = shift;
    my @cluster_merged = (@$cluster_a, @$cluster_b);

    return \@cluster_merged;
}


# 次元数dimのデータ群dataを階層的クラスタリング
sub h_cluster($){
    my $data = shift; # データセット。$data->[データ番号]->[データの次元番号]
    my @cl;           # クラスター情報。$cl[クラスター番号] = [ データ番号,,, ]

    # クラスター情報の初期化
    foreach(0..$#$data){ $cl[$_] = [ $_ ]; }

    # 相関行列の作成
    my @d;
    for my $i (0..$#$data){
	for my $j (0..$#$data){
	    $d[$i]->[$j] = corr_single($data->[$i], $data->[$j]);
	}
    }

    # 階層的クラスタリング
    while ($#cl > 0){
	my $corr_max = -1.0;
	my($c1, $c2);
	for (my $i = 0; $i < $#cl; $i ++){
	    for (my $j = $i + 1; $j <= $#cl; $j ++){
		my $corr = corr_clust($cl[$i], $cl[$j], \@d);
		if ($corr > $corr_max){
		    $corr_max = $corr;
		    $c1 = $i;
		    $c2 = $j;
		}
	    }
	}

	printf("[ Merge %d ] Merging the following two clusters:\n",
	       $#$data - $#cl + 1);
	printf("(1) Cluster containing data #");
	print join(",", @{$cl[$c1]}), "\n";
	printf("(2) Cluster containing data #");
	print join(",", @{$cl[$c2]}), "\n";
	printf("Distance between these two clusters = %lf\n\n", $corr_max);
	$cl[ $c1 ] = merge($cl[ $c1 ], $cl[ $c2 ]);
           # クラスターc2をc1へ統合して格納
	splice(@cl, $c2, 1) # クラスターc2を破棄

    }
}

h_cluster([[1,2,3,4,5],
	   [7,5,3,1,3],
	   [2,3,4,2,3],
	   [5,6,7,8,9]]);

