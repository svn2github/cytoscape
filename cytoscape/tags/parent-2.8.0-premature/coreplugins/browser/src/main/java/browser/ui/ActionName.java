/**
 *
 */
package browser.ui;

public enum ActionName {
	SET("Set"),
	REPLACE("Replace"),
	REMOVE("Remove"),
	TO_UPPER("To upper-case"),
	TO_LOWER("To lower-case"),
	ADD_PREFIX("Add Prefix"),
	ADD_SUFFIX("Add Suffix"),
	
	ADD("Add Number"),
	MUL("Mul"),
	DIV("Div"),
	COPY("Copy"),
	CLEAR("Clear");

	String dispName;

	private ActionName(String dispName) {
		this.dispName = dispName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName() {
		return dispName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param dispName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ActionName getValueOf(final String dispName) {
		for (ActionName action : values()) {
			if (action.dispName.equals(dispName)) {
				return action;
			}
		}

		return null;
	}
}
