package ucsd.rmkelley.Util;
import java.io.*;
import java.util.*;
import cytoscape.*;
import giny.model.Node;
import DistLib.*;
public class GOprediction{
  double P_VALUE_CUTOFF = .0000001;
  int CROSS_VALIDATION_SPLIT = 5;
  /*
   * GOID2Orfs maps from each GOID to each orf and child orf
   * GOID2ParentOrfs maps from each GOID to each orf which is member or
   *    child of a parent
   * GOID2Ancestors maps from each GOID to all ancestors
   * ORF2GOID maps from each ORF to all GOIDs which are a parent
  */
  HashMap ORF2GOIDs, GOID2Orfs,GOID2ParentOrfs, GOID2Ancestors ;
  HashMap ORF2Predictions;
  int [] prediction_count_history;
  int [] prediction_correct_history;
  double [] complex_score_history;
  
  /**
   * The networkmodels should be in sorted order (descending score)
   * GOID2Orfs maps from each GOID to a direct orf
   * GOID2Parents maps from each GOID to a set of parent GOIDs
   */
  public GOprediction(File GOIDOrfs, File GOIDParents, Collection complexes){
    /*
     * Initialize some record keeping data structures
     */
    System.err.println("Parsing GO hierarchy");
    ORF2Predictions = new HashMap();
    prediction_count_history = new int[complexes.size()];
    prediction_correct_history = new int[complexes.size()];
    complex_score_history = new double[complexes.size()];


    HashMap GOID2Parents = new HashMap();
    /*
     * Use the file to fill the GOID2Parents
     * hashmap
     */

    try{
      BufferedReader reader = new BufferedReader(new FileReader(GOIDParents));
      while(reader.ready()){
	String [] splat = reader.readLine().split("\t");
	if(splat.length > 1){
	  GOID2Parents.put(splat[0],Arrays.asList(splat[1].split("::")));
	}
      }
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException();
    }
    P_VALUE_CUTOFF = 0.05/GOID2Parents.size();
    
    /*
     * Use the GOID2Parents map to fill
     * the GOID2Ancestors map
     */
    HashMap GOID2Ancestors = new HashMap();
    for(Iterator GOIDIt = GOID2Parents.keySet().iterator();GOIDIt.hasNext();){
      String GOID = (String)GOIDIt.next();
      /////////
      //
      //
      // I stopp working here, I was about to write a recursive function
      // to fill this whatever the fuck I am tired I am going to sleep
      if(!GOID2Ancestors.containsKey(GOID)){
	determineAncestors(GOID,GOID2Parents,GOID2Ancestors);
      }
    }

    HashMap GOID2NonAncestorOrfs = new HashMap();
    /*
     * Fill this hashmap using the 
     * file
     */
    try{
      BufferedReader reader = new BufferedReader(new FileReader(GOIDOrfs));
      while(reader.ready()){
	String [] splat = reader.readLine().split("\t");
	GOID2NonAncestorOrfs.put(splat[0],string2NodeSet(splat[1]));
      }
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException();
    }

    /*
     * Fill the GOID2Orfs hashmap
     */
    GOID2Orfs = new HashMap();
    for(Iterator GOIDIt = GOID2Ancestors.keySet().iterator();GOIDIt.hasNext();){
      
      String GOID = (String)GOIDIt.next();
      HashSet mySet = null;
      if(GOID2Orfs.containsKey(GOID)){
	mySet = (HashSet)GOID2Orfs.get(GOID);
      }
      else{
	mySet = new HashSet();
	GOID2Orfs.put(GOID,mySet);
      }
      
      if(GOID2NonAncestorOrfs.containsKey(GOID)){
	Collection myCollection = (Collection)GOID2NonAncestorOrfs.get(GOID);
	mySet.addAll(myCollection);
	for(Iterator ancestorIt = ((Collection)GOID2Ancestors.get(GOID)).iterator();ancestorIt.hasNext();){
	  HashSet ancestorSet = null;
	  String ancestor = (String)ancestorIt.next();
	  if(GOID2Orfs.containsKey(ancestor)){
	    ancestorSet = (HashSet)GOID2Orfs.get(ancestor);
	  }
	  else{
	    ancestorSet = new HashSet();
	    GOID2Orfs.put(ancestor,ancestorSet);
	  }
	  ancestorSet.addAll(myCollection);
	}
      }
    }
    /*
     * Fill the GOID2ParentOrfs hashmap
     */
    GOID2ParentOrfs = new HashMap();
    for(Iterator GOIDIt = GOID2Parents.keySet().iterator();GOIDIt.hasNext();){
      String GOID = (String)GOIDIt.next();
      HashSet parentOrfSet = new HashSet();
      for(Iterator parentIt = ((Collection)GOID2Parents.get(GOID)).iterator();parentIt.hasNext();){
	parentOrfSet.addAll((Collection)GOID2Orfs.get(parentIt.next()));
      }
      GOID2ParentOrfs.put(GOID,parentOrfSet);
    }

        
    ORF2GOIDs = new HashMap();
    /*
     * Fill the 
     */
    for(Iterator GOIDIt = GOID2Orfs.keySet().iterator();GOIDIt.hasNext();){
      Object GOID = GOIDIt.next();
      for(Iterator orfIt = ((Collection)GOID2Orfs.get(GOID)).iterator();orfIt.hasNext();){
	Object orf = orfIt.next();
	if(!ORF2GOIDs.containsKey(orf)){
	  ORF2GOIDs.put(orf,new HashSet());
	}
	((Collection)ORF2GOIDs.get(orf)).add(GOID);
      }
    }
    System.err.println("Finished reading GO information");

    List nodesList = Cytoscape.getRootGraph().nodesList();
    Collections.shuffle(nodesList);
    int cross_validation_size = nodesList.size()/CROSS_VALIDATION_SPLIT;

    for(int idx=0;idx<CROSS_VALIDATION_SPLIT;idx++){
      HashSet hiddenNodes = new HashSet(nodesList.subList(idx*cross_validation_size,(idx+1)*cross_validation_size));
      int number_predictions = 0;
      int correct_predictions = 0;
    
      
      
      int progress = 0;
      for(Iterator complexIt = complexes.iterator(); complexIt.hasNext();){
	System.err.println(progress);
	Pathway currentModel = (Pathway)complexIt.next();
	/*
	 * For each complex, make predictions for any hidden nodes
	 */
	List predictions = makePredictions(currentModel,hiddenNodes);
	
	/*
	 * Evaluate predictions
	 */
	evaluatePredictions(predictions);

	/*
	 * For each prediction, add it to the prediction set, this may involve
	 * removing ancestor predictions from the old set of predictions
	 * make sure the count of predictions and correct predictions is updated correctly
	 */
	prediction_evaluation: 
	for(Iterator predictionIt = predictions.iterator();predictionIt.hasNext();){
	  Prediction currentPrediction = (Prediction)predictionIt.next();
	  Set ancestorSet = (Set)GOID2Ancestors.get(currentPrediction.GOID);
	  /*
	   * Check to see if any predictions will need to be removed
	   */
	  Collection previousPredictions = (Collection)ORF2Predictions.get(currentPrediction.ORF);
	  if(previousPredictions == null){
	    previousPredictions = new Vector();
	    ORF2Predictions.put(currentPrediction.ORF,previousPredictions);
	  }
	  
		
	  for(Iterator previousPredictionIt = previousPredictions.iterator();previousPredictionIt.hasNext();){
	    Prediction previousPrediction = (Prediction)previousPredictionIt.next();
	    /*
	     * Check to see if we are making an older prediction more specific
	     * We can probably optimize this to exit on the first
	     * found overlap, but I want to make sure I'm not
	     * making any mistakes
	     */
	    if(ancestorSet.contains(previousPrediction.GOID)){
	      previousPredictionIt.remove();
	      number_predictions--;
	      if(previousPrediction.correct){
		correct_predictions--;
	      }
	    }else{
	      Set previousAncestorSet = (Set)GOID2Ancestors.get(previousPrediction.GOID);
	      if(previousAncestorSet.contains(currentPrediction.GOID)){
		/*
		 * This is an ancestor prediction of a current
		 * prediction we want to ignore it
		 */
		continue prediction_evaluation;
	      }
	    }
	  }
	  /*
	   * If we got this far, it means that we weren't an ancestor of a current
	   * prediction, that means we can add this prediction to the known predictions
	   */
	  number_predictions++;
	  if(currentPrediction.correct){
	    correct_predictions++;
	  }
	  previousPredictions.add(currentPrediction);
	
	}
	/*
	 * Update the running accuracy tally
	 */
	prediction_count_history[progress] += number_predictions;
	prediction_correct_history[progress] += correct_predictions;
	complex_score_history[progress] = currentModel.score;
	progress++;
      }
    }
    try{
      FileWriter writer = new FileWriter("output.txt",false);
      for(int idx=0;idx < complexes.size();idx++){
	int count = prediction_count_history[idx];
	int correct = prediction_correct_history[idx];
	writer.write(""+complex_score_history[idx]+"\t"+correct+"\t"+count+"\t"+(correct/(double)count)+"\n");
      }
    }catch(Exception e){
      throw new RuntimeException();
    }

    
  }

  protected Collection determineAncestors(String GOID, HashMap GOID2Parents, HashMap GOID2Ancestors){
    if(GOID2Ancestors.containsKey(GOID)){
      return (Collection)GOID2Ancestors.get(GOID);
    }
    HashSet result = new HashSet();
    if(GOID2Parents.containsKey(GOID)){
      for(Iterator parentIt = ((Collection)GOID2Parents.get(GOID)).iterator();parentIt.hasNext();){
	String parent = (String)parentIt.next();
	result.add(parent);
	result.addAll(determineAncestors(parent,GOID2Parents,GOID2Ancestors));
      }
    }
    GOID2Ancestors.put(GOID,result);
    return result;
  }


  /**
   * Given a network model, make go annotation predictions for
   * hidden nodes
   */
  protected List makePredictions(Pathway model, Set hiddenOrfs){
    Vector results = new Vector();
    Vector visibleOrfs = new Vector();
    Vector invisibleOrfs = new Vector();
    for(Iterator orfIt = model.nodes.iterator();orfIt.hasNext();){
      Object orf = orfIt.next();
      if(!hiddenOrfs.contains(orf)){
	visibleOrfs.add(orf);
      }
      else{
	invisibleOrfs.add(orf);
      }
    }
    /*
     * Determine over-reprsented and majority GO categories
     */
    Vector categories = new Vector();
    for(Iterator GOIDIterator = GOID2Orfs.keySet().iterator();GOIDIterator.hasNext();){
      Object GOID = GOIDIterator.next();
      Set childOrfs = (Set)GOID2Orfs.get(GOID);
      Set parentOrfs = (Set)GOID2ParentOrfs.get(GOID);
      if(parentOrfs == null){
	continue;
      }
      int child_overlap = 0;
      int parent_overlap = 0;
      for(Iterator modelOrfIt = visibleOrfs.iterator();modelOrfIt.hasNext();){
	Object modelOrf = modelOrfIt.next();
	if(childOrfs.contains(modelOrf)){
	  child_overlap++;
	}
	if(parentOrfs.contains(modelOrf)){
	  parent_overlap++;
	}
      }
      /*
       * First check to see if we have a majority
       */
      if(child_overlap >= model.nodes.size()/2){
	double p_value = hypergeometric.cumulative(child_overlap-1,parent_overlap,parentOrfs.size()-parent_overlap,childOrfs.size());
	if(p_value < P_VALUE_CUTOFF){
	  categories.add(GOID);
	}
      }
    }
    
    /*
     * Predict those categories for hidden nodes
     */
    for(Iterator hiddenOrfIt = invisibleOrfs.iterator();hiddenOrfIt.hasNext();){
      Node hiddenOrf = (Node)hiddenOrfIt.next();
      for(Iterator categoryIt = categories.iterator();categoryIt.hasNext();){
	Prediction newPrediction = new Prediction();
	newPrediction.ORF = hiddenOrf;
	newPrediction.GOID = (String)categoryIt.next();
	results.add(newPrediction);
      }
    }
    return results;
  }

  /**
   * Given a list of GO annotation predictions
   * annotate which ones are present in the known database
   * Either as a prediction, or as an ancestor predictions
   */
  protected void evaluatePredictions(List predictions){
    for(Iterator predictionIt = predictions.iterator();predictionIt.hasNext();){
      Prediction prediction = (Prediction)predictionIt.next();
      /*
       * Check to see if this prediction is correct
       */
      if(ORF2GOIDs.containsKey(prediction.ORF)){
	if(((Set)ORF2GOIDs.get(prediction.ORF)).contains(prediction.GOID)){
	  prediction.correct = true;
	}
      }
    }
  }

  /**
   * Create a set of nodes from a colon-delimitd list
   * If a node correpsonding to a particular string
   * can not be found, a runtime exception will be thrown
   */
  protected Set string2NodeSet(String nodesString){
    Set result = new HashSet();
    String [] splat = nodesString.split("::");
    for(int idx = 0; idx < splat.length;idx++){
      Node node = Cytoscape.getCyNode(splat[idx]);
      if(node != null){
	result.add(node);
      }
    }
    return result;
	  
  }

  
}

class Prediction{
  public Node ORF;
  public boolean correct;
  public String GOID;
}
