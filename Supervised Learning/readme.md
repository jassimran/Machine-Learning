#Supervised Leraning

Implemented following machine learning algorithms:
1. Decision trees with some form of pruning
2. Neural networks
3. Boosting
4. Support Vector Machines
5. k-nearest neighbors

I ran above algorithms on two classification problems.
## PROBLEM 1: 
**Car Evaluation Data Set:** This dataset is obtained from UCI dataset repository and it has 6 attributes and 1728 instances. The task is to classify the car as Acceptable- ‘acc’, Good- ‘good’, Unacceptable- ‘unacc’ and Very Good-‘vgood’ based on its price, technical/comfort features and safety.

##PROBLEM 2:
**Red Wine Data Set:** This dataset is obtained from UCI dataset repository and it has 12 attributes and 1599
instances. The task is to classify the wine quality as a score between 0-10 based on physicochemical and sensory
variables.

### ANALYSIS
Compared these different algorithms -  what changes were made to each of these algorithms to improve performance? How fast were they in terms of wall clock time? Iterations? Would cross validation help? How much performance was due to the  chosen problems? How about the values I chose for learning rates, stopping criteria, pruning methods, and so forth? Which algorithm performed best?

### TOOLS USED
I used Weka 3.6 for my analysis because it has an easy to understand GUI. Both my datasets were originally sorted and i used Randomizer Filter with seed value 42 to randomize it. Next I split them into 70% training and 30% test sets using percentage split filter. 

I also used weka to convert the csv files into arff file to be used for analysis through arff viewer in tools. Combination of weka Explorer, Experimenter and CLI was used to generate the data for my analysis as just one of them was not able to provide all the features i needed for my analysis. 

Most of the learning curves are performed through experimenter using Cross-Validation Producer and FilteredClassifier and then remove percentage  attribute was used.

[Please go through analysis for details]()
