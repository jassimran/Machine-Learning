# Unsupervised Learning and Dimensionality Reduction

**Implemented the following two clustering algorithms:**
1. k-means clustering
2. Expectation Maximization


**Implemented the below dimensionality reduction algorithms:**
1. Principal Component Analysis (PCA)
2. Independent Component Analysis (ICA)
3. Randomized Projections (RP)
4. Information Gain (IG)

**These were run on below two datasets:**
## DATASET 1:
**Whole Sale Customer Data Set:** This dataset is obtained from UCI dataset repository and it has 8 attributes and 440 instances. It is a classification problem where Customer’s region can be classified as 1,2 3 based on their purchase of goods. 

## DATASET 2:
**Red Wine Data Set:** This dataset is obtained from UCI dataset repository and it has 12 attributes and 1599 instances. The task is to classify the wine quality as a score between 0-10 based on physicochemical and sensory variables but current dataset has score values from 3-8 only.

### AIM OF ANALYSIS:
1. Run the clustering algorithms on the datasets
2. Apply the dimensionality reduction algorithms on the datasets
3. Reproduce your clustering experiments, but on the data after applying dimensionality reduction on it.
4. Apply the clustering algorithms to the same dataset to which you just applied the dimensionality reduction algorithms.
5. My analysis explanations how k was chosen, what kind of clusters were formed? Do these clusters make "sense"? Did the clusters line up with the labels? Do they otherwise line up naturally?
6. Different algorithms are compared for their performance and how the data looks in the new spaces created with the various aglorithms? 
7. For PCA, distribution of eigenvalues is shown. 
8. For ICA, how kurtotic are the distributions is shown? 
9. How well is the data reconstructed by the randomized projections? How much variation did you get when you re-ran your RP several times.
10. When clustering experiments were reproduced on the datasets projected onto the new spaces created by ICA, PCA and RP, how different were the clusters.

For details, please see my [analysis](https://github.com/jassimran/Machine-Learning/blob/master/Unsupervised%20Learning%20and%20Dimensionality%20Reduction/Analysis%20Unsupervised%20Learning%20and%20Dimensionality%20Reduction.pdf)
