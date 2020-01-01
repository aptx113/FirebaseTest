package com.george.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.http.Tag
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val current = LocalDateTime.now()
        val sdf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        currentDate = current.format(sdf)
        auth = FirebaseAuth.getInstance()

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()



        signIn("george1@gmail.com", "1234567")
//        createAccount("george2@gmail.com", "123456789", "george2")
        postArticle("Freud", "Dream", ArticleTags.Gossiping)
//        getUserInfo("jim@gmail.com")
//        getAllUserInfo()
//        getArticleDetail("Success")
//        getAllArticleDetail()
    }

    private fun createAccount(email: String, password: String, name: String) {
        Log.d(TAG, "createAccount:$email")


        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()

                    auth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User profile updated")
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

//                val user = hashMapOf(
//                    "email" to email,
//                    "id" to auth.currentUser?.uid,
//                    "name" to name
//                )
                val user = UserSystem(email, name, auth.currentUser?.uid)

                db.collection("Users").document(email)
                    .set(user)
                    .addOnSuccessListener {
                        Log.d(
                            TAG,
                            "Users DocumentSnapshot successfully written!"
                        )
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing users document", e) }

            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")


        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser?.email

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }


                // [END_EXCLUDE]
            }
        // [END sign_in_with_email]
    }

    private fun postArticle(
        title: String,
        content: String,
        tag: ArticleTags
    ) {

        val article = PostFunction(
            db.collection("Article").document().id,
            title,
            content,
            tag,
            auth.currentUser?.displayName,
            currentDate
        )
//        val article = hashMapOf(
//
//            "article_id" to db.collection("Article").document().id,
//            "article_title" to title,
//            "article_content" to content,
//            "article_tag" to tag,
//            "author" to author,
//            "created_time" to currentDate
//        )

        db.collection("Article").document(title)
            .set(article)
            .addOnSuccessListener { Log.d(TAG, "Article DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing article document", e) }
    }

//    private fun addUserAnn() {
//
//        val user = UserSystem(
//            "9527@gmail.com", "Ann", "009527"
//        )
//
//        db.collection("Users").document(user.email)
//            .set(user)
//            .addOnSuccessListener { Log.d(TAG, "Users DocumentSnapshot successfully written!") }
//            .addOnFailureListener { e -> Log.w(TAG, "Error writing users document", e) }
//    }

//    private fun postAnnArticle() {
//
//        val article = PostFunction("001", "Sorrow", "sad", ArticleTags.Beauty, "Ann", currentDate)
//
//        db.collection("Article").document(article.article_title)
//            .set(article)
//            .addOnSuccessListener { Log.d(TAG, "Article DocumentSnapshot successfully written!") }
//            .addOnFailureListener { e -> Log.w(TAG, "Error writing articles document", e) }
//    }

    private fun getUserInfo(email: String) {

        val docRef = db.collection("Users").document(
            email
        )
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(UserSystem::class.java)
            Log.d(
                TAG, "User information:${documentSnapshot.data}"
//                "email = ${user?.email}, name = ${user?.name}, id = ${user?.id}"
            )
        }
    }

    private fun getAllUserInfo() {

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun getArticleDetail(title: String) {

        val docRef = db.collection("Article").document(
            title
        )
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(UserSystem::class.java)
            Log.d(
                TAG, "Article details:${documentSnapshot.data}"
//                "email = ${user?.email}, name = ${user?.name}, id = ${user?.id}"
            )
        }
    }

    private fun getAllArticleDetail() {

        db.collection("Article")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    companion object {
        private const val TAG = "GeorgeTest"
    }
}
