package com.khin.todoapplication.fragments.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.khin.todoapplication.R
import com.khin.todoapplication.data.models.ToDoData
import com.khin.todoapplication.data.viewmodel.ToDoViewModel
import com.khin.todoapplication.databinding.FragmentListBinding
import com.khin.todoapplication.fragments.SharedViewModel
import com.khin.todoapplication.fragments.list.adapter.ListAdapter
import com.khin.todoapplication.fragments.list.adapter.SwipeToDelete
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private val adapter: ListAdapter by lazy { ListAdapter() }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Data binding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = sharedViewModel

        // Set Up RecyclerView
        setUpRecyclerView()

        binding.listLayout.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_updateFragment)
        }
        // Observe LiveData
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }

        // Swipe To Delete
        swipeToDelete(binding.recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                // delete item
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed: ${deletedItem.title}",
                    Toast.LENGTH_SHORT
                ).show()
                // Restore Deleted Item
                restoreDeletedItem(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View, deletedItem: ToDoData, position: Int) {
        val snackBar = Snackbar.make(view, "Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all) {
            confirmRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteAll()
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed Everything!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No") { _, _ -> }
            .setTitle("Delete Everything?")
            .setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}