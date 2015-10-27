package jp.co.tabocom.tsplugin.macroconnect;

import java.io.File;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.EnvTabItem;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public class MacroConnectAction extends TeratermStationAction {

    private static final int BULK_INTERVAL = 1700;

    private File file;

    protected MacroConnectAction(TargetNode node, Shell shell, ISelectionProvider selectionProvider, File file) {
        super(file.getName().replaceAll("\\..*$", ""), "icon.png", node, shell, selectionProvider);
        this.file = file;
    }

    @Override
    public boolean isValid() {
        if (node.getIpAddr() != null) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Main main = (Main) this.shell.getData("main");
        EnvTabItem tabItem = main.getCurrentTabItem();

        try {
            String macroCmd = null;
            try {
                macroCmd = MacroUtil.genTemplateCmd(shell, this.file);
            } catch (Exception e) {
                MessageDialog.openError(this.shell, "実行時エラー", "コマンドの生成でエラーが発生しました。\n" + e.getMessage());
                return;
            }
            if (macroCmd == null) {
                return;
            }
            tabItem.makeAndExecuteTTL(node, 1, macroCmd);
            Thread.sleep(BULK_INTERVAL); // スリープしなくても問題はないけど、あまりにも連続でターミナルが開くのもあれなので。
            if (main.isTtlOnly()) {
                // TTLファイルの作成のみだったら、ファイル作成後、ダイアログを出す。
                MessageDialog.openInformation(this.shell, "TTLマクロ生成", "TTLマクロを生成しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
