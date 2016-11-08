/**
 * Created by Marko Nalis on 27.10.2015.
 */
public class RegEx {

    public static void main(String[] args) {
        String s = "This\nis a test";
        System.out.print(split(s));
    }

    private static String[] split(String s) {
        return s.split("\\r?\\n");//\\r\\n|\\r|\\n
    }
}
