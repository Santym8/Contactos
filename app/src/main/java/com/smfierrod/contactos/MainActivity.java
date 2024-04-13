package com.smfierrod.contactos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText txtName, txtPhone, txtEmail;
    Button btnSave, btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnSearch = findViewById(R.id.btnSearch);
    }



    public void saveContact_onClick(View view) {
        String name = txtName.getText().toString();
        String phone = txtPhone.getText().toString();
        String email = txtEmail.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Correo no valido", Toast.LENGTH_SHORT).show();
            return;
        }



        Contact contact = getContactByName(name);
        boolean newContact;

        if (contact == null) {
            contact = new Contact(name, phone, email);
            contact.setId(String.valueOf(System.currentTimeMillis()));
            newContact = true;
        } else {
            contact.setPhone(phone);
            contact.setEmail(email);
            newContact = false;
        }

        saveContact(contact);

        if (!newContact) {
            Toast.makeText(this, "Contacto Actualizado", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Nuevo Contacto Guardado", Toast.LENGTH_SHORT).show();

    }

    public void searchContact_onClick(View view) {
        String nameToSearch = txtName.getText().toString();

        if (nameToSearch.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        Contact contact = getContactByName(nameToSearch);

        if (contact == null) {
            txtPhone.setText("");
            txtEmail.setText("");
            Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        txtName.setText(contact.getName());
        txtPhone.setText(contact.getPhone());
        txtEmail.setText(contact.getEmail());

        txtName.setSelection(txtName.getText().length());

        Toast.makeText(this, "Contacto encontrado", Toast.LENGTH_SHORT).show();
    }

    private Contact searchContact(String nameToSearch) {
        SharedPreferences preferences = getSharedPreferences("datos", MODE_PRIVATE);

        Map<String, ?> allContacts = preferences.getAll();
        for (Map.Entry<String, ?> entry : allContacts.entrySet()) {
            String[] contactData = entry.getValue().toString().split(",");
            if (contactData[0].toLowerCase().contains(nameToSearch.toLowerCase())) {
                return new Contact(entry.getKey(), contactData[0], contactData[1], contactData[2]);
            }
        }
        return null;
    }


    private Contact getContactByName(String name) {
        SharedPreferences preferences = getSharedPreferences("datos", MODE_PRIVATE);
        Map<String, ?> allContacts = preferences.getAll();
        for (Map.Entry<String, ?> entry : allContacts.entrySet()) {
            String[] contactData = entry.getValue().toString().split(",");
            Contact contact = new Contact(entry.getKey(), contactData[0], contactData[1], contactData[2]);
            if (contact.getName().equalsIgnoreCase(name)) {
                return contact;
            }
        }
        return null;
    }

    private void saveContact(Contact contact) {
        SharedPreferences preferences = getSharedPreferences("datos", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(contact.getId(), contact.getName() + "," + contact.getPhone() + "," + contact.getEmail());
        editor.apply();
    }
}