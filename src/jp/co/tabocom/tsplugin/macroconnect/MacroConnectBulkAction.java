package jp.co.tabocom.tsplugin.macroconnect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolTip;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.TeratermStationShell;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.EnvTabItem;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;

public class MacroConnectBulkAction extends TeratermStationAction {

    private static final int BULK_INTERVAL = 1700;

    public MacroConnectBulkAction(TargetNode[] nodes, Object value, TeratermStationShell shell) {
        super("マクロの使用...", null, nodes, value, shell);
    }

    @Override
    public void run() {
        Main main = this.shell.getMain();
        EnvTabItem tabItem = main.getCurrentTabItem();
        FileDialog fileDialog = new FileDialog(shell);
        fileDialog.setText("マクロを選択してください。");
        fileDialog.setFilterPath(main.getCurrentTabItem().getRootDir());
        fileDialog.setFilterExtensions(new String[] { "*.macro" });
        String file = fileDialog.open();
        if (file == null) {
            return;
        }
        File macroFile = new File(file);
        StringBuilder builder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        BufferedReader br = null;
        try {
            // まず最初にListに各行を読み込んでしまう。
            br = new BufferedReader(new FileReader(macroFile));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                builder.append(readLine + NEW_LINE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        MacroConfirmDialog dialog = new MacroConfirmDialog(shell, builder.toString());
        int pluginResult = dialog.open();
        if (IDialogConstants.OK_ID != pluginResult) {
            return;
        }
        String macroCmd = null;
        try {
            macroCmd = MacroUtil.genTemplateCmd(shell, macroFile);
        } catch (Exception e) {
            MessageDialog.openError(shell, "実行時エラー", "コマンドの生成でエラーが発生しました。\n" + e.getMessage());
            return;
        }
        if (macroCmd == null) {
            return;
        }
        try {
            // チェックされているノードすべてで実行します。もちろん親ノード（サーバ種別を表すノード）は対象外です。
            int idx = 1;
            for (TargetNode target : nodes) {
                tabItem.makeAndExecuteTTL(target, idx, macroCmd);
                idx++;
                Thread.sleep(BULK_INTERVAL); // スリープしなくても問題はないけど、あまりにも連続でターミナルが開くのもあれなので。
            }
            if (main.isTtlOnly()) {
                // TTLファイルの作成のみだったら、ファイル作成後、ダイアログを出す。
                MessageDialog.openInformation(shell, "TTLマクロ生成", "TTLマクロを生成しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ToolTip getToolTip() {
        ToolTip toolTip = new ToolTip(shell, SWT.BALLOON);
        toolTip.setMessage("*.macroファイルを選択することにより、一括接続されたサーバに対して定型処理を実行することができます。");
        return toolTip;
    }

}
