//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					DSLooperXLSX
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import com.eviware.soapui.support.XmlHolder;
import jxl.*;
import jxl.write.*;
import groovy.io.*;
import groovy.json.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;


def projRoot = context.expand('${projectDir}');
def env2test = context.expand('${#Project#env2test}');
def DS = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSInput");
def DS_type = testRunner.testCase.testSteps['DataSources'].getPropertyValue("DSInputType");
def DSPath;
def DSPath_xlsx;
def DSPath2_xlsx;
def debug_error = context.expand('${#Project#debug_error}');
// promenne pro kontrolu spravnosti nacteni
def DSPath_ctrl = "";
def DSPath2_ctrl = "";


if (DS.startsWith("DS_envConfig")) {  // env file = NESMI SE ZMENIT NAZEV !!!
	
	// kontrola na pocet	
	def path = (context.expand('${projectDir}') + "/01_DataSources/").replaceAll("\\\\","/");
	def count = new File(path).listFiles().findAll { it.name ==~ /DS_envConfig.*/ }.size();
	if (debug_error == "true") {
		log.info "pocet stejnych 'DS_envConfig.*' = " + count;
	}
	
	if (count > 1) {
	
		log.info " ****** ****** POZOR ****** ******";
		log.error "Vstupni soubor 'DS_envConfig' nemuze byt ve vice formatech. Musi byt VZDY jen jeden tedy typ napr. XLS nebo XLSX nebo CSV";
		log.info " ****** ****** POZOR ****** ******";
		testRunner.fail("Problem with dataSource");
		
	} else {
		
		DSPath_control = ( projRoot + "/01_DataSources/" + DS + "." + DS_type ).replaceAll("\\\\","/");
		
		if (debug_error == "true") { 
			log.info "DSPath_control = " + DSPath_control;
		}
		
		DSPath_xlsx = ( projRoot + "/01_DataSources/" + DS + ".xlsx" ).replaceAll("\\\\","/");
		
		if (new File(DSPath_control).exists() == true) {
			
			if (new File(DSPath_xlsx).exists() == true) {
				//log.info "XLSX";
				DSPath = DSPath_xlsx;
				DSPath_ctrl = "xlsx";
			}
			
		} 
		else {
			log.error "File CSV or XLS or XLSX not found !!!";
		}

		testRunner.testCase.setPropertyValue("DSPath", DSPath);
		if (debug_error == "true") {
			log.info "DSPath (data input file) = " + DSPath;
		}
	}
	
}
else { // libovolny nazev data souboru
	
	// kontrola na pocet
	def path = (context.expand('${projectDir}') + "/01_DataSources/").replaceAll("\\\\","/");
	if (debug_error == "true") {
		log.info "cesta k datovemu adresari = " + path;
	}
	
	def prefix = DS + "_" + env2test + "." + DS_type;
	def prefixPattern = "${prefix}*.*";
	def count = new File(path).listFiles().findAll { it.name ==~ /${prefixPattern}/ }.size();

	if (debug_error == "true") {
		log.info "pocet souboru '" + DS + "_" + env2test + "." + DS_type + "' = " + count;
	}
	
	if (count > 1) {
		log.info " ****** ****** POZOR ****** ******";
		log.error "Vstupni soubor '" + DS + "_" + env2test + "' nemuze byt ve vice formatech. Musi byt VZDY jen jeden tedy typ napr. XLS nebo XLSX nebo CSV !!!";
		log.info " ****** ****** POZOR ****** ******";

	} else {
		
		DSPath2_xlsx = ( projRoot + "/01_DataSources/" + DS + "_" + env2test + ".xlsx" ).replaceAll("\\\\","/");
		
		if(new File(DSPath2_xlsx).exists() == true) {
			//log.info "XLSX 2";
			DSPath = DSPath2_xlsx;
			DSPath2_ctrl = "xlsx";	
		} 
		else {
			log.error "File CSV or XLS or XLSX not found !!!";
		}
		
		// ulozena cesta pro data file
		testRunner.testCase.setPropertyValue("DSPath", DSPath);
	}

}

// ********************* XLSX soubor ****************************** //

if ((DSPath_ctrl == "xlsx") || (DSPath2_ctrl == "xlsx")) {

	String filepath = DSPath;
	String sheetName = "List1";
	FileInputStream fis = new FileInputStream(new File(filepath));

	Workbook wb = WorkbookFactory.create(fis);
	Sheet sh = wb.getSheet(sheetName);

	def rowsCount = sh.getPhysicalNumberOfRows();
	if (debug_error == "true") {
		log.info "rowsCount = " + rowsCount;
	}
	def colsCount = sh.getRow(0).getLastCellNum();
	if (debug_error == "true") {
		log.info "colsCount = " + colsCount;
	}

	if(rowsCount == 1) {

		testRunner.fail("DataSource is empty");
		log.info "DataSource is empty - testCase run was canceled";
	}
	else{

		def inputProps = testRunner.testCase.getTestStepByName("InputProps");
		def looperProps = testRunner.testCase.getTestStepByName("LooperProps");
		looperProps.setPropertyValue("RowsCount", rowsCount.toString());
		actualRow = looperProps.getPropertyValue("ActualRow").toString();
		actualRow = actualRow.toInteger();

		def nextRow;
		if(actualRow > rowsCount-2 ) {

		nextRow = "1";
		testRunner.testCase.setPropertyValue("nextRow", nextRow);
		}
		else {

			nextRow = actualRow + 1;
			nextRow = nextRow.toString();
			testRunner.testCase.setPropertyValue("nextRow", nextRow);
		}
	
		nextRow = context.expand('${#TestCase#nextRow}');

		DataFormatter formatter = new DataFormatter();
		
		int i = 1;
		for(int j = 0; j < colsCount; j++) {

			Row headerCol = sh.getRow(0);
			Cell headerContent = headerCol.getCell(j);
			String headerVal = formatter.formatCellValue(headerContent);
			if (debug_error == "true") {
				log.info headerVal;
			}
			Row field = sh.getRow(actualRow);
			Cell fieldContent = field.getCell(j)
			String fieldVal = formatter.formatCellValue(fieldContent);
			if (debug_error == "true") {
				log.info fieldVal;
			}
			inputProps.setPropertyValue(headerVal, fieldVal);	
		i++
	}
	fis.close();

	looperProps.setPropertyValue("ActualRow", nextRow.toString());
	nextRow++;
	log.info "DataSource = " + DS + ", actual row = ${(actualRow)} of ${(rowsCount)-1}";
	log.info "*******************************************************************************************************************";	
	looperProps.setPropertyValue("NextRow", nextRow.toString());

	if (actualRow == rowsCount-1) {

		looperProps.setPropertyValue("StopLoop", "StopLoop");
	}
	else if (actualRow==0) {

		def runner = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner(testRunner.testCase, null);
		looperProps.setPropertyValue("StopLoop", "NextLoop");
	}
	else {

		looperProps.setPropertyValue("StopLoop", "NextLoop");
		}
	}	
}