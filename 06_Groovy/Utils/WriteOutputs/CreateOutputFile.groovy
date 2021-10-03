//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					CreateOutputFile                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//import org.apache.commons.io.FileUtils;
import jxl.*;
import jxl.write.*;

//create results file
def projRoot = context.expand('${projectDir}');
def env2test = context.expand('${#Project#env2test}');
def DS = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSOutput");
def DSPath = projRoot + "/02_Results/" + DS +  "_" + env2test + ".xls";

testRunner.testCase.setPropertyValue("DSPath", DSPath);

WritableWorkbook wb = Workbook.createWorkbook(new File(DSPath));
WritableSheet sh = wb.createSheet("List1", 0);
Label label = new Label(0,0,"noempty");
sh.addCell(label);
wb.write();
wb.close();

