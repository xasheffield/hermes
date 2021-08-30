This program is split into four packages:
- Data
- Graphing
- GUI
- I0
- UnitTests

Data contains models of the Data the software will handle at various levels of granularity. A DataFile object contains
a number of XRaySamples. DataType refers to what is being measured in a given reading of a sample (e.g. energy, theta, etc.), whereas
MeasurementType refers to what is being measured in a given file (e.g. I0, It, etc.).

Graphing contains Grapher - a class which takes DataFile objects and parameters to display pop-up windows containing
either 1-many data sets on separate axes, or 1-many data sets on a single set of axes (offset in the y axis by a
user specified amount).

GUI