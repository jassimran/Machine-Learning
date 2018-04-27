
package Assignment4.quickstart;

import Assignment4.quickstart.TestSmallGridWorld.ExampleRF;
import Assignment4.quickstart.TestSmallGridWorld.ExampleTF;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.frostbite.FrostbiteDomain;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.common.VisualActionObserver;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.EnvironmentServer;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.VisualExplorer;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.Visualizer;

import java.awt.*;
import java.util.List;


public class SampleExp {

	GridWorldDomain gwdg;
	FrostbiteDomain fbd;
	Domain domain;
	RewardFunction rf;
	TerminalFunction tf;
	StateConditionTest goalCondition;
	State initialState;
	HashableStateFactory hashingFactory;
	Environment env;


	public SampleExp(){		
		gwdg = new GridWorldDomain(5, 5);

		//gwd.setMapToFourRooms();
		gwdg.setObstacleInCell(0,1);
		//gwdg.setObstacleInCell(3,2);
		//gwdg.setObstacleInCell(3,1);
		gwdg.setObstacleInCell(2,3);
		gwdg.setObstacleInCell(2,2);
		gwdg.setObstacleInCell(2,1);
		gwdg.setObstacleInCell(1,3);
		gwdg.setObstacleInCell(1,2);
		gwdg.setObstacleInCell(3,3);

		//only go in intended directon 80% of the time
		gwdg.setProbSucceedTransitionDynamics(0.8);
		domain = gwdg.generateDomain();

		//get initial state with agent in 0,0
		initialState = GridWorldDomain.getOneAgentNoLocationState(domain);
		//all transitions return -1
		rf = new UniformCostRF();

		//terminate in top right corner
		tf = new GridWorldTerminalFunction(4,4);
		goalCondition = new TFGoalCondition(tf);
		
		hashingFactory = new SimpleHashableStateFactory();
		env = new SimulatedEnvironment(domain, rf, tf, initialState);

		VisualActionObserver observer = new VisualActionObserver(domain, 
									GridWorldVisualizer.getVisualizer(gwdg.getMap()));
		observer.initGUI();
		//TO-DO: get rid of bug in plotting!
		//env = new EnvironmentServer(env, observer);
		//((SADomain)domain).addActionObserverForAllAction(observer);
		
	}


	public void visualize(String outputpath){
		Visualizer v = GridWorldVisualizer.getVisualizer(gwdg.getMap());
		new EpisodeSequenceVisualizer(v, domain, outputpath);
	}

	
	//Value Iteration
	public void valueIterationExample(String outputPath){

		Planner plannerVI = new ValueIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 100);
		Policy pVI = plannerVI.planFromState(initialState);

		pVI.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "vi");

		//simpleValueFunctionVis((ValueFunction)planner, p);
		manualValueFunctionVis((ValueFunction)plannerVI, pVI);

	}
	//Policy Iteration
	public void policyIterationExample(String outputPath){

		Planner plannerPI = new PolicyIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 100, 100);
		Policy pPI = plannerPI.planFromState(initialState);

		pPI.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "pi");

		//simpleValueFunctionVis((ValueFunction)planner, p);
		manualValueFunctionVis((ValueFunction)plannerPI, pPI);
		
	}

	public void qLearningExample(String outputPath){

		//LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);
		Planner plannerQL = new QLearning(domain, 0.99, hashingFactory, 0., 1.);
		LearningAgent agent = (LearningAgent)plannerQL;
		plannerQL.setRf(rf);
		plannerQL.setTf(tf);
	
		Policy pQL = plannerQL.planFromState(initialState);
		


		//run learning for 50 episodes
		for(int i = 0; i < 5; i++){
			EpisodeAnalysis ea = agent.runLearningEpisode(env);

			ea.writeToFile(outputPath + "ql_" + i);
			System.out.println(i + ": " + ea.maxTimeStep());

			//reset environment for next learning episode
			env.resetEnvironment();
		}
		

		manualValueFunctionVis((ValueFunction)plannerQL, pQL);

	}


	/*public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){

		List<State> allStates = StateReachability.getReachableStates(initialState, 
									(SADomain)domain, hashingFactory);
		ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
											allStates, valueFunction, p);
		gui.initGUI();

	}
	*/

	public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){

		List<State> allStates = StateReachability.getReachableStates(initialState, 
									(SADomain)domain, hashingFactory);

		//define color function
		LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
		rb.addNextLandMark(0., Color.RED);
		rb.addNextLandMark(1., Color.BLUE);

		//define a 2D painter of state values, specifying which attributes correspond 
		//to the x and y coordinates of the canvas
		StateValuePainter2D svp = new StateValuePainter2D(rb);
		svp.setXYAttByObjectClass(TestSmallGridWorld.CLASSAGENT, TestSmallGridWorld.ATTX,
				TestSmallGridWorld.CLASSAGENT, TestSmallGridWorld.ATTY);


		//create our ValueFunctionVisualizer that paints for all states
		//using the ValueFunction source and the state value painter we defined
		ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(
												allStates, svp, valueFunction);

		//define a policy painter that uses arrow glyphs for each of the grid world actions
		PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
		spp.setXYAttByObjectClass(TestSmallGridWorld.CLASSAGENT, TestSmallGridWorld.ATTX,
				TestSmallGridWorld.CLASSAGENT, TestSmallGridWorld.ATTY);
		spp.setActionNameGlyphPainter(TestSmallGridWorld.ACTIONNORTH, new ArrowActionGlyph(0));
		spp.setActionNameGlyphPainter(TestSmallGridWorld.ACTIONSOUTH, new ArrowActionGlyph(1));
		spp.setActionNameGlyphPainter(TestSmallGridWorld.ACTIONEAST, new ArrowActionGlyph(2));
		spp.setActionNameGlyphPainter(TestSmallGridWorld.ACTIONWEST, new ArrowActionGlyph(3));
		spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);


		//add our policy renderer to it
		gui.setSpp(spp);
		gui.setPolicy(p);

		//set the background color for places where states are not rendered to grey
		gui.setBgColor(Color.GRAY);

		//start it
		gui.initGUI();

	}


	public void experimentAndPlotter(){

		//different reward function for more interesting results
		((SimulatedEnvironment)env).setRf(new GoalBasedRF(this.goalCondition, 5.0, -0.1));

		/**
		 * Create factories for Q-learning agent*/
		 
		LearningAgentFactory qLearningFactory = new LearningAgentFactory() {
			//@Override
			public String getAgentName() {
				return "Q-Learning";
			}

			//@Override
			public LearningAgent generateAgent() {
				return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.1);
			}
		};

		

		LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 10, 100, 
												qLearningFactory);
		exp.setUpPlottingConfiguration(500, 250, 2, 1000,
				TrialMode.MOSTRECENTANDAVERAGE,
				PerformanceMetric.CUMULATIVESTEPSPEREPISODE,
				PerformanceMetric.AVERAGEEPISODEREWARD);

		exp.startExperiment();
		exp.writeStepAndEpisodeDataToCSV("expData");
		 
	}


	public static void main(String[] args) {

		SampleExp example = new SampleExp();
		String outputPath = "output/";

		System.out.println("VI OUTPUT: ");
		example.valueIterationExample(outputPath);

		System.out.println(" PI OUTPUT");
		example.policyIterationExample(outputPath);

		System.out.println("Q LEARNING OUTPUT");
		example.qLearningExample(outputPath);

		example.experimentAndPlotter();

		example.visualize(outputPath);

	}


}

