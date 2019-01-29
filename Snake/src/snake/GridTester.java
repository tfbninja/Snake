package snake;

/**
 *
 * @author Timothy
 */
public class GridTester {

    public static void main(String[] args) {
        Grid tester = new Grid(10, 10);
        tester.addPortal(2, 2, 4, 4);
        System.out.println(tester.otherPortalPos(2, 2)[0] + " " + tester.otherPortalPos(2, 2)[1]);
        System.out.println(tester.otherPortalPos(4, 4)[0] + " " + tester.otherPortalPos(4, 4)[1]);
    }
}
