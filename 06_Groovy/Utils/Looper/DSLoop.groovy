//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					DSLoop
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def looperProps = testRunner.testCase.getTestStepByName("LooperProps");
def stopLoop = looperProps.getPropertyValue("StopLoop");
 
if (stopLoop.toString() == "StopLoop") {

	log.info ("End of rows  in dataSource - stop loop");
	log.info "******************************************************************************************************************";
}
else {
	testRunner.gotoStepByName("DSLooper");
}
