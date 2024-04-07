package aydin.firebasedemospring2024;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class WelcomeController {

    public Label labelStatusMsg;
    public TextField registerNameTxt;
    public TextField registerEmailTxt;
    public TextField registerPassTxt;
    public TextField registerPhoneTxt;
    public Button registerButton;
    public TextField signInEmailTxt;
    public TextField signInPassTxt;
    public Button signInBtn;


    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }

    @FXML
    private void register() throws IOException {
        if (registerUser()) {
            registerNameTxt.clear();
            registerEmailTxt.clear();
            registerPhoneTxt.clear();
            registerPassTxt.clear();
        }
    }

    @FXML
    private void signIn() {
        String email = signInEmailTxt.getText();
        String password = signInPassTxt.getText();

        try {
            UserRecord user = DemoApp.fauth.getUserByEmail(email);
            if (user != null) {
                String uuid = user.getUid();
                if (password.equals(retrievePersonByUuidAndReturnPass(uuid))) {
                    switchToPrimary();
                } else {
                    System.out.println("ERROR: Invalid password.");
                }
            }
        } catch (FirebaseAuthException | IOException e) {
            System.out.println("ERROR: Could not sign in.");
        }
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(registerEmailTxt.getText())
                .setDisplayName(registerNameTxt.getText())
                .setEmailVerified(false)
                .setPhoneNumber(registerPhoneTxt.getText())
                .setDisabled(false);


        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            addUserToDb(userRecord.getUid());
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            System.out.println("Error creating a new user in the firebase");
            ex.printStackTrace();
            return false;

        }

    }

    public void addUserToDb(String userUUID) {
        DocumentReference docRef = DemoApp.fstore.collection("Persons").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("UUID", userUUID);
        data.put("Name", registerNameTxt.getText());
        data.put("Password", registerPassTxt.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    private String retrievePersonByUuidAndReturnPass(String uuid) {
        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Persons").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(!documents.isEmpty()) {
                System.out.println("Fetching data from firebase database..");
                for (QueryDocumentSnapshot document : documents) {
                    if (document.getData().get("UUID").equals(uuid)) {
                        System.out.println("SUCCESS: Found person with corresponding UUID");
                        return document.getData().get("Password").toString();
                    }
                }
            } else {
                System.out.println("No data");
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
