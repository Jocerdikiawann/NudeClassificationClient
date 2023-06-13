package com.example.nudeclassification

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nudeclassification.data.RetrofitHelper
import com.example.nudeclassification.data.RetrofitInterface
import com.example.nudeclassification.ui.theme.NudeClassificationTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.joda.time.DateTime
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun bitmapToFile(
    context: Context,
    bitmap: Bitmap,
    fileName: String
): File? {// File name like "image.png"
    //create a file to write bitmap data

    return try {
        val dir = context.filesDir
        val file = File(dir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isNudeApi = RetrofitHelper.getInstance().create(RetrofitInterface::class.java)
            var isNude by remember { mutableStateOf("no response") }
            var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
            val now = DateTime.now()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            fun uploadFile(file: File) {
                scope.launch {
                    val result = isNudeApi.checkIsNude(
                        image = MultipartBody.Part.createFormData(
                            "image",
                            file.name,
                            file.asRequestBody("image/png".toMediaTypeOrNull())
                        )
                    )
                    Log.e("hello", result.body().toString())
                    isNude = result.body()?.result ?: "empty response"
                }
            }

            val launchCamera = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap: Bitmap? ->
                try {
                    bitmap?.let {
                        bitmapImage = it
                        bitmapToFile(context, it, "$now")?.let {
                            uploadFile(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("E", e.message.toString())
                }

            }



            NudeClassificationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier=Modifier.padding(horizontal = 20.dp)) {
                        bitmapImage?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Greeting(isNude)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            onClick = { scope.launch { launchCamera.launch(null) } }) {
                            Text(text = "Upload Image")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Result : $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NudeClassificationTheme {
        Greeting("Android")
    }
}