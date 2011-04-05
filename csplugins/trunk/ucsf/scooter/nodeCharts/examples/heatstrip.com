session open file="/Users/scooter/Documents/galFiltered.cys"
nodecharts heatstrip nodelist="all" attributelist="gal1RGexp,gal4RGexp,gal80Rexp" colorlist="redgreen" normalize="true" showlabels="false" size="40x60" position=south

network view update
