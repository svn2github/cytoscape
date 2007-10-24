package org.mskcc.pathway_commons.web_service;

/**
 * Indicates Error Connecting to cPath.
 *
 * @author Ethan Cerami.
 */
public class CPathException extends Exception {
    /**
     * Error:  Canceled by User.
     */
    public final static int ERROR_CANCELED_BY_USER = 1;

    /**
     * Error:  Unknown Host.
     */
    public final static int ERROR_UNKNOWN_HOST = 2;

    /**
     * Error:  Network IO.
     */
    public final static int ERROR_NETWORK_IO = 3;

    /**
     * Error:  XML Parsing.
     */
    public final static int ERROR_XML_PARSING = 4;

    /**
     * Error:  Web Service API.
     */
    public final static int ERROR_WEB_SERVICE_API = 5;

    /**
     * Error HTTP
     */
    public final static int ERROR_HTTP = 6;

    private int errorCode;
    private String errorDetail;

    /**
	 * Constructor.
     * @param errorCode Error Code.
	 * @param t Root throwable.
	 */
	public CPathException(int errorCode, Throwable t) {
        super(t);
        this.errorCode = errorCode;
	}

    public CPathException (int errorCode, String errorDetail) {
        this.errorDetail = errorDetail;
        this.errorCode = errorCode;
    }

    /**
     * Gets the Error Code.
     * @return Error Code.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Gets Error Message.
     * @return Error Message.
     */
    public String getMessage() {
        String msg = null;
        switch (errorCode) {
            case ERROR_CANCELED_BY_USER:
                msg =  "Canceled by user.";
                break;
            case ERROR_UNKNOWN_HOST:
                msg = "Network error occurred while tring to connect to "
                        + "remote web service.  Please check your server and network settings, "
                        + "and try again.";
                break;
            case ERROR_NETWORK_IO:
                msg = "Network error occurred while tring to connect to "
                        + "remote web service.  Please check your server and network settings, "
                        + "and try again.";
                break;
            case ERROR_XML_PARSING:
                msg = "Error occurred while trying to parse XML results "
                    + "retrieved from remote web service.  "
                    + "Please check your server and network settings, "
                    + "and try again.";
                break;
            case ERROR_HTTP:
                 msg = "Network error occurred while trying to connect to "
                        + "remote web service.  (Details:   " + errorDetail + ")";
                break;
            case ERROR_WEB_SERVICE_API:
                msg = "Error occurred while trying to connect to remote web service.  "
                        + "(Details:  " + errorDetail + ")";
                break;

        }
        return msg;
    }
}
