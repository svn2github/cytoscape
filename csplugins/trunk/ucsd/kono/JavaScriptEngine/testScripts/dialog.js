importPackage( Packages.javax.swing );
importPackage( Packages.java.lang );
importPackage( Packages.cytoscape );

dialog = new JDialog();
dialog.setAlwaysOnTop(true);
dialog.setTitle("JavaScript Output");

editor = new JEditorPane();
editor.setContentType("text/html");
builder = new StringBuilder();
builder.append("<html><body>");
builder.append("<h4><font color=\"#ff0000\"> Status of Current Network: " + Cytoscape.getCurrentNetwork().getTitle() + "</font></h4>");
builder.append("<ul><li>" + "Number of Nodes = " + Cytoscape.getCurrentNetwork().nodesList().size() + "</li>");
builder.append("<li>" + "Number of Edges = " + Cytoscape.getCurrentNetwork().edgesList().size());
builder.append("</li></ul>");
builder.append("</body></html>");

editor.setText(builder.toString());

dialog.add(editor);

dialog.pack();
dialog.setSize(300, 150);
dialog.setLocationRelativeTo(Cytoscape.getDesktop());
dialog.setVisible(true);