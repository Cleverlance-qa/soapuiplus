//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					GetCorrelationId
//>	                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def debug_error = context.expand('${#Project#debug_error}');

def randNumeric = org.apache.commons.lang.RandomStringUtils.randomNumeric(24);
def reqCorrelationId = "c" + randNumeric;
testRunner.testCase.setPropertyValue("reqCorrelationId", reqCorrelationId);

if (debug_error == "true"){ log.info "set CorrelationId = " + reqCorrelationId; }
