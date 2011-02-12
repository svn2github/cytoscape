session open file=test.cys
commandTool sleep duration=5
layout force-directed
commandTool pause message="Press OK when ready to exit"
exit
