package cytoscape.filters;


//Advanced settings
public class AdvancedSetting {
	// default settings
	private boolean session = true, global=false; //scope
	private boolean node=true, edge=true; // selectionType
	private boolean source=true, target=true; // interactionType
	private boolean relationAND = true;
	private boolean relationOR = false;
	private Relation relation = Relation.AND;
		
	public String toString() {
		String retStr = "";
		retStr += "AdvancedSetting.scope.global = ";
		if (global) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		retStr += "AdvancedSetting.scope.session = ";
		if (session) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		retStr += "AdvancedSetting.selection.node = ";
		if (node) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		retStr += "AdvancedSetting.selection.edge = ";
		if (edge) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		retStr += "AdvancedSetting.interaction.source = ";
		if (source) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		retStr += "AdvancedSetting.interaction.target = ";
		if (target) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		
		retStr += "AdvancedSetting.relation.AND = ";
		if (relationAND) {
			retStr += "true\n";
		}
		else {
			retStr += "false\n";
		}
		retStr += "AdvancedSetting.relation.OR = ";
		if (relationOR) {
			retStr += "true";
		}
		else {
			retStr += "false";
		}

		return retStr;
	}

	public Relation getRelation()
	{
		return relation;
	}
	
	public void setRelation(Relation pRelation)
	{
		relation = pRelation;
	}

	
	// util
	public boolean isSessionChecked()
	{
		return session;
	}
	public void setSession(boolean pSession)
	{
		session = pSession;
	}
	
	public boolean isGlobalChecked()
	{
		return global;
	}
	public void setGlobal(boolean pGlobal)
	{
		global = pGlobal;
	}

	public boolean isNodeChecked()
	{
		return node;
	}
	public void setNode(boolean pNode)
	{
		node = pNode;
	}
	public boolean isEdgeChecked()
	{
		return edge;
	}
	public void setEdge(boolean pEdge)
	{
		edge = pEdge;
	}
	public boolean isSourceChecked()
	{
		return source;
	}
	public void setSource(boolean pSource)
	{
		source = pSource;
	}
	public boolean isTargetChecked()
	{
		return target;
	}
	public void setTarget(boolean pTarget)
	{
		target = pTarget;
	}
	public boolean isANDSelected()
	{
		return relationAND;
	}
	public void setRelationAND(boolean pRelationAND)
	{
		relationAND = pRelationAND;
	}
	public boolean isORSelected()
	{
		return relationOR;
	}
	public void setRelationOR(boolean pRelationOR)
	{
		relationOR = pRelationOR;
	}
}//End of Advanced settings
