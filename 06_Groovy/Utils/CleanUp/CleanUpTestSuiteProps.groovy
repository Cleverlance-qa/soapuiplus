//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CleanUpTestSuiteProps   
//>										version 3.0.0                
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

for( prop in context.testCase.testSuite.properties) {
   
	def propKey = prop.getKey();
	testRunner.testCase.testSuite.removeProperty(propKey);	
}
