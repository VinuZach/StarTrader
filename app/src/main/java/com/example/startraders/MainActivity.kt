package com.example.startraders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.startraders.Repository.RetrofitManger
import com.example.startraders.Repository.SharedPrefData.Companion.COLLECTION_AGENT_ID
import com.example.startraders.Repository.SharedPrefData.Companion.INVOICE_ID
import com.example.startraders.Repository.SharedPrefData.Companion.IS_USER_LOGGED_IN
import com.example.startraders.models.LoginResponse

/**
 * Main activity
 *  https://appsonline.in/star

user : BALU1@GMAIL.COM
pw : BALUSL
website: https://appsonline.in/star



 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity()
{

    val fontFamily = FontFamily(Font(R.font.lexend_thin, FontWeight.Thin), Font(R.font.lexend_bold, FontWeight.Bold),
        Font(R.font.lexend_extrabold, FontWeight.ExtraBold), Font(R.font.lexend_light, FontWeight.Light),
        Font(R.font.lexend_medium, FontWeight.Medium), Font(R.font.lexend_regular, FontWeight.Normal),
        Font(R.font.lexend_semibold, FontWeight.SemiBold))

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            Greeting()
        }
        val isAlreadyLogin = RepositoryManager.sharedPrefData.getDataWithoutLiveData(this, IS_USER_LOGGED_IN, false)
        if (isAlreadyLogin)
        {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

//        RepositoryManager.retrofitObject.verifyUser("BALU1@GMAIL.COM",
//                "BALUSL",
//                object : RetrofitManger.ApiResponse {
//                    override fun onResponseObtained(isSuccess : Boolean, responseData : Any?) {
//
//                        if (isSuccess) {
//                            val loginResponse = responseData as LoginResponse
//                            RepositoryManager.sharedPrefData.saveDataToDataStore(this@MainActivity,
//                                    IS_USER_LOGGED_IN, true)
//                            RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@MainActivity,
//                                    INVOICE_ID, data = loginResponse.newInvoice)
//
//                            RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@MainActivity,
//                                    COLLECTION_AGENT_ID, data = loginResponse.id)
//
//                            startActivity(Intent(this@MainActivity,
//                                    HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
//                            finish()
//                        }
//                        else {
//                            val errorMessage = responseData as String
//                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT)
//                                .show()
//                        }
//
//                    }
//
//                })
    }

    @Preview
    @Composable
    fun Greeting()
    {
        val colorPrimary = Color(ContextCompat.getColor(this@MainActivity, R.color.primary))
        val colorPrimaryDark = Color(ContextCompat.getColor(this@MainActivity, R.color.primaryDark))
        val accentColor = Color(ContextCompat.getColor(this@MainActivity, R.color.accentcolor))
        val white = Color(ContextCompat.getColor(this@MainActivity, R.color.white))
        val black = Color(ContextCompat.getColor(this@MainActivity, R.color.black))
        val lightGrey = Color(ContextCompat.getColor(this@MainActivity, R.color.lightgrey))
        var userName by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var isBoxVisible by rememberSaveable { mutableStateOf(0.0f) }


        val isUserNameValid = (userName.count() > 1 && '@' in userName)
        val isPasswordValid = password.count() > 1

        val checkEnteredValues = isUserNameValid && isPasswordValid
        val buttonColor = if (checkEnteredValues) accentColor
        else colorPrimaryDark

        val buttonBorder = if (checkEnteredValues) null else BorderStroke(2.dp, accentColor)

        val buttonTextColor = if (checkEnteredValues) white else black

        var passwordVisibility by remember { mutableStateOf(false) }
        var isEditDisabled by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp).padding(top = 50.dp).border(1.dp, colorPrimaryDark),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

            Column(modifier = Modifier.fillMaxWidth()
                // margin
                .background(color = colorPrimary).border(2.dp, Color.Transparent) // outer border
                .padding(8.dp) // space between the borders
                .border(2.dp, Color.Transparent) // inner border
                .padding(8.dp)) {
                Column {
                    Text(text = "Sign In", fontFamily = fontFamily, fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth())
                }


            }

            Column(modifier = Modifier.background(Color.White)) {

                val localFocusManager = LocalFocusManager.current

                OutlinedTextField(colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = colorPrimaryDark,
                    unfocusedBorderColor = colorPrimary), value = userName, maxLines = 1,

                    onValueChange = {
                        userName = it.trim()
                    }, label = {
                        Text("E-mail", fontFamily = fontFamily, fontWeight = FontWeight.Medium, color = colorPrimaryDark)
                    },

                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).padding(10.dp), readOnly = isEditDisabled,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { localFocusManager.moveFocus(FocusDirection.Down) }))

                OutlinedTextField(colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = colorPrimaryDark,
                    unfocusedBorderColor = colorPrimary), value = password, maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { localFocusManager.moveFocus(FocusDirection.Down) }),
                    readOnly = isEditDisabled, visualTransformation = if (passwordVisibility) VisualTransformation.None
                    else PasswordVisualTransformation(), onValueChange = {
                        password = it.trim()
                    }, trailingIcon = {
                        val image = if (passwordVisibility) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(imageVector = image, "")
                        }
                    }, label = {
                        Text("Password", fontFamily = fontFamily, fontWeight = FontWeight.Medium,

                            color = colorPrimaryDark)
                    },

                    modifier = Modifier.fillMaxWidth().padding(top = 0.dp).padding(10.dp))

            }
            Button(onClick = {

                if (isUserNameValid && isPasswordValid)
                {
                    isBoxVisible = 1f
                    isEditDisabled = true
                    RepositoryManager.retrofitObject.verifyUser(userName, password, object : RetrofitManger.ApiResponse
                    {
                        override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                        {
                            isBoxVisible = 0f
                            isEditDisabled = false
                            if (isSuccess)
                            {
                                val loginResponse = responseData as LoginResponse
                                RepositoryManager.sharedPrefData.saveDataToDataStore(this@MainActivity, IS_USER_LOGGED_IN, true)
                                RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@MainActivity, INVOICE_ID,
                                    data = loginResponse.newInvoice)

                                RepositoryManager.sharedPrefData.saveDataToDataStore<String>(this@MainActivity, COLLECTION_AGENT_ID,
                                    data = loginResponse.id)

                                startActivity(Intent(this@MainActivity, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                                finish()
                            }
                            else
                            {
                                val errorMessage = responseData as String
                                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }

                        }

                    })
                    Log.d("ererhe", "userName: " + userName)
                    Log.d("ererhe", "password: " + password)


                }

            },

                modifier = Modifier.padding(10.dp).padding(20.dp).fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(backgroundColor = buttonColor), enabled = checkEnteredValues,
                border = buttonBorder) {
                Text(
                    text = "Submit",
                    color = buttonTextColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp),
                    )
            }


        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().alpha(isBoxVisible).background(lightGrey)

           ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()

                Text(
                    text = "Verifying User Data",
                    fontFamily = fontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(20.dp),
                    )
            }

        }
    }


}