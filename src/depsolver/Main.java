package depsolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;



 class Package  
 {
private String name;
private String version; 
private Integer size;
private List<List<String>> depends = new ArrayList<>();
private List<String> conflicts = new ArrayList<>();


public String getName(){return name;}
public String getVersion(){return version;}
public Integer getSize() {	return size; }
public List<List<String>> getDepends() {	return depends; 	}
public List<String> getConflicts() { return conflicts; }
public void setName(String name) { this.name = name; }
public void setVersion(String version) {this.version = version;}
public void setSize(Integer size) {this.size = size; }
public void setDepends(List<List<String>> depends) {this.depends = depends; }
public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }

public String stringify(){
	String returned= "jsaddj" + getName()+ "="+ getVersion()+ "Size" + getSize() + "Dep : "+ getDepends().toString() + "Conflicts: " + getConflicts();
	return returned;
}
/*
public float versionTotalInt(){
	float i = Main.versionTotalInt(version);
	return i;
}
*/


}


public class Main 

{
	public static String nameCons;
	public ArrayList<String> conflicts;
	
   public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);
    

    List<String> commands = new ArrayList<String>();
     
    //Construct command list

   
      for(String s : constraints){
    	
 
    	  
    	String symbol=""+ s.charAt(0);
    	String tempConstraint= s.substring(1);
   
   
    	
       	ArrayList<String> cons= splitString(tempConstraint);
    	String nameCons= cons.get(0);
    	String versionCons= cons.get(1);

     	ArrayList<String> initialChecker= checkInitial(repo,cons,initial,constraints);
    	
    	
    	
    	
  	  ArrayList<String> conflicts= new ArrayList<String>();
  	  ArrayList<String> state= new ArrayList<String>();
    	if(symbol.equals("+")){
    	commands.addAll(dependancyBuilder(repo , cons, state));
    	}
    	
    }
	   
	   
	    if(initial.size() >0){
      for(String x: constraints){
    	  
      	String symbol=""+ x.charAt(0);
      	String tempConstraint= x.substring(1);
     
     
      	
         	ArrayList<String> cons= splitString(tempConstraint);
      	String nameCons= cons.get(0);
      	String versionCons= cons.get(1);
    	  
    	  ArrayList<String> initialChecker= checkInitial(repo,cons,initial,constraints);
    	  commands.addAll(initialChecker);
      }
	   
	
      Collections.reverse(commands);
      
	System.out.println(JSON.toJSONString(commands));
	
    
  }
   
   
   public static ArrayList<String> checkInitial (List<Package> repo, ArrayList<String> cons, List<String> initial, List<String> constraints){
	   if (initial.size()==0)
	   {
		   ArrayList<String> emptyState = new ArrayList<String>();
		   return emptyState;
	   }
	   ArrayList<String> emptyState = new ArrayList<String>();
	   ArrayList<String> newCommand = new ArrayList<String>();
	   ArrayList<String> commandsChecker= dependancyBuilder(repo, cons,emptyState);
	   for(String initialS : initial){
		   
		   if( stateValid(commandsChecker ,  repo )){
			 
			    if(initialS.charAt(0)=='-'){
				 newCommand.add(initialS);
			 }else{
			   
			  String  newString= "-" + initialS;
			  newCommand.add(newString);
			 }
			  
	   }
	   
   }
	  return newCommand; 
   }
  
  
  
 
  
   
  public static boolean versionCompare(String vers1, String vers2, String symbol){
	  
	  
	  
	  if(symbol.equals("=")){
		  if(vers1.equals(vers2)){
			  return true;
		  }
	  }
	  
	  else if(symbol.equals("<=")){	
		  if(vers1.equals(vers2)){
			  return true;
		  	}
		  else{ 			  
				 if(vers1.compareTo(vers2)<0){
				return true;
				  }
			  }
		  }
	  
	  
	  else if(symbol.equals("<")){

		  if(vers1.compareTo(vers2)<0){
			  return true;
		  }
		  
	  }
	  else if(symbol.equals(">=")){
		  if(vers1.equals(vers2)){ return true;}
		  else{
		  if (vers1.compareTo(vers2)>0){

			  return true;
		  }
	  }}
	  else if(symbol.equals(">")){
		  if(vers1.compareTo(vers2)>0){
			  return true;
		  }
		  
	  }
	  else if(symbol.equals("Any")){
		  return true;
	  }
	  
	  return false;
	  
  
  }
	  
	
  
  
  
  public static ArrayList<String> dependancyBuilder (List<Package> repo, List<String> item,ArrayList<String> states){
	  ArrayList<String> originalState= new ArrayList<String>(states);
	
	  ArrayList<String> packageList = new ArrayList<String>();
	  		
	  		ArrayList<String> cons= splitString(item.toString());
			
	    	String nameCons= cons.get(0);
	    	String versionCons= cons.get(1); 
	    	String symbol;
	    	
	    	
	       	
	    	if(versionCons.equals("Any")){
	    		symbol= "Any";
	    		String[] c= nameCons.split("");
	    		nameCons= c[1];
	    	}
	    	else{
	    		
	    		symbol= cons.get(2);
	    	}
	    	
	    	
	    	//Start process
	    
	    	for(Package p : repo){
	    		

	    		if((p.getName().equals(nameCons)&&versionCompare(p.getVersion(),versionCons,symbol))){
	    	
	    		
	    			
	    			
	    			
	    			ArrayList<String> stateTest= new ArrayList<String>(states);
	    			stateTest.add("+"+p.getName()+"="+p.getVersion());
	    			
	    			  Set<String> dupeTest= new HashSet<String>(stateTest);
	    	    	  if(dupeTest.size()< stateTest.size()){
	    	    		  ArrayList<String> emptyReturned= new ArrayList<String>();
	    	    		  return emptyReturned;
	    	    	  }
	    			
	    			
    				if(stateValid(stateTest,repo)){
		    			packageList.add("+"+p.getName()+"="+p.getVersion());
		    			states.add("+"+p.getName()+"="+p.getVersion());
    				}else{
    					
    				}
		    			
		    			
		    			
		    			if(p.getDepends().size()==0){
			    			return packageList;
		    			}
		    			
		    			else if(p.getDepends().size()==1){
    					if(p.getDepends().get(0).size()==1) {
    					ArrayList<String> added= new ArrayList<String>(dependancyBuilder(repo,p.getDepends().get(0),states));
						 ArrayList<String> stateTemp = new ArrayList<String>(states);
						 stateTemp.addAll(added);
					
						 
						if(stateValid(stateTemp,repo )){
							if(states.containsAll(added)){
								ArrayList<String> empty = new ArrayList<String>();
								return empty;
							}else{
								
								packageList.addAll(added);
								states.addAll(added);
								return packageList; 
								}
						}
						else{
							ArrayList<String> empty = new ArrayList<String>();
							return empty;
						}
    				}
    					else if(p.getDepends().get(0).size()>1) {
    						for(String str: p.getDepends().get(0)) {
    							ArrayList<String> dependAdded =new ArrayList<String>();
    							dependAdded.add(str);
	    						ArrayList<String> added= new ArrayList<String>(dependancyBuilder(repo,dependAdded, states));
	    						 ArrayList<String> stateTemp = new ArrayList<String>(states);
	    						 stateTemp.addAll(added);
	    						 if(stateValid(stateTemp,repo)){
		    							if(originalState.containsAll(added)){
	    									ArrayList<String> empty = new ArrayList<String>();
	    									return empty;
	    								}else
	    								{
			    					    packageList.addAll(added);
			    					    states.addAll(packageList);
			    					    return packageList;
			    					    
			    					    }
			    					 
		    						}
		    						else{
		    					    }
	    						
    						}
    					}
    					
    				}
    				
    				
    					//For If multiple on list
	    			
	    				for(List<String> dependancyList: p.getDepends()){
	    					
	    					
	    					if(dependancyList.size()==1){
	    						
	    						
	    						
	    						ArrayList<String> added= new ArrayList<String>(dependancyBuilder(repo,dependancyList,states));
	    						 ArrayList<String> stateTemp = new ArrayList<String>(states);
	    						 stateTemp.addAll(added);
	    						 
	    						 
	    						if(stateValid(stateTemp,repo )){
	    								if(stateTemp.contains(added)){
	    									ArrayList<String> empty = new ArrayList<String>();
	    									return empty;
	    								}else{
		    						packageList.addAll(added);
		    						states.addAll(added);
		    						
		    						}
	    						}

	    						
	    					}
	    					}
	    					
	    				
	    				for(List<String> depends: p.getDepends()){

	    					if(depends.size()>1){
	    					for(String z: depends){
	    						
	    						//IN THIS AREA, CREATE A THING THAT COMPARES CONFLICTS AND ADDS IF THE FINAL STATE IS VALID
	    						//EASIER SAID THAN DONE BUT YOU KNOW
	    						
	    						ArrayList<String> x= new ArrayList<String>();
	    						x.add(z);
	    						
	    						
	    					    
	    						ArrayList<String> added= new ArrayList<String>(dependancyBuilder(repo,x, states));
	    						ArrayList<String> stateForThing=new ArrayList<String>(states);
	    						stateForThing.addAll(added);
	
	    						if(stateValid(stateForThing,repo)){
	    							if(originalState.containsAll(added)){
    									ArrayList<String> empty = new ArrayList<String>();
    								}
	    							else{
	    								
		    					    packageList.addAll(added);
		    					    states.addAll(packageList);
		    					    return packageList;
		    					    
		    					    }
		    					 
	    						}
	    						else{
	    					    }
    						

	    					}
	    					}
	
	    					

	    			}

	    			
	    			}
	    		
	    		
	    	}
			return states;
	  
	    	}
  
  
  
  
  
	   public static ArrayList<String> conflictBuilder (List<Package> repo, ArrayList<String> state){
				   ArrayList<String> tempConflict= new ArrayList<String>();
			 for(String s: state){
				 
				 //**********************************//
				  
				  ArrayList<String> stateAsArray= splitString(s);
					
			    	String name= stateAsArray.get(0);
			    	String version= stateAsArray.get(1); 
			    	String symb;
			    	
 				   name= name.replace("+", "");
				 
				 
				 for (Package pack : repo){
					 
					 if((pack.getName().equals(name) && pack.getVersion().equals(version))
				    ||
				    (pack.getName().equals(name)&&versionCompare(pack.getVersion(),version,"="))){
					 
				 tempConflict.addAll(pack.getConflicts());
				 }
				 }
			 }
	   return tempConflict;
	   }
  
  
  
  
  
  
//Work on removing conflicting details
  public static boolean stateValid(ArrayList<String> state , List<Package> repo ){
	 
	  Set<String> dupeTest= new HashSet<String>(state);
	  if(dupeTest.size()< state.size()){
		  
		  return false;
	  }
	  
	  
	ArrayList<String> conflicts= conflictBuilder(repo,state);
	  
	  ArrayList<Package> alreadyAdded=new ArrayList<Package>();
	  ArrayList<Package> statePackList = new ArrayList<Package>();
	
	 
	  
	  
	  for(String s : state){	
		  ArrayList<String> stateAsArray= splitString(s);
			
	    	String nameCons= stateAsArray.get(0);
	    	String versionCons= stateAsArray.get(1); 
	    	String symbol;
	    	
	    	if(versionCons.equals("Any")){
	    		symbol= "Any";
	    		String[] c= nameCons.split("");
	    		nameCons= c[1];
	    	}
	    	else{
	    		
	    		symbol= stateAsArray.get(2);
	    	}
		   nameCons= nameCons.replace("+", "");
	  for (Package p : repo){
	  		
		if((p.getName().equals(nameCons)&&versionCons.equals("Any"))
				
    		    ||
    		    (p.getName().equals(nameCons)&&versionCompare(p.getVersion(),versionCons,symbol)))
				{
	    		statePackList.add(p);
	    		} 
		 }
	  	}
	  for(String confl: conflicts){
		  ArrayList<String> stateAsArray= splitString(confl);
	    	String nameCons= stateAsArray.get(0);
	    	String versionCons= stateAsArray.get(1); 
	    	String symbol;
	    	
	    	if(versionCons.equals("Any")){
	    		symbol= "Any";
	    	
	    	}
	    	else{
	    		
	    		symbol= stateAsArray.get(2);
	    	}
	  	  for(Package pack: statePackList){


	    	if( (pack.getName().equals(nameCons) && versionCons.equals("Any"))||
	    	   (pack.getName().equals(nameCons) && versionCompare(pack.getVersion(),versionCons,symbol)))
	    	{
			    	return false;
		    		}
		
			
			  }
		  
	  }
	  
	  
	return true;
	  
	  
	 
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
		  input= input.replace("[", "");
		  input= input.replace("]", "");
	
		  String [] splitted=input.split(">=");
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add(">=");
			
			 return returned;
	  }
	  else if(input.contains("<=")){
		  input= input.replace("[", "");
		  input= input.replace("]", "");
	
		  String [] splitted=input.split("<=");
			 returned.add(splitted[0]);  
			 returned.add(splitted[1]);
			 returned.add("<=");
			 return returned;
	  }
	  else if(input.contains("=")){
		  
		  input= input.replace("[", "");
		  input= input.replace("]", "");
	
		  
		  
			 String [] splitted=input.split("=");
			 
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add("=");
			
			 
			  return returned;
		  }
	  else if(input.contains(">")){
		  input= input.replace("[", "");
		  input= input.replace("]", "");
	
		  String [] splitted=input.split(">");
			 returned.add(splitted[0]);
			 returned.add(splitted[1]);
			 returned.add(">");
			 return returned;
	  }
	  else if(input.contains("<")){
		  input= input.replace("[", "");
		  input= input.replace("]", "");
	
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
		


	  
	

    
    
	
	}
