#!/bin/sh

#
# Variables
#
# macbook
#
DERBY_10_10_JAR_FOLDER="/Users/ken/db-derby-10.10.2.0-bin/lib"
DERBY_10_3_JAR_FOLDER="/Users/ken/db-derby-10.3.3.0-bin/lib"
MYGOODSTUFFS_DB="/Users/ken/dev/pmm/mygoodstuffs"
PMM_DB="/Users/ken/dev/pmm/user-data/db"
#
# dover
#
#DERBY_JAR_FOLDER="/usr/share/java"
#MYGOODSTUFFS_DB="/home/ken/dev/pmm/mygoodstuffs"
#PMM_DB="/home/ken/dev/pmm/pmm-dev"

#
# Aliases :
#
alias connectmygoodstuffs="java -cp $DERBY_10_3_JAR_FOLDER/derbytools.jar:$DERBY_10_3_JAR_FOLDER/derby.jar org.apache.derby.tools.dblook -d jdbc:derby:$MYGOODSTUFFS_DB" 
alias connectpmm="java -cp $DERBY_10_10_JAR_FOLDER/derbytools.jar:$DERBY_10_10_JAR_FOLDER/derby.jar -Dij.protocol=jdbc:derby: -Dij.database=$PMM_DB\;create=true org.apache.derby.tools.ij"

#
# export data
#
#connectmygoodstuffs exportdata.sql

#
# create new database
#
connectpmm create_tables.sql

#
# import data
#
connectpmm importdata.sql

