package br.com.cviana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Startup {
	/*
	 * Caso a porta designada para iniciar a aplicação esteja ocupada
	 * execute o comando shell [netstat -ano | findstr :8080]
	 * Se o retorno desse comando exibir algum PID ocupando a porta desejada, então faça
	 * [taskkill /PID PID_NUMBER /F]
	 */
	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
	}

	/*
	public static void generatedHashedPassword() {
		PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        encoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        System.out.println( "ENCODED PASSWORD [admin123]: " + encoder.encode("admin123") );
        System.out.println( "ENCODED PASSWORD [admin234]: " + encoder.encode("admin234") );
	}
	*/

}
