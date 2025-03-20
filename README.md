Crypto Tools is an interactive Java-based application that is used to illustrate a wide range of cryptographic attacks within a learning and controlled environment. The tool supports both traditional and alternative cryptographic attack methods and allows experimentation with weaknesses in encryption schemes within a hands-on environment. Modules for the following have been implemented in this project:
•	Modern Cipher: An educational toy DES encryption scheme with Differential Cryptanalysis illustrations.
•	Public Key Cryptography: Diffie-Hellman Key exchange using Man-in-the-middle attack.
•	Primality Testing: Application of the Miller-Rabin Primality Test.
In this report, the design, behavior, and implementation of all the above modules have been explained for our application.

Overview of attacks explored

Our application is divided into three modules, each handling a distinct cryptographic method:

•	Data Encryption Standard – DES (Modern Cipher):
o	Differential Cryptanalysis: Simulates differences analysis between two ciphertexts to show how information regarding subkeys might be derived.

•	Diffie-Hellman Key Exchange (Man-in-the-middle attack):
o	Allows two parties to come to agreement on a shared secret through an insecure channel securely. It emphasizes the use of discrete logarithms and modular arithmetic, demonstrating both the mathematical basis of the protocol and how it might be vulnerable when ill-chosen parameters are used.

•	Miller-Rabin Primality Test (Primality Testing Method):
o	One of the probabilistic methods of testing the primality of a number is testing it against randomly chosen bases.

Screenshots
1. Main Interface:

![image](https://github.com/user-attachments/assets/638c6b6f-49fd-4bec-ae56-1252d4e9301c)


 



2. Diffie-Hellman Key Exchange

   ![image](https://github.com/user-attachments/assets/849ae1e9-1fda-4bed-b623-c989a7116c99)

  
 
3. Miller-Rabin Primality Test

   ![image](https://github.com/user-attachments/assets/1dfbc03b-9cd2-4961-be9a-072846754849)

 







5. Data Encryption Standard (DES)

   ![image](https://github.com/user-attachments/assets/255bad41-fb14-47e0-b099-2d92544e882b)



Conclusion

Crypto Tools is an effective learning platform that converts abstract cryptographic weaknesses into interactive demonstrations. By guiding the user through different approaches—from exposing vulnerabilities in the Diffie-Hellman key exchange and performing differential cryptanalysis on a toy DES encryption scheme to evaluate the probabilistic limits of the Rabin-Miller primality test, the application provides critical insights into the strengths and pitfalls of various encryption systems. The visual, step-by-step design of the tool simplifies complex cryptographic concepts, reinforcing the importance of robust encryption techniques when developing secure systems.


  





