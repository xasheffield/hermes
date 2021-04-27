#!/usr/bin/env python
import copy
import sys

import matplotlib.pyplot as plt
"""
This script receives blocks of data in the form (x1,x2,x3!x4...,xn) (x1,x2,x3!x4...,xn) 
"""
def main():

    rawData = copy.deepcopy(sys.argv[1:]) #The 0th argument is the script path, so we ignore it
    processedData = []
    for x in rawData:
        processedData.append(parse_data(x))

    points = ['b.', 'g.', 'r.', 'y.', 'c.', 'm.', 'b.', 'g.', 'r.', 'y.', 'c.', 'm.',]
    fig, axes = plt.subplots(2, 4)
    for y in range(0, 8): # len(processedData)):
        if y >= 4:
            indX, indY = 1, y-4
        else:
            indX, indY = 0, y
        for x in processedData[y]:
            axes[indX, indY].plot(x[0], x[2], points[y])
            axes[indX, indY].set_xlabel('Energy (eV)')
            axes[indX, indY].set_ylabel('XRay Counts')
    # for row in axes:
    #    for column in row:
    #         column.title.set_text()

    fig.tight_layout()#prevents subplots from overlapping
    fig.set_size_inches(14, 7)
    plt.show()


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

