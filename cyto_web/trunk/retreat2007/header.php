<table id="adambanner" summary="" border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr >
      <td>
      <a href="http://www.cytoscape.org" target="_blank"><img src="images/cyto_icon100.png" width=100 height=100 border="0" align="left" alt="Cytoscape" /></a>
      </td>
      <td >
      <img src="images/cytoretreattitle.png" width="100%" height=100 border="0" align="center"/>
      </td>
    </tr>
  </tbody>
</table>

<script language="javascript">
   window.onload = function() {
       	
       var bimages = ["0-banner.jpg", "1-banner.jpg", "2-banner.jpg", "3-banner.jpg", "4-banner.jpg", "5-banner.jpg"];
       var rand=Math.floor(Math.random()*6);
       newImage = "url(images/banner/" + bimages[rand] + ")";
       document.getElementById('adambanner').style.backgroundImage = newImage;
   }      
</script>
