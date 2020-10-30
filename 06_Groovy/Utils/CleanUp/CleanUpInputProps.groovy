//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CleanUpInputProps    
//>										version 3.0.0               
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def inputProps = testRunner.testCase.getTestStepByName("InputProps");

for(prop in inputProps.getProperties()){

	inputProps.removeProperty(prop.getKey());
}
