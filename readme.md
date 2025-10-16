# E-Commerce Android App

This project is an Android application for product sales, allowing users to browse products, manage a shopping cart, and more. It utilizes a **RESTful API** to communicate with a remote database.

---

### Key Features ✨

You've completed several core functionalities of the app. Here's a breakdown of the implemented features:

* **Authentication:** Users can securely **sign up** and **log in** with their credentials. Passwords are **securely hashed** to prevent unauthorized access.
* **Product Catalog:** The app fetches and displays a list of products from a remote database via a RESTful API. Each item includes its name, image, price, and a brief description. Users can also **sort and filter** the products.
* **Product Details:** From the product list, users can view a **detailed screen** with full descriptions, specifications, and multiple images. They can also add the product to their cart from this screen.
* **Shopping Cart:** The app provides a dedicated cart screen where users can see all the items they've added. The cart can be **managed** by adjusting item quantities, removing products, or clearing the entire cart. The total cart amount is **dynamically updated** with any changes.
* **Notifications:** A **cart badge notification** is displayed on the app icon, showing the number of items in the cart even when the app is closed. This feature uses libraries like `NotificationCompat`.

---

### Technical Details 🛠️

* **Backend:** The app relies on **REST APIs** for communication with the remote database.
* **Database:** The remote database used is either **MySQL or SQL Server**.
* **Payment Integration:** This feature is currently in progress, with plans to integrate payment gateways like VNPay, ZaloPay, or PayPal for secure payment processing.

---

### How to Run the Project 🚀

1.  Clone the repository:
    `git clone https://github.com/nmbt2910/PRM392_SalesAPP_Android.git`
2.  Open the project in Android Studio.
3.  Ensure you have a backend server running with the required REST APIs to handle data requests.
4.  Build and run the app on an Android emulator or a physical device.

---

### Future Work 🔮

The following features are still in development and will be added in future updates:

* **Billing:** Integrating a payment gateway and allowing users to enter billing and shipping information.
* **Map Screen:** Integrating Google Maps to show store locations and provide directions.
* **Chat Screen:** Implementing a real-time chat service for users to communicate with store representatives.