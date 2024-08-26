
package TrafficSignIdentifier;

public class main {
     public static void main(String[] args) {
        try { 
            dashboard form = new dashboard();
            form.setVisible(true);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
