//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					ChoiceInputDialog    
//>										version 3.0.0               
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import javax.swing.*

def dialogMessage = context.expand('${InputProps#dialogMessage}');
def dialogTitle =context.expand('${InputProps#dialogTitle}');

def choices = new String[3] 

choices[0] = "choice1"
choices[1] = "choice2"
choices[2] = "choice3"

JFrame frame = new JFrame("ChoiceInputDialog");
def String dialogInput = (String) JOptionPane.showInputDialog(
	   frame,
        "What is your choice?",
        "Choice",
        JOptionPane.QUESTION_MESSAGE,
        null,
        choices,
        choices[0]
);

testRunner.testCase.getTestStepByName("InputProps").setPropertyValue("dialogInput", dialogInput);
