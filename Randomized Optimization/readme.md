# Randomized Optimization

## Implemented below four local random search algorithms:
1. Randomized hill climbing
2. Simulated annealing
3. A genetic algorithm
4. MIMIC

## Problems:
1. **Flip Flop function** returns the number of times bits alternate in a bitstring, For example: “0011” would return 2.
“0101” would return 4. The optimal solution produces a constantly alternating bitstring with a return value equal
to the length of the string.
2. **TravelingSalesmanProblem:** In traveling salesman problem (TSP), a salesman needs to find the shortest path
to visit all of the N cities (nodes in a graph with N vertexes. This problem is difficult to solve due to the fact that
there are N-1 factorial possible routes that the salesman may take to complete the trip.
3. **Four Peaks Problem:** The four peaks problem is a classic optimization problem and has a total of four
maxima’s (4 peaks) out of which two are global maxima and two are suboptimal local maxima. Four peaks
function takes two inputs: (1) a bitstring of length N which represents the size of the input vector; (2) the size of
the basin of attraction T of local maxima’s.

## Analysis: 
These different problems highlight advantages of GAs, SA, and MIMIC. First three algorithms were used to find good weights for a neural network.

## Tools Used
I used Abagail for my analysis because it had mostly what was needed to complete this assignment. I started my debugger and ran all experiments to understand how it works. I modified the test files to perform different experiments. Graphs were drawn using Plotly.

For details, please see [analysis](Machine-Learning/Randomized Optimization/Analysis Randomized Optimization.pdf)
