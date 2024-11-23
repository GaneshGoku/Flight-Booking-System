import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

public class FlightBookSystem{
   static boolean validAdmin(String username, String password) {
        if(username.equals("MBM123") && password.equals("ADSB2343741")) {

            return true;
        }
        return false;
    }
    static class Contact{
        String name;
        String phone_number;

        public Contact(String name, String phone_number){
            this.name = name;
            this.phone_number = phone_number;

        }
    }

    static class AVLNode {
        Contact contact;
        AVLNode left;
        AVLNode right;
        int height;

        public AVLNode(Contact contact) {
            this.contact = contact;
            this.left = null;
            this.right = null;
            this.height = 1;
        }
    }

    static class AVLTree {
        AVLNode root;
        String file_name;

        public AVLTree(String file_name) {
            this.root = null;
            this.file_name = file_name;
            loadContacts();
        }

        public void addContact(Contact contact) {
            root = insert(root, contact);
            saveContacts();
            System.out.println("Passenger added successfully.");
        }

        public void searchContact(String name) {
            AVLNode node = search(root, name);
            if (node != null) {
                displayContact(node.contact);
            } else {
                System.out.println("Passenger not found.");
            }
        }

        public void removeContact(String name) {
            root = remove(root, name);
            saveContacts();
            System.out.println("Contact removed successfully.");
        }

        public void displayPhonebook() {
            if (root != null) {
                inOrderTraversal(root);
            } else {
                System.out.println("Phonebook is empty.");
            }
        }

        private AVLNode createNode(Contact contact) {
            return new AVLNode(contact);
        }

        private int getHeight(AVLNode node) {
            if (node != null) {
                return node.height;
            }
            return 0;
        }

        private int getBalanceFactor(AVLNode node) {
            if (node != null) {
                return getHeight(node.left) - getHeight(node.right);
            }
            return 0;
        }

        private void updateHeight(AVLNode node) {
            if (node != null) {
                node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
            }
        }

        private AVLNode rotateLeft(AVLNode node) {
            AVLNode pivot = node.right;
            AVLNode subTree = pivot.left;
            pivot.left = node;
            node.right = subTree;
            updateHeight(node);
            updateHeight(pivot);
            return pivot;
        }

        private AVLNode rotateRight(AVLNode node) {
            AVLNode pivot = node.left;
            AVLNode subTree = pivot.right;
            pivot.right = node;
            node.left = subTree;
            updateHeight(node);
            updateHeight(pivot);
            return pivot;
        }

        private AVLNode balanceNode(AVLNode node) {
            if (node != null) {
                updateHeight(node);
                int balanceFactor = getBalanceFactor(node);

                if (balanceFactor > 1) {
                    if (getBalanceFactor(node.left) >= 0) {
                        return rotateRight(node);
                    } else {
                        node.left = rotateLeft(node.left);
                        return rotateRight(node);
                    }
                }

                if (balanceFactor < -1) {
                    if (getBalanceFactor(node.right) <= 0) {
                        return rotateLeft(node);
                    } else {
                        node.right = rotateRight(node.right);
                        return rotateLeft(node);
                    }
                }
            }
            return node;
        }

        private AVLNode insert(AVLNode node, Contact contact) {
            if (node == null) {
                return createNode(contact);
            }

            if (contact.name.compareTo(node.contact.name) < 0) {
                node.left = insert(node.left, contact);
            } else if (contact.name.compareTo(node.contact.name) > 0) {
                node.right = insert(node.right, contact);
            } else {
                return node;
            }

            return balanceNode(node);
        }

        private AVLNode search(AVLNode node, String name) {
            if (node == null || node.contact.name.equals(name)) {
                return node;
            }

            if (name.compareTo(node.contact.name) < 0) {
                return search(node.left, name);
            } else {
                return search(node.right, name);
            }
        }

        private AVLNode findMinimumNode(AVLNode node) {
            AVLNode current = node;
            while (current != null && current.left != null) {
                current = current.left;
            }
            return current;
        }

        private AVLNode remove(AVLNode node, String name) {
            if (node == null) {
                return null;
            }

            if (name.compareTo(node.contact.name) < 0) {
                node.left = remove(node.left, name);
            } else if (name.compareTo(node.contact.name) > 0) {
                node.right = remove(node.right, name);
            } else {
                if (node.left == null && node.right == null) {
                   
                    node = null;
                } else if (node.left == null) {
                    
                    AVLNode temp = node.right;
                    node = null;
                    return temp;
                } else if (node.right == null) {
                    
                    AVLNode temp = node.left;
                    node = null;
                    return temp;
                } else {
                    
                    AVLNode successor = findMinimumNode(node.right);
                    node.contact = successor.contact;
                    node.right = remove(node.right, successor.contact.name);
                }
            }
            return balanceNode(node);
        }

        private void inOrderTraversal(AVLNode node) {
            if (node != null) {
                inOrderTraversal(node.left);
                displayContact(node.contact);
                inOrderTraversal(node.right);
            }
        }

        private void loadContacts() {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file_name));
                root = null;
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] cities = line.split(" ");
                    String city1 = "", city2 = "";

                    if (cities.length == 2) {
                        city1 = cities[0];
                        city2 = cities[1];
                    }
                    Contact contact = new Contact(city1, city2);
                    root = insert(root, contact);
                }
                reader.close();
            }
            catch (IOException e){
                System.out.println("Passenger List not found. Creating a new file.");
            }
        }
        private void saveContacts() {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));
                saveContactsUtil(root, writer);
                writer.close();
            } catch (IOException e) {
                System.out.println("Error: Unable to save contacts.");
            }
        }

        private void saveContactsUtil(AVLNode node, BufferedWriter writer) throws IOException {
            if (node != null) {
                saveContactsUtil(node.left, writer);
                writer.write(node.contact.name + "\n");
                writer.write(node.contact.phone_number + "\n");
                saveContactsUtil(node.right, writer);
            }
        }

        static void displayContact(Contact contact) {
            System.out.println("Name: " + contact.name);
            System.out.println("Phone Number: " + contact.phone_number);
            System.out.println();
        }
    }

    public static ArrayList<String> deleteLineFromFile(String fileName, int lineToDelete) throws IOException {
        
        ArrayList<String> updatedLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int lineNumber = 1;

        while ((line = reader.readLine()) != null) {
            if (lineNumber != lineToDelete) {
                updatedLines.add(line);
            }
            lineNumber++;
        }
        reader.close();

        try {
            
            FileWriter fileWriter = new FileWriter(fileName, false); 

            fileWriter.close();

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

        return updatedLines;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = null;
        AVLTree phonebook = new AVLTree("Flights.txt");
        System.out.println("Flight Booking System using AVL Tree: ");

        System.out.println("1. Admin access:");
        System.out.println("2. User access:");
        System.out.print("Enter your choice: ");
        int choice1 = sc.nextInt();

        switch (choice1) {
            case 1:
                sc.nextLine();
                System.out.println("Enter Admin id:");
                String username = sc.nextLine();
                System.out.println("Enter your password:");
                String password = sc.nextLine();
                if (!validAdmin(username, password)) {
                    System.out.println("Wrong Admin access,Try again later.");
                    System.exit(0);
                }
                System.out.println("Choose appropriate option:");
                    System.out.println("1.Display passenger list :");
                    System.out.println("2.Add a Flight:");
                    System.out.println("3.Delete a Flight:");
                    System.out.println("4.Exit Admin Menu");
                    int ds = sc.nextInt();
                    switch (ds) {
                        case 1:
                            System.out.println("Please Select Flight For Passenger Details: ");
                            try {
                                
                                reader = new BufferedReader(new FileReader("Flights.txt"));
                                String line;
                                int lineNumber = 1;
                                while ((line = reader.readLine()) != null) {
                                    System.out.println(lineNumber + ". : " + line);
                                    lineNumber++;
                                }
                            } catch (IOException e) {
                                System.out.println("An error occurred: ");
                            }
                            int choice = sc.nextInt();
                            String fileName = "Flights.txt"; 
                            int lineToRead = choice; 
                            try {

                                reader = new BufferedReader(new FileReader(fileName));
                                String line;
                                int lineNumber = 1;

                                while ((line = reader.readLine()) != null) {
                                    if (lineNumber == lineToRead) {

                                        phonebook = new AVLTree(line + ".txt");
                                        break; 
                                    }

                                    lineNumber++;
                                }
                            } catch (IOException e) {
                                System.err.println("An error occurred: " + e.getMessage());
                            }
                            phonebook.displayPhonebook();
                            break;
                        case 2:
                            sc.nextLine();
                            System.out.println("Enter the source and destination of flight to be added: ");
                            String flight = sc.nextLine();
                            fileName = "Flights.txt"; 

                            try {
                                
                                FileWriter fileWriter = new FileWriter(fileName, true);

                                
                                fileWriter.write("\n");
                                fileWriter.write(flight);

                                
                                fileWriter.close();

                                System.out.println("Flight has been added.");
                            } catch (IOException e) {
                                System.err.println("An error occurred: " + e.getMessage());
                            }
                            break;
                        case 3:
                            System.out.println("Choose the flight to be deleted(Source and Destination): ");
                            try {
                                reader = new BufferedReader(new FileReader("Flights.txt"));
                            } catch (Exception e) {
                                System.out.println("Invalid");
                            }

                            String line;
                            int lineNumber = 1;
                            try {
                                while ((line = reader.readLine()) != null) {
                                    System.out.println(lineNumber + ". : " + line);
                                    lineNumber++;
                                }

                            } catch (Exception e) {
                                System.out.println("Invalid");
                            }

                            choice = sc.nextInt();
                            fileName = "Flights.txt"; 
                            int lineToDelete = choice; 
                            ArrayList<String> up = new ArrayList<>();
                            try {
                                up = deleteLineFromFile(fileName, lineToDelete);
                                System.out.println("Line " + "flight" + " has been deleted from the file.");
                            } catch (IOException e) {
                                System.err.println("An error occurred: " + e.getMessage());
                            }
                            fileName = "Flights.txt";

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                                for (int i = 0; i < up.size(); i++) {
                                    writer.write(up.get(i));
                                    writer.newLine(); 
                                }
                                System.out.println("Data written to " + fileName);
                            } catch (IOException e) {
                                System.out.println("An error occurred: ");
                            }
                            break;
                        case 4:
                            System.exit(0);
                            break;
                    }

            break;
            case 2:
                System.out.println("Please Select Flight Below: ");

                try {
                    
                    reader = new BufferedReader(new FileReader("Flights.txt"));
                    String line;
                    int lineNumber = 1;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(lineNumber + ". : " + line);
                        lineNumber++;
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred: ");
                }

                int choice = sc.nextInt();

                    String fileName = "Flights.txt"; 
                    int lineToRead = choice; 

                    try {
                        
                        reader = new BufferedReader(new FileReader(fileName));
                        String line;
                        int lineNumber = 1;

                        while ((line = reader.readLine()) != null) {
                            if (lineNumber == lineToRead) {
                                File file = new File(line + ".txt");
                                if (!file.exists()) {
                                    FileWriter fileWriter = new FileWriter(line + ".txt");
                                    fileWriter.close();
                                }
                                phonebook = new AVLTree(line + ".txt");
                                break; 
                            }
                            lineNumber++;
                        }
                    } catch (IOException e) {
                        System.err.println("An error occurred: " + e.getMessage());
                    }
                    System.out.println("1. Add Passenger");
                    System.out.println("2. Search Reservation");
                    System.out.println("3. Cancel Reservation");
                    System.out.println("4. Exit Menu");
                    int choice2 = sc.nextInt();

                    switch (choice2) {
                        case 1:
                            sc.nextLine();
                            System.out.print("Enter contact name: ");
                            String name = sc.nextLine();

                            System.out.print("Enter phone number: ");
                            String phoneNumber = sc.nextLine();
                            Contact contact = new Contact(name, phoneNumber);
                            phonebook.addContact(contact);
                            break;
                        case 2:
                            sc.nextLine();
                            System.out.print("Enter passenger name to search: ");
                            String searchName = sc.nextLine();
                            phonebook.searchContact(searchName);
                            break;
                        case 3:
                            sc.nextLine();
                            System.out.print("Enter passenger name to remove: ");
                            String removeName = sc.nextLine();
                            phonebook.removeContact(removeName);
                            break;
                        case 4:
                            System.out.println("Exiting...");
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
                }
        } }
