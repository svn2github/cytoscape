require 'java'

include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.CyNetwork'
include_class 'cytoscape.CyNode'

net = Cytoscape.getCurrentNetwork
source =  net.nodesList

line = ','
source.each do |column|
  line << column.getIdentifier << ','
end

fileName = net.getTitle + "_matrix.csv"
File.open(fileName, "w") {|file|

  file.puts line
  
  source.each do |s|
    line = s.getIdentifier + ','
    targets = net.nodesList
    targets.each do |t|
      if net.edgeExists(s, t)
        line << '1,'
      else
        line << '0,'
      end
    end
    file.puts line
  end
}
