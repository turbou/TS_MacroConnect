package jp.co.tabocom.tsplugin.macroconnect;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationContextMenu;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;

public class MacroConnectPlugin implements TeratermStationPlugin {

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

    @Override
    public List<TeratermStationContextMenu> getActions(TargetNode[] nodes, Shell shell) {
        if (nodes[0].getIpAddr() == null) {
            return null;
        }
        TeratermStationContextMenu menu = new TeratermStationContextMenu();
        menu.setText("マクロ");
        for (TeratermStationAction action : getActionList(nodes, shell)) {
            menu.addAction(action);
        }
        if (menu.getActionList().isEmpty()) {
            return null;
        }
        return new ArrayList<TeratermStationContextMenu>(Arrays.asList(menu));
    }

    @Override
    public List<TeratermStationAction> getBulkActions(TargetNode[] nodes, Shell shell) {
        return new ArrayList<TeratermStationAction>(Arrays.asList(new MacroConnectBulkAction(nodes, null, shell)));
    }

    @Override
    public List<TeratermStationContextMenu> getDnDActions(TargetNode[] nodes, Object value, Shell shell) {
        return null;
    }

    private List<TeratermStationAction> getActionList(TargetNode[] nodes, Shell shell) {
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

        Map<String, File> macroMap = new TreeMap<String, File>();
        TargetNode node = nodes[0];
        File targetFile = node.getFile();

        List<File> parentDirList = new ArrayList<File>();
        Main main = (Main) shell.getData("main");
        Path rootPath = Paths.get(main.getCurrentTabItem().getRootDir());
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
            list.add(new MacroConnectAction(nodes, file, shell));
        }
        return list;
    }

}
