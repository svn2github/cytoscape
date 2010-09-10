<?php
/*
 * Created on Nov 29, 2007
 * Content within the form of user input
 */
 
 ?>

  <table width="878" border="0">
  <tr>
    <td width="208"><div align="right"></div></td>
    <td width="660"><label><?php if ($mode == 'edit') {echo 'If no file is selected, the existing one will be kept as is.'; } ?></label></td>
  </tr>
  <?php 
  if (!isset($isPluginURLSubmission)) {
	?>  
	  <tr>
		<td><div align="right"><span class="style4">*</span>Jar/Zip File </div></td>
		<td><input name="filePlugin" type="file" id="filePlugin" size="80" /></td>
	  </tr>
  	<?php
  }
  ?>
  <tr>
    <td><div align="right"></div></td>
    <td><label></label></td>
  </tr>
  <tr>
    <td><div align="right">Contact</div></td>
    <td><table width="660" border="0">
      <tr>
        <td width="420"><div align="center"> Name</div></td>
        <td width="230"><div align="center"><span class="style4">*</span>e-mail (not made public) </div></td>
      </tr>
      <tr>
        <td><label>
          <input name="tfContactName" type="text" id="tfContactName" size="70" value ="<?php echo htmlentities(stripslashes($contactName)) ?>" />
        </label></td>
        <td><input name="tfContactEmail" type="text" id="tfContactEmail" size="30" value ="<?php echo $contactEmail ?>" /></td>
        </tr>
    </table></td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td><div align="right">Release note</div></td>
    <td><label>
      <textarea name="taReleaseNote" cols="80" rows="3" id="taReleaseNote" ><?php echo $releaseNote ?></textarea>
    </label></td>
  </tr>
  <tr>
    <td><div align="right">Release note URL</div></td>
    <td><input name="tfReleaseNoteURL" type="text" id="tfReleaseNoteURL" value ="<?php echo $releaseNoteURL ?>" size="80"></td>
  </tr>
  <tr>
    <td><div align="right">Source URL</div></td>
    <td><input name="tfSourceURL" type="text" id="tfSourceURL" value ="<?php echo $sourceURL ?>" size="80"></td>
  </tr>
  <tr>
    <td><div align="right">Reference</div></td>
    <td><textarea name="taReference" cols="80" rows="3" id="taReference"><?php echo $reference; ?></textarea></td>
  </tr>
  <tr>
    <td><div align="right">License</div></td>
    <td><label>
      <textarea name="taLicense" cols="80" rows="3" id="taLicense"><?php echo $license ?></textarea>
    </label></td>
  </tr>
  <tr>
    <td><div align="right">License required</div></td>
    <td><label>
      <input type="checkbox" name="chkLicense_required" id="chkLicense_required" value="checkbox" <?php echo $license_required_checked ?> />
    </label>	</td>
  </tr>
  <tr>
    <td><div align="right">Comment</div></td>
    <td><label>
      <textarea name="taComment" cols="80" rows="2" id="taComment"><?php echo $comment; ?></textarea>
    </label></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><input name="tried" type="hidden" id="tried" value="yes">
      <input name="versionID" type="hidden" id="versionID" value="<?php echo $versionID; ?>"></td>
  </tr>
</table>

<?php
 
 
?>
