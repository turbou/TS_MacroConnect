package jp.co.tabocom.tsplugin.macroconnect;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public class MacroConnectPlugin implements TeratermStationPlugin {

    @Override
    public List<MenuManager> getSubmenus(TargetNode node, Shell shell, ISelectionProvider selectionProvider) {
        List<MenuManager> list = new ArrayList<MenuManager>();
        MenuManager subMenu = new MenuManager("マクロ", null);
        for (TeratermStationAction action: getActionList(node, shell, selectionProvider)) {
            if (node.getIpAddr() != null && !node.getIpAddr().isEmpty()) {
                subMenu.add(action);
            }
        }
        list.add(subMenu);
        return list;
    }

    @Override
    public List<TeratermStationAction> getActions(TargetNode node, Shell shell, ISelectionProvider selectionProvider) {
        return null;
    }

    private List<TeratermStationAction> getActionList(TargetNode node, Shell shell, ISelectionProvider selectionProvider) {
        List<TeratermStationAction> list = new ArrayList<TeratermStationAction>();

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
    public List<TeratermStationBulkAction> getBulkActions(List<TargetNode> nodeList, Shell shell) {
        return new ArrayList<TeratermStationBulkAction>(Arrays.asList(new MacroConnectBulkAction(nodeList, shell)));
    }

    @Override
    public PreferencePage getPreferencePage() {
        return null;
    }

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public void teminate(PreferenceStore preferenceStore) throws Exception {
    }

}
