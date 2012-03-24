#!/bin/sh
java -Xss1024k -Xmn256m -Xms512m -Xmx1024m -cp lib/*:bin/omsimulation-0.4.50-rc1.jar de.bfs.radon.omsimulation.OMMainFrame
        