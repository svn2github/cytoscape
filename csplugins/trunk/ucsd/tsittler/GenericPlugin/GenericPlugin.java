package GenericPlugin;
import java.util.*;

public interface GenericPlugin{
    void initialize();
    ArgVector getActions();
    void run(String action,ArgVector args);
    boolean setValue(ArgVector args,String curArg,String newVal);
}
