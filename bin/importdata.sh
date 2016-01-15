#!/bin/sh

#
# Aliases :
#
alias connectmygoodstuffs='java -cp /usr/share/java/derbytools.jar:/usr/share/java/derby.jar org.apache.derby.tools.dblook -d jdbc:derby:/home/ken/dev/pmm/mygoodstuffs' 
alias connectpmm='java -cp /usr/share/java/derbytools.jar:/usr/share/java/derby.jar -Dij.protocol=jdbc:derby: -Dij.database=/home/ken/dev/pmm/pmm-dev\;create=true org.apache.derby.tools.ij'

#
# export data
#
connectmygoodstuffs exportdata.sql

#
# import data
#
connectpmm importdata.sql

