package dependency;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;



 class Package implements  Comparable<Package>
 {
private String name;
private String version; 
private Integer size;
private List<List<String>> depends = new ArrayList<>();
private List<String> conflicts = new ArrayList<>();


public String getName(){
	return name;}

public String getVersion(){
	return version;
}

public Integer getSize() {
	return size; 
}

public List<List<String>> getDepends() {
	return depends; 
	}

public List<String> getConflicts() { 
	return conflicts; 
	}



public void setName(String name) { 
	this.name = name; 
	}

public void setVersion(String version) {
	this.version = version;
	}

public void setSize(Integer size) {
	this.size = size; 
	}

public void setDepends(List<List<String>> depends) {
	this.depends = depends; 
	}

public void setConflicts(List<String> conflicts) { 
	this.conflicts = conflicts; 
	}

public String stringify(){
	String returned= "jsaddj" + getName()+ "="+ getVersion()+ "Size" + getSize() + "Dep : "+ getDepends().toString() + "Conflicts: " + getConflicts();
	return returned;
}

public float versionTotalInt(){
	float i = Main.versionTotalInt(version);
	return i;
}

//useless
@Override
public int compareTo(Package o) {
	 return (this.getSize() < o.getSize() ? -1 : 
         (this.getSize() == o.getSize() ? 0 : 1));    
}


}


public class Main 

{
	public static String nameCons;
	public ArrayList<String> conflicts;
	
   public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile("seen-3/repository.json"), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile("seen-3/initial.json"), strListType);
    List<String> constraints = JSON.parseObject(readFile("seen-3/constraints.json"), strListType);
    
    //CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
 /*   for (Package p : repo) {
      System.out.printf("package %s version %s\n", p.getName(), p.getVersion());
      for (List<String> clause : p.getDepends()) {
        System.out.printf("  dep:");
        for (String q : clause) {
          System.out.printf(" %s", q);
        }
        System.out.printf("\n");
      }
    }*/
    
    List<String> commands = new ArrayList<String>();
    
    System.out.println(initial);
    System.out.println(constraints);
    //Construct command list
    commands.addAll(initial);

   
      for(String s : constraints){
    	
 
    	String symbol=""+ s.charAt(0);
    	String tempConstraint= s.substring(1);
    	System.out.println(tempConstraint);
    	
    	
    	
       	ArrayList<String> cons= splitString(tempConstraint);
    	String nameCons= cons.get(0);
    	String versionCons= cons.get(1);

    	System.out.println(nameCons);
    	System.out.println(versionCons);
    	if(symbol.equals("+")){
    	commands.addAll(install(repo , cons, constraints));
    	}
    	
    }
	System.out.println(commands.toString());
	
    
  }
  
  
  
  public static ArrayList<String> install(List<Package> repo, List<String> cons, List<String> constraints){
	  ArrayList<String> result= new ArrayList<String>();

	  
	  String nameCons= cons.get(0);
	  String versCons= cons.get(1);
	  
	  ArrayList<String> compareList= new ArrayList<String>();
	  compareList.add(nameCons);
	  Package inserted = compare(repo, compareList);
	  System.out.println("CHEAPEST VERSION OF " +nameCons +"IS" + inserted.getVersion() + "of size" + inserted.getSize());
	  if(versCons.equals("Any")){
		  versCons= inserted.getVersion();
	  }
	  
	  System.out.println("BEFORE CONS IS "+ cons.toString());
	
	  ArrayList<String> conflicts= new ArrayList<String>();
	  ArrayList<String> tests= dependancyBuilder(repo,cons,conflicts);
	  System.out.println("the thing before dupe removal is" + tests.toString());
	 
	  ArrayList<String> list = new ArrayList<String>(new LinkedHashSet<String>(tests));
	  System.out.println("THE FINAL QUESTION IS" + list);
	  Collections.reverse(list);
	/*  for (final ListIterator<String> i = list.listIterator(); i.hasNext();) {
		  final String element = i.next();
		  i.set("+["+ element + "]");
		}

	  System.out.println(list.toString());
	 */
	  for (Package a : repo) {
		  
		  if(a.getName().equals(nameCons)&& a.getVersion().equals(versCons)){
		  System.out.println("Name Cons is" + nameCons +"Package is " + a.getName());
	      System.out.printf("package %s version %s\n", a.getName(), a.getVersion());
		  System.out.println(versCons);

	      for (List<String> clause : a.getDepends()) {
	    	   if(clause.size()<=1) {
	    	  result.add("+"+ clause);
	    	  }
	    	   else{
	    		   //implement compare size function
	    		   for(String dependant: clause){
	    		   Package added= compare(repo, clause);
	    		   System.out.println("THE CLAUSE ADDED IS"+ added.getName()+"=" +added.getVersion() +"Size=" + added.getSize());
	    		   result.add("+" +added.getName() +"=" +added.getVersion());
	    	   }
	        System.out.printf("  dep:");
	        for (String q : clause) {
	          System.out.printf(" %s", q);
	        }
	        System.out.printf("\n");
	      }
	      
	 
	      }
	      result.add("+"+ a.getName() + "=" + a.getVersion());
	      System.out.println(result.toString()); 
	     
	      
		
	  }
		
	  }
	  
	  System.out.println(result.toString());
	  return result;
  }
  
  
  
  
   //Compares necessary dependancies by size
  public static Package compare(List<Package> repo, List<String> dependancies){
	  
	  List<Package> packList= new ArrayList<Package>();
	  System.out.println(dependancies);
      Map<ArrayList<Package>, Integer> hmap = new HashMap<ArrayList<Package>, Integer>();
      
	  for(String dependant: dependancies){
		  		
		       	ArrayList<String> cons= splitString(dependant);
		
		       	
		    	String nameCons= cons.get(0);
		    	String versionCons= cons.get(1);
		    	
		    	
		       	
		    	System.out.println("Splitted compare: " + nameCons+ "  " + versionCons);
		  
		  System.out.println(nameCons);
		  
		  
	  for(Package a: repo){
		      if(a.getName().equals(nameCons)){
			  packList.add(a);
		  }
	  }
	  }
	 

	  
	  Collections.sort(packList);
	
	 ArrayList<Package> s= new ArrayList<Package>();
	 
	
	  return packList.get(0); 
	  
  }
  
  public static int versionTotalInt(String version){
	  if (version.equals("Any")){return -100;}
	  int total=0;
	  String[] val= version.split("\\..");
	  if(version.equals("")){
	  return 0;
  }
	  else{
		  for(int x=0; x<val.length; x++)
		  total= 10* total+ Integer.valueOf(val[x]);
	  }
	  return total;
	  }
  
  
  
  
  public static ArrayList<String> dependancyBuilder (List<Package> repo, List<String> item, List<String> conflicts){
	  
	  System.out.println("BUILDING ITEM" +item.toString());
	  ArrayList<String> packageList = new ArrayList<String>();
	  		
	  		ArrayList<String> cons= splitString(item.toString());
			
	       	System.out.println("Cons should be" +cons);
	    	String nameCons= cons.get(0);
	    	String versionCons= cons.get(1); 
	    	String symbol;
	    	
	    	
	       	
	    	System.out.println("Splitted compare: " + nameCons+ "  " + versionCons);
	    	if(versionCons.equals("Any")){
	    		symbol= "Any";
	    		String[] c= nameCons.split("");
	    		nameCons= c[1];
	    	}
	    	else{
	    		symbol= cons.get(2);
	    	}
	    	System.out.println("Symbol is"+symbol);
	    
	    	for(Package p : repo){
	    		
	    		
	    		if((p.getName().equals(nameCons)&&versionCons.equals("Any"))
	    				
	    		    || 
	    		   (p.getName().equals(nameCons)&&symbol.equals(">") &&versionTotalInt(versionCons)>p.versionTotalInt())
	    		   ||
	    		   (p.getName().equals(nameCons)&&symbol.equals("<") &&versionTotalInt(versionCons)<p.versionTotalInt())
	    		   ||
	    		   (p.getName().equals(nameCons)&&symbol.equals(">=") &&versionTotalInt(versionCons)>=p.versionTotalInt())
	    		   ||
	    		   (p.getName().equals(nameCons)&&symbol.equals("<=") &&versionTotalInt(versionCons)<=p.versionTotalInt())
	    		   ||
	    		   (p.getName().equals(nameCons)&&symbol.equals("=") &&p.versionTotalInt()==versionTotalInt(versionCons))
	    		   )
	    		{
	    			packageList.add("+"+p.getName()+"="+p.getVersion());
	    			conflicts.addAll(p.getConflicts());
	    			
	    			System.out.println("its there i guess");
	    			if(p.getDepends().size()==0){
	    				System.out.println("0 depends");
	    				

	    			return packageList;
	    					}
	    			
	    			else{
	    				
	    				
	    				for(List<String> dependancyList: p.getDepends()){
	    					System.out.println("THE DEPENDANCY LIST"+ dependancyList);
	    					if(dependancyList.size()==1){
	    						
	    						System.out.println("dep is" + dependancyList);
	    						
	    						ArrayList<String> added= new ArrayList<String>(dependancyBuilder(repo,dependancyList, conflicts));
	    						
	    						System.out.println("Added with 1 "+ added.toString());
	    						packageList.addAll(added);
	    						return packageList;
	    					}
	    					else{
	    						
	    					for(String z: dependancyList){
	    						
	    						 packageList.add(z);
	    						System.out.println("Z is"+z);
	    						ArrayList<String> x= new ArrayList<String>();
	    						x.add(z);
	    						System.out.println(x.toString());
	    						ArrayList<String> added= new ArrayList<String>(dependancyBuilder(repo,x, conflicts));
	    						System.out.print("THE ADDED STRING FOR " + added.toString());
	    					    packageList.addAll(added);
	    					  
    						break;

	    					}
	    					}
	
	    					}
		    			return packageList;

	    				}
	    			}
	    	}
	    	
	   System.out.println("All done i guess");
	  return packageList;
	  
  }
  
  
//Work on removing conflicting details
  public static boolean conflictRemoval(List<String>packList, List<Package> repo , List<String> constraints,List<String> cons){
	  ArrayList<String> conflicts= new ArrayList<String>();
	  for (Package p : repo){
		  
		  conflicts.addAll(p.getConflicts());
		  //conflicts.addAll()
		  return true;
	  }
	  System.out.println(conflicts);
	return false;
	  
	  
	 
  }


  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }
  
  //converts string into the name and the symbol required
  public static ArrayList<String> splitString(String input){
	  ArrayList returned= new ArrayList();
	  
	  if(input.contains(">=")){
		  String [] splitted=input.split(">=");
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add(">=");
			 return returned;
	  }
	  else if(input.contains("<=")){
		  String [] splitted=input.split("<=");
			 returned.add(splitted[0]);  
			 returned.add(splitted[1]);
			 returned.add("<=");
			 return returned;
	  }
	  else if(input.contains("=")){
			 String [] splitted=input.split("=");
			 
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add("=");
			  return returned;
		  }
	  else if(input.contains(">")){
		  String [] splitted=input.split(">");
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add(">");
			 return returned;
	  }
	  else if(input.contains("<")){
		  String [] splitted=input.split("<");
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add("<");
			 return returned;
	  }
	  else{
		  returned.add(input);
		  returned.add("Any");
		  return returned;
	  }
	  
	  
  }

		
		
		

		// TODO Auto-generated method stub
		

  //Checks full size of dependancy for item to compare.
/*
* 
*
public static HashMap<ArrayList<Package>, Integer> dependancySize(List<Package> repo, List<String> dependancies){
	  System.out.println("DEPENDANCY START");
	  System.out.println(dependancies.toString());
	  int size= 0;
	   ArrayList<Integer> sizeList = new ArrayList<Integer>();
	   
	   HashMap<ArrayList<Package>, Integer> h= new HashMap<ArrayList<Package>,Integer>();
	  for(String dependant: dependancies){
		  
		 	ArrayList<String> cons= splitString(dependant);
			
	       	
	    	String nameCons= cons.get(0);
	    	String versionCons= cons.get(1);
	    	
		  for(Package p : repo){
			  ArrayList<Package> packList= new ArrayList<Package>();
			  if(p.getName().equals(nameCons)){
				    
				  if(p.getDepends().size()==0){
					  
					  size=size+p.getSize();
					  packList.add(p);
					  sizeList.add(size);
				  }
				  else{
					  for(List<String> clause: p.getDepends()){
						  packList.add(p);
						  size= p.getSize();
						  for(int i : dependancySize(repo,clause).values()){
							  System.out.println(i);
							  size=size+i;
						  }
						  

					  }
					  
				  }
				  h.put(packList, size);

				  }
			  }
		  }
	  
	  System.out.println("PackList"+ h.entrySet());
	  System.out.println("SizeList" + sizeList.toString());
	  System.out.println("The full size of this item is "+ size);
	  return h;
}
	  */
	  
	

    
    
	
	}
