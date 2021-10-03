//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					InputDialog
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import javax.swing.*

def looperProps = testRunner.testCase.getTestStepByName("LooperProps");
def RowsCount = looperProps.getPropertyValue("RowsCount");
int iRowsCount = Integer.parseInt(RowsCount)-1 ;

def dialogMessage = "Input a number from 1 to " + iRowsCount + " of line from data source";
def dialogTitle = "Select row number from data source";

JFrame frame = new JFrame("Row number:")
def String dialogInput = JOptionPane.showInputDialog(
        frame,
        dialogMessage,
        dialogTitle,
        JOptionPane.WARNING_MESSAGE
)

testRunner.testCase.setPropertyValue("debugRowNumber", dialogInput);
