package edu.ucsd.bioeng.coreplugin.tableImport.reader;

public enum SupportedFileType {
	EXCEL("xls"), OOXML("xlsx"), TEXT("txt");
	
	private final String extension;
	
	private SupportedFileType(final String extension) {
		this.extension = extension;
	}
	
	/**
	 * 
	 * @return file extension with dot.
	 */
	public String getExtension() {
		return "." + extension;
	}
}
