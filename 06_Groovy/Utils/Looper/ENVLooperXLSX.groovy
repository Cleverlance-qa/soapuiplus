//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                                     DSEnvLooper
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.poi.ss.usermodel.*;
import com.eviware.soapui.model.*;

def pipelineEnvironment = context.expand('${#Global#pipelineEnvironment}');
def pipelineEndpoint = context.expand('${#Global#pipelineEndpoint}');
def pipelineAppComponent = context.expand('${#Global#pipelineAppComponent}');

String sheetName = 'List1';
def projRoot = context.expand('${projectDir}');
def DS = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSInput");
def DS_type = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSInputType");
def DSPath

//When pipeline parameter environment is revievd and data source is envConfig
if (!pipelineEnvironment.isEmpty() && DS.startsWith("DS_envConfig")) {
    
    log.info " ************************************** FOUND PIPELINE PARAMS ***********************************************";
    def path = (context.expand('${projectDir}') + "/01_DataSources/").replaceAll("\\\\", "/");
    def count = new File(path).listFiles().findAll { it.name ==~ /DS_envConfig.*/ }.size();
    //EnvConfig file must be only one
    if (count > 1) {
        
        log.info " ****** ****** POZOR ****** ******";
        log.error "Vstupni soubor 'DS_envConfig' nemuze byt ve vice formatech. Musi byt VZDY jen jeden tedy typ napr. XLS nebo XLSX nebo CSV";
        log.info " ****** ****** POZOR ****** ******";
        testRunner.fail("Problem with dataSource");
    } 
    else {
        
        DSPath = (projRoot + "/01_DataSources/" + DS + "." + DS_type).replaceAll("\\\\", "/");
    }
    // open Excel file -> sheet
    FileInputStream fileInputStream = new FileInputStream(new File(DSPath));
    Workbook wb = WorkbookFactory.create(fileInputStream);
    Sheet sh = wb.getSheet(sheetName);

    ArrayList<String> variables = new ArrayList<String>();
    ArrayList<String> cellList = new ArrayList<String>();
    //get first row from Excel(containing variable names) in the 'variables' List
    for (Cell cellVar : sh.getRow(0)) {
        
        variables.add(cellVar.getStringCellValue());
    }

    Iterator itrRow = sh.rowIterator();
    Row targetRow;
    int i = 0
    String currentCell;
    while (itrRow.hasNext()) {
        
        currentCell = sh.getRow(i).getCell(0).toString();
        
        if (currentCell.equals(pipelineEnvironment)) {
            //when row with wanted environment is found, get this row from Excel and get all variables to 'cellList' List
            targetRow = sh.getRow(i);
            for (Cell cell : targetRow) {
                
                cellList.add(cell.getStringCellValue())
            }
            break
        }
        i++
    }
    log.info "Choosing line with env2test = " + cellList.get(0);

    //Fill InputProps with wanted env variables
    int loop = 0
    if (variables.size() != 0 && cellList.size() != 0) {
        
        for (String variable : variables) {
            
            def inputProps = testRunner.testCase.getTestStepByName("InputProps");
            inputProps.setPropertyValue(variables.get(loop), cellList.get(loop));
            inputProps.setPropertyValue("host", pipelineEndpoint);
            inputProps.setPropertyValue("appComponent", pipelineAppComponent);
            loop++
        }
        log.info "Setting endpoint to = " + pipelineEndpoint;
        //Set init state in looperProps
        def looperProps = testRunner.testCase.getTestStepByName("LooperProps");
        looperProps.setPropertyValue("StopLoop", "StopLoop");
        looperProps.setPropertyValue("ActualRow", "1");
        looperProps.setPropertyValue("NextRow", "2");

    } 
    else {
        
        fileInputStream.close();
        throw new Exception("Problem occured when picking up environment varaibles !");
    }
    fileInputStream.close();
    log.info "Env Properties has been set successfully";
}