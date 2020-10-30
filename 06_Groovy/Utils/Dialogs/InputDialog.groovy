//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					InputDialog
//>										version 3.0.0                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import javax.swing.*

def dialogMessage = context.expand('${InputProps#dialogMessage}');
def dialogTitle =context.expand('${InputProps#dialogTitle}');

JFrame frame = new JFrame("InputDialog");
def String dialogInput = JOptionPane.showInputDialog(
        frame, 
        dialogMessage, 
        dialogTitle, 
        JOptionPane.WARNING_MESSAGE
);

testRunner.testCase.getTestStepByName("InputProps").setPropertyValue("dialogInput", dialogInput);
