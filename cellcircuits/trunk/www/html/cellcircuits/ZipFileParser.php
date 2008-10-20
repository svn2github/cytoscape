<?php

class ZipFileParser
{
	var $coverImage = NULL;
	var $pdfFile = NULL;
	var $organismArray = NULL;	
	var $sifFileArray = NULL;
	var $imgFileArray = NULL;
	var $thumImgFileArray = NULL;
	var $legendFileArray = NULL;
	var $publication_url = '';
	var $supplement_material_file = NULL;
	var $supplement_url = NULL;
	
	function ZipFileParser($zip_file_name) {

		$zip = zip_open($zip_file_name);
		if ($zip) {
			//echo "The zip file -- $zip_file_name\n";
			while ($zip_entry = zip_read($zip)) {
				$cur_file_name = zip_entry_name($zip_entry);
				//echo $cur_file_name."\n";
				if (zip_entry_open($zip, $zip_entry, "r")) {
					// Ignore directories
					if (zip_entry_filesize($zip_entry) == 0) { 
						continue;
					}
					$path = dirname($cur_file_name);
					$basename =basename($cur_file_name);
					
					if ($this->isCoverImage($path,$basename)) {
						$this->coverImage['fileName'] = $basename;
						$this->coverImage['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
						$this->coverImage['fileType'] = NULL; // how to determine the file type of zip_entry?
					}
					else if ($this->isPDF_file($path,$basename)) {
						$this->pdfFile['fileName'] = $basename;
						$this->pdfFile['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
						$this->pdfFile['fileType'] = NULL; // how to determine the file type of zip_entry?
					}
					else if ($this->isSif_file($cur_file_name,$basename)) {
						//echo "\n\tFound sif file: filename = ".$basename."\n";
						
						// Get organism name from the path string
						$tmpArray = split('/', $cur_file_name);
						$organism = $tmpArray[count($tmpArray)-2];
						//echo "\n\torganism".$organism."\n";
						
						$this->organismArray[] = $organism;
						
						$sifFile = NULL;
						$sifFile['fileName'] = $basename;
						$sifFile['fileType'] = NULL;
						$sifFile['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
						$this->sifFileArray[] = $sifFile;
					}
					else if ($this->isImage_file($cur_file_name,$basename)) {
						//echo "\n\tFound image file: filename = ".$basename."\n";
						$imgFile = NULL;
						$imgFile['fileName'] = $basename;
						$imgFile['fileType'] = NULL;
						$imgFile['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
						$this->imgFileArray[] = $imgFile;
					}
					else if ($this->isThumImage_file($cur_file_name,$basename)) {
						//echo "\n\tFound image file: filename = ".$basename."\n";
						$imgFile = NULL;
						$imgFile['fileName'] = $basename;
						$imgFile['fileType'] = NULL;
						$imgFile['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
						$this->thumImgFileArray[] = $imgFile;
					}
					else if ($this->isLegend_file($cur_file_name,$basename)) {
						//echo "\n\tFound legend file: filename = ".$basename."\n";
						$legendFile = NULL;
						$legendFile['fileName'] = $basename;
						$legendFile['fileType'] = NULL;
						$legendFile['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
						$this->legendFileArray[] = $legendFile;
					}
					else if ($this->isPublicationURL_file($cur_file_name,$basename)) {
						//echo "\n\tFound pub_name_short: filename = ".$basename."\n";
						$this->publication_url = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));						 
					}
					else if ($this->isSupplement_url_file($cur_file_name,$basename)) {
						//echo "\n\tFound Supplement_url_file: filename = ".$basename."\n";
						$this->supplement_url = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));						 
					}
					else if ($this->isSupplement_material_file($cur_file_name,$basename)) {
						//echo "\n\tFound supplement_material file: filename = ".$basename."\n";
						$this->supplement_material_file['fileName'] = $basename;
						$this->supplement_material_file['fileType'] = NULL;
						$this->supplement_material_file['content'] = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
					}

					zip_entry_close($zip_entry);
				}
			}
		}
		zip_close($zip);
	}

	// private methods
	function isCoverImage($path,$basename) {
		$slashCount = substr_count($path, '/'); 
		
		if ($slashCount >0) {
			// This file is not in the top level of the zip file
			return false;
		}
		/*
		$pos = strpos($basename, 'coverImage');
		if ($pos === false) {
			// 'coverImage' is not part of its name
			return false;
		}
		*/
		
		$pos1 = strpos($basename, '.png');
		$pos2 = strpos($basename, '.PNG');
		if ($pos1 != false || $pos2 != false) {
			// this file has extension .png/.PNG
			return true;
		}

		$pos1 = strpos($basename, '.jpg');
		$pos2 = strpos($basename, '.JPG');
		if ($pos1 != false || $pos2 != false) {
			// this file has extension .jpg/.JPG
			return true;
		}

		$pos1 = strpos($basename, '.jpeg');
		$pos2 = strpos($basename, '.JPEG');
		if ($pos1 != false || $pos2 != false) {
			// this file has extension .jpeg/.JPEG
			return true;
		}

		$pos1 = strpos($basename, '.gif');
		$pos2 = strpos($basename, '.GIF');
		if ($pos1 != false || $pos2 != false) {
			// this file has extension .gif/.GIF
			return true;
		}

		$pos1 = strpos($basename, '.bmp');
		$pos2 = strpos($basename, '.BMP');
		if ($pos1 != false || $pos2 != false) {
			// this file has extension .bmp/.BMP
			return true;
		}

		return false;
	}

	function isPDF_file($path,$basename) {
		$slashCount = substr_count($path, '/'); 
		if ($slashCount >1) {
			// This file is not in the top level of the zip file
			return false;
		}
		$pos1 = strpos($basename, '.pdf');
		$pos2 = strpos($basename, '.PDF');
		if ($pos1 == false && $pos2 == false) {
			// this file doest not have extension .pdf/.PDF
			return false;
		}
		return true;
	}
	
	
	function isPublicationURL_file($path,$basename) {
		
		$slashCount = substr_count($path, '/'); 
		if ($slashCount >1) {
			// This file is not in the top level of the zip file
			return false;
		}
		
		if ($basename != 'publication_url.txt') {
			return false;
		}
		return true;
	}
	
	function isSupplement_url_file($path,$basename) {
		
		$slashCount = substr_count($path, '/'); 
		if ($slashCount >1) {
			// This file is not in the top level of the zip file
			return false;
		}
		
		if ($basename != 'supplement_url.txt') {
			return false;
		}
		return true;
	}
	
	function isSupplement_material_file($cur_file_name,$basename) {
		
		$slashCount = substr_count($cur_file_name, '/'); 
		if ($slashCount >1) {
			// This file is not in the top level of the zip file
			return false;
		}
		
		$tmpArray = split('[.]',$basename);
		$fileNamePrefix = $tmpArray[0];
		if ($fileNamePrefix != 'supplement_material') {
			return false;
		}
		return true;
	}
	
	
	
	function isSif_file($filename,$basename) {
		$pos1 = strpos($filename, '/sif/');		
		$pos2 = strpos($basename, '.sif');		
		if ($pos1 === false || $pos2 == false) {
			// this file is not in sif directory and have file extension '.sif'
			return false;
		}
		return true;
	}
	
	function isImage_file($filename,$basename) {
		$pos1 = strpos($filename, '/img/');		
		if ($pos1 === false) {
			// this file is not in img directory
			return false;
		}
		return true;
	}

	function isThumImage_file($filename,$basename) {
		$pos1 = strpos($filename, '/thm_img/');		
		if ($pos1 === false) {
			// this file is not in img directory
			return false;
		}
		return true;
	}

	
	function isLegend_file($filename,$basename) {
		$pos1 = strpos($filename, '/legend/');
		if ($pos1 === false) {
			// this file is not in legend directory
			return false;
		}
		return true;
	}

	// public methods
	function getCoverImage() {
		return $this->coverImage;
	}
	function getPDF() {
		return $this->pdfFile;
	}

	function getOrganismArray() {
		return $this->organismArray;
	}
	
	function getSifFileArray() {
		return $this->sifFileArray;
	}

	function getImgFileArray() {
		return $this->imgFileArray;
	}

	function getThumImgFileArray() {
		return $this->thumImgFileArray;
	}

	function getLegendFileArray() {
		return $this->legendFileArray;
	}
	
	function getPublication_url() { 
		return $this->publication_url;
	}
	
	function getSupplement_material_file() {
		return $this->supplement_material_file;
	}

	function getSupplement_url() {
		return $this->supplement_url;
	}
	
} // end of class definition

?>

