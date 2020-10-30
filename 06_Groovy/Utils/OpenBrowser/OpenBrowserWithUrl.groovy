//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					OpenBrowserWithUrl  
//>										version 3.0.0                 
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def browserPath = testRunner.testCase.testSuite.project.testSuites["Runner"].testCases["Runner"].testSteps["InputProps"].getPropertyValue("browserPath").replace("\\", "\\\\");
def reqUrl =context.expand('${InputProps#reqUrl}');

def ProcessBuilder pb = new ProcessBuilder(browserPath, reqUrl);
pb.start();

return;
