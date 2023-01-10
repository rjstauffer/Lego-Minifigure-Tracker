# Lego-Minifigure-Tracker

This java project is intended to be used for my personal use in tracking the value of my Lego minifigure collection. 
The program utilizes data from bricklink.com in the form of an XML file which includes a lot of information, but I am
only interested in the minifgure ID, which consists of the theme (sw for star wars, hp for harry potter) and specific
number of the minifigure in the theme, as well as its average selling price over the last 30 days. This information is 
parsed and put into a map, which is then put into another map which includes the keys as dates and the values are the
list of IDs and prices at a given date. The program can determine the current value of my collection, as well as show me
the percent gain/loss of each figure from its previous entry to the current one. The program also allows me to view the 
collection with each date in the form of a CSV file, although it is currently a work in progress since the file does
not seperate the data into individual entries.

Note that this program is not automated. In order to add new data, it is necessary to download the wish list from bricklink.com
and manually add it to the program. 
