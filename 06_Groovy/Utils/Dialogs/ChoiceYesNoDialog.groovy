//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					ChoiceYesNoDialog                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import javax.swing.*

def dialogMessage = context.expand('${InputProps#dialogMessage}');
def dialogTitle =context.expand('${InputProps#dialogTitle}');

JFrame frame = new JFrame("YesNoDialog");

def String dialogInput = JOptionPane.showConfirmDialog(
    frame,
    dialogMessage,
    dialogTitle,
    JOptionPane.YES_NO_OPTION
);

testRunner.testCase.getTestStepByName("InputProps").setPropertyValue("dialogInput", dialogInput);
