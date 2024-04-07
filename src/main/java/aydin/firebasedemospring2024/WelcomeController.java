package aydin.firebasedemospring2024;

import java.io.IOException;
import java.util.*;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
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


    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }

    @FXML
    private void register() throws IOException {
        if (registerUser()) {
            addUserToDb();
            registerNameTxt.clear();
            registerEmailTxt.clear();
            registerPhoneTxt.clear();
            registerPassTxt.clear();
        }
    }

    @FXML
    private void signIn() {
        String email = registerEmailTxt.getText();
        String phoneNum = registerPhoneTxt.getText();

        try {
            UserRecord user = getUserByEmailOrPhoneNumber(email, phoneNum);
            if (user != null) {
                System.out.println("Successfully signed in.");
                switchToPrimary();
            }
        } catch (FirebaseAuthException | IOException e) {
            System.out.println("ERROR: Could not sign in. Please check email or phone number.");
        }
    }

    private UserRecord getUserByEmailOrPhoneNumber(String email, String phoneNum) throws FirebaseAuthException, IOException {
        UserRecord user = DemoApp.fauth.getUserByEmail(email);
        if (user == null) {
            user = DemoApp.fauth.getUserByPhoneNumber(phoneNum);
        }
        return user;
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
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("Error creating a new user in the firebase");
            ex.printStackTrace();
            return false;

        }

    }

    public void addUserToDb() {
        DocumentReference docRef = DemoApp.fstore.collection("Persons").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", registerNameTxt.getText());
        data.put("Password", registerPassTxt.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }
}
