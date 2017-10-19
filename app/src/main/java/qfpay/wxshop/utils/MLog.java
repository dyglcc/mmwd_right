package qfpay.wxshop.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MLog {

    private Process process;

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    private boolean run = true;


    public void createLogCollector() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> commandList = new ArrayList<String>();
                commandList.add("logcat");
                commandList.add("-b");
                commandList.add("main");
                commandList.add("-v");
                commandList.add("time");
                commandList.add("FragmentManager:V *:S");
                try {
                    process = Runtime.getRuntime().exec(
                            commandList.toArray(new String[commandList.size()]));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    String str;
                    // process.waitFor();
                    while ((str = reader.readLine()) != null) {

                        if (!run) {
                            break;
                        }
                        System.out.println(str);
                        sb.append(str);
                        dealStr(str);
                    }
                } catch (Exception e) {
                    T.e(e);
                }finally {
                    process.destroy();
                }
            }
        }).start();

    }

    private void dealStr(String str) {

        if (str.startsWith("Op #")) {
            new String("op");
        }
    }

    public void destory(){

        run = false;

        if(process != null){
            process.destroy();
        }
    }
}