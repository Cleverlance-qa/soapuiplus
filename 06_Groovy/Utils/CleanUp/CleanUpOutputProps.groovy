//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CleanUpOutputProps     
//>										version 3.0.0              
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def outputProps = testRunner.testCase.getTestStepByName("OutputProps");

for(prop in outputProps.getProperties()){

	outputProps.removeProperty(prop.getKey());
}
