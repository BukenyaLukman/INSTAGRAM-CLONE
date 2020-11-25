package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        sign_in_link_btn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }

        sign_up_btn.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {
        val fullName = full_name_sign_up.text.toString()
        val userName = username_sign_up.text.toString()
        val email = email_sign_up.text.toString()
        val password = password_sign_up.text.toString()


        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "full name is required", Toast.LENGTH_LONG)
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "user name is required", Toast.LENGTH_LONG)
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required", Toast.LENGTH_LONG)
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required", Toast.LENGTH_LONG)

            else->{

                val progressDialog = ProgressDialog(this@SignUpActivity);
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait")
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show()


                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful)
                        {
                            saveUserInfo(fullName,userName, email, progressDialog)
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message$", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String,Any>()

        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hey I am using A coder"


        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-8be58.appspot.com/o/DefaultImages%2Fprofile.png?alt=media&token=b176b3fb-bb31-4ea0-bf07-1d3dc6f86d99"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account Created successfully", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()


                }else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message$", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

    }
}