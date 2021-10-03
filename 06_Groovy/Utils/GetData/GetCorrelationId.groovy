//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					GetCorrelationId
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def debugMode = context.expand('${#Project#debugMode}');

def randNumeric = org.apache.commons.lang.RandomStringUtils.randomNumeric(24);
def reqCorrelationId = "c" + randNumeric;
testRunner.testCase.setPropertyValue("reqCorrelationId", reqCorrelationId);

if (debugMode == "true") {
	log.info "set CorrelationId = " + reqCorrelationId;
}
