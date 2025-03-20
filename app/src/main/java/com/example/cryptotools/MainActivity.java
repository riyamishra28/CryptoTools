package com.example.cryptotools;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private EditText keyInput;
    private Button processButton;
    private TextView resultText;
    private RadioGroup algorithmGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        inputText = findViewById(R.id.inputText);
        keyInput = findViewById(R.id.keyInput);
        processButton = findViewById(R.id.processButton);
        resultText = findViewById(R.id.resultText);
        algorithmGroup = findViewById(R.id.algorithmGroup);

        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = inputText.getText().toString().trim();
                String key = keyInput.getText().toString().trim();

                int selectedAlgorithmId = algorithmGroup.getCheckedRadioButtonId();
                if (selectedAlgorithmId == -1) {
                    Toast.makeText(MainActivity.this, "Please select an algorithm", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedAlgorithmId);
                String selectedAlgorithm = selectedRadioButton.getText().toString();

                String result = "";

                switch (selectedAlgorithm) {
                    case "Diffie-Hellman - MITM Attack":
                        if (input.isEmpty() || key.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter both prime number and generator", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            BigInteger p = new BigInteger(input);
                            BigInteger g = new BigInteger(key);
                            result = performDiffieHellmanMITM(p, g);
                        } catch (NumberFormatException ex) {
                            Toast.makeText(MainActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case "Miller-Rabin Primality Test":
                        if (input.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter input text", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            BigInteger number = new BigInteger(input);
                            boolean isPrime = millerRabinTest(number, 10);
                            result = number + " is " + (isPrime ? "probably prime" : "composite");
                        } catch (NumberFormatException ex) {
                            Toast.makeText(MainActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case "DES - Differential Cryptanalysis":
                        if (input.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter input text", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            if (key.isEmpty()) {
                                // Perform differential cryptanalysis
                                result = performDifferentialCryptanalysis(input);
                            } else {
                                // Encrypt with DES
                                result = encryptDES(input, key);
                            }
                        } catch (Exception ex) {
                            Toast.makeText(MainActivity.this, "Error in DES operation: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Selected algorithm not supported", Toast.LENGTH_SHORT).show();
                        return;
                }

                resultText.setText(result);
            }
        });
    }

    // Perform Diffie-Hellman key exchange with Man-in-the-Middle attack
    private String performDiffieHellmanMITM(BigInteger p, BigInteger g) {
        StringBuilder result = new StringBuilder("Diffie-Hellman Key Exchange with MITM Attack Simulation\n\n");

        try {
            // Validate prime number
            if (!millerRabinTest(p, 10)) {
                return "Error: The provided number is not prime. Please enter a prime number.";
            }

            // Check if g is valid (1 < g < p)
            if (g.compareTo(BigInteger.ONE) <= 0 || g.compareTo(p) >= 0) {
                return "Error: Generator g must be between 1 and p.";
            }

            // Simulate Alice's private key (a)
            SecureRandom random = new SecureRandom();
            BigInteger a = new BigInteger(p.bitLength() - 1, random);
            if (a.compareTo(BigInteger.ONE) <= 0) {
                a = BigInteger.TWO; // Ensure a is at least 2
            }

            // Simulate Bob's private key (b)
            BigInteger b = new BigInteger(p.bitLength() - 1, random);
            if (b.compareTo(BigInteger.ONE) <= 0) {
                b = BigInteger.TWO; // Ensure b is at least 2
            }

            // Calculate public keys
            BigInteger A = g.modPow(a, p); // Alice's public key: g^a mod p
            BigInteger B = g.modPow(b, p); // Bob's public key: g^b mod p

            result.append("Parameters:\n");
            result.append("p (prime) = ").append(p).append("\n");
            result.append("g (generator) = ").append(g).append("\n\n");

            result.append("Alice's private key (a) = ").append(a).append("\n");
            result.append("Alice's public key (A = g^a mod p) = ").append(A).append("\n\n");

            result.append("Bob's private key (b) = ").append(b).append("\n");
            result.append("Bob's public key (B = g^b mod p) = ").append(B).append("\n\n");

            // Normal key exchange (without MITM)
            BigInteger aliceSharedSecret = B.modPow(a, p); // B^a mod p
            BigInteger bobSharedSecret = A.modPow(b, p);   // A^b mod p

            result.append("Normal Key Exchange:\n");
            result.append("Alice computes shared secret = B^a mod p = ").append(aliceSharedSecret).append("\n");
            result.append("Bob computes shared secret = A^b mod p = ").append(bobSharedSecret).append("\n");
            result.append("Shared secret matches: ").append(aliceSharedSecret.equals(bobSharedSecret)).append("\n\n");

            // Now simulate MITM attack
            result.append("Man-in-the-Middle Attack Simulation:\n\n");

            // Mallory (attacker) generates her own private keys
            BigInteger m1 = new BigInteger(p.bitLength() - 1, random);
            BigInteger m2 = new BigInteger(p.bitLength() - 1, random);
            if (m1.compareTo(BigInteger.ONE) <= 0) m1 = BigInteger.TWO;
            if (m2.compareTo(BigInteger.ONE) <= 0) m2 = BigInteger.TWO;

            // Mallory calculates her public keys
            BigInteger M1 = g.modPow(m1, p);  // For communicating with Alice
            BigInteger M2 = g.modPow(m2, p);  // For communicating with Bob

            result.append("Mallory's first private key (m1) = ").append(m1).append("\n");
            result.append("Mallory's first public key (M1 = g^m1 mod p) = ").append(M1).append("\n");
            result.append("Mallory's second private key (m2) = ").append(m2).append("\n");
            result.append("Mallory's second public key (M2 = g^m2 mod p) = ").append(M2).append("\n\n");

            // Intercepted communications:
            result.append("Attack Process:\n");
            result.append("1. Alice sends A to Bob, but Mallory intercepts it\n");
            result.append("2. Mallory sends M1 to Bob (pretending to be Alice)\n");
            result.append("3. Bob sends B to Alice, but Mallory intercepts it\n");
            result.append("4. Mallory sends M2 to Alice (pretending to be Bob)\n\n");

            // Alice and Mallory compute shared key
            BigInteger aliceMallorySecret = M2.modPow(a, p);  // Alice thinks she's communicating with Bob
            BigInteger malloryAliceSecret = A.modPow(m1, p);  // Mallory can compute the same key

            // Bob and Mallory compute shared key
            BigInteger bobMallorySecret = M1.modPow(b, p);    // Bob thinks he's communicating with Alice
            BigInteger malloryBobSecret = B.modPow(m2, p);    // Mallory can compute the same key

            result.append("Resulting key agreements:\n");
            result.append("- Alice computes shared key with 'Bob' (actually Mallory): ").append(aliceMallorySecret).append("\n");
            result.append("- Mallory computes shared key with Alice: ").append(malloryAliceSecret).append("\n");
            result.append("  Match: ").append(aliceMallorySecret.equals(malloryAliceSecret)).append("\n\n");
            result.append("- Bob computes shared key with 'Alice' (actually Mallory): ").append(bobMallorySecret).append("\n");
            result.append("- Mallory computes shared key with Bob: ").append(malloryBobSecret).append("\n");
            result.append("  Match: ").append(bobMallorySecret.equals(malloryBobSecret)).append("\n\n");

            // Summary
            result.append("MITM Attack Summary:\n");
            result.append("- Alice thinks she's sharing a key with Bob, but it's with Mallory\n");
            result.append("- Bob thinks he's sharing a key with Alice, but it's with Mallory\n");
            result.append("- Mallory can decrypt, read, and re-encrypt all messages\n");
            result.append("- Alice and Bob cannot detect the attack without an authenticated channel\n\n");

            result.append("Prevention methods:\n");
            result.append("- Use authenticated key exchange protocols like STS or SIGMA\n");
            result.append("- Implement digital signatures to verify identities\n");
            result.append("- Use certificate-based authentication\n");
            result.append("- Employ out-of-band verification of key fingerprints");

            return result.toString();
        } catch (Exception ex) {
            return "Error in Diffie-Hellman MITM simulation: " + ex.getMessage();
        }
    }

    // Miller-Rabin primality test
    private boolean millerRabinTest(BigInteger n, int k) {
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }
        BigInteger nMinus1 = n.subtract(BigInteger.ONE);
        BigInteger d = nMinus1;
        int r = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            r++;
        }
        Random rand = new Random();
        for (int i = 0; i < k; i++) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), rand);
            } while (a.compareTo(BigInteger.ONE) <= 0 || a.compareTo(n.subtract(BigInteger.ONE)) >= 0);
            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(nMinus1)) {
                continue;
            }
            boolean witness = true;
            for (int j = 0; j < r - 1; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(nMinus1)) {
                    witness = false;
                    break;
                }
            }
            if (witness) {
                return false;
            }
        }
        return true;
    }

    // DES Encryption
    private String encryptDES(String plaintext, String keyText) {
        try {
            byte[] keyBytes = new byte[8];
            byte[] providedKeyBytes = keyText.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(providedKeyBytes, 0, keyBytes, 0, Math.min(providedKeyBytes.length, 8));
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
            return "DES Encrypted: " + encryptedBase64;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Differential Cryptanalysis for DES
    private String performDifferentialCryptanalysis(String input) {
        try {
            String[] pairs = input.split(",");
            if (pairs.length != 2) {
                return "Input should be two plaintext values separated by comma";
            }
            String plaintext1 = pairs[0].trim();
            String plaintext2 = pairs[1].trim();
            byte[] bytes1 = plaintext1.getBytes(StandardCharsets.UTF_8);
            byte[] bytes2 = plaintext2.getBytes(StandardCharsets.UTF_8);
            int minLength = Math.min(bytes1.length, bytes2.length);
            StringBuilder differential = new StringBuilder("Differential Analysis:\n\n");
            differential.append("Plaintext 1: ").append(plaintext1).append("\n");
            differential.append("Plaintext 2: ").append(plaintext2).append("\n\n");
            differential.append("Byte differences (XOR):\n");
            for (int i = 0; i < minLength; i++) {
                byte xorDiff = (byte) (bytes1[i] ^ bytes2[i]);
                differential.append(String.format("Byte %d: %02X\n", i, xorDiff));
            }
            differential.append("\nBit differences:\n");
            int totalDifferences = 0;
            for (int i = 0; i < minLength; i++) {
                byte xorDiff = (byte) (bytes1[i] ^ bytes2[i]);
                int bitCount = Integer.bitCount(xorDiff & 0xFF);
                totalDifferences += bitCount;
                String bits = String.format("%8s", Integer.toBinaryString(xorDiff & 0xFF)).replace(' ', '0');
                differential.append(String.format("Byte %d: %s (%d bits differ)\n", i, bits, bitCount));
            }
            differential.append("\nTotal bit differences: ").append(totalDifferences);
            differential.append("\n\nDifferential Properties:\n");
            differential.append("- Input difference pattern strength: ");
            if (totalDifferences < 4) {
                differential.append("Low (weak differential)\n");
                differential.append("- Potential for key bit recovery: Low");
            } else if (totalDifferences < 10) {
                differential.append("Medium\n");
                differential.append("- Potential for key bit recovery: Moderate");
            } else {
                differential.append("High (strong differential)\n");
                differential.append("- Potential for key bit recovery: High");
            }
            differential.append("\n\nNote: This is a simplified demonstration of differential cryptanalysis. ");
            differential.append("Real attacks require many plaintext-ciphertext pairs and focus on specific DES S-box properties.");
            return differential.toString();
        } catch (Exception e) {
            return "Error in differential analysis: " + e.getMessage();
        }
    }
}