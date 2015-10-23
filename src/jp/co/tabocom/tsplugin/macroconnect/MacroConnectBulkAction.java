package jp.co.tabocom.tsplugin.macroconnect;

import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;

import org.eclipse.swt.widgets.Shell;

public class MacroConnectBulkAction extends TeratermStationBulkAction {

    public MacroConnectBulkAction(List<TargetNode> nodeList, Shell shell) {
        super(nodeList, shell);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void run() {
        System.out.println("run!!");
    }

    @Override
    public String getDisplayName() {
        return "�}�N���̎g�p...";
    }

    @Override
    public String getDescription() {
        return "*.macro�t�@�C����I�����邱�Ƃɂ��A�ꊇ�ڑ����ꂽ�T�[�o�ɑ΂��Ē�^���������s���邱�Ƃ��ł��܂��B";
    }

}
