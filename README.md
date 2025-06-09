# OWASP Top 10 Vulnerable UKIM CTF Website


A01:2021 – Broken Access Control - ✅ DONE (CSRF)

Example: A web app allows users to access /admin/dashboard without verifying if the user is actually an admin.

A02:2021 – Cryptographic Failures

Example: Storing passwords using MD5 hashing without a salt, allowing for easy brute-force or rainbow table attacks.

A03:2021 – Injection - ✅ DONE (SQL and XSS)

Example: SQL Injection: SELECT * FROM users WHERE username = '$user' AND password = '$pass'; is vulnerable if inputs aren’t sanitized.

A04:2021 – Insecure Design

Example: An e-commerce site has no rate limiting on checkout attempts, enabling automated discount abuse.

A05:2021 – Security Misconfiguration

Example: Leaving default credentials (admin/admin) on a production server’s management console.

---------------------------------------------------------------------------------------------------

A06:2021 – Vulnerable and Outdated Components

Example: Using a known-vulnerable version of Apache Struts that led to the 2017 Equifax breach.

A07:2021 – Identification and Authentication Failures

Example: Allowing unlimited login attempts without any account lockout or CAPTCHA (brute-force vulnerable).

A08:2021 – Software and Data Integrity Failures

Example: Using unverified plugins or scripts (e.g., from a public GitHub repo) without validating digital signatures.

A09:2021 – Security Logging and Monitoring Failures
 
Example: Not logging failed login attempts, so you can't detect a brute-force attack.

A10:2021 – Server-Side Request Forgery - ✅ DONE (SSRF)

Example: A file-fetching API fetches URLs without restrictions, allowing internal resources like http://localhost:8080/admin to be accessed.