package GenericPlugin;
import java.util.*;

public interface GenericPlugin{
    void initialize();
    HashMap getActions();
    void run(String action,HashMap args);
    boolean setValue(HashMap args,String curArg,String newVal);
}
