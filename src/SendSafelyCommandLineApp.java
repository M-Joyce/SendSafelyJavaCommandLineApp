import java.util.ArrayList;
import java.util.Scanner;

//Note: You can build the jar for this, but due to the signed files in META-INF it may trigger a JNI excpetion with java -jar.
//You can fix this easily open the built jar as an archive file (7z/rar), then find META-INF folder and delete all find with .SF, .DSA, .RSA
//I have already included my built jar in "out/artifacts" and completed this step.
//OR Just run this from an IDE/compiler.

public class SendSafelyCommandLineApp {

    public static boolean notify = false; //Used to toggle notify mode.


    public static void main(String[] args) {

        System.out.println("Welcome to Mark's SendSafely Command Line App!");
        SendSafelyInstance ssInstance = new SendSafelyInstance();


        System.out.println("Would you like to create a package for secure file sharing (Y/N)?");

        //Create a scanner to handle input.
        Scanner sc = new Scanner(System.in);
        String choice;

        //loop for valid input.
        while(true){
            choice = sc.nextLine();
            if(choice.equalsIgnoreCase("y")) {
                ssInstance.createPackage();
                break;
            }
            else if(choice.equalsIgnoreCase("n")){
                System.out.println("Can't continue without a package. Stopping the utility..");
                sc.close(); //close the scanner.
                return;
            }
            else{
                System.out.println("That was an invalid choice, please choose Yes or No (Y/N).");
            }
        }

        //Get input for recipients to add
        System.out.println("Please enter the emails of the recipients you wish to add, or enter \"N\" when done.");

        //Using this ArrayList to add all recipients in one go
        ArrayList<String> recipientsToAdd = new ArrayList<String>();

        //Getting input for recipients
        String input;
        while(true){
            input = sc.nextLine();
            if(input.equalsIgnoreCase("n")){ //break out if user is done.
                System.out.println("Adding Recipients..");
                break;
            }
            recipientsToAdd.add(input); //add to recipients list.
        }

         //if recipientsToAdd has atleast one recipient, add it to the package.
        if(recipientsToAdd.size() >= 1){
            ssInstance.addRecipients(recipientsToAdd);
        }
        else{
            System.out.println("You didn't add any recipients, there is no one to send the package to. Stopping the utility..");
            sc.close(); //close the scanner.
            return;
        }

        //Get input for files to add
        System.out.println("Please enter the paths to the files you would like the upload, or enter \"N\" when done.");


        //Collecting all the paths to upload files for in this ArrayList
        //My reasoning is if I want to add several large  files, I don't want to wait for each upload to finish
        //before I can enter the path for the next file, but rather add them all at once, and do other things
        //while I wait for all my uploads to complete.
        ArrayList<String> filesToAdd = new ArrayList<String>();

        //Getting input for file paths
        String fileInput;
        while(true){
            fileInput = sc.nextLine();
            if(fileInput.equalsIgnoreCase("n")){ //break out if user is done.
                System.out.println("Adding Files..");
                break;
            }
            filesToAdd.add(fileInput); //add to recipients list.
        }

        //if recipientsToAdd has atleast one recipient, add it to the package.
        if(filesToAdd.size() >= 1){
            ssInstance.uploadFiles(filesToAdd);
        }
        else{
            System.out.println("You didn't add files, there is nothing to upload. Stopping the utility..");
            ssInstance.deleteTempPackage(); //delete temp package
            sc.close(); //close the scanner.
            return;
        }

        System.out.println("Would you like to notify recipients? (Y/N)");

        String notifyChoice;

        //loop for valid input.
        while(true){
            notifyChoice = sc.nextLine();
            if(notifyChoice.equalsIgnoreCase("y")) {
                notify = true;
                break;
            }
            else if(notifyChoice.equalsIgnoreCase("n")){
                notify = false;
                break;
            }
            else{
                System.out.println("That was an invalid choice, please choose Yes or No (Y/N).");
            }
        }

        //Finalize the package and provide a link.
        ssInstance.finalizePackage(notify);
        sc.close(); //close the scanner.


    }

}
