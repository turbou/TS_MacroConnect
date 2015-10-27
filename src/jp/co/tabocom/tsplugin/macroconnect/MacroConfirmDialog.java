package jp.co.tabocom.tsplugin.macroconnect;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MacroConfirmDialog extends Dialog {

    private String macroStr;
    private StyledText widget;

    public MacroConfirmDialog(Shell parentShell, String macroStr) {
        super(parentShell);
        this.macroStr = macroStr;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));
        this.widget = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL);
        this.widget.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.widget.setMargins(5, 5, 10, 5);
        this.widget.setEditable(false);
        this.widget.setWordWrap(true);
        this.widget.setText(macroStr);
        this.widget.setCaret(null);
        FontRegistry fontRegistry = new FontRegistry(parent.getShell().getDisplay());
        fontRegistry.put("MSGothic", new FontData[] { new FontData("ＭＳ ゴシック", 11, SWT.NORMAL) });
        this.widget.setFont(fontRegistry.get("MSGothic"));
        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(480, 320);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("マクロの内容確認");
    }
}
