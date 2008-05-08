package cytoscape.logger;

public enum LogLevel {
    LOG_DEBUG("DEBUG",0, "Debugging"),
    LOG_INFO("INFO",1, "Information"),
    LOG_WARN("WARN",2, "Warnings"),
    LOG_ERROR("ERROR",3, "Errors"),
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





