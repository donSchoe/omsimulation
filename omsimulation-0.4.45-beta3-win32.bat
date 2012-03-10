@echo off
java -Xss1024k -Xmn256m -Xms512m -Xmx1024m -cp lib/*:bin/omsimulation-0.4.45-beta3.jar de.bfs.radon.omsimulation.OMMainFrame
        