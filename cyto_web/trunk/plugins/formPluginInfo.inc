<?php
/*
 * Created on Nov 29, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
 ?>
  <table width="878" border="0">
  <tr>
    <td width="208"><div align="right" ><span class="style4">*</span>Plugin name</div></td>
    <td width="660"><input name="tfName" type="text" value ="<?php echo $name ?>" size="40" /></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>version</div>      </td>
    <td><input name="tfVersion" type="text" id="tfVersion" value ="<?php echo $version ?>" size="20" /></td>
  </tr>
  <tr>
    <td height="75"><div align="right"><span class="style4">*</span>Description</div></td>
    <td><textarea name="taDescription" cols="80" rows="5" id="taDescription"><?php echo $description ?></textarea></td>
  </tr>
    <tr>
    <td><div align="right"><span class="style4">*</span>Category</div></td>
    <td><label>
      <select name="optCategory" id="optCategory" >

        <option <?php if ($category && $category == 'Please choose one') echo 'selected' ?>>Please choose one</option>	
        <?php if ($mode == 'edit'){ echo '<option>Core</option>';} ?>
        <option <?php if ($category && $category == 'Analysis') echo 'selected' ?>>Analysis</option>
        <option <?php if ($category && $category == 'Network and Attribute I/O') echo 'selected' ?>>Network and Attribute I/O</option>
        <option <?php if ($category && $category == 'Network Inference') echo 'selected' ?>>Network Inference</option>
        <option <?php if ($category && $category == 'Functional Enrichment') echo 'selected' ?>>Functional Enrichment</option>
        <option <?php if ($category && $category == 'Communication/Scripting') echo 'selected' ?>>Communication/Scripting</option>
		<option <?php if ($category && $category == 'Other') echo 'selected' ?>>Other</option>
      </select>
    </label></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>Release Date </div></td>
    <td><table width="118" border="0">
        <tr>
          <td width="32" scope="col"><div align="center">mm</div></td>
          <td width="29" scope="col"><div align="center">dd</div></td>
          <td width="57" scope="col"><div align="center">yyyy</div></td>
        </tr>
        <tr>
          <td><input name="tfMonth" type="text" id="tfMonth" value ="<?php echo $month ?>" size="2" /></td>
          <td><input name="tfDay" type="text" id="tfDay" value ="<?php echo $day ?>" size="2" /></td>
          <td><input name="tfYear" type="text" id="tfYear" value ="<?php echo $year ?>" size="4" /></td>
        </tr>
      </table>      </td>
  </tr>
  <tr>
    <td><div align="right"></div></td>
    <td><label></label></td>
  </tr>
  
  <tr>
    <td width="208"><div align="right" ><span class="style4">*</span>Cytoscape versions</div></td>
    <td width="660"><input name="tfCyVersion" type="text" value ="<?php echo $cyVersion ?>" size="40" /></td>
  </tr>

  
  
  <tr>
    <td><div align="right"></div></td>
    <td><label></label></td>
  </tr>
  <tr>
    <td><div align="right">Author(s)</div></td>
    <td><table width="660" border="0">
      <tr>
        <td width="444"><div align="center"> Name(s)</div></td>
        <td width="206"><?php //<div align="center">contact e-mail (not made public) </div> ?></td> 
      </tr>
      <tr>
        <td><label>
          <input name="tfNames0" type="text" id="tfNames0" size="70" value ="<?php echo htmlentities(stripslashes($names[0])) ?>" />
        </label></td>
        <td> 

        <input name="tfEmail0" type="Hidden" id="tfEmail0" size="30" value ="<?php echo $emails[0] ?>" />
        
        </td>
        </tr>
      <tr>
        <td><div align="center">Affiliation</div></td>
        <td><div align="center">Affiliation URL</div></td>
        </tr>
      <tr>
        <td><input name="tfAffiliation0" type="text" id="tfAffiliation0" size="70" value ="<?php echo htmlentities(stripslashes($affiliations[0])) ?>" /></td>
        <td><input name="tfAffiliationURL0" type="text" id="tfAffiliationURL0" size="30" value ="<?php echo $affiliationURLs[0] ?>" /></td>
      </tr>
    </table></td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td><table width="660" border="0">
      <tr>
        <td width="444"><div align="center"> Name(s)</div></td>
        <td width="206"><?php //<div align="left">contact e-mail </div>?></td>
      </tr>
      <tr>
        <td><label>
        <input name="tfNames1" type="text" id="tfNames1" size="70" value ="<?php if (isset($names[1])){ echo htmlentities(stripslashes($names[1]));} ?>" />
        </label></td>
        <td><input name="tfEmail1" type="Hidden" id="tfEmail02" size="30" value ="<?php if (isset($emails[1])){ echo $emails[1];} ?>" /></td>
      </tr>
      <tr>
        <td><div align="center">Affiliation</div></td>
        <td><div align="center">Affiliation URL</div></td>
      </tr>
      <tr>
        <td><input name="tfAffiliation1" type="text" id="tfAffiliation1" size="70" value ="<?php if (isset($affiliations[1])){echo htmlentities(stripslashes($affiliations[1]));} ?>" /></td>
        <td><input name="tfAffiliationURL1" type="text" id="tfAffiliationURL1" size="30" value ="<?php if (isset($affiliationURLs[1])){ echo stripslashes($affiliationURLs[1]);} ?>" /></td>
      </tr>
    </table></td>
  </tr>
  
  <tr>  
<td>&nbsp;</td>
<td>&nbsp;</td>
  </tr>

  <tr>
    <td><div align="right"><?php if (isset($isPluginURLSubmission)) {echo '<span class="style4">*</span>'; }; ?>Project URL</div></td>
    <td><input name="tfProjectURL" type="text" id="tfProjectURL" value ="<?php echo $projectURL ?>" size="80" /></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><input name="tried" type="hidden" id="tried" value="yes">
      <input name="versionID" type="hidden" id="versionID" value="<?php echo $versionID; ?>"></td>
  </tr>
</table>
 
<?php
?>
