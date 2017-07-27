# InventoryProject

<p><b> Inventory App - project from Android Basics by Google Nanodegree Program in Udacity </b></p>

The goal is to design and create the structure of an <b>Inventory App</b> which would allow a store to keep track of its inventory of products. The app will need to store information about price, quantity available, supplier, and a picture of the product. It will also need to allow the user to track sales and shipments and make it easy for the user to order more from the listed supplier.

This project is about combining various ideas and skills we’ve been practicing throughout the course. They include:

 - Storing information in a SQLite database
 - Integrating Android’s file storage systems into that database
 - Presenting information from files and SQLite databases to users
 - Updating information based on user input.
 - Creating intents to other apps using stored information.

![screenshot_1500503728websmall](https://user-images.githubusercontent.com/17390877/28393011-4b2984d2-6ced-11e7-8625-8e65ed281350.png)
![screenshot_1500503757web](https://user-images.githubusercontent.com/17390877/28393169-1079173e-6cee-11e7-8383-aa1c91648cf4.png)
![screenshot_1500503820web](https://user-images.githubusercontent.com/17390877/28393168-1077053e-6cee-11e7-8cf6-6e0e6c08b7d5.png)
![screenshot_1500503871web](https://user-images.githubusercontent.com/17390877/28393166-106f355c-6cee-11e7-95aa-80116d7b6c38.png)
![screenshot_1500503866web](https://user-images.githubusercontent.com/17390877/28393167-1072dee6-6cee-11e7-914e-3a168487f376.png)

 #  PROJECT SPECIFICATION Inventory App
 
 <p><b> CRITERIA / MEETS SPECIFICATIONS </b></p>
 
 <p><b>Layout</b></p>
 
<p><b>Overall Layout</b> / The app contains a list of current products and a button to add a new product.

<p><b>List Item Layout</b> / Each list item displays the product name, current quantity, and price. Each list item also contains a Sale Button that reduces the quantity by one (include logic so that no negative quantities are displayed).</p>

<p><b>Detail Layout</b> / </p>

 - The Detail Layout for each item displays the remainder of the information stored in the database.

 - The Detail Layout contains buttons that increase and decrease the available quantity displayed.

 - The Detail Layout contains a button to order from the supplier.

 - The detail view contains a button to delete the product record entirely.

<p><b>Layout Best Practices</b> / The code adheres to all of the following best practices:</p>

 - Text sizes are defined in sp
 - Lengths are defined in dp
 - Padding and margin is used appropriately, such that the views are not crammed up against each other.
 
<p><b>Default Textview</b> / When there is no information to display in the database, the layout displays a TextView with instructions on how to populate the database.</p>
 
 <p><b>Functionality</b></p>
 
<p><b>Runtime Errors</b> / The code runs without errors. For example, when user inputs product information (quantity, price, name, image), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.</p>

<p><b>ListView Population</b> / The listView populates with the current products stored in the table.</p>

<p><b>Add product button</b> / The Add product button prompts the user for information about the product and a picture, each of which are then properly stored in the table.</p>

<p><b>Input validation</b> / User input is validated. In particular, empty product information is not accepted. If user inputs product information (quantity, price, name, image), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.</p>

<p><b>Sale Button</b> / Each list item contains a Sale Button which reduces the quantity available by one (include logic so that no negative quantities are displayed).</p>

<p><b>Detail View intent</b> / Clicking on the rest of each list item sends the user to the detail screen for the correct product.</p>

<p><b>Modify quantity buttons</b> / The modify quantity buttons in the detail view properly increase and decrease the quantity available for the correct product. The student may also add input for how much to increase or decrease the quantity by.</p>

<p><b>Order Button</b> / The ‘order more’ button sends an intent to either a phone app or an email app to contact the supplier using the information stored in the database.</p>

<p><b>Delete button</b> / The delete button prompts the user for confirmation and, if confirmed, deletes the product record entirely and sends the user back to the main activity.</p>

<p><b>External Libraries and Packages</b> / The intent of this project is to give you practice writing raw Java code using the necessary classes provided by the Android framework; therefore, the use of external libraries for core functionality will not be permitted to complete this project.</p>

<p><b>Code Readability</b></p>
 
 <p><b>Naming conventions</b> / All variables, methods, and resource IDs are descriptively named such that another developer reading the code can easily understand their function.</p>

<p><b>Format</b> / The code is properly formatted i.e. there are no unnecessary blank lines; there are no unused variables or methods; there is no commented out code. The code also has proper indentation when defining variables and methods.</p>
