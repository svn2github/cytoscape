package GenericPlugin;
import java.util.*;

public class ArgVector{
    Vector names;
    Vector args;

    public ArgVector(){
	names=new Vector();
	args=new Vector();
    }

    public ArgVector(int size){
	names=new Vector(size);
	args=new Vector(size);
    }

    public ArgVector(ArgVector copy){
	names=new Vector(copy.names());
	args=new Vector(copy.args());
    }

    public ArgVector(Vector names,Vector args){
	this.names=new Vector(names);
	this.args=new Vector(args);
    }

    public int size(){
	return names.size();
    }

    public boolean hasArg(Object name){
	return (this.names.indexOf(name)>-1);
    }

    public Object name(int index){
	return names.elementAt(index);
    }

    public Object arg(int index){
	return args.elementAt(index);
    }

    public Object arg(Object name){
	int index=this.names.indexOf(name);
	if (index==-1){
	    return null;
	}
	return this.args.get(index);
    }

    //get the argument that .equals(name) after names.get(start)
    public Object arg(Object name,int start){
	int index=this.names.indexOf(name,start);
	if (index==-1){
	    return null;
	}
	return this.args.get(index);
    }

    public int indexOf(Object name){
	return this.names.indexOf(name);
    }	

    public int indexOf(Object name,int start){
	return this.names.indexOf(name,start);
    }	

    public int argIndex(Object arg){
	return this.args.indexOf(arg);
    }	

    public int argIndex(Object arg,int start){
	return this.args.indexOf(arg,start);
    }	

    public Vector args(){
	return this.args;
    }

    public Vector names(){
	return this.names;
    }
    public void add(Object name,Object arg){
	this.names.add(name);
	this.args.add(arg);
    }

    public Object get(Object name){
	return arg(name);
    }

    //will overwrite an existing argument or add one
    public void set(Object name,Object arg){
	int index=this.names.indexOf(name);
	if (index==-1){
	    System.out.println("adding "+name.toString());
	    add(name,arg);
	}
        else{
	    System.out.println("setting arg "+names.get(index).toString());
	    this.args.set(index,arg);
	}
    }

    //sets only if the argument ecists
    public boolean set(Object name,Object arg,int start){
	int index=this.names.indexOf(name,start);
	if (index==-1){
	    return false;
	}
	this.args.set(index,arg);
	return true;
    }

    public void put(Object name, Object arg){
	set(name,arg);
    }

    public void clear(){
	names.clear();
	args.clear();
    }
   
    public void putAll(ArgVector argmap){
	names=new Vector(argmap.names());
	args=new Vector(argmap.args());
    }
}
