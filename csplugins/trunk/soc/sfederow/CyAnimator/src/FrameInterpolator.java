package src;

import java.util.List;


public interface FrameInterpolator {
	
	public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int end, CyFrame[] cyFrameArray);
	
}
