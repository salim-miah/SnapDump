package com.example.snapdump

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.snapdump.databinding.FragmentEntityFormBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


/**
 * A simple [Fragment] subclass.
 * Use the [EntityForm.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntityForm : Fragment() {

    private lateinit var binding: FragmentEntityFormBinding
    private lateinit var apiService: ApiService
    private var selectedImageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEntityFormBinding.inflate(inflater, container, false)

        // Add logging interceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://labs.anontech.info/cse489/t3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        binding.buttonSelectImage.setOnClickListener { selectImage() }
        binding.buttonSubmit.setOnClickListener { submitForm() }

        return binding.root
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    private fun submitForm() {
        val title = binding.editTextTitle.text.toString()
        val latitude = binding.editTextLatitude.text.toString().toDoubleOrNull()
        val longitude = binding.editTextLongitude.text.toString().toDoubleOrNull()

        if (title.isBlank() || latitude == null || longitude == null || selectedImageUri == null) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val imageStream = context?.contentResolver?.openInputStream(selectedImageUri!!)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val filename = randomimagename()+".jpg"
        val imageBody = MultipartBody.Part.createFormData("image", filename, requestFile)
        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val latitudeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude.toString())
        val longitudeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude.toString())

        val call = apiService.createEntity(
            title = titleBody,
            lat = latitudeBody,
            lon = longitudeBody,
            image = imageBody
        )

        call.enqueue(object : Callback<Entity> {
            override fun onResponse(call: Call<Entity>, response: Response<Entity>) {
                if (response.isSuccessful) {
                    val entity = response.body()
                    if (entity != null) {
                        Toast.makeText(context, "Entity created with ID: ${entity.id}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("EntityFormFragment", "Response error: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to create entity", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Entity>, t: Throwable) {
                Log.e("EntityFormFragment", "Request failed: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun randomimagename(): String{
        var filename = ""
        for (i in 1..19){
            val random_num = (1..9).shuffled().first()
            filename+=random_num.toString()
        }
        return filename
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imageViewSelected.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 100

        @JvmStatic
        fun newInstance(entity: Entity): EntityForm {
            return EntityForm()
        }
    }

}