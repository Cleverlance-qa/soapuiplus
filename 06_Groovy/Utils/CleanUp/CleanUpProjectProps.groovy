//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CleanUpProjectProps
//>                 					version 3.0.1
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

for( prop in context.testCase.testSuite.project.properties) {
	def propKey = prop.getKey();
	testRunner.testCase.testSuite.project.removeProperty(propKey);
}
