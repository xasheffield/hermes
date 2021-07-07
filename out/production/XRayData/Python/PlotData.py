#!/usr/bin/env python
import copy
import sys
import numpy as np
import matplotlib.pyplot as plt
"""
main takes paths to files to plot as arguments
"""
def main():

    rawData = copy.deepcopy(sys.argv[1:]) #The 0th argument is the script path, so we ignore it

    processedData = []
    for x in rawData:
        processedData.append(parse_data(x))

    fileDictionary = load_files(rawData)

    points = ['bo', 'go', 'ro', 'yo', 'co', 'mo', 'bo', 'go', 'ro', 'yo', 'co', 'mo']
    fig, axes = plt.subplots(2, 4)

    for y, file in enumerate(fileDictionary):
        if y >= 4:
            indX, indY = 1, y-4
        else:
            indX, indY = 0, y
        data = fileDictionary.get(file)
        axes[indX, indY].plot(data[:, 0], data[:, 8], points[y], markersize=1)
        axes[indX, indY].set_xlabel('Energy (eV)')
        axes[indX, indY].set_ylabel('XRay Counts')
        #axes[indX, indY].title.set_text()

    fig.tight_layout() #prevents subplots from overlapping
    fig.set_size_inches(14, 7)
    plt.show()


def load_files(filePathList):
    fileDictionary = {}
    for path in filePathList: # Iterate through files in a directory
        file = open(path, "r")
        fileData = []
        for line in file: # Iterate through lines in a file
            if (line[0].isnumeric()): # Only data containing lines start with a number
                data = line[:-1].split() # Final two characters are new line
                dataVector = list(map(float, data)) # Convert strings to floats and store in list
                fileData.append(dataVector)
        npFileData = np.array(fileData)
        fileDictionary[file.buffer.name] = npFileData
        file.close()
    return fileDictionary

"""
This processes one script argument, corresponding to one file of data. It returns the data as a list of tuples
in the form [(energy, theta, cnts_per_live)]
"""
def parse_data(dataAsString):
    dataList = dataAsString.split('!') #Makes a list of inidividual samples
    data = []
    for x in dataList[:-1]: #
        tupleData = tuple(float(y) for y in x.split(",")) #Formats each sample
        data.append(tupleData)
    return data

if __name__ == '__main__':
    main()

