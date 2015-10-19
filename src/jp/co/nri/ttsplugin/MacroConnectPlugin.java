package jp.co.nri.ttsplugin;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeraTermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeraTermStationAction;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public class MacroConnectPlugin implements TeraTermStationPlugin {

    @Override
    public List<TeraTermStationAction> getActions(TargetNode node, Shell shell, ISelectionProvider selectionProvider) {
        List<TeraTermStationAction> list = new ArrayList<TeraTermStationAction>();
        FilenameFilter macroFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                System.out.println(name);
                if (name.endsWith(".macro")) {
                    return true;
                }
                return false;
            }
        };
        Map<String, File> macroMap = new HashMap<String, File>();
        File targetFile = node.getFile();
        for (int i = 1; i <= targetFile.toPath().getNameCount(); i++) {
            Path path = targetFile.toPath().subpath(0, i);
            File chkDir = path.toFile();
            if (chkDir.isDirectory()) {
                System.out.println("chkDir: " + chkDir.toString());
                for (File file : chkDir.listFiles(macroFilter)) {
                    macroMap.put(file.getName(), file);
                }
            }
        }
        for (File file : macroMap.values()) {
            list.add(new MacroConnectAction(node, shell, selectionProvider, file));
        }
        return list;
    }

    @Override
    public PreferencePage getPreferencePage() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
}
