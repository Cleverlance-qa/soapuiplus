//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					GetMetrics                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

log.info ":***************************************************SEND METRICS***************************************************";


def projRoot = context.expand('${projectDir}');
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");

int i = 1;

file.eachLine { line ->
   
	testRunner.testCase.testSuite.project.testSuites["Execution"].setPropertyValue("metric_" + i ,line);
	log.info "metric_" + i + " = " + line;
	i++	;
}
