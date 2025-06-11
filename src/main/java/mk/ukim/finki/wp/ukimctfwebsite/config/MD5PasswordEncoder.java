package mk.ukim.finki.wp.ukimctfwebsite.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.MessageDigest;

public class MD5PasswordEncoder implements PasswordEncoder {
    public MD5PasswordEncoder() {
        System.out.println("[MD5PasswordEncoder] instantiated");
    }

    @Override
    public String encode(CharSequence rawPassword) {
        System.out.println("[MD5PasswordEncoder] encode() rawPassword=" + rawPassword);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.toString().getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println("[MD5PasswordEncoder] matches() raw=" + rawPassword + " encoded=" + encodedPassword);
        return encode(rawPassword).equals(encodedPassword);
    }
}