// MainActivity.java
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter input text", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedAlgorithmId = algorithmGroup.getCheckedRadioButtonId();
                if (selectedAlgorithmId == -1) {
                    Toast.makeText(MainActivity.this, "Please select an algorithm", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedAlgorithmId);
                String selectedAlgorithm = selectedRadioButton.getText().toString();

                String result = "";

                switch (selectedAlgorithm) {
                    case "Vigenère Cipher - Kasiski":
                        if (key.isEmpty()) {
                            result = performKasiskiExamination(input);
                        } else {
                            result = encryptVigenere(input, key);
                        }
                        break;
                    case "RSA - Wiener's Attack":
                        if (key.isEmpty()) {
                            result = performWienerAttack(input);
                        } else {
                            String[] parts = key.split(",");
                            if (parts.length != 2) {
                                Toast.makeText(MainActivity.this, "RSA key format should be: e,n", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            try {
                                BigInteger e = new BigInteger(parts[0].trim());
                                BigInteger n = new BigInteger(parts[1].trim());
                                result = encryptRSA(input, e, n);
                            } catch (NumberFormatException ex) {
                                Toast.makeText(MainActivity.this, "Invalid RSA key format", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                    case "Miller-Rabin Primality Test":
                        try {
                            BigInteger number = new BigInteger(input);
                            boolean isPrime = millerRabinTest(number, 10);
                            result = number + " is " + (isPrime ? "probably prime" : "composite");
                        } catch (NumberFormatException ex) {
                            Toast.makeText(MainActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }

                resultText.setText(result);
            }
        });
    }

    // Vigenère Cipher encryption
    private String encryptVigenere(String text, String key) {
        StringBuilder result = new StringBuilder();
        text = text.toUpperCase();
        key = key.toUpperCase();

        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 'A' || c > 'Z') {
                result.append(c);
                continue;
            }

            result.append((char) ((c + key.charAt(j) - 2 * 'A') % 26 + 'A'));
            j = (j + 1) % key.length();
        }

        return "Encrypted: " + result.toString();
    }

    // Kasiski Examination for Vigenère cipher
    private String performKasiskiExamination(String ciphertext) {
        ciphertext = ciphertext.toUpperCase().replaceAll("[^A-Z]", "");

        // Find repeated sequences
        Map<String, List<Integer>> repeatedSequences = new HashMap<>();
        for (int length = 3; length <= 5; length++) {
            for (int i = 0; i <= ciphertext.length() - length; i++) {
                String sequence = ciphertext.substring(i, i + length);

                if (!repeatedSequences.containsKey(sequence)) {
                    repeatedSequences.put(sequence, new ArrayList<>());
                }

                repeatedSequences.get(sequence).add(i);
            }
        }

        StringBuilder result = new StringBuilder("Kasiski Examination Results:\n");

        // Find distances between repeated sequences
        List<Integer> distances = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : repeatedSequences.entrySet()) {
            List<Integer> positions = entry.getValue();
            if (positions.size() > 1) {
                String sequence = entry.getKey();
                result.append("Sequence '").append(sequence).append("' found at positions: ");

                for (int i = 0; i < positions.size(); i++) {
                    result.append(positions.get(i));
                    if (i < positions.size() - 1) {
                        result.append(", ");
                    }
                }
                result.append("\n");

                // Calculate distances
                for (int i = 0; i < positions.size() - 1; i++) {
                    for (int j = i + 1; j < positions.size(); j++) {
                        int distance = Math.abs(positions.get(j) - positions.get(i));
                        if (distance > 1) {
                            distances.add(distance);
                        }
                    }
                }
            }
        }

        if (distances.isEmpty()) {
            return "No repeated sequences found for Kasiski examination.";
        }

        // Find greatest common divisor of distances
        int gcd = distances.get(0);
        for (int i = 1; i < distances.size(); i++) {
            gcd = gcd(gcd, distances.get(i));
        }

        result.append("\nDistances between repetitions: ");
        for (int i = 0; i < distances.size(); i++) {
            result.append(distances.get(i));
            if (i < distances.size() - 1) {
                result.append(", ");
            }
        }

        result.append("\n\nEstimated key length: ").append(gcd);

        return result.toString();
    }

    // Greatest Common Divisor calculation
    private int gcd(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // RSA encryption
    private String encryptRSA(String message, BigInteger e, BigInteger n) {
        StringBuilder result = new StringBuilder();

        for (char c : message.toCharArray()) {
            BigInteger m = BigInteger.valueOf((int) c);
            BigInteger encrypted = m.modPow(e, n);
            result.append(encrypted.toString()).append(" ");
        }

        return "RSA Encrypted: " + result.toString();
    }

    // Wiener's Attack on RSA
    private String performWienerAttack(String input) {
        String[] parts = input.split(",");
        if (parts.length != 2) {
            return "Input format should be: e,n";
        }

        try {
            BigInteger e = new BigInteger(parts[0].trim());
            BigInteger n = new BigInteger(parts[1].trim());

            // Perform Wiener's attack
            BigInteger[] result = wienerAttack(e, n);

            if (result != null) {
                return "Wiener's Attack Success!\np = " + result[0] + "\nq = " + result[1] +
                        "\nd = " + result[2];
            } else {
                return "Wiener's Attack Failed. The key might not be vulnerable.";
            }
        } catch (NumberFormatException ex) {
            return "Invalid number format for RSA parameters";
        }
    }

    // Implementation of Wiener's Attack
    private BigInteger[] wienerAttack(BigInteger e, BigInteger n) {
        // Calculate continued fraction expansion of e/n
        ContinuedFractionExpansion cfExpansion = new ContinuedFractionExpansion(e, n);
        List<BigInteger> convergents = cfExpansion.getConvergents();

        for (int i = 0; i < convergents.size(); i++) {
            BigInteger k = convergents.get(i);
            if (k.equals(BigInteger.ZERO)) continue;

            BigInteger d = e.multiply(k).subtract(BigInteger.ONE).divide(n);

            // Check if d is a valid private key
            if (!e.multiply(d).mod(n.subtract(BigInteger.ONE)).equals(BigInteger.ONE)) {
                continue;
            }

            // Try to factor n using d
            BigInteger[] factors = factorUsingD(n, e, d);
            if (factors != null) {
                return new BigInteger[] {factors[0], factors[1], d};
            }
        }

        return null;
    }

    // Helper class for continued fraction expansion
    private class ContinuedFractionExpansion {
        private List<BigInteger> convergents = new ArrayList<>();

        public ContinuedFractionExpansion(BigInteger num, BigInteger denom) {
            List<BigInteger> quotients = new ArrayList<>();

            // Calculate continued fraction expansion
            while (!denom.equals(BigInteger.ZERO)) {
                BigInteger[] divAndRem = num.divideAndRemainder(denom);
                quotients.add(divAndRem[0]);
                num = denom;
                denom = divAndRem[1];
            }

            // Calculate convergents
            calculateConvergents(quotients);
        }

        private void calculateConvergents(List<BigInteger> quotients) {
            if (quotients.isEmpty()) return;

            BigInteger p0 = BigInteger.ONE;
            BigInteger q0 = BigInteger.ZERO;
            BigInteger p1 = quotients.get(0);
            BigInteger q1 = BigInteger.ONE;

            convergents.add(p1.divide(q1));

            for (int i = 1; i < quotients.size(); i++) {
                BigInteger p2 = quotients.get(i).multiply(p1).add(p0);
                BigInteger q2 = quotients.get(i).multiply(q1).add(q0);

                convergents.add(p2.divide(q2));

                p0 = p1;
                q0 = q1;
                p1 = p2;
                q1 = q2;
            }
        }

        public List<BigInteger> getConvergents() {
            return convergents;
        }
    }

    // Factor n using private key d
    private BigInteger[] factorUsingD(BigInteger n, BigInteger e, BigInteger d) {
        BigInteger edMinus1 = e.multiply(d).subtract(BigInteger.ONE);

        if (edMinus1.mod(BigInteger.TWO).equals(BigInteger.ONE)) {
            return null; // ed-1 must be even
        }

        BigInteger evenPart = edMinus1;
        while (evenPart.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            evenPart = evenPart.divide(BigInteger.TWO);
        }

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            BigInteger g;
            do {
                g = new BigInteger(n.bitLength(), rand).mod(n);
            } while (g.compareTo(BigInteger.ONE) <= 0 || g.compareTo(n) >= 0);

            BigInteger y = g.modPow(evenPart, n);

            if (!y.equals(BigInteger.ONE) && !y.equals(n.subtract(BigInteger.ONE))) {
                BigInteger p = y.subtract(BigInteger.ONE).gcd(n);
                if (p.compareTo(BigInteger.ONE) > 0) {
                    BigInteger q = n.divide(p);
                    return new BigInteger[] {p, q};
                }
            }
        }

        return null;
    }

    // Miller-Rabin primality test
    private boolean millerRabinTest(BigInteger n, int k) {
        // Handle edge cases
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }

        // Find r and d such that n-1 = 2^r * d, where d is odd
        BigInteger nMinus1 = n.subtract(BigInteger.ONE);
        BigInteger d = nMinus1;
        int r = 0;

        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            r++;
        }

        // Witness loop
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
}