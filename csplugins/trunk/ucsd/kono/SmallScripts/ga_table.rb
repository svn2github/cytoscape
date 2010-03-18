require "uri"

File.open("ga_table.xml", "w") {|out|
  File.open("gaTableOriginal.txt") {|file|
    while line = file.gets
      entry = line.split(/>/)
      entryStr = ""
      if entry[0] == "\<tr" then
        if /class="spp"/.match(line) then
          parts = line.split(/"spp\">/)
          test = parts[1].sub(/<\/span>/, "")
          entryStr = test.split(/<\/td>/)[0]

        else
          parts = line.split(/<td>/)
          entryStr = parts[1].split(/<\/td>/)[0]
        end

        oneEntry = entryStr.sub(/&nbsp;/, " ")
        cell = oneEntry.split(/<br>/)
        p cell[0] + " = " + cell[1]

      else
        uris = URI.extract(line)
        if uris.size == 1 then
          targeturi = uris[0].split(/\?/)[0]
          p targeturi
          out.puts("<dataSource format=\"Gene Association\"\n\tname=\"Gene Association file for " +
          cell[0] + "\"" +
          "\n\txlink:href=\"" + targeturi + "\">" +
          "\n\t<attribute name=\"curator\">" + cell[1] + "</attribute>" +
          "\n\t<attribute name=\"species\">" + cell[0] + "</attribute>" +
          "\n</dataSource>")
        end
      end

    end
  }
}