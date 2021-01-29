package com.khin.todoapplication.fragments.update

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.khin.todoapplication.R
import com.khin.todoapplication.data.models.ToDoData
import com.khin.todoapplication.data.viewmodel.ToDoViewModel
import com.khin.todoapplication.fragments.SharedViewModel

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private val todoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        setHasOptionsMenu(true)

        title = view.findViewById(R.id.current_title_et)
        description = view.findViewById(R.id.current_description_et)
        spinner = view.findViewById(R.id.current_priorities_spinner)

        title.setText(args.currentItem.title)
        description.setText(args.currentItem.description)
        spinner.setSelection(sharedViewModel.parsePriorityToInt(args.currentItem.priority))

        spinner.onItemSelectedListener = sharedViewModel.listener

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                updateItem()
            }
            R.id.menu_delete -> {
                confirmItemRemoval()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = title.text.toString()
        val description = description.text.toString()
        val getPriority = spinner.selectedItem.toString()

        val validation = sharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val updateItem = ToDoData(
                args.currentItem.id,
                title,
                sharedViewModel.parsePriority(getPriority),
                description
            )
            todoViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun confirmItemRemoval() {
        val builder = AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                todoViewModel.deleteItem(args.currentItem)
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed: ${args.currentItem.title}",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No") { _, _ -> }
            .setTitle("Delete '${args.currentItem.title}'?")
            .setMessage("Are you sure you want to remove '${args.currentItem.title}?")
        builder.create().show()
    }
}