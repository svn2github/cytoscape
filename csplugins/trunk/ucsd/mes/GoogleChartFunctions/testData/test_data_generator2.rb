# For Pie chart
File.open("galFilteredDoubleListsPie2.txt", "w") {|out|
  File.open("galNodeIDs.txt") {|file|
    while line = file.gets
      result = line.chop
      for j in 1..3 do
        line_data = ""
        num_area = rand.to_s[2,2];
        max = num_area[1,1].to_i;
#        p "MAX = " +max.to_s;
        
        vals = Array.new
        labels = Array.new
        sum = 0;
        
        listSize = max+2;
        for i in 1..max+2
          val = rand;
          sum+=val
          vals << val;
          labels << "Type " + i.to_s
        end
        
        sum_percent = 0;
        labelData = ""
        last = 0
        for i in 0..vals.size-2
          current_value = ((vals[i]/sum)*100).to_i;
          line_data+=current_value.to_s + ","
          sum_percent+=current_value
          
          labelData+=labels[i] + ","
           last = i
        end
        
        line_data+=(100-sum_percent).to_s
        labelData+=labels[last+1]
  
        result += "\t" + line_data + "\t" + labelData
      end
#      p result
      out.puts(result)
    end
  }
}