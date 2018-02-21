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
import java.util.List;
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

@Override
public int compareTo(Package o) {
	 return (this.getSize() < o.getSize() ? -1 : 
         (this.getSize() == o.getSize() ? 0 : 1));    
}


}


public class Main {
	public static String nameCons;
	
  public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile("seen-2/repository.json"), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile("seen-0/initial.json"), strListType);
    List<String> constraints = JSON.parseObject(readFile("seen-2/constraints.json"), strListType);
    
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
	  if(versCons.equals("None")){
		  versCons= inserted.getVersion();
	  }
	  
	  
	  ArrayList<String> tests= testing(repo,cons);
	  //This is potentially a terrible idea, go back to other form if this won't work
	//  ArrayList<String> packagesNeeded= dependancyBuilder(repo, cons);
     //System.out.println("Like ok no" +packagesNeeded.toString());
	  
	  
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
		  else{
			  System.out.println("Not found");
		  }
	  }
	  
	  System.out.println(result.toString());
	  return result;
  }
  public static ArrayList<String> testing(List<Package> repo, List<String> constraints){
	  ArrayList<String> result= new ArrayList<String>();
	  System.out.println("CONSTRAINT IS"+ constraints);
	  
	  
	  for(String item: constraints){
for (Package a : repo) {
		if(a.getName().equals(item)){
	      for (List<String> clause : a.getDepends()) {
	    	   if(clause.size()==0){
	    		   return result;
	    		   }
	    	   else if(clause.size()==1) {
	    	  result.addAll(testing(repo,clause));
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
	  }
	return result;
	  
}
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
  
  //Work on removing conflicting details
  public static ArrayList<String> conflictRemoval(List<Package> repo , List<String> constraints){
	  ArrayList<String> conflicts= new ArrayList<String>();
	  for (Package p : repo){
		  conflicts.addAll(p.getConflicts());
		  
	  }
	  System.out.println(conflicts);
	  return conflicts;
	  
	 
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
			  System.out.println("done that");
			  packList.add(a);
		  }
	  }
	  }
	 
	  
	  for(Package p: repo){
		  //System.out.println("the thing is"+p.getName()+"the v is"+p.getVersion());   
		  
       
	      for (List<String> clause : p.getDepends()) {
 	    	/*  HashMap<ArrayList<Package>,Integer> h=dependancySize(repo, clause); 
	    	  System.out.println(h.toString());
	    	  System.out.println("aaaa"+h.entrySet());
	    	  for(Map.Entry<ArrayList<Package>, Integer> t: h.entrySet()){
	    		  ArrayList<Package> key= t.getKey();
	    		  Integer size= t.getValue();
	    		  
	    	  hmap.put(key,size);
	    	  } */
	   
	    	  
		 
	  }
	  }
	  Collections.sort(packList);
	
	
	 //System.out.print(hmap.toString());
	 //System.out.println(hmap.values());
	 ArrayList<Package> s= new ArrayList<Package>();
	 
	 /* ordered in descending order, figure out how to fix.
	  Map<ArrayList<Package>, Integer> resultMap= hmap.entrySet().stream()
              .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                      (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	  
	  List<ArrayList<Package>> packLi= new ArrayList<ArrayList<Package>>();
	 System.out.println(resultMap.toString());
	 for(ArrayList<Package> x : resultMap.keySet() ){
		 packLi.add(x);
	 }
	 for(Package aa: packLi.get(0)){
		 System.out.println("LETS SEE IF THIS WORKS"+aa.getName() +": " +aa.getVersion() ); 
	 }
	  
	  System.out.println("THE FINAL HMAP IS"+ resultMap);*/
	  return packList.get(0); 
	  
  }
  
  public static ArrayList<String> dependancyBuilder (List<Package> repo, List<String> item){
	  System.out.println("BUILDING ITEM" +item.toString());
	  
	  ArrayList<String> packageList = new ArrayList<String>();
	  
	  for(String depend: item){
		  ArrayList<String> cons= splitString(depend);
			
	       	
	    	String nameCons= cons.get(0);
	    	System.out.println(nameCons);
	    	String versionCons= cons.get(1);
	    	for(Package p : repo){
	    		if(p.getName().equals(nameCons)){
	    			System.out.println("its there i guess");
	    			if(p.getDepends().size()==0){
	    				packageList.add(p.getName());
	    			return packageList;
	    					}
	    			else{
	    				packageList.add(p.getName());
	    				for(List<String> x: p.getDepends()){
	    					packageList.addAll(dependancyBuilder(repo,x));
	    					}
	    				}
	    			}
	    			System.out.println(packageList);
	    	}
	    	}
	  
	  
	  return packageList;
	  
  }

     //Checks full size of dependancy for item to compare.
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
		  returned.add("None");
		  return returned;
	  }
	  
	  
  }

		
		
		

		// TODO Auto-generated method stub
		
		
    
    
	
	}
