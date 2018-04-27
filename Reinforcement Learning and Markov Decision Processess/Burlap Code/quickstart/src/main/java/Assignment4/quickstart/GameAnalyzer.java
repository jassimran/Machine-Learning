package Assignment4.quickstart;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.VisualExplorer;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.Visualizer;

public class GameAnalyzer {

	public static void main(String[] args) {

		MazeGame maze = new MazeGame();
		Domain domain = maze.generateDomain();
		State initialState = MazeGame.getExampleState(domain);
		RewardFunction rf = new MazeGame.ExampleRF(MazeGame.X-1, MazeGame.Y-1);
		TerminalFunction tf = new MazeGame.ExampleTF(MazeGame.X-1, MazeGame.Y-1);
		SimulatedEnvironment env = new SimulatedEnvironment(domain, rf, tf, initialState);
		StateConditionTest goalCondition = new TFGoalCondition(tf);
		
		visualizeInitialMaze(domain, maze, env);
		runValueIteration(maze,domain,initialState, rf, tf);
		runPolicyIteration(maze,domain,initialState, rf, tf);
		runQLearning(maze, domain, initialState, rf, tf, env);
		runQlearningExperiment(domain, env, goalCondition);
	}

	private static void visualizeInitialMaze(Domain domain,	MazeGame maze, SimulatedEnvironment env) {
		Visualizer v = maze.getVisualizer();
		VisualExplorer exp = new VisualExplorer(domain, env, v);

		exp.addKeyAction("w", MazeGame.ACTIONNORTH);
		exp.addKeyAction("s", MazeGame.ACTIONSOUTH);
		exp.addKeyAction("d", MazeGame.ACTIONEAST);
		exp.addKeyAction("a", MazeGame.ACTIONWEST);

		exp.initGUI();
	}

	private static void runValueIteration(MazeGame maze, Domain domain, State initialState, RewardFunction rf, TerminalFunction tf) {		
		System.out.println("----------Value Iteration Analysis----------");
	
		ValueIteration vi = new ValueIteration(domain, rf, tf, 0.99, new SimpleHashableStateFactory(), 0.001, 100);
		
		// run planning from the initial state
		Policy p = vi.planFromState(initialState);
		List<State> allStates = vi.getAllStates();
		
		// evaluate the policy with one roll out visualize the trajectory
		EpisodeAnalysis ea = p.evaluateBehavior(initialState, rf, tf);
		
		Visualizer v = maze.getVisualizer();
		new EpisodeSequenceVisualizer(v, domain, Arrays.asList(ea));
		
		simpleValueFunctionVis(allStates, vi, p);
	}
		
	private static void runPolicyIteration(MazeGame maze, Domain domain, State initialState, RewardFunction rf, TerminalFunction tf) {
		System.out.println("###############################################\n");
		System.out.println("----------Policy Iteration Analysis----------");
		
		PolicyIteration pi = new PolicyIteration(domain, rf, tf, 0.99, new SimpleHashableStateFactory(), .5, 100, 100);
		
		// run planning from our initial state
		Policy p = pi.planFromState(initialState);
		List<State> allStates = pi.getAllStates();
		
		// evaluate the policy with one roll out visualize the trajectory
		EpisodeAnalysis ea = p.evaluateBehavior(initialState, rf, tf);

		Visualizer v = maze.getVisualizer();
		new EpisodeSequenceVisualizer(v, domain, Arrays.asList(ea));
		
		simpleValueFunctionVis(allStates, pi, p);
	}
	
	public static void simpleValueFunctionVis(List<State> allStates, ValueFunction valueFunction, Policy p) {
		ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(allStates, valueFunction, p);
		gui.initGUI();
	}

	private static void runQLearning(MazeGame maze, Domain domain, State initialState, RewardFunction rf, TerminalFunction tf, SimulatedEnvironment env) {
		System.out.println("#########################################\n");
		System.out.println("----------Q Learning Analysis----------");
		
		QLearning agent = new QLearning(domain, 0.99, new SimpleHashableStateFactory(), 0.1, 0.1);
		
		// run Q-learning and store results in a list
		int totalTime = 0;
		int steps = 0;
		PolicyIteration pi = null;
		Policy p = null;
		EpisodeAnalysis ea = null;
		List<State> allStates = null;
		List<EpisodeAnalysis> episodes = new ArrayList<EpisodeAnalysis>(1000);
		final XYSeriesCollection dataset = new XYSeriesCollection();
		
		for (int i = 0; i < 10; i++) {
			ea = agent.runLearningEpisode(env,100);
			episodes.add(ea);
			if (steps < ea.numTimeSteps()) {
				steps = ea.numTimeSteps();
				pi = new PolicyIteration(domain, rf, tf, 0.99, new SimpleHashableStateFactory(), .5, 100, 100);
				p = pi.planFromState(initialState);
				allStates = pi.getAllStates();				
			}
			
			XYSeries line = new XYSeries("Series"+i);
			double cuRewards = 0;
			for (int j=0;j<ea.rewardSequence.size()-1;j++) {
				cuRewards += ea.rewardSequence.get(j);
				line.add(j, cuRewards);
				//System.out.println("@@@@@@@@@@Total rewards: " + j + ": " + + cuRewards);
			}
			
			dataset.addSeries(line);
			System.out.println(i + ": " + ea.numTimeSteps());
			//System.out.println(i + ": " + ea.rewardSequence.get(0));
			//System.out.println(i + ": " + ea.getActionSequenceString());
			totalTime = totalTime + ea.numTimeSteps();
			env.resetEnvironment();
		}
		
		PlotChart chart = new PlotChart("Chat", dataset);
		chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
        
		System.out.println("Total iterations/steps: " + totalTime);
		Visualizer v = maze.getVisualizer();
		new EpisodeSequenceVisualizer(v, domain, episodes);	
		
		simpleValueFunctionVis(allStates, pi, p);
	}
	
	public static void runQlearningExperiment(final Domain domain, SimulatedEnvironment env, StateConditionTest goalCondition) {
		
		// different reward function for more interesting results
		((SimulatedEnvironment) env).setRf(new GoalBasedRF(goalCondition, 2.0, -0.04));
		// for 25x25 maze
		//((SimulatedEnvironment) env).setRf(new GoalBasedRF(goalCondition, 10.0, -0.01));
		
		/**
		 * Create factories for Q-learning agent and SARSA agent to compare
		 */
		LearningAgentFactory qLearningFactory = new LearningAgentFactory() {
			//@Override
			public String getAgentName() {
				return "Q-Learning";
			}
			
			//@Override
			public LearningAgent generateAgent() {
				return new QLearning(domain, 0.99, new SimpleHashableStateFactory(), 0.5, 0.9);
			}
		};
		
		LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 10, 100, qLearningFactory);
		exp.setUpPlottingConfiguration(500, 250, 2, 1000,
				TrialMode.MOSTRECENTANDAVERAGE,
				//PerformanceMetric.CUMULATIVESTEPSPEREPISODE,
				PerformanceMetric.CUMULATIVEREWARDPERSTEP,
				PerformanceMetric.AVERAGEEPISODEREWARD);
		
		exp.startExperiment();
		exp.writeStepAndEpisodeDataToCSV("expData");
	}
	
}
