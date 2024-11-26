import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static File CWD = new File(System.getProperty("user.dir"));
    public static File GITLET_SYSTEM = Utils.join(CWD, ".gitlet");

    public static void main(String[] args) {
        System.out.println("Initial CWD: " + CWD);
        System.out.println("Initial GITLET_SYSTEM: " + GITLET_SYSTEM);

        // Change CWD
        CWD = new File("/new/path");

        // GITLET_SYSTEM will still point to the old value
        System.out.println("Updated CWD: " + CWD);
        System.out.println("GITLET_SYSTEM after CWD change: " + GITLET_SYSTEM);

        // Reassign GITLET_SYSTEM to reflect new CWD
        GITLET_SYSTEM = Utils.join(CWD, ".gitlet");
        System.out.println("GITLET_SYSTEM after reassigning: " + GITLET_SYSTEM);
    }
}
