%Runs k-means on the given set of points the specified number of trials 
%for the given k value.  Returns an array containing the centers from the 
%best run as well as item group membership.

% Usage: run_kmeans(points, num_clusters, trials)
%   points = the set of points to partition with k-means
%   num_clusters = the value of k to use
%   trials = the number ot times to run k-means
%
% Returns:
%   centers - the centers fromthe best run of k-means.
%   indices - the indices of the point membership from the best run of
%   k-means.

function [centers, indices] = run_kmeans(points, num_clusters, trials)

indices = [];
centers = [];
error = Inf;

for i=1:trials
    
    [IDX,C,sumd] = kmeans(points, num_clusters,'emptyaction', 'singleton');
    
    if sumd < error
        indices = IDX;
        centers = C;
        error = sumd;
    end
    
end