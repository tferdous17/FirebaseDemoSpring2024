package aydin.firebasedemospring2024;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class WelcomeController {


    public TextField welcomeNameField;
    public TextField welcomeAgeField;
    public Button btnRegister;
    public Button btnSignIn;
    public TextField welcomeEmailField;
    public TextField welcomePhoneNumField;
    public Label labelStatusMsg;


    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }

    @FXML
    private void register() throws IOException {
        if (registerUser()) {
            labelStatusMsg.setText("User successfully registered. Please log in using only Email and Phone number.");
            welcomeNameField.setVisible(false);
            welcomeAgeField.setVisible(false);
            btnRegister.setDisable(true);
            welcomeEmailField.clear();
            welcomePhoneNumField.clear();
        }
    }

    @FXML
    private void signIn() {
        String email = welcomeEmailField.getText();
        String phoneNum = welcomePhoneNumField.getText();

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
                .setEmail(welcomeEmailField.getText())
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber(welcomePhoneNumField.getText())
                .setDisplayName("John Doe")
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
}
