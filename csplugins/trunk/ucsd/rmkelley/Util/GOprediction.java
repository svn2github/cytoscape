package ucsd.rmkelley.Util;
import java.io.*;
import java.util.*;
import cytoscape.*;
import giny.model.Node;
import DistLib.*;
public class GOprediction{
  double P_VALUE_CUTOFF = .0000001;
  double SIZE_CUTOFF = 1000;
  int CROSS_VALIDATION_SPLIT = 5;
  /*
   * GOID2Orfs maps from each GOID to each orf and child orf
   * GOID2ParentOrfs maps from each GOID to each orf which is member or
   *    child of a parent
   * GOID2Ancestors maps from each GOID to all ancestors
   * ORF2GOID maps from each ORF to all GOIDs which are a parent
   */
  HashMap ORF2GOIDs, GOID2Orfs,GOID2ParentOrfs, GOID2Ancestors ;
  
  /**
   * The networkmodels should be in sorted order (descending score)
   * GOID2Orfs maps from each GOID to a direct orf
   * GOID2Parents maps from each GOID to a set of parent GOIDs
   */
  public GOprediction(File GOIDOrfs, File GOIDParents){
    /*
     * Initialize some record keeping data structures
     */
    System.err.println("Parsing GO hierarchy");


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
    GOID2Ancestors = new HashMap();
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
  }

  /**
   * This method will return a hashmap that maps from each
   * node to the average GO agreement with all nodes
   * in that complex
   */
 //  public HashMap pathwayAssessment(Collection pathways){
//      /*
//       * First figure out what is the best scoring pathway for each node
//      */
//     HashMap result = new HashMap();
//     HashMap node2BestPathway = new HashMap();
//     for(Iterator pathwayIt = pathways.iterator();pathwayIt.hasNext();){
//       Pathway pathway = (Pathway)pathwayIt.next();
//       for(Iterator nodeIt = pathway.nodes.iterator();nodeIt.hasNext();){
// 	Node node = (Node)nodeIt.next();
// 	if(!node2BestPathway.containsKey(node)){
// 	  node2BestPathway.put(node, pathway);
// 	}
// 	else{
// 	  Pathway oldPathway = (Pathway)node2BestPathway.get(node);
// 	  if(pathway.score > oldPathway.score){
// 	    node2BestPathway.put(node,pathway);
// 	  }
// 	}
//       }
//     }
    
//     for(Iterator nodeIt = node2BestPathway.keySet().iterator();nodeIt.hasNext();){
//       Object node = nodeIt.next();
//       Pathway pathway = (Pathway)node2BestPathway.get(node);
//       result.put(node,getAverageDistance((Node)node,pathway));
//     }
//     return result;
    
//   }

  public double getAverageDistance(Node node, Collection neighbors){
    return getAverageDistance(node, neighbors, new HashSet(neighbors));
  }

  public double getAverageDistance(Node node, Collection neighbors, Collection restriction){
    int sum = 0;
    int count = 0;
    Set GOIDs = (Set)ORF2GOIDs.get(node);
    if(GOIDs == null){
      System.err.println("node not found");
      return 12345568.0;
    }
    for(Iterator nodeIt = neighbors.iterator();nodeIt.hasNext();){
      Node otherNode = (Node)nodeIt.next();
      if(otherNode == node || !restriction.contains(otherNode)){
	continue;
      }
      int minimum = Integer.MAX_VALUE;
      Set otherGOIDs = (Set)ORF2GOIDs.get(otherNode);
      if(otherGOIDs == null){
	continue;
      }
      for(Iterator IDIt = otherGOIDs.iterator();IDIt.hasNext();){
	String ID = (String)IDIt.next();
	if(GOIDs.contains(ID)){
	  int size = ((Set)GOID2Orfs.get(ID)).size();
	  minimum = Math.min(size,minimum);
	}
      }
      if(minimum > 100000){
	throw new RuntimeException("Didn't share top-level category, something wrong");
      }
      sum += minimum;
      count += 1;
    }
    return sum/(double)count;
  }

  public void crossValidate(Collection complexes){
    HashMap ORF2Predictions = new HashMap();
    int [] prediction_count_history = new int[complexes.size()];
    int [] prediction_correct_history = new int[complexes.size()];
    double [] complex_score_history = new double[complexes.size()];
            
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
	 * For each complex, find signficiant categories
	 */
	List categories = findCategories(currentModel,hiddenNodes);
	List predictions = new Vector();

	/*
	 * Predict those categories for hidden nodes
	 */
	for(Iterator hiddenOrfIt = currentModel.nodes.iterator();hiddenOrfIt.hasNext();){	  
	  Node hiddenOrf = (Node)hiddenOrfIt.next();
	  if(!hiddenNodes.contains(hiddenOrf)){
	    continue;
	  }
	  for(Iterator categoryIt = categories.iterator();categoryIt.hasNext();){
	    Prediction newPrediction = new Prediction();
	    newPrediction.ORF = hiddenOrf;
	    newPrediction.GOID = (String)categoryIt.next();
	    predictions.add(newPrediction);
	  }
	}
   	
	/*
	 * Evaluate predictions
	 */
	evaluatePredictions(predictions);
	int [] result = combinePredictions(predictions, ORF2Predictions);
	correct_predictions += result[0];
	number_predictions += result[1];
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


  public void makePredictions(Collection complexes, File screenPredictions){
    try{
      BufferedReader reader = new BufferedReader(new FileReader(screenPredictions));
      Vector screenList = new Vector();
      while(reader.ready()){
	screenList.add(new Prediction(reader.readLine()));
      }
      makePredictions(complexes,screenList);
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException();
    }
  }
  /**
   * screenPredictions is a list of predictions that we should ignore
   * if we find
   */
  public void makePredictions(Collection complexes, List screenPredictions){
    HashMap ORF2Predictions = new HashMap();
    if(screenPredictions != null){
      for(Iterator screenPredictionIt = screenPredictions.iterator();screenPredictionIt.hasNext();){
	Prediction screenPrediction = (Prediction)screenPredictionIt.next();
	screenPrediction.flagged = true;
      }
      combinePredictions(screenPredictions, ORF2Predictions);
    }

    for(Iterator complexIt = complexes.iterator();complexIt.hasNext();){
      Pathway currentModel = (Pathway)complexIt.next();
      List categories = findCategories(currentModel, null);
      List predictions = new Vector();
      for(Iterator orfIt = currentModel.nodes.iterator();orfIt.hasNext();){
	Node orf = (Node)orfIt.next();
	Set orfCategories = (Set)ORF2GOIDs.get(orf);
	for(Iterator categoryIt = categories.iterator();categoryIt.hasNext();){
	  Object category = categoryIt.next();
	  if(orfCategories == null || !orfCategories.contains(category)){
	    Prediction newPrediction = new Prediction();
	    newPrediction.ORF = orf;
	    newPrediction.GOID = (String)category;
	    predictions.add(newPrediction);
	  }
	}
      }
      combinePredictions(predictions,ORF2Predictions);
    }
    try{
      FileWriter writer = new FileWriter("predictions.txt",false);
      for(Iterator predictionCollectionIt = ORF2Predictions.values().iterator();predictionCollectionIt.hasNext();){
	Collection predictionCollection = (Collection)predictionCollectionIt.next();
	for(Iterator predictionIt = predictionCollection.iterator();predictionIt.hasNext();){
	  Prediction prediction = (Prediction)predictionIt.next();
	  if(!prediction.flagged){
	    writer.write(""+prediction+"\n");
	  }
	}
      }
      writer.close();
    }catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException();
    }
  }


  protected int [] combinePredictions(Collection predictions, HashMap ORF2Predictions){
    /*
     * For each prediction, add it to the prediction set, this may involve
     * removing ancestor predictions from the old set of predictions
     * make sure the count of predictions and correct predictions is updated correctly
     */
    int number_predictions = 0;
    int correct_predictions = 0;
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
	 * Check to see if this exact prediction already exists
	 */
	if(previousPrediction.GOID.equals(currentPrediction.GOID)){
	  continue prediction_evaluation;
	}
	/*
	 * Check to see if we are making an older prediction more specific
	 * We can probably optimize this to exit on the first
	 * found overlap, but I want to make sure I'm not
	 * making any mistakes
	 */
	if(ancestorSet.contains(previousPrediction.GOID)){
	  previousPredictionIt.remove();
	  number_predictions--;
	  if(previousPrediction.flagged){
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
      if(currentPrediction.flagged){
	correct_predictions++;
      }
      previousPredictions.add(currentPrediction);
	  
    }
    
    return new int[]{number_predictions,correct_predictions};
  }

    
  /**
   * Given a network model, make go annotation predictions for
   * hidden nodes
   */
  protected List findCategories(Pathway model, Set hiddenOrfs){
    Vector results = new Vector();
    Vector visibleOrfs = new Vector();
    Vector invisibleOrfs = new Vector();
    for(Iterator orfIt = model.nodes.iterator();orfIt.hasNext();){
      Object orf = orfIt.next();
      if(hiddenOrfs == null || !hiddenOrfs.contains(orf)){
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
      if(childOrfs.size() > SIZE_CUTOFF){
	continue;
      }
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
   
    return categories;
   
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
	  prediction.flagged = true;
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
  public boolean flagged;
  public String GOID;
  public Prediction(){}
  public Prediction(String s){
    String [] splat = s.split("\t");
    ORF = Cytoscape.getCyNode(splat[0]);
    flagged = Boolean.valueOf(splat[1]).booleanValue();
    GOID = splat[2];
  }
  public String toString(){
    return ORF.toString()+"\t"+flagged+"\t"+GOID;
  }
}
