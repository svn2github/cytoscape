$(document).ready(function()  {
	// Linking back:
	$('#link_us').click(function(evt) {
		$('a[name="Linking back to Cytoscape Web"]').click();
		return false;
	});	
	
	// Client applications - screen shots:
	var border = { width: 0, radius: 2, color: "#000000" };
	
	$('#link_genemania').qtip({
		content: '<img src="/img/clients/genemania.png" alt="GeneMANIA" />',
		style: { border: border, width: 390, height: 298, padding: 0 }
	});
	$('#link_irefweb').qtip({
		content: '<img src="/img/clients/irefweb.png" alt="iRefWeb" />',
		style: { border: border, width: 334, height: 318, padding: 0 }
	});
	$('#link_pathguide').qtip({
		content: '<img src="/img/clients/pathguide.png" alt="Pathguide" />',
		style: { border: border, width: 350, height: 294, padding: 0 }
	});
});