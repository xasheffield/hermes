This main file in this program is CoreWorker.java. This file reads information from the experimental data files (using the FileInput.java class), and stores the relevant data in a custom data type (XRaySample.java), to facilitate any processing of the data which may later be necessary. CoreWorker can then call a Python script, passing it the data to plot as a parameter. This Python script uses MatPlotLib to plot the data passed into it, generating an interactive window of subplots. This could have been done more simply and succinctly, but was done with the intention of being robust, and a base for a GUI based piece of software.


Run this program by navigating to directory XRayData in a terminal instance, and executing:
java CoreWorker x
Where x is a number from 1-4. If no parameter is provided, it will default to 1.
1 = With Sample
2 = Without Sample
3 = Background With Sample
4 = Background Without Sample

