File.open("galFilteredDoubleLists.txt", "w") {|out|
  File.open("galFilteredNodeIDs.txt") {|file|
    while line = file.gets
      put line
    end
  }
}