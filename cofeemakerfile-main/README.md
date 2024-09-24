# CoffeeMaker


*Line Coverage (should be >=70%)*

![Coverage](.github/badges/jacoco.svg)

*Branch Coverage (should be >=50%)*

![Branches](.github/badges/branches.svg)



## Extra credit that we did:

Additional User Roles (admin)
- Out project allows for administrative members to be part of the staff. These admins can manage staff users, and modify the inventory. Login information is as follows. **Username: admin** **Password: password**
- Webpage: http://localhost:8080/login

Extra credit functionality (low inventory notification)
- Log in as an admin, and attempt to fulfill orders placed by customers. If the inventory goes below 10 for any ingredient(s), a notification is printed.
- Webpage: http://localhost:8080/fulfillorder.html

Security Audit
- We ran an audit of our projects security/risks, and made some changes based on our findings. Entering the "*" character or the word "SELECT" will make the login page sad
- Webpage 1: https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-213-5/wiki/Security-Audit 
- Webpage 2: http://localhost:8080/login (for the implemented secuirty measures)

Anonymous ordering
- We allow customers to order as a guest. How this is done is detailed in the Users' Guide
- Webpage 1: https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-213-5/wiki/Users'-Guide
- Webpage 2: http://localhost:8080/login  (Click continue as guest here)

Privacy Policy
- We have a privacy policy that users can view before making accounts on our system. This can be viewed on the login page
- Webpage 1: http://localhost:8080/login
- Webpage 2: https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-213-5/wiki/Privacy-Policy (this is just the page in our wiki)

