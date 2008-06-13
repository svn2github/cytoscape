distro:
	@echo ------------------------
	@echo Setting up...
	@echo ------------------------
	rm -rf sawd-project-distro
	rm -f sawd-project-distro.tar.gz
	mkdir sawd-project-distro
	@echo ------------------------
	@echo SawdServer...
	@echo ------------------------
	ant -f SawdServer/build.xml clean jar
	rm -r SawdServer/build
	cp -r SawdServer sawd-project-distro
	@echo ------------------------
	@echo SawdVisualizer
	@echo ------------------------
	ant -f SawdVisualizer/build.xml clean jar
	rm -r SawdVisualizer/build
	cp -r SawdVisualizer sawd-project-distro
	@echo ------------------------
	@echo SawdPinnacleZ
	@echo ------------------------
	ant -f SawdPinnacleZ/build.xml clean jar
	rm -r SawdPinnacleZ/build
	cp -r SawdPinnacleZ sawd-project-distro
	@echo ------------------------
	@echo Scripts
	@echo ------------------------
	cp sawdclient.py sawd-project-distro
	cp force_layouts/force_layout.py sawd-project-distro
	cp load_graph.py sawd-project-distro
	cp sample1.txt sawd-project-distro
	cp sample2.txt sawd-project-distro
	cp sample3.txt sawd-project-distro
	cp readme/readme.pdf sawd-project-distro
	@echo ------------------------
	@echo Finishing up
	@echo ------------------------
	tar -zcf sawd-project-distro.tar.gz sawd-project-distro
