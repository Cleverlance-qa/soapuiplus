//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Runner 
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


//cleanUp project props
evaluate(new File(context.expand(context.expand('${projectDir}') + "/06_Groovy/Utils/CleanUp/CleanUpProjectProps.groovy").replaceAll("\\\\","/")));

//get testRunId for test - NEW version
evaluate(new File(context.expand(context.expand('${projectDir}') + "/06_Groovy/Utils/GetData/GetTestRunId.groovy").replaceAll("\\\\","/")));

def pipelineEnvironment = context.expand('${#Global#pipelineEnvironment}');
def pipelineEndpoint = context.expand('${#Global#pipelineEndpoint}');
def pipelineAppComponent = context.expand('${#Global#pipelineAppComponent}');

//call ENVLooper to fill inputProps with properties corresponding to the pipeline environment
if(!pipelineEnvironment.isEmpty()) {
	evaluate(new File(context.expand(context.expand('${projectDir}') + "/06_Groovy/Utils/Looper/ENVLooperXLSX.groovy").replaceAll("\\\\","/")));
}

//get ds values and set as project props
def inputProps = testRunner.testCase.testSteps["InputProps"];

for (prop in inputProps.getPropertyList()) {
	def propName = prop.getName();
	def propVal = prop.getValue();
	testRunner.testCase.testSuite.project.setPropertyValue(propName, propVal);
}

//run testSuite Execution
def project = testRunner.testCase.testSuite.project;
def runner = project.testSuites['Execution'].run(null, true);

runner.waitUntilFinished();
