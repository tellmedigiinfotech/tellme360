package tellme.sairajpatil108.tellme360

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp

import tellme.sairajpatil108.tellme360.ui.theme.TellMe360Theme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        
        setContent { 
            TellMe360Theme {
                TellMe360AppAndroid()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}