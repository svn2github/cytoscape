%This script takes in a name of a file containt test_output and
%vocab_output as well as a value of k (for k-means) and a value of n
%(number of dimensions for LSI reduction) and prints out the best groups of
%words obtained.

% Usage: LSIGroupWords(file,k,n)
%
% Returns:
%   just prints stuff to screen

function [] = LSIGroupWords(fileName,k,n)

load (fileName);
[U,S,V] = svd(test_output);

%Just pick a value
num_trials = 100;

%Get updated versions of matrices
%U_new = U(:,1:n);
%S_new = S(1:n,1:n);
%V_new = V(:,1:n);
U_new = U;

[centers, indices] = run_kmeans(U_new,k,num_trials);

for i=1:k
   ind = find(indices == i); 
   
   %Print group word information
   fprintf('Group %d words: \n',i);
   vocab_output(ind)
   
end




