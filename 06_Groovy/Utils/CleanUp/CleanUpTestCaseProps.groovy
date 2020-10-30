//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 				CleanUpTestCaseProps    
//>									version 3.0.0           
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

for( prop in context.testCase.properties) {
   
	def propKey = prop.getKey();
	testRunner.testCase.removeProperty(propKey);	
}
