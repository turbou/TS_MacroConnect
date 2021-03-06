package jp.co.tabocom.tsplugin.macroconnect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

public class MacroUtil {
    public static String genTemplateCmd(Shell shell, File templateFile) throws Exception {
        StringBuilder word = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        BufferedReader br = null;
        // テンプレートファイルの各行保持List
        List<String> lines = new ArrayList<String>();
        // 変換するキーと値保持Map
        Map<String, String> valuesMap = new TreeMap<String, String>();
        // 一時的名値保持用Map
        Map<String, String> answerMap = new HashMap<String, String>();
        try {
            // まず最初にListに各行を読み込んでしまう。
            br = new BufferedReader(new FileReader(templateFile));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                lines.add(readLine);
            }

            // 次にテンプレートファイル内の変数を拾い出す。
            for (String line : lines) {
                if (line.startsWith("@")) {
                    valuesMap.put(line.replaceFirst("@", ""), "");
                }
            }

            // そして置換すべき変数分まわして値を入力してもらう。
            for (String key : valuesMap.keySet()) {
                String dialogMsg = String.format("このキー[ %s ]に対応する値を入力してください。", key);
                final String errorMsg = "置換する値を入力してください。";
                InputDialog dialog = new InputDialog(shell, "テンプレート文字列置換", dialogMsg, "", new IInputValidator() {
                    @Override
                    public String isValid(String str) {
                        try {
                            if (str.isEmpty()) {
                                return errorMsg;
                            }
                        } catch (NumberFormatException nfe) {
                            return errorMsg;
                        }
                        return null;
                    }
                });
                if (dialog.open() == Dialog.OK) {
                    answerMap.put(key, dialog.getValue());
                } else {
                    return null;
                }
            }

            if (valuesMap.size() != answerMap.size()) {
                throw new IllegalArgumentException("テンプレート変数に対する置換文字列が指定されていません。");
            }

            // 入力してもらった値をvaluesMapに代入する。
            for (String key : answerMap.keySet()) {
                valuesMap.put(key, answerMap.get(key));
            }

            // 変換用のクラスを生成する。
            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            // 改めてテンプレートからttl文を作成する。
            for (String line : lines) {
                // 変数に相当する行は無視する。
                if (line.startsWith("@")) {
                    continue;
                }

                String resolvedLine = sub.replace(line);
                // シングルクォーテーションも送れるようにしておく。
                String correctLine = resolvedLine.replaceAll("'", Matcher.quoteReplacement("'#$27'"));
                if (correctLine.contains("?")) {
                    String waitStr = correctLine.split("\\?")[0];
                    String cmdStr = correctLine.split("\\?")[1].trim();
                    word.append("wait '" + waitStr + "'" + NEW_LINE);
                    word.append("sendln '" + cmdStr + "'" + NEW_LINE);
                } else {
                    word.append("wait ']$ '" + NEW_LINE);
                    word.append("sendln '" + correctLine.trim() + "'" + NEW_LINE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return word.toString();
    }
}
