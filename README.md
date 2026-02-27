#  P2 – RevShop  
## Full Stack E-Commerce Web Application

---

##  Application Overview

RevShop is a full-stack monolithic e-commerce web application designed for both Buyers and Sellers.

The system allows buyers to browse products, manage carts, place orders, and review purchases.  
Sellers can manage inventory, add and update products, monitor stock levels, and view orders.

The application includes:

- Responsive Web Interface
- Role-Based Access Control
- JWT-Based Authentication
- Simulated Secure Payment Processing
- Notification System
- Inventory Management

---


##  Team Members

- Mohan - Cart, Checkout & Orders + Payment
- Yamini - Authentication & User Management
- Reethika - Product Management (Seller Side)
- Lokesh  - Product Browsing & Search (Buyer Side)
- Vijay Krishna -  Reviews, Notifications, Wishlist & Alerts

---

#  Buyer Functional Requirements

As a Buyer, the system allows:

- Register with personal details (name, email, phone, address)
- Login using email and password
- Browse products by category
- Search products by keywords
- View product details (price, description, quantity)
- View product reviews and ratings
- Add products to cart
- Update product quantity in cart
- Remove products from cart
- View cart with total price calculation
- Checkout with shipping & billing information
- Make payment (Cash on Delivery / Credit Card / Debit Card – simulated)
- Receive order confirmation
- Receive in-app notifications
- View order history with status
- Review and rate purchased products
- Save products as favorites

---

#  Seller Functional Requirements

As a Seller, the system allows:

- Register with email, password, and business details
- Login securely
- Add new products (name, description, price, category, quantity)
- Update existing products
- Delete products from inventory
- View inventory with stock levels
- Set discounted price with MRP
- View all orders for their products
- View order details (buyer info, quantity, amount)
- Receive in-app order notifications
- View product reviews and ratings
- Set inventory threshold
- Receive low stock alerts

---

#  Standard Functional Scope

- Authentication & Account Management
- Notification System
- User Interface & Experience
- Data Management
- Secure Role-Based Authorization
- Order & Payment Processing
- Inventory Monitoring

---

#  Tech Stack

## Backend
- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- JPA / Hibernate
- MySQL
- Maven

## Frontend
- Angular
- TypeScript
- HTML
- CSS
- Angular Router
- HTTP Interceptor
- Role-Based Guards

---

#  Security Implementation

- JWT-Based Authentication
- BCrypt Password Encryption
- Role-Based Authorization (Buyer / Seller)
- Secure REST APIs
- OTP-Based Password Reset

---

#  How to Run

## Backend

- mvn spring-boot:run

-Runs at: http://localhost:8081/


## Frontend

- cd revshop-frontend
- npm install
- ng serve


Runs at: http://localhost:4200/


---

#  Project Documentation

- ERD (Entity Relationship Diagram)
- Application Architecture
- PPT Presentation
- Testing Artifacts

---

#  Definition of Done

- Working Web Application Demonstration
- Complete Code Repository Submission
- ER Diagram Included
- Application Architecture Included
- README Documentation
- Testing Evidence Provided

---



