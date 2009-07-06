package PinnacleZPlugin;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;
import java.io.BufferedReader;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.groups.CyGroupManager;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

import oiler.Graph;
import oiler.LinkedListGraph;
import oiler.TypeConverter;
import oiler.util.IntIterator;

import modlab.Search;
import modlab.Randomize;
import modlab.util.SearchExecutor;

import pinnaclez.Activity;
import pinnaclez.ActivityRandomize;
import pinnaclez.AbstractActivityScore;
import pinnaclez.ExpressionMatrix;
import pinnaclez.GreedySearch;
import pinnaclez.MIScore;
import pinnaclez.ST1Filter;
import pinnaclez.ST2Filter;
import pinnaclez.ST3Filter;
import pinnaclez.TScore;
import pinnaclez.Trials;
import pinnaclez.io.ClassReader;
import pinnaclez.io.ExpressionMatrixReader;
import pinnaclez.io.NetworkReader;
import pinnaclez.io.ParsingException;

public class SearchTask implements Task
{
	SearchPanel searchPanel;
	TaskMonitor taskMonitor = null;
	boolean needsToHalt = false;
	SearchExecutor.ProgressMonitor monitor = null;
	static int numOfRuns = 1;

	public SearchTask(SearchPanel searchPanel)
	{
		this.searchPanel = searchPanel;
	}

	public void run()
	{
		//
		// Stage 1.A: Read class file
		//
		
		setPercentCompleted(0);
		setStatus("Reading class file...");
		Map<String,Integer> classMap = null;
		try
		{
			classMap = ClassReader.read(searchPanel.getClassFile().getPath());
		}
		catch (ParsingException e)
		{
			setException(e, e.getMessage());
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 1.B: Read expression matrix file
		//

		setPercentCompleted(5);
		setStatus("Reading expression matrix file...");
		ExpressionMatrix matrix = null;
		try
		{
			matrix = ExpressionMatrixReader.read(classMap, searchPanel.getExpressionMatrixFile().getPath());
		}
		catch (ParsingException e)
		{
			setException(e, e.getMessage());
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 1.C: Read network file
		//

		setPercentCompleted(10);
		setStatus("Reading network...");
		Graph<Activity,String> network = null;
		try
		{
			BufferedReader buffer = CyNetworkConverter.convert(searchPanel.getNetwork());
			network = NetworkReader.read(matrix, buffer);
		}
		catch (ParsingException e)
		{
			setException(e, e.getMessage());
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 2.A: Setup search algorithm
		//

		Search<Activity,String> search = new GreedySearch<Activity,String>(searchPanel.getMaxModuleSize(),
										searchPanel.getMaxRadius(),
										searchPanel.getMaxNodeDegree(),
										searchPanel.getMinImprovement());

		//
		// Stage 2.B: Setup score algorithm
		//

		AbstractActivityScore score = null;
		if (searchPanel.getScoreModel().equals("Mutual Information"))
			score = new MIScore(matrix);
		else
			score = new TScore(matrix);
		
		//
		// Stage 2.C: Setup randomizing algorithm
		//

		Randomize<Activity,String> randomize = new ActivityRandomize();

		//
		// Stage 2.D: Setup SearchExecutor
		//

		SearchExecutor.Duplicate<Activity,String> duplicate = new SearchExecutor.Duplicate<Activity,String>()
		{
			public Graph<Activity,String> duplicate(Graph<Activity,String> network)
			{
				TypeConverter<Activity,String,Activity,String> converter = new TypeConverter<Activity,String,Activity,String>()
				{
					public Activity convertNodeObject(Activity nodeObject)
					{
						Activity newActivity = new Activity();
						newActivity.name = nodeObject.name;
						newActivity.matrixIndex = nodeObject.matrixIndex;
						return newActivity;
					}

					public String convertEdgeObject(String edgeObject)
					{
						return edgeObject;
					}
				};

				return new LinkedListGraph<Activity,String>(network, converter);
			}
		};

		monitor = new SearchExecutor.ProgressMonitor()
		{
			public void setPercentCompleted(double percent)
			{
				SearchTask.this.setPercentCompleted((int) (15 + 50 * percent));
			}
		};

		//
		// Stage 2.E: Run SearchExecutor
		//

		setPercentCompleted(15);
		setStatus("Searching for modules...");
		int numOfThreads = Runtime.getRuntime().availableProcessors();
		List<List<Graph<Activity,String>>> searchTrials = SearchExecutor.execute(network, search, score, randomize, duplicate, monitor, searchPanel.getNumOfTrials(), numOfThreads);

		monitor = null;

		if (needsToHalt) return;

		//
		// Stage 2.F: Collect random trials
		//

		setPercentCompleted(65);
		setStatus("Collecting random trials...");
		List<Graph<Activity,String>> modules = searchTrials.remove(0);
		Trials trials = new Trials(searchTrials);
		searchTrials = null;

		if (needsToHalt) return;

		//
		// Stage 3.A: Run ST1
		//

		setPercentCompleted(70);
		setStatus("Filtering modules with ST1...");
		ST1Filter<Activity,String> st1Filter = new ST1Filter<Activity,String>(trials, searchPanel.getST1Cutoff());
		modules = st1Filter.filter(modules);
		if (modules.size() == 0)
		{
			setException(new Exception(), "No modules passed ST1.");
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 3.B: Run ST2
		//

		setPercentCompleted(75);
		setStatus("Filtering modules with ST2...");
		ST2Filter<Activity,String> st2Filter = null;
		if (searchPanel.getScoreModel().equals("Mutual Information"))
			st2Filter = new ST2Filter<Activity,String>(trials, ST2Filter.Distribution.GAMMA, searchPanel.getST2Cutoff());
		else
			st2Filter = new ST2Filter<Activity,String>(trials, ST2Filter.Distribution.NORMAL, searchPanel.getST2Cutoff());
		modules = st2Filter.filter(modules);
		if (modules.size() == 0)
		{
			setException(new Exception(), "No modules passed ST2.");
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 3.C: Run ST3
		//

		ST3Filter st3Filter = new ST3Filter(network, matrix, score, searchPanel.getNumOfST3Trials(), searchPanel.getST3Cutoff());
		modules = st3Filter.filter(modules);
		if (modules.size() == 0)
		{
			setException(new Exception(), "No modules passed ST3.");
			return;
		}

		if (needsToHalt) return;
		
		//
		// Stage 4: Display results
		//

		//List<Result> results = new ArrayList<Result>();
		int i = 1;
		for (Graph<Activity,String> module : modules)
		{
			Result result = new Result();
			final IntIterator iterator = module.nodes();
			final int startNode = iterator.next();
			result.startNode = module.nodeObject(startNode).name;
			result.memberNodes = new ArrayList<String>();
			result.memberNodes.add(module.nodeObject(startNode).name);
			final List<CyNode> groupNodes = new ArrayList<CyNode>();

			while (iterator.hasNext())
			{
				final int member = iterator.next();
				result.memberNodes.add(module.nodeObject(member).name);
				groupNodes.add( Cytoscape.getCyNode(module.nodeObject(member).name) );
			}

			result.moduleScore = module.score();
			result.st1Pval = st1Filter.pValue(module);
			result.st2Pval = st2Filter.pValue(module);
			result.st3Pval = st3Filter.pValue(module);

			CyGroupManager.createGroup("Module " + Integer.toString(i++), groupNodes, null);
		
		}

	}

	public void halt()
	{
		needsToHalt = true;
		if (monitor != null)
			monitor.halt();
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) 
	{
		this.taskMonitor = taskMonitor;
	}

	public String getTitle()
	{
		return "PinnacleZ";
	}

	private void setPercentCompleted(int percent)
	{
		if (taskMonitor != null)
			taskMonitor.setPercentCompleted(percent);
	}

	private void setStatus(String message)
	{
		if (taskMonitor != null)
			taskMonitor.setStatus(message);
	}

	private void setException(Throwable t, String message)
	{
		if (taskMonitor != null)
			taskMonitor.setException(t, message);
	}
}
