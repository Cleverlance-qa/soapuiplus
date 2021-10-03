//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					WriteOutputs
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import com.eviware.soapui.support.XmlHolder;
import org.apache.commons.io.FileUtils;
import jxl.*;
import jxl.write.*;
def debugMode = context.expand('${#Project#debugMode}');

def projRoot = context.expand('${projectDir}');
def env2test = context.expand('${#Project#env2test}');
def DS = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSOutput");
def DSPath = ( projRoot + "/02_Results/" + DS +  "_" + env2test + ".xls" ).replaceAll("\\\\","/");
if (debugMode == "true"){ log.info "DSPath results = " + DSPath; }

def outputProps = testRunner.testCase.testSteps["OutputProps"];
def outputPropsCount = outputProps.getPropertyCount();

if (outputPropsCount > 0) {

	Workbook wb = Workbook.getWorkbook(new File(DSPath));
	WritableWorkbook wb1 = Workbook.createWorkbook(new File(DSPath), wb);  
	WritableSheet sh = wb1.getSheet(0);
	Sheet sh1 = wb1.getSheet(0);
	
	int i = 0;
	int j = 1;
	int k = sh1.getRows();

	for (prop in outputProps.getPropertyList()) {
	
		def headerCol = prop.getName();
		Label header = new Label(i, 0, headerCol);
		sh.addCell(header);
		def val = prop.getValue();
		Label value = new Label(i, k, val);
		sh.addCell(value);
		i++
	}
	wb1.write();
	wb1.close();
}
else {
	
	log.info("No properties");
}

