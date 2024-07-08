package com.example.snapdump

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapdump.databinding.FragmentEntityListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass.
 * Use the [EntityList.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntityList : Fragment() {

    private lateinit var binding: FragmentEntityListBinding
    private lateinit var apiService: ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEntityListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://labs.anontech.info/cse489/t3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        fetchEntities()

        return binding.root
    }

    private fun fetchEntities() {
        apiService.getEntities().enqueue(object : Callback<List<Entity>> {
            override fun onResponse(call: Call<List<Entity>>, response: Response<List<Entity>>) {
                if (response.isSuccessful) {
                    response.body()?.let { entities ->
                        binding.recyclerView.adapter = EntityAdapter(entities) { entity ->
                            // Handle item click
                            // For example, navigate to the Entity form fragment for editing
                            val fragment = EntityForm.newInstance(entity)
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.frame, fragment)
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Entity>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}