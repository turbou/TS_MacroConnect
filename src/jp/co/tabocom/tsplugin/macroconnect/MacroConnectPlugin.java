package jp.co.tabocom.tsplugin.macroconnect;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tabocom.teratermstation.Main;
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
                if (name.endsWith(".macro")) {
                    return true;
                }
                return false;
            }
        };

        Map<String, File> macroMap = new HashMap<String, File>();
        File targetFile = node.getFile();

        List<File> parentDirList = new ArrayList<File>();
        Main main = (Main) shell.getData("main");
        Path rootPath = main.getToolDefine().getRootDirPath();
        File rootDir = rootPath.toFile();

        File parentDir = targetFile.getParentFile();
        while (parentDir != null) {
            parentDirList.add(parentDir);
            if (parentDir.getPath().equals(rootDir.getPath())) {
                break;
            }
            parentDir = parentDir.getParentFile();
        }

        Collections.reverse(parentDirList);
        for (File chkDir : parentDirList) {
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
        return null;
    }
}
