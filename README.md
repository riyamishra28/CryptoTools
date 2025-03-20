Crypto Tools is an interactive Java-based application that is used to illustrate a wide range of cryptographic attacks within a learning and controlled environment. The tool supports both traditional and alternative cryptographic attack methods and allows experimentation with weaknesses in encryption schemes within a hands-on environment. Modules for the following have been implemented in this project:
•	Modern Cipher: An educational toy DES encryption scheme with Differential Cryptanalysis illustrations.
•	Public Key Cryptography: Diffie-Hellman Key exchange using Man-in-the-middle attack.
•	Primality Testing: Application of the Miller-Rabin Primality Test.
In this report, the design, behavior, and implementation of all the above modules have been explained for our application.

Overview of attacks explored
Our application is divided into four modules, each handling a distinct cryptographic method:
•	Data Encryption Standard – DES (Modern Cipher):
o	Differential Cryptanalysis: Simulates differences analysis between two ciphertexts to show how information regarding subkeys might be derived.
•	Diffie-Hellman Key Exchange (Man-in-the-middle attack):
o	Allows two parties to come to agreement on a shared secret through an insecure channel securely. It emphasizes the use of discrete logarithms and modular arithmetic, demonstrating both the mathematical basis of the protocol and how it might be vulnerable when ill-chosen parameters are used.
•	Miller-Rabin Primality Test (Primality Testing Method):
o	One of the probabilistic methods of testing the primality of a number is testing it against randomly chosen bases.


