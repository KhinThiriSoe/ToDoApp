package com.khin.todoapplication.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.khin.todoapplication.R
import com.khin.todoapplication.data.viewmodel.ToDoViewModel
import com.khin.todoapplication.data.models.Priority
import com.khin.todoapplication.data.models.ToDoData
import com.khin.todoapplication.fragments.SharedViewModel

class AddFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var titleText: EditText
    private lateinit var prioritiesSpinner: Spinner
    private lateinit var descriptionText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleText = view.findViewById(R.id.title_et)
        prioritiesSpinner = view.findViewById(R.id.priorities_spinner)
        descriptionText = view.findViewById(R.id.description_et)

        prioritiesSpinner.onItemSelectedListener = sharedViewModel.listener
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertDataToDo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDo() {
        val mTitle = titleText.text.toString()
        val mPriority = prioritiesSpinner.selectedItem.toString()
        val mDescription = descriptionText.text.toString()

        val validation = sharedViewModel.verifyDataFromUser(mTitle, mDescription)
        if (validation) {
            // Insert data to database
            val newData = ToDoData(
                0,
                mTitle,
                sharedViewModel.parsePriority(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()

            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
        }

    }
}