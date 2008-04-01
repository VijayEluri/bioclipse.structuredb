package net.bioclipse.structuredb.dialogs;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateStructureDatabaseDialog extends org.eclipse.swt.widgets.Dialog {

    private static final Logger logger = 
        Logger.getLogger(CreateStructureDatabaseDialog.class);
    
	private Shell dialogShell;
	private Label NameLabel;
	private Text nameText;
	private Button cancelButton;
	private Button okButton;
	private String name;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		// since we're running outside of Bioclipse environment, configure
	    // logging to print to console
	    org.apache.log4j.BasicConfigurator.configure();
		
	    try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			CreateStructureDatabaseDialog inst = new CreateStructureDatabaseDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
		    LogUtils.debugTrace(logger, e);
		}
	}

	public CreateStructureDatabaseDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			FormLayout dialogShellLayout = new FormLayout();
			dialogShell.setLayout(new FormLayout());
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(254, 134);
			dialogShell.setText("Create new Structure Database");
			{
				cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				cancelButton.setText("Cancel");
				FormData cancelButtonLData = new FormData();
				cancelButtonLData.width = 55;
				cancelButtonLData.height = 29;
				cancelButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
				cancelButtonLData.right =  new FormAttachment(1000, 1000, -63);
				cancelButton.setLayoutData(cancelButtonLData);
				cancelButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						dialogShell.close();
					}
				});
			}
			{
				okButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				okButton.setText("OK");
				FormData okButtonLData = new FormData();
				okButtonLData.width = 45;
				okButtonLData.height = 29;
				okButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
				okButtonLData.right =  new FormAttachment(1000, 1000, -12);
				okButton.setLayoutData(okButtonLData);
				okButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						name = nameText.getText();
						dialogShell.close();
					}
				});
			}
			{
				nameText = new Text(dialogShell, SWT.NONE);
				FormData nameTextLData = new FormData();
				nameTextLData.width = 161;
				nameTextLData.height = 17;
				nameTextLData.left =  new FormAttachment(0, 1000, 67);
				nameTextLData.top =  new FormAttachment(0, 1000, 12);
				nameText.setLayoutData(nameTextLData);
			}
			{
				NameLabel = new Label(dialogShell, SWT.NONE);
				NameLabel.setText("Name:");
				FormData NameLabelLData = new FormData();
				NameLabelLData.width = 43;
				NameLabelLData.height = 17;
				NameLabelLData.left =  new FormAttachment(0, 1000, 12);
				NameLabelLData.top =  new FormAttachment(0, 1000, 12);
				NameLabel.setLayoutData(NameLabelLData);
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
		    LogUtils.debugTrace(logger, e);
		}
	}

	public String getName() {
		return name;
	}
	
}
