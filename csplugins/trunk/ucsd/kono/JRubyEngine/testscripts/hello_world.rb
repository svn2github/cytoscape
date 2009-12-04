$LOAD_PATH << ENV['JRUBY_HOME'] + '/lib/ruby/gems/1.8/gems/bio-1.3.1/lib'
puts "LOAD_PATH: " + $:.inspect


require 'csv'
require 'bio'
puts 'Hello, Cytoscape.'