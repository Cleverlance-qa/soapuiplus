//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					MessageDialog                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import javax.swing.*

def dialogMessage = context.expand('${InputProps#dialogMessage}');
def dialogTitle =context.expand('${InputProps#dialogTitle}');

JFrame frame = new JFrame("MessageDialog");
JOptionPane.showMessageDialog(
	frame,
	dialogMessage, 
	dialogTitle, 
	JOptionPane.INFORMATION_MESSAGE
);
