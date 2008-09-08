package cytoscape.logger;

/**
 * The levels of logged messages
 */
public enum LogLevel {
		/**
		 * LOG_DEBUG should be used only for low-level debugging
		 */
    LOG_DEBUG("DEBUG",0, "Debugging"),
		/**
		 * LOG_INFO should be used for general, informational messagse
		 */
    LOG_INFO("INFO",1, "Information"),
		/**
		 * LOG_WARN should be used to warn the user of some event or issue
		 */
    LOG_WARN("WARN",2, "Warnings"),
		/**
		 * LOG_ERROR indicates an error of some sort
		 */
    LOG_ERROR("ERROR",3, "Errors"),
		/**
		 * LOG_FATAL is a fatal error
		 */
    LOG_FATAL("FATAL",4, null);

    private String prettyName;
    private String name;
    protected int level;
    private LogLevel(String str, int level, String pretty) {
      this.level = level;
      this.name = str;
      this.prettyName = pretty;
    }
    public int getLevel() { return this.level; }
    public String getPrettyName() { return this.prettyName; }
    public String toString() { return name; }
    public boolean applies(LogLevel target) { return (this.level >= target.level); }
  }





