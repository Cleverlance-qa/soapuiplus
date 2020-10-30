//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CleanUpMetricsFile 
//>										version 3.0.0                  
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.commons.io.FileUtils;

def projRoot = context.expand('${projectDir}');
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");
file.bytes = new byte[0];
