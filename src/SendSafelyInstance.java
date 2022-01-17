import com.sendsafely.File;
import com.sendsafely.Package;
import com.sendsafely.Recipient;
import com.sendsafely.SendSafely;
import com.sendsafely.dto.PackageURL;
import com.sendsafely.dto.UserInformation;
import com.sendsafely.exceptions.*;
import com.sendsafely.file.DefaultFileManager;
import com.sendsafely.file.FileManager;

import java.io.IOException;
import java.util.ArrayList;

public class SendSafelyInstance {

    //Obviously not the best method of storing secret info, but it will do for the purposes of this assessment.
    //For a real system, this could all be stored encrypted in a database.

    //Removing apikey and secrets in order to publish to github, to test, please generate your won credentials at SendSafely
    private static final String apiKey = "add yours here";
    private static final String apiSecret = "add yours here";
    private static final String host = "https://app.sendsafely.com";

    private final SendSafely sendsafely;
    private Package pkgInfo = null;


    /**
     * Constructor, attempts to initialize an instance for the SendSafelyAPI
     */
    public SendSafelyInstance(){
        //Initialize the API
        sendsafely = new SendSafely(host, apiKey, apiSecret);

        //Verify that the API credentials are valid
        try{
            UserInformation userInformation = sendsafely.getUserInformation();
            System.out.println("Connected to SendSafely as user " + userInformation.getEmail());
        } catch (UserInformationFailedException e) {
            System.out.println("Something went wrong when trying to connect to SendSafely! Are the keys invalid?");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Returns the created package, or a null package in the case of exception.
     * @return a Package
     */
    public Package createPackage(){

        try{
            pkgInfo = sendsafely.createPackage();
            System.out.println("Created new empty package with Package ID" + pkgInfo.getPackageId());
        } catch (CreatePackageFailedException e) {
            System.out.println("Something went wrong! Could not create a package!");
            e.printStackTrace();
        } catch (LimitExceededException e) {
            System.out.println("You have exceeded the allowable number of draft items. You cannot create new items until you delete your draft items.");
            System.exit(1);
        }
        return pkgInfo;
    }

    /**
     * Adds recipients to the package.
     * @param recipientsToAdd an ArrayList of all recipients the user wants to add.
     */
    public void addRecipients(ArrayList<String> recipientsToAdd){
        //Adds recipient(s) to the package

        for(String recipient : recipientsToAdd){
            try{
                Recipient newRecipient = sendsafely.addRecipient(pkgInfo.getPackageId(), recipient);
                System.out.println("Added new recipient (Id: " + newRecipient.getRecipientId() + ") " + "(Email: " + newRecipient.getEmail() + ")");
            } catch (RecipientFailedException e) {
                System.out.println("Did not add recipient: " + recipient + ". Invalid Email address."); //Let user know recipient email was invalid, and could not be added.
            } catch (LimitExceededException e) {
                System.out.println("You have exceeded the allowable number of draft items. You cannot create new items until you delete your draft items.");
                System.exit(1);
            }
        }
    }

    /**
     * Adds files to the package.
     * @param filesToAdd an ArrayList of all recipients the user wants to add.
     */
    public void uploadFiles(ArrayList<String> filesToAdd){

        for(String filePath : filesToAdd) {
            FileManager fileManager = null;
            try {
                fileManager = new DefaultFileManager(new java.io.File(filePath));
            } catch (IOException e) {
                System.out.println("File not found: " + filePath);
                continue;
            }

            File addedFile = null;
            try {
                addedFile = sendsafely.encryptAndUploadFile(pkgInfo.getPackageId(), pkgInfo.getKeyCode(), fileManager, new Progress());
            } catch (LimitExceededException e) {
                System.out.println("You have exceeded the allowable number of draft items. You cannot create new items until you delete your draft items.");
                System.exit(1);
            } catch (UploadFileException e) {
                System.out.println("Upload failed for: " + filePath);
            }
            System.out.println("File: " + addedFile.getFileName() + " was added with Id# " + addedFile.getFileId());
        }
    }

    /**
     * finalize the package.
     * @param notify whether or not to notify recipients of package link.
     */
    public void finalizePackage(boolean notify){

        if(!notify){
            //Finalize the package so we can send the link to our recipient
            PackageURL pURL = null;
            try {
                pURL = sendsafely.finalizePackage(pkgInfo.getPackageId(), pkgInfo.getKeyCode());
            } catch (LimitExceededException e) {
                System.out.println("You have exceeded the allowable number of draft items. You cannot create new items until you delete your draft items.");
                System.exit(1);
            } catch (FinalizePackageFailedException e) {
                System.out.println("An exception occurred: Could not finalize the package.");
                e.printStackTrace();
            } catch (ApproverRequiredException e) {
                System.out.println("An approver is required!");
                e.printStackTrace();
            }
            System.out.println("Package was finalized. The package can be downloaded from the following URL: " + pURL.getSecureLink());
        }
        else{ //notify emails.
            PackageURL pURL = null;
            try {
                pURL = sendsafely.finalizePackage(pkgInfo.getPackageId(), pkgInfo.getKeyCode(), true);
            } catch (LimitExceededException e) {
                System.out.println("You have exceeded the allowable number of draft items. You cannot create new items until you delete your draft items.");
                System.exit(1);
            } catch (FinalizePackageFailedException e) {
                System.out.println("An exception occurred: Could not finalize the package.");
                e.printStackTrace();
            } catch (ApproverRequiredException e) {
                System.out.println("An approver is required!");
                e.printStackTrace();
            }
            System.out.println("Package was finalized. The package can be downloaded from the following URL: " + pURL.getSecureLink());
            System.out.println("Notify package recipients status: " + pURL.getNotificationStatus());
        }
    }

    /**
     * Deletes temporary package, in the case of user not continuing.
     */
    public void deleteTempPackage(){
        //Delete the temp package if we aren't finalizing.
        try {
            sendsafely.deleteTempPackage(pkgInfo.getPackageId());
        } catch (DeletePackageException e) {
            System.out.println("An exception occurred: Could not delete temp package.");
        }
        System.out.println("Temp package was deleted.");

    }

}
