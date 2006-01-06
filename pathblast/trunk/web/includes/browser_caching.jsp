<%
response.setHeader("Pragma", "cache");
java.util.Date halfanhour = new java.util.Date(System.currentTimeMillis()+1800000);
response.setHeader("Expires",halfanhour.toString());
response.setHeader("cache-control", "public");
%>
