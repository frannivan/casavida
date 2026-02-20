import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("9981234567: " + encoder.encode("9981234567"));
        System.out.println("9987654321: " + encoder.encode("9987654321"));
    }
}
