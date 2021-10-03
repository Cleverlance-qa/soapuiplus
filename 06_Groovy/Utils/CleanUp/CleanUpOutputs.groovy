//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CleanUpOutputs                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import com.eviware.soapui.support.XmlHolder;
import jxl.*;
import jxl.write.*;

def DS = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSOutput");

if(DS.equals("N/A")) {
	
	log.info "CleanUp the output dataSource - No dataSource to clean";
	log.info "******************************************************************************************************************";
}
else {

	log.info "CleanUp the output dataSource = " + DS;
	log.info "******************************************************************************************************************";
	def DSPath = context.expand('${#TestCase#DSPath}');

	Workbook wb = Workbook.getWorkbook(new File(DSPath));
	WritableWorkbook wb1 = Workbook.createWorkbook(new File(DSPath), wb);  
	WritableSheet sh = wb1.getSheet(0);  
	Sheet sh1 = wb1.getSheet(0);  

	int i = sh1.getRows();

	for (int j = i -1 ; j >= 1 ; j--) {
		
		sh1.removeRow(j);
	}
	wb1.write();
	wb1.close();
}
