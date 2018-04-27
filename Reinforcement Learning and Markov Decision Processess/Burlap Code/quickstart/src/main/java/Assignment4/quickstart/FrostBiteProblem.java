package Assignment4.quickstart;


import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.domain.singleagent.frostbite.FrostbiteDomain;
import burlap.domain.singleagent.frostbite.FrostbiteRF;
import burlap.domain.singleagent.frostbite.FrostbiteTF;
import burlap.domain.singleagent.frostbite.FrostbiteVisualizer;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.core.*;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.FullActionModel;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.common.SimpleAction;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.VisualExplorer;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.Visualizer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simplified version of the classic Atari Frostbite domain. In this game, the agent must jump between different
 * ice blocks. Each time the agent jumps on an ice block, it adds a layer to an igloo that is being built and "activates"
 * all ice blocks on the same row. Jumping on an activated ice block does not ad a layer to the igloo. Once all rows
 * of ice blocks are activated, they reset and can be activated by jumping on them again. Once the igloo is built,
 * the agent can go to it to win the game. If the agent jumps or walks into the water, the game is over.
 * <p>
 * <p>
 * If you run the main method of this class, it will launch of a visual explorer that you can play. They keys
 * w,s,a,d,x correspond to the actions jump north, jump south, move west, move east, do nothing. If you win or lose
 * the visual explorer will automatically terminate. If you want it to keep running, you can set this class' public static
 * {@link #visualizingDomain} data member to false.
 *
 * @author Phillipe Morere
 */
public class FrostBiteProblem{
	
	public static final String ACTIONNORTH = "north";
	public static final String ACTIONSOUTH = "south";
	public static final String ACTIONEAST = "east";
	public static final String ACTIONWEST = "west";
	public static final String ACTIONIDLE = "idle";

	FrostbiteDomain fd;
	Domain d;
	State s;
	RewardFunction rf;
	TerminalFunction tf;
	StateConditionTest goalCondition;
	State initialState;
	HashableStateFactory hashingFactory;
	Environment env;
	
	public FrostBiteProblem(){
		fd = new FrostbiteDomain();
		
		d = fd.generateDomain();
		s = fd.getCleanState(d);
		 
		rf = new FrostbiteRF(d);
		tf = new FrostbiteTF(d);
		initialState = fd.getCleanState(d);
			
		hashingFactory = new SimpleHashableStateFactory();
		env = new SimulatedEnvironment(d, rf, tf, initialState);
	}


	/**
	 * Main function to test the domain.
	 * Note: The termination conditions are not checked when testing the domain this way, which means it is
	 * impossible to win or die and might trigger bugs. To enable them, uncomment the code in the "update" function.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
				
		FrostBiteProblem example = new FrostBiteProblem();
		String outputPath = "output/";

		System.out.println("VI OUTPUT: ");
		example.valueIterationExample(outputPath);

		//System.out.println(" PI OUTPUT");
		//example.policyIterationExample(outputPath);

		System.out.println("Q LEARNING OUTPUT");
		example.qLearningExample(outputPath);


		
		
	}
	
	public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){

		List<State> allStates = StateReachability.getReachableStates(initialState, 
									(SADomain)d, hashingFactory);

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


	
	//Value Iteration
		public void valueIterationExample(String outputPath){

			Planner plannerVI = new ValueIteration(d, rf, tf, 0.99, hashingFactory, 0.001, 100);
			Policy pVI = plannerVI.planFromState(initialState);

			pVI.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "vi");
			Visualizer vis = FrostbiteVisualizer.getVisualizer(fd);
			VisualExplorer exp = new VisualExplorer(d, vis, s);

			exp.initGUI();

			//simpleValueFunctionVis((ValueFunction)planner, p);
			manualValueFunctionVis((ValueFunction)plannerVI, pVI);

		}

		//Policy Iteration
		public void policyIterationExample(String outputPath){

			Planner plannerPI = new PolicyIteration(d, rf, tf, 0.99, hashingFactory, 0.001, 100, 100);
			Policy pPI = plannerPI.planFromState(initialState);

			pPI.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "pi");

			//simpleValueFunctionVis((ValueFunction)planner, p);
			//manualValueFunctionVis((ValueFunction)plannerPI, pPI);
			
		}

		public void qLearningExample(String outputPath){

			//LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);
			Planner plannerQL = new QLearning(d, 0.99, hashingFactory, 0., 1.);
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

			//manualValueFunctionVis((ValueFunction)plannerQL, pQL);

		}


}

