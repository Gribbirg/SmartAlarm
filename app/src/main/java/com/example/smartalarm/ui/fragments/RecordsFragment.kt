package com.example.smartalarm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.data.data.AccountData
import com.example.smartalarm.data.db.RecordsData
import com.example.smartalarm.databinding.FragmentRecordsBinding
import com.example.smartalarm.ui.adapters.AllRecordsAdapter
import com.example.smartalarm.ui.adapters.MyRecordsAdapter
import com.example.smartalarm.ui.viewmodels.RecordsFragmentViewModel


class RecordsFragment : Fragment(), MyRecordsAdapter.OnMyRecordClickListener,
    AllRecordsAdapter.OnWorldRecordClickListener {

    lateinit var binding: FragmentRecordsBinding
    lateinit var viewModel: RecordsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RecordsFragmentViewModel::class.java]

        binding.typeSelectionButtons.check(R.id.gamesButton)
        binding.fromSelectionButtons.check(R.id.myButton)

        binding.typeSelectionButtons.addOnButtonCheckedListener { _, _, _ ->
            viewModel.getRecordsFromDb(getNumOfButtonById())

        }

        binding.fromSelectionButtons.addOnButtonCheckedListener { _, _, _ ->
            viewModel.getRecordsFromDb(getNumOfButtonById())

        }

        viewModel.myRecordsData.observe(viewLifecycleOwner) {
            showErrorText(it.isEmpty())
            binding.recordsRecyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = MyRecordsAdapter(it, this@RecordsFragment)
            }
        }

        viewModel.allRecordsData.observe(viewLifecycleOwner) {
            showErrorText(it.isEmpty())
            binding.recordsRecyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = AllRecordsAdapter(it, this@RecordsFragment)
            }
        }

        viewModel.shareResult.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    requireContext(),
                    if (it)
                        "Успешно"
                    else
                        "Произошла ошибка. Попробуйте снова",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetShareResult()
            }
        }

        if (arguments?.getInt("saved state") != null)
            setState(requireArguments().getInt("saved state"))

        viewModel.getRecordsFromDb(getNumOfButtonById())
        return binding.root
    }

    private fun setState(state: Int) {
        if (state % 2 == 1)
            binding.typeSelectionButtons.check(R.id.lastsButton)
        if (state >= 2)
            binding.fromSelectionButtons.check(R.id.allButton)
    }

    private fun getNumOfButtonById(): Int {
        var res = 0
        if (binding.typeSelectionButtons.checkedButtonId == R.id.lastsButton)
            res++
        if (binding.fromSelectionButtons.checkedButtonId == R.id.allButton)
            res += 2
        return res
    }

    private fun showErrorText(on: Boolean) {
        if (on) {
            binding.recordsErrorTextView.visibility = View.VISIBLE
            binding.recordsErrorImageView.visibility = View.VISIBLE
        } else {
            binding.recordsErrorTextView.visibility = View.GONE
            binding.recordsErrorImageView.visibility = View.GONE
        }
    }


    override fun onShareClickListener(recordsData: RecordsData) {
        if (!viewModel.shareRecord(recordsData, getNumOfButtonById()))
            Toast.makeText(context, "Войдите в аккаунт!", Toast.LENGTH_LONG).show()
    }

    override fun onDeleteClickListener(accountData: AccountData) {}

    override fun isCurrentUser(accountData: AccountData): Boolean = false
    override fun onProfileClickListener(accountData: AccountData) {
        val bundle = Bundle()
        bundle.putString("user uid", accountData.uid)
        bundle.putInt("saved state", getNumOfButtonById())
        Navigation.findNavController(binding.root).navigate(
            R.id.action_recordsFragment_to_profileOtherFragment,
            bundle,
            NavOptions.Builder().setPopUpTo(R.id.alarmsFragment, true).build()
        )
    }
}