import app.lacabra.ragdrop.Schema;
import app.lacabra.ragdrop.Yaml;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema("classpath:schema.json"); // Import the verification scheme
        Yaml yaml = new Yaml().loadFromPath("example/person.yaml"); // Import the YAML file

        schema.addType("email", EmailType::new); // Add a new type to the scheme

        if (schema.verify()) { // Verify the integrity of the scheme
            try {
                schema.validate(yaml); // Verify the integrity of the YAML file compared to the scheme
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("The YAML file is invalid: " + e.getMessage());
            }
        } else {
            System.out.println("The verification scheme is invalid: " + schema.getErrorMessage());
        }


    }
}