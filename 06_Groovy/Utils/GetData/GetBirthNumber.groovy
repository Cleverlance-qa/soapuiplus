//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					GetBirthNumber                   
//>										version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import java.util.Random;

def String birthDateString = context.expand('${InputProps#dsBirthdate(d)}');
def gender = context.expand('${InputProps#dsGender}');
        
context.generatedRC = generateSimpleRC(birthDateString, gender);

def generateSimpleRC(birthDateString, gender) {

	def yearPartofRC = birthDateString.substring(2, 4);
	def monthPartofRC = birthDateString.substring(5, 7);
	
	if (gender.equals("F")) {
		
		int monthNumber = Integer.valueOf(monthPartofRC);
		monthPartofRC = String.valueOf(monthNumber + 50);
	}
	def dayPartofRC = birthDateString.substring(8);
	def parsedRCDateBase = yearPartofRC + monthPartofRC + dayPartofRC;
	
	def random = new Random();
	def int randomNumber = random.ints(100, (999 + 1)).findFirst().getAsInt();
	def String randomRCsuffixString = String.valueOf(randomNumber);
	def RCBaseString = parsedRCDateBase + randomRCsuffixString;
	
	int valueForModulo11Validation = Integer.valueOf(RCBaseString);
	
	def int modulo11 = valueForModulo11Validation % 11;
	def validationDigit;
	
	if (modulo11 == 10) {
		
		validationDigit = "0";
	} 
	else {
		
		validationDigit = String.valueOf(modulo11);
	}
	//log.info RCBaseString + validationDigit;
	testRunner.testCase.getTestStepByName("InputProps")setPropertyValue( "birthNumber", RCBaseString + validationDigit.toString());
}
