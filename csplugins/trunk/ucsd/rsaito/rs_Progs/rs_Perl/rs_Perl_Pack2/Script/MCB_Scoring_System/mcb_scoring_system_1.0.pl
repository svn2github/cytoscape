#!/usr/bin/perl -w

use strict;
my $save_file    = shift @ARGV;
my $list_file    = shift @ARGV;
my $answer_file  = shift @ARGV;
my $sheet_file   = shift @ARGV;
my @bonus_files  = @ARGV;

my $header; 
my $list_data;
##################################################
my $n_list=0;
open(LIST,$list_file);
while(<LIST>){
    chomp;
    if($n_list<5){
	$header .= "$_\n";
    }elsif($n_list>=7){
	my @array = split(/\,/,$_);
	$list_data->{$array[0]} = 1;
    }
    $n_list++;
}
close LIST;
##################################################


my $answer_data;
my $packed_data;
##################################################
open(ANSWER, $answer_file);
while(<ANSWER>){
    chomp;

    if($_ =~ /^(\d+)\=(\d+)/){
	$answer_data->{$1} = $2;
    }

    if($_ =~ /^\((\d+\,\d+.*)\)$/){
	$packed_data->{$1} = 1;
    }
}
close ANSWER;
##################################################

my $sheet_data;
my $analysis_data;
##################################################
open(SHEET, $sheet_file);
while(<SHEET>){
    chomp;
    if($_ =~ /^\d+\,/){
	my @array = split(/\,/,$_);
	my $student_num = $array[1];
	
	##########
	my $exist = 0;
	if(exists $list_data->{$student_num}){
	    $exist = 1;
	}else{
	    delete $list_data->{$student_num};
	}
	##########

	my $SCORE = 0;

	my $answer;
	my $boolean;
	my $answer_result;

	##########
	while(my ($Q, $A) = each %$answer_data){

	    ##########
	    if($array[$Q+1] eq $A){
		$boolean->{$Q}       = 1;
		$answer_result->{$Q} = "o";

		##########
		$analysis_data->{correct_num}->{$Q}++;
		##########

	    }else{
		$boolean->{$Q}       = 0;
		$answer_result->{$Q} = "x";
	    }
	    ##########
	    
	    $answer->{$Q} = $array[$Q+1];
	    $analysis_data->{answer}->{$Q}->{"$array[$Q+1]"}++;
	}
	##########

	##########
	for my $pack_info (keys %$packed_data){
	    my @pack_array = split(/\,/,$pack_info);

	    my $pack_score = 1;
	    for my $p (@pack_array){
		$pack_score *= $boolean->{$p};
		$boolean->{$p} = 0;
	    }
	    $SCORE += $pack_score;
	}
	while(my($Q,$value) = each(%$boolean)){
	    $SCORE += $value;
	}
	##########

	$sheet_data->{$student_num}->{exist}         = $exist;
	$sheet_data->{$student_num}->{answer}        = $answer;
	$sheet_data->{$student_num}->{answer_result} = $answer_result;
	$sheet_data->{$student_num}->{SCORE}         = $SCORE;
    }
}
close SHEET;

$analysis_data->{correct} = $answer_data;
##################################################

($sheet_data,$analysis_data) = &Deviation_and_Analysis($sheet_data,$analysis_data);

##################################################
for my $b_file (@bonus_files){
    open(BONUS,$b_file);
    while(<BONUS>){
	chomp;
	if($_ =~ /^\d+\,/){
	    my @array = split(/\,/,$_);
	    my $student_num = $array[1];
	    
	    if(exists $sheet_data->{$student_num}->{deviation}){
		$sheet_data->{$student_num}->{deviation}++;
	    }else{
		delete $sheet_data->{$student_num};
	    }
	}
    }
    close BONUS;
}
##################################################


##################################################
while(my ($student_num,$value) = each %$sheet_data){

    my $grade = '';
    my $deviation = $value->{deviation};

    if($value->{exist}){
	$grade = 'C';
	$grade = 'B' if($deviation >= 40);
	$grade = 'A' if($deviation >= 60);
    }
    $sheet_data->{$student_num}->{grade}     = $grade;
}
##################################################

##################################################
my $deviation_to_rank;

my @deviation_array      = ();
my @deviation_line_array = ();

for my $value (values %$sheet_data){
    if($value->{exist}){
	push(@deviation_array, $value->{deviation});
    }
}
@deviation_array = sort{$b<=>$a} @deviation_array;

my $n_rank = 0;
for my $deviation (@deviation_array){
    $n_rank++;
    push(@deviation_line_array,"$deviation,$n_rank");
}

for my $deviation_line (reverse @deviation_line_array){
    my @array = split(/\,/,$deviation_line);
    $deviation_to_rank->{"$array[0]"} = $array[1];
}

while(my ($student_num,$value) = each %$sheet_data){

    my $rank = '';

    if($value->{exist}){
	my $deviation = $value->{deviation};
	$rank = $deviation_to_rank->{$deviation};
    }
    $sheet_data->{$student_num}->{rank} = $rank;
}
##################################################






##################################################
open(RESULT,">result\.$save_file\.csv");

print RESULT "$header\n";
print RESULT "student_number\,score1\,ds1\,score2\,ds2\,result\n";
for my $student_number (sort{$a <=> $b} keys %$sheet_data){
    my $value = $sheet_data->{$student_number};
    
    if($value->{exist}){
	my $SCORE = $value->{SCORE};
	my $deviation = $value->{deviation};
	
	print RESULT "$student_number\,$SCORE\,$deviation\n";
    }
}
close RESULT;
##################################################

##################################################
open(ANALYSIS,">analysis\.$save_file\.csv");

my @Q_array = sort{$a <=> $b} keys %$answer_data;
print ANALYSIS 'student_number,enrolled';
for my $Q (@Q_array){
    print ANALYSIS "\,$Q";
}
print ANALYSIS ',score,deviation,rank,grade';
print ANALYSIS "\n";

my $count_grade;

for my $student_number (sort{$a <=> $b} keys %$sheet_data){
    my $value = $sheet_data->{$student_number};
    
    if($value->{exist} eq '1'){
	print ANALYSIS "$student_number\,$value->{exist}";
	for my $Q (@Q_array){
	    my $answer        = $value->{answer}->{$Q};
	    my $answer_result = $value->{answer_result}->{$Q};

	    print ANALYSIS "\,$answer\:$answer_result";
	}
	print ANALYSIS "\,$value->{SCORE}";
	print ANALYSIS "\,$value->{deviation}";
	print ANALYSIS "\,$value->{rank}";
	print ANALYSIS "\,$value->{grade}";
	print ANALYSIS "\n";

	$count_grade->{$value->{grade}}++;
    }
}

for my $student_number (sort{$a <=> $b} keys %$sheet_data){
    my $value = $sheet_data->{$student_number};

    if($value->{exist} eq '0'){
	print ANALYSIS "$student_number\,$value->{exist}";
	for my $Q (@Q_array){
	    my $answer        = $value->{answer}->{$Q};
	    my $answer_result = $value->{answer_result}->{$Q};
	    print ANALYSIS "\,$answer\:$answer_result";
	}
	print ANALYSIS "\,$value->{SCORE}";
	print ANALYSIS "\n";
    }
}

for my $tag ('correct','correct_num','mode'){
    print ANALYSIS "$tag\,";
    for my $Q (@Q_array){
	print ANALYSIS "\,$analysis_data->{$tag}->{$Q}";
    }
    print ANALYSIS "\n";
}

print ANALYSIS "question_number\,";
for my $Q (@Q_array){
    print ANALYSIS "\,$Q";
}
print ANALYSIS "\n";
print ANALYSIS "\n";
print ANALYSIS "average\,$analysis_data->{average}\n";
print ANALYSIS "sd\,$analysis_data->{sigma}\n";
print ANALYSIS "\n";
my $total = 0;
for my $grade ('A','B','C'){
    my $num = $count_grade->{$grade} || 0;
    print ANALYSIS "$grade\,$num\n";
    $total += $num;
}
print ANALYSIS "total\,$total\n";

close ANALYSIS;
##################################################





##################################################
sub Deviation_and_Analysis(){
    my $sheet_data = shift;
    my $analysis_data = shift;

    ##########
    my $sum     = 0;
    my $n       = 0;
    my $average = 0;
    my $sigma   = 0;

    for my $value (values %$sheet_data){
	if($value->{exist}){
	    $n++;
	    $sum += $value->{SCORE};
	}
    }
    $average = $sum/$n;

    
    for my $value (values %$sheet_data){
	if($value->{exist}){
	    $sigma += ($value->{SCORE} - $average)**2;
	}
    }
    $sigma /= $n;
    $sigma **= 1/2;

    while(my ($student_num,$value) = each %$sheet_data){
	my $deviation = '';
	if($value->{exist}){
	    $deviation = ($value->{SCORE} - $average) / $sigma * 10 + 50;
	}
	$sheet_data->{$student_num}->{deviation} = $deviation;
    }
    ##########
    
    ##########
    while(my ($Q,$value) = each %{$analysis_data->{answer}}){
	my $min  = 0;
	my $mode = '';
	while(my ($A,$A_num) = each %$value){
	    if($A_num > $min){
		$mode = $A;
		$min = $A_num;
	    }
	}
	$analysis_data->{mode}->{$Q} = $mode;
    }
    ##########

    $analysis_data->{average} = sprintf "%.2f", $average;
    $analysis_data->{sigma}   = sprintf "%.2f", $sigma;

    return ($sheet_data,$analysis_data);
}
