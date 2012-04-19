@echo off
javaw -Xss1024k -Xmn256m -Xms512m -Xmx1024m -cp lib/*;bin/omsimulation-0.4.54-rc2.jar de.bfs.radon.omsimulation.OMMainFrame
        