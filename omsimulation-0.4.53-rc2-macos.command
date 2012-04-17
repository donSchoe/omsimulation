#!/bin/bash
cd "$(dirname "$0")"
java -Xss1024k -Xmn256m -Xms512m -Xmx1024m -cp lib/*:bin/omsimulation-0.4.53-rc2.jar de.bfs.radon.omsimulation.OMMainFrame
        