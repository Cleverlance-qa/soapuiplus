//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					GetDates                   
//>										version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import java.util.Calendar;
import java.text.SimpleDateFormat;

def inputProps = testRunner.testCase.testSteps["InputProps"];

for (prop in inputProps.getPropertyList()) {
	
	def propName = prop.getName();
	def propValue = prop.getValue();
	
	if(propName.contains("(d)") || propName.contains("(dt)")) {
	
		def dsPeriod = propValue.substring(0,1);
		def dsOperator;
		def dsValue;
		int dsIntValue;
		
		if (propValue.length() > 1) {
			dsOperator = propValue.substring(1,2);
			dsValue = propValue.substring(2);
			dsIntValue = Integer.parseInt(dsValue);
		}
		SimpleDateFormat sdf;
		if(propName.contains("(d)")) {

			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		if(propName.contains("(dt)")) {

			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		}
		Date dt = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		Map periodMap = [D: "DATE", W: "WEEK_OF_MONTH", M: "MONTH", Y: "YEAR"];
		def periodMapVal = periodMap[dsPeriod];
		
		if(dsPeriod.equals(dsPeriod)) {
		
			if(dsOperator.equals("-")) {
				
				cal.add(Calendar."${periodMapVal}", - dsIntValue);
			}
			else{
				
				cal.add(Calendar."${periodMapVal}", dsIntValue);
			}
		}
		def evalDate = sdf.format(cal.getTime());
		testRunner.testCase.testSteps['InputProps'].setPropertyValue(propName, evalDate);
		log.info propName + " = " + evalDate;
	}
}
