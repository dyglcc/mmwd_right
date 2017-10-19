package abtestdemo.adhoc.example.com.myapplication;

/**
 * Created by dongyuangui on 15-5-27.
 */
public class B {
    public static final boolean DEBUG = false;
    private static boolean dd = false;
    public void sp(){
        if(DEBUG){
            System.out.println("debug");
        }else{
            System.out.println("not debug " + DEBUG);
        }
    }
}
